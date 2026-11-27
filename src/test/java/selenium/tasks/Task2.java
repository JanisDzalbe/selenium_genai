package selenium.tasks;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtils;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final String FITNESS_PAGE =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_SECONDS = Duration.ofSeconds(10);
    private static final By PARTICIPANT_ITEMS = By.cssSelector("#participantsList li");
    private static final Map<String, Long> DEFAULT_PARTICIPANTS = Map.ofEntries(
            Map.entry("John Smith", 15000L),
            Map.entry("David Brown", 13500L),
            Map.entry("Jill Watson", 12000L),
            Map.entry("Carlos Garcia", 11200L),
            Map.entry("Maria Rodriguez", 10500L),
            Map.entry("Sarah Johnson", 9800L),
            Map.entry("Mike Kid", 8500L),
            Map.entry("Alex Taylor", 8900L),
            Map.entry("Emily Chen", 7300L),
            Map.entry("Jane Doe", 6500L));

    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtils.createChromeDriver();
        resetToDefaults();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge",
                driver.findElement(By.cssSelector("h2")).getText());
        assertEquals(10, participantItems().size());

        assertAll(
                () -> assertEquals("Mike Kid", getParticipantName("Mike Kid")),
                () -> assertEquals("Jill Watson", getParticipantName("Jill Watson")),
                () -> assertEquals("Jane Doe", getParticipantName("Jane Doe")),
                () -> assertEquals("John Smith", getParticipantName("John Smith")),
                () -> assertEquals("Sarah Johnson", getParticipantName("Sarah Johnson")),
                () -> assertEquals("Carlos Garcia", getParticipantName("Carlos Garcia")),
                () -> assertEquals("Emily Chen", getParticipantName("Emily Chen")),
                () -> assertEquals("David Brown", getParticipantName("David Brown")),
                () -> assertEquals("Maria Rodriguez", getParticipantName("Maria Rodriguez")),
                () -> assertEquals("Alex Taylor", getParticipantName("Alex Taylor")));
        assertDefaultSteps();
        assertEquals(2, driver.findElements(By.xpath(
                "//button[@id='addStepsBtn' and normalize-space()='Add Steps']")).size());
        assertEquals(2, driver.findElements(By.xpath(
                "//button[@id='resetBtn' and normalize-space()='Reset List']")).size());
        assertTrue(driver.findElements(By.xpath(
                "//button[@id='addStepsBtn' and normalize-space()='Add Steps']")).stream()
                .allMatch(WebElement::isDisplayed));
        assertTrue(driver.findElements(By.xpath(
                "//button[@id='resetBtn' and normalize-space()='Reset List']")).stream()
                .allMatch(WebElement::isDisplayed));
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        List<WebElement> items = participantItems();
        assertEquals(10, items.size());
        long previousSteps = Long.MAX_VALUE;
        for (WebElement item : items) {
            long steps = parseSteps(item.findElement(By.cssSelector(".participant-steps")).getText());
            assertTrue(steps <= previousSteps);
            previousSteps = steps;
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> items = participantItems();
        assertEquals(1, items.get(0).findElements(By.cssSelector("i.fa-trophy")).size());
        assertEquals("rgba(255, 215, 0, 1)",
                items.get(0).findElement(By.cssSelector("i.fa-trophy"))
                        .getCssValue("color"));
        assertEquals("rgba(192, 192, 192, 1)",
                items.get(1).findElement(By.cssSelector("i.fa-trophy"))
                        .getCssValue("color"));
        assertEquals("rgba(205, 127, 50, 1)",
                items.get(2).findElement(By.cssSelector("i.fa-trophy"))
                        .getCssValue("color"));
        for (int i = 3; i < items.size(); i++) {
            assertTrue(items.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        topButton("addStepsBtn").click();
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        scrollToBottomButton("addStepsBtn");
        bottomButton("addStepsBtn").click();
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModal();
        closeModal();
        assertModalClosed();
        assertDefaultSteps();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        long before = getParticipantSteps("Mike Kid");
        addSteps("Mike Kid", "1000");
        assertModalClosed();
        assertEquals(before + 1000, getParticipantSteps("Mike Kid"));
        assertRankingDescending();
    }

    @Test
    public void addZeroSteps() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        submitModal();
        assertAndAcceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        addSteps("Mike Kid", "999999");
        assertModalClosed();
        assertEquals(1008499, getParticipantSteps("Mike Kid"));
        assertEquals("Mike Kid",
                participantItems().get(0).findElement(By.cssSelector(".participant-name")).getText());
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModal();
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        submitModal();
        assertAndAcceptAlert("Please select a participant");
        assertModalOpen();
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        submitModal();
        assertAndAcceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("-500");
        submitModal();
        assertAndAcceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertEquals("number", input.getAttribute("type"));
        assertFalse(input.getAttribute("value").matches(".*[A-Za-z]+.*"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        topButton("resetBtn").click();
        waitForReset();
        assertDefaultSteps();
        assertRankingDescending();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        addSteps("Mike Kid", "1000");
        scrollToBottomButton("resetBtn");
        bottomButton("resetBtn").click();
        waitForReset();
        assertDefaultSteps();
        assertRankingDescending();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("9007199254740991");
        submitModal();
        assertModalClosed();
        assertTrue(getParticipantSteps("Mike Kid") > 0);
    }

    @Test
    public void decimalStepValues() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("100.5");
        assertEquals("100.5", input.getAttribute("value"));
        assertTrue((Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validity.stepMismatch;", input));
        assertModalOpen();
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        verifyCrossBrowserFeatures();
    }

    @Test
    public void edgeBrowser() throws Exception {
        replaceDriver(WebDriverUtils.createEdgeDriver());
        verifyCrossBrowserFeatures();
    }

    @Test
    public void firefoxBrowser() throws Exception {
        replaceDriver(new FirefoxDriver());
        verifyCrossBrowserFeatures();
    }

    private void resetToDefaults() {
        driver.get(FITNESS_PAGE);
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver ->
                "complete".equals(((JavascriptExecutor) currentDriver)
                        .executeScript("return document.readyState"))
                        && Boolean.TRUE.equals(((JavascriptExecutor) currentDriver)
                        .executeScript("return typeof resetFitnessChallenge === 'function'")));
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge()");
        waitForReset();
    }

    private void replaceDriver(WebDriver replacement) {
        if (driver != null) {
            driver.quit();
        }
        driver = replacement;
        resetToDefaults();
    }

    private void waitForReset() {
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver -> {
            try {
                return DEFAULT_PARTICIPANTS.equals(readParticipantSteps(currentDriver));
            } catch (StaleElementReferenceException | NoSuchElementException
                     | NumberFormatException ex) {
                return false;
            }
        });
    }

    private List<WebElement> participantItems() {
        return new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver -> {
            try {
                if (readParticipantSteps(currentDriver) == null) {
                    return null;
                }
                return currentDriver.findElements(PARTICIPANT_ITEMS);
            } catch (StaleElementReferenceException | NoSuchElementException
                     | NumberFormatException ex) {
                return null;
            }
        });
    }

    private String getParticipantName(String name) {
        return participantItems().stream()
                .map(item -> item.findElement(By.cssSelector(".participant-name")).getText())
                .filter(name::equals)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private long getParticipantSteps(String name) {
        return new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver -> {
            try {
                if (readParticipantSteps(currentDriver) == null) {
                    return null;
                }
                for (WebElement item : currentDriver.findElements(PARTICIPANT_ITEMS)) {
                    String participantName =
                            item.findElement(By.cssSelector(".participant-name")).getText();
                    if (participantName.equals(name)) {
                        String steps = item.findElement(By.cssSelector(".participant-steps")).getText();
                        return steps.isBlank() ? null : parseSteps(steps);
                    }
                }
                return null;
            } catch (StaleElementReferenceException | NoSuchElementException
                     | NumberFormatException ex) {
                return null;
            }
        });
    }

    private long parseSteps(String text) {
        return Long.parseLong(text.replace(" steps", "").replace(",", "").trim());
    }

    private Map<String, Long> readParticipantSteps(WebDriver currentDriver) {
        List<WebElement> items = currentDriver.findElements(PARTICIPANT_ITEMS);
        if (items.size() != DEFAULT_PARTICIPANTS.size()) {
            return null;
        }

        Map<String, Long> participants = new LinkedHashMap<>();
        for (WebElement item : items) {
            String name = item.findElement(By.cssSelector(".participant-name")).getText().trim();
            String steps = item.findElement(By.cssSelector(".participant-steps")).getText();
            if (name.isEmpty() || steps.isBlank()) {
                return null;
            }
            participants.put(name, parseSteps(steps));
        }
        return participants.keySet().equals(DEFAULT_PARTICIPANTS.keySet())
                ? participants
                : null;
    }

    private void assertDefaultSteps() {
        waitForReset();
        assertEquals(DEFAULT_PARTICIPANTS, readParticipantSteps(driver));
    }

    private void assertRankingDescending() {
        long previous = Long.MAX_VALUE;
        for (WebElement item : participantItems()) {
            long steps = parseSteps(item.findElement(By.cssSelector(".participant-steps")).getText());
            assertTrue(steps <= previous);
            previous = steps;
        }
    }

    private WebElement topButton(String id) {
        return driver.findElements(By.id(id)).get(0);
    }

    private WebElement bottomButton(String id) {
        return driver.findElements(By.id(id)).get(1);
    }

    private void scrollToBottomButton(String id) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", bottomButton(id));
    }

    private void openModal() {
        topButton("addStepsBtn").click();
        assertModalOpen();
    }

    private void assertModalElements() {
        assertModalOpen();
        assertEquals("Add Steps to Participant",
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText());

        Select participantSelect =
                new Select(driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant",
                participantSelect.getFirstSelectedOption().getText());
        assertEquals(11, participantSelect.getOptions().size());

        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        assertEquals("number", stepsInput.getAttribute("type"));
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed());
    }

    private void assertModalOpen() {
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver ->
                "block".equals(currentDriver.findElement(By.id("addStepsModal"))
                        .getCssValue("display"))
                        || "block".equals(currentDriver.findElement(By.id("addStepsModal"))
                        .getAttribute("style")));
    }

    private void assertModalClosed() {
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver ->
                "none".equals(currentDriver.findElement(By.id("addStepsModal"))
                        .getCssValue("display")));
    }

    private void submitModal() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void addSteps(String participantName, String steps) {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText(participantName);
        driver.findElement(By.id("steps_input")).sendKeys(steps);
        submitModal();
    }

    private void assertAndAcceptAlert(String expectedText) {
        Alert alert = new WebDriverWait(driver, WAIT_SECONDS).until(
                currentDriver -> {
                    try {
                        return currentDriver.switchTo().alert();
                    } catch (org.openqa.selenium.NoAlertPresentException ex) {
                        return null;
                    }
                });
        assertEquals(expectedText, alert.getText());
        alert.accept();
    }

    private void verifyCrossBrowserFeatures() throws Exception {
        addValidStepsToParticipant();
        submitWithoutSelectingParticipant();
        closeModal();
        resetViaTopButton();
    }

    private void closeModal() {
        WebElement closeButton = driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
    }
}
