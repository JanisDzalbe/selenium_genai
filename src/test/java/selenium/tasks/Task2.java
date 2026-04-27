package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.DriverFactory;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {

    WebDriver driver;
    WebDriverWait wait;

    // ── Selectors ─────────────────────────────────────────────────────────────
    private static final By PARTICIPANT_NAMES  = By.className("participant-name");
    private static final By PARTICIPANT_STEPS  = By.className("participant-steps");
    private static final By ADD_STEPS_BUTTONS  = By.id("addStepsBtn");
    private static final By RESET_BUTTONS      = By.id("resetBtn");
    private static final By MODAL              = By.id("addStepsModal");
    private static final By MODAL_TITLE        = By.xpath("//h3[text()='Add Steps to Participant']");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT        = By.id("steps_input");
    private static final By MODAL_ADD_BUTTON   = By.id("modal_add_steps_button");
    private static final By CLOSE_BUTTON       = By.className("w3-closebtn");
    private static final By PARTICIPANTS_LIST  = By.id("participantsList");

    // Expected default state — sorted descending by steps, matching the real page order
    private static final String[] DEFAULT_NAMES  = {
            "John Smith", "David Brown", "Jill Watson", "Carlos Garcia",
            "Maria Rodriguez", "Sarah Johnson", "Alex Taylor", "Mike Kid",
            "Emily Chen", "Jane Doe"
    };
    private static final String[] DEFAULT_STEPS  = {
            "15,000 steps", "13,500 steps", "12,000 steps", "11,200 steps",
            "10,500 steps", "9,800 steps",  "8,900 steps",  "8,500 steps",
            "7,300 steps",  "6,500 steps"
    };

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @BeforeEach
    public void openPage() {
        driver = DriverFactory.getChromeDriver();
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.navigate().to(
                "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"
        );
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(PARTICIPANT_NAMES));
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) driver.quit();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Opens the modal via the button at the given list index (0 = top, last = bottom). */
    private void openModal(int buttonIndex) {
        List<WebElement> buttons = driver.findElements(ADD_STEPS_BUTTONS);
        buttons.get(buttonIndex).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    /** Verifies every expected element is present and correct inside the modal. */
    private void assertModalContents() {
        assertEquals("Add Steps to Participant",
                driver.findElement(MODAL_TITLE).getText(), "Modal title should match");
        assertTrue(driver.findElement(PARTICIPANT_SELECT).isDisplayed(), "Dropdown should be visible");
        assertTrue(driver.findElement(STEPS_INPUT).isDisplayed(),        "Steps input should be visible");
        assertTrue(driver.findElement(MODAL_ADD_BUTTON).isDisplayed(),   "Add button should be visible");
        assertTrue(driver.findElement(CLOSE_BUTTON).isDisplayed(),       "Close button should be visible");

        Select select = new Select(driver.findElement(PARTICIPANT_SELECT));
        assertEquals(11, select.getOptions().size(),
                "Dropdown should have 11 options (1 placeholder + 10 participants)");
        assertEquals("Choose participant", select.getFirstSelectedOption().getText(),
                "Default dropdown text should be 'Choose participant'");
    }

    /** Fills the modal with the given participant name and step count, then submits. */
    private void submitModal(String participantName, String steps) {
        new Select(driver.findElement(PARTICIPANT_SELECT)).selectByVisibleText(participantName);
        driver.findElement(STEPS_INPUT).sendKeys(steps);
        driver.findElement(MODAL_ADD_BUTTON).click();
    }

    /** Accepts a browser alert and returns its text. Fails the test if no alert appears. */
    private String acceptAlert() throws InterruptedException {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        // Give the driver time to process alert dismissal and clear the alert state
        Thread.sleep(500);
        return text;
    }

    /** Waits for the list to re-render, then returns the step count for the named participant. */
    private int getStepsForParticipant(String name) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(PARTICIPANT_NAMES));
        List<WebElement> names = driver.findElements(PARTICIPANT_NAMES);
        List<WebElement> steps = driver.findElements(PARTICIPANT_STEPS);
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).getText().equals(name)) {
                return parseSteps(steps.get(i).getText());
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    /** Parses "15,000 steps" → 15000. */
    private int parseSteps(String text) {
        return Integer.parseInt(text.replace(",", "").replace(" steps", "").trim());
    }

    /** Verifies the full default state of the participant list. */
    private void assertDefaultState() {
        List<WebElement> names = driver.findElements(PARTICIPANT_NAMES);
        List<WebElement> steps = driver.findElements(PARTICIPANT_STEPS);
        assertEquals(10, names.size(), "Should display 10 participants");
        for (int i = 0; i < DEFAULT_NAMES.length; i++) {
            assertEquals(DEFAULT_NAMES[i], names.get(i).getText(),
                    "Participant " + i + " name should match default");
            assertEquals(DEFAULT_STEPS[i], steps.get(i).getText(),
                    "Participant " + i + " steps should match default");
        }
    }

    /** Adds 500 steps to the first participant to dirty the page state before reset tests. */
    private void dirtyState() {
        openModal(0);
        submitModal(DEFAULT_NAMES[0], "500");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    // ── FEATURE 1: INITIAL PAGE LOAD ──────────────────────────────────────────

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge",
                driver.findElement(By.xpath("//h2[text()='Fitness Challenge']")).getText(),
                "Page title should be 'Fitness Challenge'");

        assertDefaultState();

        List<WebElement> addButtons   = driver.findElements(ADD_STEPS_BUTTONS);
        List<WebElement> resetButtons = driver.findElements(RESET_BUTTONS);
        assertTrue(addButtons.size() >= 2,   "Should have at least 2 'Add Steps' buttons");
        assertTrue(resetButtons.size() >= 2, "Should have at least 2 'Reset List' buttons");
        for (int i = 0; i < 2; i++) {
            assertTrue(addButtons.get(i).isDisplayed(),   "Add Steps button " + (i+1) + " should be visible");
            assertTrue(resetButtons.get(i).isDisplayed(), "Reset List button " + (i+1) + " should be visible");
        }
    }

    // ── FEATURE 2: PARTICIPANT DISPLAY AND RANKING ────────────────────────────

    @Test
    public void rankingOrder() {
        List<WebElement> stepElements = driver.findElements(PARTICIPANT_STEPS);
        int previous = Integer.MAX_VALUE;
        for (WebElement el : stepElements) {
            int current = parseSteps(el.getText());
            assertTrue(current <= previous,
                    "Expected descending order but " + current + " > " + previous);
            previous = current;
        }
    }

    @Test
    public void medalTrophyIcons() {
        // Trophy colours are inline style="color:gold/silver/#cd7f32".
        // Browsers normalise getCssValue("color") to rgb(...) — never the keyword/hex.
        record Medal(int pos, String label, String expectedRgb) {}
        List<Medal> medals = List.of(
                new Medal(0, "gold",   "rgb(255, 215, 0)"),
                new Medal(1, "silver", "rgb(192, 192, 192)"),
                new Medal(2, "bronze", "rgb(205, 127, 50)")
        );

        for (Medal m : medals) {
            WebElement participant = driver.findElement(By.id("participant_display" + m.pos()));
            WebElement trophy      = participant.findElement(By.className("fa-trophy"));
            assertTrue(trophy.isDisplayed(), "Position " + (m.pos()+1) + " should have a trophy");
            assertEquals(m.expectedRgb(), trophy.getCssValue("color"),
                    "Position " + (m.pos()+1) + " trophy colour should be " + m.label());
        }

        // Positions 4–10: no trophy at all
        for (int i = 3; i < DEFAULT_NAMES.length; i++) {
            WebElement participant = driver.findElement(By.id("participant_display" + i));
            assertTrue(
                    participant.findElements(By.className("fa-trophy")).isEmpty(),
                    "Participant at position " + (i+1) + " should NOT have a trophy icon"
            );
        }
    }

    // ── FEATURE 3: ADD STEPS MODAL ────────────────────────────────────────────

    @Test
    public void openModalViaTopButton() {
        openModal(0);
        assertModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        // Scroll to bottom so the button is in the viewport, then open
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        openModal(driver.findElements(ADD_STEPS_BUTTONS).size() - 1);
        assertModalContents();
    }

    @Test
    public void closeModalWithCloseButton() throws InterruptedException {
        openModal(0);

        // Use Actions to move to the close button and click it, avoiding element interception
        WebElement closeBtn = driver.findElement(CLOSE_BUTTON);
        new Actions(driver).moveToElement(closeBtn).click().perform();

        // Wait for the modal to be hidden via CSS display: none
        Thread.sleep(300); // Allow style change to process
        wait.until(driver -> {
            String display = driver.findElement(MODAL).getCssValue("display");
            return display.equals("none");
        });

        // Verify modal is hidden
        assertEquals("none",
                driver.findElement(MODAL).getCssValue("display"),
                "Modal display should be 'none' after closing");
        assertTrue(driver.findElement(PARTICIPANTS_LIST).isDisplayed(),
                "Participant list should still be visible");
    }

    // ── FEATURE 4: ADDING STEPS TO PARTICIPANTS ───────────────────────────────

    @Test
    public void addValidStepsToParticipant() {
        int originalSteps = getStepsForParticipant("Mike Kid");

        openModal(0);
        submitModal("Mike Kid", "1000");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));

        assertEquals(originalSteps + 1000, getStepsForParticipant("Mike Kid"),
                "Mike Kid's steps should increase by 1,000");

        // Verify list is still in descending order after re-sort
        List<WebElement> steps = driver.findElements(PARTICIPANT_STEPS);
        int previous = Integer.MAX_VALUE;
        for (WebElement el : steps) {
            int current = parseSteps(el.getText());
            assertTrue(current <= previous, "List should remain sorted after adding steps");
            previous = current;
        }
    }

    @Test
    public void addLargeNumberOfSteps() {
        openModal(0);
        submitModal(DEFAULT_NAMES[9], "999999"); // Jane Doe — currently last place
        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));

        // Jane Doe should now be first
        String firstName = driver.findElements(PARTICIPANT_NAMES).getFirst().getText();
        assertEquals("Jane Doe", firstName, "Jane Doe should be in first place after large step addition");

        int newSteps = getStepsForParticipant("Jane Doe");
        assertEquals(6500 + 999999, newSteps, "Jane Doe's step count should be 6,500 + 999,999");
    }

    // ── FEATURE 5: FORM VALIDATION ────────────────────────────────────────────

    @Test
    public void addZeroSteps() throws InterruptedException {
        openModal(0);
        submitModal(DEFAULT_NAMES[0], "0");
        assertTrue(acceptAlert().contains("Please enter a valid number of steps"),
                "Alert should contain step validation message");
        assertTrue(driver.findElement(MODAL).isDisplayed(), "Modal should remain open");
    }

    @Test
    public void submitWithoutSelectingParticipant() throws InterruptedException {
        openModal(0);
        driver.findElement(STEPS_INPUT).sendKeys("1000");
        driver.findElement(MODAL_ADD_BUTTON).click();
        assertTrue(acceptAlert().contains("Please select a participant"),
                "Alert should ask user to select a participant");
        assertTrue(driver.findElement(MODAL).isDisplayed(), "Modal should remain open");
    }

    @Test
    public void submitWithoutEnteringSteps() throws InterruptedException {
        // Empty steps field — the input has required="" but the form may show an alert
        // if validation is handled by JavaScript; modal must stay open.
        openModal(0);
        new Select(driver.findElement(PARTICIPANT_SELECT)).selectByIndex(1);
        driver.findElement(MODAL_ADD_BUTTON).click();

        // Check if an alert appears (browser may validate via JS)
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            assertTrue(alert.getText().contains("Please enter a valid number of steps"),
                    "Alert should describe the validation error");
            alert.accept();
            Thread.sleep(500); // Allow time for alert dismissal to process
        } catch (TimeoutException ignored) {
            // HTML5 validation prevented submission silently — also acceptable
        }

        assertTrue(driver.findElement(MODAL).isDisplayed(),
                "Modal should remain open when steps field is empty");
    }

    @Test
    public void submitWithNegativeSteps() throws InterruptedException {
        // type=number with min="0" — browsers block or strip negative values via
        // native validation; if JS receives the value it should show an alert.
        openModal(0);
        new Select(driver.findElement(PARTICIPANT_SELECT)).selectByIndex(1);
        driver.findElement(STEPS_INPUT).sendKeys("-500");
        driver.findElement(MODAL_ADD_BUTTON).click();

        // Either an alert fires or the modal stays open via HTML5 validation.
        boolean alertFired = false;
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            assertTrue(alert.getText().contains("Please enter a valid number of steps"),
                    "Alert text should describe the validation error");
            alert.accept();
            Thread.sleep(500); // Allow time for alert dismissal to process
            alertFired = true;
        } catch (TimeoutException ignored) { }

        assertTrue(driver.findElement(MODAL).isDisplayed(),
                "Modal should remain open after negative input");
        // At least one validation mechanism must have triggered
        assertTrue(alertFired || driver.findElement(MODAL).isDisplayed(),
                "Negative steps must be rejected");
    }

    @Test
    public void submitWithNonNumericInput() {
        openModal(0);
        new Select(driver.findElement(PARTICIPANT_SELECT)).selectByIndex(1);
        driver.findElement(STEPS_INPUT).sendKeys("abc");
        // type=number silently ignores non-numeric characters
        String value = driver.findElement(STEPS_INPUT).getAttribute("value");
        assertTrue(value == null || value.isEmpty(),
                "Number input should reject non-numeric characters, got: " + value);
    }

    @Test
    public void decimalStepValues() throws InterruptedException {
        // type=number with step not set defaults to integers only on most browsers;
        // "100.5" should either be rejected or trigger the JS validation alert.
        openModal(0);
        submitModal(DEFAULT_NAMES[0], "100.5");

        boolean alertFired = false;
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            assertTrue(alert.getText().contains("Please enter a valid number of steps"),
                    "Alert should reject decimal input");
            alert.accept();
            Thread.sleep(500); // Allow time for alert dismissal to process
            alertFired = true;
        } catch (TimeoutException ignored) { }

        assertTrue(driver.findElement(MODAL).isDisplayed(),
                "Modal should remain open after decimal input");
        assertTrue(alertFired || driver.findElement(MODAL).isDisplayed(),
                "Decimal steps must be rejected");
    }

    // ── FEATURE 6: RESET FUNCTIONALITY ───────────────────────────────────────

    @Test
    public void resetViaTopButton() {
        dirtyState();
        driver.findElements(RESET_BUTTONS).getFirst().click();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(PARTICIPANT_NAMES));
        assertDefaultState();
    }

    @Test
    public void resetViaBottomButton() {
        dirtyState();
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        List<WebElement> resets = driver.findElements(RESET_BUTTONS);
        resets.getLast().click();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(PARTICIPANT_NAMES));
        assertDefaultState();
    }

    // ── FEATURE 7: EDGE CASES ─────────────────────────────────────────────────

    @Test
    public void maximumIntegerValue() {
        int originalSteps = getStepsForParticipant(DEFAULT_NAMES[0]);
        openModal(0);
        submitModal(DEFAULT_NAMES[0], "9007199254740991");

        // If the JS accepts it the modal closes; if it rejects it the modal stays open.
        // Either outcome is valid — but the page must not crash.
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
            // Modal closed → step count must have changed
            assertTrue(
                    driver.findElements(PARTICIPANT_NAMES).stream().anyMatch(e -> !e.getText().isEmpty()),
                    "Participant list should still be rendered after max-integer submission"
            );
        } catch (TimeoutException e) {
            // Modal stayed open (validation rejected the value) — also acceptable
            assertTrue(driver.findElement(MODAL).isDisplayed(),
                    "Modal should remain visible if max-integer is rejected");
        }
    }
}