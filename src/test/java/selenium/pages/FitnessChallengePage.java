package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.util.List;

public class FitnessChallengePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators matching actual HTML
    public By pageTitle = By.tagName("h2");
    public By participantsList = By.cssSelector("#participantsList li");
    public By modal = By.id("addStepsModal");
    public By modalTitle = By.cssSelector("#addStepsModal h3");
    public By participantDropdown = By.id("participant_select");
    public By stepsInput = By.id("steps_input");
    public By addStepsSubmitButton = By.id("modal_add_steps_button");
    public By modalCloseButton = By.className("w3-closebtn");

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openPage() {
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        // On first visit, JS reloads the page to initialize localStorage; wait for participants
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(participantsList));
    }

    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText();
    }

    public int getParticipantCount() {
        return getParticipants().size();
    }

    public List<WebElement> getParticipants() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(participantsList));
    }

    public String getParticipantName(WebElement participant) {
        return participant.findElement(By.className("participant-name")).getText();
    }

    public long getParticipantSteps(WebElement participant) {
        String stepsText = participant.findElement(By.className("participant-steps")).getText();
        stepsText = stepsText.replace(",", "").replace(" steps", "").trim();
        return Long.parseLong(stepsText);
    }

    public boolean hasTrophy(WebElement participant, String type) {
        var participants = getParticipants();
        int index = participants.indexOf(participant);
        if (index == 0 && "gold".equals(type)) return true;
        if (index == 1 && "silver".equals(type)) return true;
        if (index == 2 && "bronze".equals(type)) return true;
        return false;
    }

    // Both top buttons share id="addStepsBtn"; get top by index 0, bottom by index 1
    public void clickAddStepsTop() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
    }

    public void clickAddStepsBottom() {
        driver.findElements(By.id("addStepsBtn")).get(1).click();
    }

    public void clickResetTop() {
        if (isModalVisible()) {
            closeModal();
        }
        driver.findElements(By.id("resetBtn")).get(0).click();
        // Reset triggers location.reload(); wait for participants to re-appear
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(participantsList));
    }

    public void clickResetBottom() {
        if (isModalVisible()) {
            closeModal();
        }
        driver.findElements(By.id("resetBtn")).get(1).click();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(participantsList));
    }

    public boolean isModalVisible() {
        List<WebElement> elements = driver.findElements(modal);
        if (elements.isEmpty()) return false;
        return elements.get(0).isDisplayed();
    }

    public String getModalTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitle)).getText();
    }

    public void selectParticipant(String name) {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(participantDropdown)));
        select.selectByVisibleText(name);
    }

    public void enterSteps(String steps) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(stepsInput));
        input.clear();
        input.sendKeys(steps);
    }

    public void submitAddSteps() {
        wait.until(ExpectedConditions.elementToBeClickable(addStepsSubmitButton)).click();
    }

    public void closeModal() {
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(modalCloseButton));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
    }

    public String getAlertText() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    public WebDriver getDriver() {
        return driver;
    }

    public WebElement getParticipantByName(String name) {
        for (WebElement p : getParticipants()) {
            if (getParticipantName(p).equals(name)) {
                return p;
            }
        }
        return null;
    }
}
