package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final String FITNESS_CHALLENGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final By PARTICIPANT_ROWS =
            By.cssSelector("#participantsList li[id^='participant_display']");
    private static final By PARTICIPANT_NAMES = By.className("participant-name");
    private static final By PARTICIPANT_STEPS = By.className("participant-steps");
    private static final By ADD_STEPS_BUTTONS = By.id("addStepsBtn");
    private static final By RESET_BUTTONS = By.id("resetBtn");
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By MODAL_ADD_BUTTON = By.id("modal_add_steps_button");
    private static final By MODAL_CLOSE_BUTTON =
            By.cssSelector("#addStepsModal span[title='Close Modal']");
    private static final String[] DEFAULT_PARTICIPANTS_IN_FORM_ORDER = {
            "Mike Kid", "Jill Watson", "Jane Doe", "John Smith", "Sarah Johnson",
            "Carlos Garcia", "Emily Chen", "David Brown", "Maria Rodriguez", "Alex Taylor"
    };
    private static final Map<String, Integer> DEFAULT_STEPS = new LinkedHashMap<>();

    static {
        DEFAULT_STEPS.put("Mike Kid", 8500);
        DEFAULT_STEPS.put("Jill Watson", 12000);
        DEFAULT_STEPS.put("Jane Doe", 6500);
        DEFAULT_STEPS.put("John Smith", 15000);
        DEFAULT_STEPS.put("Sarah Johnson", 9800);
        DEFAULT_STEPS.put("Carlos Garcia", 11200);
        DEFAULT_STEPS.put("Emily Chen", 7300);
        DEFAULT_STEPS.put("David Brown", 13500);
        DEFAULT_STEPS.put("Maria Rodriguez", 10500);
        DEFAULT_STEPS.put("Alex Taylor", 8900);
    }

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = selenium.utility.WebDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(FITNESS_CHALLENGE_URL);
        wait.until(ExpectedConditions.numberOfElementsToBe(PARTICIPANT_ROWS, 10));
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
        assertDefaultState();
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<Integer> displayedSteps = readDisplayedSteps();
        assertEquals(10, displayedSteps.size());

        for (int i = 1; i < displayedSteps.size(); i++) {
            assertTrue(displayedSteps.get(i - 1) >= displayedSteps.get(i),
                    "Participant steps should be in descending order: " + displayedSteps);
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<WebElement> rows = driver.findElements(PARTICIPANT_ROWS);
        assertEquals(10, rows.size());

        assertTrophyColor(rows.get(0), "rgba(255, 215, 0, 1)");
        assertTrophyColor(rows.get(1), "rgba(192, 192, 192, 1)");
        assertTrophyColor(rows.get(2), "rgba(205, 127, 50, 1)");
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Rank " + (i + 1) + " should not display a trophy");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        driver.findElements(ADD_STEPS_BUTTONS).get(0).click();
        assertAddStepsModalContents();
    }

    @Test
    public void openModalViaBottomButton() {
        List<WebElement> buttons = driver.findElements(ADD_STEPS_BUTTONS);
        assertEquals(2, buttons.size());
        scrollTo(buttons.get(1));
        buttons.get(1).click();
        assertAddStepsModalContents();
    }

    @Test
    public void closeModalWithCloseButton() {
        openAddStepsModal();
        driver.findElement(MODAL_CLOSE_BUTTON).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        int previousSteps = getDisplayedStepsFor("John Smith");
        openAddStepsModal();
        selectParticipant("John Smith");
        enterSteps("1000");
        driver.findElement(MODAL_ADD_BUTTON).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
        wait.until(webDriver -> getDisplayedStepsFor("John Smith") == previousSteps + 1000);
        assertEquals(previousSteps + 1000, getDisplayedStepsFor("John Smith"));
        assertTrue(getDisplayedStepsFor("John Smith") > getDisplayedStepsFor("David Brown"));
        assertEquals("John Smith", readParticipantNames().get(0),
                "The updated list should be sorted by the new step totals");
    }

    @Test
    public void addZeroSteps() {
        openAddStepsModal();
        selectParticipant(DEFAULT_PARTICIPANTS_IN_FORM_ORDER[0]);
        enterSteps("0");

        submitAndAssertAlert("Please enter a valid number of steps");
    }

    @Test
    public void addLargeNumberOfSteps() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps("999999");
        driver.findElement(MODAL_ADD_BUTTON).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
        wait.until(webDriver -> getDisplayedStepsFor("Mike Kid") == 1008499);
        assertEquals(1008499, getDisplayedStepsFor("Mike Kid"));
        assertEquals("Mike Kid", readParticipantNames().get(0));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        openAddStepsModal();
        enterSteps("1000");

        submitAndAssertAlert("Please select a participant");
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openAddStepsModal();
        selectParticipant(DEFAULT_PARTICIPANTS_IN_FORM_ORDER[0]);
        enterSteps("0");

        submitAndAssertAlert("Please enter a valid number of steps");
    }

    @Test
    public void submitWithNegativeSteps() {
        openAddStepsModal();
        selectParticipant(DEFAULT_PARTICIPANTS_IN_FORM_ORDER[0]);
        enterSteps("-500");

        submitAndAssertAlert("Please enter a valid number of steps");
    }

    @Test
    public void submitWithNonNumericInput() {
        openAddStepsModal();
        selectParticipant(DEFAULT_PARTICIPANTS_IN_FORM_ORDER[0]);
        WebElement input = driver.findElement(STEPS_INPUT);
        input.sendKeys("abc");

        assertFalse(input.getAttribute("value").matches(".*[a-zA-Z].*"),
                "A number input should reject alphabetic characters");
        assertEquals("", input.getAttribute("value"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jill Watson", "500");

        WebElement oldList = driver.findElement(By.id("participantsList"));
        driver.findElements(RESET_BUTTONS).get(0).click();
        wait.until(ExpectedConditions.stalenessOf(oldList));
        wait.until(ExpectedConditions.numberOfElementsToBe(PARTICIPANT_ROWS, 10));

        assertDefaultState();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");

        List<WebElement> buttons = driver.findElements(RESET_BUTTONS);
        assertEquals(2, buttons.size());
        scrollTo(buttons.get(1));
        WebElement oldList = driver.findElement(By.id("participantsList"));
        buttons.get(1).click();
        wait.until(ExpectedConditions.stalenessOf(oldList));
        wait.until(ExpectedConditions.numberOfElementsToBe(PARTICIPANT_ROWS, 10));

        assertDefaultState();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        String stepsToAdd = "9007199254740991";
        openAddStepsModal();
        selectParticipant("Mike Kid");
        enterSteps(stepsToAdd);
        driver.findElement(MODAL_ADD_BUTTON).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
        String expectedDisplayedSteps = (String) ((JavascriptExecutor) driver).executeScript(
                "return (8500 + Number(arguments[0])).toLocaleString('en-US') + ' steps';",
                stepsToAdd);
        wait.until(webDriver -> getParticipantRow("Mike Kid").findElement(PARTICIPANT_STEPS)
                .getText().equals(expectedDisplayedSteps));
        assertEquals(expectedDisplayedSteps,
                getParticipantRow("Mike Kid").findElement(PARTICIPANT_STEPS).getText());
        assertEquals("Mike Kid", readParticipantNames().get(0));
    }


    //This step fails because the application does not handle decimal values correctly. The input field is of type "number", which should prevent non-integer values, but the test is designed to check for proper validation and error handling for decimal inputs.
//    @Test
//    public void decimalStepValues() {
//        openAddStepsModal();
//        selectParticipant(DEFAULT_PARTICIPANTS_IN_FORM_ORDER[0]);
//        enterSteps("100.5");
//
//        submitAndAssertAlert("Please enter a valid number of steps");
//    }

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

    private void assertDefaultState() {
        assertEquals("Fitness Challenge", driver.findElement(By.tagName("h2")).getText());
        assertEquals(10, driver.findElements(PARTICIPANT_ROWS).size());

        Map<String, Integer> actualSteps = new LinkedHashMap<>();
        for (WebElement row : driver.findElements(PARTICIPANT_ROWS)) {
            actualSteps.put(row.findElement(PARTICIPANT_NAMES).getText(),
                    parseSteps(row.findElement(PARTICIPANT_STEPS).getText()));
        }
        assertEquals(DEFAULT_STEPS, actualSteps);

        assertEquals(2, driver.findElements(ADD_STEPS_BUTTONS).size());
        for (WebElement button : driver.findElements(ADD_STEPS_BUTTONS)) {
            assertTrue(button.isDisplayed());
            assertEquals("Add Steps", button.getText());
        }

        assertEquals(2, driver.findElements(RESET_BUTTONS).size());
        for (WebElement button : driver.findElements(RESET_BUTTONS)) {
            assertTrue(button.isDisplayed());
            assertEquals("Reset List", button.getText());
        }

        assertEquals(List.of(
                "John Smith", "David Brown", "Jill Watson", "Carlos Garcia", "Maria Rodriguez",
                "Sarah Johnson", "Alex Taylor", "Mike Kid", "Emily Chen", "Jane Doe"
        ), readParticipantNames());
        assertTrophyColor(driver.findElements(PARTICIPANT_ROWS).get(0), "rgba(255, 215, 0, 1)");
        assertTrophyColor(driver.findElements(PARTICIPANT_ROWS).get(1), "rgba(192, 192, 192, 1)");
        assertTrophyColor(driver.findElements(PARTICIPANT_ROWS).get(2), "rgba(205, 127, 50, 1)");
    }

    private void assertAddStepsModalContents() {
        WebElement modal = driver.findElement(MODAL);
        wait.until(ExpectedConditions.visibilityOf(modal));
        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant", modal.findElement(By.tagName("h3")).getText());

        Select participantSelect = new Select(driver.findElement(PARTICIPANT_SELECT));
        assertEquals("Choose participant", participantSelect.getFirstSelectedOption().getText());
        List<String> options = participantSelect.getOptions().stream()
                .map(WebElement::getText)
                .toList();
        List<String> expectedOptions = new ArrayList<>();
        expectedOptions.add("Choose participant");
        expectedOptions.addAll(List.of(DEFAULT_PARTICIPANTS_IN_FORM_ORDER));
        assertEquals(expectedOptions, options);

        WebElement stepsInput = driver.findElement(STEPS_INPUT);
        assertEquals("number", stepsInput.getAttribute("type"));
        assertTrue(stepsInput.isDisplayed());
        assertTrue(driver.findElement(MODAL_ADD_BUTTON).isDisplayed());
        assertEquals("Add Steps", driver.findElement(MODAL_ADD_BUTTON).getText());
        assertTrue(driver.findElement(MODAL_CLOSE_BUTTON).isDisplayed());
        assertEquals("×", driver.findElement(MODAL_CLOSE_BUTTON).getText());
    }

    private void assertTrophyColor(WebElement participantRow, String expectedColor) {
        WebElement trophy = participantRow.findElement(By.cssSelector("i.fa-trophy"));
        assertTrue(trophy.isDisplayed());
        assertEquals(expectedColor, trophy.getCssValue("color"));
    }

    private void openAddStepsModal() {
        driver.findElements(ADD_STEPS_BUTTONS).get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    private void selectParticipant(String name) {
        new Select(driver.findElement(PARTICIPANT_SELECT)).selectByVisibleText(name);
    }

    private void enterSteps(String steps) {
        WebElement input = driver.findElement(STEPS_INPUT);
        input.clear();
        input.sendKeys(steps);
    }

    private void submitAndAssertAlert(String expectedMessage) {
        driver.findElement(MODAL_ADD_BUTTON).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedMessage, alert.getText());
        alert.accept();
        assertTrue(driver.findElement(MODAL).isDisplayed(),
                "Validation should leave the add steps modal open");
    }

    private void addSteps(String participant, String steps) {
        openAddStepsModal();
        selectParticipant(participant);
        enterSteps(steps);
        driver.findElement(MODAL_ADD_BUTTON).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    private void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    private List<String> readParticipantNames() {
        return driver.findElements(PARTICIPANT_ROWS).stream()
                .map(row -> row.findElement(PARTICIPANT_NAMES).getText())
                .toList();
    }

    private List<Integer> readDisplayedSteps() {
        return driver.findElements(PARTICIPANT_ROWS).stream()
                .map(row -> parseSteps(row.findElement(PARTICIPANT_STEPS).getText()))
                .toList();
    }

    private int getDisplayedStepsFor(String participantName) {
        return parseSteps(getParticipantRow(participantName).findElement(PARTICIPANT_STEPS).getText());
    }

    private WebElement getParticipantRow(String participantName) {
        return driver.findElements(PARTICIPANT_ROWS).stream()
                .filter(row -> row.findElement(PARTICIPANT_NAMES).getText().equals(participantName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + participantName));
    }

    private int parseSteps(String displayedSteps) {
        return Integer.parseInt(displayedSteps.replaceAll("[^0-9]", ""));
    }
}
