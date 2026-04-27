package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FitnessChallengePageObject {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By titleLocator = By.tagName("h2");
    private By participantsListLocator = By.id("participantsList");
    private By participantItemLocator = By.cssSelector("#participantsList li");
    private By participantNameLocator = By.className("participant-name");
    private By participantStepsLocator = By.className("participant-steps");
    private By addStepsButtonTopLocator = By.xpath("(//button[text()='Add Steps'])[1]");
    private By addStepsButtonBottomLocator = By.xpath("(//button[text()='Add Steps'])[2]");
    private By resetButtonTopLocator = By.xpath("(//button[text()='Reset List'])[1]");
    private By resetButtonBottomLocator = By.xpath("(//button[text()='Reset List'])[2]");
    private By modalLocator = By.id("addStepsModal");
    private By modalTitleLocator = By.xpath("//h3[text()='Add Steps to Participant']");
    private By participantSelectLocator = By.id("participant_select");
    private By stepsInputLocator = By.id("steps_input");
    private By modalAddStepsButtonLocator = By.id("modal_add_steps_button");
    private By modalCloseButtonLocator = By.cssSelector(".w3-closebtn");
    private By trophyLocator = By.className("fa-trophy");

    public FitnessChallengePageObject(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Navigation
    public void navigateTo(String url) {
        driver.get(url);
    }

    // Page Load
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(titleLocator));
    }

    // Title verification
    public String getPageTitle() {
        return driver.findElement(titleLocator).getText();
    }

    // Participants List
    public int getParticipantCount() {
        return driver.findElements(participantItemLocator).size();
    }

    public List<Map<String, String>> getAllParticipants() {
        List<Map<String, String>> participants = new ArrayList<>();
        List<WebElement> participantElements = driver.findElements(participantItemLocator);

        for (WebElement element : participantElements) {
            String name = element.findElement(participantNameLocator).getText();
            String steps = element.findElement(participantStepsLocator).getText().replace(" steps", "");
            participants.add(Map.of("Name", name, "Steps", steps));
        }
        return participants;
    }

    public String getParticipantName(int index) {
        List<WebElement> participants = driver.findElements(participantItemLocator);
        return participants.get(index).findElement(participantNameLocator).getText();
    }

    public String getParticipantSteps(int index) {
        List<WebElement> participants = driver.findElements(participantItemLocator);
        return participants.get(index).findElement(participantStepsLocator).getText();
    }

    // Button visibility
    public boolean isAddStepsButtonTopVisible() {
        return driver.findElement(addStepsButtonTopLocator).isDisplayed();
    }

    public boolean isAddStepsButtonBottomVisible() {
        return driver.findElement(addStepsButtonBottomLocator).isDisplayed();
    }

    public boolean isResetButtonTopVisible() {
        return driver.findElement(resetButtonTopLocator).isDisplayed();
    }

    public boolean isResetButtonBottomVisible() {
        return driver.findElement(resetButtonBottomLocator).isDisplayed();
    }

    // Modal interaction
    public void clickAddStepsButtonTop() {
        driver.findElement(addStepsButtonTopLocator).click();
    }

    public void clickAddStepsButtonBottom() {
        driver.findElement(addStepsButtonBottomLocator).click();
    }

    public void clickResetButtonTop() {
        driver.findElement(resetButtonTopLocator).click();
    }

    public void clickResetButtonBottom() {
        driver.findElement(resetButtonBottomLocator).click();
    }

    public void waitForModalToAppear() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalLocator));
    }

    public void waitForModalToDisappear() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalLocator));
    }

    public boolean isModalDisplayed() {
        try {
            return driver.findElement(modalLocator).isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public String getModalTitle() {
        return driver.findElement(modalTitleLocator).getText();
    }

    public boolean isModalTitleVisible() {
        return driver.findElement(modalTitleLocator).isDisplayed();
    }

    // Modal form elements
    public void selectParticipantFromDropdown(String participantName) {
        Select select = new Select(driver.findElement(participantSelectLocator));
        select.selectByVisibleText(participantName);
    }

    public void enterSteps(String steps) {
        WebElement input = driver.findElement(stepsInputLocator);
        input.clear();
        input.sendKeys(steps);
    }

    public void clickModalAddStepsButton() {
        driver.findElement(modalAddStepsButtonLocator).click();
    }

    public void closeModal() {
        driver.findElement(modalCloseButtonLocator).click();
    }

    public void selectFirstParticipant() {
        Select select = new Select(driver.findElement(participantSelectLocator));
        select.selectByIndex(1);
    }

    public String getSelectedParticipantDropdownText() {
        Select select = new Select(driver.findElement(participantSelectLocator));
        return select.getFirstSelectedOption().getText();
    }

    public int getParticipantDropdownOptionsCount() {
        Select select = new Select(driver.findElement(participantSelectLocator));
        return select.getOptions().size();
    }

    // Trophy verification
    public boolean hasGoldTrophy(int index) {
        List<WebElement> participants = driver.findElements(participantItemLocator);
        try {
            WebElement trophy = participants.get(index).findElement(trophyLocator);
            return "gold".equals(trophy.getCssValue("color"));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean hasSilverTrophy(int index) {
        List<WebElement> participants = driver.findElements(participantItemLocator);
        try {
            WebElement trophy = participants.get(index).findElement(trophyLocator);
            return "silver".equals(trophy.getCssValue("color"));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean hasBronzeTrophy(int index) {
        List<WebElement> participants = driver.findElements(participantItemLocator);
        try {
            WebElement trophy = participants.get(index).findElement(trophyLocator);
            return "#cd7f32".equals(trophy.getCssValue("color"));
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean hasTrophy(int index) {
        List<WebElement> participants = driver.findElements(participantItemLocator);
        try {
            participants.get(index).findElement(trophyLocator);
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    // Scroll
    public void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    // Wait for page reload
    public void waitForPageReload() {
        wait.until(ExpectedConditions.stalenessOf(driver.findElement(participantsListLocator)));
        wait.until(ExpectedConditions.visibilityOfElementLocated(participantsListLocator));
    }

    // Utility: Get steps as integer
    public int getParticipantStepsAsInt(int index) {
        String stepsText = getParticipantSteps(index)
                .replace(",", "")
                .replace(" steps", "");
        return Integer.parseInt(stepsText);
    }
}