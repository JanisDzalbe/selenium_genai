package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.JavascriptExecutor;
import selenium.pages.FitnessChallengePage;

import static org.junit.jupiter.api.Assertions.*;

public class FitnessSteps {
    private final FitnessChallengePage page;
    private long trackedInitialSteps;
    private String trackedParticipantName;

    public FitnessSteps() {
        page = new FitnessChallengePage(Hooks.driver);
    }

    // ── Feature 1: Initial page load ──────────────────────────────────────────

    @Given("^I am on the fitness challenge page \"([^\"]*)\"$")
    public void iAmOnTheFitnessChallengePage(String url) {
        page.openPage();
        String actual = page.getCurrentUrl().replaceAll("/$", "");
        String expected = url.replaceAll("/$", "");
        assertEquals(expected, actual, "Unexpected URL after navigation");
    }

    @When("^the page loads for the first time$")
    public void thePageLoadsForTheFirstTime() {
        String flag = (String) ((JavascriptExecutor) page.getDriver())
                .executeScript("return localStorage.getItem('fitnessReload')");
        assertNotNull(flag, "localStorage 'fitnessReload' should be set after first-load initialization");
    }

    @Then("^the page displays title \"([^\"]*)\"$")
    public void thePageDisplaysTitle(String title) {
        assertEquals(title, page.getPageTitle(), "Page title mismatch");
    }

    @Then("^(\\d+) participants are displayed in the list$")
    public void participantsAreDisplayedInTheList(int count) {
        assertEquals(count, page.getParticipantCount(),
                "Expected " + count + " participants in the list");
    }

    @Then("^the default participants are displayed:$")
    public void theDefaultParticipantsAreDisplayed(io.cucumber.datatable.DataTable dataTable) {
        var participants = page.getParticipants();
        var nameToParticipant = new java.util.HashMap<String, org.openqa.selenium.WebElement>();
        for (var p : participants) {
            nameToParticipant.put(page.getParticipantName(p), p);
        }

        for (var row : dataTable.asMaps(String.class, String.class)) {
            String name = row.get("Name");
            assertTrue(nameToParticipant.containsKey(name), "Participant not found in list: " + name);

            String stepsCell = row.get("Steps");
            if (stepsCell != null && !stepsCell.isBlank()) {
                long expectedSteps = Long.parseLong(stepsCell.replace(",", "").trim());
                long actualSteps = page.getParticipantSteps(nameToParticipant.get(name));
                assertEquals(expectedSteps, actualSteps,
                        name + ": expected " + expectedSteps + " steps but got " + actualSteps);
            }
        }
    }

    @Then("^\"([^\"]*)\" buttons are visible at top and bottom$")
    public void buttonsAreVisibleAtTopAndBottom(String label) {
        assertEquals(2, page.getVisibleButtonCount(label),
                "Expected 2 visible \"" + label + "\" buttons (top and bottom)");
    }

    // ── Feature 2: Participant display and ranking ─────────────────────────────

    @When("^I view the participant list$")
    public void iViewTheParticipantList() {
        assertFalse(page.getParticipants().isEmpty(), "Participant list should be visible and non-empty");
    }

    @Then("^participants are displayed in descending order by step count$")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        assertDescendingStepOrder();
    }

    @Then("^each participant's step count is greater than or equal to the participant below them$")
    public void eachParticipantStepCountIsGreaterThanOrEqualToTheParticipantBelowThem() {
        assertDescendingStepOrder();
    }

    @Then("^the (\\d+)(?:st|nd|rd|th) place participant displays a (gold|silver|bronze) trophy icon$")
    public void theNthPlaceParticipantDisplaysTrophyIcon(int place, String trophyType) {
        assertTrue(page.hasTrophy(page.getParticipants().get(place - 1), trophyType),
                "Expected " + trophyType + " trophy at rank " + place);
    }

    @Then("^participants ranked (\\d+)(?:st|nd|rd|th) and below have no trophy icons$")
    public void participantsFromRankHaveNoTrophyIcons(int rank) {
        var participants = page.getParticipants();
        for (int i = rank - 1; i < participants.size(); i++) {
            assertFalse(page.hasTrophy(participants.get(i), "gold"),
                    "Rank " + (i + 1) + " should have no gold trophy");
            assertFalse(page.hasTrophy(participants.get(i), "silver"),
                    "Rank " + (i + 1) + " should have no silver trophy");
            assertFalse(page.hasTrophy(participants.get(i), "bronze"),
                    "Rank " + (i + 1) + " should have no bronze trophy");
        }
    }

    // ── Feature 3: Add Steps modal ────────────────────────────────────────────

    @When("^I click the \"([^\"]*)\" button at the top of the page$")
    public void iClickButtonAtTop(String button) {
        if ("Add Steps".equals(button)) page.clickAddStepsTop();
        else if ("Reset List".equals(button)) page.clickResetTop();
    }

    @When("^I click the \"([^\"]*)\" button at the bottom$")
    public void iClickButtonAtBottom(String button) {
        if ("Add Steps".equals(button)) page.clickAddStepsBottom();
        else if ("Reset List".equals(button)) page.clickResetBottom();
    }

    @Then("^a modal window appears with title \"([^\"]*)\"$")
    public void aModalWindowAppearsWithTitle(String title) {
        assertTrue(page.isModalVisible(), "Modal should be visible");
        assertEquals(title, page.getModalTitle(), "Modal title mismatch");
    }

    @Then("^the modal contains a participant dropdown$")
    public void theModalContainsAParticipantDropdown() {
        assertTrue(page.isParticipantDropdownPresent(), "Participant dropdown not found in modal");
    }

    @Then("^the modal contains a number input field$")
    public void theModalContainsANumberInputField() {
        assertTrue(page.isStepsInputPresent(), "Steps input field not found in modal");
    }

    @Then("^the modal contains \"([^\"]*)\" submit button$")
    public void theModalContainsSubmitButton(String button) {
        assertTrue(page.isSubmitButtonPresent(), "Submit button not found in modal");
        assertEquals(button, page.getSubmitButtonText(), "Submit button text mismatch");
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void theModalHasACloseButton() {
        assertTrue(page.isCloseButtonPresent(), "Close button not found in modal");
    }

    @Then("^the dropdown is prepopulated with all (\\d+) participants$")
    public void theDropdownIsPrepopulatedWithAllParticipants(int count) {
        assertEquals(count, page.getParticipantDropdownOptionCount(),
                "Expected " + count + " participant options (excluding the default placeholder)");
    }

    @Then("^the default dropdown text shows \"([^\"]*)\"$")
    public void theDefaultDropdownTextShows(String text) {
        assertEquals(text, page.getFirstSelectedDropdownOption(),
                "Default dropdown selection mismatch");
    }

    @When("^I scroll to the bottom of the page$")
    public void iScrollToTheBottomOfThePage() {
        ((JavascriptExecutor) page.getDriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @Then("^all modal elements are present$")
    public void allModalElementsArePresent() {
        assertTrue(page.isModalVisible(), "Modal should be visible");
        assertTrue(page.isParticipantDropdownPresent(), "Participant dropdown should be present");
        assertTrue(page.isStepsInputPresent(), "Steps input should be present");
        assertTrue(page.isSubmitButtonPresent(), "Submit button should be present");
        assertTrue(page.isCloseButtonPresent(), "Close button should be present");
    }

    @Given("^I have opened the \"([^\"]*)\" modal$")
    public void iHaveOpenedTheModal(String modal) {
        page.clickAddStepsTop();
        assertTrue(page.isModalVisible(), "Modal should be visible after clicking Add Steps");
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void iClickTheCloseButton() {
        page.closeModal();
    }

    @Then("^the modal closes$")
    public void theModalCloses() {
        assertFalse(page.isModalVisible(), "Modal should be closed");
    }

    @Then("^the modal closes automatically$")
    public void theModalClosesAutomatically() {
        assertFalse(page.isModalVisible(), "Modal should have closed automatically after submission");
    }

    @Then("^I return to the main page view$")
    public void iReturnToTheMainPageView() {
        assertEquals("Fitness Challenge", page.getPageTitle(), "Should be back on main page");
    }

    @Then("^no data is changed from initial state$")
    public void noDataIsChangedFromInitialState() {
        assertEquals(10, page.getParticipantCount(), "Participant count should be unchanged");
        assertDescendingStepOrder();
    }

    // ── Feature 4: Adding steps to participants ────────────────────────────────

    @Given("^I note the current step count for \"([^\"]*)\"$")
    public void iNoteTheCurrentStepCountFor(String name) {
        trackedParticipantName = name;
        trackedInitialSteps = page.getParticipantSteps(page.getParticipantByName(name));
    }

    @When("^I open the \"([^\"]*)\" modal$")
    public void iOpenTheModal(String modal) {
        page.clickAddStepsTop();
    }

    @When("^I select \"([^\"]*)\" from the dropdown$")
    public void iSelectParticipantFromDropdown(String name) {
        trackedParticipantName = name;
        page.selectParticipant(name);
    }

    @When("^I enter \"([^\"]*)\" in the steps input(?: field)?$")
    public void iEnterSteps(String steps) {
        page.enterSteps(steps);
    }

    @When("^I click \"([^\"]*)\" button$")
    public void iClickButton(String button) {
        page.submitAddSteps();
    }

    @Then("^the participant list refreshes$")
    public void theParticipantListRefreshes() {
        assertEquals(10, page.getParticipantCount(), "Participant list should still have 10 entries");
    }

    @Then("^Mike Kid's step count increases by (\\d+)$")
    public void stepCountIncreasesBy(int increase) {
        long newSteps = page.getParticipantSteps(page.getParticipantByName("Mike Kid"));
        assertEquals(trackedInitialSteps + increase, newSteps,
                "Mike Kid's step count should have increased by " + increase);
    }

    @Then("^the list re-sorts if Mike Kid's new total changes his ranking$")
    public void theListReSortsAfterStepUpdate() {
        assertDescendingStepOrder();
    }

    @Then("^the step count updates correctly with the large number$")
    public void theStepCountUpdatesCorrectlyWithTheLargeNumber() {
        assertDescendingStepOrder();
    }

    @Then("^the participant moves to first place$")
    public void theParticipantMovesToFirstPlace() {
        assertNotNull(trackedParticipantName, "No participant was tracked — call 'I select … from the dropdown' first");
        assertEquals(trackedParticipantName, page.getParticipantName(page.getParticipants().getFirst()),
                trackedParticipantName + " should be ranked 1st after adding a large step count");
    }

    // ── Feature 5: Form validation ────────────────────────────────────────────

    @When("^I select (?:a|any) participant$")
    public void iSelectAnyParticipant() {
        trackedParticipantName = "Mike Kid";
        page.selectParticipant(trackedParticipantName);
    }

    @When("^I leave the participant dropdown at \"([^\"]*)\"$")
    public void iLeaveTheParticipantDropdownAt(String text) {
        assertEquals(text, page.getFirstSelectedDropdownOption(),
                "Dropdown should be at its default value before submission");
    }

    @When("^I attempt to click \"([^\"]*)\" button$")
    public void iAttemptToClickButton(String button) {
        page.submitAddSteps();
    }

    @When("^I attempt to enter \"([^\"]*)\" in the steps input field$")
    public void iAttemptToEnterInStepsInputField(String input) {
        page.enterSteps(input);
    }

    @Then("^an alert appears: \"([^\"]*)\"$")
    public void anAlertAppears(String message) {
        assertEquals(message, page.getAlertText(), "Alert message mismatch");
    }

    @When("^I accept the alert$")
    public void iAcceptTheAlert() {
        page.acceptAlert();
    }

    @Then("^the modal remains open$")
    public void theModalRemainsOpen() {
        assertTrue(page.isModalVisible(), "Modal should remain open after validation error");
    }

    @Then("^the input field rejects non-numeric characters$")
    public void theInputFieldRejectsNonNumericCharacters() {
        assertEquals("", page.getStepsInputValue(),
                "Steps input should be empty after entering non-numeric characters");
    }

    @Then("^no alphabetic characters appear in the input$")
    public void noAlphabeticCharactersAppearInTheInput() {
        assertEquals("", page.getStepsInputValue(),
                "Steps input should contain no alphabetic characters");
    }

    // ── Feature 6: Reset functionality ───────────────────────────────────────

    @Given("^I have added steps to at least (\\d+) participants to modify the default state$")
    public void iHaveAddedStepsToAtLeastNParticipants(int num) {
        String[] targets = {"Mike Kid", "Jane Doe", "Emily Chen"};
        for (int i = 0; i < num && i < targets.length; i++) {
            page.clickAddStepsTop();
            page.selectParticipant(targets[i]);
            page.enterSteps("100");
            page.submitAddSteps();
        }
    }

    @Given("^I have modified participant data$")
    public void iHaveModifiedParticipantData() {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("100");
        page.submitAddSteps();
    }

    @When("^I scroll to bottom of page$")
    public void iScrollToBottomOfPage() {
        ((JavascriptExecutor) page.getDriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("^I wait for page reload$")
    public void iWaitForPageReload() {
        // location.reload() is triggered by reset; wait for the participant list to be stable
        page.waitForParticipantList();
    }

    @Then("^all participants return to their default step counts$")
    public void allParticipantsReturnToDefaultStepCounts() {
        var participants = page.getParticipants();
        assertEquals(10, participants.size(), "Expected 10 participants after reset");
        assertEquals("John Smith", page.getParticipantName(participants.getFirst()),
                "John Smith should be ranked 1st after reset");
        assertEquals(15000L, page.getParticipantSteps(participants.getFirst()),
                "John Smith should have 15,000 default steps after reset");
    }

    @Then("^the default ranking order is restored$")
    public void theDefaultRankingOrderIsRestored() {
        assertDescendingStepOrder();
    }

    @Then("^trophy icons display for correct default top (\\d+)$")
    public void trophyIconsForDefaultTop(int top) {
        var participants = page.getParticipants();
        String[] types = {"gold", "silver", "bronze"};
        for (int i = 0; i < top && i < types.length; i++) {
            assertTrue(page.hasTrophy(participants.get(i), types[i]),
                    "Rank " + (i + 1) + " should have " + types[i] + " trophy after reset");
        }
    }

    // ── Feature 7: Edge cases ─────────────────────────────────────────────────

    @When("^I enter the maximum safe integer \"([^\"]*)\"$")
    public void iEnterMaximumSafeInteger(String num) {
        page.enterSteps(num);
    }

    @When("^I submit the form$")
    public void iSubmitTheForm() {
        page.submitAddSteps();
    }

    @Then("^the system accepts the value$")
    public void theSystemAcceptsTheValue() {
        assertFalse(page.isModalVisible(), "Modal should close after accepting the max integer value");
    }

    @Then("^the step count updates correctly$")
    public void theStepCountUpdatesCorrectly() {
        assertFalse(page.isModalVisible(), "Modal should close after step count update");
    }

    // ── Feature 8: Cross-browser compatibility ────────────────────────────────

    @Given("^I open the fitness challenge page in (\\w+)$")
    public void iOpenTheFitnessChallengePageIn(String browser) {
        // Cross-browser switching requires driver re-initialisation; override via -Dbrowser=firefox|edge
        page.openPage();
    }

    @When("^I add valid steps to a participant$")
    public void iAddValidStepsToAParticipant() {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("1000");
        page.submitAddSteps();
    }

    @When("^I submit without selecting participant$")
    public void iSubmitWithoutSelectingParticipant() {
        page.clickAddStepsTop();
        page.enterSteps("1000");
        page.submitAddSteps();
        page.acceptAlert();
        page.closeModal();
    }

    @When("^I reset via top button$")
    public void iResetViaTopButton() {
        page.clickResetTop();
    }

    @Then("^all features work as expected$")
    public void allFeaturesWorkAsExpected() {
        assertEquals(10, page.getParticipantCount(), "Expected 10 participants");
        assertDescendingStepOrder();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void assertDescendingStepOrder() {
        var participants = page.getParticipants();
        for (int i = 0; i < participants.size() - 1; i++) {
            long current = page.getParticipantSteps(participants.get(i));
            long next    = page.getParticipantSteps(participants.get(i + 1));
            assertTrue(current >= next,
                    "Rank " + (i + 1) + " (" + current + " steps) should be >= rank " + (i + 2) + " (" + next + " steps)");
        }
    }
}
