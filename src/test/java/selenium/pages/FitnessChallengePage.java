package selenium.pages;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongPredicate;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FitnessChallengePage {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");
    private static final By MODAL = By.id("addStepsModal");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public FitnessChallengePage open() {
        driver.get(URL);
        waitForParticipantCount(10);
        return this;
    }

    public String title() {
        return driver.findElement(By.cssSelector("h2")).getText();
    }

    public int participantCount() {
        return participantRows().size();
    }

    public Map<String, Long> participantSteps() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            result.put(participantName(row), stepsFromRow(row));
        }
        return result;
    }

    public List<Long> rankedStepCounts() {
        return participantRows().stream().map(this::stepsFromRow).toList();
    }

    public String participantAt(int rank) {
        return participantName(participantRows().get(rank));
    }

    public int rankOf(String name) {
        List<WebElement> rows = participantRows();
        for (int rank = 0; rank < rows.size(); rank++) {
            if (name.equals(participantName(rows.get(rank)))) {
                return rank;
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    public long stepsFor(String name) {
        return participantRows().stream()
                .filter(row -> name.equals(participantName(row)))
                .findFirst()
                .map(this::stepsFromRow)
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    public String trophyColorAt(int rank) {
        return Color.fromString(participantRows().get(rank)
                .findElement(By.cssSelector("i.fa-trophy"))
                .getCssValue("color")).asHex();
    }

    public boolean hasTrophyAt(int rank) {
        return !participantRows().get(rank).findElements(By.cssSelector("i.fa-trophy")).isEmpty();
    }

    public int buttonCount(String buttonText) {
        return driver.findElements(buttonLocator(buttonText)).size();
    }

    public boolean allButtonsDisplayed(String buttonText) {
        return driver.findElements(buttonLocator(buttonText)).stream().allMatch(WebElement::isDisplayed);
    }

    public void clickButton(String buttonText, int index) {
        List<WebElement> buttons = driver.findElements(buttonLocator(buttonText));
        if (index < 0 || index >= buttons.size()) {
            throw new IllegalArgumentException("Button index out of range: " + index);
        }
        WebElement button = buttons.get(index);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", button);
        button.click();
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);");
    }

    public void openAddStepsModal() {
        clickButton("Add Steps", 0);
        wait.until(webDriver -> isModalOpen());
    }

    public boolean isModalOpen() {
        return driver.findElement(MODAL).isDisplayed();
    }

    public String modalTitle() {
        return driver.findElement(MODAL).findElement(By.cssSelector("h3")).getText();
    }

    public boolean participantDropdownDisplayed() {
        return driver.findElement(By.id("participant_select")).isDisplayed();
    }

    public boolean stepsInputDisplayed() {
        return driver.findElement(By.id("steps_input")).isDisplayed();
    }

    public String stepsInputType() {
        return driver.findElement(By.id("steps_input")).getAttribute("type");
    }

    public boolean submitButtonDisplayed() {
        return driver.findElement(By.id("modal_add_steps_button")).isDisplayed();
    }

    public String submitButtonText() {
        return driver.findElement(By.id("modal_add_steps_button")).getText();
    }

    public boolean closeButtonDisplayed() {
        return driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).isDisplayed();
    }

    public String closeButtonPosition() {
        return driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"))
                .getCssValue("position");
    }

    public List<String> participantOptions() {
        return new Select(driver.findElement(By.id("participant_select")))
                .getOptions().stream().map(WebElement::getText).toList();
    }

    public String selectedParticipantOption() {
        return new Select(driver.findElement(By.id("participant_select")))
                .getFirstSelectedOption().getText();
    }

    public void selectParticipant(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    public void enterSteps(String value) {
        WebElement input = driver.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(value);
    }

    public void setStepsValue(String value) {
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('steps_input').value = arguments[0];", value);
    }

    public String stepsInputValue() {
        return driver.findElement(By.id("steps_input")).getAttribute("value");
    }

    public void submitSteps() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    public void closeModal() {
        driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
        waitForModalClosed();
    }

    public String alertText() {
        wait.until(webDriver -> {
            try {
                webDriver.switchTo().alert();
                return true;
            } catch (org.openqa.selenium.NoAlertPresentException exception) {
                return false;
            }
        });
        return driver.switchTo().alert().getText();
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    public void waitForModalClosed() {
        wait.until(webDriver -> !isModalOpen());
    }

    public void waitForParticipantCount(int expectedCount) {
        wait.until(webDriver -> participantCount() == expectedCount);
    }

    public void waitForParticipant(String name, LongPredicate condition) {
        wait.until(webDriver -> {
            Long steps = participantSteps().get(name);
            return steps != null && condition.test(steps);
        });
    }

    public void waitForDefaultParticipants(Map<String, Long> expected) {
        wait.until(webDriver -> expected.equals(participantSteps()));
    }

    private List<WebElement> participantRows() {
        return driver.findElements(PARTICIPANT_ROWS);
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText();
    }

    private long stepsFromRow(WebElement row) {
        String text = row.findElement(By.cssSelector(".participant-steps")).getText();
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    private By buttonLocator(String label) {
        String id = switch (label) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported button: " + label);
        };
        return By.id(id);
    }
}
