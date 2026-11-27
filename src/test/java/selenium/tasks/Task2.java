package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FitnessChallengePage;
import selenium.utility.ChromeDriverFactory;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    FitnessChallengePage fitnessPage;

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
        fitnessPage = new FitnessChallengePage(driver).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void assertDefaultState() {
        List<FitnessChallengePage.Participant> participants = fitnessPage.participants();
        assertEquals(10, participants.size());
        for (FitnessChallengePage.Participant participant : participants) {
            assertTrue(DEFAULT_STEPS.containsKey(participant.name()),
                    "Unexpected participant: " + participant.name());
            assertEquals(BigInteger.valueOf(DEFAULT_STEPS.get(participant.name())),
                    participant.steps(), "Unexpected default steps for " + participant.name());
        }
        assertEquals("John Smith", participants.get(0).name());
        assertEquals("David Brown", participants.get(1).name());
        assertEquals("Jill Watson", participants.get(2).name());
    }

    private void assertModalElements() {
        FitnessChallengePage.ModalDetails modal = fitnessPage.modalDetails();
        assertEquals("Add Steps to Participant", modal.title());
        assertTrue(modal.hasParticipantSelect());
        assertTrue(modal.hasStepsInput());
        assertTrue(modal.hasAddButton());
        assertEquals("Close Modal", modal.closeButtonTitle());
        assertEquals("Choose participant", modal.selectedParticipant());
        assertEquals(11, modal.participantOptionCount());
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", fitnessPage.title());
        assertEquals("Fitness Challenge", fitnessPage.heading());
        assertDefaultState();
        assertEquals(2, fitnessPage.addStepsButtonCount());
        assertEquals(2, fitnessPage.resetButtonCount());
        assertTrue(fitnessPage.areAddStepsButtonsDisplayed());
        assertTrue(fitnessPage.areResetButtonsDisplayed());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        List<FitnessChallengePage.Participant> participants = fitnessPage.participants();
        assertEquals(10, participants.size());
        for (int i = 1; i < participants.size(); i++) {
            assertTrue(participants.get(i - 1).steps().compareTo(participants.get(i).steps()) >= 0,
                    "Participants are not in descending step-count order");
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<FitnessChallengePage.Participant> participants = fitnessPage.participants();
        assertTrue(participants.get(0).trophyColor().contains("255"));
        assertTrue(participants.get(1).trophyColor().contains("192"));
        assertTrue(participants.get(2).trophyColor().contains("205"));
        for (int i = 3; i < participants.size(); i++) {
            assertFalse(participants.get(i).trophyDisplayed());
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        fitnessPage.openAddStepsModal();
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        fitnessPage.openAddStepsModalAtBottom();
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        fitnessPage.openAddStepsModal();
        fitnessPage.closeModal();
        assertFalse(fitnessPage.isModalDisplayed());
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        BigInteger before = fitnessPage.participant("Mike Kid").steps();
        BigInteger expected = before.add(BigInteger.valueOf(1000));
        fitnessPage.submitSteps("Mike Kid", "1000");
        fitnessPage.waitForParticipantSteps("Mike Kid", expected);
        assertEquals(expected, fitnessPage.participant("Mike Kid").steps());
        assertFalse(fitnessPage.isModalDisplayed());
        List<FitnessChallengePage.Participant> participants = fitnessPage.participants();
        assertEquals(10, participants.size());
        for (int i = 1; i < participants.size(); i++) {
            assertTrue(participants.get(i - 1).steps().compareTo(participants.get(i).steps()) >= 0);
        }
    }

    @Test
    public void addZeroSteps() throws Exception {
        fitnessPage.submitSteps("Mike Kid", "0");
        assertEquals("Please enter a valid number of steps", fitnessPage.acceptAlert());
        assertTrue(fitnessPage.isModalDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        fitnessPage.submitSteps("Mike Kid", "999999");
        fitnessPage.waitForModalToClose();
        assertEquals("Mike Kid", fitnessPage.participants().get(0).name());
        assertEquals(BigInteger.valueOf(1008499), fitnessPage.participant("Mike Kid").steps());
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        fitnessPage.submitSteps(null, "1000");
        assertEquals("Please select a participant", fitnessPage.acceptAlert());
        assertTrue(fitnessPage.isModalDisplayed());
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        fitnessPage.submitSteps("Mike Kid", "0");
        assertEquals("Please enter a valid number of steps", fitnessPage.acceptAlert());
        assertTrue(fitnessPage.isModalDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        fitnessPage.submitSteps("Mike Kid", "-500");
        assertEquals("Please enter a valid number of steps", fitnessPage.acceptAlert());
        assertTrue(fitnessPage.isModalDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("abc");
        assertFalse(fitnessPage.stepsInputValue().matches(".*[A-Za-z].*"));
        assertEquals("", fitnessPage.stepsInputValue());
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        fitnessPage.submitSteps("Mike Kid", "1000");
        fitnessPage.waitForModalToClose();
        fitnessPage.submitSteps("Jill Watson", "2000");
        fitnessPage.waitForModalToClose();
        fitnessPage.reset(0);
        assertDefaultState();
        List<FitnessChallengePage.Participant> participants = fitnessPage.participants();
        assertTrue(participants.get(0).trophyDisplayed());
        assertTrue(participants.get(1).trophyDisplayed());
        assertTrue(participants.get(2).trophyDisplayed());
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        fitnessPage.submitSteps("Mike Kid", "1000");
        fitnessPage.waitForModalToClose();
        fitnessPage.resetAtBottom();
        assertDefaultState();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        fitnessPage.submitSteps("Mike Kid", "9007199254740991");
        fitnessPage.waitForModalToClose();
        FitnessChallengePage.Participant first = fitnessPage.participants().get(0);
        assertEquals("Mike Kid", first.name());
        assertTrue(first.steps().compareTo(new BigInteger("9007199254740991")) > 0);
        assertTrue(first.displayedSteps().endsWith("steps"));
    }

    @Test
    public void decimalStepValues() throws Exception {
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("100.5");
        fitnessPage.clickAddSteps();
        assertEquals("Please enter a valid number of steps", fitnessPage.acceptAlert());
        assertTrue(fitnessPage.isModalDisplayed());
        assertEquals(BigInteger.valueOf(8500), fitnessPage.participant("Mike Kid").steps());
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        // TODO:
        //  open the fitness challenge in Chrome
        //  verify adding steps, validation, and reset work as expected
    }

    @Test
    public void edgeBrowser() throws Exception {
        // TODO:
        //  open the fitness challenge in Edge
        //  verify adding steps, validation, and reset work as expected
    }

    @Test
    public void firefoxBrowser() throws Exception {
        // TODO:
        //  open the fitness challenge in Firefox
        //  verify adding steps, validation, and reset work as expected
    }
}
