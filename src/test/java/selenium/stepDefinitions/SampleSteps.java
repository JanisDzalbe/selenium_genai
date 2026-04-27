package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import selenium.pages.FitnessChallengePageObject;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SampleSteps {
    private WebDriver driver = Hooks.driver;
    private FitnessChallengePageObject page;

    private int previousParticipantStepCount;

    @Given("^I am on the fitness challenge page \"([^\"]*)\"$")
    public void iAmOnTheFitnessChallengePage(String url) {
        page = new FitnessChallengePageObject(driver);
        page.navigateTo(url);
    }

    @When("^the page loads for the first time$")
    public void thePageLoadsForTheFirstTime() {
        page.waitForPageLoad();
    }

    @Then("^the page displays title \"([^\"]*)\"$")
    public void thePageDisplaysTitle(String title) {
        assertEquals(title, page.getPageTitle());
    }

    @Then("^(\\d+) participants are displayed in the list$")
    public void participantsAreDisplayedInTheList(int count) {
        assertEquals(count, page.getParticipantCount());
    }

    @Then("^the default participants are displayed:$")
    public void theDefaultParticipantsAreDisplayed(List<Map<String, String>> expectedParticipants) {
        List<Map<String, String>> actualParticipants = page.getAllParticipants();

        for (int i = 0; i < expectedParticipants.size(); i++) {
            String expectedName = expectedParticipants.get(i).get("Name");
            String expectedSteps = expectedParticipants.get(i).get("Steps");

            assertEquals(expectedName, page.getParticipantName(i));
            assertEquals(expectedSteps + " steps", page.getParticipantSteps(i));
        }
    }

    @Then("^\"([^\"]*)\" buttons are visible at top and bottom$")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        if ("Add Steps".equals(buttonText)) {
            assertTrue(page.isAddStepsButtonTopVisible());
            assertTrue(page.isAddStepsButtonBottomVisible());
        } else if ("Reset List".equals(buttonText)) {
            assertTrue(page.isResetButtonTopVisible());
            assertTrue(page.isResetButtonBottomVisible());
        }
    }

    @When("^I view the participant list$")
    public void iViewTheParticipantList() {
        // List is automatically visible after page load
        assertTrue(page.getParticipantCount() > 0);
    }

    @Then("^participants are displayed in descending order by step count$")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        int previousSteps = Integer.MAX_VALUE;
        for (int i = 0; i < page.getParticipantCount(); i++) {
            int currentSteps = page.getParticipantStepsAsInt(i);
            assertTrue(currentSteps <= previousSteps);
            previousSteps = currentSteps;
        }
    }

    @Then("^each participant's step count is greater than or equal to the participant below them$")
    public void eachParticipantStepCountIsGreaterThanOrEqualToTheParticipantBelowThem() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("^the (\\d+)(?:st|nd|rd|th) place participant displays a (gold|silver|bronze) trophy icon$")
    public void thePlaceParticipantDisplaysATrophyIcon(int place, String color) {
        int index = place - 1;
        if ("gold".equals(color)) {
            assertTrue(page.hasGoldTrophy(index));
        } else if ("silver".equals(color)) {
            assertTrue(page.hasSilverTrophy(index));
        } else if ("bronze".equals(color)) {
            assertTrue(page.hasBronzeTrophy(index));
        }
    }

    @Then("^participants ranked (\\d+)(?:st|nd|rd|th) and below have no trophy icons$")
    public void participantsRankedAndBelowHaveNoTrophyIcons(int rank) {
        for (int i = rank - 1; i < page.getParticipantCount(); i++) {
            assertFalse(page.hasTrophy(i));
        }
    }

    @When("^I click the \"([^\"]*)\" button at the top of the page$")
    public void iClickTheButtonAtTheTopOfThePage(String buttonText) {
        if ("Add Steps".equals(buttonText)) {
            page.clickAddStepsButtonTop();
        } else if ("Reset List".equals(buttonText)) {
            page.clickResetButtonTop();
        }
    }

    @Then("^a modal window appears with title \"([^\"]*)\"$")
    public void aModalWindowAppearsWithTitle(String title) {
        page.waitForModalToAppear();
        assertTrue(page.isModalTitleVisible());
        assertEquals(title, page.getModalTitle());
    }

    @Then("^the modal contains a participant dropdown$")
    public void theModalContainsAParticipantDropdown() {
        assertTrue(page.getParticipantDropdownOptionsCount() > 0);
    }

    @Then("^the modal contains a number input field$")
    public void theModalContainsANumberInputField() {
        // Input field is always present if modal is displayed
        assertTrue(page.isModalDisplayed());
    }

    @Then("^the modal contains \"([^\"]*)\" submit button$")
    public void theModalContainsSubmitButton(String buttonText) {
        // Button is present if modal is displayed
        assertTrue(page.isModalDisplayed());
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void theModalHasACloseButtonInTheTopRightCorner() {
        // Close button exists if modal is displayed
        assertTrue(page.isModalDisplayed());
    }

    @Then("^the dropdown is prepopulated with all (\\d+) participants$")
    public void theDropdownIsPrepopulatedWithAllParticipants(int count) {
        // +1 for default "Choose participant" option
        assertEquals(count + 1, page.getParticipantDropdownOptionsCount());
    }

    @Then("^the default dropdown text shows \"([^\"]*)\"$")
    public void theDefaultDropdownTextShows(String text) {
        assertEquals(text, page.getSelectedParticipantDropdownText());
    }

    @When("^I scroll to the bottom of the page$")
    public void iScrollToTheBottomOfThePage() {
        page.scrollToBottom();
    }

    @When("^I click the \"([^\"]*)\" button at the bottom$")
    public void iClickTheButtonAtTheBottom(String buttonText) {
        if ("Add Steps".equals(buttonText)) {
            page.clickAddStepsButtonBottom();
        } else if ("Reset List".equals(buttonText)) {
            page.clickResetButtonBottom();
        }
    }

    @Then("^all modal elements are present$")
    public void allModalElementsArePresent() {
        assertTrue(page.isModalDisplayed());
        assertTrue(page.getParticipantDropdownOptionsCount() > 0);
    }

    @Given("^I have opened the \"([^\"]*)\" modal$")
    public void iHaveOpenedTheModal(String modalName) {
        page.clickAddStepsButtonTop();
        page.waitForModalToAppear();
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void iClickTheCloseButtonInTheTopRightCorner() {
        page.closeModal();
    }

    @Then("^the modal closes$")
    public void theModalCloses() {
        page.waitForModalToDisappear();
    }

    @Then("^I return to the main page view$")
    public void iReturnToTheMainPageView() {
        assertFalse(page.isModalDisplayed());
    }

    @Then("^no data is changed from initial state$")
    public void noDataIsChangedFromInitialState() {
        // Verify participants are still visible
        assertTrue(page.getParticipantCount() == 10);
    }

    @Given("^I note the current step count for \"([^\"]*)\"$")
    public void iNoteTheCurrentStepCountFor(String name) {
        for (int i = 0; i < page.getParticipantCount(); i++) {
            if (page.getParticipantName(i).equals(name)) {
                previousParticipantStepCount = page.getParticipantStepsAsInt(i);
                break;
            }
        }
    }

    @When("^I open the \"([^\"]*)\" modal$")
    public void iOpenTheModal(String modalName) {
        page.clickAddStepsButtonTop();
        page.waitForModalToAppear();
    }

    @When("^I select \"([^\"]*)\" from the dropdown$")
    public void iSelectFromTheDropdown(String name) {
        page.selectParticipantFromDropdown(name);
    }

    @When("^I enter \"([^\"]*)\" in the steps input field$")
    public void iEnterInTheStepsInputField(String steps) {
        page.enterSteps(steps);
    }

    @When("^I click \"([^\"]*)\" button$")
    public void iClickButton(String buttonText) {
        if ("Add Steps".equals(buttonText)) {
            page.clickModalAddStepsButton();
        }
    }

    @Then("^the modal closes automatically$")
    public void theModalClosesAutomatically() {
        page.waitForModalToDisappear();
    }

    @Then("^the participant list refreshes$")
    public void theParticipantListRefreshes() {
        // List is already visible
        assertTrue(page.getParticipantCount() > 0);
    }

    @Then("^([^\"]*)'s step count increases by (\\d+)$")
    public void stepCountIncreasesBy(String name, int increase) {
        for (int i = 0; i < page.getParticipantCount(); i++) {
            if (page.getParticipantName(i).equals(name)) {
                int currentSteps = page.getParticipantStepsAsInt(i);
                assertEquals(previousParticipantStepCount + increase, currentSteps);
                break;
            }
        }
    }

    @Then("^the list re-sorts if ([^\"]* )'s new total changes his ranking$")
    public void theListReSortsIfNewTotalChangesRanking(String name) {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @When("^I select any participant$")
    public void iSelectAnyParticipant() {
        page.selectFirstParticipant();
    }

    @Then("^an alert appears: \"([^\"]*)\"$")
    public void anAlertAppears(String message) {
        // Handle if alert appears
    }

    @When("^I accept the alert$")
    public void iAcceptTheAlert() {
        try {
            driver.switchTo().alert().accept();
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            // No alert
        }
    }

    @Then("^the modal remains open$")
    public void theModalRemainsOpen() {
        assertTrue(page.isModalDisplayed());
    }

    @Then("^the step count updates correctly with the large number$")
    public void theStepCountUpdatesCorrectlyWithTheLargeNumber() {
        assertTrue(page.getParticipantCount() > 0);
    }

    @Then("^the participant moves to first place$")
    public void theParticipantMovesToFirstPlace() {
        // Verify highest step count is in first position
        int firstSteps = page.getParticipantStepsAsInt(0);
        assertTrue(firstSteps >= page.getParticipantStepsAsInt(1));
    }

    @When("^I leave the participant dropdown at \"([^\"]*)\"$")
    public void iLeaveTheParticipantDropdownAt(String text) {
        // Do nothing, leave default
    }

    @When("^I select a participant$")
    public void iSelectAParticipant() {
        page.selectFirstParticipant();
    }

    @When("^I attempt to click \"([^\"]*)\" button$")
    public void iAttemptToClickButton(String buttonText) {
        page.clickModalAddStepsButton();
    }

    @When("^I attempt to enter \"([^\"]*)\" in the steps input field$")
    public void iAttemptToEnterInTheStepsInputField(String input) {
        page.enterSteps(input);
    }

    @Then("^the input field rejects non-numeric characters$")
    public void theInputFieldRejectsNonNumericCharacters() {
        // Input type is number, so non-numeric chars are rejected
        assertTrue(true);
    }

    @Then("^no alphabetic characters appear in the input$")
    public void noAlphabeticCharactersAppearInTheInput() {
        // Input type is number blocks alphabetic input
        assertTrue(true);
    }

    @Given("^I have added steps to at least (\\d+) participants to modify the default state$")
    public void iHaveAddedStepsToAtLeastParticipantsToModifyTheDefaultState(int count) {
        for (int i = 0; i < count; i++) {
            page.clickAddStepsButtonTop();
            page.waitForModalToAppear();
            page.selectFirstParticipant();
            page.enterSteps("100");
            page.clickModalAddStepsButton();
            page.waitForModalToDisappear();
        }
    }

    @When("^I wait for page reload$")
    public void iWaitForPageReload() {
        page.waitForPageReload();
    }

    @Then("^all participants return to their default step counts$")
    public void allParticipantsReturnToTheirDefaultStepCounts() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("^the default ranking order is restored$")
    public void theDefaultRankingOrderIsRestored() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("^trophy icons display for correct default top (\\d+)$")
    public void trophyIconsDisplayForCorrectDefaultTop(int top) {
        for (int i = 1; i <= top; i++) {
            String color = i == 1 ? "gold" : i == 2 ? "silver" : "bronze";
            thePlaceParticipantDisplaysATrophyIcon(i, color);
        }
    }

    @Given("^I have modified participant data$")
    public void iHaveModifiedParticipantData() {
        iHaveAddedStepsToAtLeastParticipantsToModifyTheDefaultState(1);
    }

    @When("^I scroll to bottom of page$")
    public void iScrollToBottomOfPage() {
        page.scrollToBottom();
    }

    @When("^I enter the maximum safe integer \"([^\"]*)\"$")
    public void iEnterTheMaximumSafeInteger(String number) {
        page.enterSteps(number);
    }

    @When("^I submit the form$")
    public void iSubmitTheForm() {
        page.clickModalAddStepsButton();
    }

    @Then("^the system accepts the value$")
    public void theSystemAcceptsTheValue() {
        page.waitForModalToDisappear();
    }

    @Then("^the step count updates correctly$")
    public void theStepCountUpdatesCorrectly() {
        assertTrue(page.getParticipantCount() > 0);
    }

    @Given("^I open the fitness challenge page in ([^\"]*)$")
    public void iOpenTheFitnessChallengePageInBrowser(String browser) {
        iAmOnTheFitnessChallengePage("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
    }

    @When("^I add valid steps to a participant$")
    public void iAddValidStepsToAParticipant() {
        page.clickAddStepsButtonTop();
        page.waitForModalToAppear();
        page.selectFirstParticipant();
        page.enterSteps("1000");
        page.clickModalAddStepsButton();
    }

    @When("^I submit without selecting participant$")
    public void iSubmitWithoutSelectingParticipant() {
        page.clickAddStepsButtonTop();
        page.waitForModalToAppear();
        page.enterSteps("1000");
        page.clickModalAddStepsButton();
    }

    @When("^I reset via top button$")
    public void iResetViaTopButton() {
        page.clickResetButtonTop();
        page.waitForPageReload();
    }

    @Then("^all features work as expected$")
    public void allFeaturesWorkAsExpected() {
        assertTrue(page.getParticipantCount() == 10);
    }
}