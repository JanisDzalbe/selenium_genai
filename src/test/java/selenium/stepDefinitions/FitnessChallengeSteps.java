package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallengeSteps {
    private static final By PARTICIPANTS = By.cssSelector("#participantsList li");
    private static final By PARTICIPANT_NAME = By.cssSelector(".participant-name");
    private static final By PARTICIPANT_STEPS = By.cssSelector(".participant-steps");
    private static final By TROPHY = By.cssSelector(".fa-trophy");
    private static final By ADD_STEPS_BUTTON = By.id("addStepsBtn");
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By MODAL_SUBMIT = By.id("modal_add_steps_button");
    private static final By MODAL_CLOSE = By.cssSelector("#addStepsModal .w3-closebtn");
    private static final Map<String, Integer> DEFAULT_PARTICIPANTS = defaultParticipants();

    private String selectedParticipant;
    private int stepsBeforeUpdate;
    private int stepsToAdd;

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        driver().get(url);
        waitForParticipants();
    }

    @When("the page loads for the first time")
    public void waitForInitialPageLoad() {
        waitForParticipants();
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, driver().findElement(By.cssSelector("h2")).getText().trim());
    }

    @Then("{int} participants are displayed in the list")
    public void participantCountIsDisplayed(int expectedCount) {
        assertEquals(expectedCount, waitForParticipants().size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable table) {
        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            String name = row.get("Name").trim();
            int expectedSteps = parseSteps(row.get("Steps"));
            assertEquals("Unexpected step count for " + name, expectedSteps, participantSteps(name));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        By locator;
        if ("Add Steps".equals(buttonText)) {
            locator = By.id("addStepsBtn");
        } else if ("Reset List".equals(buttonText)) {
            locator = By.id("resetBtn");
        } else {
            throw new AssertionError("Unsupported button label: " + buttonText);
        }

        List<WebElement> buttons = driver().findElements(locator);
        assertEquals("Expected buttons at the top and bottom of the page", 2, buttons.size());
        assertTrue(buttons.stream().allMatch(WebElement::isDisplayed));
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        waitForParticipants();
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreInDescendingOrder() {
        List<WebElement> participants = waitForParticipants();
        int previousSteps = Integer.MAX_VALUE;
        for (WebElement participant : participants) {
            int currentSteps = parseSteps(participant.findElement(PARTICIPANT_STEPS).getText());
            assertTrue("Participant list is not in descending step-count order",
                    currentSteps <= previousSteps);
            previousSteps = currentSteps;
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantHasAtLeastAsManyStepsAsBelow() {
        List<WebElement> participants = waitForParticipants();
        for (int index = 0; index < participants.size() - 1; index++) {
            int currentSteps = parseSteps(participants.get(index).findElement(PARTICIPANT_STEPS).getText());
            int nextSteps = parseSteps(participants.get(index + 1).findElement(PARTICIPANT_STEPS).getText());
            assertTrue("Participant at rank " + (index + 1) + " has fewer steps than the next participant",
                    currentSteps >= nextSteps);
        }
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceDisplaysGoldTrophy() {
        assertTrophyColor(0, ".*255, 215, 0.*|gold");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceDisplaysSilverTrophy() {
        assertTrophyColor(1, ".*192, 192, 192.*|silver");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceDisplaysBronzeTrophy() {
        assertTrophyColor(2, ".*205, 127, 50.*|#cd7f32");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void lowerRankedParticipantsHaveNoTrophies() {
        List<WebElement> participants = waitForParticipants();
        for (int index = 3; index < participants.size(); index++) {
            assertTrue("Unexpected trophy icon at rank " + (index + 1),
                    participants.get(index).findElements(TROPHY).isEmpty());
        }
    }

    @When("I click the {string} button at the top of the page")
    public void clickAddStepsButtonAtTop(String buttonLabel) {
        assertEquals("Add Steps", buttonLabel);
        browserWait().until(ExpectedConditions.elementToBeClickable(driver().findElements(ADD_STEPS_BUTTON).get(0))).click();
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) driver()).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @When("I click the {string} button at the bottom")
    public void clickAddStepsButtonAtBottom(String buttonLabel) {
        assertEquals("Add Steps", buttonLabel);
        List<WebElement> buttons = driver().findElements(ADD_STEPS_BUTTON);
        assertEquals(2, buttons.size());
        WebElement bottomButton = buttons.get(1);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView(true);", bottomButton);
        browserWait().until(ExpectedConditions.elementToBeClickable(bottomButton)).click();
    }

    @Given("I have opened the {string} modal")
    public void givenAddStepsModalIsOpened(String modalName) {
        openAddStepsModal(modalName);
    }

    @When("I open the {string} modal")
    public void whenOpenAddStepsModal(String modalName) {
        openAddStepsModal(modalName);
    }

    private void openAddStepsModal(String modalName) {
        assertEquals("Add Steps", modalName);
        browserWait().until(ExpectedConditions.elementToBeClickable(driver().findElements(ADD_STEPS_BUTTON).get(0))).click();
        browserWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        browserWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL));
        assertEquals(title, driver().findElement(By.cssSelector("#addStepsModal h3")).getText().trim());
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(browserWait().until(ExpectedConditions.visibilityOfElementLocated(PARTICIPANT_SELECT)).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInput() {
        WebElement input = browserWait().until(ExpectedConditions.visibilityOfElementLocated(STEPS_INPUT));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String label) {
        WebElement button = browserWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL_SUBMIT));
        assertEquals(label, button.getText().trim());
    }

    @Then("the modal has a close button \\(×\\) in the top-right corner")
    public void modalHasTopRightCloseButton() {
        WebElement closeButton = browserWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL_CLOSE));
        assertTrue(closeButton.isDisplayed());
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownContainsAllParticipants() {
        Select dropdown = new Select(browserWait().until(ExpectedConditions.visibilityOfElementLocated(PARTICIPANT_SELECT)));
        assertEquals(11, dropdown.getOptions().size());
        for (String name : DEFAULT_PARTICIPANTS.keySet()) {
            assertTrue("Participant missing from dropdown: " + name,
                    dropdown.getOptions().stream().anyMatch(option -> option.getText().trim().equals(name)));
        }
    }

    @Then("the default dropdown text shows {string}")
    public void defaultDropdownTextShows(String expectedText) {
        Select dropdown = new Select(browserWait().until(ExpectedConditions.visibilityOfElementLocated(PARTICIPANT_SELECT)));
        assertEquals(expectedText, dropdown.getFirstSelectedOption().getText().trim());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        assertTrue(browserWait().until(ExpectedConditions.visibilityOfElementLocated(PARTICIPANT_SELECT)).isDisplayed());
        assertTrue(browserWait().until(ExpectedConditions.visibilityOfElementLocated(STEPS_INPUT)).isDisplayed());
        assertTrue(browserWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL_SUBMIT)).isDisplayed());
        assertTrue(browserWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL_CLOSE)).isDisplayed());
    }

    @When("I click the × \\(close\\) button in the top-right corner")
    public void clickModalCloseButton() {
        browserWait().until(ExpectedConditions.elementToBeClickable(MODAL_CLOSE)).click();
    }

    @Then("the modal closes")
    public void modalCloses() {
        browserWait().until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        browserWait().until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        browserWait().until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
        assertTrue(waitForParticipants().stream().allMatch(WebElement::isDisplayed));
    }

    @Then("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        for (Map.Entry<String, Integer> participant : DEFAULT_PARTICIPANTS.entrySet()) {
            assertEquals("Unexpected step count for " + participant.getKey(),
                    (int) participant.getValue(), participantSteps(participant.getKey()));
        }
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String name) {
        selectedParticipant = name;
        stepsBeforeUpdate = participantSteps(name);
    }

    @When("I select {string} from the dropdown")
    public void selectParticipantFromDropdown(String name) {
        Select dropdown = new Select(browserWait().until(ExpectedConditions.visibilityOfElementLocated(PARTICIPANT_SELECT)));
        dropdown.selectByVisibleText(name);
        selectedParticipant = name;
        stepsBeforeUpdate = participantSteps(name);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        selectParticipantFromDropdown("Mike Kid");
    }

    @When("I enter {string} in the steps input field")
    public void enterStepsInInput(String steps) {
        stepsToAdd = Integer.parseInt(steps);
        WebElement input = browserWait().until(ExpectedConditions.visibilityOfElementLocated(STEPS_INPUT));
        input.clear();
        input.sendKeys(steps);
    }

    @When("I click {string} button")
    public void clickModalSubmitButton(String label) {
        assertEquals("Add Steps", label);
        browserWait().until(ExpectedConditions.elementToBeClickable(MODAL_SUBMIT)).click();
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        int expectedSteps = stepsBeforeUpdate + stepsToAdd;
        browserWait().until(webDriver -> participantSteps(selectedParticipant) == expectedSteps);
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeKidStepCountIncreasesByOneThousand() {
        assertEquals(stepsBeforeUpdate + 1000, participantSteps("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listIsSortedAfterMikeKidUpdate() {
        assertParticipantsSorted();
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedText) {
        Alert alert = browserWait().until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        browserWait().until(ExpectedConditions.alertIsPresent()).accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertTrue(browserWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL)).isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void stepCountUpdatesWithLargeNumber() {
        int expectedSteps = stepsBeforeUpdate + stepsToAdd;
        browserWait().until(webDriver -> participantSteps(selectedParticipant) == expectedSteps);
        assertEquals(expectedSteps, participantSteps(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        List<WebElement> participants = waitForParticipants();
        assertEquals(selectedParticipant, participants.get(0).findElement(PARTICIPANT_NAME).getText().trim());
    }

    private void assertTrophyColor(int participantIndex, String expectedColorPattern) {
        List<WebElement> participants = waitForParticipants();
        assertTrue("Not enough participants to check trophy placement", participants.size() > participantIndex);
        WebElement trophy = participants.get(participantIndex).findElement(TROPHY);
        assertTrue("Unexpected trophy color: " + trophy.getCssValue("color"),
                trophy.getCssValue("color").matches(expectedColorPattern));
    }

    private int participantSteps(String name) {
        return waitForParticipants().stream()
                .filter(row -> row.findElement(PARTICIPANT_NAME).getText().trim().equals(name))
                .findFirst()
                .map(row -> parseSteps(row.findElement(PARTICIPANT_STEPS).getText()))
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private int parseSteps(String text) {
        return Integer.parseInt(text.replace(" steps", "").replace(",", "").trim());
    }

    private void assertParticipantsSorted() {
        List<WebElement> participants = waitForParticipants();
        for (int index = 0; index < participants.size() - 1; index++) {
            int currentSteps = parseSteps(participants.get(index).findElement(PARTICIPANT_STEPS).getText());
            int nextSteps = parseSteps(participants.get(index + 1).findElement(PARTICIPANT_STEPS).getText());
            assertTrue("Participant list is not sorted by step count", currentSteps >= nextSteps);
        }
    }

    private List<WebElement> waitForParticipants() {
        return new WebDriverWait(driver(), Duration.ofSeconds(10))
                .until(webDriver -> {
                    List<WebElement> participants = webDriver.findElements(PARTICIPANTS);
                    return participants.size() == 10 ? participants : null;
                });
    }

    private WebDriverWait browserWait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(10));
    }

    private WebDriver driver() {
        if (Hooks.driver == null) {
            throw new AssertionError("WebDriver has not been initialized by the Cucumber hooks");
        }
        return Hooks.driver;
    }

    private static Map<String, Integer> defaultParticipants() {
        Map<String, Integer> participants = new LinkedHashMap<>();
        participants.put("Mike Kid", 8500);
        participants.put("Jill Watson", 12000);
        participants.put("Jane Doe", 6500);
        participants.put("John Smith", 15000);
        participants.put("Sarah Johnson", 9800);
        participants.put("Carlos Garcia", 11200);
        participants.put("Emily Chen", 7300);
        participants.put("David Brown", 13500);
        participants.put("Maria Rodriguez", 10500);
        participants.put("Alex Taylor", 8900);
        return participants;
    }

}
