package selenium.pages;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/** Page object for the fitness challenge leaderboard and steps modal. */
public class FitnessChallengePage {
    private final WebDriver driver;

    @FindBy(css = "h2")
    private WebElement heading;

    @FindBy(css = "#participantsList li")
    private List<WebElement> participantRows;

    @FindBy(id = "addStepsBtn")
    private List<WebElement> addStepsButtons;

    @FindBy(id = "resetBtn")
    private List<WebElement> resetButtons;

    @FindBy(id = "addStepsModal")
    private WebElement modal;

    @FindBy(css = "#addStepsModal h3")
    private WebElement modalHeading;

    @FindBy(id = "participant_select")
    private WebElement participantSelect;

    @FindBy(id = "steps_input")
    private WebElement stepsInput;

    @FindBy(id = "modal_add_steps_button")
    private WebElement submitStepsButton;

    @FindBy(css = "#addStepsModal .w3-closebtn")
    private WebElement closeModalButton;

    /** Initializes this page's elements for the supplied browser. */
    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /** Waits for the leaderboard to be present. */
    public void waitForParticipants() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("participantsList")));
    }

    /** Returns the page heading. */
    public String getHeading() {
        return heading.getText();
    }

    /** Returns all participant rows. */
    public List<WebElement> getParticipantRows() {
        return participantRows;
    }

    /** Returns all participant names in leaderboard order. */
    public List<String> getParticipantNames() {
        List<String> names = new ArrayList<>();
        for (WebElement row : participantRows) {
            names.add(row.findElement(By.className("participant-name")).getText());
        }
        return names;
    }

    /** Returns all displayed step totals in leaderboard order. */
    public List<String> getParticipantSteps() {
        List<String> steps = new ArrayList<>();
        for (WebElement row : participantRows) {
            steps.add(row.findElement(By.className("participant-steps")).getText());
        }
        return steps;
    }

    /** Returns parsed numeric step totals in leaderboard order. */
    public List<Integer> getNumericSteps() {
        List<Integer> steps = new ArrayList<>();
        for (String text : getParticipantSteps()) {
            steps.add(Integer.parseInt(text.replace(" steps", "").replace(",", "")));
        }
        return steps;
    }

    /** Returns the step text for the named participant. */
    public String getParticipantStepsText(String name) {
        for (WebElement row : participantRows) {
            if (row.findElement(By.className("participant-name")).getText().equals(name)) {
                return row.findElement(By.className("participant-steps")).getText();
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    /** Returns the named participant's numeric step total. */
    public int findStepCount(String name) {
        return Integer.parseInt(getParticipantStepsText(name).replace(" steps", "").replace(",", ""));
    }

    /** Returns the color of a participant's medal icon, if present. */
    public String getMedalColor(WebElement row) {
        return row.findElement(By.cssSelector("i")).getCssValue("color");
    }

    /** Returns a participant row's medal icons. */
    public List<WebElement> getMedals(WebElement row) {
        return row.findElements(By.cssSelector("i"));
    }

    /** Returns the add-steps buttons. */
    public List<WebElement> getAddStepsButtons() {
        return addStepsButtons;
    }

    /** Returns the reset buttons. */
    public List<WebElement> getResetButtons() {
        return resetButtons;
    }

    /** Clicks the add-steps button at the given position. */
    public void clickAddStepsButton(int index) {
        addStepsButtons.get(index).click();
    }

    /** Clicks the reset button at the given position. */
    public void clickResetButton(int index) {
        resetButtons.get(index).click();
    }

    /** Waits until the add-steps modal is visible. */
    public void waitForModalOpen() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOf(modal));
    }

    /** Waits until the add-steps modal is hidden and returns its display value. */
    public String waitForModalClosed() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOf(modal));
        return modal.getCssValue("display");
    }

    /** Returns the modal display value. */
    public String getModalDisplay() {
        return modal.getCssValue("display");
    }

    /** Returns the modal heading. */
    public String getModalHeading() {
        return modalHeading.getText();
    }

    /** Reports whether the participant selector is displayed. */
    public boolean isParticipantSelectDisplayed() {
        return participantSelect.isDisplayed();
    }

    /** Reports whether the steps input is displayed. */
    public boolean isStepsInputDisplayed() {
        return stepsInput.isDisplayed();
    }

    /** Reports whether the submit button is displayed. */
    public boolean isSubmitStepsButtonDisplayed() {
        return submitStepsButton.isDisplayed();
    }

    /** Reports whether the modal close button is displayed. */
    public boolean isCloseModalButtonDisplayed() {
        return closeModalButton.isDisplayed();
    }

    /** Returns the number of participant choices, including the placeholder. */
    public int getParticipantOptionCount() {
        return new Select(participantSelect).getOptions().size();
    }

    /** Returns the currently selected participant option. */
    public String getSelectedParticipantOption() {
        return new Select(participantSelect).getFirstSelectedOption().getText();
    }

    /** Selects a participant by visible name. */
    public void selectParticipant(String name) {
        new Select(participantSelect).selectByVisibleText(name);
    }

    /** Enters a step count. */
    public void enterSteps(String steps) {
        stepsInput.sendKeys(steps);
    }

    /** Returns the steps input value. */
    public String getStepsInputValue() {
        return stepsInput.getAttribute("value");
    }

    /** Clicks the modal submit button. */
    public void submitSteps() {
        submitStepsButton.click();
    }

    /** Clicks the modal close button. */
    public void closeModal() {
        closeModalButton.click();
    }

    /** Waits for the selected participant and input value to match. */
    public void waitForSelectedParticipantAndSteps(String participant, String steps) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> participant.equals(getSelectedParticipantOption())
                        && steps.equals(getStepsInputValue()));
    }

    /** Waits for the expected alert, verifies its text, and accepts it. */
    public void assertAlertAndAccept(String expected) {
        Alert alert = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.alertIsPresent());
        if (!expected.equals(alert.getText())) {
            throw new AssertionError("Expected alert '" + expected + "' but was '" + alert.getText() + "'");
        }
        alert.accept();
    }
}
