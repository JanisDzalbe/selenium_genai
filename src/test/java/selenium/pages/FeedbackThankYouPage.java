package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FeedbackThankYouPage {
    private final WebDriver driver;

    public FeedbackThankYouPage(WebDriver driver) {
        this.driver = driver;
    }

    public String message() {
        return driver.findElement(By.id("message")).getText();
    }
}
