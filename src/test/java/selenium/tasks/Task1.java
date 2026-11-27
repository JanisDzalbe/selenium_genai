package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.utility.ChromeDriverFactory;
import selenium.pages.FeedbackPage;
import selenium.pages.FeedbackSummaryPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createDriver();
        feedbackPage = new FeedbackPage(driver).open();
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
        assertEquals("", feedbackPage.nameValue());
        assertEquals("", feedbackPage.ageValue());
        assertEquals("", feedbackPage.commentValue());

        assertEquals(4, feedbackPage.languageCount());
        assertTrue(feedbackPage.noLanguagesSelected(), "No language should be selected initially");

        assertEquals(3, feedbackPage.genderCount());
        assertFalse(feedbackPage.isGenderSelected("male"));
        assertFalse(feedbackPage.isGenderSelected("female"));
        assertTrue(feedbackPage.isGenderSelectedAt(2),
                "The disabled 'Don't know' option should be selected");
        assertFalse(feedbackPage.isGenderEnabledAt(2), "The 'Don't know' option should be disabled");

        assertEquals("Choose your option", feedbackPage.selectedPreference());
        assertEquals("rgba(33, 150, 243, 1)", feedbackPage.submitBackgroundColor());
        assertEquals("rgba(255, 255, 255, 1)", feedbackPage.submitTextColor());
    }

    @Test
    public void emptyFeedbackPage() {
        FeedbackSummaryPage summaryPage = feedbackPage.submit();

        for (String field : List.of("name", "age", "language", "gender", "option", "comment")) {
            String value = summaryPage.value(field);
            assertTrue(value.isEmpty() || "null".equals(value),
                    () -> "Expected empty or null value for " + field + " but was: " + value);
        }
    }

    @Test
    public void notEmptyFeedbackPage() {
        FeedbackSummaryPage summaryPage = feedbackPage
                .fill("Alex Example", "32", List.of("English", "French"), "female", "Good",
                        "The site was easy to use.")
                .submit();

        assertEquals("Alex Example", summaryPage.value("name"));
        assertEquals("32", summaryPage.value("age"));
        assertEquals("English,French", summaryPage.value("language"));
        assertEquals("female", summaryPage.value("gender"));
        assertEquals("Good", summaryPage.value("option"));
        assertEquals("The site was easy to use.", summaryPage.value("comment"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        FeedbackSummaryPage summaryPage = feedbackPage.enterName("Alex").submit();
        assertEquals("Thank you, Alex, for your feedback!", summaryPage.clickYes());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        FeedbackSummaryPage summaryPage = feedbackPage.submit();
        assertEquals("Thank you for your feedback!", summaryPage.clickYes());
    }

    @Test
    public void noOnFeedbackPage() {
        FeedbackPage returnedPage = feedbackPage
                .fill("Jamie Example", "28", List.of("Spanish"), "male", "Ok, i guess",
                        "There is room to improve.")
                .submit()
                .clickNo();

        assertEquals("Jamie Example", returnedPage.nameValue());
        assertEquals("28", returnedPage.ageValue());
        assertTrue(returnedPage.isLanguageSelected("Spanish"));
        assertTrue(returnedPage.isGenderSelected("male"));
        assertEquals("Ok, i guess", returnedPage.selectedPreference());
        assertEquals("There is room to improve.", returnedPage.commentValue());
    }
}