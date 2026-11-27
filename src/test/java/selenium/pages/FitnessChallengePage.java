package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FitnessChallengePage {
    private static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final By PARTICIPANTS = By.cssSelector("#participantsList li");
    private static final By ADD_STEPS_BUTTONS = By.id("addStepsBtn");
    private static final By RESET_BUTTONS = By.id("resetBtn");
    private static final By MODAL = By.id("addStepsModal");

    private final WebDriver driver;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
    }

    public FitnessChallengePage open() {
        driver.get(URL);
        waitForParticipants();
        return this;
    }

    public String title() {
        return driver.getTitle();
    }

    public Map<String, Integer> participantStepCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            counts.put(participantName(row), participantSteps(row));
        }
        return counts;
    }

    public List<Integer> participantStepsInOrder() {
        return participantRows().stream().map(this::participantSteps).toList();
    }

    public List<String> participantNamesInOrder() {
        return participantRows().stream().map(this::participantName).toList();
    }

    public int participantSteps(String name) {
        return participantSteps(participantRow(name));
    }

    public String participantNameAt(int index) {
        return participantName(participantRows().get(index));
    }

    public String trophyColorAt(int index) {
        return participantRows().get(index).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color");
    }

    public boolean participantHasTrophy(int index) {
        return !participantRows().get(index).findElements(By.cssSelector("i.fa-trophy")).isEmpty();
    }

    public int addStepsButtonCount() {
        return driver.findElements(ADD_STEPS_BUTTONS).size();
    }

    public boolean allAddStepsButtonsVisible() {
        return driver.findElements(ADD_STEPS_BUTTONS).stream().allMatch(WebElement::isDisplayed);
    }

    public int resetButtonCount() {
        return driver.findElements(RESET_BUTTONS).size();
    }

    public boolean allResetButtonsVisible() {
        return driver.findElements(RESET_BUTTONS).stream().allMatch(WebElement::isDisplayed);
    }

    public AddStepsModal openAddStepsModal() {
        driver.findElements(ADD_STEPS_BUTTONS).get(0).click();
        return waitForModal();
    }

    public AddStepsModal openAddStepsModalAtBottom() {
        List<WebElement> buttons = driver.findElements(ADD_STEPS_BUTTONS);
        WebElement bottomButton = buttons.get(buttons.size() - 1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", bottomButton);
        bottomButton.click();
        return waitForModal();
    }

    public boolean participantListVisible() {
        return driver.findElement(By.id("participantsList")).isDisplayed();
    }

    public double storedSteps(String participantIndex) {
        Number value = (Number) ((JavascriptExecutor) driver).executeScript(
                "return JSON.parse(localStorage.getItem('participant' + arguments[0])).steps;", participantIndex);
        if (value == null) {
            throw new IllegalStateException("No stored step count for participant index " + participantIndex);
        }
        return value.doubleValue();
    }

    public double addStoredSteps(double originalSteps, String stepsToAdd) {
        Number value = (Number) ((JavascriptExecutor) driver).executeScript(
                "return Number(arguments[0]) + Number(arguments[1]);", originalSteps, stepsToAdd);
        if (value == null) {
            throw new IllegalStateException("Could not calculate the updated step count");
        }
        return value.doubleValue();
    }

    public String storedStepsDisplay(String participantIndex) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "return JSON.parse(localStorage.getItem('participant' + arguments[0])).steps.toLocaleString() + ' steps';",
                participantIndex);
    }

    public String participantStepsDisplay(String name) {
        return participantRow(name).findElement(By.className("participant-steps")).getText();
    }

    public void resetAt(int buttonIndex) {
        WebElement currentFirstRow = participantRows().get(0);
        WebElement resetButton = driver.findElements(RESET_BUTTONS).get(buttonIndex);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true)", resetButton);
        resetButton.click();
        new WebDriverWait(driver, WAIT_TIMEOUT).until(ExpectedConditions.stalenessOf(currentFirstRow));
        waitForParticipants();
    }

    private AddStepsModal waitForModal() {
        new WebDriverWait(driver, WAIT_TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(MODAL));
        return new AddStepsModal(driver);
    }

    private List<WebElement> participantRows() {
        waitForParticipants();
        return driver.findElements(PARTICIPANTS);
    }

    private void waitForParticipants() {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.findElements(PARTICIPANTS).size() == 10);
    }

    private WebElement participantRow(String name) {
        return participantRows().stream()
                .filter(row -> name.equals(participantName(row)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private String participantName(WebElement row) {
        return row.findElement(By.className("participant-name")).getText();
    }

    private int participantSteps(WebElement row) {
        String steps = row.findElement(By.className("participant-steps")).getText();
        return Integer.parseInt(steps.replaceAll("[^0-9]", ""));
    }
}
