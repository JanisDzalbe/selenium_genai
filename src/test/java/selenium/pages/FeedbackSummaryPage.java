package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackSummaryPage {
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackSummaryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    public String getName() {
        return getSummaryValue("name");
    }

    public String getAge() {
        return getSummaryValue("age");
    }

    public String getLanguage() {
        return getSummaryValue("language");
    }

    public String getGender() {
        return getSummaryValue("gender");
    }

    public String getOption() {
        return getSummaryValue("option");
    }

    public String getComment() {
        return getSummaryValue("comment");
    }

    public FeedbackThankYouPage confirm() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(d -> d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        return new FeedbackThankYouPage(driver);
    }

    public FeedbackPage reject() {
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(d -> d.getCurrentUrl().contains("provide_feedback"));
        return new FeedbackPage(driver);
    }

    private String getSummaryValue(String id) {
        return wait.until(d -> d.findElement(By.id(id)).getText());
    }
}
