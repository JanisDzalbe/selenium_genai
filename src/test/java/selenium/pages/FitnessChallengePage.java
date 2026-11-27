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
import java.util.List;
import java.util.stream.Collectors;

public class FitnessChallengePage {
    private static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By participantRows = By.cssSelector("#participantsList li");
    private final By participantName = By.cssSelector(".participant-name");
    private final By participantStepCount = By.cssSelector(".participant-steps");
    private final By pageTitle = By.xpath("//h2[normalize-space()='Fitness Challenge']");
    private final By addStepsModal = By.id("addStepsModal");
    private final By modalTitle = By.xpath("//h3[normalize-space()='Add Steps to Participant']");
    private final By participantSelect = By.id("participant_select");
    private final By stepsInput = By.id("steps_input");
    private final By modalAddButton = By.id("modal_add_steps_button");
    private final By modalCloseButton = By.cssSelector("#addStepsModal .w3-closebtn");
    private final By trophyIcons = By.cssSelector("i.fa-trophy");

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public void open() {
        driver.get(URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
        wait.until(currentDriver -> participantRows().size() == 10);
    }

    public List<WebElement> participantRows() {
        return driver.findElements(participantRows);
    }

    public List<String> participantNames() {
        return participantRows().stream()
                .map(row -> row.findElement(participantName).getText().trim())
                .collect(Collectors.toList());
    }

    public List<Long> participantStepCounts() {
        return participantRows().stream()
                .map(row -> parseSteps(row.findElement(participantStepCount).getText()))
                .collect(Collectors.toList());
    }

    public long participantSteps(String name) {
        WebElement row = wait.until(currentDriver -> currentDriver.findElements(participantRows).stream()
                .filter(candidate -> candidate.findElement(participantName).getText().trim().equals(name))
                .findFirst()
                .orElse(null));
        return parseSteps(row.findElement(participantStepCount).getText());
    }

    public boolean buttonDisplayed(String text, int index) {
        return pageButton(text, index).isDisplayed();
    }

    public void openModalTop() {
        pageButton("Add Steps", 0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(addStepsModal));
    }

    public void openModalBottom() {
        WebElement bottomButton = pageButton("Add Steps", 1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", bottomButton);
        bottomButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(addStepsModal));
    }

    public void selectParticipant(String name) {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(participantSelect)))
                .selectByVisibleText(name);
    }

    public void enterSteps(String text) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(stepsInput));
        input.clear();
        input.sendKeys(text);
    }

    public void submitModal() {
        wait.until(ExpectedConditions.elementToBeClickable(modalAddButton)).click();
    }

    public void closeModal() {
        WebElement closeButton = wait.until(ExpectedConditions.visibilityOfElementLocated(modalCloseButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
    }

    public void resetTop() {
        pageButton("Reset List", 0).click();
    }

    public void resetBottom() {
        WebElement bottomButton = pageButton("Reset List", 1);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", bottomButton);
        bottomButton.click();
    }

    public void addSteps(String name, String steps) {
        openModalTop();
        selectParticipant(name);
        enterSteps(steps);
        submitModal();
    }

    public String title() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText();
    }

    public WebElement modalTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitle));
    }

    public WebElement modalParticipantDropdown() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(participantSelect));
    }

    public WebElement modalStepsInput() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(stepsInput));
    }

    public WebElement modalSubmitButton() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalAddButton));
    }

    public WebElement modalCloseButton() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalCloseButton));
    }

    public List<String> participantOptions() {
        return new Select(modalParticipantDropdown()).getOptions().stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public String selectedParticipantOptionText() {
        return new Select(modalParticipantDropdown()).getFirstSelectedOption().getText();
    }

    public List<String> trophyColors() {
        return participantRows().stream()
                .flatMap(row -> row.findElements(trophyIcons).stream())
                .map(trophy -> trophy.getCssValue("color"))
                .collect(Collectors.toList());
    }

    public int trophyCountAtRank(int rank) {
        return participantRows().get(rank - 1).findElements(trophyIcons).size();
    }

    public void waitForModalClosed() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addStepsModal));
    }

    public void waitForParticipantSteps(String name, long expectedSteps) {
        wait.until(currentDriver -> participantSteps(name) == expectedSteps);
    }

    public void waitForDefaultParticipantCount() {
        wait.until(currentDriver -> participantRows().size() == 10);
    }

    public Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    public boolean modalDisplayed() {
        return driver.findElement(addStepsModal).isDisplayed();
    }

    private WebElement pageButton(String text, int index) {
        By buttons = By.xpath("//button[normalize-space()='" + text + "']");
        return wait.until(currentDriver -> {
            List<WebElement> matchingButtons = currentDriver.findElements(buttons);
            return matchingButtons.size() > index ? matchingButtons.get(index) : null;
        });
    }

    private long parseSteps(String text) {
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }
}
