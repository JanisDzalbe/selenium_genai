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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FitnessChallengeSteps {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private Map<String, String> originalParticipantData;
    private int mikeKidStepCountBefore;
    private String largeStepParticipantName;
    private int largeStepParticipantCountBefore;

    // ==================================================
    // SHARED STEP DEFINITIONS (FEATURES 1-4)
    // ==================================================

    // Implements: Given I am on the fitness challenge page "<url>"
    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        Hooks.driver.get(url);
        waitForParticipantCount(10);
        ((JavascriptExecutor) Hooks.driver).executeScript("resetFitnessChallenge();");
        waitForParticipantCount(10);
    }

    // ==================================================
    // FITNESS CHALLENGE 1 - INITIAL PAGE LOAD
    // ==================================================

    // Implements: When the page loads for the first time
    @When("the page loads for the first time")
    public void pageLoadsForTheFirstTime() {
        waitForParticipantCount(10);
    }

    // Implements: Then the page displays title "<title>"
    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, Hooks.driver.getTitle());
    }

    // Implements: Then <count> participants are displayed in the list
    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        assertEquals(expectedCount, waitForParticipantCount(expectedCount).size());
    }

    // Implements: And the default participants are displayed:
    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable dataTable) {
        Map<String, String> expected = new LinkedHashMap<>();
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            expected.put(row.get("Name"), normalizeSteps(row.get("Steps")));
        }

        List<WebElement> actualParticipants = waitForParticipantCount(expected.size());
        Map<String, String> actual = new LinkedHashMap<>();

        for (WebElement participant : actualParticipants) {
            String name = participant.findElement(By.cssSelector(".participant-name")).getText();
            String steps = normalizeSteps(participant
                    .findElement(By.cssSelector(".participant-steps")).getText());
            actual.put(name, steps);
        }

        assertEquals(expected, actual);
    }

    // Implements: And "<button text>" buttons are visible at top and bottom
    @Then("{string} buttons are visible at top and bottom")
    public void actionButtonsAreVisibleAtTopAndBottom(String buttonText) {
        String buttonId = "Add Steps".equals(buttonText) ? "addStepsBtn" : "resetBtn";
        List<WebElement> buttons = Hooks.driver.findElements(By.id(buttonId));

        assertEquals(2, buttons.size());
        assertTrue(buttons.get(0).isDisplayed());
        assertTrue(buttons.get(1).isDisplayed());
        assertEquals(buttonText, buttons.get(0).getText());
        assertEquals(buttonText, buttons.get(1).getText());
    }

    // ==================================================
    // FITNESS CHALLENGE 2 - PARTICIPANT DISPLAY AND RANKING
    // ==================================================

    // Implements: When I view the participant list
    @When("I view the participant list")
    public void viewParticipantList() {
        waitForParticipantCount(10);
    }

    // Implements: Then participants are displayed in descending order by step count
    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrder() {
        List<WebElement> participants = getParticipants();
        int previous = Integer.MAX_VALUE;

        for (WebElement participant : participants) {
            int current = parseSteps(participant
                    .findElement(By.cssSelector(".participant-steps")).getText());
            assertTrue(current <= previous,
                    "Participant steps must be in descending order");
            previous = current;
        }
    }

    // Implements: And each participant's step count is greater than or equal to the participant below them
    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantStepCountIsGreaterOrEqualToBelow() {
        participantsAreDisplayedInDescendingOrder();
    }

    // Implements: Then the 1st place participant displays a gold trophy icon
    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceDisplaysGoldTrophy() {
        assertTrophyStyle(getParticipants().get(0),
                "rgba(255, 215, 0, 1)");
    }

    // Implements: And the 2nd place participant displays a silver trophy icon
    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceDisplaysSilverTrophy() {
        assertTrophyStyle(getParticipants().get(1),
                "rgba(192, 192, 192, 1)");
    }

    // Implements: And the 3rd place participant displays a bronze trophy icon
    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceDisplaysBronzeTrophy() {
        assertTrophyStyle(getParticipants().get(2),
                "rgba(205, 127, 50, 1)");
    }

    // Implements: And participants ranked 4th and below have no trophy icons
    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsBelowThirdHaveNoTrophies() {
        List<WebElement> participants = getParticipants();
        for (int i = 3; i < participants.size(); i++) {
            assertEquals(0, participants.get(i)
                    .findElements(By.cssSelector(".fa-trophy")).size());
        }
    }

    // ==================================================
    // FITNESS CHALLENGE 3 - ADD STEPS MODAL
    // ==================================================

    // Implements: When I click the "Add Steps" button at the top of the page
    @When("I click the {string} button at the top of the page")
    public void clickAddStepsAtTop(String buttonText) {
        assertEquals("Add Steps", buttonText);
        openModal(false);
    }

    // Implements: Then a modal window appears with title "<modal title>"
    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        assertModalOpen();
        assertEquals(title, Hooks.driver.findElement(
                By.cssSelector("#addStepsModal h3")).getText());
    }

    // Implements: And the modal contains a participant dropdown
    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(Hooks.driver.findElement(By.id("participant_select")).isDisplayed());
    }

    // Implements: And the modal contains a number input field
    @Then("the modal contains a number input field")
    public void modalContainsNumberInput() {
        assertTrue(Hooks.driver.findElement(By.id("steps_input")).isDisplayed());
    }

    // Implements: And the modal contains "Add Steps" submit button
    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String buttonText) {
        WebElement button = Hooks.driver.findElement(By.id("modal_add_steps_button"));
        assertTrue(button.isDisplayed());
        assertEquals(buttonText, button.getText());
    }

    // Implements: And the modal has a close button (×) in the top-right corner
    @Then("^the modal has .*close button.*top-right corner$")
    public void modalHasCloseButton() {
        assertEquals(1, Hooks.driver
                .findElements(By.cssSelector("#addStepsModal .w3-closebtn")).size());
        assertEquals("×", Hooks.driver.findElement(
                By.cssSelector("#addStepsModal .w3-closebtn")).getText());
    }

    // Implements: And the dropdown is prepopulated with all 10 participants
    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownContainsTenParticipants() {
        assertEquals(11,
                new Select(Hooks.driver.findElement(By.id("participant_select")))
                        .getOptions().size());
    }

    // Implements: And the default dropdown text shows "Choose participant"
    @Then("the default dropdown text shows {string}")
    public void defaultDropdownTextShows(String text) {
        assertEquals(text,
                new Select(Hooks.driver.findElement(By.id("participant_select")))
                        .getFirstSelectedOption().getText());
    }

    // Implements: When I scroll to the bottom of the page
    @When("I scroll to the bottom of the page")
    public void scrollToBottom() {
        List<WebElement> buttons = Hooks.driver.findElements(By.id("addStepsBtn"));
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "arguments[0].scrollIntoView(true);", buttons.get(1));
    }

    // Implements: And I click the "Add Steps" button at the bottom
    @When("I click the {string} button at the bottom")
    public void clickAddStepsAtBottom(String buttonText) {
        assertEquals("Add Steps", buttonText);
        openModal(true);
    }

    // Implements: And all modal elements are present
    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        assertModalElements();
    }

    // Implements: Given I have opened the "Add Steps" modal
    @Given("I have opened the {string} modal")
    public void haveOpenedAddStepsModal(String modalButtonText) {
        assertEquals("Add Steps", modalButtonText);
        openModal(false);
    }

    // Implements: When I click the × (close) button in the top-right corner
    @When("^I click the .*\\(close\\) button in the top-right corner$")
    public void clickModalCloseButton() {
        originalParticipantData = captureParticipantData();
        WebElement closeButton =
                Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "arguments[0].click();", closeButton);
    }

    // Implements: Then the modal closes
    @Then("the modal closes")
    public void modalCloses() {
        waitForModalClosed();
    }

    // Implements: And I return to the main page view
    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(Hooks.driver.findElement(By.cssSelector("#participantsList")).isDisplayed());
    }

    // Implements: And no data is changed from initial state
    @Then("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        assertEquals(originalParticipantData, captureParticipantData());
    }

    // ==================================================
    // FITNESS CHALLENGE 4 - ADDING STEPS TO PARTICIPANTS
    // ==================================================

    // Implements: Given I note the current step count for "<name>"
    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String participantName) {
        assertEquals("Mike Kid", participantName);
        mikeKidStepCountBefore = parseSteps(getParticipantSteps(participantName));
    }

    // Implements: When I open the "Add Steps" modal
    @When("I open the {string} modal")
    public void openAddStepsModal(String modalButtonText) {
        assertEquals("Add Steps", modalButtonText);
        openModal(false);
    }

    // Implements: And I select "<name>" from the dropdown
    @When("I select {string} from the dropdown")
    public void selectNamedParticipant(String participantName) {
        selectParticipant(participantName);
    }

    // Implements: And I enter "<steps>" in the steps input field
    @When("I enter {string} in the steps input field")
    public void enterSteps(String steps) {
        Hooks.driver.findElement(By.id("steps_input")).clear();
        Hooks.driver.findElement(By.id("steps_input")).sendKeys(steps);
    }

    // Implements: And I click "Add Steps" button
    @When("I click {string} button")
    public void clickSubmitButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        clickModalSubmit();
    }

    // Implements: Then the modal closes automatically
    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        waitForModalClosed();
    }

    // Implements: And the participant list refreshes
    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        waitForParticipantCount(10);
    }

    // Implements: And Mike Kid's step count increases by 1,000
    @Then("Mike Kid's step count increases by 1,000")
    public void mikeKidStepCountIncreasesByOneThousand() {
        assertEquals(mikeKidStepCountBefore + 1000,
                parseSteps(getParticipantSteps("Mike Kid")));
    }

    // Implements: And the list re-sorts if Mike Kid's new total changes his ranking
    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void verifyRankingAfterMikeKidUpdate() {
        assertDescendingRanking();
    }

    // Implements: And I select any participant
    @When("I select any participant")
    public void selectAnyParticipant() {
        largeStepParticipantName = "Mike Kid";
        largeStepParticipantCountBefore =
                parseSteps(getParticipantSteps(largeStepParticipantName));
        selectParticipant(largeStepParticipantName);
    }

    // Implements: Then an alert appears: "<alert text>"
    @Then("an alert appears: {string}")
    public void alertAppears(String alertText) {
        Alert alert = new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(d -> d.switchTo().alert());
        assertEquals(alertText, alert.getText());
    }

    // Implements: When I accept the alert
    @When("I accept the alert")
    public void acceptAlert() {
        Hooks.driver.switchTo().alert().accept();
    }

    // Implements: Then the modal remains open
    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertModalOpen();
    }

    // Implements: Then the step count updates correctly with the large number
    @Then("the step count updates correctly with the large number")
    public void largeStepCountUpdatesCorrectly() {
        assertEquals(largeStepParticipantCountBefore + 999999,
                parseSteps(getParticipantSteps(largeStepParticipantName)));
    }

    // Implements: And the participant moves to first place
    @Then("the participant moves to first place")
    public void selectedParticipantMovesToFirstPlace() {
        assertEquals(largeStepParticipantName,
                getParticipants().get(0)
                        .findElement(By.cssSelector(".participant-name")).getText());
    }

    // ==================================================
    // PRIVATE HELPER METHODS
    // ==================================================

    private void openModal(boolean bottom) {
        List<WebElement> buttons = Hooks.driver.findElements(By.id("addStepsBtn"));
        WebElement button = bottom ? buttons.get(1) : buttons.get(0);
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "arguments[0].scrollIntoView(true);", button);
        button.click();
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(d -> "block".equals(d.findElement(By.id("addStepsModal"))
                        .getCssValue("display")));
    }

    private void assertModalElements() {
        assertModalOpen();
        assertEquals("Add Steps to Participant",
                Hooks.driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertTrue(Hooks.driver.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(Hooks.driver.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(Hooks.driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertEquals(1, Hooks.driver
                .findElements(By.cssSelector("#addStepsModal .w3-closebtn")).size());
        assertEquals(11,
                new Select(Hooks.driver.findElement(By.id("participant_select")))
                        .getOptions().size());
        assertEquals("Choose participant",
                new Select(Hooks.driver.findElement(By.id("participant_select")))
                        .getFirstSelectedOption().getText());
    }

    private void assertModalOpen() {
        assertEquals("block",
                Hooks.driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    private void waitForModalClosed() {
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(d -> "none".equals(d.findElement(By.id("addStepsModal"))
                        .getCssValue("display")));
    }

    private void clickModalSubmit() {
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "document.getElementById('modal_add_steps_button').click()");
    }

    private void selectParticipant(String participantName) {
        new Select(Hooks.driver.findElement(By.id("participant_select")))
                .selectByVisibleText(participantName);
    }

    private String getParticipantSteps(String participantName) {
        for (WebElement participant : getParticipants()) {
            if (participantName.equals(participant
                    .findElement(By.cssSelector(".participant-name")).getText())) {
                return participant.findElement(By.cssSelector(".participant-steps"))
                        .getText();
            }
        }
        throw new AssertionError("Participant not found: " + participantName);
    }

    private Map<String, String> captureParticipantData() {
        Map<String, String> participantData = new LinkedHashMap<>();
        for (WebElement participant : getParticipants()) {
            String name = participant.findElement(By.cssSelector(".participant-name")).getText();
            String steps = normalizeSteps(participant
                    .findElement(By.cssSelector(".participant-steps")).getText());
            participantData.put(name, steps);
        }
        return participantData;
    }

    private void assertDescendingRanking() {
        int previous = Integer.MAX_VALUE;
        for (WebElement participant : getParticipants()) {
            int steps = parseSteps(participant
                    .findElement(By.cssSelector(".participant-steps")).getText());
            assertTrue(steps <= previous,
                    "Participant steps must be in descending order");
            previous = steps;
        }
    }

    private List<WebElement> getParticipants() {
        return waitForParticipantCount(10);
    }

    private int parseSteps(String text) {
        return Integer.parseInt(text
                .replace("steps", "")
                .replace(",", "")
                .trim());
    }

    private List<WebElement> waitForParticipantCount(int expectedCount) {
        WebDriver driver = Hooks.driver;
        return new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(d -> {
                    List<WebElement> participants =
                            d.findElements(By.cssSelector("#participantsList li"));
                    return participants.size() == expectedCount ? participants : null;
                });
    }

    private String normalizeSteps(String text) {
        return text.replace(" steps", "")
                .replace(",", "")
                .replaceAll("\\s+", "")
                .trim();
    }

    private void assertTrophyStyle(WebElement participant, String expectedColor) {
        List<WebElement> trophies = participant.findElements(By.cssSelector(".fa-trophy"));
        assertEquals(1, trophies.size());
        assertEquals(expectedColor, trophies.get(0).getCssValue("color"));
    }
}
