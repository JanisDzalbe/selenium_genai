package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class FitnessChallengeSteps {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private Map<String, Integer> initialParticipants;
    private String trackedParticipant;
    private int trackedInitialSteps;
    private int trackedInitialRank;
    private String selectedParticipant;
    private int stepsToAdd;

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        trackedParticipant = null;
        selectedParticipant = null;
        stepsToAdd = 0;
        Hooks.driver.get(url);
        waitForParticipants();
        initialParticipants = participantStepCounts();
    }

    @When("the page loads for the first time")
    public void waitForInitialPageLoad() {
        waitForParticipants();
    }

    @Then("the page displays title {string}")
    public void verifyPageTitle(String expectedTitle) {
        Assert.assertEquals(expectedTitle, Hooks.driver.getTitle());
    }

    @Then("{int} participants are displayed in the list")
    public void verifyParticipantCount(int expectedCount) {
        Assert.assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void verifyDefaultParticipants(DataTable table) {
        Map<String, String> expected = new LinkedHashMap<>();
        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            expected.put(row.get("Name").trim(), row.get("Steps").trim());
        }

        Map<String, String> actual = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            actual.put(participantName(row), participantSteps(row));
        }
        Assert.assertEquals(expected, actual);
    }

    @Then("{string} buttons are visible at top and bottom")
    public void verifyButtonsAtTopAndBottom(String buttonText) {
        String buttonId = switch (buttonText) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported fitness-page button: " + buttonText);
        };

        List<WebElement> buttons = Hooks.driver.findElements(By.id(buttonId));
        Assert.assertEquals("Expected top and bottom '" + buttonText + "' buttons", 2, buttons.size());
        Assert.assertTrue("Both '" + buttonText + "' buttons should be visible",
                buttons.stream().allMatch(WebElement::isDisplayed));
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        waitForParticipants();
    }

    @Then("participants are displayed in descending order by step count")
    public void verifyDescendingParticipantOrder() {
        List<Integer> steps = participantRows().stream().map(this::stepCount).toList();
        List<Integer> sorted = new ArrayList<>(steps);
        sorted.sort((left, right) -> Integer.compare(right, left));
        Assert.assertEquals("Participants should be ranked from highest to lowest steps", sorted, steps);
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void verifyEachParticipantRanksAboveOrEqualToNext() {
        List<WebElement> rows = participantRows();
        for (int i = 0; i < rows.size() - 1; i++) {
            Assert.assertTrue(participantName(rows.get(i)) + " should have at least as many steps as "
                            + participantName(rows.get(i + 1)),
                    stepCount(rows.get(i)) >= stepCount(rows.get(i + 1)));
        }
    }

    @Then("^the (1st|2nd|3rd) place participant displays a (gold|silver|bronze) trophy icon$")
    public void verifyTrophyForRank(String rank, String color) {
        int index = switch (rank) {
            case "1st" -> 0;
            case "2nd" -> 1;
            case "3rd" -> 2;
            default -> throw new IllegalArgumentException("Unsupported trophy rank: " + rank);
        };
        String expectedColor = switch (color) {
            case "gold" -> "rgba(255, 215, 0, 1)";
            case "silver" -> "rgba(192, 192, 192, 1)";
            case "bronze" -> "rgba(205, 127, 50, 1)";
            default -> throw new IllegalArgumentException("Unsupported trophy color: " + color);
        };

        List<WebElement> rows = participantRows();
        WebElement trophy = rows.get(index).findElement(By.cssSelector("i.fa-trophy"));
        Assert.assertEquals(expectedColor, trophy.getCssValue("color"));
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void verifyNoTrophiesBelowThirdPlace() {
        List<WebElement> rows = participantRows();
        for (WebElement row : rows.subList(3, rows.size())) {
            Assert.assertTrue("Participant " + participantName(row) + " should not have a trophy",
                    row.findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    @When("I click the {string} button at the top of the page")
    public void clickAddStepsButtonAtTop(String buttonText) {
        Assert.assertEquals("Add Steps", buttonText);
        Hooks.driver.findElements(By.id("addStepsBtn")).get(0).click();
        waitForModal();
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);");
    }

    @When("I click the {string} button at the bottom")
    public void clickAddStepsButtonAtBottom(String buttonText) {
        Assert.assertEquals("Add Steps", buttonText);
        List<WebElement> buttons = Hooks.driver.findElements(By.id("addStepsBtn"));
        WebElement bottomButton = buttons.get(buttons.size() - 1);
        ((JavascriptExecutor) Hooks.driver).executeScript("arguments[0].scrollIntoView(true)", bottomButton);
        bottomButton.click();
        waitForModal();
    }

    @Then("a modal window appears with title {string}")
    public void verifyModalTitle(String expectedTitle) {
        WebElement modal = waitForModal();
        Assert.assertEquals(expectedTitle, modal.findElement(By.tagName("h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void verifyModalParticipantDropdown() {
        Assert.assertTrue(waitForModal().findElement(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void verifyModalNumberInput() {
        WebElement input = waitForModal().findElement(By.id("steps_input"));
        Assert.assertTrue(input.isDisplayed());
        Assert.assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void verifyModalSubmitButton(String buttonText) {
        WebElement button = waitForModal().findElement(By.id("modal_add_steps_button"));
        Assert.assertTrue(button.isDisplayed());
        Assert.assertEquals(buttonText, button.getText());
    }

    @Then("^the modal has a close button \\(.*\\) in the top-right corner$")
    public void verifyModalCloseButton() {
        WebElement closeButton = waitForModal().findElement(By.cssSelector(".w3-closebtn[title='Close Modal']"));
        Assert.assertTrue(closeButton.isDisplayed());
        String closeButtonClasses = closeButton.getAttribute("class");
        Assert.assertNotNull(closeButtonClasses);
        Assert.assertTrue(closeButtonClasses.contains("w3-display-topright"));
    }

    @Then("the dropdown is prepopulated with all {int} participants")
    public void verifyDropdownParticipantOptions(int expectedParticipants) {
        Select participantSelect = new Select(waitForModal().findElement(By.id("participant_select")));
        Assert.assertEquals(expectedParticipants + 1, participantSelect.getOptions().size());
        Assert.assertEquals(expectedParticipants,
                participantSelect.getOptions().stream().skip(1).filter(WebElement::isEnabled).count());
    }

    @Then("the default dropdown text shows {string}")
    public void verifyDefaultParticipantDropdownText(String expectedText) {
        Select participantSelect = new Select(waitForModal().findElement(By.id("participant_select")));
        Assert.assertEquals(expectedText, participantSelect.getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void verifyAllModalElementsArePresent() {
        verifyModalTitle("Add Steps to Participant");
        verifyModalParticipantDropdown();
        verifyModalNumberInput();
        verifyModalSubmitButton("Add Steps");
        verifyModalCloseButton();
        verifyDropdownParticipantOptions(10);
        verifyDefaultParticipantDropdownText("Choose participant");
    }

    @Given("I have opened the {string} modal")
    @When("I open the {string} modal")
    public void openAddStepsModal(String modalName) {
        Assert.assertEquals("Add Steps", modalName);
        Hooks.driver.findElements(By.id("addStepsBtn")).get(0).click();
        waitForModal();
    }

    @When("^I click the . \\(close\\) button in the top-right corner$")
    public void clickModalCloseButton() {
        waitForModal().findElement(By.cssSelector(".w3-closebtn[title='Close Modal']")).click();
    }

    @Then("the modal closes")
    @Then("the modal closes automatically")
    public void verifyModalCloses() {
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(webDriver ->
                "none".equals(webDriver.findElement(By.id("addStepsModal")).getCssValue("display")));
    }

    @Then("I return to the main page view")
    public void verifyMainPageIsVisible() {
        Assert.assertTrue(Hooks.driver.findElement(By.id("participantsList")).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void verifyNoDataChangedFromInitialState() {
        Assert.assertEquals(initialParticipants, participantStepCounts());
    }

    @Given("I note the current step count for {string}")
    public void noteParticipantSteps(String name) {
        List<WebElement> rows = participantRows();
        trackedParticipant = name;
        trackedInitialSteps = stepCount(findParticipantRow(name, rows));
        trackedInitialRank = participantNames(rows).indexOf(name);
        Assert.assertTrue("Participant not present in the list: " + name, trackedInitialRank >= 0);
    }

    @When("I select {string} from the dropdown")
    public void selectParticipantFromDropdown(String name) {
        selectParticipant(name);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select participantSelect = new Select(waitForModal().findElement(By.id("participant_select")));
        participantSelect.selectByIndex(1);
        selectedParticipant = participantSelect.getFirstSelectedOption().getText();
        trackedParticipant = selectedParticipant;
        trackedInitialSteps = participantStepsFor(selectedParticipant);
        trackedInitialRank = participantNames(participantRows()).indexOf(selectedParticipant);
    }

    @When("I enter {string} in the steps input field")
    public void enterStepsInInputField(String value) {
        stepsToAdd = Integer.parseInt(value);
        WebElement input = waitForModal().findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(value);
    }

    @When("I click {string} button")
    public void clickModalAddStepsButton(String buttonText) {
        Assert.assertEquals("Add Steps", buttonText);
        waitForModal().findElement(By.id("modal_add_steps_button")).click();
    }

    @Then("the participant list refreshes")
    public void verifyParticipantListRefreshes() {
        waitForParticipants();
        Assert.assertTrue("Participant list should contain the selected participant",
                participantNames(participantRows()).contains(selectedParticipant));
    }

    @Then("^(.+?)'s step count increases by ([0-9,]+)$")
    public void verifyParticipantStepCountIncreased(String name, String increase) {
        Assert.assertEquals(name, trackedParticipant);
        int stepIncrease = Integer.parseInt(increase.replace(",", ""));
        Assert.assertEquals(trackedInitialSteps + stepIncrease, participantStepsFor(name));
    }

    @Then("^the list re-sorts if (.+?)'s new total changes his ranking$")
    public void verifyListResortedForParticipant(String name) {
        Assert.assertEquals(name, trackedParticipant);
        List<WebElement> rows = participantRows();
        List<Integer> counts = rows.stream().map(this::stepCount).toList();
        List<Integer> sorted = new ArrayList<>(counts);
        sorted.sort((left, right) -> Integer.compare(right, left));
        Assert.assertEquals("Updated list should remain sorted by descending step count", sorted, counts);
        int updatedRank = participantNames(rows).indexOf(name);
        if (trackedInitialSteps + stepsToAdd > 0) {
            Assert.assertTrue("Participant ranking should not move down after adding steps",
                    updatedRank <= trackedInitialRank);
        }
    }

    @Then("an alert appears: {string}")
    public void verifyAlertAppears(String expectedMessage) {
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(ExpectedConditions.alertIsPresent());
        Assert.assertEquals(expectedMessage, Hooks.driver.switchTo().alert().getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        Hooks.driver.switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void verifyModalRemainsOpen() {
        Assert.assertTrue(waitForModal().isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void verifyLargeStepCountUpdate() {
        Assert.assertEquals(participantStartingSteps() + stepsToAdd, participantStepsFor(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void verifyParticipantMovesToFirstPlace() {
        Assert.assertEquals(selectedParticipant, participantName(participantRows().get(0)));
    }

    private WebElement waitForModal() {
        return new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void selectParticipant(String name) {
        Select participantSelect = new Select(waitForModal().findElement(By.id("participant_select")));
        participantSelect.selectByVisibleText(name);
        selectedParticipant = name;
        if (trackedParticipant == null) {
            trackedParticipant = name;
            trackedInitialSteps = participantStepsFor(name);
            trackedInitialRank = participantNames(participantRows()).indexOf(name);
        }
    }

    private Map<String, Integer> participantStepCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            counts.put(participantName(row), stepCount(row));
        }
        return counts;
    }

    private List<String> participantNames(List<WebElement> rows) {
        return rows.stream().map(this::participantName).toList();
    }

    private WebElement findParticipantRow(String name, List<WebElement> rows) {
        return rows.stream()
                .filter(row -> name.equals(participantName(row)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private int participantStepsFor(String name) {
        return stepCount(findParticipantRow(name, participantRows()));
    }

    private int participantStartingSteps() {
        Assert.assertNotNull("A participant must be selected before checking its step count", trackedParticipant);
        return trackedInitialSteps;
    }

    private List<WebElement> participantRows() {
        waitForParticipants();
        return Hooks.driver.findElements(By.cssSelector("#participantsList li"));
    }

    private void waitForParticipants() {
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.findElements(By.cssSelector("#participantsList li")).size() == 10);
    }

    private String participantName(WebElement row) {
        return row.findElement(By.className("participant-name")).getText();
    }

    private String participantSteps(WebElement row) {
        return row.findElement(By.className("participant-steps")).getText()
                .replaceFirst("\\s+steps$", "");
    }

    private int stepCount(WebElement row) {
        return Integer.parseInt(participantSteps(row).replaceAll(Pattern.quote(","), ""));
    }
}
