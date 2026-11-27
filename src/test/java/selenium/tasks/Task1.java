package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackConfirmationPage;
import selenium.pages.FeedbackPage;
import selenium.utility.ChromeDriverFactory;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    FeedbackPage feedbackPage;

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

    private void assertEmptyOrNull(FeedbackConfirmationPage page, String id) {
        String value = page.summaryValue(id);
        assertTrue(value.isEmpty() || "null".equalsIgnoreCase(value),
                "Expected an empty or null value for " + id + ", but got: " + value);
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", feedbackPage.nameValue());
        assertEquals("", feedbackPage.ageValue());
        assertEquals("", feedbackPage.commentValue());
        assertTrue(feedbackPage.languageSelections().stream().noneMatch(Boolean::booleanValue));
        assertTrue(feedbackPage.enabledGenderSelections().stream().noneMatch(Boolean::booleanValue));
        assertTrue(feedbackPage.isUnknownGenderSelected());
        assertFalse(feedbackPage.isUnknownGenderEnabled());
        assertEquals("Choose your option", feedbackPage.selectedRating());
        assertTrue(feedbackPage.sendButtonClass().contains("w3-blue"));
        String background = feedbackPage.sendButtonBackgroundColor().toLowerCase(Locale.ROOT);
        String foreground = feedbackPage.sendButtonTextColor().toLowerCase(Locale.ROOT);
        assertTrue(background.contains("33, 150, 243") || background.contains("33,150,243"),
                "Send button should be blue, but its background is " + background);
        assertTrue(foreground.contains("255, 255, 255") || foreground.contains("255,255,255"),
                "Send button text should be white, but its color is " + foreground);
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        FeedbackConfirmationPage confirmationPage = feedbackPage.submit();
        assertEmptyOrNull(confirmationPage, "name");
        assertEmptyOrNull(confirmationPage, "age");
        assertEmptyOrNull(confirmationPage, "language");
        assertEmptyOrNull(confirmationPage, "gender");
        assertEmptyOrNull(confirmationPage, "option");
        assertEmptyOrNull(confirmationPage, "comment");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        FeedbackConfirmationPage confirmationPage = feedbackPage.fillWholeForm().submit();

        assertEquals("Janis", confirmationPage.summaryValue("name"));
        assertEquals("30", confirmationPage.summaryValue("age"));
        assertEquals("English,French", confirmationPage.summaryValue("language"));
        assertEquals("male", confirmationPage.summaryValue("gender"));
        assertEquals("Good", confirmationPage.summaryValue("option"));
        assertEquals("Great example site", confirmationPage.summaryValue("comment"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        String message = feedbackPage.enterName("Janis").submit().acceptFeedback();
        assertEquals("Thank you, Janis, for your feedback!", message);
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        String message = feedbackPage.submit().acceptFeedback();
        assertEquals("Thank you for your feedback!", message);
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        FeedbackConfirmationPage confirmationPage = feedbackPage.fillWholeForm().submit();
        assertEquals("Janis", confirmationPage.summaryValue("name"));
        feedbackPage = confirmationPage.rejectFeedback();

        assertEquals("Janis", feedbackPage.nameValue());
        assertEquals("30", feedbackPage.ageValue());
        assertTrue(feedbackPage.isLanguageSelected("English"));
        assertTrue(feedbackPage.isLanguageSelected("French"));
        assertTrue(feedbackPage.isGenderSelected("male"));
        assertEquals("Good", feedbackPage.selectedRating());
        assertEquals("Great example site", feedbackPage.commentValue());
    }
}