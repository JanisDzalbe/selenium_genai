package selenium.tasks;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverUtility;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final String FITNESS_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final By PARTICIPANTS = By.cssSelector("#participantsList li");
    private static final Map<String, Long> DEFAULT_STEPS = Map.of(
            "Mike Kid", 8_500L,
            "Jill Watson", 12_000L,
            "Jane Doe", 6_500L,
            "John Smith", 15_000L,
            "Sarah Johnson", 9_800L,
            "Carlos Garcia", 11_200L,
            "Emily Chen", 7_300L,
            "David Brown", 13_500L,
            "Maria Rodriguez", 10_500L,
            "Alex Taylor", 8_900L);

    private WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverUtility.createDriver();
        driver.get(FITNESS_URL);
        waitFor(By.id("participantsList"));
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge()");
        waitForParticipants();
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
        assertEquals("Fitness Challenge", driver.findElement(By.tagName("h2")).getText());
        assertEquals(10, participantRows().size());
        assertEquals(DEFAULT_STEPS, participantStepsByName());
        assertEquals(2, visibleButtons("addStepsBtn"));
        assertEquals(2, visibleButtons("resetBtn"));
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<Long> counts = participantRows().stream()
                .map(this::stepsFromRow)
                .toList();

        assertEquals(10, counts.size());
        for (int i = 1; i < counts.size(); i++) {
            assertTrue(counts.get(i - 1) >= counts.get(i),
                    "Step counts are not descending at positions " + (i - 1) + " and " + i);
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());

        assertTrophyColor(rows.get(0), "rgba(255, 215, 0, 1)");
        assertTrophyColor(rows.get(1), "rgba(192, 192, 192, 1)");
        assertTrophyColor(rows.get(2), "rgba(205, 127, 50, 1)");
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty(),
                    "Unexpected trophy icon at rank " + (i + 1));
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        assertModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", buttons.get(1));
        buttons.get(1).click();
        assertModalContents();
    }

    @Test
    public void closeModalWithCloseButton() {
        openAddStepsModal();
        driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();

        waitUntil(ExpectedConditions.not(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))));
        assertEquals(DEFAULT_STEPS, participantStepsByName());
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long previousSteps = stepsFor("Mike Kid");
        addSteps("Mike Kid", "1000");

        assertFalse(isModalVisible());
        assertEquals(previousSteps + 1_000, stepsFor("Mike Kid"));
        assertSortedDescending();
    }

    @Test
    public void addZeroSteps() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("0");
        submitAndAcceptAlert("Please enter a valid number of steps");

        assertTrue(isModalVisible());
    }

    @Test
    public void addLargeNumberOfSteps() {
        openAddStepsModal();
        selectParticipant("Jane Doe");
        enterSteps("999999");
        clickSubmitSteps();

        assertFalse(isModalVisible());
        assertEquals(1_006_499L, stepsFor("Jane Doe"));
        assertEquals("Jane Doe", participantName(participantRows().get(0)));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        openAddStepsModal();
        enterSteps("1000");
        submitAndAcceptAlert("Please select a participant");

        assertTrue(isModalVisible());
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("0");
        submitAndAcceptAlert("Please enter a valid number of steps");

        assertTrue(isModalVisible());
    }

    @Test
    public void submitWithNegativeSteps() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("-500");
        submitAndAcceptAlert("Please enter a valid number of steps");

        assertTrue(isModalVisible());
    }

    @Test
    public void submitWithNonNumericInput() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");

        assertFalse(input.getAttribute("value").matches(".*[a-zA-Z].*"),
                "The numeric steps field accepted alphabetic characters");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        WebElement oldFirstRow = participantRows().get(0);
        driver.findElements(By.id("resetBtn")).get(0).click();
        waitForPageReload(oldFirstRow);

        assertDefaultStateRestored();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        List<WebElement> buttons = driver.findElements(By.id("resetBtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", buttons.get(1));
        WebElement oldFirstRow = participantRows().get(0);
        buttons.get(1).click();
        waitForPageReload(oldFirstRow);

        assertDefaultStateRestored();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        long originalSteps = stepsFor("Jane Doe");
        openAddStepsModal();
        selectParticipant("Jane Doe");
        enterSteps("9007199254740991");
        clickSubmitSteps();

        assertFalse(isModalVisible());
        String expectedDisplay = (String) ((JavascriptExecutor) driver).executeScript(
                "return (Number(arguments[0]) + Number(arguments[1])).toLocaleString()",
                originalSteps, 9_007_199_254_740_991L);
        assertEquals(expectedDisplay + " steps", displayedSteps("Jane Doe"));
    }

    @Test
    public void decimalStepValues() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("100.5");

        WebElement input = driver.findElement(By.id("steps_input"));
        assertEquals("100.5", input.getAttribute("value"));
        assertTrue((Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validity.stepMismatch", input));
        assertTrue(isModalVisible());
    }

    private void assertModalContents() {
        WebElement modal = waitFor(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant", modal.findElement(By.tagName("h3")).getText());
        assertTrue(modal.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(modal.findElement(By.id("steps_input")).isDisplayed());
        assertEquals("number", modal.findElement(By.id("steps_input")).getAttribute("type"));
        assertTrue(modal.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(modal.findElement(By.cssSelector(".w3-closebtn")).isDisplayed());

        Select participants = new Select(modal.findElement(By.id("participant_select")));
        assertEquals(11, participants.getOptions().size());
        assertEquals("Choose participant", participants.getFirstSelectedOption().getText());
    }

    private void assertDefaultStateRestored() {
        assertEquals(DEFAULT_STEPS, participantStepsByName());
        assertSortedDescending();
        medalTrophyIcons();
    }

    private void assertSortedDescending() {
        List<Long> counts = participantRows().stream()
                .map(this::stepsFromRow)
                .toList();
        for (int i = 1; i < counts.size(); i++) {
            assertTrue(counts.get(i - 1) >= counts.get(i));
        }
    }

    private void assertTrophyColor(WebElement row, String expectedColor) {
        WebElement trophy = row.findElement(By.cssSelector(".fa-trophy"));
        assertEquals(expectedColor, trophy.getCssValue("color"));
    }

    private void openAddStepsModal() {
        if (!isModalVisible()) {
            driver.findElements(By.id("addStepsBtn")).get(0).click();
        }
        waitFor(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void selectParticipant(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    private void enterSteps(String steps) {
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys(steps);
    }

    private void clickSubmitSteps() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void submitAndAcceptAlert(String expectedMessage) {
        clickSubmitSteps();
        Alert alert = waitFor(ExpectedConditions.alertIsPresent());
        assertEquals(expectedMessage, alert.getText());
        alert.accept();
        waitFor(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void addSteps(String participant, String steps) {
        openAddStepsModal();
        selectParticipant(participant);
        enterSteps(steps);
        clickSubmitSteps();
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    private boolean isModalVisible() {
        return driver.findElement(By.id("addStepsModal")).isDisplayed();
    }

    private List<WebElement> participantRows() {
        waitForParticipants();
        return driver.findElements(PARTICIPANTS);
    }

    private void waitForParticipants() {
        new WebDriverWait(driver, WAIT_TIMEOUT).until(
                webDriver -> webDriver.findElements(PARTICIPANTS).size() == 10);
    }

    private Map<String, Long> participantStepsByName() {
        return participantRows().stream().collect(Collectors.toMap(
                this::participantName,
                this::stepsFromRow));
    }

    private long stepsFor(String name) {
        return participantRows().stream()
                .filter(row -> participantName(row).equals(name))
                .findFirst()
                .map(this::stepsFromRow)
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private String displayedSteps(String name) {
        return participantRows().stream()
                .filter(row -> participantName(row).equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name))
                .findElement(By.cssSelector(".participant-steps"))
                .getText();
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText();
    }

    private long stepsFromRow(WebElement row) {
        String steps = row.findElement(By.cssSelector(".participant-steps")).getText()
                .replaceAll("[^0-9]", "");
        return Long.parseLong(steps);
    }

    private int visibleButtons(String id) {
        return (int) driver.findElements(By.id(id)).stream()
                .filter(WebElement::isDisplayed)
                .count();
    }

    private void waitForPageReload(WebElement previousFirstRow) {
        waitUntil(ExpectedConditions.stalenessOf(previousFirstRow));
        waitForParticipants();
    }

    private WebElement waitFor(By locator) {
        return waitFor(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private <T> T waitFor(org.openqa.selenium.support.ui.ExpectedCondition<T> condition) {
        return new WebDriverWait(driver, WAIT_TIMEOUT).until(condition);
    }

    private void waitUntil(org.openqa.selenium.support.ui.ExpectedCondition<?> condition) {
        new WebDriverWait(driver, WAIT_TIMEOUT).until(condition);
    }
}
