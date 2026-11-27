package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverFactory;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final String FITNESS_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final String MODAL_ID = "addStepsModal";
    private static final String PARTICIPANT_SELECT_ID = "participant_select";
    private static final String STEPS_INPUT_ID = "steps_input";
    private static final long MAX_SAFE_INTEGER = 9_007_199_254_740_991L;
    private static final Map<String, Long> DEFAULT_STEPS = createDefaultSteps();

    private WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverFactory.createChromeDriver();
        driver.get(FITNESS_URL);
        waitUntilParticipantsAreLoaded();
        resetParticipantData();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        assertDefaultState();
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());

        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(stepsInRow(rows.get(i)) >= stepsInRow(rows.get(i + 1)),
                    "Each participant must have at least as many steps as the next participant");
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());

        assertTrophyColor(rows.get(0), "rgb(255, 215, 0)");
        assertTrophyColor(rows.get(1), "rgb(192, 192, 192)");
        assertTrophyColor(rows.get(2), "rgb(205, 127, 50)");
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Participants ranked fourth and below should not have trophies");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        openAddStepsModal(0);
        assertModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        assertEquals(2, buttons.size());
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", buttons.get(1));
        buttons.get(1).click();

        waitForModalToOpen();
        assertModalContents();
    }

    @Test
    public void closeModalWithCloseButton() {
        openAddStepsModal(0);
        WebElement closeButton = driver.findElement(By.cssSelector("#" + MODAL_ID + " .w3-closebtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", closeButton);

        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id(MODAL_ID)));
        assertTrue(driver.findElement(By.id("participantsList")).isDisplayed());
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long initialSteps = stepsForParticipant("Mike Kid");
        addSteps("Mike Kid", "1000");

        assertEquals(initialSteps + 1_000, stepsForParticipant("Mike Kid"));
        assertRankingOrder();
    }

    @Test
    public void addZeroSteps() {
        openAddStepsModal(0);
        selectParticipant(0);
        enterSteps("0");
        clickModalSubmitButton();

        assertAlertAndModalRemainsOpen("Please enter a valid number of steps");
    }

    @Test
    public void addLargeNumberOfSteps() {
        addSteps("Mike Kid", "999999");

        assertEquals(1_008_499L, stepsForParticipant("Mike Kid"));
        assertEquals("Mike Kid", participantName(participantRows().get(0)));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        openAddStepsModal(0);
        enterSteps("1000");
        clickModalSubmitButton();

        assertAlertAndModalRemainsOpen("Please select a participant");
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openAddStepsModal(0);
        selectParticipant(0);
        enterSteps("0");
        clickModalSubmitButton();

        assertAlertAndModalRemainsOpen("Please enter a valid number of steps");
    }

    @Test
    public void submitWithNegativeSteps() {
        openAddStepsModal(0);
        selectParticipant(0);
        enterSteps("-500");
        clickModalSubmitButton();

        assertAlertAndModalRemainsOpen("Please enter a valid number of steps");
    }

    @Test
    public void submitWithNonNumericInput() {
        openAddStepsModal(0);
        selectParticipant(0);
        enterSteps("abc");

        String inputValue = driver.findElement(By.id(STEPS_INPUT_ID)).getAttribute("value");
        assertFalse(inputValue.matches(".*[A-Za-z].*"),
                "The number input should reject alphabetic characters");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jill Watson", "2000");
        clickResetButton(0);

        assertDefaultState();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        List<WebElement> buttons = driver.findElements(By.id("resetBtn"));
        assertEquals(2, buttons.size());
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", buttons.get(1));
        clickAndWaitForReload(buttons.get(1));

        assertDefaultState();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        long initialSteps = stepsForParticipant("Mike Kid");
        openAddStepsModal(0);
        selectParticipantByName("Mike Kid");
        enterSteps(Long.toString(MAX_SAFE_INTEGER));

        String expectedDisplayedSteps = (String) ((JavascriptExecutor) driver).executeScript(
                "return (Number(arguments[0]) + Number(arguments[1])).toLocaleString()",
                initialSteps, MAX_SAFE_INTEGER);
        clickModalSubmitButton();

        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id(MODAL_ID)));
        assertEquals(expectedDisplayedSteps + " steps",
                participantStepsElement("Mike Kid").getText());
        assertEquals("Mike Kid", participantName(participantRows().get(0)));
    }

    @Test
    public void decimalStepValues() {
        openAddStepsModal(0);
        selectParticipant(0);
        enterSteps("100.5");
        clickModalSubmitButton();

        assertAlertAndModalRemainsOpen("Please enter a valid number of steps");
    }

    private static Map<String, Long> createDefaultSteps() {
        Map<String, Long> steps = new LinkedHashMap<>();
        steps.put("Mike Kid", 8_500L);
        steps.put("Jill Watson", 12_000L);
        steps.put("Jane Doe", 6_500L);
        steps.put("John Smith", 15_000L);
        steps.put("Sarah Johnson", 9_800L);
        steps.put("Carlos Garcia", 11_200L);
        steps.put("Emily Chen", 7_300L);
        steps.put("David Brown", 13_500L);
        steps.put("Maria Rodriguez", 10_500L);
        steps.put("Alex Taylor", 8_900L);
        return steps;
    }

    private void assertDefaultState() {
        assertEquals("Fitness Challenge", driver.findElement(By.cssSelector("h2")).getText());
        assertEquals(10, participantRows().size());

        for (Map.Entry<String, Long> participant : DEFAULT_STEPS.entrySet()) {
            assertEquals(participant.getValue(), stepsForParticipant(participant.getKey()),
                    participant.getKey() + " should have the default step count");
        }

        assertEquals(List.of(
                        "John Smith", "David Brown", "Jill Watson", "Carlos Garcia", "Maria Rodriguez",
                        "Sarah Johnson", "Alex Taylor", "Mike Kid", "Emily Chen", "Jane Doe"),
                participantRows().stream().map(this::participantName).toList());
        assertButtonsVisible("addStepsBtn");
        assertButtonsVisible("resetBtn");
        assertCorrectTrophies();
    }

    private void assertButtonsVisible(String buttonId) {
        List<WebElement> buttons = driver.findElements(By.id(buttonId));
        assertEquals(2, buttons.size());
        assertTrue(buttons.stream().allMatch(WebElement::isDisplayed),
                buttonId + " buttons should be visible at the top and bottom");
    }

    private void assertRankingOrder() {
        List<WebElement> rows = participantRows();
        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(stepsInRow(rows.get(i)) >= stepsInRow(rows.get(i + 1)),
                    "Participants should be re-sorted by descending step count");
        }
    }

    private void assertCorrectTrophies() {
        List<WebElement> rows = participantRows();
        assertTrophyColor(rows.get(0), "rgb(255, 215, 0)");
        assertTrophyColor(rows.get(1), "rgb(192, 192, 192)");
        assertTrophyColor(rows.get(2), "rgb(205, 127, 50)");
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    private void assertTrophyColor(WebElement participantRow, String expectedColor) {
        WebElement trophy = participantRow.findElement(By.cssSelector("i.fa-trophy"));
        assertEquals(expectedColor, Color.fromString(trophy.getCssValue("color")).asRgb());
    }

    private void openAddStepsModal(int buttonIndex) {
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        assertTrue(buttonIndex >= 0 && buttonIndex < buttons.size(),
                "The requested Add Steps button should exist");
        buttons.get(buttonIndex).click();
        waitForModalToOpen();
    }

    private void waitForModalToOpen() {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id(MODAL_ID)));
    }

    private void assertModalContents() {
        WebElement modal = driver.findElement(By.id(MODAL_ID));
        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant", modal.findElement(By.cssSelector("h3")).getText());

        WebElement participantSelect = modal.findElement(By.id(PARTICIPANT_SELECT_ID));
        assertEquals("select", participantSelect.getTagName());
        assertEquals("number", modal.findElement(By.id(STEPS_INPUT_ID)).getAttribute("type"));
        assertEquals("Add Steps", modal.findElement(By.id("modal_add_steps_button")).getText());

        WebElement closeButton = modal.findElement(By.cssSelector(".w3-closebtn"));
        assertEquals("×", closeButton.getText());
        assertEquals("Close Modal", closeButton.getAttribute("title"));

        List<WebElement> options = new Select(participantSelect).getOptions();
        assertEquals(11, options.size());
        assertEquals("Choose participant", options.get(0).getText());
        assertEquals("", options.get(0).getAttribute("value"));
        for (int i = 1; i < options.size(); i++) {
            assertTrue(DEFAULT_STEPS.containsKey(options.get(i).getText()),
                    "Dropdown should contain only the default participants");
        }
    }

    private void addSteps(String participantName, String steps) {
        openAddStepsModal(0);
        selectParticipantByName(participantName);
        enterSteps(steps);
        clickModalSubmitButton();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id(MODAL_ID)));
    }

    private void selectParticipant(int optionIndex) {
        new Select(driver.findElement(By.id(PARTICIPANT_SELECT_ID))).selectByIndex(optionIndex + 1);
    }

    private void selectParticipantByName(String participantName) {
        new Select(driver.findElement(By.id(PARTICIPANT_SELECT_ID))).selectByVisibleText(participantName);
    }

    private void enterSteps(String steps) {
        WebElement input = driver.findElement(By.id(STEPS_INPUT_ID));
        input.clear();
        input.sendKeys(steps);
    }

    private void clickModalSubmitButton() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void assertAlertAndModalRemainsOpen(String expectedText) {
        Alert alert = new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
        alert.accept();
        assertTrue(driver.findElement(By.id(MODAL_ID)).isDisplayed());
    }

    private void clickResetButton(int buttonIndex) {
        List<WebElement> buttons = driver.findElements(By.id("resetBtn"));
        assertTrue(buttonIndex >= 0 && buttonIndex < buttons.size(),
                "The requested Reset List button should exist");
        clickAndWaitForReload(buttons.get(buttonIndex));
    }

    private void clickAndWaitForReload(WebElement button) {
        WebElement oldList = driver.findElement(By.id("participantsList"));
        button.click();
        new WebDriverWait(driver, WAIT_TIMEOUT).until(ExpectedConditions.stalenessOf(oldList));
        waitUntilParticipantsAreLoaded();
    }

    private void waitUntilParticipantsAreLoaded() {
        new WebDriverWait(driver, WAIT_TIMEOUT).until((Function<WebDriver, Boolean>) webDriver ->
                webDriver.findElements(By.cssSelector("#participantsList li")).size() == 10);
    }

    private void resetParticipantData() {
        WebElement oldList = driver.findElement(By.id("participantsList"));
        ((JavascriptExecutor) driver).executeScript(
                "for (var i = localStorage.length - 1; i >= 0; i--) {" +
                        "var key = localStorage.key(i);" +
                        "if (key && (key.indexOf('participant') === 0 || key === 'fitnessReload')) {" +
                        "localStorage.removeItem(key);" +
                        "}" +
                        "}" +
                        "resetFitnessChallenge();");
        new WebDriverWait(driver, WAIT_TIMEOUT).until(ExpectedConditions.stalenessOf(oldList));
        waitUntilParticipantsAreLoaded();
    }

    private List<WebElement> participantRows() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private long stepsForParticipant(String participantName) {
        return stepsInRow(participantRowByName(participantName));
    }

    private WebElement participantStepsElement(String participantName) {
        return participantRowByName(participantName).findElement(By.cssSelector(".participant-steps"));
    }

    private WebElement participantRowByName(String participantName) {
        for (WebElement row : participantRows()) {
            if (participantName.equals(participantName(row))) {
                return row;
            }
        }
        throw new NoSuchElementException("Participant not found: " + participantName);
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText();
    }

    private long stepsInRow(WebElement row) {
        String digits = row.findElement(By.cssSelector(".participant-steps"))
                .getText().replaceAll("[^0-9]", "");
        return Long.parseLong(digits);
    }
    // decimalStepValues fails, entering 100.5 in Add Steps is accepted and truncated to 100. Expected the alert "Please enter a valid number of steps".
    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        // TODO:
        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Chrome
        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
        //  verify all features work as expected
    }

    @Test
    public void edgeBrowser() throws Exception {
        // TODO:
        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Edge
        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
        //  verify all features work as expected
    }

    @Test
    public void firefoxBrowser() throws Exception {
        // TODO:
        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Firefox
        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
        //  verify all features work as expected
    }
}

