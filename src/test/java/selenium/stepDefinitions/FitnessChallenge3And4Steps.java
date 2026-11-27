package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FitnessChallenge3And4Steps {
    private static final By ADD_STEPS_BUTTONS = By.id("addStepsBtn");
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By MODAL_SUBMIT_BUTTON = By.id("modal_add_steps_button");
    private static final By MODAL_CLOSE_BUTTON =
            By.cssSelector("#addStepsModal span[title='Close Modal']");
    private static final By PARTICIPANT_ROWS =
            By.cssSelector("#participantsList li[id^='participant_display']");
    private static final By PARTICIPANT_NAME = By.className("participant-name");
    private static final By PARTICIPANT_STEPS = By.className("participant-steps");
    private static final List<String> DEFAULT_PARTICIPANTS = List.of(
            "Mike Kid", "Jill Watson", "Jane Doe", "John Smith", "Sarah Johnson",
            "Carlos Garcia", "Emily Chen", "David Brown", "Maria Rodriguez", "Alex Taylor");

    private String selectedParticipant;
    private int stepsBeforeUpdate;
    private int stepsToAdd;
    private Alert pendingAlert;

    @When("I click the {string} button at the top of the page")
    public void clickTopAddStepsButton(String buttonText) throws Throwable{
        assertEquals("Add Steps", buttonText);
        List<WebElement> buttons = Hooks.driver.findElements(ADD_STEPS_BUTTONS);
        assertEquals(2, buttons.size());
        buttons.get(0).click();
        webDriverWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);");
    }

    @When("I click the {string} button at the bottom")
    public void clickBottomAddStepsButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        List<WebElement> buttons = Hooks.driver.findElements(ADD_STEPS_BUTTONS);
        assertEquals(2, buttons.size());
        WebElement bottomButton = buttons.get(1);
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", bottomButton);
        webDriverWait().until(ExpectedConditions.elementToBeClickable(bottomButton)).click();
        webDriverWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    @Given("I have opened the {string} modal")
    public void openAddStepsModal(String modalName) {
        assertEquals("Add Steps", modalName);
        Hooks.driver.findElements(ADD_STEPS_BUTTONS).get(0).click();
        webDriverWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        WebElement modal = webDriverWait().until(
                ExpectedConditions.visibilityOfElementLocated(MODAL));
        assertEquals(title, modal.findElement(By.tagName("h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        WebElement dropdown = Hooks.driver.findElement(PARTICIPANT_SELECT);
        assertTrue(dropdown.isDisplayed());
        assertEquals("select", dropdown.getTagName());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInput() {
        WebElement input = Hooks.driver.findElement(STEPS_INPUT);
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String label) {
        WebElement button = Hooks.driver.findElement(MODAL_SUBMIT_BUTTON);
        assertTrue(button.isDisplayed());
        assertEquals(label, button.getText());
    }

    @Then("the modal has a close button \\(×) in the top-right corner")
    public void modalHasTopRightCloseButton() {
        WebElement closeButton = Hooks.driver.findElement(MODAL_CLOSE_BUTTON);
        assertTrue(closeButton.isDisplayed());
        assertEquals("×", closeButton.getText());
        assertTrue(closeButton.getAttribute("class").contains("w3-display-topright"));
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownContainsAllParticipants() {
        List<String> options = new Select(Hooks.driver.findElement(PARTICIPANT_SELECT))
                .getOptions().stream()
                .skip(1)
                .map(WebElement::getText)
                .toList();
        assertEquals(DEFAULT_PARTICIPANTS, options);
    }

    @Then("the default dropdown text shows {string}")
    public void defaultDropdownText(String expectedText) {
        Select dropdown = new Select(Hooks.driver.findElement(PARTICIPANT_SELECT));
        assertEquals(expectedText, dropdown.getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        modalAppearsWithTitle("Add Steps to Participant");
        modalContainsParticipantDropdown();
        modalContainsNumberInput();
        modalContainsSubmitButton("Add Steps");
        modalHasTopRightCloseButton();
        dropdownContainsAllParticipants();
        defaultDropdownText("Choose participant");
    }

    @When("I click the × \\(close) button in the top-right corner")
    public void clickModalCloseButton() {
        Hooks.driver.findElement(MODAL_CLOSE_BUTTON).click();
    }

    @Then("the modal closes")
    public void modalCloses() {
        webDriverWait().until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(Hooks.driver.findElement(By.id("participantsList")).isDisplayed());
        assertFalse(Hooks.driver.findElement(MODAL).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        assertEquals(10, Hooks.driver.findElements(PARTICIPANT_ROWS).size());
        for (WebElement row : Hooks.driver.findElements(PARTICIPANT_ROWS)) {
            String name = row.findElement(PARTICIPANT_NAME).getText();
            int expectedSteps = switch (name) {
                case "Mike Kid" -> 8500;
                case "Jill Watson" -> 12000;
                case "Jane Doe" -> 6500;
                case "John Smith" -> 15000;
                case "Sarah Johnson" -> 9800;
                case "Carlos Garcia" -> 11200;
                case "Emily Chen" -> 7300;
                case "David Brown" -> 13500;
                case "Maria Rodriguez" -> 10500;
                case "Alex Taylor" -> 8900;
                default -> throw new AssertionError("Unexpected participant: " + name);
            };
            assertEquals(expectedSteps, parseSteps(row.findElement(PARTICIPANT_STEPS).getText()));
        }
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String participant) {
        selectedParticipant = participant;
        stepsBeforeUpdate = getDisplayedSteps(participant);
    }

    @When("I open the {string} modal")
    public void openModal(String modalName) {
        openModalNamed(modalName);
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String participant) {
        selectParticipantAndRecordInitialSteps(participant);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        selectParticipantAndRecordInitialSteps(DEFAULT_PARTICIPANTS.get(0));
    }

    @When("I enter {string} in the steps input field")
    public void enterStepsInInputField(String steps) {
        enterSteps(steps);
    }

    @When("I click {string} button")
    public void clickButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        Hooks.driver.findElement(MODAL_SUBMIT_BUTTON).click();
    }

    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        modalCloses();
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        webDriverWait().until(ExpectedConditions.numberOfElementsToBe(PARTICIPANT_ROWS, 10));
        assertEquals(10, Hooks.driver.findElements(PARTICIPANT_ROWS).size());
    }

    @Then("Mike Kid's step count increases by {double}")
    public void mikeKidStepCountIncreasesBy(double increase) {
        assertEquals(stepsBeforeUpdate + increase, getDisplayedSteps("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listResortsWhenMikeKidRankingChanges() {
        List<Integer> displayedSteps = Hooks.driver.findElements(PARTICIPANT_ROWS).stream()
                .map(row -> parseSteps(row.findElement(PARTICIPANT_STEPS).getText()))
                .toList();
        for (int i = 1; i < displayedSteps.size(); i++) {
            assertTrue(displayedSteps.get(i - 1) >= displayedSteps.get(i),
                    "Participant list should be re-sorted by descending steps");
        }
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedText) {
        pendingAlert = webDriverWait().until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, pendingAlert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        assertTrue(pendingAlert != null, "Expected to have captured an alert before accepting it");
        pendingAlert.accept();
        pendingAlert = null;
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertTrue(Hooks.driver.findElement(MODAL).isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void stepCountUpdatesWithLargeNumber() {
        assertEquals(stepsBeforeUpdate + stepsToAdd, getDisplayedSteps(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertEquals(selectedParticipant, Hooks.driver.findElement(PARTICIPANT_ROWS)
                .findElement(PARTICIPANT_NAME).getText());
    }

    private void openModalNamed(String modalName) {
        assertEquals("Add Steps", modalName);
        Hooks.driver.findElements(ADD_STEPS_BUTTONS).get(0).click();
        webDriverWait().until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    private void selectParticipantAndRecordInitialSteps(String participant) {
        selectedParticipant = participant;
        stepsBeforeUpdate = getDisplayedSteps(participant);
        new Select(Hooks.driver.findElement(PARTICIPANT_SELECT)).selectByVisibleText(participant);
    }

    private void enterSteps(String steps) {
        stepsToAdd = Integer.parseInt(steps);
        WebElement input = Hooks.driver.findElement(STEPS_INPUT);
        input.clear();
        input.sendKeys(steps);
    }

    private int getDisplayedSteps(String participant) {
        for (WebElement row : Hooks.driver.findElements(PARTICIPANT_ROWS)) {
            if (row.findElement(PARTICIPANT_NAME).getText().equals(participant)) {
                return parseSteps(row.findElement(PARTICIPANT_STEPS).getText());
            }
        }
        throw new AssertionError("Participant not found: " + participant);
    }

    private int parseSteps(String displayedSteps) {
        return Integer.parseInt(displayedSteps.replaceAll("[^0-9]", ""));
    }

    private WebDriverWait webDriverWait() {
        return new WebDriverWait(Hooks.driver, Duration.ofSeconds(10));
    }
}
