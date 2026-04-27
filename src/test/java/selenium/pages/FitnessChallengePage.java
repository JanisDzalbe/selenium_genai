package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FitnessChallengePage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Page elements
    private By pageTitle = By.tagName("h2");
    private By participantsList = By.id("participantsList");
    private By participantItems = By.cssSelector("#participantsList li");
    private By addStepsButtons = By.id("addStepsBtn");
    private By resetButtons = By.id("resetBtn");

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Navigation
    public void navigateToPage(String url) {
        driver.get(url);
    }

    // Page verification methods
    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }

    public int getParticipantCount() {
        return driver.findElements(participantItems).size();
    }

    public List<Map<String, String>> getAllParticipants() {
        List<Map<String, String>> participants = new ArrayList<>();
        List<WebElement> participantElements = driver.findElements(participantItems);

        for (WebElement participant : participantElements) {
            Map<String, String> participantData = new HashMap<>();
            participantData.put("name", participant.findElement(By.className("participant-name")).getText());
            participantData.put("steps", participant.findElement(By.className("participant-steps")).getText());
            participants.add(participantData);
        }

        return participants;
    }

    public Map<String, Integer> getParticipantStepsMap() {
        Map<String, Integer> stepsMap = new HashMap<>();
        List<WebElement> participantElements = driver.findElements(participantItems);

        for (WebElement participant : participantElements) {
            String name = participant.findElement(By.className("participant-name")).getText();
            String stepsText = participant.findElement(By.className("participant-steps")).getText();
            int steps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));
            stepsMap.put(name, steps);
        }

        return stepsMap;
    }

    public boolean areParticipantsInDescendingOrder() {
        List<WebElement> participantElements = driver.findElements(participantItems);
        int previousSteps = Integer.MAX_VALUE;

        for (WebElement participant : participantElements) {
            String stepsText = participant.findElement(By.className("participant-steps")).getText();
            int currentSteps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));

            if (currentSteps > previousSteps) {
                return false;
            }
            previousSteps = currentSteps;
        }

        return true;
    }

    public boolean hasTrophyIcon(int position, String expectedColor) {
        List<WebElement> participantElements = driver.findElements(participantItems);
        if (position >= participantElements.size()) {
            return false;
        }

        WebElement participant = participantElements.get(position);
        List<WebElement> trophyIcons = participant.findElements(By.className("fa-trophy"));

        if (trophyIcons.isEmpty()) {
            return false;
        }

        WebElement trophyIcon = trophyIcons.get(0);
        String style = trophyIcon.getAttribute("style");

        // Handle different color representations and null style
        if (style == null) {
            return false;
        }

        // Handle different color representations
        switch (expectedColor) {
            case "gold":
                return style.contains("color:gold");
            case "silver":
                return style.contains("color:silver");
            case "bronze":
                return style.contains("color:#cd7f32") || style.contains("cd7f32");
            default:
                return false;
        }
    }

    // Button interactions
    public void clickAddStepsButton(int index) {
        List<WebElement> buttons = driver.findElements(addStepsButtons);
        buttons.get(index).click();
    }

    public void clickResetButton(int index) {
        List<WebElement> buttons = driver.findElements(resetButtons);
        buttons.get(index).click();
    }

    public void scrollToBottom() {
        // Scroll down to the bottom of the page
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        try {
            Thread.sleep(500); // Wait for half a second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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

    // Wait methods
    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(participantsList));
    }

    public void waitForReload() {
        // Wait for page to reload by checking if the URL changes or page refreshes
        String initialUrl = driver.getCurrentUrl();
        wait.until(driver -> !driver.getCurrentUrl().equals(initialUrl) ||
                           driver.findElement(participantsList).isDisplayed());
    }
}
