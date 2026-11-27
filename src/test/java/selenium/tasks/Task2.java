package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.utility.WebDriverUtility;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task2 {
    WebDriver driver;
    WebDriverWait wait;

    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";

    private static final Map<String, Integer> DEFAULT_STEPS = new LinkedHashMap<>();

    static {
        DEFAULT_STEPS.put("Mike Kid", 8500);
        DEFAULT_STEPS.put("Jill Watson", 12000);
        DEFAULT_STEPS.put("Jane Doe", 6500);
        DEFAULT_STEPS.put("John Smith", 15000);
        DEFAULT_STEPS.put("Sarah Johnson", 9800);
        DEFAULT_STEPS.put("Carlos Garcia", 11200);
        DEFAULT_STEPS.put("Emily Chen", 7300);
        DEFAULT_STEPS.put("David Brown", 13500);
        DEFAULT_STEPS.put("Maria Rodriguez", 10500);
        DEFAULT_STEPS.put("Alex Taylor", 8900);
    }

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(PAGE_URL);
        wait.until(d -> d.findElements(By.cssSelector("#participantsList li")).size() == 10);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private List<WebElement> participantRows() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private WebElement participantRow(String name) {
        return participantRows().stream()
                .filter(row -> row.findElement(By.cssSelector(".participant-name")).getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private int stepsOf(String name) {
        String text = participantRow(name)
                .findElement(By.cssSelector(".participant-steps"))
                .getText()
                .replace("steps", "")
                .replace(",", "")
                .trim();
        return Integer.parseInt(text);
    }

    private WebElement topButton(String id) {
        return driver.findElements(By.id(id)).getFirst();
    }

    private WebElement bottomButton(String id) {
        List<WebElement> buttons = driver.findElements(By.id(id));
        assertTrue(buttons.size() >= 2, "Expected top and bottom " + id + " buttons");
        return buttons.get(1);
    }

    private void openModal() {
        topButton("addStepsBtn").click();
        wait.until(d -> "block".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
    }

    private void assertModalElements() {
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant",
                modal.findElement(By.tagName("h3")).getText());
        assertTrue(modal.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(modal.findElement(By.id("steps_input")).isDisplayed());
        assertEquals("number", modal.findElement(By.id("steps_input")).getAttribute("type"));
        assertTrue(modal.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(modal.findElement(By.cssSelector(".w3-closebtn")).isDisplayed());
        assertEquals("×", modal.findElement(By.cssSelector(".w3-closebtn")).getText());
    }

    private void closeModalWithButton() {
        driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
        wait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
    }

    private void addSteps(String participantName, String steps) {
        openModal();
        Select participantSelect = new Select(driver.findElement(By.id("participant_select")));
        if (participantName != null) {
            participantSelect.selectByVisibleText(participantName);
        }
        driver.findElement(By.id("steps_input")).sendKeys(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void acceptAlertWithText(String expectedText) {
        Alert alert = wait.until(d -> d.switchTo().alert());
        assertEquals(expectedText, alert.getText());
        alert.accept();
    }

    private void assertModalOpen() {
        assertEquals("block",
                driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    private void assertDefaultState() {
        wait.until(d -> d.findElements(By.cssSelector("#participantsList li")).size() == 10);
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());

        List<String> names = rows.stream()
                .map(row -> row.findElement(By.cssSelector(".participant-name")).getText())
                .toList();
        assertTrue(names.containsAll(DEFAULT_STEPS.keySet()));

        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(entry.getValue(), stepsOf(entry.getKey()),
                    "Unexpected steps for " + entry.getKey());
        }

        assertEquals("John Smith", names.get(0));
        assertEquals("David Brown", names.get(1));
        assertEquals("Jill Watson", names.get(2));
        assertEquals("Carlos Garcia", names.get(3));
    }

    private void assertTopThreeTrophies() {
        List<WebElement> rows = participantRows();
        assertEquals(1, rows.get(0).findElements(By.cssSelector(".fa-trophy")).size());
        assertEquals(1, rows.get(1).findElements(By.cssSelector(".fa-trophy")).size());
        assertEquals(1, rows.get(2).findElements(By.cssSelector(".fa-trophy")).size());

        String goldColor = rows.get(0).findElement(By.cssSelector(".fa-trophy"))
                .getCssValue("color");
        String silverColor = rows.get(1).findElement(By.cssSelector(".fa-trophy"))
                .getCssValue("color");
        String bronzeColor = rows.get(2).findElement(By.cssSelector(".fa-trophy"))
                .getCssValue("color");
        assertEquals("rgba(255, 215, 0, 1)", goldColor);
        assertEquals("rgba(192, 192, 192, 1)", silverColor);
        assertEquals("rgba(205, 127, 50, 1)", bronzeColor);
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", driver.getTitle());
        assertEquals("Fitness Challenge",
                driver.findElement(By.cssSelector("h2")).getText());
        assertDefaultState();

        assertTrue(topButton("addStepsBtn").isDisplayed());
        assertTrue(bottomButton("addStepsBtn").isDisplayed());
        assertTrue(topButton("resetBtn").isDisplayed());
        assertTrue(bottomButton("resetBtn").isDisplayed());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());
        int previousSteps = Integer.MAX_VALUE;
        for (WebElement row : rows) {
            String text = row.findElement(By.cssSelector(".participant-steps"))
                    .getText()
                    .replace("steps", "")
                    .replace(",", "")
                    .trim();
            int steps = Integer.parseInt(text);
            assertTrue(steps <= previousSteps,
                    "Participants are not in descending step order");
            previousSteps = steps;
        }
    }

    @Test
    public void medalTrophyIcons() {
        assertTopThreeTrophies();
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        openModal();
        assertModalElements();

        Select participantSelect =
                new Select(driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant",
                participantSelect.getFirstSelectedOption().getText());
        assertEquals(11, participantSelect.getOptions().size());
    }

    @Test
    public void openModalViaBottomButton() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        bottomButton("addStepsBtn").click();
        wait.until(d -> "block".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() {
        openModal();
        closeModalWithButton();
        assertNotEquals("block",
                driver.findElement(By.id("addStepsModal")).getCssValue("display"));
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        int initialSteps = stepsOf("Mike Kid");
        addSteps("Mike Kid", "1000");
        wait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
        assertEquals(initialSteps + 1000, stepsOf("Mike Kid"));
    }

    @Test
    public void addZeroSteps() {
        addSteps("Mike Kid", "0");
        acceptAlertWithText("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void addLargeNumberOfSteps() {
        addSteps("Mike Kid", "999999");
        wait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
        assertEquals("Mike Kid",
                participantRows().get(0)
                        .findElement(By.cssSelector(".participant-name"))
                        .getText());
        assertEquals(1008499, stepsOf("Mike Kid"));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        addSteps(null, "1000");
        acceptAlertWithText("Please select a participant");
        assertModalOpen();
    }

    @Test
    public void submitWithoutEnteringSteps() {
        addSteps("Mike Kid", "");
        acceptAlertWithText("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNegativeSteps() {
        addSteps("Mike Kid", "-500");
        acceptAlertWithText("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNonNumericInput() {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.sendKeys("abc");
        String value = stepsInput.getAttribute("value");
        assertTrue(value == null || !value.matches(".*[A-Za-z].*"));
        assertTrue(value == null || value.isEmpty());
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        wait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
        addSteps("Jill Watson", "1000");
        wait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
        assertNotEquals(DEFAULT_STEPS.get("Mike Kid"), stepsOf("Mike Kid"));
        assertNotEquals(DEFAULT_STEPS.get("Jill Watson"), stepsOf("Jill Watson"));

        topButton("resetBtn").click();
        wait.until(d -> d.findElements(By.cssSelector("#participantsList li")).size() == 10);
        assertDefaultState();
        assertTopThreeTrophies();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        wait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
        assertNotEquals(DEFAULT_STEPS.get("Mike Kid"), stepsOf("Mike Kid"));

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        bottomButton("resetBtn").click();
        wait.until(d -> d.findElements(By.cssSelector("#participantsList li")).size() == 10);
        assertDefaultState();
        assertTopThreeTrophies();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        addSteps("Mike Kid", "9007199254740991");
        wait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));

        Object storedSteps = ((JavascriptExecutor) driver).executeScript(
                "return JSON.parse(localStorage.getItem('participant0')).steps;");
        assertTrue(storedSteps instanceof Number
                && ((Number) storedSteps).longValue() >= 9007199254740991L);
        assertEquals("Mike Kid",
                participantRows().get(0)
                        .findElement(By.cssSelector(".participant-name"))
                        .getText());
    }

    @Test
    public void decimalStepValues() {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.sendKeys("100.5");

        Boolean stepMismatch = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validity.stepMismatch;", stepsInput);
        assertTrue(Boolean.TRUE.equals(stepMismatch));

        driver.findElement(By.id("modal_add_steps_button")).click();
        wait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
        assertEquals(8600, stepsOf("Mike Kid"));
    }

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
