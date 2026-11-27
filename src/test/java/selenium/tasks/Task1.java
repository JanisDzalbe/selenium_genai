package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackPage;
import selenium.pages.FeedbackReviewPage;
import selenium.pages.FeedbackThankYouPage;
import selenium.utility.ChromeDriverUtility;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        // TODO
        //  initialize the driver
        //  open page https://janisdzalbe.github.io/example-site/tasks/provide_feedback
        driver = ChromeDriverUtility.createChromeDriver();
        feedbackPage = new FeedbackPage(driver).open();
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterEach
    public void closeBrowser() {
        // TODO
        //  close the browser
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        // TODO:
        //  check that all field are empty and no ticks are clicked
        //  "Don't know" is selected in "Genre"
        //  "Choose your option" in "How do you like us?"
        //  check that the button send is blue with white letters
        assertEquals("", feedbackPage.getNameInputValue());
        assertEquals("", feedbackPage.getAgeInputValue());
        assertEquals("", feedbackPage.getCommentInputValue());
        assertTrue(feedbackPage.areLanguagesUnselected());
        assertTrue(feedbackPage.isDefaultGenderSelected());
        assertEquals("Choose your option", feedbackPage.getSelectedLikeUsOption());
        assertTrue(feedbackPage.getSubmitButtonBackgroundColor()
                .matches("rgba?\\(33, ?150, ?243(?:, ?1)?\\)"));
        assertTrue(feedbackPage.getSubmitButtonTextColor()
                .matches("rgba?\\(255, ?255, ?255(?:, ?1)?\\)"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        // TODO:
        //  click "Send" without entering any data
        //  check fields are empty or "null"
        FeedbackReviewPage reviewPage = feedbackPage.submit();

        assertEmptyOrNull("name", reviewPage.getName());
        assertEmptyOrNull("age", reviewPage.getAge());
        assertEmptyOrNull("language", reviewPage.getLanguage());
        assertEmptyOrNull("gender", reviewPage.getGender());
        assertEmptyOrNull("option", reviewPage.getOption());
        assertEmptyOrNull("comment", reviewPage.getComment());
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        // TODO:
        //  fill the whole form, click "Send"
        //  check fields are filled correctly
        FeedbackReviewPage reviewPage = feedbackPage
                .fill("Janis", "30", "English", "male", "Good", "Great experience")
                .submit();

        assertFeedback(reviewPage);
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // TODO:
        //  enter only name
        //  click "Send"
        //  click "Yes"
        //  check message text: "Thank you, NAME, for your feedback!"
        FeedbackThankYouPage thankYouPage = feedbackPage
                .enterName("Janis")
                .submit()
                .confirm();

        assertEquals("Thank you, Janis, for your feedback!", thankYouPage.getMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        // TODO:
        //  click "Send" (without entering anything)
        //  click "Yes"
        //  check message text: "Thank you for your feedback!"
        FeedbackThankYouPage thankYouPage = feedbackPage.submit().confirm();

        assertEquals("Thank you for your feedback!", thankYouPage.getMessage());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        // TODO:
        //  fill the whole form
        //  click "Send"
        //  click "No"
        //  check fields are filled correctly
        FeedbackReviewPage reviewPage = feedbackPage
                .fill("Janis", "30", "English", "male", "Good", "Great experience")
                .submit();
        assertFeedback(reviewPage);

        FeedbackPage returnedPage = reviewPage.edit();
        assertEquals("Janis", returnedPage.getNameInputValue());
        assertEquals("30", returnedPage.getAgeInputValue());
        assertTrue(returnedPage.isLanguageSelected("English"));
        assertTrue(returnedPage.isGenderSelected("male"));
        assertEquals("Good", returnedPage.getSelectedLikeUsOption());
        assertEquals("Great experience", returnedPage.getCommentInputValue());
    }

    private void assertFeedback(FeedbackReviewPage reviewPage) {
        assertEquals("Janis", reviewPage.getName());
        assertEquals("30", reviewPage.getAge());
        assertEquals("English", reviewPage.getLanguage());
        assertEquals("male", reviewPage.getGender());
        assertEquals("Good", reviewPage.getOption());
        assertEquals("Great experience", reviewPage.getComment());
    }

    private void assertEmptyOrNull(String field, String value) {
//    helper method to assert that a field is empty or null
        value = value.trim();
        assertTrue(value.isEmpty() || value.equalsIgnoreCase("null"),
                field + " should be empty or null, but was: " + value);
    }
}