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
    private By pageTitle = By.tagName("h1");
    private By participantsList = By.id("participantsList");
    private By participantDivs = By.cssSelector("#participantsList > div");
    private By addStepsTop = By.id("addStepsBtnTop");
    private By addStepsBottom = By.id("addStepsBtnBottom");
    private By resetTop = By.id("resetBtnTop");
    private By resetBottom = By.id("resetBtnBottom");

    // Modal locators
    private By modal = By.className("modal");
    private By modalTitle = By.cssSelector(".modal-title");
    private By participantDropdown = By.id("participantSelect");
    private By stepsInput = By.id("stepsInput");
    private By addStepsSubmit = By.cssSelector(".modal .btn-primary");
    private By closeModal = By.cssSelector(".modal .close");

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Page actions
    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }

    public int getParticipantCount() {
        return driver.findElements(participantDivs).size();
    }

    public List<Map<String, String>> getParticipants() {
        List<WebElement> participantElements = driver.findElements(participantDivs);
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
        driver.findElement(addStepsTop).click();
    }

    public void clickAddStepsBottom() {
        driver.findElement(addStepsBottom).click();
    }

    public void clickResetTop() {
        driver.findElement(resetTop).click();
    }

    public void clickResetBottom() {
        driver.findElement(resetBottom).click();
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
        List<WebElement> participantElements = driver.findElements(participantDivs);
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
        try {
            return driver.findElement(addStepsTop).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAddStepsBottomDisplayed() {
        try {
            return driver.findElement(addStepsBottom).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isResetTopDisplayed() {
        try {
            return driver.findElement(resetTop).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isResetBottomDisplayed() {
        try {
            return driver.findElement(resetBottom).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
