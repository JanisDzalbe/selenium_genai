package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FitnessChallengeSteps {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final List<String> DEFAULT_NAMES = Arrays.asList(
            "John Smith", "David Brown", "Jill Watson", "Carlos Garcia",
            "Maria Rodriguez", "Sarah Johnson", "Alex Taylor", "Mike Kid",
            "Emily Chen", "Jane Doe");
    private static final List<String> DEFAULT_STEPS = Arrays.asList(
            "15,000 steps", "13,500 steps", "12,000 steps", "11,200 steps",
            "10,500 steps", "9,800 steps", "8,900 steps", "8,500 steps",
            "7,300 steps", "6,500 steps");
    private static final List<String> DEFAULT_TROPHY_COLORS = Arrays.asList(
            "rgba(255, 215, 0, 1)",
            "rgba(192, 192, 192, 1)",
            "rgba(205, 127, 50, 1)",
            "none", "none", "none", "none", "none", "none", "none");

    private List<String> initialNames;
    private List<String> initialSteps;
    private Map<String, Long> initialCounts;
    private String selectedParticipant;
    private long expectedUpdatedCount;

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String pageUrl) {
        Hooks.driver.get(pageUrl);
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "for (var i = localStorage.length - 1; i >= 0; i--) {" +
                        "var key = localStorage.key(i);" +
                        "if (key && (key.startsWith('participant') || key === 'fitnessReload'))" +
                        "localStorage.removeItem(key);" +
                        "}");
        Hooks.driver.navigate().refresh();
        waitVisible(By.id("participantsList"));
        initialNames = participantNames();
        initialSteps = participantSteps();
        initialCounts = participantCounts();
    }

    @When("the page loads for the first time")
    public void waitForFirstPageLoad() {
        waitVisible(By.id("participantsList"));
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String title) {
        assertEquals(title, Hooks.driver.findElement(By.cssSelector("h2")).getText());
    }

    @And("{int} participants are displayed in the list")
    public void participantsAreDisplayed(int expectedSize) {
        assertEquals(expectedSize, participantRows().size());
    }

    @And("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        List<Map<String, String>> rankedRows = rows.stream()
                .sorted(Comparator.comparingLong(
                        (Map<String, String> row) -> parseStepCount(row.get("Steps")))
                        .reversed())
                .toList();
        List<String> expectedNames = rankedRows.stream()
                .map(row -> row.get("Name").trim()).toList();
        List<String> expectedSteps = rankedRows.stream()
                .map(row -> row.get("Steps").trim() + " steps").toList();

        assertEquals(expectedNames.size(), participantRows().size());
        assertEquals(expectedNames, participantNames());
        assertEquals(expectedSteps, participantSteps());
    }

    @And("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        String id = switch (buttonText) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported button: " + buttonText);
        };
        List<WebElement> buttons = Hooks.driver.findElements(By.id(id));
        assertEquals(2, buttons.size());
        assertTrue(buttons.stream().allMatch(WebElement::isDisplayed));
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        waitVisible(By.id("participantsList"));
    }

    @Then("participants are displayed in descending order by step count")
    @And("each participant's step count is greater than or equal to the participant below them")
    public void participantsAreRankedByStepCount() {
        List<Long> actualSteps = numericParticipantSteps();
        List<Long> expectedSteps = DEFAULT_STEPS.stream()
                .map(FitnessChallengeSteps::parseStepCount).toList();
        assertEquals(DEFAULT_NAMES.size(), actualSteps.size());
        assertEquals(expectedSteps, actualSteps);
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceDisplaysGoldTrophy() {
        assertTrophyColors();
    }

    @And("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceDisplaysSilverTrophy() {
        assertTrophyColors();
    }

    @And("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceDisplaysBronzeTrophy() {
        assertTrophyColors();
    }

    @And("participants ranked 4th and below have no trophy icons")
    public void lowerRanksHaveNoTrophyIcons() {
        assertTrophyColors();
    }

    @When("I click the {string} button at the top of the page")
    public void clickAddStepsButtonAtTop(String buttonText) {
        assertEquals("Add Steps", buttonText);
        Hooks.driver.findElements(By.id("addStepsBtn")).get(0).click();
        waitModalOpen();
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight)");
    }

    @And("I click the {string} button at the bottom")
    public void clickAddStepsButtonAtBottom(String buttonText) {
        assertEquals("Add Steps", buttonText);
        Hooks.driver.findElements(By.id("addStepsBtn")).get(1).click();
        waitModalOpen();
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        waitModalOpen();
        assertEquals(title, Hooks.driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
    }

    @And("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(Hooks.driver.findElement(By.id("participant_select")).isDisplayed());
    }

    @And("the modal contains a number input field")
    public void modalContainsNumberInput() {
        assertTrue(Hooks.driver.findElement(By.id("steps_input")).isDisplayed());
    }

    @And("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String text) {
        assertEquals(text, Hooks.driver.findElement(By.id("modal_add_steps_button")).getText());
    }

    @And("^the modal has a close button \\(×\\) in the top-right corner$")
    public void modalHasCloseButton() {
        assertTrue(Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed());
    }

    @And("the dropdown is prepopulated with all {int} participants")
    public void dropdownContainsParticipants(int expectedSize) {
        List<String> actualNames = new Select(Hooks.driver.findElement(By.id("participant_select")))
                .getOptions().stream().skip(1).map(WebElement::getText).sorted().toList();
        List<String> expectedNames = new ArrayList<>(DEFAULT_NAMES);
        expectedNames.sort(String::compareTo);

        assertEquals(expectedSize, actualNames.size());
        assertEquals(expectedNames, actualNames);
    }

    @And("the default dropdown text shows {string}")
    public void defaultDropdownTextIs(String text) {
        assertEquals(text, new Select(Hooks.driver.findElement(By.id("participant_select")))
                .getFirstSelectedOption().getText());
    }

    @And("all modal elements are present")
    public void allModalElementsArePresent() {
        assertModalElements();
    }

    @Given("I have opened the {string} modal")
    public void openNamedModal(String modalName) {
        assertEquals("Add Steps", modalName);
        Hooks.driver.findElements(By.id("addStepsBtn")).get(0).click();
        waitModalOpen();
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void clickModalCloseButton() {
        Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
    }

    @Then("the modal closes")
    @And("the modal closes automatically")
    public void modalCloses() {
        waitModalClosed();
    }

    @And("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(waitVisible(By.id("participantsList")).isDisplayed());
    }

    @And("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        assertEquals(initialNames, participantNames());
        assertEquals(initialSteps, participantSteps());
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String participant) {
        selectedParticipant = participant;
        assertTrue(initialCounts.containsKey(participant), "Unknown participant: " + participant);
        expectedUpdatedCount = initialCounts.get(participant);
    }

    @When("I open the {string} modal")
    public void openNamedAddStepsModal(String modalName) {
        assertEquals("Add Steps", modalName);
        Hooks.driver.findElements(By.id("addStepsBtn")).get(0).click();
        waitModalOpen();
    }

    @And("I select {string} from the dropdown")
    public void selectParticipant(String participant) {
        new Select(Hooks.driver.findElement(By.id("participant_select"))).selectByVisibleText(participant);
        selectedParticipant = participant;
    }

    @And("I select any participant")
    public void selectAnyParticipant() {
        Select dropdown = new Select(Hooks.driver.findElement(By.id("participant_select")));
        dropdown.selectByVisibleText(DEFAULT_NAMES.get(0));
        selectedParticipant = dropdown.getFirstSelectedOption().getText();
    }

    @And("I enter {string} in the steps input field")
    public void enterSteps(String steps) {
        Hooks.driver.findElement(By.id("steps_input")).sendKeys(steps);
    }

    @And("I click {string} button")
    public void clickSubmitButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        String input = Hooks.driver.findElement(By.id("steps_input")).getAttribute("value");
        if (selectedParticipant != null && input != null && input.matches("\\d+")) {
            expectedUpdatedCount = initialCounts.get(selectedParticipant) + Long.parseLong(input);
        }
        Hooks.driver.findElement(By.id("modal_add_steps_button")).click();
    }

    @And("the participant list refreshes")
    public void participantListRefreshes() {
        waitModalClosed();
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(driver ->
                participantStepCount(selectedParticipant) == expectedUpdatedCount);
        assertEquals(DEFAULT_NAMES.size(), participantRows().size());
    }

    @And("Mike Kid's step count increases by 1,000")
    public void mikeKidStepCountIncreases() {
        assertEquals(initialCounts.get("Mike Kid") + 1_000,
                participantStepCount("Mike Kid"));
    }

    @And("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listResortsAfterMikeKidUpdate() {
        Map<String, Long> expectedCounts = new LinkedHashMap<>(initialCounts);
        expectedCounts.put("Mike Kid", expectedCounts.get("Mike Kid") + 1_000);
        List<String> expectedNames = expectedCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey).toList();

        assertEquals(expectedNames, participantNames());
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedText) {
        Alert alert = new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.alertIsPresent()).accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertEquals("block", Hooks.driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    @And("the step count updates correctly with the large number")
    public void largeStepCountUpdatesCorrectly() {
        waitModalClosed();
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(driver ->
                participantStepCount(selectedParticipant) == expectedUpdatedCount);
        assertEquals(expectedUpdatedCount, participantStepCount(selectedParticipant));
    }

    @And("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertEquals(selectedParticipant, participantNames().get(0));
    }

    private void assertTrophyColors() {
        List<WebElement> rows = participantRows();
        List<String> actualColors = rows.stream().map(row -> {
            List<WebElement> icons = row.findElements(By.cssSelector("i"));
            return icons.isEmpty() ? "none" : icons.get(0).getCssValue("color");
        }).toList();
        assertEquals(DEFAULT_NAMES.size(), rows.size());
        assertEquals(DEFAULT_TROPHY_COLORS, actualColors);
    }

    private void assertModalElements() {
        waitModalOpen();
        assertEquals("Add Steps to Participant",
                Hooks.driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertTrue(Hooks.driver.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(Hooks.driver.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(Hooks.driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed());
        assertEquals(11, new Select(Hooks.driver.findElement(By.id("participant_select"))).getOptions().size());
        assertEquals("Choose participant",
                new Select(Hooks.driver.findElement(By.id("participant_select")))
                        .getFirstSelectedOption().getText());
    }

    private void waitModalOpen() {
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void waitModalClosed() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOf(modal));
    }

    private WebElement waitVisible(By locator) {
        return new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private List<WebElement> participantRows() {
        return Hooks.driver.findElements(By.cssSelector("#participantsList li"));
    }

    private List<String> participantNames() {
        return participantRows().stream()
                .map(row -> row.findElement(By.className("participant-name")).getText()).toList();
    }

    private List<String> participantSteps() {
        return participantRows().stream()
                .map(row -> row.findElement(By.className("participant-steps")).getText()).toList();
    }

    private List<Long> numericParticipantSteps() {
        return participantSteps().stream().map(FitnessChallengeSteps::parseStepCount).toList();
    }

    private Map<String, Long> participantCounts() {
        List<String> names = participantNames();
        List<Long> counts = numericParticipantSteps();
        Map<String, Long> result = new LinkedHashMap<>();
        for (int i = 0; i < names.size(); i++) {
            result.put(names.get(i), counts.get(i));
        }
        return result;
    }

    private long participantStepCount(String participant) {
        List<WebElement> matchingRows = participantRows().stream()
                .filter(row -> row.findElement(By.className("participant-name")).getText().equals(participant))
                .toList();
        assertEquals(1, matchingRows.size(), "Expected exactly one row for " + participant);
        return parseStepCount(matchingRows.get(0).findElement(By.className("participant-steps")).getText());
    }

    private static long parseStepCount(String text) {
        return Long.parseLong(text.replace(" steps", "").replace(",", ""));
    }
}
