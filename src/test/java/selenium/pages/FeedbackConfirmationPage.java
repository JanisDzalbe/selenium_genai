package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackConfirmationPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackConfirmationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String summaryValue(String fieldId, String expected) {
        By locator = By.id(fieldId);
        if (expected.isEmpty()) {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } else {
            wait.until(ExpectedConditions.textToBe(locator, expected));
        }
        return driver.findElement(locator).getText().trim();
    }

    public ThankYouPage confirmYes() {
        clickConfirmationButton("Yes");
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));
        return new ThankYouPage(driver);
    }

    public FeedbackPage confirmNo() {
        clickConfirmationButton("No");
        wait.until(ExpectedConditions.urlToBe(FeedbackPage.URL));
        return new FeedbackPage(driver);
    }

    private void clickConfirmationButton(String text) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='" + text + "']"))).click();
    }
}
