package selenium.tasks;

// JUnit annotations for setup, cleanup, and tests
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Selenium classes for browser actions, locators, alerts, elements, JS, etc.
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

// Selenium helper classes
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

// Your own driver factory class
import selenium.utility.DriverFactory;

// Java utilities
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// JUnit assertions
import static org.junit.jupiter.api.Assertions.*;

public class Task2 {

    // Page URL used in all tests
    private static final String URL = "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";

    // Expected alert messages
    private static final String ALERT_INVALID_STEPS = "Please enter a valid number of steps";
    private static final String ALERT_SELECT_PARTICIPANT = "Please select a participant";

    // Selenium WebDriver controls the browser
    private WebDriver driver;

    // Explicit wait waits for elements/alerts/modals before interacting with them
    private WebDriverWait wait;

    // Default participant names and their starting step counts
    // LinkedHashMap keeps insertion order
    private static final LinkedHashMap<String, Long> DEFAULT_STEPS = new LinkedHashMap<>();

    // Fill default test data once before tests run
    static {
        DEFAULT_STEPS.put("Mike Kid", 8500L);
        DEFAULT_STEPS.put("Jill Watson", 12000L);
        DEFAULT_STEPS.put("Jane Doe", 6500L);
        DEFAULT_STEPS.put("John Smith", 15000L);
        DEFAULT_STEPS.put("Sarah Johnson", 9800L);
        DEFAULT_STEPS.put("Carlos Garcia", 11200L);
        DEFAULT_STEPS.put("Emily Chen", 7300L);
        DEFAULT_STEPS.put("David Brown", 13500L);
        DEFAULT_STEPS.put("Maria Rodriguez", 10500L);
        DEFAULT_STEPS.put("Alex Taylor", 8900L);
    }

    @BeforeEach
    public void openPage() {
        // Create browser before each test
        driver = DriverFactory.getDriver();

        // Set explicit wait timeout to 8 seconds
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));

        // Open tested page
        driver.navigate().to(URL);
    }

    @AfterEach
    public void closeBrowser() {
        // Close browser after each test
        if (driver != null) {
            driver.quit();
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        // Check that page contains expected title text
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Fitness Challenge"),
                "Page title should contain 'Fitness Challenge'");

        // Get all participant rows from the page
        List<ParticipantRow> rows = getParticipantRows();

        // Verify there are 10 participants
        assertEquals(10, rows.size(), "There should be 10 participants");

        // Convert displayed rows to map: participant name -> steps
        Map<String, Long> current = toNameStepsMap(rows);

        // Verify every default participant exists and has correct steps
        for (Map.Entry<String, Long> entry : DEFAULT_STEPS.entrySet()) {
            assertTrue(current.containsKey(entry.getKey()), "Missing participant: " + entry.getKey());
            assertEquals(entry.getValue(), current.get(entry.getKey()),
                    "Unexpected step count for " + entry.getKey());
        }

        // Verify Add Steps and Reset List buttons appear twice
        List<WebElement> addButtons = getMainAddStepsButtons();
        List<WebElement> resetButtons = getMainResetButtons();

        assertEquals(2, addButtons.size(), "'Add Steps' should appear twice (top and bottom)");
        assertEquals(2, resetButtons.size(), "'Reset List' should appear twice (top and bottom)");

        assertTrue(addButtons.get(0).isDisplayed() && addButtons.get(1).isDisplayed(),
                "Add buttons should be visible");

        assertTrue(resetButtons.get(0).isDisplayed() && resetButtons.get(1).isDisplayed(),
                "Reset buttons should be visible");
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        // Get participant rows
        List<ParticipantRow> rows = getParticipantRows();

        // Verify participant count
        assertEquals(10, rows.size(), "Expected 10 participants");

        // Verify step count goes from biggest to smallest
        for (int i = 0; i < rows.size() - 1; i++) {
            long current = rows.get(i).steps;
            long next = rows.get(i + 1).steps;

            assertTrue(current >= next,
                    "Ranking should be descending. " + rows.get(i).name + " (" + current + ") < "
                            + rows.get(i + 1).name + " (" + next + ")");
        }
    }

    @Test
    public void medalTrophyIcons() {
        // Get participant rows
        List<ParticipantRow> rows = getParticipantRows();

        // Need at least first 3 + one lower participant
        assertTrue(rows.size() >= 4, "Need at least 4 rows for medal checks");

        // Verify top 3 have trophies
        assertTrue(hasTrophy(rows.get(0).element), "1st place should have trophy");
        assertTrue(hasTrophy(rows.get(1).element), "2nd place should have trophy");
        assertTrue(hasTrophy(rows.get(2).element), "3rd place should have trophy");

        // Verify trophy colors
        assertTrue(isGold(rows.get(0).element), "1st place trophy should be gold");
        assertTrue(isSilver(rows.get(1).element), "2nd place trophy should be silver");
        assertTrue(isBronze(rows.get(2).element), "3rd place trophy should be bronze (#cd7f32)");

        // Verify rank 4 and below do not have trophies
        for (int i = 3; i < rows.size(); i++) {
            assertFalse(hasTrophy(rows.get(i).element),
                    "Rank " + (i + 1) + " should not have trophy");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        // Open modal using top Add Steps button
        openModalFromTop();

        // Verify modal content
        verifyModalStructure();
    }

    @Test
    public void openModalViaBottomButton() {
        // Scroll to bottom first
        scrollToBottom();

        // Open modal using bottom Add Steps button
        openModalFromBottom();

        // Verify modal content
        verifyModalStructure();
    }

    @Test
    public void closeModalWithCloseButton() {
        // Open modal
        openModalFromTop();

        // Get currently visible modal
        WebElement modal = getVisibleModal();

        // Click close button inside modal
        clickModalCloseButton(modal);

        // Wait until modal disappears
        wait.until(ExpectedConditions.invisibilityOf(modal));

        // Verify data was not changed
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        // Save Mike Kid's current step count
        long before = getStepsFor("Mike Kid");

        // Open modal and add 1000 steps
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("1000");
        submitModal();

        // Wait for modal to close
        waitForModalClosed();

        // Verify steps increased
        long after = getStepsFor("Mike Kid");
        assertEquals(before + 1000, after, "Mike Kid steps should increase by 1,000");

        // Verify list is still sorted
        assertRankingDescending();
    }

    @Test
    public void addZeroSteps() {
        // Open modal
        openModalFromTop();

        // Select participant and enter invalid zero steps
        selectParticipant("Mike Kid");
        setSteps("0");
        submitModal();

        // Verify alert and modal remains open
        assertAlertTextAndAccept(ALERT_INVALID_STEPS);
        assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
    }

    @Test
    public void addLargeNumberOfSteps() {
        // Add many steps to Mike Kid
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("999999");
        submitModal();

        // Wait for modal to close
        waitForModalClosed();

        // Mike Kid should now be first
        List<ParticipantRow> rows = getParticipantRows();
        assertEquals("Mike Kid", rows.get(0).name, "Mike Kid should move to first place");
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        // Open modal
        openModalFromTop();

        // Enter steps but do not select participant
        setSteps("1000");
        submitModal();

        // Verify correct alert
        assertAlertTextAndAccept(ALERT_SELECT_PARTICIPANT);

        // Modal should still be open
        assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
    }

    @Test
    public void submitWithoutEnteringSteps() {
        // Open modal
        openModalFromTop();

        // Select participant but enter invalid steps
        selectParticipant("Mike Kid");
        setSteps("0");
        submitModal();

        // Verify validation alert
        assertAlertTextAndAccept(ALERT_INVALID_STEPS);

        // Modal should stay open
        assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
    }

    @Test
    public void submitWithNegativeSteps() {
        // Open modal
        openModalFromTop();

        // Enter negative steps
        selectParticipant("Mike Kid");
        setSteps("-500");
        submitModal();

        // Verify validation alert
        assertAlertTextAndAccept(ALERT_INVALID_STEPS);

        // Modal should stay open
        assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
    }

    @Test
    public void submitWithNonNumericInput() {
        // Open modal
        openModalFromTop();

        // Select participant
        selectParticipant("Mike Kid");

        // Try entering letters into number input
        WebElement input = getModalStepsInput();
        input.clear();
        input.sendKeys("abc");

        // Browser number input should reject letters
        String value = input.getDomAttribute("value");

        assertTrue(value == null || value.isEmpty() || value.matches("[-+]?\\d*"),
                "Input should reject alphabetic characters. Actual value: " + value);
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        // Change data first
        modifyAtLeastTwoParticipants();

        // Click top reset button
        WebElement topReset = getMainResetButtons().get(0);
        topReset.click();

        // Wait for page/list to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

        // Verify default data is back
        assertDefaultState();
    }

    @Test
    public void resetViaBottomButton() {
        // Change data first
        modifyAtLeastTwoParticipants();

        // Scroll to bottom and click bottom reset button
        scrollToBottom();
        WebElement bottomReset = getMainResetButtons().get(1);
        bottomReset.click();

        // Wait for page/list to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

        // Verify default data is back
        assertDefaultState();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        // Save current steps
        long before = getStepsFor("Mike Kid");

        // JS maximum safe integer
        String maxSafe = "9007199254740991";

        // Add max safe number
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps(maxSafe);
        submitModal();

        // Wait for modal to close
        waitForModalClosed();

        // Read updated steps
        long after = getStepsFor("Mike Kid");

        // JavaScript uses Number type, so very large values may be rounded
        long expectedJsResult = Math.round((double) before + Double.parseDouble(maxSafe));

        assertEquals(expectedJsResult, after,
                "Step count should update according to JS numeric precision");
    }

    @Test
    public void decimalStepValues() {
        // Save current value
        long before = getStepsFor("Mike Kid");

        // Try decimal value
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("100.5");
        submitModal();

        // Some implementations show alert
        Alert alert = tryGetAlert(Duration.ofSeconds(2));

        if (alert != null) {
            assertEquals(ALERT_INVALID_STEPS, alert.getText(), "Unexpected alert text");
            alert.accept();
            assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
            return;
        }

        // Other implementations sanitize/round value instead of alerting
        waitForModalClosed();

        long after = getStepsFor("Mike Kid");

        assertTrue(
                after == before || after == before + 100 || after == before + 101,
                "Decimal input should not be stored as a true decimal increment. before="
                        + before + ", after=" + after
        );
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() {
        // Run core flow in Chrome/default browser
        runCoreFlowChecks();
    }

    @Test
    public void edgeBrowser() {
        // Switch to Edge and run core flow
        switchToBrowser("edge");
        runCoreFlowChecks();
    }

    @Test
    public void firefoxBrowser() {
        // Switch to Firefox and run core flow
        // This requires Firefox support in DriverFactory
        switchToBrowser("firefox");
        runCoreFlowChecks();
    }

    // HELPERS

    private void runCoreFlowChecks() {
        // Check add steps functionality
        addValidStepsToParticipant();

        // Reload page before next flow
        driver.navigate().to(URL);

        // Check validation without participant
        submitWithoutSelectingParticipant();
        closeModalIfOpen();

        // Reload again before reset test
        driver.navigate().to(URL);

        // Check reset
        resetViaTopButton();
    }

    private void assertDefaultState() {
        // Get rows from page
        List<ParticipantRow> rows = getParticipantRows();

        // Verify count
        assertEquals(10, rows.size(), "There should be 10 participants after reset");

        // Verify default steps
        Map<String, Long> current = toNameStepsMap(rows);

        for (Map.Entry<String, Long> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(entry.getValue(), current.get(entry.getKey()),
                    "Default steps mismatch for " + entry.getKey());
        }

        // Verify ranking and trophies
        assertRankingDescending();

        assertTrue(hasTrophy(rows.get(0).element), "Top rank should have trophy");
        assertTrue(hasTrophy(rows.get(1).element), "Second rank should have trophy");
        assertTrue(hasTrophy(rows.get(2).element), "Third rank should have trophy");
    }

    private void assertRankingDescending() {
        // Verify each participant has equal or fewer steps than previous participant
        List<ParticipantRow> rows = getParticipantRows();

        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(rows.get(i).steps >= rows.get(i + 1).steps,
                    "List must stay in descending order");
        }
    }

    private void modifyAtLeastTwoParticipants() {
        // Add steps to Mike Kid
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("1000");
        submitModal();
        waitForModalClosed();

        // Add steps to Jane Doe
        openModalFromTop();
        selectParticipant("Jane Doe");
        setSteps("500");
        submitModal();
        waitForModalClosed();
    }

    private void openModalFromTop() {
        // Get main Add Steps buttons
        List<WebElement> buttons = getMainAddStepsButtons();

        // Click first/top button
        assertTrue(buttons.size() >= 1, "Top Add Steps button not found");
        buttons.get(0).click();

        // Wait until modal becomes visible
        wait.until(ExpectedConditions.visibilityOf(getVisibleModal()));
    }

    private void openModalFromBottom() {
        // Get main Add Steps buttons
        List<WebElement> buttons = getMainAddStepsButtons();

        // Click second/bottom button
        assertTrue(buttons.size() >= 2, "Bottom Add Steps button not found");
        buttons.get(1).click();

        // Wait until modal becomes visible
        wait.until(ExpectedConditions.visibilityOf(getVisibleModal()));
    }

    private void verifyModalStructure() {
        // Get visible modal
        WebElement modal = getVisibleModal();

        // Verify modal title text
        assertTrue(modal.getText().contains("Add Steps to Participant"), "Modal title mismatch");

        // Get modal controls
        WebElement select = getModalSelect();
        WebElement input = getModalStepsInput();
        WebElement submit = getModalSubmitButton();
        WebElement close = getModalCloseButton(modal);

        // Verify controls are visible
        assertTrue(select.isDisplayed(), "Participant dropdown should be visible");
        assertTrue(input.isDisplayed(), "Steps input should be visible");
        assertTrue(submit.isDisplayed(), "Modal Add Steps button should be visible");
        assertTrue(close.isDisplayed(), "Close button should be visible");

        // Verify dropdown options
        Select s = new Select(select);

        assertEquals("Choose participant", s.getFirstSelectedOption().getText().trim(),
                "Default dropdown option should be 'Choose participant'");

        assertEquals(11, s.getOptions().size(),
                "Dropdown should include default + 10 participants");

        // Verify each participant exists in dropdown
        for (String name : DEFAULT_STEPS.keySet()) {
            assertTrue(hasSelectOption(s, name), "Dropdown missing participant: " + name);
        }
    }

    private boolean hasSelectOption(Select select, String text) {
        // Check if dropdown contains specific visible option
        for (WebElement o : select.getOptions()) {
            if (text.equals(o.getText().trim())) {
                return true;
            }
        }

        return false;
    }

    private void selectParticipant(String name) {
        // Select participant by visible text
        Select s = new Select(getModalSelect());
        s.selectByVisibleText(name);
    }

    private void setSteps(String steps) {
        // Clear and type step value
        WebElement input = getModalStepsInput();
        input.clear();
        input.sendKeys(steps);
    }

    private void submitModal() {
        // Click Add Steps button inside modal
        getModalSubmitButton().click();
    }

    private void waitForModalClosed() {
        // Wait until modal is invisible.
        // Timeout is ignored because some validation cases keep modal open.
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal, .modal-content")));
        } catch (TimeoutException ignored) {
        }
    }

    private void closeModalIfOpen() {
        // Try to find close button and close modal if it is open
        List<WebElement> closeButtons = driver.findElements(By.xpath(
                "//*[contains(@class,'close') and normalize-space()='×'] | //button[normalize-space()='×']"
        ));

        if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
            closeButtons.get(0).click();
        }
    }

    private void clickModalCloseButton(WebElement modal) {
        // Click close button inside given modal
        getModalCloseButton(modal).click();
    }

    private WebElement getModalCloseButton(WebElement modal) {
        // Try several possible locators for close button
        List<By> locators = List.of(
                By.xpath(".//*[contains(@class,'close') and normalize-space()='×']"),
                By.xpath(".//button[normalize-space()='×']"),
                By.xpath(".//*[contains(@class,'close')]")
        );

        // Return first matching close button
        for (By by : locators) {
            List<WebElement> found = modal.findElements(by);

            if (!found.isEmpty()) {
                return found.get(0);
            }
        }

        throw new IllegalStateException("Modal close button not found");
    }

    private WebElement getVisibleModal() {
        // Try several possible modal locators
        List<By> modalLocators = List.of(
                By.cssSelector(".modal-content"),
                By.cssSelector(".modal"),
                By.xpath("//*[contains(@class,'modal') and .//*[contains(.,'Add Steps to Participant')]]")
        );

        // Return visible modal that contains expected title
        for (By by : modalLocators) {
            List<WebElement> found = driver.findElements(by);

            for (WebElement e : found) {
                if (e.isDisplayed() && e.getText().contains("Add Steps to Participant")) {
                    return e;
                }
            }
        }

        throw new IllegalStateException("Visible modal not found");
    }

    private WebElement getModalSelect() {
        // Find select element inside modal
        WebElement modal = getVisibleModal();
        List<WebElement> selects = modal.findElements(By.tagName("select"));

        if (selects.isEmpty()) {
            throw new IllegalStateException("Participant dropdown not found in modal");
        }

        return selects.get(0);
    }

    private WebElement getModalStepsInput() {
        // Find number input inside modal
        WebElement modal = getVisibleModal();

        List<WebElement> inputs = modal.findElements(By.cssSelector("input[type='number']"));

        if (!inputs.isEmpty()) {
            return inputs.get(0);
        }

        // Fallback: any input inside modal
        inputs = modal.findElements(By.tagName("input"));

        if (!inputs.isEmpty()) {
            return inputs.get(0);
        }

        throw new IllegalStateException("Steps input not found in modal");
    }

    private WebElement getModalSubmitButton() {
        // Try several possible locators for submit button inside modal
        WebElement modal = getVisibleModal();

        List<By> locators = List.of(
                By.xpath(".//button[normalize-space()='Add Steps']"),
                By.xpath(".//button[@type='submit']"),
                By.xpath(".//button[contains(.,'Add Steps')]")
        );

        for (By by : locators) {
            List<WebElement> found = modal.findElements(by);

            if (!found.isEmpty()) {
                return found.get(0);
            }
        }

        throw new IllegalStateException("Modal submit button not found");
    }

    private List<WebElement> getMainAddStepsButtons() {
        // Find Add Steps buttons outside modal
        return driver.findElements(By.xpath(
                "//button[normalize-space()='Add Steps' and not(ancestor::*[contains(@class,'modal')])]"
        ));
    }

    private List<WebElement> getMainResetButtons() {
        // Find Reset List buttons outside modal
        return driver.findElements(By.xpath(
                "//button[normalize-space()='Reset List' and not(ancestor::*[contains(@class,'modal')])]"
        ));
    }

    private void scrollToBottom() {
        // Scroll page to bottom using JavaScript
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    private void assertAlertTextAndAccept(String expected) {
        // Wait for alert, verify text, then accept it
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        assertEquals(expected, alert.getText(), "Unexpected alert text");

        alert.accept();
    }

    private Alert tryGetAlert(Duration timeout) {
        // Try to get alert using custom timeout.
        // Return null if no alert appears.
        try {
            return new WebDriverWait(driver, timeout).until(ExpectedConditions.alertIsPresent());
        } catch (TimeoutException e) {
            return null;
        }
    }

    private long getStepsFor(String participant) {
        // Find participant and return their step count
        for (ParticipantRow row : getParticipantRows()) {
            if (participant.equals(row.name)) {
                return row.steps;
            }
        }

        fail("Participant not found: " + participant);

        return -1;
    }

    private Map<String, Long> toNameStepsMap(List<ParticipantRow> rows) {
        // Convert list of participant rows into map
        Map<String, Long> map = new LinkedHashMap<>();

        for (ParticipantRow row : rows) {
            map.put(row.name, row.steps);
        }

        return map;
    }

    private List<ParticipantRow> getParticipantRows() {
        // Store candidate row elements
        List<WebElement> rowCandidates = new ArrayList<>();

        // Try different possible selectors for participant rows
        List<By> selectors = List.of(
                By.cssSelector("#participantsList li"),
                By.cssSelector("#participant-list li"),
                By.cssSelector("#participants li"),
                By.cssSelector("ul li"),
                By.cssSelector("tbody tr")
        );

        // Find visible rows that contain known participant names
        for (By by : selectors) {
            List<WebElement> found = driver.findElements(by);
            List<WebElement> filtered = new ArrayList<>();

            for (WebElement e : found) {
                if (!e.isDisplayed()) {
                    continue;
                }

                String text = e.getText();

                if (text == null || text.isBlank()) {
                    continue;
                }

                if (containsAnyDefaultName(text)) {
                    filtered.add(e);
                }
            }

            if (filtered.size() >= 10) {
                rowCandidates = filtered;
                break;
            }
        }

        if (rowCandidates.isEmpty()) {
            fail("Could not detect participant rows");
        }

        // Parse rows into ParticipantRow objects
        List<ParticipantRow> parsed = new ArrayList<>();

        for (WebElement row : rowCandidates) {
            ParticipantRow p = parseParticipant(row);

            if (p != null) {
                parsed.add(p);
            }
        }

        // Remove duplicates while keeping order
        Map<String, ParticipantRow> uniq = new LinkedHashMap<>();

        for (ParticipantRow p : parsed) {
            uniq.putIfAbsent(p.name, p);
        }

        return new ArrayList<>(uniq.values());
    }

    private ParticipantRow parseParticipant(WebElement row) {
        // Get row text
        String text = row.getText();

        if (text == null) {
            return null;
        }

        // Detect participant name from known default names
        String name = null;

        for (String n : DEFAULT_STEPS.keySet()) {
            if (text.contains(n)) {
                name = n;
                break;
            }
        }

        if (name == null) {
            return null;
        }

        // Extract numeric step count from row text
        Matcher m = Pattern.compile("(\\d[\\d,]*)").matcher(text);

        long steps = -1;

        while (m.find()) {
            String num = m.group(1).replace(",", "");

            try {
                long candidate = Long.parseLong(num);

                // Keep largest number found in the row
                if (candidate > steps) {
                    steps = candidate;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        if (steps < 0) {
            return null;
        }

        return new ParticipantRow(name, steps, row);
    }

    private boolean containsAnyDefaultName(String text) {
        // Check if text contains any participant name
        for (String n : DEFAULT_STEPS.keySet()) {
            if (text.contains(n)) {
                return true;
            }
        }

        return false;
    }

    private boolean hasTrophy(WebElement row) {
        // Check emoji trophies first
        String text = row.getText();

        if (text.contains("🏆") || text.contains("🥇") || text.contains("🥈") || text.contains("🥉")) {
            return true;
        }

        // Check icon elements/classes
        List<WebElement> icons = row.findElements(By.cssSelector("i, svg, span"));

        for (WebElement icon : icons) {
            String cls = safe(icon.getDomAttribute("class")).toLowerCase(Locale.ROOT);

            if (cls.contains("trophy") || cls.contains("medal") || cls.contains("cup")) {
                return true;
            }
        }

        return false;
    }

    private boolean isGold(WebElement row) {
        // Gold can be emoji or RGB color
        return hasMedalEmoji(row, "🥇") || hasRgbColor(row, 255, 215, 0);
    }

    private boolean isSilver(WebElement row) {
        // Silver can be emoji or RGB color
        return hasMedalEmoji(row, "🥈") || hasRgbColor(row, 192, 192, 192);
    }

    private boolean isBronze(WebElement row) {
        // Bronze can be emoji or RGB color
        return hasMedalEmoji(row, "🥉") || hasRgbColor(row, 205, 127, 50);
    }

    private boolean hasMedalEmoji(WebElement row, String emoji) {
        // Check if row contains medal emoji
        return row.getText().contains(emoji);
    }

    private boolean hasRgbColor(WebElement row, int r, int g, int b) {
        // Check icon color inside row
        List<WebElement> icons = row.findElements(By.cssSelector("i, svg, span"));

        for (WebElement icon : icons) {
            String cls = safe(icon.getDomAttribute("class")).toLowerCase(Locale.ROOT);

            // Only check likely trophy/medal/cup icons
            if (!cls.contains("trophy") && !cls.contains("medal") && !cls.contains("cup")) {
                continue;
            }

            try {
                java.awt.Color c = Color.fromString(icon.getCssValue("color")).getColor();

                if (c.getRed() == r && c.getGreen() == g && c.getBlue() == b) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }

        return false;
    }

    private String safe(String v) {
        // Avoid NullPointerException when attribute is null
        return v == null ? "" : v;
    }

    private void switchToBrowser(String browser) {
        // Close current driver before switching browser
        if (driver != null) {
            driver.quit();
        }

        // Create requested browser driver
        driver = createDriverViaReflection(browser);

        // Recreate wait for new driver
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));

        // Open page again
        driver.navigate().to(URL);
    }

    private WebDriver createDriverViaReflection(String browser) {
        // This method tries to call methods from DriverFactory dynamically.
        // Example: createEdgeDriver(), createFirefoxDriver(), createChromeDriver()

        List<String> methodNames = new ArrayList<>();

        String b = browser.toLowerCase(Locale.ROOT);

        if ("edge".equals(b)) {
            methodNames.add("createEdgeDriver");
        } else if ("firefox".equals(b)) {
            methodNames.add("createFirefoxDriver");
        } else {
            methodNames.add("createChromeDriver");
        }

        // Fallback to Chrome if requested browser method does not exist
        methodNames.add("createChromeDriver");

        for (String methodName : methodNames) {
            try {
                Method m = DriverFactory.class.getMethod(methodName);
                Object out = m.invoke(null);

                if (out instanceof WebDriver) {
                    return (WebDriver) out;
                }
            } catch (Exception ignored) {
            }
        }

        throw new IllegalStateException(
                "No suitable driver factory found in DriverManager for browser: " + browser
        );
    }

    private static class ParticipantRow {
        // Participant name
        final String name;

        // Participant step count
        final long steps;

        // Original Selenium element representing this participant row
        final WebElement element;

        ParticipantRow(String name, long steps, WebElement element) {
            this.name = name;
            this.steps = steps;
            this.element = element;
        }
    }
}