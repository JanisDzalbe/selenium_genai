package selenium.stepDefinitions;

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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessSteps {
    private static final By ADD_STEPS_BUTTONS = By.id("addStepsBtn");
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANTS = By.cssSelector("#participantsList li");
    private static final By PARTICIPANT_NAME = By.cssSelector(".participant-name");
    private static final By PARTICIPANT_STEPS = By.cssSelector(".participant-steps");
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
        assertEquals(title, driver().findElement(By.cssSelector("#addStepsModal h3")).getText());
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
        assertEquals(buttonText, driver().findElement(MODAL_ADD_BUTTON).getText());
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
            dropdownParticipants.add(select.getOptions().get(i).getText());
        }
        Set<String> listedParticipants = new HashSet<>(getParticipantSteps().keySet());
        assertEquals(expectedParticipants, dropdownParticipants.size());
        assertEquals(listedParticipants, dropdownParticipants);
    }

    @Then("the default dropdown text shows {string}")
    public void dropdownShowsDefaultText(String expectedText) {
        assertEquals(expectedText,
                new Select(driver().findElement(PARTICIPANT_SELECT))
                        .getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        waitForModal(true);
        assertTrue(driver().findElement(By.cssSelector("#addStepsModal h3")).isDisplayed());
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
        assertTrue(driver().findElement(By.cssSelector("h2")).isDisplayed());
        assertTrue(driver().findElements(PARTICIPANTS).get(0).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        assertEquals(stepsBeforeModal, getParticipantSteps());
    }

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
            String name = select.getOptions().get(i).getText();
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
        driver().findElement(STEPS_INPUT).sendKeys(stepsText);
    }

    @When("I click {string} button")
    public void clickModalSubmitButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        driver().findElement(MODAL_ADD_BUTTON).click();
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedText) {
        assertEquals(expectedText, driver().switchTo().alert().getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
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

    private WebDriver driver() {
        return Hooks.driver;
    }

    private void waitForModal(boolean visible) {
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(d -> isModalVisible() == visible);
    }

    private boolean isModalVisible() {
        return driver().findElement(MODAL).isDisplayed();
    }

    private Map<String, Long> getParticipantSteps() {
        Map<String, Long> participantSteps = new LinkedHashMap<>();
        for (WebElement participant : driver().findElements(PARTICIPANTS)) {
            String name = participant.findElement(PARTICIPANT_NAME).getText();
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
        return driver().findElements(PARTICIPANTS).get(index).findElement(PARTICIPANT_NAME).getText();
    }

    private void assertParticipantsSorted() {
        List<WebElement> participants = driver().findElements(PARTICIPANTS);
        for (int i = 1; i < participants.size(); i++) {
            assertTrue("Participants are not sorted by descending step count",
                    getStepCount(participants.get(i - 1)) >= getStepCount(participants.get(i)));
        }
    }
}
