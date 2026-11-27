package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackConfirmationPage;
import selenium.pages.FeedbackPage;
import selenium.pages.ThankYouPage;
import selenium.utility.DriverUtility;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = DriverUtility.createChromeDriver();
        driver.manage().window().maximize();
        driver.get(FeedbackPage.URL);
        feedbackPage = new FeedbackPage(driver);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }


    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackPage.name());
        assertEquals("", feedbackPage.age());
        assertEquals("", feedbackPage.comment());
        assertTrue(feedbackPage.areLanguagesUnselected());
        assertTrue(feedbackPage.isUnknownGenderSelected());
        assertTrue(feedbackPage.isGenderEnabled("male"));
        assertEquals("Choose your option", feedbackPage.selectedOption());
        assertTrue(feedbackPage.sendButtonClass().contains("w3-blue"));
        assertEquals("rgba(255, 255, 255, 1)", feedbackPage.sendButtonColor());
    }

    @Test
    public void emptyFeedbackPage() {
        FeedbackConfirmationPage confirmationPage = feedbackPage.submit();
        assertSummaryValue(confirmationPage, "name", "");
        assertSummaryValue(confirmationPage, "age", "");
        assertSummaryValue(confirmationPage, "language", "");
        assertSummaryValue(confirmationPage, "gender", "");
        assertSummaryValue(confirmationPage, "option", "");
        assertSummaryValue(confirmationPage, "comment", "");
    }

    @Test
    public void notEmptyFeedbackPage() {
        feedbackPage.fillForm("Jane Doe", "32", "English", "female", "Good", "Great website!");
        FeedbackConfirmationPage confirmationPage = feedbackPage.submit();
        assertSummaryValue(confirmationPage, "name", "Jane Doe");
        assertSummaryValue(confirmationPage, "age", "32");
        assertSummaryValue(confirmationPage, "language", "English");
        assertSummaryValue(confirmationPage, "gender", "female");
        assertSummaryValue(confirmationPage, "option", "Good");
        assertSummaryValue(confirmationPage, "comment", "Great website!");
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        feedbackPage.enterName("Alice");
        ThankYouPage thankYouPage = feedbackPage.submit().confirmYes();
        assertEquals("Thank you, Alice, for your feedback!", thankYouPage.message());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        ThankYouPage thankYouPage = feedbackPage.submit().confirmYes();
        assertEquals("Thank you for your feedback!", thankYouPage.message());
    }

    @Test
    public void noOnFeedbackPage() {
        feedbackPage.fillForm(
                "Bob Smith", "41", "French", "male", "Ok, i guess", "Could be better");
        FeedbackConfirmationPage confirmationPage = feedbackPage.submit();

        assertSummaryValue(confirmationPage, "name", "Bob Smith");
        assertSummaryValue(confirmationPage, "age", "41");
        assertSummaryValue(confirmationPage, "language", "French");
        assertSummaryValue(confirmationPage, "gender", "male");
        assertSummaryValue(confirmationPage, "option", "Ok, i guess");
        assertSummaryValue(confirmationPage, "comment", "Could be better");

        feedbackPage = confirmationPage.confirmNo();
        assertEquals("Bob Smith", feedbackPage.name());
        assertEquals("41", feedbackPage.age());
        assertTrue(feedbackPage.isLanguageSelected("French"));
        assertTrue(feedbackPage.isGenderSelected("male"));
        assertEquals("Ok, i guess", feedbackPage.selectedOption());
        assertEquals("Could be better", feedbackPage.comment());
    }

    private void assertSummaryValue(FeedbackConfirmationPage page, String id, String expected) {
        String actual = page.summaryValue(id, expected);
        if (expected.isEmpty()) {
            assertTrue(actual.isEmpty() || "null".equals(actual),
                    "Expected field '" + id + "' to be empty or \"null\", but was: " + actual);
        } else {
            assertEquals(expected, actual);
        }
    }
}