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
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class Task2 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        waitForParticipants();
        resetList();
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
        assertDefaultPage();
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        for (int i = 0; i < participants.size() - 1; i++) {
            long first = getStepsFromRow(participants.get(i));
            long second = getStepsFromRow(participants.get(i + 1));
            assertTrue(first >= second);
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        assertTrue(hasTrophyColor(participants.get(0), "gold", "255, 215, 0"));
        assertTrue(hasTrophyColor(participants.get(1), "silver", "192, 192, 192"));
        assertTrue(hasTrophyColor(participants.get(2), "#cd7f32", "205, 127, 50"));

        for (int i = 3; i < participants.size(); i++) {
            assertEquals(0, participants.get(i).findElements(By.cssSelector("i.fa-trophy")).size());
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        getAddStepsButtons().get(0).click();
        checkModal();
    }

    @Test
    public void openModalViaBottomButton() {
        WebElement button = getAddStepsButtons().get(1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
        checkModal();
    }

    @Test
    public void closeModalWithCloseButton() {
        getAddStepsButtons().get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        driver.findElement(By.cssSelector("#addStepsModal [title='Close Modal']")).click();

        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertDefaultPage();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        long oldSteps = getParticipantSteps("Mike Kid");
        int oldPosition = getParticipantPosition("Mike Kid");

        openModal();
        chooseParticipant("Mike Kid");
        writeSteps("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();

        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(oldSteps + 1000, getParticipantSteps("Mike Kid"));
        assertTrue(getParticipantPosition("Mike Kid") < oldPosition);
    }

    @Test
    public void addZeroSteps() {
        openModal();
        chooseParticipant("Alex Taylor");
        writeSteps("0");
        driver.findElement(By.id("modal_add_steps_button")).click();

        assertEquals("Please enter a valid number of steps", acceptAlert());
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() {
        openModal();
        chooseParticipant("Jane Doe");
        writeSteps("999999");
        driver.findElement(By.id("modal_add_steps_button")).click();

        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(1006499, getParticipantSteps("Jane Doe"));
        assertEquals("Jane Doe", getParticipantNameByIndex(0));
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        openModal();
        writeSteps("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();

        assertEquals("Please select a participant", acceptAlert());
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openModal();
        chooseParticipant("Alex Taylor");
        writeSteps("0");
        driver.findElement(By.id("modal_add_steps_button")).click();

        assertEquals("Please enter a valid number of steps", acceptAlert());
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() {
        openModal();
        chooseParticipant("Alex Taylor");
        ((JavascriptExecutor) driver).executeScript("document.getElementById('steps_input').value='-500';");
        driver.findElement(By.id("modal_add_steps_button")).click();

        assertEquals("Please enter a valid number of steps", acceptAlert());
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() {
        openModal();
        chooseParticipant("Alex Taylor");
        driver.findElement(By.id("steps_input")).sendKeys("abc");

        String value = driver.findElement(By.id("steps_input")).getAttribute("value");
        assertFalse(value.matches(".*[A-Za-z].*"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        changeList();
        getResetButtons().get(0).click();
        waitForParticipants();
        assertDefaultPage();
    }

    @Test
    public void resetViaBottomButton() {
        changeList();
        WebElement button = getResetButtons().get(1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        button.click();
        waitForParticipants();
        assertDefaultPage();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        String expected = ((JavascriptExecutor) driver).executeScript(
                "return String(JSON.parse(localStorage.getItem('participant0')).steps + Number('9007199254740991'));"
        ).toString();

        openModal();
        chooseParticipant("Mike Kid");
        ((JavascriptExecutor) driver).executeScript("document.getElementById('steps_input').value='9007199254740991';");
        driver.findElement(By.id("modal_add_steps_button")).click();

        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());

        String actual = ((JavascriptExecutor) driver).executeScript(
                "return String(JSON.parse(localStorage.getItem('participant0')).steps);"
        ).toString();

        assertEquals(expected, actual);
    }

    @Test
    public void decimalStepValues() {
        long oldSteps = getParticipantSteps("Alex Taylor");

        openModal();
        chooseParticipant("Alex Taylor");
        ((JavascriptExecutor) driver).executeScript("document.getElementById('steps_input').value='100.5';");
        driver.findElement(By.id("modal_add_steps_button")).click();

        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(oldSteps + 100, getParticipantSteps("Alex Taylor"));
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() {
        runSimpleBrowserCheck();
    }

    @Test
    public void edgeBrowser() {
        if (!isInstalled(
                "C:\\Program Files (x86)\\Microsoft\\Edge\\Application\\msedge.exe",
                "C:\\Program Files\\Microsoft\\Edge\\Application\\msedge.exe"
        )) {
            return;
        }

        if (driver != null) {
            driver.quit();
        }

        driver = new EdgeDriver(new EdgeOptions());
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        waitForParticipants();
        resetList();
        runSimpleBrowserCheck();
    }

    @Test
    public void firefoxBrowser() {
        if (!isInstalled(
                "C:\\Program Files\\Mozilla Firefox\\firefox.exe",
                "C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe"
        )) {
            return;
        }

        if (driver != null) {
            driver.quit();
        }

        driver = new FirefoxDriver(new FirefoxOptions());
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        waitForParticipants();
        resetList();
        runSimpleBrowserCheck();
    }

    private void runSimpleBrowserCheck() {
        long oldSteps = getParticipantSteps("Mike Kid");

        openModal();
        chooseParticipant("Mike Kid");
        writeSteps("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(oldSteps + 1000, getParticipantSteps("Mike Kid"));

        openModal();
        writeSteps("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        assertEquals("Please select a participant", acceptAlert());

        driver.findElement(By.cssSelector("#addStepsModal [title='Close Modal']")).click();
        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());

        getResetButtons().get(0).click();
        waitForParticipants();
        assertDefaultPage();
    }

    private void checkModal() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals("Add Steps to Participant", driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(driver.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("#addStepsModal [title='Close Modal']")).isDisplayed());

        Select select = new Select(driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant", select.getFirstSelectedOption().getText());
        assertEquals(11, select.getOptions().size());
    }

    private void openModal() {
        getAddStepsButtons().get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void chooseParticipant(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    private void writeSteps(String steps) {
        driver.findElement(By.id("steps_input")).clear();
        driver.findElement(By.id("steps_input")).sendKeys(steps);
    }

    private String acceptAlert() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }

    private void changeList() {
        openModal();
        chooseParticipant("Mike Kid");
        writeSteps("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());

        openModal();
        chooseParticipant("Jane Doe");
        writeSteps("500");
        driver.findElement(By.id("modal_add_steps_button")).click();
        wait.until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    private void assertDefaultPage() {
        assertEquals("Fitness Challenge", driver.getTitle());
        assertEquals("Fitness Challenge", driver.findElement(By.tagName("h2")).getText());
        assertEquals(10, driver.findElements(By.cssSelector("#participantsList li")).size());
        assertEquals(defaultParticipants(), getAllParticipants());
        assertEquals("John Smith", getParticipantNameByIndex(0));
        assertTrue(hasTrophyColor(driver.findElements(By.cssSelector("#participantsList li")).get(0), "gold", "255, 215, 0"));
        assertEquals(2, getVisibleButtonsCount("Add Steps"));
        assertEquals(2, getVisibleButtonsCount("Reset List"));
    }

    private void waitForParticipants() {
        wait.until(ExpectedConditions.titleIs("Fitness Challenge"));
        wait.until(driver -> driver.findElements(By.cssSelector("#participantsList li")).size() == 10);
    }

    private void resetList() {
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge();");
        waitForParticipants();
    }

    private List<WebElement> getAddStepsButtons() {
        return driver.findElements(By.xpath("//button[normalize-space()='Add Steps' and @onclick='openModalForAddSteps()']"));
    }

    private List<WebElement> getResetButtons() {
        return driver.findElements(By.xpath("//button[normalize-space()='Reset List' and @onclick='resetFitnessChallenge()']"));
    }

    private int getVisibleButtonsCount(String text) {
        List<WebElement> buttons = driver.findElements(By.xpath("//button[normalize-space()='" + text + "']"));
        int count = 0;

        for (WebElement button : buttons) {
            if (button.isDisplayed()) {
                count++;
            }
        }

        return count;
    }

    private String getParticipantNameByIndex(int index) {
        return driver.findElements(By.cssSelector("#participantsList li"))
                .get(index)
                .findElement(By.cssSelector(".participant-name"))
                .getText()
                .trim();
    }

    private long getParticipantSteps(String name) {
        List<WebElement> rows = driver.findElements(By.cssSelector("#participantsList li"));

        for (WebElement row : rows) {
            String currentName = row.findElement(By.cssSelector(".participant-name")).getText().trim();
            if (currentName.equals(name)) {
                return getStepsFromRow(row);
            }
        }

        fail("Participant not found: " + name);
        return -1;
    }

    private int getParticipantPosition(String name) {
        List<WebElement> rows = driver.findElements(By.cssSelector("#participantsList li"));

        for (int i = 0; i < rows.size(); i++) {
            String currentName = rows.get(i).findElement(By.cssSelector(".participant-name")).getText().trim();
            if (currentName.equals(name)) {
                return i;
            }
        }

        fail("Participant not found: " + name);
        return -1;
    }

    private long getStepsFromRow(WebElement row) {
        String text = row.findElement(By.cssSelector(".participant-steps")).getText();
        text = text.replace(" steps", "").replace(",", "").replace(" ", "").replace("\u00A0", "").trim();
        return Long.parseLong(text);
    }

    private String getTrophyStyle(WebElement row) {
        return row.findElement(By.cssSelector("i.fa-trophy")).getAttribute("style").toLowerCase();
    }

    private boolean hasTrophyColor(WebElement row, String textColor, String rgbColor) {
        String style = getTrophyStyle(row);
        return style.contains(textColor) || style.contains(rgbColor);
    }

    private Map<String, Long> getAllParticipants() {
        Map<String, Long> participants = new LinkedHashMap<>();
        List<WebElement> rows = driver.findElements(By.cssSelector("#participantsList li"));

        for (WebElement row : rows) {
            String name = row.findElement(By.cssSelector(".participant-name")).getText().trim();
            participants.put(name, getStepsFromRow(row));
        }

        return participants;
    }

    private Map<String, Long> defaultParticipants() {
        Map<String, Long> participants = new LinkedHashMap<>();
        participants.put("Mike Kid", 8500L);
        participants.put("Jill Watson", 12000L);
        participants.put("Jane Doe", 6500L);
        participants.put("John Smith", 15000L);
        participants.put("Sarah Johnson", 9800L);
        participants.put("Carlos Garcia", 11200L);
        participants.put("Emily Chen", 7300L);
        participants.put("David Brown", 13500L);
        participants.put("Maria Rodriguez", 10500L);
        participants.put("Alex Taylor", 8900L);
        return participants;
    }

    private boolean isInstalled(String... paths) {
        for (String path : paths) {
            if (Files.exists(Path.of(path))) {
                return true;
            }
        }
        return false;
    }
}
