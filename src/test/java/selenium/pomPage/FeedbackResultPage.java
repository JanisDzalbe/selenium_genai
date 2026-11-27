package selenium.pomPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackResultPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackResultPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String resultText(String id) {
        wait.until(d -> d.findElements(By.id(id)).size() == 1);
        return driver.findElement(By.id(id)).getText();
    }

    public ThankYouPage answerYes() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(d -> d.getCurrentUrl() != null
                && d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        return new ThankYouPage(driver);
    }

    public FeedbackPage answerNo() {
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(d -> d.getCurrentUrl() != null
                && d.getCurrentUrl().contains("provide_feedback"));
        return new FeedbackPage(driver);
    }
}
