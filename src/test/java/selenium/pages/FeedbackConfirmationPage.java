package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackConfirmationPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackConfirmationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String summaryValue(String id) {
        return wait.until(d -> d.findElement(By.id(id)).getText());
    }

    public String acceptFeedback() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(d -> d.findElement(By.id("message")).getText().contains("Thank you"));
        return driver.findElement(By.id("message")).getText();
    }

    public FeedbackPage rejectFeedback() {
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(d -> d.findElement(By.id("fb_name")).isDisplayed());
        return new FeedbackPage(driver);
    }
}
