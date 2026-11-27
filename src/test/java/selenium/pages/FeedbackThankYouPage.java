package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackThankYouPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackThankYouPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String message() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message"))).getText();
    }
}
