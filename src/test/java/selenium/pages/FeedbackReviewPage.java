package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackReviewPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackReviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getName() {
        return textFor(By.id("name"));
    }

    public String getAge() {
        return textFor(By.id("age"));
    }

    public String getLanguage() {
        return textFor(By.id("language"));
    }

    public String getGender() {
        return textFor(By.id("gender"));
    }

    public String getOption() {
        return textFor(By.id("option"));
    }

    public String getComment() {
        return textFor(By.id("comment"));
    }

    public FeedbackThankYouPage confirmYes() {
        responseButton("Yes").click();
        wait.until(webDriver -> webDriver.getCurrentUrl()
                .contains("thank_you_for_feedback.html"));
        return new FeedbackThankYouPage(driver);
    }

    public FeedbackPage decline() {
        responseButton("No").click();
        wait.until(webDriver -> webDriver.getCurrentUrl().equals(FeedbackPage.PAGE_URL));
        return new FeedbackPage(driver);
    }

    private String textFor(By locator) {
        return wait.until(webDriver -> webDriver.findElement(locator).getText());
    }

    private WebElement responseButton(String response) {
        return driver.findElement(By.xpath("//button[normalize-space()='" + response + "']"));
    }
}
