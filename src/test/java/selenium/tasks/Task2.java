package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtils;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
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
        driver = WebDriverUtils.createChromeDriver();
        driver.manage().timeouts().implicitlyWait(TIMEOUT);
        driver.manage().window().maximize();
        driver.get(PAGE_URL);
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.findElements(By.cssSelector("#participantsList .participant-name")).size() == 10);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement buttonById(String id, int index) {
        List<WebElement> buttons = driver.findElements(By.id(id));
        assertTrue(buttons.size() > index, "Expected button " + id + " at index " + index);
        return buttons.get(index);
    }

    private void openModal(int buttonIndex) {
        buttonById("addStepsBtn", buttonIndex).click();
        new WebDriverWait(driver, TIMEOUT).until(
                d -> "block".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
    }

    private void assertModalVisible() {
        assertEquals("block", driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    private void closeModal() {
        assertModalVisible();
        WebElement closeButton = driver.findElement(By.cssSelector(".w3-closebtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", closeButton);
        new WebDriverWait(driver, TIMEOUT).until(
                d -> !"block".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));
    }

    private void acceptAlert(String expectedText) {
        Alert alert = new WebDriverWait(driver, TIMEOUT).until(
                d -> d.switchTo().alert());
        assertEquals(expectedText, alert.getText());
        alert.accept();
    }

    private List<WebElement> participantNames() {
        return driver.findElements(By.cssSelector("#participantsList .participant-name"));
    }

    private List<WebElement> participantSteps() {
        return driver.findElements(By.cssSelector("#participantsList .participant-steps"));
    }

    private int participantStepCount(String name) {
        List<WebElement> names = participantNames();
        List<WebElement> steps = participantSteps();
        for (int i = 0; i < names.size(); i++) {
            if (name.equals(names.get(i).getText().trim())) {
                String text = steps.get(i).getText()
                        .replace(",", "")
                        .replace(" steps", "")
                        .trim();
                return Integer.parseInt(text);
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    private void submitSteps(String participantName, String steps) {
        openModal(0);
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText(participantName);
        driver.findElement(By.id("steps_input")).clear();
        driver.findElement(By.id("steps_input")).sendKeys(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private void reset(int buttonIndex) {
        buttonById("resetBtn", buttonIndex).click();
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.findElements(By.cssSelector("#participantsList .participant-name")).size() == 10);
    }

    private void assertDefaultState() {
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.findElements(By.cssSelector("#participantsList .participant-name")).size() == 10);
        assertEquals(10, participantNames().size());

        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(entry.getValue().intValue(), participantStepCount(entry.getKey()),
                    "Unexpected default steps for " + entry.getKey());
        }

        List<Integer> counts = participantSteps().stream()
                .map(element -> Integer.parseInt(
                        element.getText().replace(",", "").replace(" steps", "").trim()))
                .toList();
        for (int i = 0; i < counts.size() - 1; i++) {
            assertTrue(counts.get(i) >= counts.get(i + 1),
                    "Participants are not in descending step order");
        }
        assertEquals(List.of("John Smith", "David Brown", "Jill Watson"),
                participantNames().subList(0, 3).stream()
                        .map(element -> element.getText().trim())
                        .toList());
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", driver.findElement(By.cssSelector("h2")).getText());
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
        List<Integer> counts = participantSteps().stream()
                .map(element -> Integer.parseInt(
                        element.getText().replace(",", "").replace(" steps", "").trim()))
                .toList();
        assertEquals(10, counts.size());
        for (int i = 0; i < counts.size() - 1; i++) {
            assertTrue(counts.get(i) >= counts.get(i + 1));
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        for (int i = 0; i < 10; i++) {
            WebElement row = driver.findElement(By.id("participant_display" + i));
            List<WebElement> trophies = row.findElements(By.cssSelector("i.fa-trophy"));
            if (i < 3) {
                assertEquals(1, trophies.size());
                String color = trophies.get(0).getCssValue("color").replace(" ", "");
                if (i == 0) {
                    assertTrue(color.equals("rgba(255,215,0,1)") || color.equals("rgb(255,215,0)"));
                } else if (i == 1) {
                    assertTrue(color.equals("rgba(192,192,192,1)") || color.equals("rgb(192,192,192)"));
                } else {
                    assertTrue(color.contains("205,127,50") || color.contains("205, 127, 50"));
                }
            } else {
                assertTrue(trophies.isEmpty());
            }
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        openModal(0);
        assertEquals("Add Steps to Participant",
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        new WebDriverWait(driver, TIMEOUT).until(d -> stepsInput.isDisplayed());
        assertTrue(stepsInput.isDisplayed());
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector(".w3-closebtn")).isDisplayed());
        Select select = new Select(driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant", select.getFirstSelectedOption().getText());
        assertEquals(11, select.getOptions().size());
        closeModal();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        openModal(1);
        assertModalVisible();
        assertEquals("Add Steps to Participant",
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertEquals(11, new Select(driver.findElement(By.id("participant_select"))).getOptions().size());
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0)");
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        new WebDriverWait(driver, TIMEOUT).until(d -> stepsInput.isDisplayed());
        assertTrue(stepsInput.isDisplayed());
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        closeModal();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModal(0);
        closeModal();
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = participantStepCount("Mike Kid");
        submitSteps("Mike Kid", "1000");
        new WebDriverWait(driver, TIMEOUT).until(
                d -> participantStepCount("Mike Kid") == before + 1000);
        assertEquals(before + 1000, participantStepCount("Mike Kid"));
        assertEquals("none", driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    @Test
    public void addZeroSteps() throws Exception {
        openModal(0);
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        ((JavascriptExecutor) driver).executeScript("addStepsToParticipant()");
        acceptAlert("Please enter a valid number of steps");
        assertModalVisible();
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        int before = participantStepCount("Mike Kid");
        submitSteps("Mike Kid", "999999");
        new WebDriverWait(driver, TIMEOUT).until(
                d -> participantStepCount("Mike Kid") == before + 999999);
        assertEquals(before + 999999, participantStepCount("Mike Kid"));
        assertEquals("Mike Kid", participantNames().get(0).getText().trim());
        assertEquals("none", driver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModal(0);
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        acceptAlert("Please select a participant");
        assertModalVisible();
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        openModal(0);
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        driver.findElement(By.id("modal_add_steps_button")).click();
        acceptAlert("Please enter a valid number of steps");
        assertModalVisible();
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        openModal(0);
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("-500");
        driver.findElement(By.id("modal_add_steps_button")).click();
        acceptAlert("Please enter a valid number of steps");
        assertModalVisible();
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal(0);
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertFalse(input.getAttribute("value").matches(".*[A-Za-z].*"));
        assertEquals("", input.getAttribute("value"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        submitSteps("Mike Kid", "1000");
        new WebDriverWait(driver, TIMEOUT).until(d -> participantStepCount("Mike Kid") == 9500);
        submitSteps("Alex Taylor", "2000");
        new WebDriverWait(driver, TIMEOUT).until(d -> participantStepCount("Alex Taylor") == 10900);
        reset(0);
        assertDefaultState();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        submitSteps("Mike Kid", "500");
        new WebDriverWait(driver, TIMEOUT).until(d -> participantStepCount("Mike Kid") == 9000);
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        reset(1);
        assertDefaultState();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        submitSteps("Mike Kid", "9007199254740991");
        new WebDriverWait(driver, TIMEOUT).until(
                d -> participantSteps().stream().anyMatch(
                        element -> element.getText().replace(",", "").replace(" steps", "").trim()
                                .equals("9007199254749492")));
        String actual = participantSteps().stream()
                .filter(element -> element.findElement(By.xpath("..")).getText().contains("Mike Kid"))
                .findFirst()
                .orElseThrow()
                .getText();
        assertEquals("9,007,199,254,749,492 steps",
                actual,
                "The maximum-safe-integer addition should be accepted");
        assertEquals("Mike Kid", participantNames().get(0).getText().trim());
    }

    @Test
    public void decimalStepValues() throws Exception {
        openModal(0);
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("100.5");
        assertTrue((Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validity.stepMismatch", input));
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].form.requestSubmit()", input);
        assertModalVisible();
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY( DO NOT TEST)

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
