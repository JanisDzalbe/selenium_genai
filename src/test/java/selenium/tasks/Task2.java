package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtil;

import java.time.Duration;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final String FITNESS_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final Map<String, Long> DEFAULT_PARTICIPANTS = defaultParticipants();

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtil.getDriver();
        wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        driver.get(FITNESS_URL);
        waitForDefaultList();
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
        assertEquals("Fitness Challenge", driver.findElement(By.cssSelector("h2")).getText());
        assertDefaultParticipants();

        List<WebElement> addButtons = driver.findElements(By.id("addStepsBtn"));
        List<WebElement> resetButtons = driver.findElements(By.id("resetBtn"));

        assertEquals(2, addButtons.size());
        assertEquals(2, resetButtons.size());
        assertTrue(addButtons.stream().allMatch(WebElement::isDisplayed));
        assertTrue(resetButtons.stream().allMatch(WebElement::isDisplayed));
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<Long> steps = participantRows().stream()
                .map(this::stepsFromRow)
                .collect(Collectors.toList());

        assertEquals(10, steps.size());
        List<Long> sortedDesc = steps.stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        assertEquals(sortedDesc, steps);
    }

    @Test
    public void medalTrophyIcons() {
        List<WebElement> rows = participantRows();
        assertTrue(rows.size() >= 3, "Expected at least 3 participant rows");

        assertEquals("#ffd700", trophyColor(rows.get(0)));
        assertEquals("#c0c0c0", trophyColor(rows.get(1)));
        assertEquals("#cd7f32", trophyColor(rows.get(2)));

        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Unexpected trophy icon at rank " + (i + 1));
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        clickAddStepsButton(0);
        assertModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        clickAddStepsButton(1);
        assertModalContents();
    }

    @Test
    public void closeModalWithCloseButton() {
        openModal();
        WebElement closeButton = driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        closeButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        assertDefaultParticipants();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long initialSteps = stepsFor("Mike Kid");
        addSteps("Mike Kid", "1000");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        assertEquals(initialSteps + 1_000L, stepsFor("Mike Kid"));
    }

    @Test
    public void addZeroSteps() {
        openModal();
        selectParticipant("Mike Kid");
        enterSteps("0");
        submitSteps();

        assertAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() {
        long initialSteps = stepsFor("Mike Kid");
        addSteps("Mike Kid", "999999");

        assertFalse(driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(initialSteps + 999_999L, stepsFor("Mike Kid"));
        assertEquals("Mike Kid", participantName(participantRows().get(0)));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        openModal();
        enterSteps("1000");
        submitSteps();

        assertAlert("Please select a participant");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openModal();
        selectParticipant("Mike Kid");
        enterSteps("0");
        submitSteps();

        assertAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() {
        openModal();
        selectParticipant("Mike Kid");
        setStepsInput("-500");
        submitSteps();

        assertAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() {
        openModal();
        selectParticipant("Mike Kid");
        enterSteps("abc");

        String value = driver.findElement(By.id("steps_input")).getAttribute("value");
        assertFalse(value.matches(".*[a-zA-Z].*"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        resetList(0);

        assertDefaultParticipants();
        assertDefaultRankingAndTrophies();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        resetList(1);

        assertDefaultParticipants();
        assertDefaultRankingAndTrophies();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        addSteps("Mike Kid", "9007199254740991");

        assertFalse(driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertTrue(stepsFor("Mike Kid") >= 9_007_199_254_740_991L);
    }

    @Test
    public void decimalStepValues() {

        long initialSteps = stepsFor("Mike Kid");
        openModal();
        selectParticipant("Mike Kid");
        setStepsInput("100.5");
        submitSteps();

        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(initialSteps, stepsFor("Mike Kid"));
    }

    private void assertDefaultParticipants() {
        waitForDefaultList();
        assertEquals(DEFAULT_PARTICIPANTS, participantStepsByName());
    }

    private void assertDefaultRankingAndTrophies() {
        List<WebElement> rows = participantRows();
        assertTrue(rows.size() >= 3, "Expected at least 3 participant rows");

        List<Long> expectedSteps = DEFAULT_PARTICIPANTS.values().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        List<Long> actualSteps = rows.stream()
                .map(this::stepsFromRow)
                .collect(Collectors.toList());

        assertEquals(expectedSteps, actualSteps);
        assertEquals("#ffd700", trophyColor(rows.get(0)));
        assertEquals("#c0c0c0", trophyColor(rows.get(1)));
        assertEquals("#cd7f32", trophyColor(rows.get(2)));
    }

    private void assertModalContents() {
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant", modal.findElement(By.cssSelector("h3")).getText());
        assertTrue(modal.findElement(By.id("participant_select")).isDisplayed());
        assertEquals("number", modal.findElement(By.id("steps_input")).getAttribute("type"));
        assertTrue(modal.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(modal.findElement(By.cssSelector(".w3-closebtn")).isDisplayed());

        Select participants = new Select(modal.findElement(By.id("participant_select")));
        assertEquals(11, participants.getOptions().size());
        assertEquals("Choose participant", participants.getFirstSelectedOption().getText());
        assertEquals(10L, participants.getOptions().stream()
                .skip(1)
                .map(WebElement::getText)
                .distinct()
                .count());
    }

    private void addSteps(String participant, String steps) {
        long previousSteps = stepsFor(participant);
        openModal();
        selectParticipant(participant);
        enterSteps(steps);
        submitSteps();
        wait.until(webDriver -> !webDriver.findElement(By.id("addStepsModal")).isDisplayed());
        wait.until(webDriver -> stepsFor(participant) > previousSteps);
    }

    private void openModal() {
        clickAddStepsButton(0);
        wait.until(webDriver -> webDriver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    private void clickAddStepsButton(int index) {
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        assertEquals(2, buttons.size());
        WebElement button = buttons.get(index);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", button);
        button.click();
    }

    private void selectParticipant(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    private void enterSteps(String steps) {
        WebElement input = driver.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(steps);
    }

    private void setStepsInput(String steps) {
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('steps_input').value = arguments[0];", steps);
    }

    private void submitSteps() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void assertAlert(String expectedText) {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertNotNull(alert);
        try {
            assertEquals(expectedText, alert.getText());
        } catch (NoAlertPresentException exception) {
            fail("Expected alert was not present: " + expectedText);
        }
        alert.accept();
    }

    private void resetList(int index) {
        clickResetButton(index);
        waitForDefaultList();
    }

    private void clickResetButton(int index) {
        List<WebElement> buttons = driver.findElements(By.id("resetBtn"));
        assertEquals(2, buttons.size());
        WebElement button = buttons.get(index);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", button);
        button.click();
    }

    private void waitForDefaultList() {
        wait.until(webDriver -> {
            List<WebElement> rows = participantRows();
            return rows.size() == DEFAULT_PARTICIPANTS.size()
                    && DEFAULT_PARTICIPANTS.equals(participantStepsByName());
        });
    }

    private List<WebElement> participantRows() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private Map<String, Long> participantStepsByName() {
        Map<String, Long> participants = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            participants.put(participantName(row), stepsFromRow(row));
        }
        return participants;
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
        String steps = row.findElement(By.cssSelector(".participant-steps")).getText();
        return Long.parseLong(steps.replaceAll("[^0-9]", ""));
    }

    private String trophyColor(WebElement row) {
        return Color.fromString(row.findElement(By.cssSelector("i.fa-trophy"))
                .getCssValue("color")).asHex();
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
    //    // FEATURE 8: CROSS-BROWSER COMPATIBILITY
//
//    @Test
//    public void chromeBrowser() throws Exception {
//        // TODO:
//        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Chrome
//        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
//        //  verify all features work as expected
//    }
//
//    @Test
//    public void edgeBrowser() throws Exception {
//        // TODO:
//        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Edge
//        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
//        //  verify all features work as expected
//    }
//
//    @Test
//    public void firefoxBrowser() throws Exception {
//        // TODO:
//        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Firefox
//        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
//        //  verify all features work as expected
//    }
}