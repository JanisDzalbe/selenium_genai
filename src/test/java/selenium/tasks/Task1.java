package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = selenium.utility.WebDriverUtility.createChromeDriver();
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
        assertEquals("", feedbackPage.getName());
        assertEquals("", feedbackPage.getAge());
        assertEquals("", feedbackPage.getComment());
        assertEquals(List.of(false, false, false, false), feedbackPage.getLanguageSelections(),
                "Language checkboxes should start unchecked");

        assertEquals(3, feedbackPage.getGenderOptionCount());
        assertFalse(feedbackPage.isGenderSelected(0), "Male should start unchecked");
        assertFalse(feedbackPage.isGenderSelected(1), "Female should start unchecked");
        assertTrue(feedbackPage.isGenderSelected(2), "\"Don't know\" should be selected");
        assertTrue(feedbackPage.isDontKnowGenderVisible());
        assertFalse(feedbackPage.isDontKnowGenderEnabled(), "\"Don't know\" should be disabled");

        assertEquals("Choose your option", feedbackPage.getSelectedFeedbackOptionText());
        assertEquals("rgba(33, 150, 243, 1)", feedbackPage.getSendButtonBackgroundColor());
        assertEquals("rgba(255, 255, 255, 1)", feedbackPage.getSendButtonTextColor());
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        feedbackPage.submit();
        assertFeedbackSummary("", "", "", "", "", "");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        feedbackPage.fillForm().submit();

        assertFeedbackSummary(
                "Ada Lovelace",
                "36",
                "English,French",
                "female",
                "Good",
                "Selenium feedback");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        feedbackPage.enterName("Ada Lovelace").submit().confirmFeedback();
        assertEquals("Thank you, Ada Lovelace, for your feedback!",
                feedbackPage.getThankYouMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        feedbackPage.submit().confirmFeedback();
        assertEquals("Thank you for your feedback!", feedbackPage.getThankYouMessage());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        feedbackPage.fillForm().submit().rejectFeedback();
        assertEquals("Ada Lovelace", feedbackPage.getName());
        assertEquals("36", feedbackPage.getAge());
        assertEquals(List.of(true, true, false, false), feedbackPage.getLanguageSelections());
        assertTrue(feedbackPage.isGenderSelected(1));
        assertEquals("Good", feedbackPage.getSelectedFeedbackOptionValue());
        assertEquals("Selenium feedback", feedbackPage.getComment());
    }

    private void assertFeedbackSummary(
            String name,
            String age,
            String language,
            String gender,
            String option,
            String comment) {
        assertSummaryValue("name", name);
        assertSummaryValue("age", age);
        assertSummaryValue("language", language);
        assertSummaryValue("gender", gender);
        assertSummaryValue("option", option);
        assertSummaryValue("comment", comment);
    }

    private void assertSummaryValue(String field, String expected) {
        String actual = feedbackPage.getConfirmationValue(field);
        if (expected.isEmpty()) {
            assertTrue(actual.isEmpty() || "null".equals(actual),
                    () -> field + " should be empty or null, but was: " + actual);
        } else {
            assertEquals(expected, actual, field);
        }
    }
}