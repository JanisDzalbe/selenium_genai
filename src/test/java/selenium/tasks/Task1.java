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
        driver.manage().window().maximize();
        feedbackPage = new FeedbackPage(driver).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", feedbackPage.name());
        assertEquals("", feedbackPage.age());
        assertEquals("", feedbackPage.comment());

        assertTrue(feedbackPage.areLanguagesUnselected());
        assertEquals(List.of(false, false, true), feedbackPage.genderSelections());

        assertEquals("Choose your option", feedbackPage.selectedLikeUsOption());

        String backgroundColor = feedbackPage.sendButtonBackgroundColor().replace(" ", "");
        String textColor = feedbackPage.sendButtonTextColor().replace(" ", "");
        assertTrue(backgroundColor.contains("33") && backgroundColor.contains("150")
                        && backgroundColor.contains("243"),
                "Expected blue send button, but background was: " + backgroundColor);
        assertTrue(textColor.contains("255"),
                "Expected white send-button text, but color was: " + textColor);
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        FeedbackReviewPage reviewPage = feedbackPage.submit();

        assertEmptyOrNull(reviewPage.name());
        assertEmptyOrNull(reviewPage.age());
        assertEmptyOrNull(reviewPage.language());
        assertEmptyOrNull(reviewPage.gender());
        assertEmptyOrNull(reviewPage.option());
        assertEmptyOrNull(reviewPage.comment());
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        feedbackPage.fillForm("John Doe", "30", "English", "male", "Good", "Great experience");
        FeedbackReviewPage reviewPage = feedbackPage.submit();

        assertEquals("John Doe", reviewPage.name());
        assertEquals("30", reviewPage.age());
        assertTrue(reviewPage.language().contains("English"));
        assertEquals("male", reviewPage.gender());
        assertEquals("Good", reviewPage.option());
        assertEquals("Great experience", reviewPage.comment());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        feedbackPage.enterName("Alice");
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        FeedbackThankYouPage thankYouPage = reviewPage.confirm();

        assertEquals("Thank you, Alice, for your feedback!", thankYouPage.message());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        FeedbackThankYouPage thankYouPage = reviewPage.confirm();

        assertEquals("Thank you for your feedback!", thankYouPage.message());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        feedbackPage.fillForm("Jane Doe", "28", "French", "female", "Bad", "Could be better");
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        FeedbackPage returnedFeedbackPage = reviewPage.goBack();

        assertEquals("Jane Doe", returnedFeedbackPage.name());
        assertEquals("28", returnedFeedbackPage.age());
        assertTrue(returnedFeedbackPage.isLanguageSelected("French"));
        assertEquals(List.of(false, true, false), returnedFeedbackPage.genderSelections());
        assertEquals("Bad", returnedFeedbackPage.selectedLikeUsOption());
        assertEquals("Could be better", returnedFeedbackPage.comment());
    }

    private void assertEmptyOrNull(String value) {
        String text = value.trim();
        assertTrue(text.isEmpty() || text.equalsIgnoreCase("null"),
                "Expected field to be empty or \"null\", but was: " + text);
    }
}