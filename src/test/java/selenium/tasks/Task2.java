package selenium.tasks;

import net.bytebuddy.build.ToStringPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverUtility;

import java.math.BigInteger;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task2 {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";

    private static final Map<String, String> DEFAULT_PARTICIPANTS =
            new LinkedHashMap<>();

    static {
        DEFAULT_PARTICIPANTS.put("Mike Kid", "8,500");
        DEFAULT_PARTICIPANTS.put("Jill Watson", "12,000");
        DEFAULT_PARTICIPANTS.put("Jane Doe", "6,500");
        DEFAULT_PARTICIPANTS.put("John Smith", "15,000");
        DEFAULT_PARTICIPANTS.put("Sarah Johnson", "9,800");
        DEFAULT_PARTICIPANTS.put("Carlos Garcia", "11,200");
        DEFAULT_PARTICIPANTS.put("Emily Chen", "7,300");
        DEFAULT_PARTICIPANTS.put("David Brown", "13,500");
        DEFAULT_PARTICIPANTS.put("Maria Rodriguez", "10,500");
        DEFAULT_PARTICIPANTS.put("Alex Taylor", "8,900");
    }

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(PAGE_URL);

        ((JavascriptExecutor) driver).executeScript(
                "for (var i = localStorage.length - 1; i >= 0; i--) {" +
                "  var key = localStorage.key(i);" +
                "  if (key && (key.startsWith('participant') || key === 'fitnessReload')) {" +
                "    localStorage.removeItem(key);" +
                "  }" +
                "}");
        driver.navigate().refresh();
        wait.until(webDriver -> webDriver.findElements(By.cssSelector("#participantsList li")).size() == 10);
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
        assertEquals("Fitness Challenge", waitForText(By.cssSelector("h2")));

        Map<String, String> displayed = readParticipants();
        assertEquals(10, displayed.size());
        assertEquals(DEFAULT_PARTICIPANTS, displayed);

        List<WebElement> addButtons = driver.findElements(By.cssSelector("#addStepsBtn"));
        List<WebElement> resetButtons = driver.findElements(By.cssSelector("#resetBtn"));
        assertEquals(2, addButtons.size());
        assertEquals(2, resetButtons.size());
        for (WebElement button : addButtons) {
            assertTrue(button.isDisplayed());
            assertEquals("Add Steps", button.getText());
        }
        for (WebElement button : resetButtons) {
            assertTrue(button.isDisplayed());
            assertEquals("Reset List", button.getText());
        }
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        // TODO:
        //  review the order of participants in the list
        //  verify participants are displayed in descending order by step count
        //  verify each participant's step count is greater than or equal to the participant below them
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        // TODO:
        //  identify the first three participants in the list
        //  verify 1st place participant displays a gold trophy icon
        //  verify 2nd place participant displays a silver trophy icon
        //  verify 3rd place participant displays a bronze (#cd7f32) trophy icon
        //  verify participants ranked 4th and below have no trophy icons
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        // TODO:
        //  locate the "Add Steps" button at the top of the page
        //  click the button
        //  verify modal window appears with title "Add Steps to Participant"
        //  verify modal contains a participant dropdown
        //  verify modal contains a number input field
        //  verify modal contains "Add Steps" submit button
        //  verify modal has a close button (×) in the top-right corner
        //  verify dropdown is prepopulated with all 10 participants
        //  verify default dropdown text shows "Choose participant"
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        // TODO:
        //  scroll to the bottom of the page
        //  locate the "Add Steps" button at the bottom
        //  click the button
        //  verify modal opens
        //  verify all modal elements (same behavior as openModalViaTopButton)
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  click the × (close) button in the top-right corner
        //  verify modal closes
        //  verify user returns to the main page view
        //  verify no data is changed (same as firstTimePageLoad)
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        // TODO:
        //  note the current step count for "Mike Kid"
        //  open the "Add Steps" modal
        //  select "Mike Kid" from the dropdown
        //  enter "1000" in the steps input field
        //  click "Add Steps" button
        //  verify modal closes automatically
        //  verify participant list refreshes
        //  verify Mike Kid's step count increases by 1,000
        //  verify list re-sorts if Mike Kid's new total changes his ranking
    }

    @Test
    public void addZeroSteps() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select any participant
        //  enter "0" in the steps input field
        //  click "Add Steps" button
        //  verify alert appears: "Please enter a valid number of steps"
        //  accept alert
        //  verify modal remains open
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select any participant
        //  enter "999999" in the steps input field
        //  click "Add Steps" button
        //  verify modal closes
        //  verify step count updates correctly with the large number
        //  verify participant moves to first place
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  leave the participant dropdown at "Choose participant"
        //  enter "1000" in the steps input
        //  click "Add Steps" button
        //  verify alert appears: "Please select a participant"
        //  accept alert
        //  verify modal remains open
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select any participant
        //  enter "0" in the steps input field
        //  click "Add Steps" button
        //  verify alert appears: "Please enter a valid number of steps"
        //  accept alert
        //  verify modal remains open
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select a participant
        //  enter "-500" in the steps input field
        //  attempt to click "Add Steps" button
        //  verify alert appears: "Please enter a valid number of steps"
        //  accept alert
        //  verify modal remains open
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select a participant
        //  attempt to enter "abc" in the steps input field
        //  verify input field rejects non-numeric characters
        //  verify no alphabetic characters appear in the input
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        // TODO:
        //  add steps to at least 2 participants to modify the default state
        //  click the "Reset List" button at the top of the page
        //  wait for page reload
        //  verify all participants return to their default step counts (same as firstTimePageLoad)
        //  verify default ranking order is restored (same as firstTimePageLoad)
        //  verify trophy icons display for correct default top 3 (same as firstTimePageLoad)
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        // TODO:
        //  modify participant data
        //  scroll to bottom of page
        //  click the "Reset List" button at the bottom
        //  wait for page reload
        //  verify same behavior as resetViaTopButton
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        openAddStepsModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("9007199254740991");
        driver.findElement(By.id("modal_add_steps_button")).click();

        wait.until(webDriver ->
                "none".equals(webDriver.findElement(By.id("addStepsModal"))
                        .getCssValue("display")));

        BigInteger expectedSteps = new BigInteger("9007199254749492");
        assertEquals(expectedSteps, readParticipantSteps("Mike Kid"));

        assertEquals("Mike Kid",
                wait.until(webDriver -> webDriver.findElement(By.cssSelector(
                        "#participantsList li")).findElement(By.cssSelector(".participant-name"))
                        .getText()));
    }

    @Test
    public void decimalStepValues() throws Exception {
        openAddStepsModal();
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("100.5");
        driver.findElement(By.id("modal_add_steps_button")).click();

        Alert alert = wait.until(org.openqa.selenium.support.ui.ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();

        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(new BigInteger("8500"), readParticipantSteps("Mike Kid"));
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

    private void openAddStepsModal() {
        driver.findElement(By.cssSelector("#addStepsBtn")).click();
        wait.until(webDriver -> "block".equals(
                webDriver.findElement(By.id("addStepsModal")).getCssValue("display")));
    }

    private Map<String, String> readParticipants() {
        Map<String, String> participants = new LinkedHashMap<>();
        for (WebElement row : driver.findElements(By.cssSelector("#participantsList li"))) {
            String name = row.findElement(By.cssSelector(".participant-name")).getText();
            String steps = row.findElement(By.cssSelector(".participant-steps"))
                    .getText()
                    .replace(" steps", "")
                    .trim();
            participants.put(name, steps);
        }
        return participants;
    }

    private BigInteger readParticipantSteps(String participantName) {
        for (WebElement row : driver.findElements(By.cssSelector("#participantsList li"))) {
            String name = row.findElement(By.cssSelector(".participant-name")).getText();
            if (participantName.equals(name)) {
                String steps = row.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(",", "")
                        .replace(" steps", "")
                        .trim();
                return new BigInteger(steps);
            }
        }
        throw new AssertionError("Participant not found: " + participantName);
    }

    private String waitForText(By locator) {
        return wait.until(webDriver -> webDriver.findElement(locator).getText());
    }
}
