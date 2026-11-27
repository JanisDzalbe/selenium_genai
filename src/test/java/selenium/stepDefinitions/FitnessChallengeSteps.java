package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallengeSteps {
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final By PARTICIPANT_NAMES =
            By.cssSelector("#participantsList .participant-name");
    private static final By PARTICIPANT_STEPS =
            By.cssSelector("#participantsList .participant-steps");
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By MODAL_SUBMIT = By.id("modal_add_steps_button");
    private static final By MODAL_CLOSE = By.cssSelector(".w3-closebtn");
    private final Map<String, Integer> initialStepCounts = new LinkedHashMap<>();
    private Integer notedMikeStepCount;
    private String selectedParticipant;
    private int enteredStepCount;

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        driver().get(url);
        waitForParticipants();
        initialStepCounts.clear();
        List<WebElement> names = driver().findElements(PARTICIPANT_NAMES);
        for (WebElement name : names) {
            String participant = name.getText().trim();
            initialStepCounts.put(participant, stepCountFor(participant));
        }
        notedMikeStepCount = null;
        selectedParticipant = null;
        enteredStepCount = 0;
    }

    @When("the page loads for the first time")
    public void waitForInitialPageLoad() {
        waitForParticipants();
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        waitForParticipants();
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, driver().findElement(By.cssSelector("h2")).getText().trim());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayed(int expectedCount) {
        assertEquals(expectedCount, driver().findElements(PARTICIPANT_NAMES).size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable participants) {
        for (Map<String, String> participant : participants.asMaps(String.class, String.class)) {
            String name = participant.get("Name");
            int expectedSteps = Integer.parseInt(participant.get("Steps").replace(",", "").trim());
            assertEquals("Unexpected steps for " + name, expectedSteps, stepCountFor(name));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String label) {
        String buttonId;
        if ("Add Steps".equals(label)) {
            buttonId = "addStepsBtn";
        } else if ("Reset List".equals(label)) {
            buttonId = "resetBtn";
        } else {
            throw new AssertionError("Unexpected button label: " + label);
        }

        List<WebElement> buttons = driver().findElements(By.id(buttonId));
        assertEquals("Expected a top and bottom " + label + " button", 2, buttons.size());
        for (WebElement button : buttons) {
            assertTrue(label + " button should be visible", button.isDisplayed());
        }
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreInDescendingOrder() {
        assertDescendingStepCounts();
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachStepCountIsAtLeastTheNext() {
        assertDescendingStepCounts();
    }

    @Then("^the (\\d+)(?:st|nd|rd|th) place participant displays a (gold|silver|bronze) trophy icon$")
    public void rankedParticipantDisplaysTrophy(int place, String medal) {
        WebElement row = driver().findElement(By.id("participant_display" + (place - 1)));
        List<WebElement> trophies = row.findElements(By.cssSelector("i.fa-trophy"));
        assertEquals("Expected one trophy for " + place + " place", 1, trophies.size());
        assertEquals(medal.toLowerCase(), trophyColor(trophies.get(0)));
    }

    @Then("participants ranked {int}th and below have no trophy icons")
    public void lowerRankedParticipantsHaveNoTrophies(int firstRankWithoutTrophy) {
        List<WebElement> names = driver().findElements(PARTICIPANT_NAMES);
        for (int rank = firstRankWithoutTrophy; rank <= names.size(); rank++) {
            WebElement row = driver().findElement(By.id("participant_display" + (rank - 1)));
            assertTrue("Unexpected trophy at rank " + rank,
                    row.findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    @When("I click the {string} button at the top of the page")
    public void clickTopButton(String label) {
        assertEquals("Add Steps", label);
        clickAddStepsButton(0);
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottom() {
        ((JavascriptExecutor) driver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void clickBottomButton(String label) {
        assertEquals("Add Steps", label);
        clickAddStepsButton(1);
    }

    @Given("I have opened the {string} modal")
    @When("I open the {string} modal")
    public void openAddStepsModal(String label) {
        assertEquals("Add Steps", label);
        clickAddStepsButton(0);
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void clickModalCloseButton() {
        WebElement closeButton = driver().findElement(MODAL_CLOSE);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].click()", closeButton);
        waitForModalToClose();
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        waitForModalToOpen();
        assertEquals(title, driver().findElement(By.cssSelector("#addStepsModal h3")).getText().trim());
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(driver().findElement(PARTICIPANT_SELECT).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInput() {
        WebElement input = driver().findElement(STEPS_INPUT);
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String label) {
        WebElement button = driver().findElement(MODAL_SUBMIT);
        assertTrue(button.isDisplayed());
        assertEquals(label, button.getText().trim());
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void modalHasCloseButton() {
        assertTrue(driver().findElement(MODAL_CLOSE).isDisplayed());
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownContainsAllParticipants() {
        List<WebElement> options = new Select(driver().findElement(PARTICIPANT_SELECT)).getOptions();
        assertEquals(11, options.size());
        assertEquals("Choose participant", options.get(0).getText().trim());
        assertEquals(10, initialStepCounts.size());
        for (int index = 1; index < options.size(); index++) {
            assertTrue("Unexpected dropdown participant: " + options.get(index).getText(),
                    initialStepCounts.containsKey(options.get(index).getText().trim()));
        }
    }

    @Then("the default dropdown text shows {string}")
    public void dropdownHasDefaultText(String text) {
        assertEquals(text, new Select(driver().findElement(PARTICIPANT_SELECT))
                .getFirstSelectedOption().getText().trim());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        waitForModalToOpen();
        assertTrue(driver().findElement(PARTICIPANT_SELECT).isDisplayed());
        assertTrue(driver().findElement(STEPS_INPUT).isDisplayed());
        assertTrue(driver().findElement(MODAL_SUBMIT).isDisplayed());
        assertTrue(driver().findElement(MODAL_CLOSE).isDisplayed());
        assertEquals(11, new Select(driver().findElement(PARTICIPANT_SELECT)).getOptions().size());
    }

    @Then("the modal closes")
    public void modalCloses() {
        waitForModalToClose();
    }

    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        waitForModalToClose();
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(driver().findElement(By.cssSelector("h2")).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        assertEquals(initialStepCounts.size(), driver().findElements(PARTICIPANT_NAMES).size());
        for (Map.Entry<String, Integer> participant : initialStepCounts.entrySet()) {
            assertEquals("Unexpected steps for " + participant.getKey(),
                    participant.getValue().intValue(), stepCountFor(participant.getKey()));
        }
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String participant) {
        notedMikeStepCount = stepCountFor(participant);
        selectedParticipant = participant;
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String participant) {
        new Select(driver().findElement(PARTICIPANT_SELECT)).selectByVisibleText(participant);
        selectedParticipant = participant;
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select select = new Select(driver().findElement(PARTICIPANT_SELECT));
        assertTrue("No participants available in the dropdown", select.getOptions().size() > 1);
        selectedParticipant = select.getOptions().get(1).getText().trim();
        select.selectByIndex(1);
    }

    @When("I enter {string} in the steps input field")
    public void enterSteps(String steps) {
        WebElement input = driver().findElement(STEPS_INPUT);
        input.clear();
        input.sendKeys(steps);
        enteredStepCount = Integer.parseInt(steps);
    }

    @When("I click {string} button")
    public void clickModalSubmitButton(String label) {
        assertEquals("Add Steps", label);
        driver().findElement(MODAL_SUBMIT).click();
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        if (selectedParticipant != null && enteredStepCount > 0) {
            waitForStepCount(selectedParticipant,
                    initialStepCounts.get(selectedParticipant) + enteredStepCount);
        }
        assertEquals(10, driver().findElements(PARTICIPANT_NAMES).size());
        assertEquals(10, driver().findElements(PARTICIPANT_STEPS).size());
    }

    @Then("^Mike Kid's step count increases by ([\\d,]+)$")
    public void mikeStepCountIncreasesBy(String amount) {
        assertTrue("Mike Kid's original count was not recorded", notedMikeStepCount != null);
        int increase = Integer.parseInt(amount.replace(",", ""));
        int expected = notedMikeStepCount + increase;
        waitForStepCount("Mike Kid", expected);
        assertEquals(expected, stepCountFor("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listIsSortedAfterAddingMikeSteps() {
        assertDescendingStepCounts();
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedText) {
        Alert alert = new WebDriverWait(driver(), TIMEOUT).until(d -> d.switchTo().alert());
        assertEquals(expectedText, alert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        driver().switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertEquals("block", driver().findElement(MODAL).getCssValue("display"));
    }

    @Then("the step count updates correctly with the large number")
    public void stepCountUpdatesWithLargeNumber() {
        assertTrue("No participant was selected", selectedParticipant != null);
        int expected = initialStepCounts.get(selectedParticipant) + enteredStepCount;
        waitForStepCount(selectedParticipant, expected);
        assertEquals(expected, stepCountFor(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertTrue("No participant was selected", selectedParticipant != null);
        assertEquals(selectedParticipant, driver().findElements(PARTICIPANT_NAMES)
                .get(0).getText().trim());
    }

    private WebDriver driver() {
        if (Hooks.driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized");
        }
        return Hooks.driver;
    }

    private void waitForParticipants() {
        new WebDriverWait(driver(), TIMEOUT).until(
                d -> d.findElements(PARTICIPANT_NAMES).size() == 10);
    }

    private void clickAddStepsButton(int index) {
        List<WebElement> buttons = driver().findElements(By.id("addStepsBtn"));
        assertTrue("Expected Add Steps button at index " + index, buttons.size() > index);
        buttons.get(index).click();
        waitForModalToOpen();
    }

    private void waitForModalToOpen() {
        new WebDriverWait(driver(), TIMEOUT).until(
                d -> "block".equals(d.findElement(MODAL).getCssValue("display")));
    }

    private void waitForModalToClose() {
        new WebDriverWait(driver(), TIMEOUT).until(
                d -> !"block".equals(d.findElement(MODAL).getCssValue("display")));
    }

    private void waitForStepCount(String participant, int expected) {
        new WebDriverWait(driver(), TIMEOUT).until(
                d -> stepCountFor(participant) == expected);
    }

    private int stepCountFor(String participantName) {
        List<WebElement> names = driver().findElements(PARTICIPANT_NAMES);
        List<WebElement> steps = driver().findElements(PARTICIPANT_STEPS);
        assertEquals("Participant name and step lists should match", names.size(), steps.size());
        for (int index = 0; index < names.size(); index++) {
            if (participantName.equals(names.get(index).getText().trim())) {
                String count = steps.get(index).getText()
                        .replace(",", "")
                        .replace(" steps", "")
                        .trim();
                return Integer.parseInt(count);
            }
        }
        throw new AssertionError("Participant not found: " + participantName);
    }

    private void assertDescendingStepCounts() {
        List<WebElement> steps = driver().findElements(PARTICIPANT_STEPS);
        assertEquals(10, steps.size());
        for (int index = 0; index < steps.size() - 1; index++) {
            int current = parseStepCount(steps.get(index));
            int next = parseStepCount(steps.get(index + 1));
            assertTrue("Step counts are not descending at rank " + (index + 1),
                    current >= next);
        }
    }

    private int parseStepCount(WebElement element) {
        String count = element.getText().replace(",", "").replace(" steps", "").trim();
        return Integer.parseInt(count);
    }

    private String trophyColor(WebElement trophy) {
        String color = trophy.getCssValue("color").replace(" ", "");
        if (color.contains("255,215,0")) {
            return "gold";
        }
        if (color.contains("192,192,192")) {
            return "silver";
        }
        if (color.contains("205,127,50")) {
            return "bronze";
        }
        throw new AssertionError("Unexpected trophy color: " + color);
    }
}
