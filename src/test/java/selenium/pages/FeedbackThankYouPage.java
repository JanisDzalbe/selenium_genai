package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackThankYouPage {
    private static final By MESSAGE = By.id("message");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackThankYouPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getMessage() {
        return wait.until(d -> {
            String message = d.findElement(MESSAGE).getText();
            return message.isEmpty() ? null : message;
        });
    }
}
