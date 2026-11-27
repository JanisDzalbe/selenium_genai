package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.pages.FitnessChallengePage;
import selenium.utility.WebDriverFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task2 {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final Map<String, Long> DEFAULT_PARTICIPANTS = Map.of(
            "Mike Kid", 8_500L,
            "Jill Watson", 12_000L,
            "Jane Doe", 6_500L,
            "John Smith", 15_000L,
            "Sarah Johnson", 9_800L,
            "Carlos Garcia", 11_200L,
            "Emily Chen", 7_300L,
            "David Brown", 13_500L,
            "Maria Rodriguez", 10_500L,
            "Alex Taylor", 8_900L
    );

    private WebDriver driver;
    private FitnessChallengePage fitnessPage;

    @BeforeEach
    public void openPage(TestInfo testInfo) {
        String testName = testInfo.getTestMethod().map(method -> method.getName()).orElse("");
        if ("edgeBrowser".equals(testName)) {
            driver = WebDriverFactory.createEdgeDriver();
        } else if ("firefoxBrowser".equals(testName)) {
            driver = WebDriverFactory.createFirefoxDriver();
        } else {
            driver = WebDriverFactory.createChromeDriver();
        }

        driver.manage().window().maximize();
        fitnessPage = new FitnessChallengePage(driver);
        fitnessPage.open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", fitnessPage.title());
        assertDefaultParticipants();
        assertButtonsVisible();
    }

    @Test
    public void rankingOrder() {
        assertRankingOrder();
    }

    @Test
    public void medalTrophyIcons() {
        assertTrophyIcons();
    }

    @Test
    public void openModalViaTopButton() {
        fitnessPage.openAddStepsModal(0);
        assertModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        fitnessPage.openAddStepsModal(1);
        assertModalContents();
    }

    @Test
    public void closeModalWithCloseButton() {
        Map<String, Long> originalParticipants = fitnessPage.participants();
        fitnessPage.openAddStepsModal(0);
        fitnessPage.closeModal();

        assertEquals(originalParticipants, fitnessPage.participants());
    }

    @Test
    public void addValidStepsToParticipant() {
        long originalSteps = fitnessPage.participantSteps("Mike Kid");
        addSteps("Mike Kid", "1000");

        assertEquals(originalSteps + 1_000L, fitnessPage.participantSteps("Mike Kid"));
        assertRankingOrder();
    }

    @Test
    public void addZeroSteps() {
        fitnessPage.openAddStepsModal(0);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("0");
        fitnessPage.submitSteps();
        acceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void addLargeNumberOfSteps() {
        long originalSteps = fitnessPage.participantSteps("Mike Kid");
        addSteps("Mike Kid", "999999");

        assertEquals(originalSteps + 999_999L, fitnessPage.participantSteps("Mike Kid"));
        assertEquals("Mike Kid", fitnessPage.participantNames().get(0));
    }

    @Test
    public void submitWithoutSelectingParticipant() {
        fitnessPage.openAddStepsModal(0);
        fitnessPage.enterSteps("1000");
        fitnessPage.submitSteps();
        acceptAlert("Please select a participant");
        assertModalOpen();
    }

    @Test
    public void submitWithoutEnteringSteps() {
        fitnessPage.openAddStepsModal(0);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("0");
        fitnessPage.submitSteps();
        acceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNegativeSteps() {
        fitnessPage.openAddStepsModal(0);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("-500");
        fitnessPage.submitSteps();
        acceptAlert("Please enter a valid number of steps");
        assertModalOpen();
    }

    @Test
    public void submitWithNonNumericInput() {
        fitnessPage.openAddStepsModal(0);
        fitnessPage.selectParticipant("Mike Kid");
        fitnessPage.enterSteps("abc");

        assertFalse(fitnessPage.stepsInputValue().matches(".*[A-Za-z].*"));
    }

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jill Watson", "2000");
        resetAndAssertDefault(0);
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        resetAndAssertDefault(1);
    }

    @Test
    public void chromeBrowser() {
        verifyCoreFlows();
    }

    @Test
    public void firefoxBrowser() {
        verifyCoreFlows();
    }

    private void verifyCoreFlows() {
        addValidStepsToParticipant();
        submitWithoutSelectingParticipant();
        fitnessPage.closeModal();
        resetViaTopButton();
    }

    private void addSteps(String participant, String amount) {
        fitnessPage.openAddStepsModal(0);
        fitnessPage.selectParticipant(participant);
        fitnessPage.enterSteps(amount);
        fitnessPage.submitSteps();
        fitnessPage.waitForModalToClose();
        long defaultSteps = DEFAULT_PARTICIPANTS.get(participant);
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(d -> fitnessPage.participantSteps(participant) > defaultSteps);
    }

    private void resetAndAssertDefault(int buttonIndex) {
        fitnessPage.clickResetButton(buttonIndex);
        assertDefaultParticipants();
        assertRankingOrder();
        assertTrophyIcons();
    }

    private void assertDefaultParticipants() {
        assertEquals(DEFAULT_PARTICIPANTS, fitnessPage.participants());
    }

    private void assertButtonsVisible() {
        assertEquals(2, fitnessPage.addStepsButtonCount());
        assertEquals(2, fitnessPage.resetButtonCount());
        assertTrue(fitnessPage.areAddStepsButtonsVisible());
        assertTrue(fitnessPage.areResetButtonsVisible());
    }

    private void assertRankingOrder() {
        List<Long> steps = fitnessPage.participantStepCounts();
        assertEquals(DEFAULT_PARTICIPANTS.size(), steps.size());
        for (int i = 1; i < steps.size(); i++) {
            assertTrue(steps.get(i - 1) >= steps.get(i),
                    "Participant ranking is not descending at position " + i);
        }
    }

    private void assertTrophyIcons() {
        assertEquals(DEFAULT_PARTICIPANTS.size(), fitnessPage.participantCount());
        assertTrophyColor(0, "255, 215, 0");
        assertTrophyColor(1, "192, 192, 192");
        assertTrophyColor(2, "205, 127, 50");
        for (int i = 3; i < fitnessPage.participantCount(); i++) {
            assertFalse(fitnessPage.hasTrophyAt(i),
                    "Participant at position " + (i + 1) + " should not have a trophy");
        }
    }

    private void assertTrophyColor(int participantIndex, String channels) {
        String color = fitnessPage.trophyColorAt(participantIndex);
        assertTrue(color.equals("rgb(" + channels + ")")
                        || color.equals("rgba(" + channels + ", 1)"),
                "Unexpected trophy color: " + color);
    }

    private void assertModalContents() {
        assertTrue(fitnessPage.isModalVisible());
        assertEquals("Add Steps to Participant", fitnessPage.modalTitle());
        assertTrue(fitnessPage.isParticipantSelectVisible());
        assertEquals("select", fitnessPage.participantSelectTagName());
        assertEquals("number", fitnessPage.stepsInputType());
        assertEquals("Add Steps", fitnessPage.modalSubmitButtonText());
        assertTrue(fitnessPage.isModalSubmitButtonEnabled());
        assertEquals("Close Modal", fitnessPage.modalCloseButtonTitle());

        List<String> options = fitnessPage.participantOptions();
        assertEquals(11, options.size());
        assertEquals("Choose participant", options.get(0));
        assertEquals(DEFAULT_PARTICIPANTS.keySet(), Set.copyOf(options.subList(1, options.size())));
    }

    private void assertModalOpen() {
        assertTrue(fitnessPage.isModalVisible());
    }

    private void acceptAlert(String expectedMessage) {
        assertEquals(expectedMessage, fitnessPage.alertText());
        fitnessPage.acceptAlert();
    }
}
