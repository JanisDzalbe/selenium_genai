package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

/**
 * Page Object Model for the Fitness Challenge page
 * Encapsulates all locators and page interactions
 */
public class FitnessChallengePage {
    private  WebDriver driver;

    // Locators
    private By participantsListLocator = By.cssSelector("#participantsList li");
    private By addStepsButtonsLocator = By.id("addStepsBtn");
    private By resetButtonsLocator = By.id("resetBtn");
    private By participantNameLocator = By.cssSelector(".participant-name");
    private By participantStepsLocator = By.cssSelector(".participant-steps");
    
    // Modal Locators
    private By addStepsModalLocator = By.id("addStepsModal");
    private By modalTitleLocator = By.cssSelector("#addStepsModal h3");
    private By participantDropdownLocator = By.id("participant_select");
    private By stepsInputLocator = By.id("steps_input");
    private By modalSubmitButtonLocator = By.id("modal_add_steps_button");
    private By modalCloseButtonLocator = By.cssSelector("#addStepsModal .w3-closebtn");

    /**
     * Constructor to initialize the page with a WebDriver instance
     */
    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Get the page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Get all participant list items
     */
    public List<WebElement> getParticipants() {
        return driver.findElements(participantsListLocator);
    }

    /**
     * Get all "Add Steps" buttons (top and bottom)
     */
    public List<WebElement> getAddStepsButtons() {
        return driver.findElements(addStepsButtonsLocator);
    }

    /**
     * Get all "Reset List" buttons (top and bottom)
     */
    public List<WebElement> getResetButtons() {
        return driver.findElements(resetButtonsLocator);
    }

    /**
     * Get participant name from a participant element
     */
    public String getParticipantName(WebElement participantElement) {
        return participantElement.findElement(participantNameLocator).getText();
    }

    /**
     * Get participant step count from a participant element
     */
    public String getParticipantSteps(WebElement participantElement) {
        return participantElement.findElement(participantStepsLocator).getText();
    }

    /**
     * Check if a button is displayed
     */
    public boolean isButtonDisplayed(WebElement button) {
        return button.isDisplayed();
    }

    /**
     * Check if a trophy icon exists in a participant element
     */
    public boolean hasTrophyIcon(WebElement participantElement) {
        try {
            participantElement.findElement(By.cssSelector("i.fa-trophy"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Get the color of the trophy icon from a participant element
     * Returns the color value (e.g., "gold", "silver", "rgb(205, 127, 50)")
     */
    public String getTrophyIconColor(WebElement participantElement) {
        WebElement trophyIcon = participantElement.findElement(By.cssSelector("i.fa-trophy"));
        return trophyIcon.getCssValue("color");
    }

    /**
     * Click the "Add Steps" button at the top of the page to open the modal
     */
    public void clickTopAddStepsButton() {
        List<WebElement> buttons = getAddStepsButtons();
        // Get the first (top) button
        buttons.get(0).click();
    }

    /**
     * Click the "Add Steps" button at the bottom of the page to open the modal
     */
    public void clickBottomAddStepsButton() {
        List<WebElement> buttons = getAddStepsButtons();
        // Get the second (bottom) button
        buttons.get(1).click();
    }

    /**
     * Scroll to the bottom of the page
     */
    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Check if the "Add Steps" modal is displayed
     */
    public boolean isModalDisplayed() {
        try {
            WebElement modal = driver.findElement(addStepsModalLocator);
            return modal.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Get the modal title text
     */
    public String getModalTitle() {
        return driver.findElement(modalTitleLocator).getText();
    }

    /**
     * Check if the participant dropdown exists in the modal
     */
    public boolean hasParticipantDropdown() {
        try {
            driver.findElement(participantDropdownLocator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if the steps input field exists in the modal
     */
    public boolean hasStepsInputField() {
        try {
            driver.findElement(stepsInputLocator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if the modal submit button exists
     */
    public boolean hasModalSubmitButton() {
        try {
            driver.findElement(modalSubmitButtonLocator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if the modal close button exists
     */
    public boolean hasModalCloseButton() {
        try {
            driver.findElement(modalCloseButtonLocator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Get all options in the participant dropdown
     */
    public List<WebElement> getDropdownOptions() {
        WebElement dropdown = driver.findElement(participantDropdownLocator);
        return dropdown.findElements(By.tagName("option"));
    }

    /**
     * Get the default/selected option text in the dropdown
     */
    public String getDefaultDropdownText() {
        WebElement dropdown = driver.findElement(participantDropdownLocator);
        WebElement selectedOption = dropdown.findElement(By.cssSelector("option[selected]"));
        return selectedOption.getText();
    }

    /**
     * Click the modal close button (×) in the top-right corner
     */
    public void clickModalCloseButton() {
        WebElement closeButton = driver.findElement(modalCloseButtonLocator);
        closeButton.click();
    }
}

