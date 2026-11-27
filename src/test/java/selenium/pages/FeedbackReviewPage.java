package selenium.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FeedbackReviewPage {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;

    public FeedbackReviewPage(WebDriver driver) {
        this.driver = driver;
    }

    public String name() {
        return text("name");
    }

    public String age() {
        return text("age");
    }

    public String language() {
        return text("language");
    }

    public String gender() {
        return text("gender");
    }

    public String option() {
        return text("option");
    }

    public String comment() {
        return text("comment");
    }

    public FeedbackThankYouPage confirm() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.getCurrentUrl().contains("thank_you_for_feedback.html"));
        return new FeedbackThankYouPage(driver);
    }

    public FeedbackFormPage decline() {
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.getCurrentUrl().contains("provide_feedback"));
        return new FeedbackFormPage(driver);
    }

    private String text(String id) {
        return driver.findElement(By.id(id)).getText();
    }
}
