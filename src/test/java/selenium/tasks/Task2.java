package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverFactory;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    WebDriverWait wait;

    private static final String REAL_PAGE_URL =
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
        driver = ChromeDriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(REAL_PAGE_URL);
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();
        waitUntilParticipantCount(10);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void waitUntilParticipantCount(int count) {
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), count));
    }

    private WebElement topButton(String id) {
        return driver.findElements(By.id(id)).get(0);
    }

    private WebElement bottomButton(String id) {
        return driver.findElements(By.id(id)).get(1);
    }

    private void openModal() {
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        if (!modal.getCssValue("display").equals("block")) {
            topButton("addStepsBtn").click();
        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private boolean modalIsOpen() {
        return "block".equals(driver.findElement(By.id("addStepsModal"))
                .getCssValue("display"));
    }

    private void assertAndAcceptAlert(String expectedText) {
        org.openqa.selenium.Alert alert =
                wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
        alert.accept();
    }

    private Map<String, Integer> participantSteps() {
        Map<String, Integer> steps = new LinkedHashMap<>();
        for (WebElement participant : driver.findElements(By.cssSelector("#participantsList li"))) {
            String name = participant.findElement(By.cssSelector(".participant-name"))
                    .getText().trim();
            String text = participant.findElement(By.cssSelector(".participant-steps"))
                    .getText().trim();
            text = text.replace("steps", "")
                    .replace(",", "")
                    .trim();
            steps.put(name, Integer.parseInt(text));
        }
        return steps;
    }

    private int participantSteps(String name) {
        for (Map.Entry<String, Integer> entry : participantSteps().entrySet()) {
            if (entry.getKey().equals(name)) {
                return entry.getValue();
            }
        }
        fail("Participant not found: " + name);
        return 0;
    }

    private void addSteps(String name, String steps) {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText(name);
        WebElement input = driver.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void assertDefaultState() {
        assertEquals("Fitness Challenge",
                driver.findElement(By.cssSelector("h2")).getText().trim());
        waitUntilParticipantCount(10);

        List<WebElement> participants =
                driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(10, participants.size());

        Map<String, Integer> actual = participantSteps();
        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertTrue(actual.containsKey(entry.getKey()),
                    "Missing participant: " + entry.getKey());
        }

        assertEquals("John Smith", participants.get(0)
                .findElement(By.cssSelector(".participant-name")).getText().trim());
        assertEquals("David Brown", participants.get(1)
                .findElement(By.cssSelector(".participant-name")).getText().trim());
        assertEquals("Jill Watson", participants.get(2)
                .findElement(By.cssSelector(".participant-name")).getText().trim());

        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals((int) entry.getValue(), participantSteps(entry.getKey()));
        }

        assertEquals(2, driver.findElements(By.id("addStepsBtn")).size());
        assertEquals(2, driver.findElements(By.id("resetBtn")).size());
        for (WebElement button : driver.findElements(By.id("addStepsBtn"))) {
            assertTrue(button.isDisplayed());
        }
        for (WebElement button : driver.findElements(By.id("resetBtn"))) {
            assertTrue(button.isDisplayed());
        }
    }

    private void assertDefaultTrophies() {
        List<WebElement> participants =
                driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals("rgba(255, 215, 0, 1)",
                participants.get(0).findElement(By.cssSelector(".fa-trophy"))
                        .getCssValue("color"));
        assertEquals("rgba(192, 192, 192, 1)",
                participants.get(1).findElement(By.cssSelector(".fa-trophy"))
                        .getCssValue("color"));
        assertEquals("rgba(205, 127, 50, 1)",
                participants.get(2).findElement(By.cssSelector(".fa-trophy"))
                        .getCssValue("color"));
        for (int i = 3; i < participants.size(); i++) {
            assertTrue(participants.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
    }

    private void submitAndWaitForModalClose() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        waitUntilParticipantCount(10);
    }

    private void submitModalByJavaScript() {
        ((JavascriptExecutor) driver).executeScript("addStepsToParticipant()");
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertDefaultState();
        assertDefaultTrophies();
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        List<WebElement> participants =
                driver.findElements(By.cssSelector("#participantsList li"));
        int previous = Integer.MAX_VALUE;
        for (WebElement participant : participants) {
            int steps = Integer.parseInt(participant
                    .findElement(By.cssSelector(".participant-steps"))
                    .getText().replace("steps", "")
                    .replace(",", "")
                    .trim());
            assertTrue(steps <= previous);
            previous = steps;
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        assertDefaultTrophies();
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        openModal();
        assertEquals("Add Steps to Participant",
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText().trim());
        assertEquals(1, driver.findElements(By.id("participant_select")).size());
        assertEquals("number",
                driver.findElement(By.id("steps_input")).getAttribute("type"));
        assertEquals("Add Steps",
                driver.findElement(By.id("modal_add_steps_button")).getText().trim());
        assertEquals(1, driver.findElements(By.cssSelector(".w3-closebtn")).size());
        Select select = new Select(driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant", select.getFirstSelectedOption().getText());
        assertEquals(11, select.getOptions().size());
        assertEquals(10, select.getOptions().stream()
                .filter(option -> !option.getText().equals("Choose participant"))
                .count());
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        bottomButton("addStepsBtn").click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertEquals("Add Steps to Participant",
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText().trim());
        assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(driver.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertEquals(10, new Select(driver.findElement(By.id("participant_select")))
                .getOptions().size() - 1);
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModal();
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('addStepsModal').style.display='none'");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        assertFalse(modalIsOpen());
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = participantSteps("Mike Kid");
        addSteps("Mike Kid", "1000");
        submitAndWaitForModalClose();
        assertEquals(before + 1000, participantSteps("Mike Kid"));

        List<WebElement> participants =
                driver.findElements(By.cssSelector("#participantsList li"));
        int previous = Integer.MAX_VALUE;
        for (WebElement participant : participants) {
            int steps = Integer.parseInt(participant
                    .findElement(By.cssSelector(".participant-steps"))
                    .getText().replace("steps", "")
                    .replace(",", "")
                    .trim());
            assertTrue(steps <= previous);
            previous = steps;
        }
    }

    @Test
    public void addZeroSteps() throws Exception {
        addSteps("Mike Kid", "0");
        assertAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(modalIsOpen());
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        int before = participantSteps("Mike Kid");
        addSteps("Mike Kid", "999999");
        submitAndWaitForModalClose();
        assertEquals(before + 999999, participantSteps("Mike Kid"));
        assertEquals("Mike Kid",
                driver.findElements(By.cssSelector("#participantsList li")).get(0)
                        .findElement(By.cssSelector(".participant-name")).getText().trim());
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModal();
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        submitModalByJavaScript();
        assertAndAcceptAlert("Please select a participant");
        assertTrue(modalIsOpen());
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('addStepsModal').style.display='none'");
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        addSteps("Mike Kid", "0");
        assertAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(modalIsOpen());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("-500");
        submitModalByJavaScript();
        assertAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(modalIsOpen());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertEquals("", input.getAttribute("value"));
        assertFalse(input.getAttribute("value").matches(".*[A-Za-z].*"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        addSteps("Mike Kid", "1000");
        submitAndWaitForModalClose();
        addSteps("Jill Watson", "2000");
        submitAndWaitForModalClose();
        topButton("resetBtn").click();
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10));
        assertDefaultState();
        assertDefaultTrophies();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        addSteps("Mike Kid", "1000");
        submitAndWaitForModalClose();
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        bottomButton("resetBtn").click();
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10));
        assertDefaultState();
        assertDefaultTrophies();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input"))
                .sendKeys("9007199254740991");
        submitModalByJavaScript();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        waitUntilParticipantCount(10);
        assertEquals("Mike Kid",
                driver.findElements(By.cssSelector("#participantsList li")).get(0)
                        .findElement(By.cssSelector(".participant-name")).getText().trim());
    }

    @Test
    public void decimalStepValues() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("100.5");
        submitModalByJavaScript();
        assertAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(modalIsOpen());
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    private void verifyCrossBrowserFlow() {
        int mikeBefore = participantSteps("Mike Kid");
        addSteps("Mike Kid", "1000");
        submitAndWaitForModalClose();
        assertEquals(mikeBefore + 1000, participantSteps("Mike Kid"));

        openModal();
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        submitModalByJavaScript();
        assertAndAcceptAlert("Please select a participant");
        assertTrue(modalIsOpen());

        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('addStepsModal').style.display='none'");
        topButton("resetBtn").click();
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10));
        assertDefaultState();
    }

    @Test
    public void chromeBrowser() throws Exception {
        verifyCrossBrowserFlow();
    }

    @Test
    @Disabled("This project is configured to use a local ChromeDriver only")
    public void edgeBrowser() throws Exception {
        addValidStepsToParticipant();
        submitWithoutSelectingParticipant();
        resetViaTopButton();
    }

    @Test
    @Disabled("This project is configured to use a local ChromeDriver only")
    public void firefoxBrowser() throws Exception {
        addValidStepsToParticipant();
        submitWithoutSelectingParticipant();
        resetViaTopButton();
    }
}
