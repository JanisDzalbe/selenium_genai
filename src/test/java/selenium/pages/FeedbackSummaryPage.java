package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackSummaryPage {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final By MESSAGE = By.id("message");

    private final WebDriver driver;

    public FeedbackSummaryPage(WebDriver driver) {
        this.driver = driver;
    }

    public String value(String fieldId) {
        return driver.findElement(By.id(fieldId)).getText();
    }

    public String clickYes() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        return new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> {
                    String message = webDriver.findElement(MESSAGE).getText();
                    return message.isEmpty() ? null : message;
                });
    }

    public FeedbackPage clickNo() {
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.getCurrentUrl() != null
                        && webDriver.getCurrentUrl().contains("provide_feedback"));
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.findElement(By.id("fb_name")).isDisplayed());
        return new FeedbackPage(driver);
    }
}
