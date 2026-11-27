package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SampleSteps {
    private static final By PARTICIPANTS = By.cssSelector("#participantsList li");

    private Map<String, Integer> initialParticipantSteps = new HashMap<>();
    private String selectedParticipantName;
    private int selectedParticipantStepsBefore;
    private int selectedParticipantPositionBefore;
    private int stepsToAdd;
    private int mikeStepsBefore;

    private WebDriver driver() {
        return Hooks.driver;
    }

    private WebDriverWait browserWait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(10));
    }

    private List<WebElement> participants() {
        return driver().findElements(PARTICIPANTS);
    }

    private int stepCount(WebElement participant) {
        String steps = participant.findElement(By.cssSelector(".participant-steps"))
                .getText()
                .replace("steps", "")
                .replace(",", "")
                .trim();
        return Integer.parseInt(steps);
    }

    private Map<String, Integer> participantStepsByName() {
        Map<String, Integer> stepsByName = new HashMap<>();
        for (WebElement participant : participants()) {
            String name = participant.findElement(By.cssSelector(".participant-name"))
                    .getText().trim();
            stepsByName.put(name, stepCount(participant));
        }
        return stepsByName;
    }

    private int participantPosition(String name) {
        List<WebElement> participantList = participants();
        for (int i = 0; i < participantList.size(); i++) {
            String participantName = participantList.get(i)
                    .findElement(By.cssSelector(".participant-name")).getText().trim();
            if (name.equals(participantName)) {
                return i;
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    private void openAddStepsModal() {
        WebElement modal = driver().findElement(By.id("addStepsModal"));
        if (!modal.isDisplayed()) {
            driver().findElements(By.id("addStepsBtn")).get(0).click();
        }
        browserWait().until(ExpectedConditions.visibilityOf(modal));
    }

    private void assertModalElementsPresent() {
        WebElement modal = driver().findElement(By.id("addStepsModal"));
        assertTrue(modal.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(modal.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(modal.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(modal.findElement(By.cssSelector(".w3-closebtn")).isDisplayed());
    }

    private void selectParticipant(String name) {
        new Select(driver().findElement(By.id("participant_select")))
                .selectByVisibleText(name);
        selectedParticipantName = name;
        selectedParticipantStepsBefore = participantStepsByName().get(name);
        selectedParticipantPositionBefore = participantPosition(name);
    }

    private void assertParticipantsAreSortedBySteps() {
        List<WebElement> participantList = participants();
        for (int i = 1; i < participantList.size(); i++) {
            assertTrue("Participant list is not in descending step-count order",
                    stepCount(participantList.get(i - 1)) >= stepCount(participantList.get(i)));
        }
    }

    @Given("I am on the fitness challenge page {string}")
    public void iAmOnTheFitnessChallengePage(String url) {
        driver().get(url);
        ((JavascriptExecutor) driver()).executeScript("window.localStorage.clear();");
        driver().navigate().refresh();
        browserWait().until(ExpectedConditions.numberOfElementsToBe(PARTICIPANTS, 10));
        initialParticipantSteps = participantStepsByName();
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
        browserWait().until(ExpectedConditions.numberOfElementsToBe(PARTICIPANTS, 10));
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String title) {
        assertEquals(title, driver().findElement(By.cssSelector("h2")).getText().trim());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int count) {
        assertEquals(count, participants().size());
    }

    @Then("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(DataTable table) {
        Map<String, String> actualStepsByName = new HashMap<>();
        for (WebElement participant : participants()) {
            String name = participant.findElement(By.cssSelector(".participant-name"))
                    .getText().trim();
            String steps = participant.findElement(By.cssSelector(".participant-steps"))
                    .getText().replace("steps", "").trim();
            actualStepsByName.put(name, steps);
        }

        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            String name = row.get("Name").trim();
            String expectedSteps = row.get("Steps").trim();
            assertEquals("Unexpected step count for " + name,
                    expectedSteps, actualStepsByName.get(name));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String label) {
        String id;
        if ("Add Steps".equals(label)) {
            id = "addStepsBtn";
        } else if ("Reset List".equals(label)) {
            id = "resetBtn";
        } else {
            throw new IllegalArgumentException("Unsupported button label: " + label);
        }

        List<WebElement> buttons = driver().findElements(By.id(id));
        assertEquals("Expected a top and bottom " + label + " button", 2, buttons.size());
        assertTrue("Top " + label + " button is not visible", buttons.get(0).isDisplayed());
        assertTrue("Bottom " + label + " button is not visible", buttons.get(1).isDisplayed());
    }

    @When("I view the participant list")
    public void iViewTheParticipantList() {
        browserWait().until(ExpectedConditions.numberOfElementsToBe(PARTICIPANTS, 10));
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        assertParticipantsAreSortedBySteps();
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantsStepCountIsGreaterThanOrEqualToParticipantBelowThem() {
        List<WebElement> participantList = participants();
        for (int i = 0; i + 1 < participantList.size(); i++) {
            assertTrue("A participant has fewer steps than the participant below them",
                    stepCount(participantList.get(i)) >= stepCount(participantList.get(i + 1)));
        }
    }

    @Then("^the (\\d+)(st|nd|rd|th) place participant displays a (gold|silver|bronze) trophy icon$")
    public void placeParticipantDisplaysATrophyIcon(int place, String ordinal, String medal) {
        assertEquals(ordinalSuffix(place), ordinal);
        List<WebElement> participantList = participants();
        assertTrue("No participant exists at place " + place,
                place >= 1 && participantList.size() >= place);

        String expectedColor;
        switch (medal) {
            case "gold":
                expectedColor = "rgba(255, 215, 0, 1)";
                break;
            case "silver":
                expectedColor = "rgba(192, 192, 192, 1)";
                break;
            case "bronze":
                expectedColor = "rgba(205, 127, 50, 1)";
                break;
            default:
                throw new IllegalArgumentException("Unsupported trophy color: " + medal);
        }

        WebElement trophy = participantList.get(place - 1)
                .findElement(By.cssSelector(".fa-trophy"));
        assertEquals(expectedColor, trophy.getCssValue("color"));
    }

    private String ordinalSuffix(int place) {
        int lastTwoDigits = place % 100;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 13) {
            return "th";
        }
        switch (place % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsRankedFourthAndBelowHaveNoTrophyIcons() {
        List<WebElement> participantList = participants();
        for (int i = 3; i < participantList.size(); i++) {
            assertTrue("Unexpected trophy icon for participant ranked " + (i + 1),
                    participantList.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
    }

    @When("I click the {string} button at the top of the page")
    public void clickAddStepsButtonAtTop(String label) {
        if (!"Add Steps".equals(label)) {
            throw new IllegalArgumentException("Unsupported button label: " + label);
        }
        driver().findElements(By.id("addStepsBtn")).get(0).click();
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) driver())
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void clickAddStepsButtonAtBottom(String label) {
        if (!"Add Steps".equals(label)) {
            throw new IllegalArgumentException("Unsupported button label: " + label);
        }
        driver().findElements(By.id("addStepsBtn")).get(1).click();
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        WebElement modal = browserWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertEquals(title, modal.findElement(By.cssSelector("h3")).getText().trim());
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(driver().findElement(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInputField() {
        WebElement input = driver().findElement(By.id("steps_input"));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String label) {
        assertEquals(label, driver().findElement(By.id("modal_add_steps_button"))
                .getText().trim());
    }

    @Then("the modal has a close button \\(×\\) in the top-right corner")
    public void modalHasCloseButtonInTopRightCorner() {
        WebElement closeButton = driver().findElement(
                By.cssSelector("#addStepsModal .w3-closebtn"));
        assertTrue(closeButton.isDisplayed());
        assertEquals("×", closeButton.getText().trim());
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownIsPrepopulatedWithAllParticipants() {
        List<WebElement> options = new Select(driver().findElement(By.id("participant_select")))
                .getOptions();
        Set<String> dropdownNames = new HashSet<>();
        for (WebElement option : options) {
            String name = option.getText().trim();
            if (!"Choose participant".equals(name)) {
                dropdownNames.add(name);
            }
        }

        Set<String> participantNames = new HashSet<>(participantStepsByName().keySet());
        assertEquals(10, dropdownNames.size());
        assertEquals(participantNames, dropdownNames);
    }

    @Then("the default dropdown text shows {string}")
    public void defaultDropdownTextShows(String expectedText) {
        assertEquals(expectedText, new Select(driver().findElement(By.id("participant_select")))
                .getFirstSelectedOption().getText().trim());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        assertModalElementsPresent();
    }

    @Given("I have opened the {string} modal")
    @When("I open the {string} modal")
    public void openAddStepsModal(String label) {
        if (!"Add Steps".equals(label)) {
            throw new IllegalArgumentException("Unsupported modal: " + label);
        }
        openAddStepsModal();
    }

    @When("I click the × \\(close\\) button in the top-right corner")
    public void clickModalCloseButton() {
        driver().findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
    }

    @Then("the modal closes")
    @Then("the modal closes automatically")
    public void modalCloses() {
        browserWait().until(ExpectedConditions.invisibilityOfElementLocated(
                By.id("addStepsModal")));
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(driver().findElement(By.cssSelector("h2")).isDisplayed());
        assertEquals(10, participants().size());
    }

    @Then("no data is changed from initial state")
    public void noDataIsChangedFromInitialState() {
        assertEquals(initialParticipantSteps, participantStepsByName());
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String name) {
        mikeStepsBefore = participantStepsByName().get(name);
    }

    @When("I select {string} from the dropdown")
    public void selectParticipantFromDropdown(String name) {
        selectParticipant(name);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select dropdown = new Select(driver().findElement(By.id("participant_select")));
        String name = dropdown.getOptions().stream()
                .map(WebElement::getText)
                .map(String::trim)
                .filter(option -> !"Choose participant".equals(option))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No participants available in dropdown"));
        selectParticipant(name);
    }

    @When("I enter {string} in the steps input field")
    public void enterStepsInInputField(String value) {
        WebElement input = driver().findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(value);
        stepsToAdd = Integer.parseInt(value);
    }

    @When("I click {string} button")
    public void clickModalSubmitButton(String label) {
        if (!"Add Steps".equals(label)) {
            throw new IllegalArgumentException("Unsupported button label: " + label);
        }
        driver().findElement(By.id("modal_add_steps_button")).click();
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        browserWait().until(ExpectedConditions.numberOfElementsToBe(PARTICIPANTS, 10));
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeKidsStepCountIncreasesByOneThousand() {
        assertEquals(mikeStepsBefore + 1000,
                participantStepsByName().get("Mike Kid").intValue());
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listResortsWhenMikeKidsRankingChanges() {
        assertParticipantsAreSortedBySteps();
        int expectedPosition = 0;
        int mikeNewTotal = mikeStepsBefore + 1000;
        for (Map.Entry<String, Integer> participant : participantStepsByName().entrySet()) {
            if (!"Mike Kid".equals(participant.getKey())
                    && participant.getValue() > mikeNewTotal) {
                expectedPosition++;
            }
        }
        if (expectedPosition != selectedParticipantPositionBefore) {
            assertEquals(expectedPosition, participantPosition("Mike Kid"));
        }
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedText) {
        assertEquals(expectedText, browserWait().until(ExpectedConditions.alertIsPresent())
                .getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        browserWait().until(ExpectedConditions.alertIsPresent()).accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertTrue(browserWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.id("addStepsModal"))).isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void stepCountUpdatesWithLargeNumber() {
        assertEquals(selectedParticipantStepsBefore + stepsToAdd,
                participantStepsByName().get(selectedParticipantName).intValue());
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertEquals(selectedParticipantName, participants().get(0)
                .findElement(By.cssSelector(".participant-name")).getText().trim());
    }
}
