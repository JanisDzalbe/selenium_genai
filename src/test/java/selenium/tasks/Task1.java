package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackPage;
import selenium.pages.FeedbackSummaryPage;
import selenium.pages.FeedbackThankYouPage;
import selenium.utility.WebDriverUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private WebDriver driver;
    private FeedbackPage feedbackPage;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtils.createChromeDriver();
        driver.manage().timeouts().implicitlyWait(TIMEOUT);
        driver.manage().window().maximize();
        feedbackPage = new FeedbackPage(driver).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private FeedbackSummaryPage fillAllFields() {
        return feedbackPage.submitFeedback("Janis", "30", "English,French", "male", "Good",
                "The service is excellent");
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackPage.getName());
        assertEquals("", feedbackPage.getAge());
        assertTrue(feedbackPage.getSelectedLanguages().isEmpty());
        assertTrue(feedbackPage.getSelectedEnabledGenders().isEmpty());

        assertEquals(List.of(""), feedbackPage.getDisabledGenderValues());
        assertEquals(List.of(""), feedbackPage.getSelectedDisabledGenderValues());

        assertEquals("Choose your option", feedbackPage.getSelectedLikeUsOption());
        assertEquals("", feedbackPage.getComment());

        assertTrue(feedbackPage.getSubmitButtonClass().contains("w3-blue"));
        String textColor = feedbackPage.getSubmitButtonTextColor();
        assertTrue(textColor.matches("rgba?\\(255,255,255(?:,1)?\\)"),
                "Send button text is not white: " + textColor);
    }

    @Test
    public void emptyFeedbackPage() {
        FeedbackSummaryPage summaryPage = feedbackPage.submitFeedback(null, null, null, null, null, null);
        for (String value : Arrays.asList(summaryPage.getName(), summaryPage.getAge(), summaryPage.getLanguage(),
                summaryPage.getGender(), summaryPage.getOption(), summaryPage.getComment())) {
            assertTrue(value == null || value.isEmpty() || "null".equals(value),
                    "Expected summary value to be empty or null, but was " + value);
        }
    }

    @Test
    public void notEmptyFeedbackPage() {
        FeedbackSummaryPage summaryPage = fillAllFields();
        assertEquals("Janis", summaryPage.getName());
        assertEquals("30", summaryPage.getAge());
        assertTrue(summaryPage.getLanguage().contains("English"),
                "Expected language summary to contain English but was " + summaryPage.getLanguage());
        assertEquals("male", summaryPage.getGender());
        assertEquals("Good", summaryPage.getOption());
        assertEquals("The service is excellent", summaryPage.getComment());
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        FeedbackSummaryPage summaryPage =
                feedbackPage.submitFeedback("Janis", null, null, null, null, null);
        FeedbackThankYouPage thankYouPage = summaryPage.confirm();
        assertEquals("Thank you, Janis, for your feedback!",
                thankYouPage.getMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        FeedbackSummaryPage summaryPage = feedbackPage.submitFeedback(null, null, null, null, null, null);
        FeedbackThankYouPage thankYouPage = summaryPage.confirm();
        assertEquals("Thank you for your feedback!",
                thankYouPage.getMessage());
    }

    @Test
    public void noOnFeedbackPage() {
        FeedbackPage returnedPage = fillAllFields().reject();
        assertEquals("Janis", returnedPage.getName());
        assertEquals("30", returnedPage.getAge());
        assertTrue(returnedPage.getSelectedLanguages().contains("English"));
        assertTrue(returnedPage.getSelectedEnabledGenders().contains("male"));
        assertEquals("Good", returnedPage.getSelectedLikeUsOption());
        assertEquals("The service is excellent", returnedPage.getComment());
    }
}