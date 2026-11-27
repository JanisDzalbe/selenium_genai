package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.FeedbackPage;
import selenium.utility.WebDriverUtils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task1 {
    private static final String TEST_NAME = "Taylor Example";
    private static final String TEST_AGE = "34";
    private static final String TEST_COMMENT = "The site is easy to use.";

    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtils.createChromeDriver();
        feedbackPage = new FeedbackPage(driver);
        feedbackPage.open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        feedbackPage = null;
    }

    @Test
    public void initialFeedbackPage() {
        assertAll(
                () -> assertEquals("", feedbackPage.getNameValue()),
                () -> assertEquals("", feedbackPage.getAgeValue()),
                () -> assertEquals("", feedbackPage.getCommentValue()),
                () -> assertFalse(feedbackPage.areLanguagesSelected()),
                () -> assertFalse(feedbackPage.isGenderSelected("male")),
                () -> assertFalse(feedbackPage.isGenderSelected("female")),
                () -> assertTrue(feedbackPage.isDisabledGenderSelected()),
                () -> assertEquals("Choose your option", feedbackPage.getSelectedOptionText()));

        WebElement sendButton = feedbackPage.getSendButton();
        assertAll(
                () -> assertEquals("Send", sendButton.getText()),
                () -> assertEquals("rgba(33, 150, 243, 1)", sendButton.getCssValue("background-color")),
                () -> assertEquals("rgba(255, 255, 255, 1)", sendButton.getCssValue("color")));
    }

    @Test
    public void emptyFeedbackPage() {
        feedbackPage.submit();

        assertAll(
                () -> assertEmptyOrNull("name"),
                () -> assertEmptyOrNull("age"),
                () -> assertEmptyOrNull("language"),
                () -> assertEmptyOrNull("gender"),
                () -> assertEmptyOrNull("option"),
                () -> assertEmptyOrNull("comment"));
    }

    @Test
    public void notEmptyFeedbackPage() {
        feedbackPage.fillForm(TEST_NAME, TEST_AGE, TEST_COMMENT);
        feedbackPage.submit();

        assertFeedbackSummary(TEST_NAME, TEST_AGE, "English,French",
                "female", "Good", TEST_COMMENT);
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        feedbackPage.enterName(TEST_NAME);
        feedbackPage.submit();
        feedbackPage.clickYes();

        assertEquals("Thank you, " + TEST_NAME + ", for your feedback!",
                feedbackPage.waitForMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        feedbackPage.submit();
        feedbackPage.clickYes();

        assertEquals("Thank you for your feedback!", feedbackPage.waitForMessage());
    }

    @Test
    public void noOnFeedbackPage() {
        feedbackPage.fillForm(TEST_NAME, TEST_AGE, TEST_COMMENT);
        feedbackPage.submit();
        feedbackPage.clickNo();
        feedbackPage.waitForFeedbackForm();

        assertAll(
                () -> assertEquals(TEST_NAME, feedbackPage.getNameValue()),
                () -> assertEquals(TEST_AGE, feedbackPage.getAgeValue()),
                () -> assertTrue(feedbackPage.isLanguageSelected("English")),
                () -> assertTrue(feedbackPage.isLanguageSelected("French")),
                () -> assertTrue(feedbackPage.isGenderSelected("female")),
                () -> assertEquals("Good", feedbackPage.getSelectedOptionValue()),
                () -> assertEquals(TEST_COMMENT, feedbackPage.getCommentValue()));
    }

    private void assertFeedbackSummary(
            String name, String age, String languages, String gender, String option, String comment) {
        assertAll(
                () -> assertEquals(name, feedbackPage.getSummaryValue("name")),
                () -> assertEquals(age, feedbackPage.getSummaryValue("age")),
                () -> assertEquals(languages, feedbackPage.getSummaryValue("language")),
                () -> assertEquals(gender, feedbackPage.getSummaryValue("gender")),
                () -> assertEquals(option, feedbackPage.getSummaryValue("option")),
                () -> assertEquals(comment, feedbackPage.getSummaryValue("comment")));
    }

    private void assertEmptyOrNull(String fieldId) {
        String value = feedbackPage.getSummaryValue(fieldId);
        assertTrue(value.isEmpty() || "null".equals(value),
                () -> fieldId + " should be empty or 'null' but was '" + value + "'");
    }
}
