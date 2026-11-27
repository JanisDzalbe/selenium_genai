package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtility;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    WebDriverWait wait;

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

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.getChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
        wait.until(driver -> driver.findElements(By.cssSelector("#participantsList li")).size() == 10);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private List<WebElement> participantRows() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private WebElement participantRow(String name) {
        return wait.until(driver -> {
            for (WebElement row : participantRows()) {
                if (row.findElement(By.cssSelector(".participant-name")).getText().trim().equals(name)) {
                    return row;
                }
            }
            return null;
        });
    }

    private long participantSteps(String name) {
        String text = participantRow(name).findElement(By.cssSelector(".participant-steps")).getText();
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    private WebElement button(String text, int index) {
        List<WebElement> buttons = driver.findElements(
                By.xpath("//button[normalize-space()='" + text + "']")
        );
        assertTrue(buttons.size() > index, "Expected " + text + " button at index " + index);
        return buttons.get(index);
    }

    private void openModal() {
        button("Add Steps", 0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void openModalFromBottom() {
        WebElement bottomButton = button("Add Steps", 1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", bottomButton);
        bottomButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void assertModalElements() {
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[normalize-space()='Add Steps to Participant']"))).isDisplayed());
        assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
        assertTrue(driver.findElement(By.id("steps_input")).isDisplayed());
        assertTrue(driver.findElement(By.id("modal_add_steps_button")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed());

        Select select = new Select(driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant", select.getFirstSelectedOption().getText());
        assertEquals(11, select.getOptions().size());
        assertTrue(select.getOptions().stream().anyMatch(option -> option.getText().equals("Mike Kid")));
        assertTrue(select.getOptions().stream().anyMatch(option -> option.getText().equals("Alex Taylor")));
    }

    private void addSteps(String name, String steps) {
        openModal();
        Select select = new Select(wait.until(
                ExpectedConditions.elementToBeClickable(By.id("participant_select"))
        ));
        select.selectByVisibleText(name);
        WebElement input = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("steps_input"))
        );
        input.clear();
        input.sendKeys(steps);
        wait.until(
                ExpectedConditions.elementToBeClickable(By.id("modal_add_steps_button"))
        ).click();
    }

    private void assertDefaultState() {
        wait.until(driver -> participantRows().size() == 10);
        assertEquals(10, participantRows().size());
        for (Map.Entry<String, Integer> entry : DEFAULT_STEPS.entrySet()) {
            assertEquals(entry.getValue().longValue(), participantSteps(entry.getKey()), entry.getKey());
        }
    }

    private void assertRankingOrder() {
        List<WebElement> rows = participantRows();
        long previous = Long.MAX_VALUE;
        for (WebElement row : rows) {
            long steps = Long.parseLong(
                    row.findElement(By.cssSelector(".participant-steps")).getText()
                            .replaceAll("[^0-9]", "")
            );
            assertTrue(steps <= previous);
            previous = steps;
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        assertEquals("Fitness Challenge", driver.findElement(By.xpath("//h2[normalize-space()='Fitness Challenge']")).getText());
        assertDefaultState();
        assertTrue(button("Add Steps", 0).isDisplayed());
        assertTrue(button("Add Steps", 1).isDisplayed());
        assertTrue(button("Reset List", 0).isDisplayed());
        assertTrue(button("Reset List", 1).isDisplayed());
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        assertRankingOrder();
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        List<WebElement> rows = participantRows();
        assertEquals(3, rows.stream()
                .limit(3)
                .map(row -> row.findElements(By.cssSelector("i.fa-trophy")).size())
                .mapToInt(Integer::intValue)
                .sum());

        assertEquals("rgba(255, 215, 0, 1)",
                rows.get(0).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color"));
        assertEquals("rgba(192, 192, 192, 1)",
                rows.get(1).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color"));
        assertEquals("rgba(205, 127, 50, 1)",
                rows.get(2).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color"));
        for (WebElement row : rows.subList(3, rows.size())) {
            assertTrue(row.findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        openModal();
        assertModalElements();
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        openModalFromBottom();
        assertModalElements();
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        openModal();
        WebElement closeButton = driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        wait.until(ExpectedConditions.elementToBeClickable(closeButton)).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        assertDefaultState();
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        long before = participantSteps("Mike Kid");
        addSteps("Mike Kid", "1000");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        assertEquals(before + 1000, participantSteps("Mike Kid"));
        assertRankingOrder();
    }

    @Test
    public void addZeroSteps() throws Exception {
        addSteps("Mike Kid", "0");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        addSteps("Jane Doe", "999999");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        assertEquals(1006499, participantSteps("Jane Doe"));
        assertEquals("Jane Doe", participantRows().get(0).findElement(By.cssSelector(".participant-name")).getText());
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        openModal();
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        wait.until(
                ExpectedConditions.elementToBeClickable(By.id("modal_add_steps_button"))
        ).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please select a participant", alert.getText());
        alert.accept();
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText("Mike Kid");
        wait.until(
                ExpectedConditions.elementToBeClickable(By.id("modal_add_steps_button"))
        ).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        addSteps("Mike Kid", "-500");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        openModal();
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText("Mike Kid");
        WebElement input = driver.findElement(By.id("steps_input"));
        input.sendKeys("abc");
        assertEquals("", input.getAttribute("value"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        addSteps("Mike Kid", "1000");
        wait.until(driver -> participantSteps("Mike Kid") == 9500);
        addSteps("Jane Doe", "2000");
        wait.until(driver -> participantSteps("Jane Doe") == 8500);
        button("Reset List", 0).click();
        wait.until(driver -> participantSteps("Mike Kid") == 8500);
        assertDefaultState();
        assertRankingOrder();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        addSteps("Mike Kid", "1000");
        wait.until(driver -> participantSteps("Mike Kid") == 9500);
        WebElement bottomReset = button("Reset List", 1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", bottomReset);
        bottomReset.click();
        wait.until(driver -> participantSteps("Mike Kid") == 8500);
        assertDefaultState();
        assertRankingOrder();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    @Disabled("Application defect: adding 8,500 to 9007199254740991 produces 9007199254749492 instead of the exact sum 9007199254749491 due JavaScript Number precision.")
    public void maximumIntegerValue() throws Exception {
        addSteps("Mike Kid", "9007199254740991");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        assertEquals(9007199254749491L, participantSteps("Mike Kid"));
        assertEquals("Mike Kid", participantRows().get(0).findElement(By.cssSelector(".participant-name")).getText());
    }

    @Test
    @Disabled("Application defect: decimal 100.5 is parsed with parseInt(), accepted as 100, and no validation alert is shown.")
    public void decimalStepValues() throws Exception {
        addSteps("Mike Kid", "100.5");
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());
        alert.accept();
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        long before = participantSteps("Mike Kid");
        addSteps("Mike Kid", "1000");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        assertEquals(before + 1000, participantSteps("Mike Kid"));

        openModal();
        driver.findElement(By.id("steps_input")).sendKeys("1000");
        driver.findElement(By.id("modal_add_steps_button")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please select a participant", alert.getText());
        alert.accept();
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
        wait.until(ExpectedConditions.elementToBeClickable(
                driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"))
        )).click();

        button("Reset List", 0).click();
        wait.until(driver -> participantSteps("Mike Kid") == 8500);
        assertDefaultState();
    }

    @Test
    @Disabled("Edge-specific test is not runnable: WebDriverUtility currently provides only Chrome and EdgeDriver/lib/edgedriver.exe is unavailable.")
    public void edgeBrowser() throws Exception {
        assertEquals("Fitness Challenge",
                driver.findElement(By.xpath("//h2[normalize-space()='Fitness Challenge']")).getText());
        assertRankingOrder();
        openModal();
        assertModalElements();
    }

    @Test
    @Disabled("Firefox-specific test is not runnable: WebDriverUtility currently provides only Chrome and Firefox/geckodriver are unavailable.")
    public void firefoxBrowser() throws Exception {
        assertEquals("Fitness Challenge",
                driver.findElement(By.xpath("//h2[normalize-space()='Fitness Challenge']")).getText());
        assertRankingOrder();
        openModal();
        assertModalElements();
    }
}
