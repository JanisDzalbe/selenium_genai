package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
        return textOf("name");
    }

    public String age() {
        return textOf("age");
    }

    public String language() {
        return textOf("language");
    }

    public String gender() {
        return textOf("gender");
    }

    public String option() {
        return textOf("option");
    }

    public String comment() {
        return textOf("comment");
    }

    public FeedbackThankYouPage confirm() {
        driver.findElement(By.cssSelector("button.w3-green")).click();
        wait.until(d -> d.getCurrentUrl().contains("thank_you_for_feedback"));
        return new FeedbackThankYouPage(driver);
    }

    public FeedbackPage goBack() {
        driver.findElement(By.cssSelector("button.w3-red")).click();
        wait.until(d -> d.getCurrentUrl().contains("provide_feedback"));
        return new FeedbackPage(driver);
    }

    private String textOf(String fieldId) {
        return driver.findElement(By.id(fieldId)).getText();
    }
}
