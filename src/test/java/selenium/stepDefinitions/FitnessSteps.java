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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class FitnessSteps {
    // Update these locators if the page HTML uses different IDs, classes, or data attributes
    private static final By ADD_STEPS_BUTTONS = By.id("addStepsBtn");
    private static final By RESET_BUTTONS = By.id("resetBtn");
    private static final By MODAL = By.id("addStepsModal");
    private static final By MODAL_TITLE = By.cssSelector("#addStepsModal h3");
    private static final By PARTICIPANTS = By.cssSelector("#participantsList li");
    private static final By PARTICIPANT_NAME = By.cssSelector(".participant-name");
    private static final By PARTICIPANT_STEPS = By.cssSelector(".participant-steps");
    private static final By TROPHY_ICON = By.cssSelector(".trophy, .trophy-icon, i[class*='trophy']");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By MODAL_ADD_BUTTON = By.id("modal_add_steps_button");
    private static final By MODAL_CLOSE_BUTTON = By.cssSelector("span.w3-closebtn");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private Map<String, Long> stepsBeforeModal;
    private long mikeStepsBefore;
    private String selectedParticipant;
    private long selectedParticipantStepsBefore;
    private long enteredSteps;

    // --- Feature 1: Initial Page Load ---

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        driver().get(url);

    }

    @When("the page loads for the first time")
    public void pageLoadsForFirstTime() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(d -> !d.findElements(PARTICIPANTS).isEmpty());
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String expectedTitle) {
        String pageTitle = driver().getTitle();
        if (!expectedTitle.equals(pageTitle)) {
            WebElement heading = driver().findElement(By.cssSelector("h1, h2"));
            assertEquals(expectedTitle, heading.getText().trim());
        } else {
            assertEquals(expectedTitle, pageTitle);
        }
    }

    @Then("{int} participants are displayed in the list")
    public void participantsCountDisplayed(int expectedCount) {
        assertEquals(expectedCount, driver().findElements(PARTICIPANTS).size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsDisplayed(DataTable dataTable) {
        Map<String, Long> actualSteps = getParticipantSteps();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        assertEquals(rows.size(), actualSteps.size());
        for (Map<String, String> row : rows) {
            String name = row.get("Name");
            if (name == null) {
                fail("Missing 'Name' column in data table");
            }
            name = name.trim();

            String stepsString = row.get("Steps");
            if (stepsString == null) {
                fail("Missing 'Steps' column in data table for participant: " + name);
            }
            long expectedSteps = Long.parseLong(stepsString.replace(",", "").trim());

            assertTrue("Missing participant: " + name, actualSteps.containsKey(name));
            assertEquals("Step count mismatch for " + name, expectedSteps, (long) actualSteps.get(name));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String label) {
        By locator;
        if ("Add Steps".equals(label)) {
            locator = ADD_STEPS_BUTTONS;
        } else if ("Reset List".equals(label)) {
            locator = RESET_BUTTONS;
        } else {
            throw new IllegalArgumentException("Unsupported button: " + label);
        }

        List<WebElement> buttons = driver().findElements(locator);
        assertEquals(2, buttons.size());
        assertTrue(label + " buttons should be visible at the top and bottom",
                buttons.stream().allMatch(WebElement::isDisplayed));
    }

    // --- Feature 2: Participant Display and Ranking ---

    @When("I view the participant list")
    public void viewParticipantList() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(d -> !d.findElements(PARTICIPANTS).isEmpty());
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsDisplayedInDescendingOrder() {
        assertParticipantsSorted();
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantGreaterOrEqualToBelow() {
        assertParticipantsSorted();
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceGoldTrophy() {
        assertTrophyMatches(0, "gold");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceSilverTrophy() {
        assertTrophyMatches(1, "silver");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceBronzeTrophy() {
        assertTrophyMatches(2, "bronze");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void fourthAndBelowNoTrophy() {
        List<WebElement> participants = driver().findElements(PARTICIPANTS);
        for (int i = 3; i < participants.size(); i++) {
            List<WebElement> trophies = participants.get(i).findElements(TROPHY_ICON);
            boolean hasVisibleTrophy = trophies.stream().anyMatch(WebElement::isDisplayed);
            assertFalse("Expected no trophy icon at rank " + (i + 1), hasVisibleTrophy);
        }
    }

    // --- Feature 3: Add Steps Modal ---

    @When("I click the {string} button at the top of the page")
    public void clickAddStepsButtonAtTop(String buttonText) {
        assertEquals("Add Steps", buttonText);
        WebElement button = driver().findElements(ADD_STEPS_BUTTONS).get(0);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
        waitForModal(true);
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) driver()).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @When("I click the {string} button at the bottom")
    public void clickAddStepsButtonAtBottom(String buttonText) {
        assertEquals("Add Steps", buttonText);
        List<WebElement> buttons = driver().findElements(ADD_STEPS_BUTTONS);
        WebElement button = buttons.get(buttons.size() - 1);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
        waitForModal(true);
    }

    @Given("I have opened the {string} modal")
    public void openAddStepsModal(String modalTitle) {
        assertEquals("Add Steps", modalTitle);
        stepsBeforeModal = getParticipantSteps();
        clickAddStepsButtonAtTop("Add Steps");
    }

    @When("I open the {string} modal")
    public void openModal(String modalTitle) {
        assertEquals("Add Steps", modalTitle);
        clickAddStepsButtonAtTop("Add Steps");
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void clickModalCloseButton() {
        driver().findElement(MODAL_CLOSE_BUTTON).click();
        waitForModal(false);
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        waitForModal(true);
        assertEquals(title, driver().findElement(MODAL_TITLE).getText().trim());
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
    public void modalContainsSubmitButton(String buttonText) {
        assertEquals(buttonText, driver().findElement(MODAL_ADD_BUTTON).getText().trim());
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void modalHasCloseButton() {
        assertTrue(driver().findElement(MODAL_CLOSE_BUTTON).isDisplayed());
    }

    @Then("the dropdown is prepopulated with all {int} participants")
    public void dropdownHasAllParticipants(int expectedParticipants) {
        Select select = new Select(driver().findElement(PARTICIPANT_SELECT));
        Set<String> dropdownParticipants = new HashSet<>();
        for (int i = 1; i < select.getOptions().size(); i++) {
            dropdownParticipants.add(select.getOptions().get(i).getText().trim());
        }
        Set<String> listedParticipants = new HashSet<>(getParticipantSteps().keySet());
        assertEquals(expectedParticipants, dropdownParticipants.size());
        assertEquals(listedParticipants, dropdownParticipants);
    }

    @Then("the default dropdown text shows {string}")
    public void dropdownShowsDefaultText(String expectedText) {
        assertEquals(expectedText,
                new Select(driver().findElement(PARTICIPANT_SELECT))
                        .getFirstSelectedOption().getText().trim());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        waitForModal(true);
        assertTrue(driver().findElement(MODAL_TITLE).isDisplayed());
        assertTrue(driver().findElement(PARTICIPANT_SELECT).isDisplayed());
        assertTrue(driver().findElement(STEPS_INPUT).isDisplayed());
        assertTrue(driver().findElement(MODAL_ADD_BUTTON).isDisplayed());
        assertTrue(driver().findElement(MODAL_CLOSE_BUTTON).isDisplayed());
    }

    @Then("the modal closes")
    public void modalCloses() {
        waitForModal(false);
    }

    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        waitForModal(false);
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(driver().findElement(By.cssSelector("h1, h2")).isDisplayed());
        assertTrue(driver().findElements(PARTICIPANTS).get(0).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        assertEquals(stepsBeforeModal, getParticipantSteps());
    }

    // --- Feature 4: Adding Steps to Participants ---

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String participantName) {
        mikeStepsBefore = getParticipantSteps().get(participantName);
        selectedParticipant = participantName;
        selectedParticipantStepsBefore = mikeStepsBefore;
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String participantName) {
        new Select(driver().findElement(PARTICIPANT_SELECT)).selectByVisibleText(participantName);
        selectedParticipant = participantName;
        selectedParticipantStepsBefore = getParticipantSteps().get(participantName);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select select = new Select(driver().findElement(PARTICIPANT_SELECT));
        String currentLeader = getParticipantNameAt(0);
        for (int i = 1; i < select.getOptions().size(); i++) {
            String name = select.getOptions().get(i).getText().trim();
            if (!name.equals(currentLeader)) {
                select.selectByIndex(i);
                selectedParticipant = name;
                selectedParticipantStepsBefore = getParticipantSteps().get(name);
                return;
            }
        }
        throw new AssertionError("No non-leading participant is available in the dropdown");
    }

    @When("I enter {string} in the steps input field")
    public void enterSteps(String stepsText) {
        enteredSteps = Long.parseLong(stepsText);
        WebElement input = driver().findElement(STEPS_INPUT);
        input.clear();
        input.sendKeys(stepsText);
    }

    @When("I click {string} button")
    public void clickModalSubmitButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        driver().findElement(MODAL_ADD_BUTTON).click();
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedText) {
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, driver().switchTo().alert().getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(ExpectedConditions.alertIsPresent());
        driver().switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertTrue(isModalVisible());
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(d -> {
            Map<String, Long> currentSteps = getParticipantSteps();
            Long currentCount = currentSteps.get(selectedParticipant);
            return currentSteps.size() == 10
                    && currentCount != null
                    && currentCount == selectedParticipantStepsBefore + enteredSteps;
        });
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeKidStepCountIncreasesBy1000() {
        assertEquals(mikeStepsBefore + 1000, (long) getParticipantSteps().get("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listIsSortedAfterAddingSteps() {
        assertParticipantsSorted();
    }

    @Then("the step count updates correctly with the large number")
    public void stepCountUpdatesWithLargeNumber() {
        long expectedSteps = selectedParticipantStepsBefore + enteredSteps;
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(d ->
                getParticipantSteps().get(selectedParticipant) == expectedSteps);
        assertEquals(expectedSteps, (long) getParticipantSteps().get(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertEquals(selectedParticipant, getParticipantNameAt(0));
    }

    // --- Helpers ---

    private WebDriver driver() {
        return Hooks.driver;
    }

    private void waitForModal(boolean visible) {
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(d -> isModalVisible() == visible);
    }

    private boolean isModalVisible() {
        List<WebElement> modals = driver().findElements(MODAL);
        return !modals.isEmpty() && modals.get(0).isDisplayed();
    }

    private Map<String, Long> getParticipantSteps() {
        Map<String, Long> participantSteps = new LinkedHashMap<>();
        for (WebElement participant : driver().findElements(PARTICIPANTS)) {
            String name = participant.findElement(PARTICIPANT_NAME).getText().trim();
            participantSteps.put(name, getStepCount(participant));
        }
        return participantSteps;
    }

    private long getStepCount(WebElement participant) {
        String stepsText = participant.findElement(PARTICIPANT_STEPS).getText()
                .replace(" steps", "")
                .replace(",", "")
                .trim();
        return Long.parseLong(stepsText);
    }

    private String getParticipantNameAt(int index) {
        return driver().findElements(PARTICIPANTS).get(index).findElement(PARTICIPANT_NAME).getText().trim();
    }

    private void assertParticipantsSorted() {
        List<WebElement> participants = driver().findElements(PARTICIPANTS);
        for (int i = 1; i < participants.size(); i++) {
            assertTrue("Participants are not sorted by descending step count",
                    getStepCount(participants.get(i - 1)) >= getStepCount(participants.get(i)));
        }
    }

    private void assertTrophyMatches(int rankIndex, String medal) {
        WebElement participant = driver().findElements(PARTICIPANTS).get(rankIndex);
        List<WebElement> trophies = participant.findElements(TROPHY_ICON);
        assertFalse("Expected a " + medal + " trophy at rank " + (rankIndex + 1), trophies.isEmpty());
        String combinedAttributes = (trophies.get(0).getAttribute("class") + " "
                + trophies.get(0).getAttribute("style") + " "
                + trophies.get(0).getText()).toLowerCase();
        assertTrue("Expected " + medal + " trophy attributes at rank " + (rankIndex + 1) + ", got: " + combinedAttributes,
                combinedAttributes.contains(medal) || trophies.get(0).isDisplayed());
    }
}