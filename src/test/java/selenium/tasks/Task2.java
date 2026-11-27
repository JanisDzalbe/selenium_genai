package selenium.tasks;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import selenium.pages.FitnessChallengePage;
import selenium.utility.WebDriverUtils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task2 {
    private WebDriver driver;
    private FitnessChallengePage fitnessPage;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtils.createChromeDriver();
        fitnessPage = new FitnessChallengePage(driver);
        fitnessPage.openAndReset();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        fitnessPage = null;
    }

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", fitnessPage.getTitle());
        assertEquals(10, fitnessPage.getParticipantItems().size());

        assertAll(
                () -> assertEquals("Mike Kid", fitnessPage.getParticipantName("Mike Kid")),
                () -> assertEquals("Jill Watson", fitnessPage.getParticipantName("Jill Watson")),
                () -> assertEquals("Jane Doe", fitnessPage.getParticipantName("Jane Doe")),
                () -> assertEquals("John Smith", fitnessPage.getParticipantName("John Smith")),
                () -> assertEquals("Sarah Johnson", fitnessPage.getParticipantName("Sarah Johnson")),
                () -> assertEquals("Carlos Garcia", fitnessPage.getParticipantName("Carlos Garcia")),
                () -> assertEquals("Emily Chen", fitnessPage.getParticipantName("Emily Chen")),
                () -> assertEquals("David Brown", fitnessPage.getParticipantName("David Brown")),
                () -> assertEquals("Maria Rodriguez", fitnessPage.getParticipantName("Maria Rodriguez")),
                () -> assertEquals("Alex Taylor", fitnessPage.getParticipantName("Alex Taylor")));
        assertDefaultSteps();
        assertEquals(2, fitnessPage.getButtonCount("addStepsBtn"));
        assertEquals(2, fitnessPage.getButtonCount("resetBtn"));
        assertEquals("Add Steps", fitnessPage.getTopButtonText("addStepsBtn"));
        assertEquals("Add Steps", fitnessPage.getBottomButtonText("addStepsBtn"));
        assertEquals("Reset List", fitnessPage.getTopButtonText("resetBtn"));
        assertEquals("Reset List", fitnessPage.getBottomButtonText("resetBtn"));
        assertTrue(fitnessPage.areButtonsDisplayed("addStepsBtn"));
        assertTrue(fitnessPage.areButtonsDisplayed("resetBtn"));
    }

    @Test
    public void rankingOrder() throws Exception {
        List<WebElement> items = fitnessPage.getParticipantItems();
        assertEquals(10, items.size());
        long previousSteps = Long.MAX_VALUE;
        for (WebElement item : items) {
            long steps = fitnessPage.getItemSteps(item);
            assertTrue(steps <= previousSteps);
            previousSteps = steps;
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> items = fitnessPage.getParticipantItems();
        assertEquals(1, fitnessPage.getTrophyCount(0));
        assertEquals("rgba(255, 215, 0, 1)", fitnessPage.getTrophyColor(0));
        assertEquals("rgba(192, 192, 192, 1)", fitnessPage.getTrophyColor(1));
        assertEquals("rgba(205, 127, 50, 1)", fitnessPage.getTrophyColor(2));
        for (int i = 3; i < items.size(); i++) {
            assertFalse(fitnessPage.hasTrophy(i));
        }
    }

    @Test
    public void openModalViaTopButton() throws Exception {
        fitnessPage.getTopButton("addStepsBtn").click();
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        fitnessPage.scrollToBottomButton("addStepsBtn");
        fitnessPage.getBottomButton("addStepsBtn").click();
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        fitnessPage.openModal();
        fitnessPage.closeModal();
        fitnessPage.waitForModalClosed();
        assertDefaultSteps();
    }

    @Test
    public void addValidStepsToParticipant() throws Exception {
        long before = fitnessPage.getParticipantSteps("Mike Kid");
        fitnessPage.addSteps("Mike Kid", "1000");
        fitnessPage.waitForModalClosed();
        assertEquals(before + 1000, fitnessPage.getParticipantSteps("Mike Kid"));
        assertRankingDescending();
    }

    @Test
    public void addZeroSteps() throws Exception {
        fitnessPage.openModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("0");
        fitnessPage.submitModal();
        assertEquals("Please enter a valid number of steps",
                fitnessPage.acceptAlert("Please enter a valid number of steps"));
        fitnessPage.waitForModalOpen();
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        fitnessPage.addSteps("Mike Kid", "999999");
        fitnessPage.waitForModalClosed();
        assertEquals(1008499, fitnessPage.getParticipantSteps("Mike Kid"));
        assertEquals("Mike Kid",
                fitnessPage.getItemName(fitnessPage.getParticipantItems().get(0)));
    }

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        fitnessPage.openModal();
        fitnessPage.enterSteps("1000");
        fitnessPage.submitModal();
        assertEquals("Please select a participant",
                fitnessPage.acceptAlert("Please select a participant"));
        fitnessPage.waitForModalOpen();
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        fitnessPage.openModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.submitModal();
        assertEquals("Please enter a valid number of steps",
                fitnessPage.acceptAlert("Please enter a valid number of steps"));
        fitnessPage.waitForModalOpen();
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        fitnessPage.openModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("-500");
        fitnessPage.submitModal();
        assertEquals("Please enter a valid number of steps",
                fitnessPage.acceptAlert("Please enter a valid number of steps"));
        fitnessPage.waitForModalOpen();
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        fitnessPage.openModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("abc");
        assertEquals("number", fitnessPage.getStepsInputType());
        assertFalse(fitnessPage.getStepsInputValue().matches(".*[A-Za-z]+.*"));
    }

    @Test
    public void resetViaTopButton() throws Exception {
        fitnessPage.addSteps("Mike Kid", "1000");
        fitnessPage.addSteps("Jane Doe", "2000");
        fitnessPage.getTopButton("resetBtn").click();
        fitnessPage.waitForReset();
        assertDefaultSteps();
        assertRankingDescending();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        fitnessPage.addSteps("Mike Kid", "1000");
        fitnessPage.scrollToBottomButton("resetBtn");
        fitnessPage.getBottomButton("resetBtn").click();
        fitnessPage.waitForReset();
        assertDefaultSteps();
        assertRankingDescending();
    }

    @Test
    public void maximumIntegerValue() throws Exception {
        fitnessPage.openModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("9007199254740991");
        fitnessPage.submitModal();
        fitnessPage.waitForModalClosed();
        assertTrue(fitnessPage.getParticipantSteps("Mike Kid") > 0);
    }

    @Test
    public void decimalStepValues() throws Exception {
        fitnessPage.openModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("100.5");
        assertEquals("100.5", fitnessPage.getStepsInputValue());
        assertTrue(fitnessPage.isStepsInputMismatch());
        fitnessPage.waitForModalOpen();
    }

    @Test
    public void chromeBrowser() throws Exception {
        verifyCrossBrowserFeatures();
    }

    @Test
    public void edgeBrowser() throws Exception {
        replaceDriver(WebDriverUtils.createEdgeDriver());
        verifyCrossBrowserFeatures();
    }

    @Test
    public void firefoxBrowser() throws Exception {
        replaceDriver(new FirefoxDriver());
        verifyCrossBrowserFeatures();
    }

    private void replaceDriver(WebDriver replacement) {
        if (driver != null) {
            driver.quit();
        }
        driver = replacement;
        fitnessPage = new FitnessChallengePage(driver);
        fitnessPage.openAndReset();
    }

    private void assertDefaultSteps() {
        fitnessPage.waitForReset();
        assertEquals(fitnessPage.getDefaultParticipants(), fitnessPage.readParticipantSteps());
    }

    private void assertRankingDescending() {
        long previous = Long.MAX_VALUE;
        for (WebElement item : fitnessPage.getParticipantItems()) {
            long steps = fitnessPage.getItemSteps(item);
            assertTrue(steps <= previous);
            previous = steps;
        }
    }

    private void assertModalElements() {
        fitnessPage.waitForModalOpen();
        assertEquals("Add Steps to Participant", fitnessPage.getModalTitle());
        assertEquals("Choose participant", fitnessPage.getDefaultModalSelection());
        assertEquals(11, fitnessPage.getParticipantOptionCount());
        assertEquals("number", fitnessPage.getStepsInputType());
        assertTrue(fitnessPage.isModalSubmitDisplayed());
        assertTrue(fitnessPage.isModalCloseDisplayed());
    }

    private void verifyCrossBrowserFeatures() throws Exception {
        fitnessPage.addSteps("Mike Kid", "1000");
        fitnessPage.waitForModalClosed();
        assertEquals(9500, fitnessPage.getParticipantSteps("Mike Kid"));

        fitnessPage.openModal();
        fitnessPage.enterSteps("1000");
        fitnessPage.submitModal();
        assertEquals("Please select a participant",
                fitnessPage.acceptAlert("Please select a participant"));
        fitnessPage.waitForModalOpen();

        fitnessPage.closeModal();
        fitnessPage.waitForModalClosed();

        fitnessPage.getTopButton("resetBtn").click();
        fitnessPage.waitForReset();
        assertDefaultSteps();
    }
}
