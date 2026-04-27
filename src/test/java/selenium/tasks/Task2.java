package selenium.tasks;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    WebDriverWait wait;

    private final String URL = "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";

    @BeforeEach
    public void openPage() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().window().maximize();

        driver.get(URL);

        // Reset localStorage before every test
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10
        ));
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void firstTimePageLoad() {
        assertEquals("Fitness Challenge", driver.findElement(By.tagName("h2")).getText());

        List<WebElement> participants = getParticipants();
        assertEquals(10, participants.size());

        Map<String, Long> expected = getDefaultParticipants();

        for (Map.Entry<String, Long> entry : expected.entrySet()) {
            assertEquals(entry.getValue(), getStepsByName(entry.getKey()));
        }

        assertEquals(2, driver.findElements(By.id("addStepsBtn")).size());
        assertEquals(2, driver.findElements(By.id("resetBtn")).size());

        for (WebElement button : driver.findElements(By.id("addStepsBtn"))) {
            assertTrue(button.isDisplayed());
            assertEquals("Add Steps", button.getText());
        }

        for (WebElement button : driver.findElements(By.id("resetBtn"))) {
            assertTrue(button.isDisplayed());
            assertEquals("Reset List", button.getText());
        }
    }

    @Test
    public void rankingOrder() {
        List<WebElement> participants = getParticipants();

        for (int i = 0; i < participants.size() - 1; i++) {
            long currentSteps = getStepsFromParticipant(participants.get(i));
            long nextSteps = getStepsFromParticipant(participants.get(i + 1));

            assertTrue(currentSteps >= nextSteps);
        }
    }

    @Test
    public void medalTrophyIcons() {
        List<WebElement> participants = getParticipants();

        assertEquals("rgba(255, 215, 0, 1)",
                participants.get(0).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color"));

        assertEquals("rgba(192, 192, 192, 1)",
                participants.get(1).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color"));

        assertEquals("rgba(205, 127, 50, 1)",
                participants.get(2).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color"));

        for (int i = 3; i < participants.size(); i++) {
            assertEquals(0, participants.get(i).findElements(By.cssSelector("i.fa-trophy")).size());
        }
    }

    @Test
    public void openModalViaTopButton() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();

        verifyModalIsOpen();
    }

    @Test
    public void openModalViaBottomButton() {
        scrollToBottom();

        driver.findElements(By.id("addStepsBtn")).get(1).click();

        verifyModalIsOpen();
    }

    @Test
    public void closeModalWithCloseButton() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        verifyModalIsOpen();

        driver.findElement(By.cssSelector(".w3-closebtn")).click();

        wait.until(ExpectedConditions.attributeContains(
                By.id("addStepsModal"), "style", "display: none"
        ));

        assertEquals(10, getParticipants().size());
        assertEquals(15000, getStepsByName("John Smith"));
    }

    @Test
    public void addValidStepsToParticipant() {
        long beforeSteps = getStepsByName("Mike Kid");

        openAddStepsModal();
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();

        waitUntilModalClosed();

        assertEquals(beforeSteps + 1000, getStepsByName("Mike Kid"));
        rankingOrder();
    }

    @Test
    public void addZeroSteps() {
        openAddStepsModal();
        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("0");
        driver.findElement(By.id("modal_add_steps_button")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();

        assertTrue(isModalOpen());
    }

    @Test
    public void addLargeNumberOfSteps() {
        openAddStepsModal();
        selectParticipant("Jane Doe");
        driver.findElement(By.id("steps_input")).sendKeys("999999");
        driver.findElement(By.id("modal_add_steps_button")).click();

        waitUntilModalClosed();

        assertEquals(1006499, getStepsByName("Jane Doe"));
        assertEquals("Jane Doe", getParticipantName(getParticipants().get(0)));
    }

    @Test
    public void submitWithoutSelectingParticipant() {
        openAddStepsModal();

        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please select a participant", alert.getText());
        alert.accept();

        assertTrue(isModalOpen());
    }

    @Test
    public void submitWithoutEnteringSteps() {
        openAddStepsModal();

        selectParticipant("Mike Kid");
        driver.findElement(By.id("modal_add_steps_button")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();

        assertTrue(isModalOpen());
    }

    @Test
    public void submitWithNegativeSteps() {
        openAddStepsModal();

        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("-500");
        driver.findElement(By.id("modal_add_steps_button")).click();

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();

        assertTrue(isModalOpen());
    }

    @Test
    public void submitWithNonNumericInput() {
        openAddStepsModal();

        selectParticipant("Mike Kid");

        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");

        assertEquals("", input.getAttribute("value"));
    }

    @Test
    public void resetViaTopButton() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "2000");

        driver.findElements(By.id("resetBtn")).get(0).click();

        verifyDefaultState();
    }

    @Test
    public void resetViaBottomButton() {
        addSteps("Mike Kid", "1000");

        scrollToBottom();
        driver.findElements(By.id("resetBtn")).get(1).click();

        verifyDefaultState();
    }

    @Test
    public void maximumIntegerValue() {
        openAddStepsModal();

        selectParticipant("Jane Doe");
        driver.findElement(By.id("steps_input")).sendKeys("9007199254740991");
        driver.findElement(By.id("modal_add_steps_button")).click();

        waitUntilModalClosed();

        assertTrue(getStepsByName("Jane Doe") > 9007199254740000L);
        assertEquals("Jane Doe", getParticipantName(getParticipants().get(0)));
    }

    @Test
    public void decimalStepValues() {
        openAddStepsModal();

        selectParticipant("Mike Kid");
        driver.findElement(By.id("steps_input")).sendKeys("100.5");
        driver.findElement(By.id("modal_add_steps_button")).click();

        waitUntilModalClosed();

        // Page accepts decimal value, probably converts it to 100
        assertTrue(getStepsByName("Mike Kid") >= 8600);
    }

    // Helper methods

    private List<WebElement> getParticipants() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private String getParticipantName(WebElement participant) {
        return participant.findElement(By.cssSelector(".participant-name")).getText();
    }

    private long getStepsFromParticipant(WebElement participant) {
        String text = participant.findElement(By.cssSelector(".participant-steps")).getText();
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    private long getStepsByName(String name) {
        for (WebElement participant : getParticipants()) {
            if (getParticipantName(participant).equals(name)) {
                return getStepsFromParticipant(participant);
            }
        }
        fail("Participant not found: " + name);
        return 0;
    }

    private String getTrophyStyle(WebElement participant) {
        return participant.findElement(By.cssSelector("i.fa-trophy")).getAttribute("style");
    }

    private void openAddStepsModal() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        verifyModalIsOpen();
    }

    private void verifyModalIsOpen() {
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant", driver.findElement(By.cssSelector("#addStepsModal h3")).getText());

        assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(driver.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector(".w3-closebtn")).isDisplayed());

        Select dropdown = new Select(driver.findElement(By.id("participant_select")));

        assertEquals("Choose participant", dropdown.getFirstSelectedOption().getText());
        assertEquals(11, dropdown.getOptions().size());
    }

    private boolean isModalOpen() {
        return driver.findElement(By.id("addStepsModal")).isDisplayed();
    }

    private void waitUntilModalClosed() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void selectParticipant(String name) {
        Select dropdown = new Select(driver.findElement(By.id("participant_select")));
        dropdown.selectByVisibleText(name);
    }

    private void addSteps(String participantName, String steps) {
        openAddStepsModal();
        selectParticipant(participantName);
        driver.findElement(By.id("steps_input")).sendKeys(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
        waitUntilModalClosed();
    }

    private void verifyDefaultState() {
        Map<String, Long> expected = getDefaultParticipants();

        for (Map.Entry<String, Long> entry : expected.entrySet()) {
            assertEquals(entry.getValue(), getStepsByName(entry.getKey()));
        }

        assertEquals("John Smith", getParticipantName(getParticipants().get(0)));
        assertEquals("David Brown", getParticipantName(getParticipants().get(1)));
        assertEquals("Jill Watson", getParticipantName(getParticipants().get(2)));

        medalTrophyIcons();
    }

    private Map<String, Long> getDefaultParticipants() {
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

    private void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }
}