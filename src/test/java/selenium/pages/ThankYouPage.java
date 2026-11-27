package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ThankYouPage {
    private final WebDriverWait wait;

    public ThankYouPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String message() {
        WebElement message = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        return message.getText();
    }
}
