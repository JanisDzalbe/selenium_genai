package selenium.stepDefinitions;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FitnessChallengeSteps {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final Map<String, Long> DEFAULT_PARTICIPANTS = defaultParticipants();

    private WebDriver driver;
    private WebDriverWait wait;

    private String selectedParticipant;
    private long initialStepCount;
    private long expectedStepCount;
    private String expectedAlert;
    @Before(order = 1)
    public void useScenarioBrowser() {
        driver = Hooks.driver;
        Assertions.assertNotNull(driver, "Browser was not initialized by the scenario hook");
        wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallenge(String url) {
        driver.get(url);
        waitForDefaultParticipants();
    }

    @When("the page loads for the first time")
    public void waitForInitialLoad() {
        waitForDefaultParticipants();
    }

    @Then("the page displays title {string}")
    public void verifyPageTitle(String title) {
        Assertions.assertEquals(title, driver.findElement(By.cssSelector("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void verifyParticipantCount(int expectedCount) {
        Assertions.assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void verifyDefaultParticipants(DataTable table) {
        Map<String, String> expected = table.asMaps(String.class, String.class).stream()
                .collect(Collectors.toMap(
                        row -> row.get("Name").trim(),
                        row -> normalizeSteps(row.get("Steps")),
                        (first, second) -> second,
                        LinkedHashMap::new));
        Map<String, Long> expectedCounts = new LinkedHashMap<>();
        expected.forEach((name, steps) -> expectedCounts.put(name, Long.parseLong(steps)));
        Assertions.assertEquals(expectedCounts, participantSteps());
    }

    @Then("{string} buttons are visible at top and bottom")
    public void verifyButtonsAtTopAndBottom(String label) {
        String id = switch (label) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported button: " + label);
        };
        List<WebElement> buttons = driver.findElements(By.id(id));
        Assertions.assertEquals(2, buttons.size());
        Assertions.assertTrue(buttons.stream().allMatch(WebElement::isDisplayed));
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        wait.until(webDriver -> participantRows().size() == DEFAULT_PARTICIPANTS.size());
    }

    @Then("participants are displayed in descending order by step count")
    public void verifyDescendingRanking() {
        List<Long> steps = stepCounts();
        Assertions.assertEquals(steps.stream().sorted(Comparator.reverseOrder()).toList(), steps);
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void verifyAdjacentRanking() {
        List<Long> steps = stepCounts();
        for (int i = 0; i + 1 < steps.size(); i++) {
            Assertions.assertTrue(steps.get(i) >= steps.get(i + 1));
        }
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void verifyGoldTrophy() {
        Assertions.assertEquals("#ffd700", trophyColorAt(0));
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void verifySilverTrophy() {
        Assertions.assertEquals("#c0c0c0", trophyColorAt(1));
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void verifyBronzeTrophy() {
        Assertions.assertEquals("#cd7f32", trophyColorAt(2));
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void verifyNoOtherTrophies() {
        List<WebElement> rows = participantRows();
        for (int i = 3; i < rows.size(); i++) {
            Assertions.assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Unexpected trophy icon at rank " + (i + 1));
        }
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);");
    }

    @When("I click the {string} button at the bottom")
    public void clickBottomButton(String label) {
        clickButton(label, 1);
    }

    @Given("I have opened the {string} modal")
    @When("I open the {string} modal")
    public void openAddStepsModal(String modalName) {
        Assertions.assertEquals("Add Steps", modalName);
        clickButton("Add Steps", 0);
        wait.until(webDriver -> webDriver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Then("a modal window appears with title {string}")
    public void verifyModalTitle(String title) {
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        Assertions.assertTrue(modal.isDisplayed());
        Assertions.assertEquals(title, modal.findElement(By.cssSelector("h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void verifyParticipantDropdown() {
        Assertions.assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void verifyStepsInput() {
        WebElement input = driver.findElement(By.id("steps_input"));
        Assertions.assertTrue(input.isDisplayed());
        Assertions.assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void verifySubmitButton(String label) {
        WebElement button = driver.findElement(By.id("modal_add_steps_button"));
        Assertions.assertTrue(button.isDisplayed());
        Assertions.assertEquals(label, button.getText());
    }



    @Then("the dropdown is prepopulated with all 10 participants")
    public void verifyParticipantOptions() {
        Select select = new Select(driver.findElement(By.id("participant_select")));
        Assertions.assertEquals(11, select.getOptions().size());
        Assertions.assertEquals(10, select.getOptions().stream()
                .skip(1)
                .map(WebElement::getText)
                .distinct()
                .count());
    }

    @Then("the default dropdown text shows {string}")
    public void verifyDefaultDropdown(String expectedText) {
        Assertions.assertEquals(expectedText, new Select(
                driver.findElement(By.id("participant_select")))
                .getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void verifyAllModalElements() {
        verifyModalTitle("Add Steps to Participant");
        verifyParticipantDropdown();
        verifyStepsInput();
        verifySubmitButton("Add Steps");
        verifyModalCloseButton();
        verifyParticipantOptions();
        verifyDefaultDropdown("Choose participant");
    }



    @Then("the modal closes")
    public void verifyModalClosed() {
        waitForModalToClose();
    }

    @Then("the modal closes automatically")
    public void verifyModalClosesAutomatically() {
        waitForModalToClose();
    }

    private void waitForModalToClose() {
        wait.until(webDriver -> !webDriver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Then("I return to the main page view")
    public void verifyMainView() {
        Assertions.assertTrue(driver.findElement(By.cssSelector("h2")).isDisplayed());
        Assertions.assertTrue(driver.getCurrentUrl().contains("fitness_challenge"));
    }

    @Then("no data is changed from initial state")
    public void verifyNoDataChanged() {
        Assertions.assertEquals(DEFAULT_PARTICIPANTS, participantSteps());
    }

    @Given("I note the current step count for {string}")
    public void noteParticipantSteps(String name) {
        selectedParticipant = name;
        initialStepCount = stepsFor(name);
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String name) {
        selectParticipantByName(name);
    }

    @When("I click the {string} button at the top of the page")
    public void clickTopButton(String label) {
        clickButton(label, 0);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select select = new Select(driver.findElement(By.id("participant_select")));
        select.selectByIndex(1);
        selectedParticipant = select.getFirstSelectedOption().getText();
        initialStepCount = stepsFor(selectedParticipant);
    }

    @When("I select a participant")
    public void selectAParticipant() {
        selectAnyParticipant();
    }

    @When("I enter {string} in the steps input field")
    public void enterStepsField(String value) {
        setStepsInput(value);
    }

    @When("I enter {string} in the steps input")
    public void enterSteps(String value) {
        setStepsInput(value);
    }

    @When("I enter the maximum safe integer {string}")
    public void enterMaximumSafeInteger(String value) {
        selectedParticipant = new Select(driver.findElement(By.id("participant_select")))
                .getFirstSelectedOption().getText();
        initialStepCount = stepsFor(selectedParticipant);
        setStepsInput(value);
        expectedStepCount = (long) (Double.parseDouble(value) + initialStepCount);
    }

    @When("I leave the participant dropdown at {string}")
    public void leaveParticipantUnselected(String placeholder) {
        Select select = new Select(driver.findElement(By.id("participant_select")));
        Assertions.assertEquals(placeholder, select.getFirstSelectedOption().getText());
        selectedParticipant = null;
    }

    @When("I click {string} button")
    public void clickAddStepsSubmit(String label) {
        Assertions.assertEquals("Add Steps", label);
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    @When("I attempt to click {string} button")
    public void attemptToClickAddSteps(String label) {
        clickAddStepsSubmit(label);
    }

    @When("I submit the form")
    public void submitStepsForm() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    @Then("an alert appears: {string}")
    public void verifyAlertAppears(String message) {
        expectedAlert = message;
        wait.until(ExpectedConditions.alertIsPresent());
        Assertions.assertEquals(message, driver.switchTo().alert().getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        Assertions.assertNotNull(expectedAlert, "No expected alert was recorded");
        driver.switchTo().alert().accept();
        expectedAlert = null;
    }

    @Then("the modal remains open")
    public void verifyModalRemainsOpen() {
        Assertions.assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Then("the participant list refreshes")
    public void waitForParticipantListRefresh() {
        wait.until(webDriver -> participantRows().size() == DEFAULT_PARTICIPANTS.size());
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void verifyMikeStepsIncrease() {
        Assertions.assertEquals(initialStepCount + 1_000L, stepsFor("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void verifyListResorted() {
        List<Long> steps = stepCounts();
        Assertions.assertEquals(steps.stream().sorted(Comparator.reverseOrder()).toList(), steps);
        List<WebElement> rows = participantRows();
        long mikeSteps = stepsFor("Mike Kid");
        long participantsAheadOfMike = rows.stream()
                .filter(row -> !"Mike Kid".equals(participantName(row)))
                .filter(row -> stepsFromRow(row) > mikeSteps)
                .count();
        int mikeRank = java.util.stream.IntStream.range(0, rows.size())
                .filter(index -> "Mike Kid".equals(participantName(rows.get(index))))
                .findFirst()
                .orElseThrow();
        Assertions.assertEquals(participantsAheadOfMike, mikeRank);
    }

    @Then("the step count updates correctly with the large number")
    public void verifyLargeStepCount() {
        Assertions.assertEquals(initialStepCount + 999_999L, stepsFor(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void verifySelectedParticipantFirst() {
        Assertions.assertEquals(selectedParticipant, participantName(participantRows().get(0)));
    }

    @When("I attempt to enter {string} in the steps input field")
    public void attemptNonNumericSteps(String value) {
        setStepsInput(value);
    }

    @Then("the input field rejects non-numeric characters")
    public void verifyInputRejectsNonNumeric() {
        String value = driver.findElement(By.id("steps_input")).getAttribute("value");
        Assertions.assertFalse(value.matches(".*[a-zA-Z].*"));
    }

    @Then("no alphabetic characters appear in the input")
    public void verifyNoLettersInInput() {
        verifyInputRejectsNonNumeric();
    }

    @Given("I have added steps to at least 2 participants to modify the default state")
    public void modifyAtLeastTwoParticipants() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
    }

    @Given("I have modified participant data")
    public void modifyParticipantData() {
        addSteps("Mike Kid", "1000");
    }

    @When("I scroll to bottom of page")
    public void scrollToBottomWithoutArticle() {
        scrollToBottom();
    }

    @When("I click the {string} button at the bottom of the page")
    public void clickResetBottom(String label) {
        clickButton(label, 1);
    }

    @When("I wait for page reload")
    public void waitForResetPageLoad() {
        waitForDefaultParticipants();
    }

    @Then("all participants return to their default step counts")
    public void verifyDefaultCounts() {
        Assertions.assertEquals(DEFAULT_PARTICIPANTS, participantSteps());
    }

    @Then("the default ranking order is restored")
    public void verifyDefaultRanking() {
        List<Long> expected = DEFAULT_PARTICIPANTS.values().stream()
                .sorted(Comparator.reverseOrder())
                .toList();
        Assertions.assertEquals(expected, stepCounts());
    }

    @Then("trophy icons display for correct default top 3")
    public void verifyDefaultTrophies() {
        Assertions.assertEquals("#ffd700", trophyColorAt(0));
        Assertions.assertEquals("#c0c0c0", trophyColorAt(1));
        Assertions.assertEquals("#cd7f32", trophyColorAt(2));
    }

    @Then("the system accepts the value")
    public void verifyMaximumAccepted() {
        wait.until(webDriver -> !webDriver.findElement(By.id("addStepsModal")).isDisplayed());
        Assertions.assertEquals(selectedParticipant, participantName(participantRows().get(0)));
    }

    @Then("the step count updates correctly")
    public void verifyMaximumStepCount() {
        Assertions.assertEquals(expectedStepCount, stepsFor(selectedParticipant));
    }

    private void addSteps(String participant, String steps) {
        clickButton("Add Steps", 0);
        wait.until(webDriver -> webDriver.findElement(By.id("addStepsModal")).isDisplayed());
        selectParticipantByName(participant);
        long baseline = stepsFor(participant);
        setStepsInput(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
        wait.until(webDriver -> !webDriver.findElement(By.id("addStepsModal")).isDisplayed());
        wait.until(webDriver -> stepsFor(participant) > baseline);
    }

    private void selectParticipantByName(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
        selectedParticipant = name;
        initialStepCount = stepsFor(name);
    }

    private void setStepsInput(String value) {
        WebElement input = driver.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(value);
    }

    private void clickButton(String label, int index) {
        String id = switch (label) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported button: " + label);
        };

        List<WebElement> buttons = driver.findElements(By.id(id));
        Assertions.assertEquals(2, buttons.size());
        WebElement button = buttons.get(index);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", button);
        button.click();
    }

    private void waitForDefaultParticipants() {
        wait.until(webDriver -> DEFAULT_PARTICIPANTS.equals(participantSteps()));
    }

    private List<WebElement> participantRows() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private Map<String, Long> participantSteps() {
        Map<String, Long> participants = new HashMap<>();
        for (WebElement row : participantRows()) {
            participants.put(participantName(row), stepsFromRow(row));
        }
        return participants;
    }

    private List<Long> stepCounts() {
        return participantRows().stream().map(this::stepsFromRow).toList();
    }

    private long stepsFor(String name) {
        return participantRows().stream()
                .filter(row -> name.equals(participantName(row)))
                .findFirst()
                .map(this::stepsFromRow)
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText();
    }

    private long stepsFromRow(WebElement row) {
        String text = row.findElement(By.cssSelector(".participant-steps")).getText();
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    private String trophyColorAt(int index) {
        WebElement icon = participantRows().get(index).findElement(By.cssSelector("i.fa-trophy"));
        return Color.fromString(icon.getCssValue("color")).asHex();
    }

    private static String normalizeSteps(String text) {
        return text.replace(",", "").trim();
    }

    private static Map<String, Long> defaultParticipants() {
        Map<String, Long> participants = new LinkedHashMap<>();
        participants.put("Mike Kid", 8_500L);
        participants.put("Jill Watson", 12_000L);
        participants.put("Jane Doe", 6_500L);
        participants.put("John Smith", 15_000L);
        participants.put("Sarah Johnson", 9_800L);
        participants.put("Carlos Garcia", 11_200L);
        participants.put("Emily Chen", 7_300L);
        participants.put("David Brown", 13_500L);
        participants.put("Maria Rodriguez", 10_500L);
        participants.put("Alex Taylor", 8_900L);
        return participants;
    }

    @When("I click the × \\(close) button in the top-right corner")
    public void closeAddStepsModal() {
        driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
    }

    @And("the modal has a close button \\(×) in the top-right corner")
    public void verifyModalCloseButton() {
        WebElement closeButton = driver.findElement(
                By.cssSelector("#addStepsModal .w3-closebtn"));
        Assertions.assertTrue(closeButton.isDisplayed());
        Assertions.assertEquals("absolute", closeButton.getCssValue("position"));
    }
}
