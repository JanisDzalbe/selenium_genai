package selenium.tasks;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.FeedbackFormPage;
import selenium.pages.FeedbackSummaryPage;
import selenium.pages.FeedbackThankYouPage;
import selenium.utility.ChromeDriverUtility;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private WebDriver driver;
    private FeedbackFormPage feedbackForm;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverUtility.createDriver();
        driver.get(FeedbackFormPage.URL);
        feedbackForm = new FeedbackFormPage(driver);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackForm.getName());
        assertEquals("", feedbackForm.getAge());
        assertEquals("", feedbackForm.getComment());

        List<WebElement> languages = feedbackForm.getLanguageOptions();
        assertEquals(4, languages.size());
        assertTrue(languages.stream().noneMatch(WebElement::isSelected));

        List<WebElement> genres = feedbackForm.getGenreOptions();
        assertEquals(3, genres.size());
        assertTrue(genres.get(2).isSelected());
        assertFalse(genres.get(2).isEnabled());
        assertTrue(genres.subList(0, 2).stream().noneMatch(WebElement::isSelected));

        assertEquals("Choose your option", feedbackForm.getSelectedLikeUsText());
        assertEquals("rgba(33, 150, 243, 1)", feedbackForm.getSendButtonBackgroundColor());
        assertEquals("rgba(255, 255, 255, 1)", feedbackForm.getSendButtonTextColor());
    }

    @Test
    public void emptyFeedbackPage() {
        FeedbackSummaryPage summary = feedbackForm.submit();

        assertEmptyOrNull(summary.getName());
        assertEmptyOrNull(summary.getAge());
        assertEmptyOrNull(summary.getLanguages());
        assertEmptyOrNull(summary.getGenre());
        assertEmptyOrNull(summary.getLikeUs());
        assertEmptyOrNull(summary.getComment());
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillFeedbackForm();
        FeedbackSummaryPage summary = feedbackForm.submit();

        assertEquals("Taylor Tester", summary.getName());
        assertEquals("29", summary.getAge());
        assertEquals("English,French", summary.getLanguages());
        assertEquals("female", summary.getGenre());
        assertEquals("Good", summary.getLikeUs());
        assertEquals("Helpful feedback", summary.getComment());
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        feedbackForm.enterName("Taylor");
        FeedbackThankYouPage thankYou = feedbackForm.submit().clickYes();

        assertEquals("Thank you, Taylor, for your feedback!", thankYou.getMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        FeedbackThankYouPage thankYou = feedbackForm.submit().clickYes();

        assertEquals("Thank you for your feedback!", thankYou.getMessage());
    }

    @Test
    public void noOnFeedbackPage() {
        fillFeedbackForm();
        FeedbackFormPage returnedForm = feedbackForm.submit().clickNo();

        assertEquals("Taylor Tester", returnedForm.getName());
        assertEquals("29", returnedForm.getAge());
        assertTrue(returnedForm.getLanguageOptions().get(0).isSelected());
        assertTrue(returnedForm.getLanguageOptions().get(1).isSelected());
        assertTrue(returnedForm.getGenreOptions().get(1).isSelected());
        assertEquals("Good", returnedForm.getSelectedLikeUsValue());
        assertEquals("Helpful feedback", returnedForm.getComment());
    }

    private void fillFeedbackForm() {
        feedbackForm.enterName("Taylor Tester");
        feedbackForm.enterAge("29");
        feedbackForm.selectLanguages(0, 1);
        feedbackForm.selectGenre(1);
        feedbackForm.selectLikeUs("Good");
        feedbackForm.enterComment("Helpful feedback");
    }

    private void assertEmptyOrNull(String value) {
        assertTrue(value.isEmpty() || "null".equals(value),
                () -> "Expected value to be empty or 'null' but was: " + value);
    }
}
