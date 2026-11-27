package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.FitnessChallengePage;
import selenium.utility.ChromeDriverUtility;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private WebDriver driver;
    private FitnessChallengePage fitnessChallengePage;
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
        driver = ChromeDriverUtility.createChromeDriver();
        fitnessChallengePage = new FitnessChallengePage(driver);
        fitnessChallengePage.open();
        fitnessChallengePage.resetList();
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
        assertEquals("Fitness Challenge", fitnessChallengePage.getTitle());
        assertEquals("Fitness Challenge", fitnessChallengePage.getHeading());
        assertEquals(10, fitnessChallengePage.waitForParticipants(10).size());

        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(FitnessChallengePage.formatSteps(entry.getValue()),
                    fitnessChallengePage.getParticipantSteps(entry.getKey()));
        }

        assertEquals(2, fitnessChallengePage.getAddStepsButtons().size());
        assertEquals(2, fitnessChallengePage.getResetButtons().size());
        assertTrue(fitnessChallengePage.areMainAddAndResetButtonsVisible());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        assertTrue(fitnessChallengePage.isParticipantRankingDescending());
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        assertEquals(1, fitnessChallengePage.getTrophyCount(0));
        assertEquals(1, fitnessChallengePage.getTrophyCount(1));
        assertEquals(1, fitnessChallengePage.getTrophyCount(2));

        assertEquals("rgba(255, 215, 0, 1)",
                fitnessChallengePage.getTrophyColor(0));
        assertEquals("rgba(192, 192, 192, 1)",
                fitnessChallengePage.getTrophyColor(1));
        assertEquals("rgba(205, 127, 50, 1)",
                fitnessChallengePage.getTrophyColor(2));
        for (int i = 3; i < fitnessChallengePage.getParticipantElements().size(); i++) {
            assertEquals(0, fitnessChallengePage.getTrophyCount(i));
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        fitnessChallengePage.openModal(false);
        assertModalElements();
        assertEquals("Choose participant",
                fitnessChallengePage.getDefaultDropdownText());
        assertEquals(11,
                fitnessChallengePage.getParticipantDropdownOptionCount());
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        fitnessChallengePage.openModal(true);
        assertModalElements();
        assertEquals(11,
                fitnessChallengePage.getParticipantDropdownOptionCount());
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.closeModalWithCloseButton();

        assertFalse(fitnessChallengePage.isModalOpen());
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        int before = fitnessChallengePage.getParticipantStepCount("Mike Kid");
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("1000");
        fitnessChallengePage.submitSteps();

        fitnessChallengePage.waitForModalClosed();
        assertEquals(FitnessChallengePage.formatSteps(before + 1000),
                fitnessChallengePage.getParticipantSteps("Mike Kid"));
        assertTrue(fitnessChallengePage.isParticipantRankingDescending());
    }

    @Test
    public void addZeroSteps() throws Exception {
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("0");
        fitnessChallengePage.submitSteps();

        fitnessChallengePage.acceptAlert("Please enter a valid number of steps");
        assertTrue(fitnessChallengePage.isModalOpen());
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        String participantName = "Mike Kid";
        int before = fitnessChallengePage.getParticipantStepCount(participantName);
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant(participantName);
        fitnessChallengePage.enterSteps("999999");
        fitnessChallengePage.submitSteps();

        fitnessChallengePage.waitForModalClosed();
        assertEquals(FitnessChallengePage.formatSteps(before + 999999),
                fitnessChallengePage.getParticipantSteps(participantName));
        assertEquals(participantName,
                fitnessChallengePage.getFirstParticipantName());
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.enterSteps("1000");
        fitnessChallengePage.submitSteps();

        fitnessChallengePage.acceptAlert("Please select a participant");
        assertTrue(fitnessChallengePage.isModalOpen());
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("0");
        fitnessChallengePage.submitSteps();

        fitnessChallengePage.acceptAlert("Please enter a valid number of steps");
        assertTrue(fitnessChallengePage.isModalOpen());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("-500");
        fitnessChallengePage.submitSteps();

        fitnessChallengePage.acceptAlert("Please enter a valid number of steps");
        assertTrue(fitnessChallengePage.isModalOpen());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("abc");

        assertFalse(fitnessChallengePage.getStepsInputValue()
                .matches(".*[A-Za-z].*"));
        assertTrue(fitnessChallengePage.getStepsInputValue().isEmpty());
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        addSteps("Mike Kid", "1000");
        addSteps("Jill Watson", "2000");

        fitnessChallengePage.resetViaTopButton();
        assertDefaultState();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        addSteps("Mike Kid", "1000");
        fitnessChallengePage.resetViaBottomButton();
        assertDefaultState();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        BigInteger before = fitnessChallengePage
                .getParticipantStepCountBigInteger("Mike Kid");
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("9007199254740991");
        fitnessChallengePage.submitSteps();
        fitnessChallengePage.waitForModalClosed();

        BigInteger updated = fitnessChallengePage
                .getParticipantStepCountBigInteger("Mike Kid");
        assertEquals("9,007,199,254,749,492",
                FitnessChallengePage.formatSteps(updated));
        assertTrue(updated.compareTo(before) > 0);
        assertEquals("Mike Kid",
                fitnessChallengePage.getFirstParticipantName());
    }

    @Test
    public void decimalStepValues() throws Exception {
        int stepCountBefore =
                fitnessChallengePage.getParticipantStepCount("Mike Kid");
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("100.5");
        fitnessChallengePage.submitSteps();

        // The external website currently accepts decimal values instead of
        // returning the required validation alert. This test preserves the
        // original assignment expectation and exposes that application defect.
        fitnessChallengePage.acceptAlert(
                "Please enter a valid number of steps");
        assertTrue(fitnessChallengePage.isModalOpen());
        assertEquals(stepCountBefore,
                fitnessChallengePage.getParticipantStepCount("Mike Kid"));
    }

    private void addSteps(String name, String steps) {
        fitnessChallengePage.openModal(false);
        fitnessChallengePage.selectParticipant(name);
        fitnessChallengePage.enterSteps(steps);
        fitnessChallengePage.submitSteps();
        fitnessChallengePage.waitForModalClosed();
    }

    private void assertModalElements() {
        assertTrue(fitnessChallengePage.isModalOpen());
        assertEquals("Add Steps to Participant",
                fitnessChallengePage.getModalTitle());
        assertTrue(fitnessChallengePage.isParticipantDropdownDisplayed());
        assertTrue(fitnessChallengePage.isStepsInputDisplayed());
        assertTrue(fitnessChallengePage.isModalSubmitButtonDisplayed());
        assertEquals("Add Steps",
                fitnessChallengePage.getModalSubmitButtonText());
        assertTrue(fitnessChallengePage.isModalCloseButtonPresent());
    }

    private void assertDefaultState() {
        assertEquals(10,
                fitnessChallengePage.waitForParticipants(10).size());
        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(FitnessChallengePage.formatSteps(entry.getValue()),
                    fitnessChallengePage.getParticipantSteps(entry.getKey()));
        }
        assertTrue(fitnessChallengePage.isParticipantRankingDescending());
    }
}
    /*
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
*/
