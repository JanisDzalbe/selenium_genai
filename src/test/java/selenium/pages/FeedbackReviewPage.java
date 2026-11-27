package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackReviewPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackReviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String name() {
        return value("name");
    }

    public String age() {
        return value("age");
    }

    public String language() {
        return value("language");
    }

    public String gender() {
        return value("gender");
    }

    public String option() {
        return value("option");
    }

    public String comment() {
        return value("comment");
    }

    public FeedbackThankYouPage confirmYes() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        return new FeedbackThankYouPage(driver);
    }

    public FeedbackPage confirmNo() {
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(ExpectedConditions.urlToBe(
                "https://janisdzalbe.github.io/example-site/tasks/provide_feedback"));
        return new FeedbackPage(driver);
    }

    private String value(String id) {
        return driver.findElement(By.id(id)).getText();
    }
}
