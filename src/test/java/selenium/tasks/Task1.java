package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackPage;
import selenium.pages.FeedbackReviewPage;
import selenium.pages.FeedbackThankYouPage;
import selenium.utility.ChromeDriverUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task1 {
    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverUtility.createChromeDriver();
        feedbackPage = new FeedbackPage(driver);
        feedbackPage.open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackPage.getName());
        assertEquals("", feedbackPage.getAge());
        assertEquals("", feedbackPage.getComment());
        assertTrue(feedbackPage.areAllLanguageOptionsUnselected());
        assertTrue(feedbackPage.isUnknownGenderSelected());
        assertFalse(feedbackPage.isGenderSelected("male"));
        assertFalse(feedbackPage.isGenderSelected("female"));
        assertEquals("Choose your option", feedbackPage.getSelectedLikeUsOption());
        assertEquals("rgba(33, 150, 243, 1)", feedbackPage.getSubmitButtonBackgroundColor());
        assertEquals("rgba(255, 255, 255, 1)", feedbackPage.getSubmitButtonTextColor());
    }

    @Test
    public void emptyFeedbackPage() {
        FeedbackReviewPage reviewPage = feedbackPage.submit();

        assertNullOrEmpty(reviewPage.getName());
        assertNullOrEmpty(reviewPage.getAge());
        assertNullOrEmpty(reviewPage.getLanguage());
        assertNullOrEmpty(reviewPage.getGender());
        assertNullOrEmpty(reviewPage.getOption());
        assertNullOrEmpty(reviewPage.getComment());
    }

    @Test
    public void notEmptyFeedbackPage() {
        FeedbackReviewPage reviewPage = feedbackPage
                .enterName("Janis")
                .enterAge("30")
                .selectLanguage("English")
                .selectLanguage("French")
                .selectGender("male")
                .selectLikeUsOption("Good")
                .enterComment("Automated Selenium feedback")
                .submit();

        assertEquals("Janis", reviewPage.getName());
        assertEquals("30", reviewPage.getAge());
        assertEquals("English,French", reviewPage.getLanguage());
        assertEquals("male", reviewPage.getGender());
        assertEquals("Good", reviewPage.getOption());
        assertEquals("Automated Selenium feedback", reviewPage.getComment());
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        FeedbackThankYouPage thankYouPage = feedbackPage
                .enterName("Janis")
                .submit()
                .confirmYes();

        assertEquals("Thank you, Janis, for your feedback!", thankYouPage.getMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        FeedbackThankYouPage thankYouPage = feedbackPage
                .submit()
                .confirmYes();

        assertEquals("Thank you for your feedback!", thankYouPage.getMessage());
    }

    @Test
    public void noOnFeedbackPage() {
        FeedbackPage returnedFeedbackPage = feedbackPage
                .enterName("Janis")
                .enterAge("30")
                .selectLanguage("English")
                .selectLanguage("French")
                .selectGender("male")
                .selectLikeUsOption("Good")
                .enterComment("Automated Selenium feedback")
                .submit()
                .decline();

        assertEquals("Janis", returnedFeedbackPage.getName());
        assertEquals("30", returnedFeedbackPage.getAge());
        assertEquals("Automated Selenium feedback", returnedFeedbackPage.getComment());
        assertTrue(returnedFeedbackPage.isLanguageSelected("English"));
        assertTrue(returnedFeedbackPage.isLanguageSelected("French"));
        assertTrue(returnedFeedbackPage.isGenderSelected("male"));
        assertEquals("Good", returnedFeedbackPage.getSelectedLikeUsOption());
    }

    private void assertNullOrEmpty(String value) {
        assertTrue(value.isEmpty() || value.equalsIgnoreCase("null"),
                "Expected a null or empty value, but was '" + value + "'");
    }
}
