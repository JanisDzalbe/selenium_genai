package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    private static final String FITNESS_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
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
    private WebDriverWait wait;

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
        wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        driver.manage().window().maximize();
        driver.get(FITNESS_URL);
        waitForParticipants();
    }

    private void waitForParticipants() {
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList > div > li"), 10));
    }

    private List<WebElement> participantRows() {
        return driver.findElements(By.cssSelector("#participantsList > div > li"));
    }

    private List<String> displayedParticipantNames() {
        List<String> names = new ArrayList<>();
        for (WebElement row : participantRows()) {
            names.add(row.findElement(By.className("participant-name")).getText().trim());
        }
        return names;
    }

    private WebElement participantRow(String name) {
        return wait.until(driver -> {
            for (WebElement row : participantRows()) {
                try {
                    String participantName = row
                            .findElement(By.className("participant-name"))
                            .getText()
                            .trim();

                    if (participantName.equals(name)) {
                        return row;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    // The list changed. Try again.
                }
            }
            return null;
        });
    }

    private long displayedStepCount(WebElement row) {
        String text = row.findElement(By.className("participant-steps")).getText();
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    private long displayedStepCount(String name) {
        return displayedStepCount(participantRow(name));
    }

    private Map<String, Long> displayedStepCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            String name = row.findElement(By.className("participant-name")).getText().trim();
            counts.put(name, displayedStepCount(row));
        }
        return counts;
    }

    private WebElement pageButton(String id, int position) {
        List<WebElement> buttons = driver.findElements(By.id(id));
        assertEquals(2, buttons.size(), id + " should appear at the top and bottom of the page");
        return buttons.get(position);
    }

    private void openAddStepsModal(int buttonPosition) {
        WebElement button = pageButton("addStepsBtn", buttonPosition);
        if (buttonPosition == 1) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        }
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void assertModalContents() {
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant",
                modal.findElement(By.tagName("h3")).getText().trim());

        WebElement dropdown = modal.findElement(By.id("participant_select"));
        Select participants = new Select(dropdown);
        List<String> options = participants.getOptions().stream()
                .map(option -> option.getText().trim())
                .toList();
        assertEquals(11, options.size());
        assertEquals("Choose participant", options.get(0));
        assertEquals(DEFAULT_NAMES, options.subList(1, options.size()));

        WebElement stepsInput = modal.findElement(By.id("steps_input"));
        assertEquals("number", stepsInput.getAttribute("type"));
        assertTrue(stepsInput.isDisplayed());
        assertTrue(modal.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertEquals("Add Steps",
                modal.findElement(By.id("modal_add_steps_button")).getText().trim());

        WebElement closeButton = modal.findElement(By.cssSelector(".w3-closebtn[title='Close Modal']"));
        assertTrue(closeButton.isDisplayed());
        assertTrue(closeButton.getAttribute("class").contains("w3-display-topright"));
        assertEquals("×", closeButton.getText().trim());
    }

    private void selectParticipantAndEnterSteps(String participant, String steps) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(participant);
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys(steps);
    }

    private void submitAndAcceptAlert(String expectedText) {
        driver.findElement(By.id("modal_add_steps_button")).click();
        org.openqa.selenium.Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
        alert.accept();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void addSteps(String participant, String steps) {
        openAddStepsModal(0);
        selectParticipantAndEnterSteps(participant, steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        waitForParticipants();
    }

    private void assertDefaultParticipants() {
        assertEquals(10, participantRows().size());
        assertEquals(DEFAULT_STEPS, displayedStepCounts());
    }

    private void assertDefaultRankingAndTrophies() {
        assertEquals(DEFAULT_RANKING, displayedParticipantNames());
        assertTrophy(0, "rgb(255, 215, 0)");
        assertTrophy(1, "rgb(192, 192, 192)");
        assertTrophy(2, "rgb(205, 127, 50)");
        for (int i = 3; i < participantRows().size(); i++) {
            assertTrue(participantRows().get(i).findElements(By.cssSelector("i.fa.fa-trophy")).isEmpty(),
                    "Participant ranked " + (i + 1) + " should not have a trophy");
        }
    }

    private void assertTrophy(int position, String expectedColor) {
        WebElement trophy = participantRows().get(position)
                .findElement(By.cssSelector("i.fa.fa-trophy"));

        String actualColor = trophy.getCssValue("color")
                .replace("rgba(", "rgb(")
                .replace(", 1)", ")");

        assertEquals(expectedColor, actualColor);
    }

    private void resetUsingButton(int position) {
        WebElement previousFirstRow = participantRows().get(0);
        WebElement resetButton = pageButton("resetBtn", position);
        if (position == 1) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", resetButton);
        }
        wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
        wait.until(ExpectedConditions.stalenessOf(previousFirstRow));
        waitForParticipants();
        assertDefaultParticipants();
        assertDefaultRankingAndTrophies();
    }

    private void assertAlertForNoParticipant() {
        openAddStepsModal(0);
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        submitAndAcceptAlert("Please select a participant");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    private void runCrossBrowserScenarios() {
        long previousSteps = displayedStepCount("Mike Kid");
        addSteps("Mike Kid", "1000");
        assertEquals(previousSteps + 1_000, displayedStepCount("Mike Kid"));
        assertTrue(displayedParticipantNames().indexOf("Mike Kid")
                > displayedParticipantNames().indexOf("Jill Watson"));

        assertAlertForNoParticipant();
        driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
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
        assertEquals("Fitness Challenge", driver.findElement(By.tagName("h2")).getText().trim());
        assertDefaultParticipants();
        assertEquals(DEFAULT_NAMES.size(), displayedStepCounts().size());
        assertEquals(2, driver.findElements(By.id("addStepsBtn")).size());
        assertEquals(2, driver.findElements(By.id("resetBtn")).size());
        for (WebElement button : driver.findElements(By.id("addStepsBtn"))) {
            assertTrue(button.isDisplayed());
            assertEquals("Add Steps", button.getText().trim());
        }
        for (WebElement button : driver.findElements(By.id("resetBtn"))) {
            assertTrue(button.isDisplayed());
            assertEquals("Reset List", button.getText().trim());
        }
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<WebElement> rows = participantRows();
        List<Long> steps = rows.stream().map(this::displayedStepCount).toList();
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
        driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(driver.findElement(By.tagName("h2")).isDisplayed());
        assertDefaultParticipants();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long previousSteps = displayedStepCount("Mike Kid");
        addSteps("Mike Kid", "1000");
        assertEquals(previousSteps + 1_000, displayedStepCount("Mike Kid"));
        assertTrue(displayedParticipantNames().indexOf("Mike Kid")
                > displayedParticipantNames().indexOf("Sarah Johnson"));
        rankingOrder();
    }

    @Test
    public void addZeroSteps() {
        openAddStepsModal(0);
        selectParticipantAndEnterSteps("Mike Kid", "0");
        submitAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() {
        long previousSteps = displayedStepCount("Mike Kid");
        addSteps("Mike Kid", "999999");
        assertEquals(previousSteps + 999_999, displayedStepCount("Mike Kid"));
        assertEquals("Mike Kid", displayedParticipantNames().get(0));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        assertAlertForNoParticipant();
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openAddStepsModal(0);
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        submitAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() {
        openAddStepsModal(0);
        selectParticipantAndEnterSteps("Mike Kid", "-500");
        submitAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() {
        openAddStepsModal(0);
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText("Mike Kid");
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.sendKeys("abc");
        String value = stepsInput.getAttribute("value");
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
        long previousSteps = displayedStepCount("Mike Kid");
        openAddStepsModal(0);
        selectParticipantAndEnterSteps("Mike Kid", "9007199254740991");
        driver.findElement(By.id("modal_add_steps_button")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        waitForParticipants();

        String expectedDisplay = (String) ((JavascriptExecutor) driver).executeScript(
                "return (Number(arguments[0]) + Number(arguments[1])).toLocaleString();",
                previousSteps, "9007199254740991");
        assertEquals(expectedDisplay + " steps",
                participantRow("Mike Kid").findElement(By.className("participant-steps")).getText());
        assertEquals("Mike Kid", displayedParticipantNames().get(0));
    }

    @Test
    public void decimalStepValues() {
        openAddStepsModal(0);
        selectParticipantAndEnterSteps("Mike Kid", "100.5");
        submitAndAcceptAlert("Please enter a valid number of steps");
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
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
