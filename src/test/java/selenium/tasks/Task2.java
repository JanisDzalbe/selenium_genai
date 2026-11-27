package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverFactory;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

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
        driver = ChromeDriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(PAGE_URL);
        wait.until(d -> d.findElements(By.cssSelector("#participantsList .participant-name")).size() == 10);
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

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText();
    }

    private int participantSteps(WebElement row) {
        String text = row.findElement(By.cssSelector(".participant-steps")).getText();
        return Integer.parseInt(text.replaceAll("[^0-9]", "").trim());
    }

    private BigInteger readParticipantSteps(String name) {
        String text = findParticipantRow(name)
                .findElement(By.cssSelector(".participant-steps"))
                .getText();
        return new BigInteger(text.replaceAll("[^0-9]", "").trim());
    }

    private WebElement findParticipantRow(String name) {
        for (WebElement row : participantRows()) {
            if (participantName(row).equals(name)) {
                return row;
            }
        }
        fail("Participant not found: " + name);
        return null;
    }

    private void openModal() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        wait.until(d -> {
            WebElement modal = d.findElement(By.id("addStepsModal"));
            return modal.isDisplayed() && "block".equals(modal.getCssValue("display"));
        });
    }

    private void openModalAtBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        buttons.get(buttons.size() - 1).click();
        wait.until(d -> d.findElement(By.id("addStepsModal")).isDisplayed());
    }

    private void assertModalElements() {
        wait.until(d -> d.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals("Add Steps to Participant",
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertNotNull(driver.findElement(By.id("participant_select")));
        assertNotNull(driver.findElement(By.id("steps_input")));
        assertNotNull(driver.findElement(By.id("modal_add_steps_button")));
        WebElement closeButton = driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        assertEquals("Close Modal", closeButton.getAttribute("title"));

        Select participantSelect = new Select(driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant",
                participantSelect.getFirstSelectedOption().getText());
        assertEquals(11, participantSelect.getOptions().size());
    }

    private void submitSteps(String participantName, String steps) {
        openModal();
        Select participantSelect = new Select(driver.findElement(By.id("participant_select")));
        if (participantName != null) {
            participantSelect.selectByVisibleText(participantName);
        }
        driver.findElement(By.id("steps_input")).sendKeys(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void acceptAlert(String expectedText) {
        org.openqa.selenium.Alert alert = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
        alert.accept();
    }

    private void resetViaButton(int buttonIndex) {
        List<WebElement> buttons = driver.findElements(By.id("resetBtn"));
        buttons.get(buttonIndex).click();
        wait.until(d -> d.findElements(By.cssSelector("#participantsList .participant-name")).size() == 10);
    }

    private void assertDefaultState() {
        assertEquals(10, participantRows().size());
        for (WebElement row : participantRows()) {
            String name = participantName(row);
            assertTrue(DEFAULT_STEPS.containsKey(name), "Unexpected participant: " + name);
            assertEquals((int) DEFAULT_STEPS.get(name), participantSteps(row),
                    "Unexpected default steps for " + name);
        }
        assertEquals("John Smith", participantName(participantRows().get(0)));
        assertEquals("David Brown", participantName(participantRows().get(1)));
        assertEquals("Jill Watson", participantName(participantRows().get(2)));
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", driver.getTitle());
        assertEquals("Fitness Challenge",
                driver.findElement(By.cssSelector("h2")).getText());
        assertDefaultState();
        assertEquals(2, driver.findElements(By.id("addStepsBtn")).size());
        assertEquals(2, driver.findElements(By.id("resetBtn")).size());
        for (WebElement button : driver.findElements(By.id("addStepsBtn"))) {
            assertTrue(button.isDisplayed());
        }
        for (WebElement button : driver.findElements(By.id("resetBtn"))) {
            assertTrue(button.isDisplayed());
        }
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());
        for (int i = 0; i < rows.size(); i++) {
            int current = participantSteps(rows.get(i));
            if (i > 0) {
                assertTrue(participantSteps(rows.get(i - 1)) >= current,
                        "Participants are not in descending step-count order");
            }
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> rows = participantRows();
        assertTrue(rows.get(0).findElement(By.cssSelector(".fa-trophy"))
                .getCssValue("color").contains("255"));
        assertTrue(rows.get(1).findElement(By.cssSelector(".fa-trophy"))
                .getCssValue("color").contains("192"));
        assertTrue(rows.get(2).findElement(By.cssSelector(".fa-trophy"))
                .getCssValue("color").contains("205"));
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        openModal();
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        openModalAtBottom();
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModal();
        WebElement closeButton = driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
        wait.until(d -> !d.findElement(By.id("addStepsModal")).isDisplayed());
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = participantSteps(findParticipantRow("Mike Kid"));
        submitSteps("Mike Kid", "1000");
        wait.until(d -> participantSteps(findParticipantRow("Mike Kid")) == before + 1000);
        assertEquals(before + 1000, participantSteps(findParticipantRow("Mike Kid")));
        assertFalse(driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertTrue(participantRows().size() == 10);
        for (int i = 1; i < participantRows().size(); i++) {
            assertTrue(participantSteps(participantRows().get(i - 1)) >= participantSteps(participantRows().get(i)));
        }
    }

    @Test
    public void addZeroSteps() throws Exception {
        submitSteps("Mike Kid", "0");
        acceptAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        submitSteps("Mike Kid", "999999");
        wait.until(d -> !d.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals("Mike Kid", participantName(participantRows().get(0)));
        assertEquals(1008499, participantSteps(findParticipantRow("Mike Kid")));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        submitSteps(null, "1000");
        acceptAlert("Please select a participant");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        submitSteps("Mike Kid", "0");
        acceptAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        submitSteps("Mike Kid", "-500");
        acceptAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.sendKeys("abc");
        assertFalse(stepsInput.getAttribute("value").matches(".*[A-Za-z].*"));
        assertEquals("", stepsInput.getAttribute("value"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        submitSteps("Mike Kid", "1000");
        wait.until(d -> !d.findElement(By.id("addStepsModal")).isDisplayed());
        submitSteps("Jill Watson", "2000");
        wait.until(d -> !d.findElement(By.id("addStepsModal")).isDisplayed());
        resetViaButton(0);
        assertDefaultState();
        assertTrue(participantRows().get(0).findElement(By.cssSelector(".fa-trophy")).isDisplayed());
        assertTrue(participantRows().get(1).findElement(By.cssSelector(".fa-trophy")).isDisplayed());
        assertTrue(participantRows().get(2).findElement(By.cssSelector(".fa-trophy")).isDisplayed());
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        submitSteps("Mike Kid", "1000");
        wait.until(d -> !d.findElement(By.id("addStepsModal")).isDisplayed());
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        resetViaButton(1);
        assertDefaultState();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        submitSteps("Mike Kid", "9007199254740991");
        wait.until(d -> !d.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals("Mike Kid", participantName(participantRows().get(0)));

        String displayed = participantRows().get(0)
                .findElement(By.cssSelector(".participant-steps")).getText();
        String digits = displayed.replaceAll("[^0-9]", "");
        BigInteger updatedSteps = new BigInteger(digits);
        assertTrue(updatedSteps.compareTo(new BigInteger("9007199254740991")) > 0);
        assertTrue(displayed.endsWith("steps"));
    }

    @Test
    public void decimalStepValues() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("100.5");
        driver.findElement(By.id("modal_add_steps_button")).click();

        org.openqa.selenium.Alert alert =
                wait.until(org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(new BigInteger("8500"), readParticipantSteps("Mike Kid"));
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
