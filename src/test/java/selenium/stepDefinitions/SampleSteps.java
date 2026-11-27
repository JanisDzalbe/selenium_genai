package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SampleSteps {
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By SUBMIT_BUTTON = By.id("modal_add_steps_button");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private Map<String, Integer> participantStateBeforeModal;
    private int initialSteps;
    private String selectedParticipant;
    private int stepsToAdd;

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String pageUrl) {
        driver().get(pageUrl);
        waitForParticipants();
    }

    @When("I click the {string} button at the top of the page")
    public void clickAddStepsButtonAtTop(String buttonLabel) {
        assertEquals("Add Steps", buttonLabel);
        driver().findElements(By.id("addStepsBtn")).get(0).click();
        waitForModal(true);
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) driver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void clickAddStepsButtonAtBottom(String buttonLabel) {
        assertEquals("Add Steps", buttonLabel);
        List<WebElement> buttons = driver().findElements(By.id("addStepsBtn"));
        assertTrue("Expected a bottom Add Steps button", buttons.size() >= 2);
        buttons.get(1).click();
        waitForModal(true);
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String expectedTitle) {
        WebElement modal = modal();
        assertTrue("The Add Steps modal should be visible", modal.isDisplayed());
        assertEquals(expectedTitle, modal.findElement(By.tagName("h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(modal().findElement(PARTICIPANT_SELECT).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInput() {
        WebElement input = modal().findElement(STEPS_INPUT);
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String buttonLabel) {
        WebElement button = modal().findElement(SUBMIT_BUTTON);
        assertTrue(button.isDisplayed());
        assertEquals(buttonLabel, button.getText());
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void modalHasCloseButton() {
        WebElement closeButton = modal().findElement(By.cssSelector(".w3-closebtn"));
        assertTrue(closeButton.isDisplayed());
        assertEquals("×", closeButton.getText());
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownContainsAllParticipants() {
        List<String> participantNames = participantRows().stream()
                .map(row -> row.findElement(By.cssSelector(".participant-name")).getText())
                .sorted()
                .toList();
        List<String> dropdownNames = new Select(modal().findElement(PARTICIPANT_SELECT))
                .getOptions()
                .stream()
                .map(WebElement::getText)
                .filter(name -> !name.equals("Choose participant"))
                .sorted()
                .toList();
        assertEquals(10, dropdownNames.size());
        assertEquals(participantNames, dropdownNames);
    }

    @Then("the default dropdown text shows {string}")
    public void defaultDropdownTextShows(String expectedText) {
        Select dropdown = new Select(modal().findElement(PARTICIPANT_SELECT));
        assertEquals(expectedText, dropdown.getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        assertTrue(modal().isDisplayed());
        assertTrue(modal().findElement(PARTICIPANT_SELECT).isDisplayed());
        WebElement stepsInput = modal().findElement(STEPS_INPUT);
        assertTrue(stepsInput.isDisplayed());
        assertEquals("number", stepsInput.getAttribute("type"));
        assertTrue(modal().findElement(SUBMIT_BUTTON).isDisplayed());
        assertTrue(modal().findElement(By.cssSelector(".w3-closebtn")).isDisplayed());
    }

    @Given("I have opened the {string} modal")
    public void openNamedModal(String modalName) {
        assertEquals("Add Steps", modalName);
        participantStateBeforeModal = participantState();
        driver().findElements(By.id("addStepsBtn")).get(0).click();
        waitForModal(true);
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void clickModalCloseButton() {
        modal().findElement(By.cssSelector(".w3-closebtn")).click();
        waitForModal(false);
    }

    @Then("the modal closes")
    public void modalCloses() {
        waitForModal(false);
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(driver().findElement(By.cssSelector("h2")).isDisplayed());
        assertTrue("The modal should no longer be visible", !modal().isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void dataIsUnchangedFromInitialState() {
        assertEquals(participantStateBeforeModal, participantState());
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String participantName) {
        selectedParticipant = participantName;
        initialSteps = stepsOf(participantName);
    }

    @When("I open the {string} modal")
    public void openAddStepsModal(String modalName) {
        assertEquals("Add Steps", modalName);
        driver().findElements(By.id("addStepsBtn")).get(0).click();
        waitForModal(true);
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String participantName) {
        selectedParticipant = participantName;
        initialSteps = stepsOf(participantName);
        new Select(modal().findElement(PARTICIPANT_SELECT)).selectByVisibleText(participantName);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select dropdown = new Select(modal().findElement(PARTICIPANT_SELECT));
        selectedParticipant = dropdown.getOptions().stream()
                .map(WebElement::getText)
                .filter(name -> !name.equals("Choose participant"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No participants are available in the dropdown"));
        initialSteps = stepsOf(selectedParticipant);
        dropdown.selectByVisibleText(selectedParticipant);
    }

    @When("I enter {string} in the steps input field")
    public void enterSteps(String stepsText) {
        stepsToAdd = Integer.parseInt(stepsText);
        WebElement input = modal().findElement(STEPS_INPUT);
        input.clear();
        input.sendKeys(stepsText);
    }

    @When("I click {string} button")
    public void clickModalSubmitButton(String buttonLabel) {
        assertEquals("Add Steps", buttonLabel);
        modal().findElement(SUBMIT_BUTTON).click();
    }

    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        waitForModal(false);
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        webDriverWait().until(webDriver -> webDriver.findElements(PARTICIPANT_ROWS).size() == 10
                && stepsOf(selectedParticipant) == initialSteps + stepsToAdd);
    }

    @Then("^Mike Kid's step count increases by 1,000$")
    public void mikeKidStepCountIncreases() {
        assertEquals(initialSteps + stepsToAdd, stepsOf("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listResortsWhenMikesRankingChanges() {
        assertDescendingStepCounts();
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedText) {
        assertEquals(expectedText, webDriverWait().until(webDriver -> webDriver.switchTo().alert()).getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        webDriverWait().until(webDriver -> webDriver.switchTo().alert()).accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        waitForModal(true);
    }

    @Then("the step count updates correctly with the large number")
    public void stepCountUpdatesWithLargeNumber() {
        assertEquals(initialSteps + stepsToAdd, stepsOf(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertEquals(selectedParticipant,
                participantRows().get(0).findElement(By.cssSelector(".participant-name")).getText());
    }

    private WebDriver driver() {
        if (Hooks.driver == null) {
            throw new IllegalStateException("The Cucumber browser has not been initialized");
        }
        return Hooks.driver;
    }

    private WebElement modal() {
        return driver().findElement(MODAL);
    }

    private WebDriverWait webDriverWait() {
        return new WebDriverWait(driver(), WAIT_TIMEOUT);
    }

    private void waitForModal(boolean open) {
        webDriverWait().until(webDriver -> open
                ? "block".equals(webDriver.findElement(MODAL).getCssValue("display"))
                : "none".equals(webDriver.findElement(MODAL).getCssValue("display")));
    }

    private void waitForParticipants() {
        webDriverWait().until(webDriver -> webDriver.findElements(PARTICIPANT_ROWS).size() == 10);
    }

    private List<WebElement> participantRows() {
        return driver().findElements(PARTICIPANT_ROWS);
    }

    private int stepsOf(String name) {
        WebElement row = participantRows().stream()
                .filter(candidate -> candidate.findElement(By.cssSelector(".participant-name"))
                        .getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
        String steps = row.findElement(By.cssSelector(".participant-steps")).getText();
        return Integer.parseInt(steps.replaceAll("[^0-9]", ""));
    }

    private Map<String, Integer> participantState() {
        return participantRows().stream().collect(Collectors.toMap(
                row -> row.findElement(By.cssSelector(".participant-name")).getText(),
                row -> Integer.parseInt(row.findElement(By.cssSelector(".participant-steps"))
                        .getText().replaceAll("[^0-9]", "")),
                (first, second) -> first,
                LinkedHashMap::new));
    }

    private void assertDescendingStepCounts() {
        int previousSteps = Integer.MAX_VALUE;
        for (WebElement row : participantRows()) {
            int currentSteps = Integer.parseInt(row.findElement(By.cssSelector(".participant-steps"))
                    .getText().replaceAll("[^0-9]", ""));
            assertTrue("Participant step counts should be in descending order", previousSteps >= currentSteps);
            previousSteps = currentSteps;
        }
    }

}
