package selenium.pomTasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pomPage.FeedbackPage;
import selenium.pomPage.FeedbackResultPage;
import selenium.pomPage.ThankYouPage;
import selenium.utility.WebDriverUtility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task1 {
    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.createChromeDriver();
        feedbackPage = new FeedbackPage(driver).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void assertEmptyOrNull(FeedbackResultPage resultPage, String id) {
        String text = resultPage.resultText(id);
        assertTrue(text.isBlank() || "null".equals(text),
                "Expected an empty or null value for " + id + ", but was '" + text + "'");
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackPage.nameValue());
        assertEquals("", feedbackPage.ageValue());
        assertEquals("", feedbackPage.commentValue());

        List<WebElement> languages = feedbackPage.languages();
        languages.forEach(language -> assertFalse(language.isSelected()));

        WebElement male = feedbackPage.gender("male");
        WebElement female = feedbackPage.gender("female");
        WebElement unknown = feedbackPage.unknownGender();
        assertFalse(male.isSelected());
        assertFalse(female.isSelected());
        assertTrue(unknown.isSelected());
        assertTrue(unknown.isDisplayed());
        assertFalse(unknown.isEnabled());

        assertEquals("Choose your option",
                feedbackPage.options().getFirstSelectedOption().getText());
        assertEquals("", feedbackPage.options().getFirstSelectedOption().getAttribute("value"));

        WebElement send = feedbackPage.sendButton();
        assertTrue(send.getCssValue("background-color").matches("rgba?\\(33, 150, 243(?:, 1)?\\)")
                || send.getCssValue("background-color").contains("33, 150, 243"));
        assertTrue(send.getCssValue("color").matches("rgba?\\(255, 255, 255(?:, 1)?\\)")
                || send.getCssValue("color").contains("255, 255, 255"));
    }

    @Test
    public void emptyFeedbackPage() {
        FeedbackResultPage resultPage = feedbackPage.submit();

        assertEmptyOrNull(resultPage, "name");
        assertEmptyOrNull(resultPage, "age");
        assertEmptyOrNull(resultPage, "language");
        assertEmptyOrNull(resultPage, "gender");
        assertEmptyOrNull(resultPage, "option");
        assertEmptyOrNull(resultPage, "comment");
    }

    @Test
    public void notEmptyFeedbackPage() {
        String name = "John";
        String age = "30";
        String comment = "Great experience";

        feedbackPage.enterName(name);
        feedbackPage.enterAge(age);
        feedbackPage.selectLanguage("English");
        feedbackPage.selectLanguage("French");
        feedbackPage.selectGender("male");
        feedbackPage.selectOption("Good");
        feedbackPage.enterComment(comment);

        FeedbackResultPage resultPage = feedbackPage.submit();
        assertEquals(name, resultPage.resultText("name"));
        assertEquals(age, resultPage.resultText("age"));
        assertTrue(resultPage.resultText("language").contains("English"));
        assertTrue(resultPage.resultText("language").contains("French"));
        assertEquals("male", resultPage.resultText("gender"));
        assertEquals("Good", resultPage.resultText("option"));
        assertEquals(comment, resultPage.resultText("comment"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        feedbackPage.enterName("Alice");
        FeedbackResultPage resultPage = feedbackPage.submit();

        ThankYouPage thankYouPage = resultPage.answerYes();
        assertEquals("Thank you, Alice, for your feedback!", thankYouPage.message());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        FeedbackResultPage resultPage = feedbackPage.submit();

        ThankYouPage thankYouPage = resultPage.answerYes();
        assertEquals("Thank you for your feedback!", thankYouPage.message());
    }

    @Test
    public void noOnFeedbackPage() {
        feedbackPage.enterName("Bob");
        feedbackPage.enterAge("42");
        feedbackPage.selectLanguage("Spanish");
        feedbackPage.selectGender("female");
        feedbackPage.selectOption("Ok, i guess");
        feedbackPage.enterComment("Could be better");

        FeedbackResultPage resultPage = feedbackPage.submit();
        assertEquals("Bob", resultPage.resultText("name"));
        assertEquals("42", resultPage.resultText("age"));
        assertEquals("Spanish", resultPage.resultText("language"));
        assertEquals("female", resultPage.resultText("gender"));
        assertEquals("Ok, i guess", resultPage.resultText("option"));
        assertEquals("Could be better", resultPage.resultText("comment"));

        FeedbackPage returnedPage = resultPage.answerNo();
        assertEquals("Bob", returnedPage.nameValue());
        assertEquals("42", returnedPage.ageValue());
        assertTrue(returnedPage.languages().stream()
                .anyMatch(language -> "Spanish".equals(language.getAttribute("value"))
                        && language.isSelected()));
        assertTrue(returnedPage.gender("female").isSelected());
        assertEquals("Ok, i guess",
                returnedPage.options().getFirstSelectedOption().getText());
        assertEquals("Could be better", returnedPage.commentValue());
    }
}
