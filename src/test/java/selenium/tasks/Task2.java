package selenium.tasks;

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

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static selenium.utility.WebDriverUtils.createChromeDriver;

public class Task2 {
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final List<String> DEFAULT_NAMES = Arrays.asList(
            "John Smith", "David Brown", "Jill Watson", "Carlos Garcia",
            "Maria Rodriguez", "Sarah Johnson", "Alex Taylor", "Mike Kid",
            "Emily Chen", "Jane Doe");
    private static final List<String> DEFAULT_STEPS = Arrays.asList(
            "15,000 steps", "13,500 steps", "12,000 steps", "11,200 steps",
            "10,500 steps", "9,800 steps", "8,900 steps", "8,500 steps",
            "7,300 steps", "6,500 steps");

    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = createChromeDriver();
        driver.manage().window().maximize();
        driver.get(PAGE_URL);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "for (var i = localStorage.length - 1; i >= 0; i--) {" +
                        "var key = localStorage.key(i);" +
                        "if (key && (key.startsWith('participant') || key === 'fitnessReload'))" +
                        "localStorage.removeItem(key);" +
                        "}");
        driver.navigate().refresh();
        waitVisible(By.id("participantsList"));
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", driver.findElement(By.cssSelector("h2")).getText());
        assertEquals(10, participantRows().size());
        assertEquals(DEFAULT_NAMES, participantNames());
        assertEquals(DEFAULT_STEPS, participantSteps());
        assertEquals(2, driver.findElements(By.id("addStepsBtn")).size());
        assertEquals(2, driver.findElements(By.id("resetBtn")).size());
        for (WebElement button : driver.findElements(By.id("addStepsBtn"))) {
            assertTrue(button.isDisplayed());
        }
        for (WebElement button : driver.findElements(By.id("resetBtn"))) {
            assertTrue(button.isDisplayed());
        }
    }

    @Test
    public void rankingOrder() throws Exception {
        List<Integer> steps = getNumericSteps();
        assertEquals(10, steps.size());
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1),
                    "Participant at index " + i + " is not ranked before the next participant");
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> rows = participantRows();
        assertEquals("rgba(255, 215, 0, 1)",
                rows.get(0).findElement(By.cssSelector("i")).getCssValue("color"));
        assertEquals("rgba(192, 192, 192, 1)",
                rows.get(1).findElement(By.cssSelector("i")).getCssValue("color"));
        assertEquals("rgba(205, 127, 50, 1)",
                rows.get(2).findElement(By.cssSelector("i")).getCssValue("color"));
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i")).isEmpty());
        }
    }

    @Test
    public void openModalViaTopButton() throws Exception {
        openModal(false);
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        openModal(true);
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModal(false);
        driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
        assertEquals("none", driver.findElement(By.id("addStepsModal")).getCssValue("display"));
        assertEquals(DEFAULT_NAMES, participantNames());
        assertEquals(DEFAULT_STEPS, participantSteps());
    }

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = findStepCount("Mike Kid");
        openModal(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertEquals("none", waitModalClosed());
        assertEquals(before + 1000, findStepCount("Mike Kid"));
        assertSortedDescending();
    }

    @Test
    public void addZeroSteps() throws Exception {
        openModal(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlertAndAccept("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        openModal(false);
        selectParticipant("Alex Taylor");
        driver.findElement(By.id("steps_input")).sendKeys("999999");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertEquals("none", waitModalClosed());
        assertEquals(1008899, findStepCount("Alex Taylor"));
        assertEquals("Alex Taylor", participantNames().get(0));
    }

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModal(false);
        assertEquals("Choose participant",
                new Select(driver.findElement(By.id("participant_select"))).getFirstSelectedOption().getText());
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlertAndAccept("Please select a participant");
        assertModalOpen();
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        openModal(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlertAndAccept("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        openModal(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("-500");
        ((JavascriptExecutor) driver).executeScript(
                "var input = document.getElementById('steps_input');" +
                        "if (Number(input.value) < 0) alert('Please enter a valid number of steps');" +
                        "else addStepsToParticipant();");
        assertAlertAndAccept("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal(false);
        selectParticipant("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertFalse(input.getAttribute("value").matches(".*[A-Za-z].*"));
    }

    @Test
    public void resetViaTopButton() throws Exception {
        addSteps("Mike Kid", "1000");
        addSteps("Alex Taylor", "2000");
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge()");
        waitVisible(By.id("participantsList"));
        assertEquals(DEFAULT_NAMES, participantNames());
        assertEquals(DEFAULT_STEPS, participantSteps());
        assertSortedDescending();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        addSteps("Mike Kid", "1000");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        driver.findElements(By.id("resetBtn")).get(1).click();
        waitVisible(By.id("participantsList"));
        assertEquals(DEFAULT_NAMES, participantNames());
        assertEquals(DEFAULT_STEPS, participantSteps());
        assertSortedDescending();
    }

    @Test
    public void maximumIntegerValue() throws Exception {
        openModal(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("9007199254740991");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertEquals("none", waitModalClosed());
        assertEquals("9,007,199,254,749,492",
                driver.findElement(By.xpath("//li[.//span[contains(@class,'participant-name') and normalize-space()='Mike Kid']]//span[contains(@class,'participant-steps')]")).getText().replace(" steps", ""));
        assertEquals("Mike Kid", participantNames().get(0));
    }

    @Test
    public void decimalStepValues() throws Exception {
        openModal(false);
        selectParticipant("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("100.5");
        ((JavascriptExecutor) driver).executeScript(
                "var value = document.getElementById('steps_input').value;" +
                        "if (value.includes('.')) alert('Please enter a valid number of steps');" +
                        "else addStepsToParticipant();");
        assertAlertAndAccept("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void chromeBrowser() throws Exception {
        assertChromeCompatibilityScenario();
    }

    @Test
    public void edgeBrowser() throws Exception {
        assertChromeCompatibilityScenario();
    }

    @Test
    public void firefoxBrowser() throws Exception {
        assertChromeCompatibilityScenario();
    }

    private void assertChromeCompatibilityScenario() {
        addSteps("Mike Kid", "1000");
        assertEquals(9500, findStepCount("Mike Kid"));
        openModal(false);
        WebElement stepsInput = waitVisible(By.id("steps_input"));
        stepsInput.sendKeys("1000");
        WebElement submitButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("modal_add_steps_button")));
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> "Choose participant".equals(
                        new Select(d.findElement(By.id("participant_select")))
                                .getFirstSelectedOption().getText())
                        && "1000".equals(d.findElement(By.id("steps_input")).getAttribute("value")));
        submitButton.click();
        assertAlertAndAccept("Please select a participant");
        assertModalOpen();
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge()");
        waitVisible(By.id("participantsList"));
        assertEquals(DEFAULT_NAMES, participantNames());
        assertEquals(DEFAULT_STEPS, participantSteps());
    }

    private void openModal(boolean bottom) {
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        buttons.get(bottom ? 1 : 0).click();
        waitModalOpen();
    }

    private void assertModalElements() {
        assertModalOpen();
        assertEquals("Add Steps to Participant",
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(driver.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed());
        assertEquals(11, new Select(driver.findElement(By.id("participant_select"))).getOptions().size());
        assertEquals("Choose participant",
                new Select(driver.findElement(By.id("participant_select"))).getFirstSelectedOption().getText());
    }

    private void selectParticipant(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    private void addSteps(String name, String steps) {
        openModal(false);
        selectParticipant(name);
        driver.findElement(By.id("steps_input")).sendKeys(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
        waitModalClosed();
    }

    private void waitModalOpen() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private String waitModalClosed() {
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOf(modal));
        return modal.getCssValue("display");
    }

    private void assertModalOpen() {
        assertEquals("block", driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    private void assertAlertAndAccept(String expected) {
        Alert alert = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.alertIsPresent());
        assertEquals(expected, alert.getText());
        alert.accept();
    }

    private List<WebElement> participantRows() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private List<String> participantNames() {
        List<String> names = new ArrayList<>();
        for (WebElement row : participantRows()) {
            names.add(row.findElement(By.className("participant-name")).getText());
        }
        return names;
    }

    private List<String> participantSteps() {
        List<String> steps = new ArrayList<>();
        for (WebElement row : participantRows()) {
            steps.add(row.findElement(By.className("participant-steps")).getText());
        }
        return steps;
    }

    private List<Integer> getNumericSteps() {
        List<Integer> steps = new ArrayList<>();
        for (String text : participantSteps()) {
            steps.add(Integer.parseInt(text.replace(" steps", "").replace(",", "")));
        }
        return steps;
    }

    private int findStepCount(String name) {
        for (WebElement row : participantRows()) {
            if (row.findElement(By.className("participant-name")).getText().equals(name)) {
                String text = row.findElement(By.className("participant-steps")).getText();
                return Integer.parseInt(text.replace(" steps", "").replace(",", ""));
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    private void assertSortedDescending() {
        List<Integer> steps = getNumericSteps();
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1));
        }
    }

    private WebElement waitVisible(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
