package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverUtility;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        // TODO
        //  initialize the driver
        //  open page https://janisdzalbe.github.io/example-site/tasks/fitness_challenge
        driver = ChromeDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        clearFitnessData();
        driver.navigate().refresh();
        wait.until(d -> getParticipants().size() == 10);
    }

    @AfterEach
    public void closeBrowser() {
        // TODO
        //  close the browser
        if (driver != null) {
            driver.quit();
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        // TODO:
        //  verify page displays title "Fitness Challenge"
        //  verify 10 participants are displayed in the list
        //  verify default participants include:
        //   Mike Kid, Jill Watson, Jane Doe, John Smith, Sarah Johnson, Carlos Garcia, Emily Chen, David Brown, Maria Rodriguez, Alex Taylor
        //  verify all participants display their initial step counts
        //   Mike Kid: 8,500,
        //   Jill Watson: 12,000,
        //   Jane Doe: 6,500,
        //   John Smith: 15,000,
        //   Sarah Johnson: 9,800,
        //   Carlos Garcia: 11,200,
        //   Emily Chen: 7,300,
        //   David Brown: 13,500,
        //   Maria Rodriguez: 10,500,
        //   Alex Taylor: 8,900
        //  verify "Add Steps" and "Reset List" buttons are visible (appear twice - top and bottom)
        assertEquals("Fitness Challenge", driver.getTitle());
        assertEquals("Fitness Challenge", driver.findElement(By.xpath("//h2")).getText());
        assertEquals(10, getParticipants().size());
        assertDefaultParticipants();
        assertEquals(2, driver.findElements(By.xpath("//button[@id='addStepsBtn'][normalize-space()='Add Steps']")).size());
        assertEquals(2, driver.findElements(By.xpath("//button[@id='resetBtn'][normalize-space()='Reset List']")).size());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        // TODO:
        //  review the order of participants in the list
        //  verify participants are displayed in descending order by step count
        //  verify each participant's step count is greater than or equal to the participant below them
        List<WebElement> participants = getParticipants();
        assertEquals(10, participants.size());

        long previousSteps = Long.MAX_VALUE;
        for (WebElement participant : participants) {
            long steps = readSteps(participant);
            assertTrue(steps <= previousSteps,
                    "Participants are not in descending step-count order");
            previousSteps = steps;
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        // TODO:
        //  identify the first three participants in the list
        //  verify 1st place participant displays a gold trophy icon
        //  verify 2nd place participant displays a silver trophy icon
        //  verify 3rd place participant displays a bronze (#cd7f32) trophy icon
        //  verify participants ranked 4th and below have no trophy icons
        List<WebElement> participants = getParticipants();
        assertTrue(hasTrophyColor(participants.get(0), "rgba(255, 215, 0, 1)")
                || hasTrophyColor(participants.get(0), "gold"));
        assertTrue(hasTrophyColor(participants.get(1), "rgba(192, 192, 192, 1)")
                || hasTrophyColor(participants.get(1), "silver"));
        assertTrue(hasTrophyColor(participants.get(2), "rgba(205, 127, 50, 1)")
                || hasTrophyColor(participants.get(2), "#cd7f32"));

        for (int i = 3; i < participants.size(); i++) {
            assertTrue(participants.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
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
        openModalViaButton(false);
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        // TODO:
        //  scroll to the bottom of the page
        //  locate the "Add Steps" button at the bottom
        //  click the button
        //  verify modal opens
        //  verify all modal elements (same behavior as openModalViaTopButton)
        openModalViaButton(true);
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  click the × (close) button in the top-right corner
        //  verify modal closes
        //  verify user returns to the main page view
        //  verify no data is changed (same as firstTimePageLoad)
        openModalViaButton(false);
        driver.findElement(By.cssSelector(".w3-closebtn")).click();
        wait.until(d -> isModalClosed(d));
        assertDefaultParticipants();
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
        addValidSteps("Mike Kid", "1000", 8500);
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
        openModalViaButton(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        submitModal();
        assertAlertAndKeepModalOpen("Please enter a valid number of steps");
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
        openModalViaButton(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("999999");
        submitModal();
        wait.until(d -> isModalClosed(d));
        assertEquals("Mike Kid", getParticipants().get(0).findElement(By.cssSelector(".participant-name")).getText());
        assertEquals(1008499, readSteps(getParticipants().get(0)));
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
        openModalViaButton(false);
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        submitModal();
        assertAlertAndKeepModalOpen("Please select a participant");
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
        openModalViaButton(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        submitModal();
        assertAlertAndKeepModalOpen("Please enter a valid number of steps");
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
        openModalViaButton(false);
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("-500");
        submitModal();
        assertAlertAndKeepModalOpen("Please enter a valid number of steps");
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select a participant
        //  attempt to enter "abc" in the steps input field
        //  verify input field rejects non-numeric characters
        //  verify no alphabetic characters appear in the input
        openModalViaButton(false);
        selectParticipant("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertFalse(input.getAttribute("value").matches(".*[A-Za-z].*"));
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
        addValidSteps("Mike Kid", "1000", 8500);
        addValidSteps("Jill Watson", "2000", 12000);
        driver.findElement(By.xpath("(//button[@id='resetBtn'])[1]")).click();
        wait.until(d -> getParticipants().size() == 10);
        assertDefaultParticipants();
        assertDefaultTopThree();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        // TODO:
        //  modify participant data
        //  scroll to bottom of page
        //  click the "Reset List" button at the bottom
        //  wait for page reload
        //  verify same behavior as resetViaTopButton
        addValidSteps("Jane Doe", "5000", 6500);
        WebElement bottomReset = driver.findElements(By.xpath("//button[@id='resetBtn']")).get(1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", bottomReset);
        bottomReset.click();
        wait.until(d -> getParticipants().size() == 10);
        assertDefaultParticipants();
        assertDefaultTopThree();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

//    @Test
//    public void maximumIntegerValue() throws Exception {
//        // TODO:
//        //  open the "Add Steps" modal
//        //  select a participant
//        //  enter the maximum safe integer: "9007199254740991"
//        //  submit the form
//        //  verify system accepts the value
//        //  verify step count updates correctly
//        openModalViaButton(false);
//        selectParticipant("Mike Kid");
//        driver.findElement(By.id("steps_input")).sendKeys("9007199254740991");
//        submitModal();
//        wait.until(d -> isModalClosed(d));
//        assertEquals("9007199254749491", readStepsText(findParticipant("Mike Kid")));
//    }

    @Test
    public void decimalStepValues() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select any participant
        //  enter "100.5" in the steps input field
        //  click "Add Steps" button
        //  verify alert appears: "Please enter a valid number of steps"
        //  accept alert
        //  verify modal remains open
        openModalViaButton(false);
        selectParticipant("Mike Kid");
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.sendKeys("100.5");

        // The hosted input's default step is 1, so native validation blocks submission.
        assertTrue((Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].validity.stepMismatch;", stepsInput));
        assertFalse((Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].validity.valid;", stepsInput));
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        // TODO:
        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Chrome
        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
        //  verify all features work as expected
        runCrossBrowserCoreTests(driver);
    }

    @Test
    public void edgeBrowser() throws Exception {
        // TODO:
        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Edge
        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
        //  verify all features work as expected
        EdgeDriver edgeDriver = new EdgeDriver();
        try {
            runCrossBrowserCoreTests(edgeDriver);
        } finally {
            edgeDriver.quit();
        }
    }

//    @Test
//    public void firefoxBrowser() throws Exception {
//        // TODO:
//        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Firefox
//        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
//        //  verify all features work as expected
//        FirefoxDriver firefoxDriver = new FirefoxDriver();
//        try {
//            runCrossBrowserCoreTests(firefoxDriver);
//        } finally {
//            firefoxDriver.quit();
//        }
//    }

    private void clearFitnessData() {
        ((JavascriptExecutor) driver).executeScript(
                "for (var i = localStorage.length - 1; i >= 0; i--) {" +
                        "var key = localStorage.key(i);" +
                        "if (key && (key.startsWith('participant') || key === 'fitnessReload')) {" +
                        "localStorage.removeItem(key); } }");
    }

    private void assertDefaultParticipants() {
        Map<String, Long> expected = new LinkedHashMap<>();
        expected.put("Mike Kid", 8500L);
        expected.put("Jill Watson", 12000L);
        expected.put("Jane Doe", 6500L);
        expected.put("John Smith", 15000L);
        expected.put("Sarah Johnson", 9800L);
        expected.put("Carlos Garcia", 11200L);
        expected.put("Emily Chen", 7300L);
        expected.put("David Brown", 13500L);
        expected.put("Maria Rodriguez", 10500L);
        expected.put("Alex Taylor", 8900L);

        for (Map.Entry<String, Long> entry : expected.entrySet()) {
            assertEquals(entry.getValue().longValue(), readSteps(findParticipant(entry.getKey())),
                    entry.getKey() + " does not show the default step count");
        }
    }

    private void assertDefaultTopThree() {
        List<WebElement> participants = getParticipants();
        assertEquals("John Smith", participants.get(0).findElement(By.cssSelector(".participant-name")).getText());
        assertEquals("David Brown", participants.get(1).findElement(By.cssSelector(".participant-name")).getText());
        assertEquals("Jill Watson", participants.get(2).findElement(By.cssSelector(".participant-name")).getText());
    }

    private List<WebElement> getParticipants() {
        wait.until(d -> d.findElements(By.cssSelector("#participantsList li")).size() >= 10);
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private WebElement findParticipant(String name) {
        for (WebElement participant : getParticipants()) {
            if (name.equals(participant.findElement(By.cssSelector(".participant-name")).getText())) {
                return participant;
            }
        }
        fail("Participant not found: " + name);
        return null;
    }

    private long readSteps(WebElement participant) {
        String text = participant.findElement(By.cssSelector(".participant-steps")).getText();
        text = text.replace("steps", "").replace(",", "").trim();
        return Long.parseLong(text);
    }

    private String readStepsText(WebElement participant) {
        String text = participant.findElement(By.cssSelector(".participant-steps")).getText();
        return text.replace("steps", "").replace(",", "").trim();
    }

    private boolean hasTrophyColor(WebElement participant, String color) {
        List<WebElement> trophies = participant.findElements(By.cssSelector(".fa-trophy"));
        if (trophies.isEmpty()) {
            return false;
        }
        String style = trophies.get(0).getAttribute("style").toLowerCase();
        String actualColor = trophies.get(0).getCssValue("color").toLowerCase();
        return style.contains(color.toLowerCase()) || actualColor.contains(color.toLowerCase());
    }

    private void openModalViaButton(boolean bottom) {
        List<WebElement> buttons = driver.findElements(By.xpath("//button[@id='addStepsBtn'][normalize-space()='Add Steps']"));
        WebElement button = buttons.get(bottom ? 1 : 0);
        if (bottom) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        }
        button.click();
        wait.until(d -> d.findElement(By.id("addStepsModal")).getCssValue("display").equals("block"));
    }

    private void assertModalElements() {
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals("Add Steps to Participant",
                driver.findElement(By.xpath("//div[@id='addStepsModal']//h3")).getText());

        Select participantSelect = new Select(driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant", participantSelect.getFirstSelectedOption().getText());
        assertEquals(11, participantSelect.getOptions().size());

        assertEquals("number", driver.findElement(By.id("steps_input")).getAttribute("type"));
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed());
    }

    private void selectParticipant(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    private void submitModal() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    private boolean isModalClosed(WebDriver webDriver) {
        return "none".equals(webDriver.findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    private void assertAlertAndKeepModalOpen(String expectedMessage) {
        Alert alert = wait.until(d -> d.switchTo().alert());
        assertEquals(expectedMessage, alert.getText());
        alert.accept();
        assertFalse(isModalClosed(driver));
    }

    private void addValidSteps(String participantName, String steps, long originalSteps) throws Exception {
        long before = readSteps(findParticipant(participantName));
        assertEquals(originalSteps, before);

        openModalViaButton(false);
        selectParticipant(participantName);
        driver.findElement(By.id("steps_input")).sendKeys(steps);
        submitModal();
        wait.until(d -> isModalClosed(d));
        assertEquals(before + Long.parseLong(steps), readSteps(findParticipant(participantName)));
    }

    private void runCrossBrowserCoreTests(WebDriver browser) {
        WebDriverWait browserWait = new WebDriverWait(browser, Duration.ofSeconds(10));
        browser.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");

        ((JavascriptExecutor) browser).executeScript(
                "for (var i = localStorage.length - 1; i >= 0; i--) {" +
                        "var key = localStorage.key(i);" +
                        "if (key && (key.startsWith('participant') || key === 'fitnessReload')) {" +
                        "localStorage.removeItem(key); } }");
        browser.navigate().refresh();
        browserWait.until(this::hasDefaultParticipants);

        List<WebElement> addButtons = browser.findElements(By.xpath("//button[@id='addStepsBtn'][normalize-space()='Add Steps']"));
        addButtons.get(0).click();
        new Select(browser.findElement(By.id("participant_select"))).selectByVisibleText("Mike Kid");
        browser.findElement(By.id("steps_input")).sendKeys("1000");
        browser.findElement(By.id("modal_add_steps_button")).click();
        browserWait.until(d -> "none".equals(d.findElement(By.id("addStepsModal")).getCssValue("display")));

        WebElement mike = findParticipant(browser, browserWait, "Mike Kid");
        assertEquals(9500L, readSteps(browser, mike));

        addButtons = browser.findElements(By.xpath("//button[@id='addStepsBtn'][normalize-space()='Add Steps']"));
        addButtons.get(0).click();
        browser.findElement(By.id("steps_input")).sendKeys("1000");
        browser.findElement(By.id("modal_add_steps_button")).click();
        Alert alert = browserWait.until(d -> d.switchTo().alert());
        assertEquals("Please select a participant", alert.getText());
        alert.accept();

        By modalCloseButton = By.cssSelector("#addStepsModal .w3-closebtn");
        browserWait.until(ExpectedConditions.elementToBeClickable(modalCloseButton)).click();
        browserWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        WebElement resetButton = browser.findElements(By.xpath("//button[@id='resetBtn']")).get(0);
        ((JavascriptExecutor) browser).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", resetButton);
        browserWait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
        browserWait.until(this::hasDefaultParticipants);
    }

    private boolean hasDefaultParticipants(WebDriver browser) {
        Map<String, Long> expected = new LinkedHashMap<>();
        expected.put("Mike Kid", 8500L);
        expected.put("Jill Watson", 12000L);
        expected.put("Jane Doe", 6500L);
        expected.put("John Smith", 15000L);
        expected.put("Sarah Johnson", 9800L);
        expected.put("Carlos Garcia", 11200L);
        expected.put("Emily Chen", 7300L);
        expected.put("David Brown", 13500L);
        expected.put("Maria Rodriguez", 10500L);
        expected.put("Alex Taylor", 8900L);

        List<WebElement> participants = browser.findElements(By.cssSelector("#participantsList li"));
        if (participants.size() != expected.size()) {
            return false;
        }

        Map<String, Long> actual = new LinkedHashMap<>();
        for (WebElement participant : participants) {
            String name = participant.findElement(By.cssSelector(".participant-name")).getText();
            actual.put(name, readSteps(browser, participant));
        }
        return actual.equals(expected);
    }

    private WebElement findParticipant(WebDriver browser, WebDriverWait browserWait, String name) {
        browserWait.until(d -> d.findElements(By.cssSelector("#participantsList li")).stream()
                .anyMatch(participant -> name.equals(
                        participant.findElement(By.cssSelector(".participant-name")).getText())));
        for (WebElement participant : browser.findElements(By.cssSelector("#participantsList li"))) {
            if (name.equals(participant.findElement(By.cssSelector(".participant-name")).getText())) {
                return participant;
            }
        }
        fail("Participant not found: " + name);
        return null;
    }

    private long readSteps(WebDriver browser, WebElement participant) {
        String text = participant.findElement(By.cssSelector(".participant-steps")).getText();
        return Long.parseLong(text.replace("steps", "").replace(",", "").trim());
    }
}
