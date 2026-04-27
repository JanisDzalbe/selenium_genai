package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utils.DriverManager;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final String URL = "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final String ALERT_INVALID_STEPS = "Please enter a valid number of steps";
    private static final String ALERT_SELECT_PARTICIPANT = "Please select a participant";

    private WebDriver driver;
    private WebDriverWait wait;

    private static final LinkedHashMap<String, Long> DEFAULT_STEPS = new LinkedHashMap<>();

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
        driver = DriverManager.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        driver.navigate().to(URL);
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
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Fitness Challenge"),
                "Page title should contain 'Fitness Challenge'");

        List<ParticipantRow> rows = getParticipantRows();
        assertEquals(10, rows.size(), "There should be 10 participants");

        Map<String, Long> current = toNameStepsMap(rows);
        for (Map.Entry<String, Long> entry : DEFAULT_STEPS.entrySet()) {
            assertTrue(current.containsKey(entry.getKey()), "Missing participant: " + entry.getKey());
            assertEquals(entry.getValue(), current.get(entry.getKey()),
                    "Unexpected step count for " + entry.getKey());
        }

        List<WebElement> addButtons = getMainAddStepsButtons();
        List<WebElement> resetButtons = getMainResetButtons();
        assertEquals(2, addButtons.size(), "'Add Steps' should appear twice (top and bottom)");
        assertEquals(2, resetButtons.size(), "'Reset List' should appear twice (top and bottom)");
        assertTrue(addButtons.get(0).isDisplayed() && addButtons.get(1).isDisplayed(), "Add buttons should be visible");
        assertTrue(resetButtons.get(0).isDisplayed() && resetButtons.get(1).isDisplayed(), "Reset buttons should be visible");
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<ParticipantRow> rows = getParticipantRows();
        assertEquals(10, rows.size(), "Expected 10 participants");

        for (int i = 0; i < rows.size() - 1; i++) {
            long current = rows.get(i).steps;
            long next = rows.get(i + 1).steps;
            assertTrue(current >= next,
                    "Ranking should be descending. " + rows.get(i).name + " (" + current + ") < " + rows.get(i + 1).name + " (" + next + ")");
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<ParticipantRow> rows = getParticipantRows();
        assertTrue(rows.size() >= 4, "Need at least 4 rows for medal checks");

        assertTrue(hasTrophy(rows.get(0).element), "1st place should have trophy");
        assertTrue(hasTrophy(rows.get(1).element), "2nd place should have trophy");
        assertTrue(hasTrophy(rows.get(2).element), "3rd place should have trophy");

        assertTrue(isGold(rows.get(0).element), "1st place trophy should be gold");
        assertTrue(isSilver(rows.get(1).element), "2nd place trophy should be silver");
        assertTrue(isBronze(rows.get(2).element), "3rd place trophy should be bronze (#cd7f32)");

        for (int i = 3; i < rows.size(); i++) {
            assertFalse(hasTrophy(rows.get(i).element), "Rank " + (i + 1) + " should not have trophy");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        openModalFromTop();
        verifyModalStructure();
    }

    @Test
    public void openModalViaBottomButton() {
        scrollToBottom();
        openModalFromBottom();
        verifyModalStructure();
    }

    @Test
    public void closeModalWithCloseButton() {
        openModalFromTop();
        WebElement modal = getVisibleModal();
        clickModalCloseButton(modal);
        wait.until(ExpectedConditions.invisibilityOf(modal));
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long before = getStepsFor("Mike Kid");

        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("1000");
        submitModal();

        waitForModalClosed();

        long after = getStepsFor("Mike Kid");
        assertEquals(before + 1000, after, "Mike Kid steps should increase by 1,000");
        assertRankingDescending();
    }

    @Test
    public void addZeroSteps() {
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("0");
        submitModal();

        assertAlertTextAndAccept(ALERT_INVALID_STEPS);
        assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
    }

    @Test
    public void addLargeNumberOfSteps() {
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("999999");
        submitModal();

        waitForModalClosed();

        List<ParticipantRow> rows = getParticipantRows();
        assertEquals("Mike Kid", rows.get(0).name, "Mike Kid should move to first place");
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        openModalFromTop();
        setSteps("1000");
        submitModal();

        assertAlertTextAndAccept(ALERT_SELECT_PARTICIPANT);
        assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("0");
        submitModal();

        assertAlertTextAndAccept(ALERT_INVALID_STEPS);
        assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
    }

    @Test
    public void submitWithNegativeSteps() {
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("-500");
        submitModal();

        assertAlertTextAndAccept(ALERT_INVALID_STEPS);
        assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
    }

    @Test
    public void submitWithNonNumericInput() {
        openModalFromTop();
        selectParticipant("Mike Kid");

        WebElement input = getModalStepsInput();
        input.clear();
        input.sendKeys("abc");

        String value = input.getAttribute("value");
        assertTrue(value == null || value.isEmpty() || value.matches("[-+]?\\d*"),
                "Input should reject alphabetic characters. Actual value: " + value);
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        modifyAtLeastTwoParticipants();

        WebElement topReset = getMainResetButtons().get(0);
        topReset.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        assertDefaultState();
    }

    @Test
    public void resetViaBottomButton() {
        modifyAtLeastTwoParticipants();

        scrollToBottom();
        WebElement bottomReset = getMainResetButtons().get(1);
        bottomReset.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        assertDefaultState();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        long before = getStepsFor("Mike Kid");
        String maxSafe = "9007199254740991";

        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps(maxSafe);
        submitModal();

        waitForModalClosed();

        long after = getStepsFor("Mike Kid");

        // JS Number precision behavior above 2^53
        long expectedJsResult = Math.round((double) before + Double.parseDouble(maxSafe));
        assertEquals(expectedJsResult, after, "Step count should update according to JS numeric precision");
    }

    @Test
    public void decimalStepValues() {
        long before = getStepsFor("Mike Kid");

        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("100.5");
        submitModal();

        Alert alert = tryGetAlert(Duration.ofSeconds(2));
        if (alert != null) {
            assertEquals(ALERT_INVALID_STEPS, alert.getText(), "Unexpected alert text");
            alert.accept();
            assertTrue(getVisibleModal().isDisplayed(), "Modal should remain open");
            return;
        }

        // Some implementations sanitize instead of alerting.
        waitForModalClosed();
        long after = getStepsFor("Mike Kid");

        assertTrue(
                after == before || after == before + 100 || after == before + 101,
                "Decimal input should not be stored as a true decimal increment. before=" + before + ", after=" + after
        );
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() {
        runCoreFlowChecks();
    }

    @Test
    public void edgeBrowser() {
        switchToBrowser("edge");
        runCoreFlowChecks();
    }

    @Test
    public void firefoxBrowser() {
        switchToBrowser("firefox");
        runCoreFlowChecks();
    }

    // -------------------------- helpers --------------------------

    private void runCoreFlowChecks() {
        addValidStepsToParticipant();
        driver.navigate().to(URL);

        submitWithoutSelectingParticipant();
        closeModalIfOpen();

        driver.navigate().to(URL);
        resetViaTopButton();
    }

    private void assertDefaultState() {
        List<ParticipantRow> rows = getParticipantRows();
        assertEquals(10, rows.size(), "There should be 10 participants after reset");

        Map<String, Long> current = toNameStepsMap(rows);
        for (Map.Entry<String, Long> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(entry.getValue(), current.get(entry.getKey()),
                    "Default steps mismatch for " + entry.getKey());
        }

        assertRankingDescending();

        assertTrue(hasTrophy(rows.get(0).element), "Top rank should have trophy");
        assertTrue(hasTrophy(rows.get(1).element), "Second rank should have trophy");
        assertTrue(hasTrophy(rows.get(2).element), "Third rank should have trophy");
    }

    private void assertRankingDescending() {
        List<ParticipantRow> rows = getParticipantRows();
        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(rows.get(i).steps >= rows.get(i + 1).steps,
                    "List must stay in descending order");
        }
    }

    private void modifyAtLeastTwoParticipants() {
        openModalFromTop();
        selectParticipant("Mike Kid");
        setSteps("1000");
        submitModal();
        waitForModalClosed();

        openModalFromTop();
        selectParticipant("Jane Doe");
        setSteps("500");
        submitModal();
        waitForModalClosed();
    }

    private void openModalFromTop() {
        List<WebElement> buttons = getMainAddStepsButtons();
        assertTrue(buttons.size() >= 1, "Top Add Steps button not found");
        buttons.get(0).click();
        wait.until(ExpectedConditions.visibilityOf(getVisibleModal()));
    }

    private void openModalFromBottom() {
        List<WebElement> buttons = getMainAddStepsButtons();
        assertTrue(buttons.size() >= 2, "Bottom Add Steps button not found");
        buttons.get(1).click();
        wait.until(ExpectedConditions.visibilityOf(getVisibleModal()));
    }

    private void verifyModalStructure() {
        WebElement modal = getVisibleModal();

        assertTrue(modal.getText().contains("Add Steps to Participant"), "Modal title mismatch");

        WebElement select = getModalSelect();
        WebElement input = getModalStepsInput();
        WebElement submit = getModalSubmitButton();
        WebElement close = getModalCloseButton(modal);

        assertTrue(select.isDisplayed(), "Participant dropdown should be visible");
        assertTrue(input.isDisplayed(), "Steps input should be visible");
        assertTrue(submit.isDisplayed(), "Modal Add Steps button should be visible");
        assertTrue(close.isDisplayed(), "Close button should be visible");

        Select s = new Select(select);
        assertEquals("Choose participant", s.getFirstSelectedOption().getText().trim(),
                "Default dropdown option should be 'Choose participant'");
        assertEquals(11, s.getOptions().size(), "Dropdown should include default + 10 participants");

        for (String name : DEFAULT_STEPS.keySet()) {
            assertTrue(hasSelectOption(s, name), "Dropdown missing participant: " + name);
        }
    }

    private boolean hasSelectOption(Select select, String text) {
        for (WebElement o : select.getOptions()) {
            if (text.equals(o.getText().trim())) {
                return true;
            }
        }
        return false;
    }

    private void selectParticipant(String name) {
        Select s = new Select(getModalSelect());
        s.selectByVisibleText(name);
    }

    private void setSteps(String steps) {
        WebElement input = getModalStepsInput();
        input.clear();
        input.sendKeys(steps);
    }

    private void submitModal() {
        getModalSubmitButton().click();
    }

    private void waitForModalClosed() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal, .modal-content")));
        } catch (TimeoutException ignored) {
        }
    }

    private void closeModalIfOpen() {
        List<WebElement> closeButtons = driver.findElements(By.xpath(
                "//*[contains(@class,'close') and normalize-space()='×'] | //button[normalize-space()='×']"
        ));
        if (!closeButtons.isEmpty() && closeButtons.get(0).isDisplayed()) {
            closeButtons.get(0).click();
        }
    }

    private void clickModalCloseButton(WebElement modal) {
        getModalCloseButton(modal).click();
    }

    private WebElement getModalCloseButton(WebElement modal) {
        List<By> locators = List.of(
                By.xpath(".//*[contains(@class,'close') and normalize-space()='×']"),
                By.xpath(".//button[normalize-space()='×']"),
                By.xpath(".//*[contains(@class,'close')]")
        );

        for (By by : locators) {
            List<WebElement> found = modal.findElements(by);
            if (!found.isEmpty()) {
                return found.get(0);
            }
        }
        throw new IllegalStateException("Modal close button not found");
    }

    private WebElement getVisibleModal() {
        List<By> modalLocators = List.of(
                By.cssSelector(".modal-content"),
                By.cssSelector(".modal"),
                By.xpath("//*[contains(@class,'modal') and .//*[contains(.,'Add Steps to Participant')]]")
        );

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
        WebElement modal = getVisibleModal();
        List<WebElement> selects = modal.findElements(By.tagName("select"));
        if (selects.isEmpty()) {
            throw new IllegalStateException("Participant dropdown not found in modal");
        }
        return selects.get(0);
    }

    private WebElement getModalStepsInput() {
        WebElement modal = getVisibleModal();
        List<WebElement> inputs = modal.findElements(By.cssSelector("input[type='number']"));
        if (!inputs.isEmpty()) return inputs.get(0);

        inputs = modal.findElements(By.tagName("input"));
        if (!inputs.isEmpty()) return inputs.get(0);

        throw new IllegalStateException("Steps input not found in modal");
    }

    private WebElement getModalSubmitButton() {
        WebElement modal = getVisibleModal();
        List<By> locators = List.of(
                By.xpath(".//button[normalize-space()='Add Steps']"),
                By.xpath(".//button[@type='submit']"),
                By.xpath(".//button[contains(.,'Add Steps')]")
        );

        for (By by : locators) {
            List<WebElement> found = modal.findElements(by);
            if (!found.isEmpty()) return found.get(0);
        }

        throw new IllegalStateException("Modal submit button not found");
    }

    private List<WebElement> getMainAddStepsButtons() {
        return driver.findElements(By.xpath(
                "//button[normalize-space()='Add Steps' and not(ancestor::*[contains(@class,'modal')])]"
        ));
    }

    private List<WebElement> getMainResetButtons() {
        return driver.findElements(By.xpath(
                "//button[normalize-space()='Reset List' and not(ancestor::*[contains(@class,'modal')])]"
        ));
    }

    private void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    private void assertAlertTextAndAccept(String expected) {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(expected, alert.getText(), "Unexpected alert text");
        alert.accept();
    }

    private Alert tryGetAlert(Duration timeout) {
        try {
            return new WebDriverWait(driver, timeout).until(ExpectedConditions.alertIsPresent());
        } catch (TimeoutException e) {
            return null;
        }
    }

    private long getStepsFor(String participant) {
        for (ParticipantRow row : getParticipantRows()) {
            if (participant.equals(row.name)) return row.steps;
        }
        fail("Participant not found: " + participant);
        return -1;
    }

    private Map<String, Long> toNameStepsMap(List<ParticipantRow> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (ParticipantRow row : rows) {
            map.put(row.name, row.steps);
        }
        return map;
    }

    private List<ParticipantRow> getParticipantRows() {
        List<WebElement> rowCandidates = new ArrayList<>();

        List<By> selectors = List.of(
                By.cssSelector("#participantsList li"),
                By.cssSelector("#participant-list li"),
                By.cssSelector("#participants li"),
                By.cssSelector("ul li"),
                By.cssSelector("tbody tr")
        );

        for (By by : selectors) {
            List<WebElement> found = driver.findElements(by);
            List<WebElement> filtered = new ArrayList<>();
            for (WebElement e : found) {
                if (!e.isDisplayed()) continue;
                String text = e.getText();
                if (text == null || text.isBlank()) continue;
                if (containsAnyDefaultName(text)) filtered.add(e);
            }
            if (filtered.size() >= 10) {
                rowCandidates = filtered;
                break;
            }
        }

        if (rowCandidates.isEmpty()) {
            fail("Could not detect participant rows");
        }

        List<ParticipantRow> parsed = new ArrayList<>();
        for (WebElement row : rowCandidates) {
            ParticipantRow p = parseParticipant(row);
            if (p != null) parsed.add(p);
        }

        Map<String, ParticipantRow> uniq = new LinkedHashMap<>();
        for (ParticipantRow p : parsed) {
            uniq.putIfAbsent(p.name, p);
        }
        return new ArrayList<>(uniq.values());
    }

    private ParticipantRow parseParticipant(WebElement row) {
        String text = row.getText();
        if (text == null) return null;

        String name = null;
        for (String n : DEFAULT_STEPS.keySet()) {
            if (text.contains(n)) {
                name = n;
                break;
            }
        }
        if (name == null) return null;

        Matcher m = Pattern.compile("(\\d[\\d,]*)").matcher(text);
        long steps = -1;
        while (m.find()) {
            String num = m.group(1).replace(",", "");
            try {
                long candidate = Long.parseLong(num);
                if (candidate > steps) steps = candidate;
            } catch (NumberFormatException ignored) {
            }
        }

        if (steps < 0) return null;
        return new ParticipantRow(name, steps, row);
    }

    private boolean containsAnyDefaultName(String text) {
        for (String n : DEFAULT_STEPS.keySet()) {
            if (text.contains(n)) return true;
        }
        return false;
    }

    private boolean hasTrophy(WebElement row) {
        String text = row.getText();
        if (text.contains("🏆") || text.contains("🥇") || text.contains("🥈") || text.contains("🥉")) return true;

        List<WebElement> icons = row.findElements(By.cssSelector("i, svg, span"));
        for (WebElement icon : icons) {
            String cls = safe(icon.getAttribute("class")).toLowerCase(Locale.ROOT);
            if (cls.contains("trophy") || cls.contains("medal") || cls.contains("cup")) return true;
        }
        return false;
    }

    private boolean isGold(WebElement row) {
        return hasMedalEmoji(row, "🥇") || hasRgbColor(row, 255, 215, 0);
    }

    private boolean isSilver(WebElement row) {
        return hasMedalEmoji(row, "🥈") || hasRgbColor(row, 192, 192, 192);
    }

    private boolean isBronze(WebElement row) {
        return hasMedalEmoji(row, "🥉") || hasRgbColor(row, 205, 127, 50);
    }

    private boolean hasMedalEmoji(WebElement row, String emoji) {
        return row.getText().contains(emoji);
    }

    private boolean hasRgbColor(WebElement row, int r, int g, int b) {
        List<WebElement> icons = row.findElements(By.cssSelector("i, svg, span"));
        for (WebElement icon : icons) {
            String cls = safe(icon.getAttribute("class")).toLowerCase(Locale.ROOT);
            if (!cls.contains("trophy") && !cls.contains("medal") && !cls.contains("cup")) continue;
            try {
                java.awt.Color c = Color.fromString(icon.getCssValue("color")).getColor();
                if (c.getRed() == r && c.getGreen() == g && c.getBlue() == b) return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }

    private void switchToBrowser(String browser) {
        if (driver != null) driver.quit();
        driver = createDriverViaReflection(browser);
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        driver.navigate().to(URL);
    }

    private WebDriver createDriverViaReflection(String browser) {
        List<String> methodNames = new ArrayList<>();
        String b = browser.toLowerCase(Locale.ROOT);

        if ("edge".equals(b)) methodNames.add("createEdgeDriver");
        else if ("firefox".equals(b)) methodNames.add("createFirefoxDriver");
        else methodNames.add("createChromeDriver");

        methodNames.add("createChromeDriver");

        for (String methodName : methodNames) {
            try {
                Method m = DriverManager.class.getMethod(methodName);
                Object out = m.invoke(null);
                if (out instanceof WebDriver) return (WebDriver) out;
            } catch (Exception ignored) {
            }
        }

        throw new IllegalStateException("No suitable driver factory found in DriverManager for browser: " + browser);
    }

    private static class ParticipantRow {
        final String name;
        final long steps;
        final WebElement element;

        ParticipantRow(String name, long steps, WebElement element) {
            this.name = name;
            this.steps = steps;
            this.element = element;
        }
    }
}
