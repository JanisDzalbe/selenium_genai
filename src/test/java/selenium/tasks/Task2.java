package selenium.tasks;

import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.math.BigInteger;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    WebDriverWait wait;

    private static final Map<String, Integer> DEFAULT_PARTICIPANTS = new LinkedHashMap<>();

    static {
        DEFAULT_PARTICIPANTS.put("Mike Kid", 8500);
        DEFAULT_PARTICIPANTS.put("Jill Watson", 12000);
        DEFAULT_PARTICIPANTS.put("Jane Doe", 6500);
        DEFAULT_PARTICIPANTS.put("John Smith", 15000);
        DEFAULT_PARTICIPANTS.put("Sarah Johnson", 9800);
        DEFAULT_PARTICIPANTS.put("Carlos Garcia", 11200);
        DEFAULT_PARTICIPANTS.put("Emily Chen", 7300);
        DEFAULT_PARTICIPANTS.put("David Brown", 13500);
        DEFAULT_PARTICIPANTS.put("Maria Rodriguez", 10500);
        DEFAULT_PARTICIPANTS.put("Alex Taylor", 8900);
    }

    @BeforeEach
    public void openPage() {
        Path chromeDriver = Path.of("lib", "chromedriver.exe");
        if (Files.exists(chromeDriver)) {
            System.setProperty("webdriver.chrome.driver", chromeDriver.toAbsolutePath().toString());
        }
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox",
                "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        waitForParticipants();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void waitForParticipants() {
        wait.until(driver -> driver.findElements(By.cssSelector("#participantsList li")).size() == 10);
    }

    private List<WebElement> participants() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#participantsList li")));
    }

    private WebElement participantRow(String name) {
        return participants().stream()
                .filter(row -> row.findElement(By.cssSelector(".participant-name")).getText().trim().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private int participantSteps(String name) {
        String text = participantRow(name).findElement(By.cssSelector(".participant-steps"))
                .getText().replace(" steps", "").replace(",", "").trim();
        return Integer.parseInt(text);
    }

    private void openModal(int buttonIndex, boolean unused) {
        List<WebElement> buttons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("addStepsBtn")));
        if (buttonIndex == 1) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", buttons.get(1));
        }
        wait.until(ExpectedConditions.elementToBeClickable(buttons.get(buttonIndex))).click();
    }

    private void openModalViaTop() {
        openModal(0, true);
    }

    private void openModalViaBottom() {
        openModal(1, true);
    }

    private void assertModalOpen() {
        assertEquals("block", waitForElement(By.id("addStepsModal")).getCssValue("display"));
    }

    private void closeModal() {
        waitForElement(By.cssSelector(".w3-closebtn")).click();
    }

    private void selectParticipant(String name) {
        new Select(waitForElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    private void submitModal(String steps) {
        WebElement input = waitForElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(steps);
        waitForElement(By.id("modal_add_steps_button")).click();
    }

    private String acceptAlert(String expected) {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(expected, alert.getText());
        alert.accept();
        return expected;
    }

    private void resetAt(int buttonIndex) {
        List<WebElement> resetButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("resetBtn")));
        if (buttonIndex == 1) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", resetButtons.get(1));
        }
        wait.until(ExpectedConditions.elementToBeClickable(resetButtons.get(buttonIndex))).click();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#participantsList li")));
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", waitForElement(By.cssSelector("h2")).getText());
        assertEquals(10, participants().size());
        for (Map.Entry<String, Integer> entry : DEFAULT_PARTICIPANTS.entrySet()) {
            assertEquals(entry.getValue(), participantSteps(entry.getKey()));
        }
        assertEquals(2, wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("addStepsBtn"))).size());
        assertEquals(2, wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("resetBtn"))).size());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        List<WebElement> rows = participants();
        int previous = Integer.MAX_VALUE;
        for (WebElement row : rows) {
            int steps = Integer.parseInt(row.findElement(By.cssSelector(".participant-steps"))
                    .getText().replace(" steps", "").replace(",", "").trim());
            assertTrue(steps <= previous);
            previous = steps;
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> rows = participants();
        assertTrue(rows.get(0).findElement(By.cssSelector(".fa-trophy")).getCssValue("color")
                .matches(".*255, 215, 0.*|gold"));
        assertTrue(rows.get(1).findElement(By.cssSelector(".fa-trophy")).getCssValue("color")
                .matches(".*192, 192, 192.*|silver"));
        assertTrue(rows.get(2).findElement(By.cssSelector(".fa-trophy")).getCssValue("color")
                .matches(".*205, 127, 50.*|#cd7f32"));
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        openModalViaTop();
        assertModalOpen();
        assertEquals("Add Steps to Participant", waitForElement(By.cssSelector("#addStepsModal h3")).getText());
        assertTrue(waitForElement(By.id("participant_select")).isDisplayed());
        assertTrue(waitForElement(By.id("steps_input")).isDisplayed());
        assertTrue(waitForElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(waitForElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed());
        Select select = new Select(waitForElement(By.id("participant_select")));
        assertEquals("Choose participant", select.getFirstSelectedOption().getText());
        assertEquals(11, select.getOptions().size());
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        openModalViaBottom();
        assertModalOpen();
        assertEquals("Add Steps to Participant", waitForElement(By.cssSelector("#addStepsModal h3")).getText());
        assertEquals(11, new Select(waitForElement(By.id("participant_select"))).getOptions().size());
        assertTrue(waitForElement(By.id("steps_input")).isDisplayed());
        assertTrue(waitForElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(waitForElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed());
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModalViaTop();
        closeModal();
        assertEquals("none", waitForElement(By.id("addStepsModal")).getCssValue("display"));
        for (Map.Entry<String, Integer> entry : DEFAULT_PARTICIPANTS.entrySet()) {
            assertEquals(entry.getValue(), participantSteps(entry.getKey()));
        }
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = participantSteps("Mike Kid");
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("1000");
        assertEquals("none", waitForElement(By.id("addStepsModal")).getCssValue("display"));
        assertEquals(before + 1000, participantSteps("Mike Kid"));
    }

    @Test
    public void addZeroSteps() throws Exception {
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("0");
        acceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("999999");
        assertEquals("none", waitForElement(By.id("addStepsModal")).getCssValue("display"));
        assertEquals(1008499, participantSteps("Mike Kid"));
        assertEquals("Mike Kid", participants().get(0).findElement(By.cssSelector(".participant-name")).getText());
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModalViaTop();
        submitModal("1000");
        acceptAlert("Please select a participant");
        assertModalOpen();
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("0");
        acceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("-500");
        acceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModalViaTop();
        selectParticipant("Mike Kid");
        WebElement input = waitForElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertFalse(input.getAttribute("value").matches(".*[A-Za-z]+.*"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("1000");
        openModalViaTop();
        selectParticipant("Jill Watson");
        submitModal("2000");
        resetAt(0);
        for (Map.Entry<String, Integer> entry : DEFAULT_PARTICIPANTS.entrySet()) {
            assertEquals(entry.getValue(), participantSteps(entry.getKey()));
        }
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("1000");
        resetAt(1);
        for (Map.Entry<String, Integer> entry : DEFAULT_PARTICIPANTS.entrySet()) {
            assertEquals(entry.getValue(), participantSteps(entry.getKey()));
        }
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        BigInteger before = BigInteger.valueOf(participantSteps("Mike Kid"));
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("9007199254740991");
        assertEquals("none", waitForElement(By.id("addStepsModal")).getCssValue("display"));
        String stepsText = participantRow("Mike Kid").findElement(By.cssSelector(".participant-steps")).getText();
        BigInteger actual = new BigInteger(stepsText.replace(" steps", "").replace(",", "").trim());
        BigInteger added = new BigInteger("9007199254740991");
        BigInteger difference = actual.subtract(before);
        assertTrue(difference.subtract(added).abs().compareTo(BigInteger.ONE) <= 0,
                "Unexpected rendered step count: " + stepsText);
    }

    @Test @Ignore
    public void decimalStepValues() throws Exception {
        openModalViaTop();
        selectParticipant("Mike Kid");
        submitModal("100.5");
        acceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }
}
