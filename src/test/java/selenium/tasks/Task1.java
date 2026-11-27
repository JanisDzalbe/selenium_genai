package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.pages.FeedbackPage;
import selenium.utility.WebDriverUtility;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    FeedbackPage page;

    private static final String NAME = "Alex Example";
    private static final String AGE = "30";
    private static final String LANGUAGE = "English";
    private static final String GENDER = "female";
    private static final String OPTION = "Good";
    private static final String COMMENT = "Great service!";

    private static final Map<String, String> FEEDBACK = new LinkedHashMap<>();

    static {
        FEEDBACK.put("name", NAME);
        FEEDBACK.put("age", AGE);
        FEEDBACK.put("language", LANGUAGE);
        FEEDBACK.put("gender", GENDER);
        FEEDBACK.put("option", OPTION);
        FEEDBACK.put("comment", COMMENT);
    }

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.getChromeDriver();
        page = new FeedbackPage(driver);
        page.open();
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void fillWholeForm() {
        page.fillWholeForm(NAME, AGE, LANGUAGE, GENDER, OPTION, COMMENT);
    }

    private void clickSend() {
        page.clickSend();
    }

    private void assertFeedbackResult(Map<String, String> expected) {
        assertEquals(expected, page.result());
    }

    private void assertEmptyOrNullFeedbackResult() {
        Map<String, String> result = page.result();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            assertTrue(entry.getValue().isEmpty() || "null".equals(entry.getValue()),
                    entry.getKey() + " should be empty or null, but was: " + entry.getValue());
        }
    }

    private String normalizeColor(String color) {
        return color.replaceAll("\\s+", "").toLowerCase();
    }

    private void assertFeedbackFormValues() {
        assertEquals(NAME, page.nameValue());
        assertEquals(AGE, page.ageValue());
        assertTrue(page.languageSelectedByValue(LANGUAGE));
        assertTrue(page.genderRadioSelectedByValue(GENDER));
        assertEquals(OPTION, page.selectedOptionValue());
        assertEquals(COMMENT, page.commentValue());
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", page.nameValue());
        assertEquals("", page.ageValue());
        assertEquals("", page.commentValue());

        for (boolean languageSelected : page.languageSelectionStates()) {
            assertFalse(languageSelected);
        }
        assertTrue(page.genderSelectedByValue(""), "\"Don't know\" should be selected by default");
        List<String> genderValues = page.genderValues();
        List<Boolean> genderStates = page.genderSelectionStates();
        for (int i = 0; i < genderValues.size(); i++) {
            if (!genderValues.get(i).isEmpty()) {
                assertFalse(genderStates.get(i));
            }
        }

        assertEquals("Choose your option", page.selectedOptionText());

        assertEquals("rgba(33,150,243,1)", normalizeColor(page.sendButtonBackgroundColor()));
        assertEquals("rgba(255,255,255,1)", normalizeColor(page.sendButtonTextColor()));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        clickSend();
        assertEmptyOrNullFeedbackResult();
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillWholeForm();
        clickSend();
        assertFeedbackResult(FEEDBACK);
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        page.enterName(NAME);
        clickSend();
        page.clickYes();
        assertEquals("Thank you, " + NAME + ", for your feedback!", page.thankYouMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        clickSend();
        page.clickYes();
        assertEquals("Thank you for your feedback!", page.thankYouMessage());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm();
        clickSend();
        page.clickNo();
        assertFeedbackFormValues();
    }
}
