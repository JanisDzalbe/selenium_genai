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
import selenium.utility.ChromeDriverFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        wait.until(d -> d.findElements(By.cssSelector("#participantsList li")).size() == 10);
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
        assertEquals(10, getParticipants().size());
        assertEquals(2, driver.findElements(By.cssSelector("button[id='addStepsBtn']")).size());
        assertEquals(2, driver.findElements(By.cssSelector("button[id='resetBtn']")).size());
        for (WebElement button : driver.findElements(By.cssSelector("button[id='addStepsBtn'], button[id='resetBtn']"))) {
            assertTrue(button.isDisplayed());
        }
        Map<String, Integer> displayed = getParticipantSteps();
        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertTrue(displayed.containsKey(entry.getKey()), "Missing participant: " + entry.getKey());
            assertEquals(entry.getValue(), displayed.get(entry.getKey()), "Incorrect steps for " + entry.getKey());
        }
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        List<Integer> counts = getParticipantCountsInOrder();
        assertEquals(10, counts.size());
        for (int i = 1; i < counts.size(); i++) {
            assertTrue(counts.get(i - 1) >= counts.get(i),
                    "Participants are not in descending step-count order");
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> participants = getParticipants();
        assertTrophy(participants.get(0), "gold");
        assertTrophy(participants.get(1), "silver");
        assertTrophy(participants.get(2), "bronze");
        for (int i = 3; i < participants.size(); i++) {
            assertTrue(participants.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Participant ranked " + (i + 1) + " unexpectedly has a trophy");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        openModal(false);
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        openModal(true);
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModal(false);
        closeModal();
        assertEquals(DEFAULT_STEPS, getParticipantSteps());
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = getParticipantSteps().get("Mike Kid");
        openModal(false);
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertFalse(isModalVisible());
        wait.until(d -> getParticipantSteps().get("Mike Kid") == before + 1000);
        assertEquals(before + 1000, getParticipantSteps().get("Mike Kid"));
        assertSorted();
    }

    @Test
    public void addZeroSteps() throws Exception {
        openModal(false);
        selectFirstParticipant();
        driver.findElement(By.id("steps_input")).sendKeys("0");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please enter a valid number of steps");
        assertTrue(isModalVisible());
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        openModal(false);
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText("Alex Taylor");
        int before = getParticipantSteps().get("Alex Taylor");
        driver.findElement(By.id("steps_input")).sendKeys("999999");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertFalse(isModalVisible());
        wait.until(d -> getParticipantSteps().get("Alex Taylor") == before + 999999);
        assertEquals(before + 999999, getParticipantSteps().get("Alex Taylor"));
        assertEquals("Alex Taylor", getParticipantNameAt(0));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModal(false);
        assertEquals("Choose participant",
                new Select(driver.findElement(By.id("participant_select"))).getFirstSelectedOption().getText());
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please select a participant");
        assertTrue(isModalVisible());
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        openModal(false);
        selectFirstParticipant();
        driver.findElement(By.id("steps_input")).sendKeys("0");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please enter a valid number of steps");
        assertTrue(isModalVisible());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        openModal(false);
        selectFirstParticipant();
        driver.findElement(By.id("steps_input")).sendKeys("-500");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertAlert("Please enter a valid number of steps");
        assertTrue(isModalVisible());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal(false);
        selectFirstParticipant();
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertEquals("", input.getAttribute("value"));
        assertFalse(input.getAttribute("value").matches(".*[A-Za-z].*"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        addSteps("Mike Kid", 1000);
        addSteps("Jill Watson", 2000);
        driver.findElements(By.cssSelector("button[id='resetBtn']")).get(0).click();
        wait.until(d -> getParticipantSteps().equals(DEFAULT_STEPS));
        assertEquals(DEFAULT_STEPS, getParticipantSteps());
        assertSorted();
        assertDefaultTopThreeTrophies();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        addSteps("Mike Kid", 1000);
        openModal(false);
        closeModal();
        List<WebElement> resetButtons = driver.findElements(By.cssSelector("button[id='resetBtn']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", resetButtons.get(1));
        resetButtons.get(1).click();
        wait.until(d -> getParticipantSteps().equals(DEFAULT_STEPS));
        assertEquals(DEFAULT_STEPS, getParticipantSteps());
        assertSorted();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        openModal(false);
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("9007199254740991");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertFalse(isModalVisible());
        wait.until(d -> getParticipantNameAt(0).equals("Mike Kid"));
        double expected = 8500d + 9007199254740991d;
        assertEquals(expected, (double) getLongParticipantSteps("Mike Kid"));
    }

    @Test
    public void decimalStepValues() throws Exception {
        openModal(false);
        selectFirstParticipant();
        Map<String, Integer> stepsBefore = getParticipantSteps();
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("100.5");
        assertEquals("100.5", input.getAttribute("value"));
        assertTrue((Boolean) ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].validity.stepMismatch;", input),
                "The number input should reject fractional steps because its default step is 1");
        assertFalse((Boolean) ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].reportValidity();", input),
                "The browser should reject a fractional step value");
        assertTrue(isModalVisible());
        assertEquals(stepsBefore, getParticipantSteps(), "Invalid decimal steps must not change participant data");
    }

    private List<WebElement> getParticipants() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private Map<String, Integer> getParticipantSteps() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (WebElement participant : getParticipants()) {
            String name = participant.findElement(By.cssSelector(".participant-name")).getText();
            String stepsText = participant.findElement(By.cssSelector(".participant-steps"))
                    .getText().replace(" steps", "").replace(",", "").trim();
            result.put(name, Integer.parseInt(stepsText));
        }
        return result;
    }

    private long getLongParticipantSteps(String name) {
        for (WebElement participant : getParticipants()) {
            if (participant.findElement(By.cssSelector(".participant-name")).getText().equals(name)) {
                String stepsText = participant.findElement(By.cssSelector(".participant-steps"))
                        .getText().replace(" steps", "").replace(",", "").trim();
                return Long.parseLong(stepsText);
            }
        }
        fail("Participant not found: " + name);
        return 0;
    }

    private List<Integer> getParticipantCountsInOrder() {
        List<Integer> counts = new ArrayList<>();
        for (WebElement participant : getParticipants()) {
            String stepsText = participant.findElement(By.cssSelector(".participant-steps"))
                    .getText().replace(" steps", "").replace(",", "").trim();
            counts.add(Integer.parseInt(stepsText));
        }
        return counts;
    }

    private String getParticipantNameAt(int index) {
        return getParticipants().get(index).findElement(By.cssSelector(".participant-name")).getText();
    }

    private void openModal(boolean bottom) {
        List<WebElement> buttons = driver.findElements(By.cssSelector("button[id='addStepsBtn']"));
        WebElement button = bottom ? buttons.get(1) : buttons.get(0);
        if (bottom) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        }
        button.click();
        wait.until(d -> isModalVisible());
    }

    private boolean isModalVisible() {
        return "block".equals(driver.findElement(By.id("addStepsModal"))
                .getCssValue("display"));
    }

    private void closeModal() {
        WebElement closeButton = driver.findElement(By.cssSelector("span.w3-closebtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
        wait.until(d -> !isModalVisible());
    }

    private void assertModalElements() {
        assertTrue(isModalVisible());
        assertEquals("Add Steps to Participant",
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(driver.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("span.w3-closebtn")).isDisplayed());
        assertEquals(11,
                new Select(driver.findElement(By.id("participant_select"))).getOptions().size());
        assertEquals("Choose participant",
                new Select(driver.findElement(By.id("participant_select"))).getFirstSelectedOption().getText());
    }

    private void selectFirstParticipant() {
        Select select = new Select(driver.findElement(By.id("participant_select")));
        select.selectByIndex(1);
    }

    private void assertAlert(String expectedText) {
        Alert alert;
        try {
            alert = driver.switchTo().alert();
        } catch (org.openqa.selenium.NoAlertPresentException ex) {
            fail("Expected alert with text \"" + expectedText + "\", but no alert was displayed", ex);
            return;
        }
        assertEquals(expectedText, alert.getText());
        alert.accept();
    }

    private void assertSorted() {
        List<Integer> counts = getParticipantCountsInOrder();
        for (int i = 1; i < counts.size(); i++) {
            assertTrue(counts.get(i - 1) >= counts.get(i));
        }
    }

    private void assertTrophy(WebElement participant, String place) {
        List<WebElement> trophies = participant.findElements(By.cssSelector("i.fa-trophy"));
        assertEquals(1, trophies.size());
        String color = trophies.get(0).getCssValue("color").replace(" ", "");
        if ("gold".equals(place)) {
            assertTrue(color.contains("255,215,0"), "Unexpected gold color: " + color);
        } else if ("silver".equals(place)) {
            assertTrue(color.contains("192,192,192"), "Unexpected silver color: " + color);
        } else {
            assertTrue(color.contains("205,127,50"), "Unexpected bronze color: " + color);
        }
    }

    private void assertDefaultTopThreeTrophies() {
        List<WebElement> participants = getParticipants();
        assertEquals("John Smith", getParticipantNameAt(0));
        assertEquals("David Brown", getParticipantNameAt(1));
        assertEquals("Jill Watson", getParticipantNameAt(2));
        assertTrophy(participants.get(0), "gold");
        assertTrophy(participants.get(1), "silver");
        assertTrophy(participants.get(2), "bronze");
    }

    private void addSteps(String participantName, int steps) {
        openModal(false);
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(participantName);
        driver.findElement(By.id("steps_input")).sendKeys(String.valueOf(steps));
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertFalse(isModalVisible());
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
