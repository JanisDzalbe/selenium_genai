package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackConfirmationPage;
import selenium.pages.FeedbackPage;
import selenium.pages.ThankYouPage;
import selenium.utility.WebDriverUtility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.createChromeDriver();
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

    private FeedbackConfirmationPage submitFeedback() {
        return feedbackPage.submit();
    }

    private void fillWholeForm() {
        feedbackPage.enterName("Alice");
        feedbackPage.enterAge("30");
        feedbackPage.selectLanguage("English");
        feedbackPage.selectLanguage("French");
        feedbackPage.selectGender("female");
        feedbackPage.selectRating("Good");
        feedbackPage.enterComment("Excellent service");
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", feedbackPage.getNameValue());
        assertEquals("", feedbackPage.getAgeValue());

        List<String> languages = feedbackPage.getLanguageValues();
        for (String language : languages) {
            assertFalse(feedbackPage.isLanguageSelected(language), language + " should not be selected");
        }

        List<String> genders = feedbackPage.getGenderValues();
        for (String gender : genders) {
            if (gender.isEmpty()) {
                assertTrue(feedbackPage.isGenderSelected(gender));
                assertTrue(feedbackPage.isGenderDisplayed(gender));
            } else {
                assertFalse(feedbackPage.isGenderSelected(gender));
            }
        }

        assertEquals("Choose your option", feedbackPage.getSelectedRating());
        assertEquals("", feedbackPage.getCommentValue());

        assertTrue(feedbackPage.getSendButtonBackgroundColor().contains("33, 150, 243")
                || feedbackPage.getSendButtonBackgroundColor().equals("rgb(33, 150, 243)"));
        assertTrue(feedbackPage.getSendButtonColor().equals("rgba(255, 255, 255, 1)")
                || feedbackPage.getSendButtonColor().equals("rgb(255, 255, 255)"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        FeedbackConfirmationPage confirmationPage = submitFeedback();

        assertTrue(List.of("", "null").contains(confirmationPage.getName()));
        assertTrue(List.of("", "null").contains(confirmationPage.getAge()));
        assertTrue(List.of("", "null").contains(confirmationPage.getLanguage()));
        assertTrue(List.of("", "null").contains(confirmationPage.getGender()));
        assertTrue(List.of("", "null").contains(confirmationPage.getRating()));
        assertTrue(List.of("", "null").contains(confirmationPage.getComment()));
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillWholeForm();
        FeedbackConfirmationPage confirmationPage = submitFeedback();

        confirmationPage.waitForName("Alice");
        assertEquals("Alice", confirmationPage.getName());
        assertEquals("30", confirmationPage.getAge());
        assertTrue(confirmationPage.getLanguage().contains("English"));
        assertTrue(confirmationPage.getLanguage().contains("French"));
        assertEquals("female", confirmationPage.getGender());
        assertEquals("Good", confirmationPage.getRating());
        assertEquals("Excellent service", confirmationPage.getComment());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        feedbackPage.enterName("Alice");
        FeedbackConfirmationPage confirmationPage = submitFeedback();

        ThankYouPage thankYouPage = confirmationPage.clickYes();
        thankYouPage.waitForMessage("Thank you, Alice, for your feedback!");
        assertEquals("Thank you, Alice, for your feedback!",
                thankYouPage.getMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        FeedbackConfirmationPage confirmationPage = submitFeedback();

        ThankYouPage thankYouPage = confirmationPage.clickYes();
        thankYouPage.waitForMessage("Thank you for your feedback!");
        assertEquals("Thank you for your feedback!",
                thankYouPage.getMessage());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm();
        FeedbackConfirmationPage confirmationPage = submitFeedback();

        feedbackPage = confirmationPage.clickNo();

        assertEquals("Alice", feedbackPage.getNameValue());
        assertEquals("30", feedbackPage.getAgeValue());
        assertTrue(feedbackPage.isLanguageSelected("English"));
        assertTrue(feedbackPage.isLanguageSelected("French"));
        assertTrue(feedbackPage.isGenderSelected("female"));
        assertEquals("Good", feedbackPage.getSelectedRating());
        assertEquals("Excellent service", feedbackPage.getCommentValue());
    }
}