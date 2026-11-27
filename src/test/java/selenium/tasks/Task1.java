package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackPage;
import selenium.utility.WebDriverFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String NAME = "Jane Doe";
    private static final String AGE = "30";
    private static final String COMMENT = "A helpful feedback comment.";

    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = WebDriverFactory.createChromeDriver();
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
        assertEquals("", feedbackPage.getName());
        assertEquals("", feedbackPage.getAge());
        assertEquals("", feedbackPage.getComment());

        assertTrue(feedbackPage.getSelectedLanguages().isEmpty());
        assertFalse(feedbackPage.isGenderSelected("male"));
        assertFalse(feedbackPage.isGenderSelected("female"));
        assertTrue(feedbackPage.isDefaultGenderSelected());
        assertEquals("", feedbackPage.getSelectedOptionValue());
        assertEquals("Choose your option", feedbackPage.getSelectedOptionText());

        assertEquals("rgb(33, 150, 243)", feedbackPage.getSubmitButtonBackgroundColor());
        assertEquals("rgb(255, 255, 255)", feedbackPage.getSubmitButtonTextColor());
    }

    @Test
    public void emptyFeedbackPage() {
        feedbackPage.submit();

        assertSummaryFieldIsEmptyOrNull("name");
        assertSummaryFieldIsEmptyOrNull("age");
        assertSummaryFieldIsEmptyOrNull("language");
        assertSummaryFieldIsEmptyOrNull("gender");
        assertSummaryFieldIsEmptyOrNull("option");
        assertSummaryFieldIsEmptyOrNull("comment");
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillWholeForm();
        feedbackPage.submit();
        assertWholeFormSummary();
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        feedbackPage.enterName(NAME).submit().confirmFeedback();
        assertEquals("Thank you, " + NAME + ", for your feedback!",
                feedbackPage.getThankYouMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        feedbackPage.submit().confirmFeedback();
        assertEquals("Thank you for your feedback!", feedbackPage.getThankYouMessage());
    }

    @Test
    public void noOnFeedbackPage() {
        fillWholeForm();
        feedbackPage.submit().rejectFeedback();

        assertWholeFormValues();
    }

    private void fillWholeForm() {
        feedbackPage.fillForm(NAME, AGE, List.of("English", "French"), "female", "Good", COMMENT);
    }

    private void assertWholeFormValues() {
        assertEquals(NAME, feedbackPage.getName());
        assertEquals(AGE, feedbackPage.getAge());
        assertTrue(feedbackPage.isLanguageSelected("English"));
        assertTrue(feedbackPage.isLanguageSelected("French"));
        assertFalse(feedbackPage.isLanguageSelected("Spanish"));
        assertFalse(feedbackPage.isLanguageSelected("Chinese"));
        assertTrue(feedbackPage.isGenderSelected("female"));
        assertEquals("Good", feedbackPage.getSelectedOptionValue());
        assertEquals(COMMENT, feedbackPage.getComment());
    }

    private void assertWholeFormSummary() {
        assertEquals(NAME, feedbackPage.getSummaryValue("name"));
        assertEquals(AGE, feedbackPage.getSummaryValue("age"));
        assertEquals("English,French", feedbackPage.getSummaryValue("language"));
        assertEquals("female", feedbackPage.getSummaryValue("gender"));
        assertEquals("Good", feedbackPage.getSummaryValue("option"));
        assertEquals(COMMENT, feedbackPage.getSummaryValue("comment"));
    }

    private void assertSummaryFieldIsEmptyOrNull(String fieldId) {
        String text = feedbackPage.getSummaryValue(fieldId);
        assertTrue(text.isEmpty() || "null".equals(text),
                () -> fieldId + " should be empty or null, but was: " + text);
    }
}