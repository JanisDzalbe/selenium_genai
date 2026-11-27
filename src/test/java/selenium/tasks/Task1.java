package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackPage;
import selenium.utility.WebDriverFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task1 {
    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = WebDriverFactory.createChromeDriver();
        feedbackPage = new FeedbackPage(driver);
        feedbackPage.open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackPage.name());
        assertEquals("", feedbackPage.age());
        assertEquals("", feedbackPage.comment());

        assertEquals(4, feedbackPage.languageCount());
        assertFalse(feedbackPage.anyLanguageSelected());

        assertEquals(3, feedbackPage.genderCount());
        assertFalse(feedbackPage.isGenderSelectedAt(0));
        assertFalse(feedbackPage.isGenderSelectedAt(1));
        assertTrue(feedbackPage.isGenderSelectedAt(2));
        assertFalse(feedbackPage.isGenderEnabledAt(2));

        assertEquals("Choose your option", feedbackPage.selectedOptionText());
        assertEquals("rgba(33, 150, 243, 1)", feedbackPage.submitButtonBackgroundColor());
        assertEquals("rgba(255, 255, 255, 1)", feedbackPage.submitButtonTextColor());
    }

    @Test
    public void emptyFeedbackPage() {
        feedbackPage.submit();

        String summary = feedbackPage.summaryText();
        assertTrue(summary.contains("Your name:"));
        assertTrue(summary.contains("Your age:"));
        assertTrue(summary.contains("Your language:"));
        assertTrue(summary.contains("Your genre: null"));
        assertTrue(summary.contains("Your option of us: null"));
        assertTrue(summary.contains("Your comment:"));
    }

    @Test
    public void notEmptyFeedbackPage() {
        feedbackPage.fillDefaultFeedback();
        feedbackPage.submit();

        String summary = feedbackPage.summaryText();
        assertTrue(summary.contains("Your name: Alex Tester"));
        assertTrue(summary.contains("Your age: 31"));
        assertTrue(summary.contains("Your language: English,French"));
        assertTrue(summary.contains("Your genre: female"));
        assertTrue(summary.contains("Your option of us: Good"));
        assertTrue(summary.contains("Your comment: Great experience"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        feedbackPage.enterName("Alex Tester");
        feedbackPage.submit();
        feedbackPage.confirmSubmission();

        assertEquals("Thank you, Alex Tester, for your feedback!",
                feedbackPage.thankYouHeading());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        feedbackPage.submit();
        feedbackPage.confirmSubmission();

        assertEquals("Thank you for your feedback!", feedbackPage.thankYouHeading());
    }

    @Test
    public void noOnFeedbackPage() {
        feedbackPage.fillDefaultFeedback();
        feedbackPage.submit();
        feedbackPage.rejectSubmission();

        assertEquals("Alex Tester", feedbackPage.name());
        assertEquals("31", feedbackPage.age());
        assertTrue(feedbackPage.isLanguageSelected("English"));
        assertTrue(feedbackPage.isLanguageSelected("French"));
        assertTrue(feedbackPage.isGenderSelected("female"));
        assertEquals("Good", feedbackPage.selectedOptionText());
        assertEquals("Great experience", feedbackPage.comment());
    }
}
