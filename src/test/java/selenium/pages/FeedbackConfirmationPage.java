package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackConfirmationPage {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackConfirmationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public String getName() {
        return driver.findElement(By.id("name")).getText();
    }

    public String getAge() {
        return driver.findElement(By.id("age")).getText();
    }

    public String getLanguage() {
        return driver.findElement(By.id("language")).getText();
    }

    public String getGender() {
        return driver.findElement(By.id("gender")).getText();
    }

    public String getRating() {
        return driver.findElement(By.id("option")).getText();
    }

    public String getComment() {
        return driver.findElement(By.id("comment")).getText();
    }

    public void waitForName(String name) {
        wait.until(ExpectedConditions.textToBe(By.id("name"), name));
    }

    public ThankYouPage clickYes() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        return new ThankYouPage(driver);
    }

    public FeedbackPage clickNo() {
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(ExpectedConditions.urlContains("provide_feedback"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_form")));
        return new FeedbackPage(driver);
    }
}
