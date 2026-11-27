package selenium.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FeedbackThankYouPage {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;

    public FeedbackThankYouPage(WebDriver driver) {
        this.driver = driver;
    }

    public String message() {
        return new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> {
                    String message = webDriver.findElement(By.id("message")).getText();
                    return message.isBlank() ? null : message;
                });
    }
}
