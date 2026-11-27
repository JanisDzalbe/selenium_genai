package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import selenium.pages.FitnessChallengePage;
import selenium.utility.WebDriverUtility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final List<String> DEFAULT_NAMES = List.of(
            "Mike Kid", "Jill Watson", "Jane Doe", "John Smith", "Sarah Johnson",
            "Carlos Garcia", "Emily Chen", "David Brown", "Maria Rodriguez", "Alex Taylor"
    );
    private static final Map<String, Long> DEFAULT_STEPS = createDefaultSteps();
    private static final List<String> DEFAULT_RANKING = List.of(
            "John Smith", "David Brown", "Jill Watson", "Carlos Garcia", "Maria Rodriguez",
            "Sarah Johnson", "Alex Taylor", "Mike Kid", "Emily Chen", "Jane Doe"
    );

    WebDriver driver;
    private FitnessChallengePage fitnessChallengePage;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.createChromeDriver();
        openFitnessPage();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private static Map<String, Long> createDefaultSteps() {
        Map<String, Long> steps = new LinkedHashMap<>();
        steps.put("Mike Kid", 8_500L);
        steps.put("Jill Watson", 12_000L);
        steps.put("Jane Doe", 6_500L);
        steps.put("John Smith", 15_000L);
        steps.put("Sarah Johnson", 9_800L);
        steps.put("Carlos Garcia", 11_200L);
        steps.put("Emily Chen", 7_300L);
        steps.put("David Brown", 13_500L);
        steps.put("Maria Rodriguez", 10_500L);
        steps.put("Alex Taylor", 8_900L);
        return Map.copyOf(steps);
    }

    private void openFitnessPage() {
        fitnessChallengePage = new FitnessChallengePage(driver);
        fitnessChallengePage.open();
    }

    private void addSteps(String participant, String steps) {
        openAddStepsModal(0);
        fitnessChallengePage.selectParticipant(participant);
        fitnessChallengePage.enterSteps(steps);
        fitnessChallengePage.submitValidSteps();
    }

    private void openAddStepsModal(int buttonPosition) {
        assertEquals(2, fitnessChallengePage.getAddStepsButtonCount(),
                "addStepsBtn should appear at the top and bottom of the page");
        fitnessChallengePage.openAddStepsModal(buttonPosition);
    }

    private void assertDefaultParticipants() {
        assertEquals(10, fitnessChallengePage.getParticipantCount());
        assertEquals(DEFAULT_STEPS, fitnessChallengePage.getStepCounts());
    }

    private void assertDefaultRankingAndTrophies() {
        assertEquals(DEFAULT_RANKING, fitnessChallengePage.getParticipantNames());
        assertTrophy(0, "rgb(255, 215, 0)");
        assertTrophy(1, "rgb(192, 192, 192)");
        assertTrophy(2, "rgb(205, 127, 50)");
        for (int i = 3; i < fitnessChallengePage.getParticipantCount(); i++) {
            assertFalse(fitnessChallengePage.hasTrophyAtRank(i),
                    "Participant ranked " + (i + 1) + " should not have a trophy");
        }
    }

    private void assertTrophy(int position, String expectedColor) {
        assertEquals(expectedColor, fitnessChallengePage.getTrophyColorAtRank(position));
    }

    private void resetUsingButton(int position) {
        assertEquals(2, fitnessChallengePage.getResetButtonCount(),
                "resetBtn should appear at the top and bottom of the page");
        fitnessChallengePage.resetParticipantList(position);
        assertDefaultParticipants();
        assertDefaultRankingAndTrophies();
    }

    private void assertAlertForNoParticipant() {
        openAddStepsModal(0);
        fitnessChallengePage.appendSteps("1000");
        submitAndAcceptAlert("Please select a participant");
        assertTrue(fitnessChallengePage.isAddStepsModalDisplayed());
    }

    private void submitAndAcceptAlert(String expectedText) {
        assertEquals(expectedText, fitnessChallengePage.submitStepsAndGetAlertText());
        fitnessChallengePage.acceptAlertAndWaitForModal();
    }

    private void assertModalContents() {
        assertTrue(fitnessChallengePage.isAddStepsModalDisplayed());
        assertEquals("Add Steps to Participant", fitnessChallengePage.getModalHeading());

        List<String> options = fitnessChallengePage.getParticipantOptions();
        assertEquals(11, options.size());
        assertEquals("Choose participant", options.get(0));
        assertEquals(DEFAULT_NAMES, options.subList(1, options.size()));

        assertEquals("number", fitnessChallengePage.getStepsInputType());
        assertTrue(fitnessChallengePage.isStepsInputDisplayed());
        assertTrue(fitnessChallengePage.isAddStepsSubmitButtonDisplayed());
        assertEquals("Add Steps", fitnessChallengePage.getAddStepsSubmitButtonText());

        assertTrue(fitnessChallengePage.isModalCloseButtonDisplayed());
        assertTrue(fitnessChallengePage.getModalCloseButtonClass().contains("w3-display-topright"));
        assertEquals("×", fitnessChallengePage.getModalCloseButtonText());
    }

    private void runCrossBrowserScenarios() {
        long previousSteps = fitnessChallengePage.getStepCount("Mike Kid");
        addSteps("Mike Kid", "1000");
        assertEquals(previousSteps + 1_000, fitnessChallengePage.getStepCount("Mike Kid"));
        assertTrue(fitnessChallengePage.getParticipantNames().indexOf("Mike Kid")
                > fitnessChallengePage.getParticipantNames().indexOf("Jill Watson"));

        assertAlertForNoParticipant();
        fitnessChallengePage.closeAddStepsModal();
        resetUsingButton(0);
    }

    private void switchToBrowser(String browser) {
        if ("Chrome".equals(browser)) {
            return;
        }
        driver.quit();
        driver = null;
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String executable = switch (browser) {
            case "Edge" -> osName.startsWith("windows") ? "msedgedriver.exe" : "msedgedriver";
            case "Firefox" -> osName.startsWith("windows") ? "geckodriver.exe" : "geckodriver";
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
        Path driverPath = Path.of("lib", executable).toAbsolutePath().normalize();
        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException(browser + " WebDriver executable not found: " + driverPath
                    + ". Place " + executable + " in lib/ to run this cross-browser test.");
        }

        if ("Edge".equals(browser)) {
            System.setProperty("webdriver.edge.driver", driverPath.toString());
            driver = new EdgeDriver();
        } else {
            System.setProperty("webdriver.gecko.driver", driverPath.toString());
            driver = new FirefoxDriver();
        }
        openFitnessPage();
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", fitnessChallengePage.getHeading());
        assertDefaultParticipants();
        assertEquals(DEFAULT_NAMES.size(), fitnessChallengePage.getStepCounts().size());
        assertEquals(2, fitnessChallengePage.getAddStepsButtonCount());
        assertEquals(2, fitnessChallengePage.getResetButtonCount());
        for (int i = 0; i < fitnessChallengePage.getAddStepsButtonCount(); i++) {
            assertTrue(fitnessChallengePage.isPageButtonDisplayed("addStepsBtn", i));
            assertEquals("Add Steps", fitnessChallengePage.getPageButtonText("addStepsBtn", i));
        }
        for (int i = 0; i < fitnessChallengePage.getResetButtonCount(); i++) {
            assertTrue(fitnessChallengePage.isPageButtonDisplayed("resetBtn", i));
            assertEquals("Reset List", fitnessChallengePage.getPageButtonText("resetBtn", i));
        }
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<Long> steps = fitnessChallengePage.getRankedStepCounts();
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1),
                    "Steps should be descending at ranks " + (i + 1) + " and " + (i + 2));
        }
    }

    @Test
    public void medalTrophyIcons() {
        assertDefaultRankingAndTrophies();
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        openAddStepsModal(0);
        assertModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        openAddStepsModal(1);
        assertModalContents();
    }

    @Test
    public void closeModalWithCloseButton() {
        openAddStepsModal(0);
        fitnessChallengePage.closeAddStepsModal();
        assertTrue(fitnessChallengePage.isHeadingDisplayed());
        assertDefaultParticipants();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long previousSteps = fitnessChallengePage.getStepCount("Mike Kid");
        addSteps("Mike Kid", "1000");
        assertEquals(previousSteps + 1_000, fitnessChallengePage.getStepCount("Mike Kid"));
        assertTrue(fitnessChallengePage.getParticipantNames().indexOf("Mike Kid")
                > fitnessChallengePage.getParticipantNames().indexOf("Sarah Johnson"));
        rankingOrder();
    }

    @Test
    public void addZeroSteps() {
        openAddStepsModal(0);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("0");
        submitAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(fitnessChallengePage.isAddStepsModalDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() {
        long previousSteps = fitnessChallengePage.getStepCount("Mike Kid");
        addSteps("Mike Kid", "999999");
        assertEquals(previousSteps + 999_999, fitnessChallengePage.getStepCount("Mike Kid"));
        assertEquals("Mike Kid", fitnessChallengePage.getParticipantNames().get(0));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        assertAlertForNoParticipant();
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openAddStepsModal(0);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.appendSteps("0");
        submitAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(fitnessChallengePage.isAddStepsModalDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() {
        openAddStepsModal(0);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("-500");
        submitAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(fitnessChallengePage.isAddStepsModalDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() {
        openAddStepsModal(0);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.appendSteps("abc");
        String value = fitnessChallengePage.getStepsInputValue();
        assertFalse(value.matches(".*[A-Za-z].*"), "Number input must reject alphabetic characters");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jill Watson", "2000");
        resetUsingButton(0);
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jill Watson", "2000");
        resetUsingButton(1);
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        long previousSteps = fitnessChallengePage.getStepCount("Mike Kid");
        openAddStepsModal(0);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("9007199254740991");
        fitnessChallengePage.submitValidSteps();

        String expectedDisplay = fitnessChallengePage.formatStepTotal(previousSteps, "9007199254740991");
        assertEquals(expectedDisplay + " steps", fitnessChallengePage.getStepCountText("Mike Kid"));
        assertEquals("Mike Kid", fitnessChallengePage.getParticipantNames().get(0));
    }

    @Test
    public void decimalStepValues() {
        openAddStepsModal(0);
        fitnessChallengePage.selectParticipant("Mike Kid");
        fitnessChallengePage.enterSteps("100.5");
        submitAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(fitnessChallengePage.isAddStepsModalDisplayed());
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() {
        switchToBrowser("Chrome");
        runCrossBrowserScenarios();
    }

    @Test
    public void firefoxBrowser() {
        switchToBrowser("Firefox");
        runCrossBrowserScenarios();
    }
}
