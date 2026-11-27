package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackPage;
import selenium.pages.FeedbackReviewPage;
import selenium.pages.FeedbackThankYouPage;
import selenium.utility.ChromeDriverFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createChromeDriver();
        feedbackPage = new FeedbackPage(driver).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackPage.nameValue());
        assertEquals("", feedbackPage.ageValue());
        assertEquals("", feedbackPage.commentValue());
        assertTrue(feedbackPage.languageSelections().stream().noneMatch(Boolean::booleanValue));

        assertEquals(3, feedbackPage.genderRadioButtonCount());
        assertEquals(List.of(false, false, true), feedbackPage.genderSelections());
        assertTrue(feedbackPage.unknownGenderSelected());

        assertEquals("Choose your option", feedbackPage.selectedOption());
        assertEquals("Send", feedbackPage.sendButtonText());
        assertEquals("rgba(33, 150, 243, 1)", feedbackPage.sendButtonBackgroundColor());
        assertEquals("rgba(255, 255, 255, 1)", feedbackPage.sendButtonTextColor());
    }

    @Test
    public void emptyFeedbackPage() {
        FeedbackReviewPage reviewPage = feedbackPage.submit();

        assertTrue(List.of("", "null").contains(reviewPage.name()));
        assertTrue(List.of("", "null").contains(reviewPage.age()));
        assertTrue(List.of("", "null").contains(reviewPage.language()));
        assertTrue(List.of("", "null").contains(reviewPage.gender()));
        assertTrue(List.of("", "null").contains(reviewPage.option()));
        assertTrue(List.of("", "null").contains(reviewPage.comment()));
    }

    @Test
    public void notEmptyFeedbackPage() {
        FeedbackReviewPage reviewPage = feedbackPage.fillWholeForm().submit();

        assertEquals("Janis", reviewPage.name());
        assertEquals("30", reviewPage.age());
        assertTrue(reviewPage.language().contains("English"));
        assertTrue(reviewPage.language().contains("French"));
        assertTrue(reviewPage.language().contains("Spanish"));
        assertTrue(reviewPage.language().contains("Chinese"));
        assertEquals("male", reviewPage.gender());
        assertEquals("Good", reviewPage.option());
        assertEquals("Excellent service", reviewPage.comment());
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        FeedbackThankYouPage thankYouPage = feedbackPage.enterName("Janis")
                .submit()
                .confirmYes();

        assertEquals("Thank you, Janis, for your feedback!", thankYouPage.message());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        FeedbackThankYouPage thankYouPage = feedbackPage.submit().confirmYes();

        assertEquals("Thank you for your feedback!", thankYouPage.message());
    }

    @Test
    public void noOnFeedbackPage() {
        FeedbackPage returnedPage = feedbackPage.fillWholeForm().submit().confirmNo();

        assertEquals("Janis", returnedPage.nameValue());
        assertEquals("30", returnedPage.ageValue());
        assertTrue(returnedPage.languageSelected("English"));
        assertTrue(returnedPage.languageSelected("French"));
        assertTrue(returnedPage.genderSelected("male"));
        assertEquals("Good", returnedPage.selectedOption());
        assertEquals("Excellent service", returnedPage.commentValue());
    }
}
