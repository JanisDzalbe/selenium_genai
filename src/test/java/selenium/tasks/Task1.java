package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.FeedbackPage;
import selenium.pages.FeedbackReviewPage;
import selenium.pages.FeedbackThankYouPage;
import selenium.utility.ChromeDriverUtility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task1 {
    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverUtility.createChromeDriver();
        feedbackPage = new FeedbackPage(driver);
        feedbackPage.open();
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

        for (WebElement checkbox : feedbackPage.getLanguageCheckboxes()) {
            assertFalse(checkbox.isSelected());
        }
        for (WebElement gender : feedbackPage.getGenderRadios()) {
            if (!"male".equals(gender.getAttribute("value"))
                    && !"female".equals(gender.getAttribute("value"))) {
                assertTrue(gender.isSelected());
            } else {
                assertFalse(gender.isSelected());
            }
        }

        assertEquals("Choose your option", feedbackPage.getSelectedOption());
        assertTrue(feedbackPage.getSendButtonClass().contains("w3-blue"));
        assertEquals("rgba(33, 150, 243, 1)",
                feedbackPage.getSendButtonBackgroundColor());
        assertEquals("rgba(255, 255, 255, 1)",
                feedbackPage.getSendButtonTextColor());
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        reviewPage.waitUntilLoaded();

        assertEmptyOrNull(reviewPage.getSummary("name"));
        assertEmptyOrNull(reviewPage.getSummary("age"));
        assertEmptyOrNull(reviewPage.getSummary("language"));
        assertEmptyOrNull(reviewPage.getSummary("gender"));
        assertEmptyOrNull(reviewPage.getSummary("option"));
        assertEmptyOrNull(reviewPage.getSummary("comment"));
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        feedbackPage.fillWholeForm("Alex", "30");
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        reviewPage.waitUntilLoaded();

        assertEquals("Alex", reviewPage.getSummary("name"));
        assertEquals("30", reviewPage.getSummary("age"));
        assertEquals("English,French", reviewPage.getSummary("language"));
        assertEquals("male", reviewPage.getSummary("gender"));
        assertEquals("Good", reviewPage.getSummary("option"));
        assertEquals("Great service", reviewPage.getSummary("comment"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        feedbackPage.enterName("Alex");
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        reviewPage.waitUntilLoaded();

        FeedbackThankYouPage thankYouPage = reviewPage.clickYes();
        assertEquals("Thank you, Alex, for your feedback!",
                thankYouPage.getMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        reviewPage.waitUntilLoaded();

        FeedbackThankYouPage thankYouPage = reviewPage.clickYes();
        assertEquals("Thank you for your feedback!",
                thankYouPage.getMessage());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        feedbackPage.fillWholeForm("Alex", "30");
        FeedbackReviewPage reviewPage = feedbackPage.submit();
        reviewPage.waitUntilLoaded();

        assertEquals("Alex", reviewPage.getSummary("name"));
        assertEquals("30", reviewPage.getSummary("age"));

        FeedbackPage returnedPage = reviewPage.clickNo();
        assertEquals("Alex", returnedPage.getName());
        assertEquals("30", returnedPage.getAge());

        for (WebElement checkbox : returnedPage.getLanguageCheckboxes()) {
            if ("English".equals(checkbox.getAttribute("value"))
                    || "French".equals(checkbox.getAttribute("value"))) {
                assertTrue(checkbox.isSelected());
            }
        }
        for (WebElement gender : returnedPage.getGenderRadios()) {
            if ("male".equals(gender.getAttribute("value"))) {
                assertTrue(gender.isSelected());
            }
        }
        assertEquals("Good", returnedPage.getSelectedOption());
        assertEquals("Great service", returnedPage.getComment());
    }

    private void assertEmptyOrNull(String value) {
        assertTrue(value.isEmpty() || "null".equalsIgnoreCase(value),
                "Expected field to be empty or null, but was '" + value + "'");
    }
}
