package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
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
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", page.getPageTitle());
        assertEquals(10, page.getParticipantCount());
        var participants = page.getParticipants();
        // Sorted descending: John Smith has the highest default steps (15,000)
        assertEquals("John Smith", page.getParticipantName(participants.get(0)));
        assertEquals(15000L, page.getParticipantSteps(participants.get(0)));
        // Add Steps and Reset List buttons appear twice (top and bottom)
        assertEquals(2, page.getDriver().findElements(By.id("addStepsBtn")).size());
        assertEquals(2, page.getDriver().findElements(By.id("resetBtn")).size());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        var participants = page.getParticipants();
        for (int i = 0; i < participants.size() - 1; i++) {
            long current = page.getParticipantSteps(participants.get(i));
            long next = page.getParticipantSteps(participants.get(i + 1));
            assertTrue(current >= next, "Rank " + i + " should have >= steps than rank " + (i + 1));
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        var participants = page.getParticipants();
        assertTrue(page.hasTrophy(participants.get(0), "gold"));
        assertTrue(page.hasTrophy(participants.get(1), "silver"));
        assertTrue(page.hasTrophy(participants.get(2), "bronze"));
        for (int i = 3; i < participants.size(); i++) {
            assertFalse(page.hasTrophy(participants.get(i), "gold"));
            assertFalse(page.hasTrophy(participants.get(i), "silver"));
            assertFalse(page.hasTrophy(participants.get(i), "bronze"));
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        page.clickAddStepsTop();
        assertTrue(page.isModalVisible());
        assertEquals("Add Steps to Participant", page.getModalTitle());
        assertTrue(!page.getDriver().findElements(page.participantDropdown).isEmpty());
        assertTrue(!page.getDriver().findElements(page.stepsInput).isEmpty());
        assertTrue(!page.getDriver().findElements(page.addStepsSubmitButton).isEmpty());
        assertTrue(!page.getDriver().findElements(page.modalCloseButton).isEmpty());
        Select select = new Select(page.getDriver().findElement(page.participantDropdown));
        assertEquals(10, select.getOptions().size() - 1); // minus default "Choose participant"
        assertEquals("Choose participant", select.getFirstSelectedOption().getText());
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        page.clickAddStepsBottom();
        assertTrue(page.isModalVisible());
        assertEquals("Add Steps to Participant", page.getModalTitle());
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        page.clickAddStepsTop();
        assertTrue(page.isModalVisible());
        page.closeModal();
        assertFalse(page.isModalVisible());
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        var mikeKid = page.getParticipantByName("Mike Kid");
        long initialSteps = page.getParticipantSteps(mikeKid);
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("1000");
        page.submitAddSteps();
        assertFalse(page.isModalVisible());
        // Refresh to verify localStorage persistence
        page.getDriver().navigate().refresh();
        mikeKid = page.getParticipantByName("Mike Kid");
        long newSteps = page.getParticipantSteps(mikeKid);
        assertEquals(initialSteps + 1000L, newSteps);
    }

    @Test
    public void addZeroSteps() throws Exception {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("0");
        page.submitAddSteps();
        assertEquals("Please enter a valid number of steps", page.getAlertText());
        page.acceptAlert();
        assertTrue(page.isModalVisible());
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        var mikeKid = page.getParticipantByName("Mike Kid");
        long initialSteps = page.getParticipantSteps(mikeKid);
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("999999");
        page.submitAddSteps();
        assertFalse(page.isModalVisible());
        mikeKid = page.getParticipantByName("Mike Kid");
        assertEquals(initialSteps + 999999L, page.getParticipantSteps(mikeKid));
        assertEquals("Mike Kid", page.getParticipantName(page.getParticipants().get(0)));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        page.clickAddStepsTop();
        page.enterSteps("1000");
        page.submitAddSteps();
        assertEquals("Please select a participant", page.getAlertText());
        page.acceptAlert();
        assertTrue(page.isModalVisible());
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.submitAddSteps();
        assertEquals("Please enter a valid number of steps", page.getAlertText());
        page.acceptAlert();
        assertTrue(page.isModalVisible());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("-500");
        page.submitAddSteps();
        assertEquals("Please enter a valid number of steps", page.getAlertText());
        page.acceptAlert();
        assertTrue(page.isModalVisible());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("abc");
        // number inputs reject non-numeric characters; value should be empty
        assertEquals("", page.getDriver().findElement(page.stepsInput).getAttribute("value"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("1000");
        page.submitAddSteps();
        page.clickResetTop();
        assertEquals("Fitness Challenge", page.getPageTitle());
        assertEquals(10, page.getParticipantCount());
        var participants = page.getParticipants();
        assertEquals("John Smith", page.getParticipantName(participants.get(0)));
        assertEquals(15000L, page.getParticipantSteps(participants.get(0)));
        assertTrue(page.hasTrophy(participants.get(0), "gold"));
        assertTrue(page.hasTrophy(participants.get(1), "silver"));
        assertTrue(page.hasTrophy(participants.get(2), "bronze"));
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("1000");
        page.submitAddSteps();
        page.clickResetBottom();
        assertEquals("Fitness Challenge", page.getPageTitle());
        assertEquals(10, page.getParticipantCount());
        var participants = page.getParticipants();
        assertEquals("John Smith", page.getParticipantName(participants.get(0)));
        assertEquals(15000L, page.getParticipantSteps(participants.get(0)));
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        var mikeKid = page.getParticipantByName("Mike Kid");
        long initialSteps = page.getParticipantSteps(mikeKid);
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("9007199254740991");
        page.submitAddSteps();
        assertFalse(page.isModalVisible());
        mikeKid = page.getParticipantByName("Mike Kid");
        assertTrue(page.getParticipantSteps(mikeKid) > initialSteps);
    }

    @Test
    public void decimalStepValues() throws Exception {
        // parseInt("100.5") = 100 in JS, so 100 steps are added and modal closes
        var mikeKid = page.getParticipantByName("Mike Kid");
        long initialSteps = page.getParticipantSteps(mikeKid);
        page.clickAddStepsTop();
        page.selectParticipant("Mike Kid");
        page.enterSteps("100.5");
        page.submitAddSteps();
        assertFalse(page.isModalVisible());
        mikeKid = page.getParticipantByName("Mike Kid");
        assertEquals(initialSteps + 100L, page.getParticipantSteps(mikeKid));
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        addValidStepsToParticipant();
    }
}
