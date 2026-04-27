package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import selenium.pages.FeedbackPage;
import selenium.utility.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    FeedbackPage page;

    @BeforeEach
    public void openPage() {
        page = new FeedbackPage(DriverManager.createChromeDriver());
        page.openPage();
    }

    @AfterEach
    public void closeBrowser() {
        page.getDriver().quit();
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertTrue(page.isFieldEmpty(page.nameField));
        assertTrue(page.isFieldEmpty(page.ageField));
        assertTrue(page.isFieldEmpty(page.commentField));
        assertEquals("Don't know", page.getSelectedGender());
        assertEquals("Choose your option", page.getSelectedLikeUs());
        assertTrue(page.getDriver().findElement(page.submitButton).getAttribute("class").contains("w3-blue"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        page.submitForm();
        assertTrue(page.isConfirmationPageVisible());
        assertEquals("", page.getDriver().findElement(page.confirmationNameSpan).getText());
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        page.enterName("Test Name");
        page.enterAge("25");
        page.enterComment("Test feedback");
        page.selectGender("male");
        page.selectLikeUs("Good");
        page.submitForm();
        assertTrue(page.isConfirmationPageVisible());
        assertEquals("Test Name", page.getDriver().findElement(page.confirmationNameSpan).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        page.enterName("John");
        page.submitForm();
        assertTrue(page.isConfirmationPageVisible());
        page.clickYes();
        assertEquals("Thank you, John, for your feedback!", page.getThankYouMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        page.submitForm();
        assertTrue(page.isConfirmationPageVisible());
        page.clickYes();
        assertEquals("Thank you for your feedback!", page.getThankYouMessage());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        page.enterName("Test Name");
        page.enterAge("25");
        page.enterComment("Test feedback");
        page.selectGender("male");
        page.selectLikeUs("Good");
        page.submitForm();
        assertTrue(page.isConfirmationPageVisible());
        page.clickNo();
        assertEquals("Test Name", page.getDriver().findElement(page.nameField).getAttribute("value"));
    }
}
