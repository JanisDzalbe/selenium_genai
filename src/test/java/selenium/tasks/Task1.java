package selenium.tasks;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackFormPage;
import selenium.pages.FeedbackReviewPage;
import selenium.pages.FeedbackThankYouPage;
import selenium.utility.DriverFactory;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String EXPECTED_COMMENT = "The form is easy to use.";

    private WebDriver driver;
    private FeedbackFormPage feedbackForm;

    @BeforeEach
    public void openPage() {
        driver = DriverFactory.createChromeDriver();
        feedbackForm = new FeedbackFormPage(driver).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackForm.nameValue());
        assertEquals("", feedbackForm.ageValue());
        assertEquals("", feedbackForm.commentValue());
        assertEquals(List.of(false, false, false, false), feedbackForm.languageSelections());
        assertFalse(feedbackForm.genderSelected("male"));
        assertFalse(feedbackForm.genderSelected("female"));
        assertTrue(feedbackForm.unknownGenderSelectedAndDisabled());
        assertEquals("Choose your option", feedbackForm.selectedLikeUsText());
        assertEquals("#2196f3", feedbackForm.sendButtonBackground());
        assertEquals("#ffffff", feedbackForm.sendButtonTextColor());
    }

    @Test
    public void emptyFeedbackPage() {
        FeedbackReviewPage review = feedbackForm.submit();

        assertEmptyOrNull(review.name());
        assertEmptyOrNull(review.age());
        assertEmptyOrNull(review.language());
        assertEmptyOrNull(review.gender());
        assertEmptyOrNull(review.option());
        assertEmptyOrNull(review.comment());
    }

    @Test
    public void notEmptyFeedbackPage() {
        FeedbackReviewPage review = feedbackForm
                .fill("Taylor Example", "32", EXPECTED_COMMENT)
                .submit();

        assertEquals("Taylor Example", review.name());
        assertEquals("32", review.age());
        assertEquals("English,French", review.language());
        assertEquals("female", review.gender());
        assertEquals("Good", review.option());
        assertEquals(EXPECTED_COMMENT, review.comment());
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        FeedbackThankYouPage thankYou = feedbackForm.enterName("Taylor").submit().confirm();

        assertEquals("Thank you, Taylor, for your feedback!", thankYou.message());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        FeedbackThankYouPage thankYou = feedbackForm.submit().confirm();

        assertEquals("Thank you for your feedback!", thankYou.message());
    }

    @Test
    public void noOnFeedbackPage() {
        FeedbackFormPage returnedForm = feedbackForm
                .fill("Taylor Example", "32", EXPECTED_COMMENT)
                .submit()
                .decline();

        assertEquals("Taylor Example", returnedForm.nameValue());
        assertEquals("32", returnedForm.ageValue());
        assertTrue(returnedForm.languageSelected("English"));
        assertTrue(returnedForm.languageSelected("French"));
        assertTrue(returnedForm.genderSelected("female"));
        assertEquals("Good", returnedForm.selectedLikeUsValue());
        assertEquals(EXPECTED_COMMENT, returnedForm.commentValue());
    }

    private void assertEmptyOrNull(String value) {
        assertTrue(value.isEmpty() || "null".equals(value),
                () -> "Expected empty or null text but was: " + value);
    }
}
