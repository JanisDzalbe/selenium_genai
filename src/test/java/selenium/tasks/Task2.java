package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import selenium.pages.FitnessChallengePage;
import selenium.utility.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    FitnessChallengePage page;

    @BeforeEach
    public void openPage() {
        page = new FitnessChallengePage(DriverManager.createChromeDriver());
        page.openPage();
    }

    @AfterEach
    public void closeBrowser() {
        page.getDriver().quit();
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", page.getPageTitle());
        assertEquals(10, page.getParticipantCount());
        var participants = page.getParticipants();
        assertEquals("John Smith", page.getParticipantName(participants.getFirst()),
                "John Smith should be ranked 1st (15,000 default steps)");
        assertEquals(15000L, page.getParticipantSteps(participants.getFirst()));
        assertEquals(2, page.getVisibleButtonCount("Add Steps"), "Expected 2 Add Steps buttons");
        assertEquals(2, page.getVisibleButtonCount("Reset List"), "Expected 2 Reset List buttons");
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        var participants = page.getParticipants();
        for (int i = 0; i < participants.size() - 1; i++) {
            long current = page.getParticipantSteps(participants.get(i));
            long next    = page.getParticipantSteps(participants.get(i + 1));
            assertTrue(current >= next,
                    "Rank " + (i + 1) + " (" + current + ") should have >= steps than rank " + (i + 2) + " (" + next + ")");
        }
    }

    @Test
    public void medalTrophyIcons() {
        var participants = page.getParticipants();
        assertTrue(page.hasTrophy(participants.get(0), "gold"),   "Rank 1 should have gold trophy");
        assertTrue(page.hasTrophy(participants.get(1), "silver"), "Rank 2 should have silver trophy");
        assertTrue(page.hasTrophy(participants.get(2), "bronze"), "Rank 3 should have bronze trophy");
        for (int i = 3; i < participants.size(); i++) {
            assertFalse(page.hasTrophy(participants.get(i), "gold"),   "Rank " + (i + 1) + " should have no gold trophy");
            assertFalse(page.hasTrophy(participants.get(i), "silver"), "Rank " + (i + 1) + " should have no silver trophy");
            assertFalse(page.hasTrophy(participants.get(i), "bronze"), "Rank " + (i + 1) + " should have no bronze trophy");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        page.clickAddStepsTop();
        assertTrue(page.isModalVisible(), "Modal should be visible");
        assertEquals("Add Steps to Participant", page.getModalTitle());
        assertTrue(page.isParticipantDropdownPresent(), "Participant dropdown should be present");
        assertTrue(page.isStepsInputPresent(), "Steps input should be present");
        assertTrue(page.isSubmitButtonPresent(), "Submit button should be present");
        assertTrue(page.isCloseButtonPresent(), "Close button should be present");
        assertEquals(10, page.getParticipantDropdownOptionCount(),
                "Dropdown should have 10 participants (excluding placeholder)");
        assertEquals("Choose participant", page.getFirstSelectedDropdownOption());
    }

    @Test
    public void openModalViaBottomButton() {
        page.clickAddStepsBottom();
        assertTrue(page.isModalVisible(), "Modal should be visible after clicking bottom button");
        assertEquals("Add Steps to Participant", page.getModalTitle());
    }

    @Test
    public void closeModalWithCloseButton() {
        page.clickAddStepsTop();
        assertTrue(page.isModalVisible());
        page.closeModal();
        assertFalse(page.isModalVisible(), "Modal should be closed after clicking ×");
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long initialSteps = page.getParticipantSteps(page.getParticipantByName("Mike Kid"));
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("1000");
        page.submitAddSteps();
        assertFalse(page.isModalVisible(), "Modal should close after submission");
        // Refresh to verify localStorage persistence
        page.getDriver().navigate().refresh();
        page.waitForParticipantList();
        assertEquals(initialSteps + 1000L, page.getParticipantSteps(page.getParticipantByName("Mike Kid")));
    }

    @Test
    public void addZeroSteps() {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("0");
        page.submitAddSteps();
        assertEquals("Please enter a valid number of steps", page.getAlertText());
        page.acceptAlert();
        assertTrue(page.isModalVisible(), "Modal should remain open after validation error");
    }

    @Test
    public void addLargeNumberOfSteps() {
        long initialSteps = page.getParticipantSteps(page.getParticipantByName("Mike Kid"));
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("999999");
        page.submitAddSteps();
        assertFalse(page.isModalVisible(), "Modal should close after submission");
        assertEquals(initialSteps + 999999L, page.getParticipantSteps(page.getParticipantByName("Mike Kid")));
        assertEquals("Mike Kid", page.getParticipantName(page.getParticipants().getFirst()),
                "Mike Kid should be ranked 1st after adding 999,999 steps");
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        page.clickAddStepsTop();
        page.enterSteps("1000");
        page.submitAddSteps();
        assertEquals("Please select a participant", page.getAlertText());
        page.acceptAlert();
        assertTrue(page.isModalVisible(), "Modal should remain open after validation error");
    }

    @Test
    public void submitWithoutEnteringSteps() {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.submitAddSteps();
        assertEquals("Please enter a valid number of steps", page.getAlertText());
        page.acceptAlert();
        assertTrue(page.isModalVisible(), "Modal should remain open after validation error");
    }

    @Test
    public void submitWithNegativeSteps() {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("-500");
        page.submitAddSteps();
        assertEquals("Please enter a valid number of steps", page.getAlertText());
        page.acceptAlert();
        assertTrue(page.isModalVisible(), "Modal should remain open after validation error");
    }

    @Test
    public void submitWithNonNumericInput() {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("abc");
        // number inputs reject non-numeric characters; value should be empty
        assertEquals("", page.getStepsInputValue(),
                "Steps input should be empty after entering non-numeric characters");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("1000");
        page.submitAddSteps();
        page.clickResetTop();
        assertEquals("Fitness Challenge", page.getPageTitle());
        assertEquals(10, page.getParticipantCount());
        var participants = page.getParticipants();
        assertEquals("John Smith", page.getParticipantName(participants.getFirst()));
        assertEquals(15000L, page.getParticipantSteps(participants.getFirst()));
        assertTrue(page.hasTrophy(participants.getFirst(), "gold"),  "Rank 1 should have gold trophy after reset");
        assertTrue(page.hasTrophy(participants.get(1),     "silver"), "Rank 2 should have silver trophy after reset");
        assertTrue(page.hasTrophy(participants.get(2),     "bronze"), "Rank 3 should have bronze trophy after reset");
    }

    @Test
    public void resetViaBottomButton() {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("1000");
        page.submitAddSteps();
        page.clickResetBottom();
        assertEquals("Fitness Challenge", page.getPageTitle());
        assertEquals(10, page.getParticipantCount());
        var participants = page.getParticipants();
        assertEquals("John Smith", page.getParticipantName(participants.getFirst()));
        assertEquals(15000L, page.getParticipantSteps(participants.getFirst()));
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        long initialSteps = page.getParticipantSteps(page.getParticipantByName("Mike Kid"));
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("9007199254740991");
        page.submitAddSteps();
        assertFalse(page.isModalVisible(), "Modal should close after accepting max integer");
        assertTrue(page.getParticipantSteps(page.getParticipantByName("Mike Kid")) > initialSteps,
                "Step count should increase after adding max integer value");
    }

    @Test
    public void decimalStepValues() {
        // parseInt("100.5") === 100 in JS, so exactly 100 steps are added
        long initialSteps = page.getParticipantSteps(page.getParticipantByName("Mike Kid"));
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("100.5");
        page.submitAddSteps();
        assertFalse(page.isModalVisible(), "Modal should close after decimal step submission");
        assertEquals(initialSteps + 100L, page.getParticipantSteps(page.getParticipantByName("Mike Kid")));
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() {
        addValidStepsToParticipant();
    }
}
