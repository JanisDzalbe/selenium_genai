package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackReviewPage {
    private static final By NAME = By.id("name");
    private static final By AGE = By.id("age");
    private static final By LANGUAGE = By.id("language");
    private static final By GENDER = By.id("gender");
    private static final By OPTION = By.id("option");
    private static final By COMMENT = By.id("comment");
    private static final By CONFIRM_BUTTON = By.xpath("//button[normalize-space()='Yes']");
    private static final By EDIT_BUTTON = By.xpath("//button[normalize-space()='No']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackReviewPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getName() {
        return driver.findElement(NAME).getText();
    }

    public String getAge() {
        return driver.findElement(AGE).getText();
    }

    public String getLanguage() {
        return driver.findElement(LANGUAGE).getText();
    }

    public String getGender() {
        return driver.findElement(GENDER).getText();
    }

    public String getOption() {
        return driver.findElement(OPTION).getText();
    }

    public String getComment() {
        return driver.findElement(COMMENT).getText();
    }

    public FeedbackThankYouPage confirm() {
        driver.findElement(CONFIRM_BUTTON).click();
        wait.until(d -> d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        return new FeedbackThankYouPage(driver);
    }

    public FeedbackPage edit() {
        driver.findElement(EDIT_BUTTON).click();
        wait.until(d -> d.getCurrentUrl().contains("provide_feedback"));
        return new FeedbackPage(driver);
    }

}
