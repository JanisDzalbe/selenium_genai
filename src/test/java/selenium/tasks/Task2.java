package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.AddStepsModal;
import selenium.pages.FitnessChallengePage;
import selenium.utility.ChromeDriverFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final Map<String, Integer> DEFAULT_PARTICIPANTS = Map.ofEntries(
            Map.entry("Mike Kid", 8500),
            Map.entry("Jill Watson", 12000),
            Map.entry("Jane Doe", 6500),
            Map.entry("John Smith", 15000),
            Map.entry("Sarah Johnson", 9800),
            Map.entry("Carlos Garcia", 11200),
            Map.entry("Emily Chen", 7300),
            Map.entry("David Brown", 13500),
            Map.entry("Maria Rodriguez", 10500),
            Map.entry("Alex Taylor", 8900)
    );

    private WebDriver driver;
    private FitnessChallengePage fitnessPage;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createDriver();
        fitnessPage = new FitnessChallengePage(driver).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", fitnessPage.title());
        assertDefaultParticipants();
        assertEquals(2, fitnessPage.addStepsButtonCount());
        assertTrue(fitnessPage.allAddStepsButtonsVisible());
        assertEquals(2, fitnessPage.resetButtonCount());
        assertTrue(fitnessPage.allResetButtonsVisible());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<Integer> steps = fitnessPage.participantStepsInOrder();
        List<Integer> sortedSteps = new ArrayList<>(steps);
        sortedSteps.sort(Comparator.reverseOrder());
        assertEquals(sortedSteps, steps, "Participants must be listed from highest to lowest steps");
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1), "Step counts must not increase lower in the list");
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<String> expectedColors = List.of("rgba(255, 215, 0, 1)", "rgba(192, 192, 192, 1)",
                "rgba(205, 127, 50, 1)");
        for (int i = 0; i < expectedColors.size(); i++) {
            assertEquals(expectedColors.get(i), fitnessPage.trophyColorAt(i));
        }
        for (int i = 3; i < DEFAULT_PARTICIPANTS.size(); i++) {
            assertFalse(fitnessPage.participantHasTrophy(i),
                    "Only the top three participants should have trophy icons");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        assertModalContents(fitnessPage.openAddStepsModal());
    }

    @Test
    public void openModalViaBottomButton() {
        assertModalContents(fitnessPage.openAddStepsModalAtBottom());
    }

    @Test
    public void closeModalWithCloseButton() {
        fitnessPage.openAddStepsModal().close();
        assertTrue(fitnessPage.participantListVisible());
        assertDefaultParticipants();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        List<String> originalRanking = fitnessPage.participantNamesInOrder();
        int originalRank = originalRanking.indexOf("Mike Kid");
        int originalSteps = fitnessPage.participantSteps("Mike Kid");
        addSteps("Mike Kid", "1000");
        assertEquals(originalSteps + 1000, fitnessPage.participantSteps("Mike Kid"));
        assertRankingIsDescending();
        List<String> updatedRanking = fitnessPage.participantNamesInOrder();
        assertTrue(updatedRanking.indexOf("Mike Kid") < originalRank,
                "Mike Kid should move up after overtaking a participant");
    }

    @Test
    public void addZeroSteps() {
        AddStepsModal modal = fitnessPage.openAddStepsModal().selectParticipant("Mike Kid").enterSteps("0");
        assertEquals("Please enter a valid number of steps", modal.submitAndAcceptAlert());
        assertTrue(modal.isDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() {
        int originalSteps = fitnessPage.participantSteps("Jane Doe");
        addSteps("Jane Doe", "999999");
        assertEquals(originalSteps + 999999, fitnessPage.participantSteps("Jane Doe"));
        assertEquals("Jane Doe", fitnessPage.participantNameAt(0));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        AddStepsModal modal = fitnessPage.openAddStepsModal().enterSteps("1000");
        assertEquals("Please select a participant", modal.submitAndAcceptAlert());
        assertTrue(modal.isDisplayed());
    }

    @Test
    public void submitWithoutEnteringSteps() {
        AddStepsModal modal = fitnessPage.openAddStepsModal().selectParticipant("Mike Kid").enterSteps("0");
        assertEquals("Please enter a valid number of steps", modal.submitAndAcceptAlert());
        assertTrue(modal.isDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() {
        AddStepsModal modal = fitnessPage.openAddStepsModal().selectParticipant("Mike Kid").enterSteps("-500");
        assertEquals("Please enter a valid number of steps", modal.submitAndAcceptAlert());
        assertTrue(modal.isDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() {
        AddStepsModal modal = fitnessPage.openAddStepsModal().selectParticipant("Mike Kid").enterSteps("abc");
        String value = modal.stepsInputValue();
        assertNotNull(value);
        assertFalse(value.matches(".*[a-zA-Z].*"), "Number input must reject alphabetic characters");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "500");
        fitnessPage.resetAt(0);
        assertDefaultParticipants();
        assertRankingIsDescending();
        assertDefaultTrophyColors();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "500");
        fitnessPage.resetAt(1);
        assertDefaultParticipants();
        assertRankingIsDescending();
        assertDefaultTrophyColors();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        AddStepsModal modal = fitnessPage.openAddStepsModal().selectParticipant("Mike Kid");
        String participantIndex = modal.selectedParticipantIndex();
        double originalSteps = fitnessPage.storedSteps(participantIndex);
        modal.enterSteps("9007199254740991").submitAndWaitForClose();

        double storedStepCount = fitnessPage.storedSteps(participantIndex);
        double expectedStepCount = fitnessPage.addStoredSteps(originalSteps, "9007199254740991");
        assertEquals(expectedStepCount, storedStepCount);
        assertEquals(fitnessPage.storedStepsDisplay(participantIndex),
                fitnessPage.participantStepsDisplay("Mike Kid"));
    }

    @Test
    public void decimalStepValues() {
        AddStepsModal modal = fitnessPage.openAddStepsModal().selectParticipant("Mike Kid").enterSteps("100.5");
        assertEquals("Please enter a valid number of steps", modal.submitAndAcceptAlert());
        assertTrue(modal.isDisplayed());
    }

    private void assertDefaultParticipants() {
        assertEquals(DEFAULT_PARTICIPANTS, fitnessPage.participantStepCounts());
    }

    private void assertRankingIsDescending() {
        List<Integer> steps = fitnessPage.participantStepsInOrder();
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1), "Step counts must be sorted in descending order");
        }
    }

    private void assertDefaultTrophyColors() {
        assertEquals("rgba(255, 215, 0, 1)", fitnessPage.trophyColorAt(0));
        assertEquals("rgba(192, 192, 192, 1)", fitnessPage.trophyColorAt(1));
        assertEquals("rgba(205, 127, 50, 1)", fitnessPage.trophyColorAt(2));
    }

    private void assertModalContents(AddStepsModal modal) {
        assertTrue(modal.title().contains("Add Steps to Participant"));
        assertTrue(modal.participantDropdownVisible());
        assertEquals("Choose participant", modal.selectedParticipantText());
        assertEquals(11, modal.participantOptionCount());
        assertTrue(modal.stepsInputVisible());
        assertEquals("number", modal.stepsInputType());
        assertTrue(modal.submitButtonVisible());
        assertEquals("Add Steps", modal.submitButtonText());
        assertTrue(modal.closeButtonVisible());
    }

    private void addSteps(String participant, String steps) {
        fitnessPage.openAddStepsModal().selectParticipant(participant).enterSteps(steps).submitAndWaitForClose();
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
