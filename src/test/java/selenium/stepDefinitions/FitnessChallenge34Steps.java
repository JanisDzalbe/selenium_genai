package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FitnessChallenge34Steps {
    private static final String PARTICIPANT_ROWS = "#participantsList li";

    private int notedSteps;
    private Map<String, Integer> stateBeforeClose;
    private String selectedParticipant;
    private int stepsBeforeUpdate;

    private WebDriverWait webDriverWait() {
        return new WebDriverWait(Hooks.driver, Duration.ofSeconds(10));
    }

    @When("I click the {string} button at the top of the page")
    public void clickTopButton(String buttonText) {
        visibleButtons(buttonText).get(0).click();
    }

    @Then("a modal window appears with title {string}")
    public void modalAppears(String expectedTitle) {
        WebElement modal = webDriverWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(modal.isDisplayed());
        assertEquals(expectedTitle, modal.findElement(By.cssSelector("h3")).getText().trim());
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(Hooks.driver.findElement(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInput() {
        WebElement input = Hooks.driver.findElement(By.id("steps_input"));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String ignoredButtonText) {
        WebElement button = Hooks.driver.findElement(By.id("modal_add_steps_button"));
        assertTrue(button.isDisplayed());
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void modalHasCloseButton() {
        WebElement close = Hooks.driver.findElement(By.cssSelector("span.w3-closebtn"));
        assertTrue(close.isDisplayed());
        assertEquals("×", close.getText().trim());
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownHasAllParticipants() {
        Select select = new Select(Hooks.driver.findElement(By.id("participant_select")));
        assertEquals(11, select.getOptions().size());
    }

    @Then("the default dropdown text shows {string}")
    public void defaultDropdownText(String expectedText) {
        Select select = new Select(Hooks.driver.findElement(By.id("participant_select")));
        assertEquals(expectedText, select.getFirstSelectedOption().getText().trim());
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottom() {
        ((JavascriptExecutor) Hooks.driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void clickBottomButton(String buttonText) {
        List<WebElement> buttons = visibleButtons(buttonText);
        assertTrue(buttons.size() >= 2, "Expected an Add Steps button at the bottom");
        ((JavascriptExecutor) Hooks.driver).executeScript("arguments[0].scrollIntoView(true);", buttons.get(1));
        buttons.get(1).click();
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        modalAppears("Add Steps to Participant");
        modalContainsParticipantDropdown();
        modalContainsNumberInput();
        modalContainsSubmitButton("Add Steps");
        modalHasCloseButton();
    }

    @Given("I have opened the {string} modal")
    public void haveOpenedModal(String modalTitle) {
        stateBeforeClose = participantState();
        openModal();
        WebElement modal = webDriverWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        String actualTitle = modal.findElement(By.cssSelector("h3")).getText().trim();
        assertTrue(actualTitle.contains(modalTitle),
                "Expected modal title to contain \"" + modalTitle + "\" but was \"" + actualTitle + "\"");
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void clickModalCloseButton() {
        Hooks.driver.findElement(By.cssSelector("span.w3-closebtn")).click();
    }

    @Then("the modal closes")
    public void modalCloses() {
        webDriverWait().until(ExpectedConditions.not(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))));
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertEquals("none",
                Hooks.driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    @Then("no data is changed from initial state")
    public void noDataChanged() {
        assertEquals(stateBeforeClose, participantState());
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String participantName) {
        notedSteps = stepsForParticipant(participantName);
    }

    @When("I open the {string} modal")
    public void openNamedModal(String ignoredModalTitle) {
        openModal();
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String participantName) {
        new Select(Hooks.driver.findElement(By.id("participant_select")))
                .selectByVisibleText(participantName);
        selectedParticipant = participantName;
        stepsBeforeUpdate = stepsForParticipant(participantName);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select select = new Select(Hooks.driver.findElement(By.id("participant_select")));
        select.selectByIndex(1);
        selectedParticipant = select.getFirstSelectedOption().getText().trim();
        stepsBeforeUpdate = stepsForParticipant(selectedParticipant);
    }

    @When("I enter {string} in the steps input field")
    public void enterSteps(String steps) {
        Hooks.driver.findElement(By.id("steps_input")).sendKeys(steps);
    }

    @When("I click {string} button")
    public void clickModalAddStepsButton(String ignoredButtonText) {
        WebElement button = Hooks.driver.findElement(By.id("modal_add_steps_button"));
        button.click();
    }

    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        modalCloses();
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        webDriverWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(PARTICIPANT_ROWS)));
        assertEquals(10, participantRows().size());
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeStepsIncreaseByOneThousand() {
        assertEquals(notedSteps + 1000, stepsForParticipant("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listResorts() {
        assertDescendingRanking();
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedMessage) {
        Alert alert = webDriverWait().until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedMessage, alert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        webDriverWait().until(ExpectedConditions.alertIsPresent()).accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertEquals("block",
                Hooks.driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    @Then("the step count updates correctly with the large number")
    public void stepCountUpdatesWithLargeNumber() {
        assertEquals(stepsBeforeUpdate + 999999, stepsForParticipant(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertEquals(selectedParticipant, participantName(participantRows().get(0)));
    }

    private void openModal() {
        waitForParticipants();
        visibleButtons("Add Steps").get(0).click();
        webDriverWait().until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void waitForParticipants() {
        webDriverWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(PARTICIPANT_ROWS)));
    }

    private List<WebElement> participantRows() {
        waitForParticipants();
        return Hooks.driver.findElements(By.cssSelector(PARTICIPANT_ROWS));
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText().trim();
    }

    private int stepsForRow(WebElement row) {
        String text = row.findElement(By.cssSelector(".participant-steps"))
                .getText()
                .replace(",", "")
                .replace(" steps", "")
                .trim();
        return Integer.parseInt(text);
    }

    private int stepsForParticipant(String name) {
        for (WebElement row : participantRows()) {
            if (participantName(row).equals(name)) {
                return stepsForRow(row);
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    private Map<String, Integer> participantState() {
        Map<String, Integer> state = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            state.put(participantName(row), stepsForRow(row));
        }
        return state;
    }

    private List<WebElement> visibleButtons(String text) {
        List<WebElement> result = new ArrayList<>();
        for (WebElement button : Hooks.driver.findElements(By.xpath(
                "//button[starts-with(normalize-space(.), '" + text + "')]"))) {
            if (button.isDisplayed()) {
                result.add(button);
            }
        }
        return result;
    }

    private void assertDescendingRanking() {
        int previous = Integer.MAX_VALUE;
        for (WebElement row : participantRows()) {
            int steps = stepsForRow(row);
            assertTrue(steps <= previous, "Participants are not in descending step-count order");
            previous = steps;
        }
    }
}
