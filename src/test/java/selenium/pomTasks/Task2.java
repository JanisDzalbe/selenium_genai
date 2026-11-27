package selenium.pomTasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pomPage.FitnessChallengePage;
import selenium.utility.WebDriverUtility;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task2 {
    private WebDriver driver;
    private FitnessChallengePage page;

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
        page = new FitnessChallengePage(driver).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void acceptAlertWithText(String expectedText) {
        Alert alert = page.alert();
        assertEquals(expectedText, alert.getText());
        alert.accept();
    }

    private void assertModalElements() {
        assertTrue(page.modal().isDisplayed());
        assertEquals("Add Steps to Participant",
                page.modal().findElement(By.tagName("h3")).getText());
        assertTrue(page.modal().findElement(By.id("participant_select")).isDisplayed());
        assertTrue(page.stepsInput().isDisplayed());
        assertEquals("number", page.stepsInput().getAttribute("type"));
        assertTrue(page.modal().findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(page.modal().findElement(By.cssSelector(".w3-closebtn")).isDisplayed());
        assertEquals("×", page.modal().findElement(By.cssSelector(".w3-closebtn")).getText());
    }

    private void assertDefaultState() {
        List<WebElement> rows = page.participantRows();
        assertEquals(10, rows.size());

        List<String> names = rows.stream()
                .map(row -> row.findElement(By.cssSelector(".participant-name")).getText())
                .toList();
        assertTrue(names.containsAll(DEFAULT_STEPS.keySet()));

        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(entry.getValue(), page.stepsOf(entry.getKey()),
                    "Unexpected steps for " + entry.getKey());
        }

        assertEquals("John Smith", names.get(0));
        assertEquals("David Brown", names.get(1));
        assertEquals("Jill Watson", names.get(2));
        assertEquals("Carlos Garcia", names.get(3));
    }

    private void assertTopThreeTrophies() {
        List<WebElement> rows = page.participantRows();
        assertEquals(1, rows.get(0).findElements(By.cssSelector(".fa-trophy")).size());
        assertEquals(1, rows.get(1).findElements(By.cssSelector(".fa-trophy")).size());
        assertEquals(1, rows.get(2).findElements(By.cssSelector(".fa-trophy")).size());
        assertEquals("rgba(255, 215, 0, 1)",
                rows.get(0).findElement(By.cssSelector(".fa-trophy")).getCssValue("color"));
        assertEquals("rgba(192, 192, 192, 1)",
                rows.get(1).findElement(By.cssSelector(".fa-trophy")).getCssValue("color"));
        assertEquals("rgba(205, 127, 50, 1)",
                rows.get(2).findElement(By.cssSelector(".fa-trophy")).getCssValue("color"));
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
    }

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", page.title());
        assertEquals("Fitness Challenge", page.heading());
        assertDefaultState();
        assertTrue(page.topButton("addStepsBtn").isDisplayed());
        assertTrue(page.bottomButton("addStepsBtn").isDisplayed());
        assertTrue(page.topButton("resetBtn").isDisplayed());
        assertTrue(page.bottomButton("resetBtn").isDisplayed());
    }

    @Test
    public void rankingOrder() {
        List<WebElement> rows = page.participantRows();
        assertEquals(10, rows.size());
        int previousSteps = Integer.MAX_VALUE;
        for (WebElement row : rows) {
            String text = row.findElement(By.cssSelector(".participant-steps"))
                    .getText()
                    .replace("steps", "")
                    .replace(",", "")
                    .trim();
            int steps = Integer.parseInt(text);
            assertTrue(steps <= previousSteps, "Participants are not in descending step order");
            previousSteps = steps;
        }
    }

    @Test
    public void medalTrophyIcons() {
        assertTopThreeTrophies();
    }

    @Test
    public void openModalViaTopButton() {
        page.openModal();
        assertModalElements();
        assertEquals("Choose participant",
                page.participantSelect().getFirstSelectedOption().getText());
        assertEquals(11, page.participantSelect().getOptions().size());
    }

    @Test
    public void openModalViaBottomButton() {
        page.openModalFromBottom();
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() {
        page.openModal();
        page.closeModalWithButton();
        assertTrue(!page.modalIsOpen());
        assertDefaultState();
    }

    @Test
    public void addValidStepsToParticipant() {
        int initialSteps = page.stepsOf("Mike Kid");
        page.addSteps("Mike Kid", "1000");
        page.waitForModalToClose();
        assertEquals(initialSteps + 1000, page.stepsOf("Mike Kid"));
    }

    @Test
    public void addZeroSteps() {
        page.addSteps("Mike Kid", "0");
        acceptAlertWithText("Please enter a valid number of steps");
        assertTrue(page.modalIsOpen());
    }

    @Test
    public void addLargeNumberOfSteps() {
        page.addSteps("Mike Kid", "999999");
        page.waitForModalToClose();
        assertEquals("Mike Kid",
                page.participantRows().get(0)
                        .findElement(By.cssSelector(".participant-name"))
                        .getText());
        assertEquals(1008499, page.stepsOf("Mike Kid"));
    }

    @Test
    public void submitWithoutSelectingParticipant() {
        page.addSteps(null, "1000");
        acceptAlertWithText("Please select a participant");
        assertTrue(page.modalIsOpen());
    }

    @Test
    public void submitWithoutEnteringSteps() {
        page.addSteps("Mike Kid", "");
        acceptAlertWithText("Please enter a valid number of steps");
        assertTrue(page.modalIsOpen());
    }

    @Test
    public void submitWithNegativeSteps() {
        page.addSteps("Mike Kid", "-500");
        acceptAlertWithText("Please enter a valid number of steps");
        assertTrue(page.modalIsOpen());
    }

    @Test
    public void submitWithNonNumericInput() {
        page.openModal();
        page.participantSelect().selectByVisibleText("Mike Kid");
        page.stepsInput().sendKeys("abc");
        String value = page.stepsInput().getAttribute("value");
        assertTrue(value == null || !value.matches(".*[A-Za-z].*"));
        assertTrue(value == null || value.isEmpty());
    }

    @Test
    public void resetViaTopButton() {
        page.addSteps("Mike Kid", "1000");
        page.waitForModalToClose();
        page.addSteps("Jill Watson", "1000");
        page.waitForModalToClose();
        assertNotEquals(DEFAULT_STEPS.get("Mike Kid"), page.stepsOf("Mike Kid"));
        assertNotEquals(DEFAULT_STEPS.get("Jill Watson"), page.stepsOf("Jill Watson"));

        page.resetFromTop();
        assertDefaultState();
        assertTopThreeTrophies();
    }

    @Test
    public void resetViaBottomButton() {
        page.addSteps("Mike Kid", "1000");
        page.waitForModalToClose();
        assertNotEquals(DEFAULT_STEPS.get("Mike Kid"), page.stepsOf("Mike Kid"));

        page.resetFromBottom();
        assertDefaultState();
        assertTopThreeTrophies();
    }

    @Test
    public void maximumIntegerValue() {
        page.addSteps("Mike Kid", "9007199254740991");
        page.waitForModalToClose();

        Object storedSteps = page.executeScript(
                "return JSON.parse(localStorage.getItem('participant0')).steps;");
        assertTrue(storedSteps instanceof Number
                && ((Number) storedSteps).longValue() >= 9007199254740991L);
        assertEquals("Mike Kid", page.participantRows().get(0)
                .findElement(By.cssSelector(".participant-name")).getText());
    }

    @Test
    public void decimalStepValues() {
        page.openModal();
        page.participantSelect().selectByVisibleText("Mike Kid");
        page.stepsInput().sendKeys("100.5");

        Boolean stepMismatch = (Boolean) page.executeScript(
                "return arguments[0].validity.stepMismatch;", page.stepsInput());
        assertTrue(Boolean.TRUE.equals(stepMismatch));

        page.modal().findElement(By.id("modal_add_steps_button")).click();
        page.waitForModalToClose();
        assertEquals(8600, page.stepsOf("Mike Kid"));
    }

    @Test
    public void chromeBrowser() throws Exception {
        // TODO: Execute representative fitness challenge tests in Chrome.
    }

    @Test
    public void edgeBrowser() throws Exception {
        // TODO: Execute representative fitness challenge tests in Edge.
    }

    @Test
    public void firefoxBrowser() throws Exception {
        // TODO: Execute representative fitness challenge tests in Firefox.
    }
}
