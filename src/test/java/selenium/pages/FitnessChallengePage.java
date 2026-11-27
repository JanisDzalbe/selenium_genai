package selenium.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FitnessChallengePage {
    private static final String FITNESS_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private static final By PARTICIPANTS = By.cssSelector("#participantsList li");
    private static final By ADD_STEPS_BUTTONS = By.id("addStepsBtn");
    private static final By RESET_BUTTONS = By.id("resetBtn");
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By MODAL_SUBMIT_BUTTON = By.id("modal_add_steps_button");
    private static final By MODAL_CLOSE_BUTTON =
            By.cssSelector("#addStepsModal .w3-closebtn");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public void open() {
        driver.get(FITNESS_URL);
        wait.until(d -> d.findElement(By.id("participantsList")).isDisplayed());
    }

    public String title() {
        return driver.findElement(By.tagName("h2")).getText();
    }

    public Map<String, Long> participants() {
        Map<String, Long> participants = new HashMap<>();
        for (WebElement row : participantRows()) {
            participants.put(participantName(row), participantSteps(row));
        }
        return participants;
    }

    public int participantCount() {
        return participantRows().size();
    }

    public List<Long> participantStepCounts() {
        return participantRows().stream().map(this::participantSteps).toList();
    }

    public List<String> participantNames() {
        return participantRows().stream().map(this::participantName).toList();
    }

    public long participantSteps(String participant) {
        for (WebElement row : participantRows()) {
            if (participant.equals(participantName(row))) {
                return participantSteps(row);
            }
        }
        throw new AssertionError("Participant was not displayed: " + participant);
    }

    public String trophyColorAt(int participantIndex) {
        return participantRows().get(participantIndex)
                .findElement(By.cssSelector("i.fa-trophy")).getCssValue("color");
    }

    public boolean hasTrophyAt(int participantIndex) {
        return !participantRows().get(participantIndex)
                .findElements(By.cssSelector("i.fa-trophy")).isEmpty();
    }

    public int addStepsButtonCount() {
        return driver.findElements(ADD_STEPS_BUTTONS).size();
    }

    public int resetButtonCount() {
        return driver.findElements(RESET_BUTTONS).size();
    }

    public boolean areAddStepsButtonsVisible() {
        return driver.findElements(ADD_STEPS_BUTTONS).stream().allMatch(WebElement::isDisplayed);
    }

    public boolean areResetButtonsVisible() {
        return driver.findElements(RESET_BUTTONS).stream().allMatch(WebElement::isDisplayed);
    }

    public void openAddStepsModal(int buttonIndex) {
        clickButton(ADD_STEPS_BUTTONS, buttonIndex);
        wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL));
        wait.until(d -> {
            String transform = d.findElement(
                    By.cssSelector("#addStepsModal .w3-modal-content")).getCssValue("transform");
            return "none".equals(transform) || transform.startsWith("matrix(1, 0, 0, 1,");
        });
    }

    public void closeModal() {
        driver.findElement(MODAL_CLOSE_BUTTON).click();
        waitForModalToClose();
    }

    public void waitForModalToClose() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    public boolean isModalVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL)).isDisplayed();
    }

    public String modalTitle() {
        return driver.findElement(By.cssSelector("#addStepsModal h3")).getText();
    }

    public boolean isParticipantSelectVisible() {
        return driver.findElement(PARTICIPANT_SELECT).isDisplayed();
    }

    public String participantSelectTagName() {
        return driver.findElement(PARTICIPANT_SELECT).getTagName();
    }

    public List<String> participantOptions() {
        return new Select(driver.findElement(PARTICIPANT_SELECT)).getOptions().stream()
                .map(WebElement::getText)
                .toList();
    }

    public String selectedParticipantOption() {
        return new Select(driver.findElement(PARTICIPANT_SELECT))
                .getFirstSelectedOption().getText();
    }

    public void selectParticipant(String participant) {
        new Select(driver.findElement(PARTICIPANT_SELECT)).selectByVisibleText(participant);
    }

    public String stepsInputType() {
        return driver.findElement(STEPS_INPUT).getAttribute("type");
    }

    public String stepsInputValue() {
        return driver.findElement(STEPS_INPUT).getAttribute("value");
    }

    public void enterSteps(String steps) {
        WebElement input = driver.findElement(STEPS_INPUT);
        scrollIntoView(input);
        input.sendKeys(steps);
    }

    public String modalSubmitButtonText() {
        return driver.findElement(MODAL_SUBMIT_BUTTON).getText();
    }

    public boolean isModalSubmitButtonEnabled() {
        return driver.findElement(MODAL_SUBMIT_BUTTON).isEnabled();
    }

    public String modalCloseButtonTitle() {
        return driver.findElement(MODAL_CLOSE_BUTTON).getAttribute("title");
    }

    public void submitSteps() {
        WebElement button = driver.findElement(MODAL_SUBMIT_BUTTON);
        scrollIntoView(button);
        button.click();
    }

    public String alertText() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        return alert.getText();
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    public void clickResetButton(int buttonIndex) {
        WebElement oldFirstRow = participantRows().get(0);
        clickButton(RESET_BUTTONS, buttonIndex);
        wait.until(ExpectedConditions.stalenessOf(oldFirstRow));
        wait.until(d -> participantRows().size() == 10);
    }

    private List<WebElement> participantRows() {
        return driver.findElements(PARTICIPANTS);
    }

    private String participantName(WebElement row) {
        return row.findElement(By.className("participant-name")).getText();
    }

    private long participantSteps(WebElement row) {
        String text = row.findElement(By.className("participant-steps")).getText();
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    private void clickButton(By locator, int buttonIndex) {
        WebElement button = driver.findElements(locator).get(buttonIndex);
        scrollIntoView(button);
        button.click();
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
    }
}
