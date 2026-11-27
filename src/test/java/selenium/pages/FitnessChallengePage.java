package selenium.pages;

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

public class FitnessChallengePage {
    private static final String FITNESS_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList > div > li");

    private final WebDriver driver;
    private final WebDriverWait wait;
    private Alert pendingAlert;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public void open() {
        driver.manage().window().maximize();
        driver.get(FITNESS_URL);
        waitForParticipants();
    }

    public void waitForParticipants() {
        wait.until(ExpectedConditions.numberOfElementsToBe(PARTICIPANT_ROWS, 10));
    }

    public int getParticipantCount() {
        return driver.findElements(PARTICIPANT_ROWS).size();
    }

    public List<String> getParticipantNames() {
        List<String> names = new ArrayList<>();
        for (WebElement row : participantRows()) {
            names.add(row.findElement(By.className("participant-name")).getText().trim());
        }
        return names;
    }

    public List<Long> getRankedStepCounts() {
        List<Long> counts = new ArrayList<>();
        for (WebElement row : participantRows()) {
            counts.add(readStepCount(row));
        }
        return counts;
    }

    public Map<String, Long> getStepCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            String name = row.findElement(By.className("participant-name")).getText().trim();
            counts.put(name, readStepCount(row));
        }
        return counts;
    }

    public long getStepCount(String name) {
        return readStepCount(findParticipantRow(name));
    }

    public String getStepCountText(String name) {
        return findParticipantRow(name).findElement(By.className("participant-steps")).getText();
    }

    public boolean hasTrophyAtRank(int position) {
        return !participantRows().get(position)
                .findElements(By.cssSelector("i.fa.fa-trophy")).isEmpty();
    }

    public String getTrophyColorAtRank(int position) {
        return participantRows().get(position)
                .findElement(By.cssSelector("i.fa.fa-trophy"))
                .getCssValue("color")
                .replace("rgba(", "rgb(")
                .replace(", 1)", ")");
    }

    public String formatStepTotal(long previousSteps, String addedSteps) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "return (Number(arguments[0]) + Number(arguments[1])).toLocaleString();",
                previousSteps, addedSteps);
    }

    public String getHeading() {
        return driver.findElement(By.tagName("h2")).getText().trim();
    }

    public boolean isHeadingDisplayed() {
        return driver.findElement(By.tagName("h2")).isDisplayed();
    }

    public int getAddStepsButtonCount() {
        return driver.findElements(By.id("addStepsBtn")).size();
    }

    public int getResetButtonCount() {
        return driver.findElements(By.id("resetBtn")).size();
    }

    public boolean isPageButtonDisplayed(String buttonId, int position) {
        return pageButtons(buttonId).get(position).isDisplayed();
    }

    public String getPageButtonText(String buttonId, int position) {
        return pageButtons(buttonId).get(position).getText().trim();
    }

    public void openAddStepsModal(int buttonPosition) {
        WebElement button = pageButton("addStepsBtn", buttonPosition);
        scrollToBottomButton(button, buttonPosition);
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    public boolean isAddStepsModalDisplayed() {
        return driver.findElement(By.id("addStepsModal")).isDisplayed();
    }

    public String getModalHeading() {
        return driver.findElement(By.id("addStepsModal"))
                .findElement(By.tagName("h3")).getText().trim();
    }

    public List<String> getParticipantOptions() {
        return new Select(driver.findElement(By.id("participant_select"))).getOptions().stream()
                .map(option -> option.getText().trim())
                .toList();
    }

    public String getStepsInputType() {
        return driver.findElement(By.id("steps_input")).getAttribute("type");
    }

    public boolean isStepsInputDisplayed() {
        return driver.findElement(By.id("steps_input")).isDisplayed();
    }

    public String getAddStepsSubmitButtonText() {
        return driver.findElement(By.id("modal_add_steps_button")).getText().trim();
    }

    public boolean isAddStepsSubmitButtonDisplayed() {
        return driver.findElement(By.id("modal_add_steps_button")).isDisplayed();
    }

    public boolean isModalCloseButtonDisplayed() {
        return modalCloseButton().isDisplayed();
    }

    public String getModalCloseButtonClass() {
        return modalCloseButton().getAttribute("class");
    }

    public String getModalCloseButtonText() {
        return modalCloseButton().getText().trim();
    }

    public void selectParticipant(String participant) {
        new Select(driver.findElement(By.id("participant_select")))
                .selectByVisibleText(participant);
    }

    public void enterSteps(String steps) {
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys(steps);
    }

    public void appendSteps(String steps) {
        driver.findElement(By.id("steps_input")).sendKeys(steps);
    }

    public String getStepsInputValue() {
        return driver.findElement(By.id("steps_input")).getAttribute("value");
    }

    public String submitStepsAndGetAlertText() {
        driver.findElement(By.id("modal_add_steps_button")).click();
        pendingAlert = wait.until(ExpectedConditions.alertIsPresent());
        return pendingAlert.getText();
    }

    public void acceptAlertAndWaitForModal() {
        pendingAlert.accept();
        pendingAlert = null;
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    public void submitValidSteps() {
        driver.findElement(By.id("modal_add_steps_button")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        waitForParticipants();
    }

    public void closeAddStepsModal() {
        modalCloseButton().click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    public void resetParticipantList(int buttonPosition) {
        WebElement previousFirstRow = participantRows().get(0);
        WebElement resetButton = pageButton("resetBtn", buttonPosition);
        scrollToBottomButton(resetButton, buttonPosition);
        wait.until(ExpectedConditions.elementToBeClickable(resetButton)).click();
        wait.until(ExpectedConditions.stalenessOf(previousFirstRow));
        waitForParticipants();
    }

    private List<WebElement> participantRows() {
        return driver.findElements(PARTICIPANT_ROWS);
    }

    private WebElement findParticipantRow(String name) {
        return wait.until(currentDriver -> {
            for (WebElement row : currentDriver.findElements(PARTICIPANT_ROWS)) {
                try {
                    String participantName = row
                            .findElement(By.className("participant-name"))
                            .getText()
                            .trim();
                    if (participantName.equals(name)) {
                        return row;
                    }
                } catch (StaleElementReferenceException ignored) {
                    // The list changed. Try again.
                }
            }
            return null;
        });
    }

    private long readStepCount(WebElement row) {
        String text = row.findElement(By.className("participant-steps")).getText();
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    private List<WebElement> pageButtons(String id) {
        return driver.findElements(By.id(id));
    }

    private WebElement pageButton(String id, int position) {
        return pageButtons(id).get(position);
    }

    private WebElement modalCloseButton() {
        return driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn[title='Close Modal']"));
    }

    private void scrollToBottomButton(WebElement button, int position) {
        if (position == 1) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", button);
        }
    }
}
