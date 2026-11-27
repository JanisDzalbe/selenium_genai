package selenium.tasks;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.AddStepsModalPage;
import selenium.pages.FitnessChallengePage;
import selenium.pages.FitnessChallengePage.Participant;
import selenium.utility.ChromeDriverUtility;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final Map<String, Long> DEFAULT_STEPS = Map.of(
            "Mike Kid", 8_500L,
            "Jill Watson", 12_000L,
            "Jane Doe", 6_500L,
            "John Smith", 15_000L,
            "Sarah Johnson", 9_800L,
            "Carlos Garcia", 11_200L,
            "Emily Chen", 7_300L,
            "David Brown", 13_500L,
            "Maria Rodriguez", 10_500L,
            "Alex Taylor", 8_900L);

    private WebDriver driver;
    private FitnessChallengePage fitnessPage;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverUtility.createDriver();
        driver.get(FitnessChallengePage.URL);
        fitnessPage = new FitnessChallengePage(driver);
        fitnessPage.resetToDefaults();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", fitnessPage.getTitle());
        assertEquals(10, participantsByName().size());
        assertEquals(DEFAULT_STEPS, participantStepsByName());
        assertEquals(2, fitnessPage.getVisibleAddStepsButtonCount());
        assertEquals(2, fitnessPage.getVisibleResetButtonCount());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<Long> counts = participantsByName().stream().map(Participant::steps).toList();

        assertEquals(10, counts.size());
        for (int i = 1; i < counts.size(); i++) {
            assertTrue(counts.get(i - 1) >= counts.get(i),
                    "Step counts are not descending at positions " + (i - 1) + " and " + i);
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<Participant> participants = participantsByName();
        assertEquals(10, participants.size());

        assertEquals("rgba(255, 215, 0, 1)", participants.get(0).trophyColor());
        assertEquals("rgba(192, 192, 192, 1)", participants.get(1).trophyColor());
        assertEquals("rgba(205, 127, 50, 1)", participants.get(2).trophyColor());
        for (int i = 3; i < participants.size(); i++) {
            assertNull(participants.get(i).trophyColor(), "Unexpected trophy icon at rank " + (i + 1));
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        assertModalContents(fitnessPage.openAddStepsModalAtTop());
    }

    @Test
    public void openModalViaBottomButton() {
        assertModalContents(fitnessPage.openAddStepsModalAtBottom());
    }

    @Test
    public void closeModalWithCloseButton() {
        fitnessPage.openAddStepsModalAtTop().close();
        assertEquals(DEFAULT_STEPS, participantStepsByName());
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long previousSteps = stepsFor("Mike Kid");
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant("Mike Kid");
        modal.enterSteps("1000");
        modal.submit();
        modal.waitUntilClosed();

        assertEquals(previousSteps + 1_000, stepsFor("Mike Kid"));
        assertSortedDescending();
    }

    @Test
    public void addZeroSteps() {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant("Mike Kid");
        modal.enterSteps("0");
        modal.submit();
        assertEquals("Please enter a valid number of steps", modal.waitForAlertText());
        modal.acceptAlert();

        assertTrue(modal.isVisible());
    }

    @Test
    public void addLargeNumberOfSteps() {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant("Jane Doe");
        modal.enterSteps("999999");
        modal.submit();
        modal.waitUntilClosed();

        assertEquals(1_006_499L, stepsFor("Jane Doe"));
        assertEquals("Jane Doe", participantsByName().get(0).name());
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.enterSteps("1000");
        modal.submit();
        assertEquals("Please select a participant", modal.waitForAlertText());
        modal.acceptAlert();

        assertTrue(modal.isVisible());
    }

    @Test
    public void submitWithoutEnteringSteps() {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant("Mike Kid");
        modal.enterSteps("0");
        modal.submit();
        assertEquals("Please enter a valid number of steps", modal.waitForAlertText());
        modal.acceptAlert();

        assertTrue(modal.isVisible());
    }

    @Test
    public void submitWithNegativeSteps() {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant("Mike Kid");
        modal.enterSteps("-500");
        modal.submit();
        assertEquals("Please enter a valid number of steps", modal.waitForAlertText());
        modal.acceptAlert();

        assertTrue(modal.isVisible());
    }

    @Test
    public void submitWithNonNumericInput() {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant("Mike Kid");
        modal.enterSteps("abc");

        assertFalse(modal.getStepsInputValue().matches(".*[a-zA-Z].*"),
                "The numeric steps field accepted alphabetic characters");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        fitnessPage.resetFromTop();

        assertDefaultStateRestored();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        fitnessPage.resetFromBottom();

        assertDefaultStateRestored();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant("Jane Doe");
        modal.enterSteps("9007199254740991");
        modal.submit();
        modal.waitUntilClosed();

        assertEquals("9,007,199,254,747,492 steps", participantNamed("Jane Doe").displayedSteps());
    }

    @Test
    public void decimalStepValues() {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant("Mike Kid");
        modal.enterSteps("100.5");

        assertEquals("100.5", modal.getStepsInputValue());
        assertTrue(modal.hasStepMismatch());
        assertTrue(modal.isVisible());
    }

    private void assertModalContents(AddStepsModalPage modal) {
        assertEquals("Add Steps to Participant", modal.getTitle());
        assertTrue(modal.hasParticipantDropdown());
        assertTrue(modal.hasNumberInput());
        assertTrue(modal.hasSubmitButton("Add Steps"));
        assertTrue(modal.hasTopRightCloseButton());
        assertEquals(10, modal.getParticipantOptions().size());
        assertEquals(DEFAULT_STEPS.keySet(), modal.getParticipantOptions().stream().collect(Collectors.toSet()));
        assertEquals("Choose participant", modal.getSelectedParticipantText());
    }

    private void assertDefaultStateRestored() {
        assertEquals(DEFAULT_STEPS, participantStepsByName());
        assertSortedDescending();
        List<Participant> participants = participantsByName();
        assertEquals("rgba(255, 215, 0, 1)", participants.get(0).trophyColor());
        assertEquals("rgba(192, 192, 192, 1)", participants.get(1).trophyColor());
        assertEquals("rgba(205, 127, 50, 1)", participants.get(2).trophyColor());
        assertTrue(participants.subList(3, participants.size()).stream()
                .allMatch(participant -> participant.trophyColor() == null));
    }

    private void assertSortedDescending() {
        List<Long> counts = participantsByName().stream().map(Participant::steps).toList();
        for (int i = 1; i < counts.size(); i++) {
            assertTrue(counts.get(i - 1) >= counts.get(i));
        }
    }

    private void addSteps(String name, String count) {
        AddStepsModalPage modal = fitnessPage.openAddStepsModalAtTop();
        modal.selectParticipant(name);
        modal.enterSteps(count);
        modal.submit();
        modal.waitUntilClosed();
    }

    private List<Participant> participantsByName() {
        return fitnessPage.getParticipants();
    }

    private Map<String, Long> participantStepsByName() {
        return participantsByName().stream().collect(Collectors.toMap(
                Participant::name,
                Participant::steps));
    }

    private long stepsFor(String name) {
        return participantNamed(name).steps();
    }

    private Participant participantNamed(String name) {
        return participantsByName().stream()
                .filter(participant -> participant.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }
}
