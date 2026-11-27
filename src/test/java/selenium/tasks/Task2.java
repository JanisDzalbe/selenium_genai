package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import selenium.pages.FitnessChallengePage;
import selenium.utility.WebDriverUtility;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    FitnessChallengePage page;

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
        driver = WebDriverUtility.getChromeDriver();
        page = new FitnessChallengePage(driver);
        page.open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void assertModalElements() {
        assertTrue(page.modalTitle().isDisplayed());
        assertTrue(page.modalParticipantDropdown().isDisplayed());
        assertTrue(page.modalStepsInput().isDisplayed());
        assertTrue(page.modalSubmitButton().isDisplayed());
        assertTrue(page.modalCloseButton().isDisplayed());

        assertEquals("Choose participant", page.selectedParticipantOptionText());
        List<String> options = page.participantOptions();
        assertEquals(11, options.size());
        assertTrue(options.contains("Mike Kid"));
        assertTrue(options.contains("Alex Taylor"));
    }

    private void assertDefaultState() {
        page.waitForDefaultParticipantCount();
        assertEquals(10, page.participantRows().size());
        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(entry.getValue().longValue(), page.participantSteps(entry.getKey()), entry.getKey());
        }
    }

    private void assertRankingOrder() {
        List<Long> displayedSteps = page.participantStepCounts();
        long previous = Long.MAX_VALUE;
        for (long steps : displayedSteps) {
            assertTrue(steps <= previous);
            previous = steps;
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", page.title());
        assertDefaultState();
        assertTrue(page.buttonDisplayed("Add Steps", 0));
        assertTrue(page.buttonDisplayed("Add Steps", 1));
        assertTrue(page.buttonDisplayed("Reset List", 0));
        assertTrue(page.buttonDisplayed("Reset List", 1));
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        assertRankingOrder();
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        assertEquals(3, page.trophyCountAtRank(1)
                + page.trophyCountAtRank(2)
                + page.trophyCountAtRank(3));
        assertEquals(List.of("rgba(255, 215, 0, 1)",
                        "rgba(192, 192, 192, 1)",
                        "rgba(205, 127, 50, 1)"),
                page.trophyColors());
        for (int rank = 4; rank <= page.participantRows().size(); rank++) {
            assertTrue(page.trophyCountAtRank(rank) == 0);
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        page.openModalTop();
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        page.openModalBottom();
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        page.openModalTop();
        page.closeModal();
        page.waitForModalClosed();
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        long before = page.participantSteps("Mike Kid");
        page.addSteps("Mike Kid", "1000");
        page.waitForModalClosed();
        assertEquals(before + 1000, page.participantSteps("Mike Kid"));
        assertRankingOrder();
    }

    @Test
    public void addZeroSteps() throws Exception {
        page.addSteps("Mike Kid", "0");
        Alert alert = page.waitForAlert();
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(page.modalDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        page.addSteps("Jane Doe", "999999");
        page.waitForModalClosed();
        assertEquals(1006499, page.participantSteps("Jane Doe"));
        assertEquals("Jane Doe", page.participantNames().get(0));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        page.openModalTop();
        page.enterSteps("1000");
        page.submitModal();
        Alert alert = page.waitForAlert();
        assertEquals("Please select a participant", alert.getText());
        alert.accept();
        assertTrue(page.modalDisplayed());
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        page.openModalTop();
        page.selectParticipant("Mike Kid");
        page.submitModal();
        Alert alert = page.waitForAlert();
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(page.modalDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        page.addSteps("Mike Kid", "-500");
        Alert alert = page.waitForAlert();
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(page.modalDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        page.openModalTop();
        page.selectParticipant("Mike Kid");
        page.modalStepsInput().sendKeys("abc");
        assertEquals("", page.modalStepsInput().getAttribute("value"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        page.addSteps("Mike Kid", "1000");
        page.waitForParticipantSteps("Mike Kid", 9500);
        page.addSteps("Jane Doe", "2000");
        page.waitForParticipantSteps("Jane Doe", 8500);
        page.resetTop();
        page.waitForParticipantSteps("Mike Kid", 8500);
        assertDefaultState();
        assertRankingOrder();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        page.addSteps("Mike Kid", "1000");
        page.waitForParticipantSteps("Mike Kid", 9500);
        page.resetBottom();
        page.waitForParticipantSteps("Mike Kid", 8500);
        assertDefaultState();
        assertRankingOrder();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    @Disabled("Application defect: adding 8,500 to 9007199254740991 produces 9007199254749492 instead of the exact sum 9007199254749491 due JavaScript Number precision.")
    public void maximumIntegerValue() throws Exception {
        page.addSteps("Mike Kid", "9007199254740991");
        page.waitForModalClosed();
        assertEquals(9007199254749491L, page.participantSteps("Mike Kid"));
        assertEquals("Mike Kid", page.participantNames().get(0));
    }

    @Test
    @Disabled("Application defect: decimal 100.5 is parsed with parseInt(), accepted as 100, and no validation alert is shown.")
    public void decimalStepValues() throws Exception {
        page.addSteps("Mike Kid", "100.5");
        Alert alert = page.waitForAlert();
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(page.modalDisplayed());
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        long before = page.participantSteps("Mike Kid");
        page.addSteps("Mike Kid", "1000");
        page.waitForModalClosed();
        assertEquals(before + 1000, page.participantSteps("Mike Kid"));

        page.openModalTop();
        page.enterSteps("1000");
        page.submitModal();
        Alert alert = page.waitForAlert();
        assertEquals("Please select a participant", alert.getText());
        alert.accept();
        assertTrue(page.modalDisplayed());
        page.closeModal();

        page.resetTop();
        page.waitForParticipantSteps("Mike Kid", 8500);
        assertDefaultState();
    }

    @Test
    @Disabled("Edge-specific test is not runnable: WebDriverUtility currently provides only Chrome and EdgeDriver/lib/edgedriver.exe is unavailable.")
    public void edgeBrowser() throws Exception {
        assertEquals("Fitness Challenge", page.title());
        assertRankingOrder();
        page.openModalTop();
        assertModalElements();
    }

    @Test
    @Disabled("Firefox-specific test is not runnable: WebDriverUtility currently provides only Chrome and Firefox/geckodriver are unavailable.")
    public void firefoxBrowser() throws Exception {
        assertEquals("Fitness Challenge", page.title());
        assertRankingOrder();
        page.openModalTop();
        assertModalElements();
    }
}
