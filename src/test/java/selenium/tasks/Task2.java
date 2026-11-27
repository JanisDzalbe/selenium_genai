package selenium.tasks;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import selenium.pages.FitnessChallengePage;
import selenium.utility.DriverFactory;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final Map<String, Long> DEFAULT_PARTICIPANTS = defaultParticipants();

    private WebDriver driver;
    private FitnessChallengePage fitnessPage;

    @BeforeEach
    public void openPage(TestInfo testInfo) {
        String testName = testInfo.getTestMethod()
                .orElseThrow(() -> new IllegalStateException("Test method is unavailable"))
                .getName();
        driver = switch (testName) {
            case "edgeBrowser" -> new EdgeDriver();
            case "firefoxBrowser" -> new FirefoxDriver();
            default -> DriverFactory.createChromeDriver();
        };
        fitnessPage = new FitnessChallengePage(driver).open();
        fitnessPage.waitForDefaultParticipants(DEFAULT_PARTICIPANTS);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", fitnessPage.title());
        assertDefaultParticipants();
        assertEquals(2, fitnessPage.buttonCount("Add Steps"));
        assertEquals(2, fitnessPage.buttonCount("Reset List"));
        assertTrue(fitnessPage.allButtonsDisplayed("Add Steps"));
        assertTrue(fitnessPage.allButtonsDisplayed("Reset List"));
    }

    @Test
    public void rankingOrder() {
        List<Long> steps = fitnessPage.rankedStepCounts();
        assertEquals(10, steps.size());
        assertEquals(steps.stream().sorted(Comparator.reverseOrder()).toList(), steps);
    }

    @Test
    public void medalTrophyIcons() {
        assertEquals("#ffd700", fitnessPage.trophyColorAt(0));
        assertEquals("#c0c0c0", fitnessPage.trophyColorAt(1));
        assertEquals("#cd7f32", fitnessPage.trophyColorAt(2));
        for (int rank = 3; rank < fitnessPage.participantCount(); rank++) {
            assertFalse(fitnessPage.hasTrophyAt(rank),
                    "Unexpected trophy icon at rank " + (rank + 1));
        }
    }

    @Test
    public void openModalViaTopButton() {
        fitnessPage.clickButton("Add Steps", 0);
        assertModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        fitnessPage.scrollToBottom();
        fitnessPage.clickButton("Add Steps", 1);
        assertModalContents();
    }

    @Test
    public void closeModalWithCloseButton() {
        fitnessPage.openAddStepsModal();
        fitnessPage.closeModal();

        assertDefaultParticipants();
    }

    @Test
    public void addValidStepsToParticipant() {
        addSteps("Mike Kid", "1000");

        assertEquals(9_500L, fitnessPage.stepsFor("Mike Kid"));
        assertEquals(6, fitnessPage.rankOf("Mike Kid"));
        assertEquals(fitnessPage.rankedStepCounts().stream()
                .sorted(Comparator.reverseOrder()).toList(), fitnessPage.rankedStepCounts());
    }

    @Test
    public void addZeroSteps() {
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("0");
        fitnessPage.submitSteps();

        assertAlert("Please enter a valid number of steps");
        assertTrue(fitnessPage.isModalOpen());
    }

    @Test
    public void addLargeNumberOfSteps() {
        addSteps("Mike Kid", "999999");

        assertEquals(1_008_499L, fitnessPage.stepsFor("Mike Kid"));
        assertEquals("Mike Kid", fitnessPage.participantAt(0));
    }

    @Test
    public void submitWithoutSelectingParticipant() {
        fitnessPage.openAddStepsModal();
        fitnessPage.enterSteps("1000");
        fitnessPage.submitSteps();

        assertAlert("Please select a participant");
        assertTrue(fitnessPage.isModalOpen());
    }

    @Test
    public void submitWithoutEnteringSteps() {
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("0");
        fitnessPage.submitSteps();

        assertAlert("Please enter a valid number of steps");
        assertTrue(fitnessPage.isModalOpen());
    }

    @Test
    public void submitWithNegativeSteps() {
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.setStepsValue("-500");
        fitnessPage.submitSteps();

        assertAlert("Please enter a valid number of steps");
        assertTrue(fitnessPage.isModalOpen());
    }

    @Test
    public void submitWithNonNumericInput() {
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("abc");

        assertFalse(fitnessPage.stepsInputValue().matches(".*[a-zA-Z].*"));
    }

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");
        resetList(0);

        assertDefaultParticipants();
        assertDefaultRankingAndTrophies();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        fitnessPage.scrollToBottom();
        resetList(1);

        assertDefaultParticipants();
        assertDefaultRankingAndTrophies();
    }

    @Test
    public void maximumIntegerValue() {
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.setStepsValue("9007199254740991");
        fitnessPage.submitSteps();
        fitnessPage.waitForModalClosed();

        assertTrue(fitnessPage.stepsFor("Mike Kid") >= 9_007_199_254_740_991L);
    }

    @Test
    public void decimalStepValues() {
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.setStepsValue("100.5");
        fitnessPage.submitSteps();

        assertAlert("Please enter a valid number of steps");
        assertTrue(fitnessPage.isModalOpen());
    }

    @Test
    public void chromeBrowser() {
        assertCrossBrowserWorkflows();
    }

    @Test
    public void edgeBrowser() {
        assertCrossBrowserWorkflows();
    }

    @Test
    public void firefoxBrowser() {
        assertCrossBrowserWorkflows();
    }

    private void assertCrossBrowserWorkflows() {
        addSteps("Mike Kid", "1000");
        assertEquals(9_500L, fitnessPage.stepsFor("Mike Kid"));

        fitnessPage.openAddStepsModal();
        fitnessPage.enterSteps("1000");
        fitnessPage.submitSteps();
        assertAlert("Please select a participant");
        assertTrue(fitnessPage.isModalOpen());
        fitnessPage.closeModal();

        addSteps("Jane Doe", "500");
        resetList(0);
        assertDefaultParticipants();
    }

    private void assertDefaultParticipants() {
        fitnessPage.waitForDefaultParticipants(DEFAULT_PARTICIPANTS);
        assertEquals(DEFAULT_PARTICIPANTS, fitnessPage.participantSteps());
    }

    private void assertDefaultRankingAndTrophies() {
        List<Long> expectedSteps = DEFAULT_PARTICIPANTS.values().stream()
                .sorted(Comparator.reverseOrder())
                .toList();
        assertEquals(expectedSteps, fitnessPage.rankedStepCounts());
        assertEquals("#ffd700", fitnessPage.trophyColorAt(0));
        assertEquals("#c0c0c0", fitnessPage.trophyColorAt(1));
        assertEquals("#cd7f32", fitnessPage.trophyColorAt(2));
    }

    private void assertModalContents() {
        assertTrue(fitnessPage.isModalOpen());
        assertEquals("Add Steps to Participant", fitnessPage.modalTitle());
        assertTrue(fitnessPage.participantDropdownDisplayed());
        assertTrue(fitnessPage.stepsInputDisplayed());
        assertEquals("number", fitnessPage.stepsInputType());
        assertTrue(fitnessPage.submitButtonDisplayed());
        assertEquals("Add Steps", fitnessPage.submitButtonText());
        assertTrue(fitnessPage.closeButtonDisplayed());
        assertEquals("absolute", fitnessPage.closeButtonPosition());

        List<String> options = fitnessPage.participantOptions();
        assertEquals(11, options.size());
        assertEquals("Choose participant", fitnessPage.selectedParticipantOption());
        assertEquals(10, options.stream().skip(1).distinct().count());
    }

    private void addSteps(String participant, String steps) {
        long baseline = fitnessPage.stepsFor(participant);
        fitnessPage.openAddStepsModal();
        fitnessPage.selectParticipant(participant);
        fitnessPage.enterSteps(steps);
        fitnessPage.submitSteps();
        fitnessPage.waitForModalClosed();
        fitnessPage.waitForParticipant(participant, updatedSteps -> updatedSteps > baseline);
    }

    private void assertAlert(String expectedText) {
        assertEquals(expectedText, fitnessPage.alertText());
        fitnessPage.acceptAlert();
    }

    private void resetList(int buttonIndex) {
        fitnessPage.clickButton("Reset List", buttonIndex);
        fitnessPage.waitForDefaultParticipants(DEFAULT_PARTICIPANTS);
    }

    private static Map<String, Long> defaultParticipants() {
        Map<String, Long> participants = new LinkedHashMap<>();
        participants.put("Mike Kid", 8_500L);
        participants.put("Jill Watson", 12_000L);
        participants.put("Jane Doe", 6_500L);
        participants.put("John Smith", 15_000L);
        participants.put("Sarah Johnson", 9_800L);
        participants.put("Carlos Garcia", 11_200L);
        participants.put("Emily Chen", 7_300L);
        participants.put("David Brown", 13_500L);
        participants.put("Maria Rodriguez", 10_500L);
        participants.put("Alex Taylor", 8_900L);
        return participants;
    }
}
