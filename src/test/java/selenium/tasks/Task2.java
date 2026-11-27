package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final String FITNESS_CHALLENGE_PAGE =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final Map<String, Integer> DEFAULT_PARTICIPANTS = Map.ofEntries(
            Map.entry("Mike Kid", 8500),
            Map.entry("Jill Watson", 12000),
            Map.entry("Jane Doe", 6500),
            Map.entry("John Smith", 15000),
            Map.entry("Sarah Johnson", 9800),
            Map.entry("Carlos Garcia", 11200),
            Map.entry("Emily Chen", 7300),
            Map.entry("David Brown", 13500),
            Map.entry("Maria Rodriguez", 10500),
            Map.entry("Alex Taylor", 8900)
    );

    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createDriver();
        driver.get(FITNESS_CHALLENGE_PAGE);
        participantRows();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", driver.getTitle());
        assertDefaultParticipants();
        assertEquals(2, driver.findElements(By.id("addStepsBtn")).size());
        assertTrue(driver.findElements(By.id("addStepsBtn")).stream().allMatch(WebElement::isDisplayed));
        assertEquals(2, driver.findElements(By.id("resetBtn")).size());
        assertTrue(driver.findElements(By.id("resetBtn")).stream().allMatch(WebElement::isDisplayed));
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<Integer> steps = participantRows().stream().map(this::stepCount).toList();
        List<Integer> sortedSteps = new ArrayList<>(steps);
        sortedSteps.sort(Comparator.reverseOrder());
        assertEquals(sortedSteps, steps, "Participants must be listed from highest to lowest steps");
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1), "Step counts must not increase lower in the list");
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<WebElement> rows = participantRows();
        assertEquals("rgba(255, 215, 0, 1)", trophy(rows.get(0)).getCssValue("color"));
        assertEquals("rgba(192, 192, 192, 1)", trophy(rows.get(1)).getCssValue("color"));
        assertEquals("rgba(205, 127, 50, 1)", trophy(rows.get(2)).getCssValue("color"));
        for (WebElement row : rows.subList(3, rows.size())) {
            assertTrue(row.findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Only the top three participants should have trophy icons");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        assertModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        WebElement bottomButton = driver.findElements(By.id("addStepsBtn")).get(1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", bottomButton);
        bottomButton.click();
        assertModalContents();
    }

    @Test
    public void closeModalWithCloseButton() {
        openAddStepsModal();
        WebElement closeButton = driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        closeButton.click();
        new WebDriverWait(driver, WAIT_TIMEOUT).until(webDriver ->
                "none".equals(webDriver.findElement(By.id("addStepsModal")).getCssValue("display")));
        assertTrue(driver.findElement(By.id("participantsList")).isDisplayed());
        assertDefaultParticipants();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        List<String> originalRanking = participantRows().stream().map(this::participantName).toList();
        int originalRank = originalRanking.indexOf("Mike Kid");
        int originalSteps = participantSteps("Mike Kid");
        addSteps("Mike Kid", "1000");
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> participantSteps("Mike Kid") == originalSteps + 1000);
        assertEquals(originalSteps + 1000, participantSteps("Mike Kid"));
        assertRankingIsDescending();
        List<String> updatedRanking = participantRows().stream().map(this::participantName).toList();
        assertTrue(updatedRanking.indexOf("Mike Kid") < originalRank,
                "Mike Kid should move up after overtaking a participant");
    }

    @Test
    public void addZeroSteps() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("0");
        submitStepsAndAssertAlert("Please enter a valid number of steps");
    }

    @Test
    public void addLargeNumberOfSteps() {
        int originalSteps = participantSteps("Jane Doe");
        addSteps("Jane Doe", "999999");
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        assertEquals(originalSteps + 999999, participantSteps("Jane Doe"));
        assertEquals("Jane Doe", participantName(participantRows().get(0)));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        openAddStepsModal();
        enterSteps("1000");
        submitStepsAndAssertAlert("Please select a participant");
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("0");
        submitStepsAndAssertAlert("Please enter a valid number of steps");
    }

    @Test
    public void submitWithNegativeSteps() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("-500");
        submitStepsAndAssertAlert("Please enter a valid number of steps");
    }

    @Test
    public void submitWithNonNumericInput() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("abc");
        String value = driver.findElement(By.id("steps_input")).getAttribute("value");
        assertNotNull(value);
        assertFalse(value.matches(".*[a-zA-Z].*"), "Number input must reject alphabetic characters");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "500");
        clickResetButton(0);
        waitForDefaultParticipantsAfterReload();
        assertDefaultParticipants();
        assertRankingIsDescending();
        assertDefaultTrophyColors();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "500");
        clickResetButton(1);
        waitForDefaultParticipantsAfterReload();
        assertDefaultParticipants();
        assertRankingIsDescending();
        assertDefaultTrophyColors();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        String participantIndex = new Select(driver.findElement(By.id("participant_select")))
                .getFirstSelectedOption().getAttribute("value");
        Number originalSteps = (Number) ((JavascriptExecutor) driver).executeScript(
                "return JSON.parse(localStorage.getItem('participant' + arguments[0])).steps;", participantIndex);
        enterSteps("9007199254740991");
        driver.findElement(By.id("modal_add_steps_button")).click();

        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        Object storedSteps = ((JavascriptExecutor) driver).executeScript(
                "return JSON.parse(localStorage.getItem('participant' + arguments[0])).steps;", participantIndex);
        Number storedStepCount = assertInstanceOf(Number.class, storedSteps);
        Number expectedStepCount = assertInstanceOf(Number.class, ((JavascriptExecutor) driver).executeScript(
                "return Number(arguments[0]) + Number(arguments[1]);", originalSteps, "9007199254740991"));
        assertEquals(expectedStepCount.doubleValue(), storedStepCount.doubleValue());
        String expectedDisplay = (String) ((JavascriptExecutor) driver).executeScript(
                "return JSON.parse(localStorage.getItem('participant' + arguments[0])).steps.toLocaleString() + ' steps';",
                participantIndex);
        assertEquals(expectedDisplay, participantRow("Mike Kid").findElement(By.className("participant-steps")).getText());
    }

    @Test
    public void decimalStepValues() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("100.5");
        submitStepsAndAssertAlert("Please enter a valid number of steps");
    }

    private List<WebElement> participantRows() {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.findElements(By.cssSelector("#participantsList li")).size() == 10);
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private void assertDefaultParticipants() {
        Map<String, Integer> actualParticipants = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            actualParticipants.put(participantName(row), stepCount(row));
        }
        assertEquals(DEFAULT_PARTICIPANTS, actualParticipants);
    }

    private void assertRankingIsDescending() {
        List<Integer> steps = participantRows().stream().map(this::stepCount).toList();
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1), "Step counts must be sorted in descending order");
        }
    }

    private void assertDefaultTrophyColors() {
        List<WebElement> rows = participantRows();
        assertEquals("rgba(255, 215, 0, 1)", trophy(rows.get(0)).getCssValue("color"));
        assertEquals("rgba(192, 192, 192, 1)", trophy(rows.get(1)).getCssValue("color"));
        assertEquals("rgba(205, 127, 50, 1)", trophy(rows.get(2)).getCssValue("color"));
    }

    private void assertModalContents() {
        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(modal.findElement(By.tagName("h3")).getText().contains("Add Steps to Participant"));
        WebElement participantSelect = modal.findElement(By.id("participant_select"));
        assertTrue(participantSelect.isDisplayed());
        assertEquals("Choose participant", new Select(participantSelect).getFirstSelectedOption().getText());
        assertEquals(11, new Select(participantSelect).getOptions().size());
        assertEquals("number", modal.findElement(By.id("steps_input")).getAttribute("type"));
        assertTrue(modal.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertEquals("Add Steps", modal.findElement(By.id("modal_add_steps_button")).getText());
        assertTrue(modal.findElement(By.cssSelector(".w3-closebtn[title='Close Modal']")).isDisplayed());
    }

    private void openAddStepsModal() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void selectParticipant(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    private void enterSteps(String steps) {
        WebElement input = driver.findElement(By.id("steps_input"));
        String currentValue = input.getAttribute("value");
        if (currentValue != null && !currentValue.isEmpty()) {
            input.clear();
        }
        input.sendKeys(steps);
    }

    private void addSteps(String participant, String steps) {
        openAddStepsModal();
        selectParticipant(participant);
        enterSteps(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void submitStepsAndAssertAlert(String expectedMessage) {
        driver.findElement(By.id("modal_add_steps_button")).click();
        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedMessage, driver.switchTo().alert().getText());
        driver.switchTo().alert().accept();
        wait.until(webDriver -> {
            try {
                webDriver.switchTo().alert();
                return false;
            } catch (NoAlertPresentException e) {
                return true;
            }
        });
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    private void clickResetButton(int buttonIndex) {
        WebElement participantBeforeReload = participantRows().get(0);
        WebElement resetButton = driver.findElements(By.id("resetBtn")).get(buttonIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", resetButton);
        resetButton.click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.stalenessOf(participantBeforeReload));
    }

    private void waitForDefaultParticipantsAfterReload() {
        participantRows();
        assertDefaultParticipants();
    }

    private WebElement participantRow(String name) {
        return participantRows().stream()
                .filter(row -> name.equals(participantName(row)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private int participantSteps(String name) {
        return stepCount(participantRow(name));
    }

    private String participantName(WebElement row) {
        return row.findElement(By.className("participant-name")).getText();
    }

    private int stepCount(WebElement row) {
        String steps = row.findElement(By.className("participant-steps")).getText();
        return Integer.parseInt(steps.replaceAll("[^0-9]", ""));
    }

    private WebElement trophy(WebElement row) {
        return row.findElement(By.cssSelector("i.fa-trophy"));
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
}
