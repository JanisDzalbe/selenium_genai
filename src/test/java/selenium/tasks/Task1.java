package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.pages.FeedbackPage;
import selenium.pages.FeedbackReviewPage;
import selenium.pages.ThankYouPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static selenium.utility.WebDriverUtils.createChromeDriver;

public class Task1 {
    WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = createChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
        feedbackPage = new FeedbackPage(driver);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", feedbackPage.getName());
        assertEquals("", feedbackPage.getAge());
        assertTrue(feedbackPage.getSelectedLanguages().isEmpty());
        assertTrue(feedbackPage.isDisabledGenderSelected());
        assertEquals("Choose your option", feedbackPage.getSelectedOption());
        assertEquals("", feedbackPage.getComment());
        assertEquals("Send", feedbackPage.getSendButton().getText());
        assertEquals("rgba(33, 150, 243, 1)", feedbackPage.getSendButton().getCssValue("background-color"));
        assertEquals("rgba(255, 255, 255, 1)", feedbackPage.getSendButton().getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        waitForUrl("check_feedback.html");
        assertEquals("", reviewPage.getName());
        assertEquals("", reviewPage.getAge());
        assertEquals("", reviewPage.getLanguage());
        assertEquals("null", reviewPage.getGender());
        assertEquals("null", reviewPage.getOption());
        assertEquals("", reviewPage.getComment());
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        feedbackPage.fillForm("Jane Doe", "30", "English", "female", "Good", "Excellent service");
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        waitForUrl("check_feedback.html");
        assertEquals("Jane Doe", reviewPage.getName());
        assertEquals("30", reviewPage.getAge());
        assertEquals("English", reviewPage.getLanguage());
        assertEquals("female", reviewPage.getGender());
        assertEquals("Good", reviewPage.getOption());
        assertEquals("Excellent service", reviewPage.getComment());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        feedbackPage.enterName("Jane");
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        waitForUrl("check_feedback.html");
        ThankYouPage thankYouPage = reviewPage.confirm();
        waitForUrl("thank_you_for_feedback.html");
        assertEquals("Thank you, Jane, for your feedback!", thankYouPage.getMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        waitForUrl("check_feedback.html");
        ThankYouPage thankYouPage = reviewPage.confirm();
        waitForUrl("thank_you_for_feedback.html");
        assertEquals("Thank you for your feedback!", thankYouPage.getMessage());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        feedbackPage.fillForm("John Smith", "25", "French", "male", "Ok, i guess", "Could be better");
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        waitForUrl("check_feedback.html");
        feedbackPage = reviewPage.edit();
        waitForUrl("provide_feedback");
        assertEquals("John Smith", feedbackPage.getName());
        assertEquals("25", feedbackPage.getAge());
        assertTrue(feedbackPage.isLanguageSelected("French"));
        assertTrue(feedbackPage.isGenderSelected("male"));
        assertEquals("Ok, i guess", feedbackPage.getSelectedOption());
        assertEquals("Could be better", feedbackPage.getComment());
    }

    private void waitForUrl(String fragment) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getCurrentUrl().contains(fragment));
    }
}
