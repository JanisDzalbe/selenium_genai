package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.FeedbackPage;
import selenium.utility.WebDriverUtil;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private WebDriver driver;
    private FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtil.getDriver();
        driver.manage().timeouts().implicitlyWait(TIMEOUT);
        driver.manage().window().maximize();
        feedbackPage = new FeedbackPage(driver, TIMEOUT).open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", feedbackPage.getNameValue());
        assertEquals("", feedbackPage.getAgeValue());

        for (WebElement checkbox : feedbackPage.getLanguageCheckboxes()) {
            assertFalse(checkbox.isSelected());
        }
        for (WebElement radio : feedbackPage.getGenderRadios()) {
            if (radio.isEnabled()) {
                assertFalse(radio.isSelected());
            }
        }

        List<WebElement> disabledGenderRadios = feedbackPage.getDisabledGenderRadios();
        assertEquals(1, disabledGenderRadios.size());
        assertTrue(disabledGenderRadios.get(0).isSelected());
        assertEquals("", disabledGenderRadios.get(0).getAttribute("value"));

        assertEquals("Choose your option", feedbackPage.getSelectedLikeUsOption());
        assertEquals("", feedbackPage.getCommentValue());

        WebElement button = feedbackPage.sendButton();
        assertTrue(button.getAttribute("class").contains("w3-blue"));
        String textColor = button.getCssValue("color").replace(" ", "");
        assertTrue(textColor.matches("rgba?\\(255,255,255(?:,1)?\\)"),
                "Send button text is not white: " + textColor);
    }

    @Test
    public void emptyFeedbackPage() {
        feedbackPage.clickSendAndWaitForSummary();

        for (String id : List.of("name", "age", "language", "gender", "option", "comment")) {
            String value = feedbackPage.getSummaryValue(id);
            assertTrue(value == null || value.isEmpty() || "null".equals(value),
                    "Expected " + id + " to be empty or null, but was " + value);
        }
    }

    @Test
    public void notEmptyFeedbackPage() {
        feedbackPage.fillAllFields();
        feedbackPage.assertSummaryValue("name", "Janis");
        feedbackPage.assertSummaryValue("age", "30");
        feedbackPage.assertSummaryValue("language", "English");
        feedbackPage.assertSummaryValue("gender", "male");
        feedbackPage.assertSummaryValue("option", "Good");
        feedbackPage.assertSummaryValue("comment", "The service is excellent");
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        feedbackPage.submitFeedback("Janis", null, null, null, null, null);
        feedbackPage.confirmYes();
        assertEquals("Thank you, Janis, for your feedback!", feedbackPage.getConfirmationMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        feedbackPage.clickSendAndWaitForSummary();
        feedbackPage.confirmYes();
        assertEquals("Thank you for your feedback!", feedbackPage.getConfirmationMessage());
    }

    @Test
    public void noOnFeedbackPage() {
        feedbackPage.fillAllFields();
        feedbackPage.clickNo();

        assertEquals("Janis", feedbackPage.getNameValue());
        assertEquals("30", feedbackPage.getAgeValue());
        assertTrue(feedbackPage.isLanguageSelected("English"));
        assertTrue(feedbackPage.isGenderSelected("male"));
        assertEquals("Good", feedbackPage.getSelectedLikeUsOption());
        assertEquals("The service is excellent", feedbackPage.getCommentValue());
    }
}