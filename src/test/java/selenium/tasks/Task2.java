package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.DriverUtility;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
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

    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = DriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(PAGE_URL);
        ((JavascriptExecutor) driver).executeScript("localStorage.clear()");
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#participantsList li")));
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", driver.findElement(By.cssSelector("h2")).getText());
        assertEquals(10, participantRows().size());
        assertDefaultParticipants();
        assertEquals(2, visibleButtons("Add Steps").size());
        assertEquals(2, visibleButtons("Reset List").size());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());
        int previous = Integer.MAX_VALUE;
        for (WebElement row : rows) {
            int steps = stepsForRow(row);
            assertTrue(steps <= previous,
                    "Participants are not in descending step-count order");
            previous = steps;
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> rows = participantRows();
        assertTrophy(rows.get(0), "gold");
        assertTrophy(rows.get(1), "silver");
        assertTrophy(rows.get(2), "rgb(205, 127, 50)");
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Participant ranked " + (i + 1) + " should not have a trophy");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        visibleButtons("Add Steps").get(0).click();
        assertModalElements();
        Select participantSelect = new Select(driver.findElement(By.id("participant_select")));
        assertEquals(11, participantSelect.getOptions().size());
        assertEquals("Choose participant",
                participantSelect.getFirstSelectedOption().getText());
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        List<WebElement> addButtons = visibleButtons("Add Steps");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
                addButtons.get(1));
        addButtons.get(1).click();
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModal();
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelector('span.w3-closebtn').click()");
        assertEquals("none",
                driver.findElement(By.id("addStepsModal")).getCssValue("display"));
        assertDefaultParticipants();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = stepsForParticipant("Mike Kid");
        addSteps("Mike Kid", "1000");
        assertModalClosed();
        assertEquals(before + 1000, stepsForParticipant("Mike Kid"));
        assertRankingDescending();
    }

    @Test
    public void addZeroSteps() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        int before = stepsForParticipant("Mike Kid");
        addSteps("Mike Kid", "999999");
        assertModalClosed();
        assertEquals(before + 999999, stepsForParticipant("Mike Kid"));
        assertEquals("Mike Kid", participantName(participantRows().get(0)));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModal();
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please select a participant");
        assertModalOpen();
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('min')", input);
        input.sendKeys("-500");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertTrue(input.getAttribute("value").isEmpty(),
                "Non-numeric characters should not appear in the steps input");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        visibleButtons("Reset List").get(0).click();
        wait.until(ExpectedConditions.urlToBe(PAGE_URL));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#participantsList li")));
        assertDefaultParticipants();
        assertRankingDescending();
        assertDefaultMedals();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        List<WebElement> resetButtons = visibleButtons("Reset List");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
                resetButtons.get(1));
        resetButtons.get(1).click();
        wait.until(ExpectedConditions.urlToBe(PAGE_URL));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#participantsList li")));
        assertDefaultParticipants();
        assertRankingDescending();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input"))
                .sendKeys("9007199254740991");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertModalClosed();
        long actual = (long) ((Number) ((JavascriptExecutor) driver).executeScript(
                "return JSON.parse(localStorage.getItem('participant0')).steps;"
        )).longValue();
        assertEquals(9007199254749492L, actual);
        assertEquals("Mike Kid", participantName(participantRows().get(0)));
    }

    @Test
    public void decimalStepValues() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("100.5");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    private List<WebElement> participantRows() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#participantsList li")));
    }

    private List<WebElement> visibleButtons(String text) {
        List<WebElement> result = new java.util.ArrayList<>();
        for (WebElement button : driver.findElements(By.xpath(
                "//button[normalize-space()='" + text + "']"))) {
            if (button.isDisplayed()) {
                result.add(button);
            }
        }
        return result;
    }

    private void assertDefaultParticipants() {
        Map<String, Integer> actual = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            actual.put(participantName(row), stepsForRow(row));
        }
        assertEquals(DEFAULT_STEPS, actual);
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText().trim();
    }

    private int stepsForRow(WebElement row) {
        String text = row.findElement(By.cssSelector(".participant-steps"))
                .getText().replace(",", "").replace(" steps", "").trim();
        return Integer.parseInt(text);
    }

    private int stepsForParticipant(String name) {
        for (WebElement row : participantRows()) {
            if (participantName(row).equals(name)) {
                return stepsForRow(row);
            }
        }
        fail("Participant not found: " + name);
        return -1;
    }

    private void assertRankingDescending() {
        int previous = Integer.MAX_VALUE;
        for (WebElement row : participantRows()) {
            int steps = stepsForRow(row);
            assertTrue(steps <= previous);
            previous = steps;
        }
    }

    private void assertTrophy(WebElement row, String expectedColor) {
        WebElement trophy = row.findElement(By.cssSelector("i.fa-trophy"));
        String color = trophy.getCssValue("color");
        boolean matches = expectedColor.equals(color)
                || "gold".equals(expectedColor) && "rgba(255, 215, 0, 1)".equals(color)
                || "gold".equals(expectedColor) && "rgb(255, 215, 0)".equals(color)
                || "silver".equals(expectedColor) && "rgba(192, 192, 192, 1)".equals(color)
                || "silver".equals(expectedColor) && "rgb(192, 192, 192)".equals(color)
                || "rgb(205, 127, 50)".equals(expectedColor)
                        && ("rgb(205, 127, 50)".equals(color)
                        || "rgba(205, 127, 50, 1)".equals(color));
        assertTrue(matches,
                "Unexpected trophy color: " + color);
    }

    private void assertDefaultMedals() {
        List<WebElement> rows = participantRows();
        assertTrophy(rows.get(0), "gold");
        assertTrophy(rows.get(1), "silver");
        assertTrophy(rows.get(2), "rgb(205, 127, 50)");
    }

    private void openModal() {
        visibleButtons("Add Steps").get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void assertModalElements() {
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant",
                modal.findElement(By.cssSelector("h3")).getText());
        assertTrue(modal.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(modal.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(modal.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(modal.findElement(By.cssSelector("span.w3-closebtn")).isDisplayed());
        assertEquals("×",
                modal.findElement(By.cssSelector("span.w3-closebtn")).getText());
    }

    private void addSteps(String participantName, String steps) {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText(participantName);
        driver.findElement(By.id("steps_input")).sendKeys(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void assertAlert(String expectedMessage) {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedMessage, alert.getText());
        alert.accept();
    }

    private void assertModalOpen() {
        assertEquals("block",
                driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    private void assertModalClosed() {
        wait.until(ExpectedConditions.not(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))));
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
