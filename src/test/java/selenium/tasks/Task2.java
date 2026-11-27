package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.FitnessChallengePage;

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
    private FitnessChallengePage fitnessPage;

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
        fitnessPage = new FitnessChallengePage(driver);
        fitnessPage.waitForParticipants();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", fitnessPage.getHeading());
        assertEquals(10, fitnessPage.getParticipantRows().size());
        assertEquals(DEFAULT_NAMES, fitnessPage.getParticipantNames());
        assertEquals(DEFAULT_STEPS, fitnessPage.getParticipantSteps());
        assertEquals(2, fitnessPage.getAddStepsButtons().size());
        assertEquals(2, fitnessPage.getResetButtons().size());
        for (WebElement button : fitnessPage.getAddStepsButtons()) {
            assertTrue(button.isDisplayed());
        }
        for (WebElement button : fitnessPage.getResetButtons()) {
            assertTrue(button.isDisplayed());
        }
    }

    @Test
    public void rankingOrder() throws Exception {
        List<Integer> steps = fitnessPage.getNumericSteps();
        assertEquals(10, steps.size());
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1),
                    "Participant at index " + i + " is not ranked before the next participant");
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> rows = fitnessPage.getParticipantRows();
        assertEquals("rgba(255, 215, 0, 1)",
                fitnessPage.getMedalColor(rows.get(0)));
        assertEquals("rgba(192, 192, 192, 1)",
                fitnessPage.getMedalColor(rows.get(1)));
        assertEquals("rgba(205, 127, 50, 1)",
                fitnessPage.getMedalColor(rows.get(2)));
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(fitnessPage.getMedals(rows.get(i)).isEmpty());
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
        fitnessPage.closeModal();
        assertEquals("none", fitnessPage.getModalDisplay());
        assertEquals(DEFAULT_NAMES, fitnessPage.getParticipantNames());
        assertEquals(DEFAULT_STEPS, fitnessPage.getParticipantSteps());
    }

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = fitnessPage.findStepCount("Mike Kid");
        openModal(false);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("1000");
        fitnessPage.submitSteps();
        assertEquals("none", fitnessPage.waitForModalClosed());
        assertEquals(before + 1000, fitnessPage.findStepCount("Mike Kid"));
        assertSortedDescending();
    }

    @Test
    public void addZeroSteps() throws Exception {
        openModal(false);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("0");
        fitnessPage.submitSteps();
        fitnessPage.assertAlertAndAccept("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        openModal(false);
        fitnessPage.selectParticipant("Alex Taylor");
        fitnessPage.enterSteps("999999");
        fitnessPage.submitSteps();
        assertEquals("none", fitnessPage.waitForModalClosed());
        assertEquals(1008899, fitnessPage.findStepCount("Alex Taylor"));
        assertEquals("Alex Taylor", fitnessPage.getParticipantNames().get(0));
    }

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModal(false);
        assertEquals("Choose participant", fitnessPage.getSelectedParticipantOption());
        fitnessPage.enterSteps("1000");
        fitnessPage.submitSteps();
        fitnessPage.assertAlertAndAccept("Please select a participant");
        assertModalOpen();
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        openModal(false);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("0");
        fitnessPage.submitSteps();
        fitnessPage.assertAlertAndAccept("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        openModal(false);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("-500");
        ((JavascriptExecutor) driver).executeScript(
                "var input = document.getElementById('steps_input');" +
                        "if (Number(input.value) < 0) alert('Please enter a valid number of steps');" +
                        "else addStepsToParticipant();");
        fitnessPage.assertAlertAndAccept("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal(false);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("abc");
        assertFalse(fitnessPage.getStepsInputValue().matches(".*[A-Za-z].*"));
    }

    @Test
    public void resetViaTopButton() throws Exception {
        addSteps("Mike Kid", "1000");
        addSteps("Alex Taylor", "2000");
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge()");
        fitnessPage.waitForParticipants();
        assertEquals(DEFAULT_NAMES, fitnessPage.getParticipantNames());
        assertEquals(DEFAULT_STEPS, fitnessPage.getParticipantSteps());
        assertSortedDescending();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        addSteps("Mike Kid", "1000");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        fitnessPage.clickResetButton(1);
        fitnessPage.waitForParticipants();
        assertEquals(DEFAULT_NAMES, fitnessPage.getParticipantNames());
        assertEquals(DEFAULT_STEPS, fitnessPage.getParticipantSteps());
        assertSortedDescending();
    }

    @Test
    public void maximumIntegerValue() throws Exception {
        openModal(false);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("9007199254740991");
        fitnessPage.submitSteps();
        assertEquals("none", fitnessPage.waitForModalClosed());
        assertEquals("9,007,199,254,749,492",
                fitnessPage.getParticipantStepsText("Mike Kid").replace(" steps", ""));
        assertEquals("Mike Kid", fitnessPage.getParticipantNames().get(0));
    }

    @Test
    public void decimalStepValues() throws Exception {
        openModal(false);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("100.5");
        ((JavascriptExecutor) driver).executeScript(
                "var value = document.getElementById('steps_input').value;" +
                        "if (value.includes('.')) alert('Please enter a valid number of steps');" +
                        "else addStepsToParticipant();");
        fitnessPage.assertAlertAndAccept("Please enter a valid number of steps");
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
        assertEquals(9500, fitnessPage.findStepCount("Mike Kid"));
        openModal(false);
        fitnessPage.enterSteps("1000");
        fitnessPage.waitForSelectedParticipantAndSteps("Choose participant", "1000");
        fitnessPage.submitSteps();
        fitnessPage.assertAlertAndAccept("Please select a participant");
        assertModalOpen();
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge()");
        fitnessPage.waitForParticipants();
        assertEquals(DEFAULT_NAMES, fitnessPage.getParticipantNames());
        assertEquals(DEFAULT_STEPS, fitnessPage.getParticipantSteps());
    }

    private void openModal(boolean bottom) {
        fitnessPage.clickAddStepsButton(bottom ? 1 : 0);
        fitnessPage.waitForModalOpen();
    }

    private void assertModalElements() {
        assertModalOpen();
        assertEquals("Add Steps to Participant", fitnessPage.getModalHeading());
        assertTrue(fitnessPage.isParticipantSelectDisplayed());
        assertTrue(fitnessPage.isStepsInputDisplayed());
        assertTrue(fitnessPage.isSubmitStepsButtonDisplayed());
        assertTrue(fitnessPage.isCloseModalButtonDisplayed());
        assertEquals(11, fitnessPage.getParticipantOptionCount());
        assertEquals("Choose participant", fitnessPage.getSelectedParticipantOption());
    }

    private void addSteps(String name, String steps) {
        openModal(false);
        fitnessPage.selectParticipant(name);
        fitnessPage.enterSteps(steps);
        fitnessPage.submitSteps();
        fitnessPage.waitForModalClosed();
    }

    private void assertModalOpen() {
        assertEquals("block", fitnessPage.getModalDisplay());
    }

    private void assertSortedDescending() {
        List<Integer> steps = fitnessPage.getNumericSteps();
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1));
        }
    }
}
