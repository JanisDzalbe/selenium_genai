package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FitnessChallengePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By pageTitle = By.tagName("h2");
    private By participantsList = By.id("participantsList");
    private By participantItems = By.cssSelector("#participantsList li");
    private By addStepsButtons = By.id("addStepsBtn");
    private By resetButtons = By.id("resetBtn");

    // Modal locators
    private By modal = By.id("addStepsModal");
    private By modalTitle = By.tagName("h3");
    private By participantDropdown = By.id("participant_select");
    private By stepsInput = By.id("steps_input");
    private By addStepsSubmit = By.id("modal_add_steps_button");
    private By closeModal = By.cssSelector(".w3-closebtn");

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Page actions
    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }

    public int getParticipantCount() {
        return driver.findElements(participantItems).size();
    }

    public List<Map<String, String>> getParticipants() {
        List<WebElement> participantElements = driver.findElements(participantItems);
        List<Map<String, String>> participants = new java.util.ArrayList<>();
        for (WebElement element : participantElements) {
            String name = element.findElement(By.className("participant-name")).getText();
            String steps = element.findElement(By.className("participant-steps")).getText();
            Map<String, String> participant = new HashMap<>();
            participant.put("name", name);
            participant.put("steps", steps);
            participants.add(participant);
        }
        return participants;
    }

    public void clickAddStepsTop() {
        List<WebElement> buttons = driver.findElements(addStepsButtons);
        if (buttons.size() >= 1) buttons.get(0).click();
    }

    public void clickAddStepsBottom() {
        List<WebElement> buttons = driver.findElements(addStepsButtons);
        if (buttons.size() >= 2) buttons.get(1).click();
    }

    public void clickResetTop() {
        List<WebElement> buttons = driver.findElements(resetButtons);
        if (buttons.size() >= 1) buttons.get(0).click();
    }

    public void clickResetBottom() {
        List<WebElement> buttons = driver.findElements(resetButtons);
        if (buttons.size() >= 2) buttons.get(1).click();
    }

    // Modal actions
    public boolean isModalDisplayed() {
        try {
            return driver.findElement(modal).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getModalTitle() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitle));
        return driver.findElement(modalTitle).getText();
    }

    public void selectParticipant(String name) {
        wait.until(ExpectedConditions.elementToBeClickable(participantDropdown));
        Select select = new Select(driver.findElement(participantDropdown));
        select.selectByVisibleText(name);
    }

    public void enterSteps(String steps) {
        WebElement input = driver.findElement(stepsInput);
        input.clear();
        input.sendKeys(steps);
    }

    public void clickAddStepsInModal() {
        driver.findElement(addStepsSubmit).click();
    }

    public void clickCloseModal() {
        driver.findElement(closeModal).click();
    }

    public List<String> getDropdownOptions() {
        Select select = new Select(driver.findElement(participantDropdown));
        return select.getOptions().stream().map(WebElement::getText).toList();
    }

    public String getDefaultDropdownText() {
        Select select = new Select(driver.findElement(participantDropdown));
        return select.getFirstSelectedOption().getText();
    }

    // Trophy actions
    public String getTrophyColor(int place) {
        List<WebElement> participantElements = driver.findElements(participantItems);
        if (place > participantElements.size()) return null;
        try {
            WebElement trophy = participantElements.get(place - 1).findElement(By.className("fa-trophy"));
            return trophy.getCssValue("color");
        } catch (Exception e) {
            return null; // No trophy
        }
    }

    // Alert handling
    public String getAlertText() {
        wait.until(ExpectedConditions.alertIsPresent());
        return driver.switchTo().alert().getText();
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    // Wait for page load or refresh
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
    }

    // Button display actions
    public boolean isAddStepsTopDisplayed() {
        List<WebElement> buttons = driver.findElements(addStepsButtons);
        return buttons.size() >= 1 && buttons.get(0).isDisplayed();
    }

    public boolean isAddStepsBottomDisplayed() {
        List<WebElement> buttons = driver.findElements(addStepsButtons);
        return buttons.size() >= 2 && buttons.get(1).isDisplayed();
    }

    public boolean isResetTopDisplayed() {
        List<WebElement> buttons = driver.findElements(resetButtons);
        return buttons.size() >= 1 && buttons.get(0).isDisplayed();
    }

    public boolean isResetBottomDisplayed() {
        List<WebElement> buttons = driver.findElements(resetButtons);
        return buttons.size() >= 2 && buttons.get(1).isDisplayed();
    }
}
