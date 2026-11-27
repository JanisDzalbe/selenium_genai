package selenium.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigInteger;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Page Object for the Fitness Challenge page.
 */
public class FitnessChallengePage {
    public static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "h2")
    private WebElement pageHeading;

    @FindBy(id = "participantsList")
    private WebElement participantsList;

    @FindBy(id = "participant_select")
    private WebElement participantSelect;

    @FindBy(id = "steps_input")
    private WebElement stepsInput;

    @FindBy(id = "modal_add_steps_button")
    private WebElement modalSubmitButton;

    @FindBy(id = "addStepsModal")
    private WebElement modal;

    @FindBy(css = "#addStepsModal .w3-closebtn")
    private WebElement modalCloseButton;

    // Constructor initializes all @FindBy elements through PageFactory.
    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get(URL);
        waitForParticipants(10);
    }

    public void resetList() {
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge();");
        waitForParticipants(10);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getHeading() {
        return pageHeading.getText();
    }

    public List<WebElement> getParticipantElements() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    public List<WebElement> waitForParticipants(int count) {
        return wait.until(d -> {
            List<WebElement> participants =
                    d.findElements(By.cssSelector("#participantsList li"));
            return participants.size() == count ? participants : null;
        });
    }

    public Map<String, String> getParticipantData() {
        Map<String, String> result = new LinkedHashMap<>();
        for (WebElement participant : waitForParticipants(10)) {
            String name = participant.findElement(
                    By.cssSelector(".participant-name")).getText();
            String steps = normalizeSteps(participant.findElement(
                    By.cssSelector(".participant-steps")).getText());
            result.put(name, steps);
        }
        return result;
    }

    public String getParticipantSteps(String name) {
        for (WebElement participant : getParticipantElements()) {
            if (name.equals(participant.findElement(
                    By.cssSelector(".participant-name")).getText())) {
                return normalizeSteps(participant.findElement(
                        By.cssSelector(".participant-steps")).getText());
            }
        }
        throw new IllegalArgumentException("Participant not found: " + name);
    }

    public int getParticipantStepCount(String name) {
        return parseRegularSteps(getParticipantSteps(name));
    }

    public BigInteger getParticipantStepCountBigInteger(String name) {
        return new BigInteger(getParticipantSteps(name).replace(",", ""));
    }

    public List<WebElement> getAddStepsButtons() {
        return driver.findElements(By.id("addStepsBtn"));
    }

    public List<WebElement> getResetButtons() {
        return driver.findElements(By.id("resetBtn"));
    }

    public void openModal(boolean bottom) {
        List<WebElement> buttons = getAddStepsButtons();
        WebElement button = bottom ? buttons.get(1) : buttons.get(0);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", button);
        button.click();
        wait.until(d -> "block".equals(modal.getCssValue("display")));
    }

    public void closeModalWithCloseButton() {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", modalCloseButton);
        waitForModalClosed();
    }

    public void closeModalByJavaScript() {
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('addStepsModal').style.display='none'");
        waitForModalClosed();
    }

    public boolean isModalOpen() {
        return "block".equals(modal.getCssValue("display"));
    }

    public void waitForModalClosed() {
        wait.until(d -> "none".equals(modal.getCssValue("display")));
    }

    public void assertModalElementsPresent() {
        if (!isModalOpen()) {
            throw new AssertionError("Add Steps modal is not open");
        }
    }

    public String getModalTitle() {
        return driver.findElement(By.cssSelector("#addStepsModal h3")).getText();
    }

    public boolean isParticipantDropdownDisplayed() {
        return participantSelect.isDisplayed();
    }

    public boolean isStepsInputDisplayed() {
        return stepsInput.isDisplayed();
    }

    public boolean isModalSubmitButtonDisplayed() {
        return modalSubmitButton.isDisplayed();
    }

    public boolean isModalCloseButtonPresent() {
        return !driver.findElements(
                By.cssSelector("#addStepsModal .w3-closebtn")).isEmpty();
    }

    public String getModalSubmitButtonText() {
        return modalSubmitButton.getText();
    }

    public String getDefaultDropdownText() {
        return new Select(participantSelect).getFirstSelectedOption().getText();
    }

    public int getParticipantDropdownOptionCount() {
        return new Select(participantSelect).getOptions().size();
    }

    public void selectParticipant(String name) {
        new Select(participantSelect).selectByVisibleText(name);
    }

    public void enterSteps(String steps) {
        stepsInput.clear();
        stepsInput.sendKeys(steps);
    }

    public String getStepsInputValue() {
        return stepsInput.getAttribute("value");
    }

    public void submitSteps() {
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('modal_add_steps_button').click()");
    }

    public void acceptAlert(String expectedText) {
        Alert alert = wait.until(d -> d.switchTo().alert());
        if (!expectedText.equals(alert.getText())) {
            throw new AssertionError("Expected alert '" + expectedText
                    + "' but found '" + alert.getText() + "'");
        }
        alert.accept();
    }

    public void resetViaTopButton() {
        getResetButtons().get(0).click();
        waitForParticipants(10);
    }

    public void resetViaBottomButton() {
        WebElement bottomReset = getResetButtons().get(1);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", bottomReset);
        bottomReset.click();
        waitForParticipants(10);
    }

    public String getFirstParticipantName() {
        return getParticipantElements().get(0).findElement(
                By.cssSelector(".participant-name")).getText();
    }

    public String getTrophyColor(int participantIndex) {
        WebElement trophy = getParticipantElements().get(participantIndex)
                .findElement(By.cssSelector(".fa-trophy"));
        return trophy.getCssValue("color");
    }

    public int getTrophyCount(int participantIndex) {
        return getParticipantElements().get(participantIndex)
                .findElements(By.cssSelector(".fa-trophy")).size();
    }

    public boolean isParticipantRankingDescending() {
        int previous = Integer.MAX_VALUE;
        for (WebElement participant : getParticipantElements()) {
            int steps = parseRegularSteps(normalizeSteps(participant.findElement(
                    By.cssSelector(".participant-steps")).getText()));
            if (steps > previous) {
                return false;
            }
            previous = steps;
        }
        return true;
    }

    public boolean areMainAddAndResetButtonsVisible() {
        List<WebElement> addButtons = getAddStepsButtons();
        List<WebElement> resetButtons = getResetButtons();
        if (addButtons.size() != 2 || resetButtons.size() != 2) {
            return false;
        }
        for (WebElement button : addButtons) {
            if (!button.isDisplayed()) {
                return false;
            }
        }
        for (WebElement button : resetButtons) {
            if (!button.isDisplayed()) {
                return false;
            }
        }
        return true;
    }

    public boolean isParticipantsListVisible() {
        return participantsList.isDisplayed();
    }

    private String normalizeSteps(String text) {
        return text.replace(" steps", "").replaceAll("\\s+", "").trim();
    }

    private int parseRegularSteps(String text) {
        return Integer.parseInt(text.replace(",", ""));
    }

    public static String formatSteps(int steps) {
        return String.format("%,d", steps);
    }

    public static String formatSteps(BigInteger steps) {
        return String.format("%,d", steps);
    }
}
