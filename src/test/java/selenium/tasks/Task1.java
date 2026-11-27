package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import selenium.pages.FeedbackPage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    FeedbackPage feedbackPage;

    @BeforeEach
    public void openPage() {
        Path chromeDriver = Path.of("lib", "chromedriver.exe");
        if (Files.exists(chromeDriver)) {
            System.setProperty("webdriver.chrome.driver", chromeDriver.toAbsolutePath().toString());
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox",
                "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        feedbackPage = new FeedbackPage(driver);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void assertEmptyOrNull(String fieldName) {
        String text = feedbackPage.submittedValue(fieldName);
        assertTrue(text.isEmpty() || text.equalsIgnoreCase("null"),
                () -> "Expected an empty or null value for " + fieldName + ", but was '" + text + "'");
    }

    private void assertRgba(WebElement element, String property, String expected) {
        String value = element.getCssValue(property);
        assertTrue(value.equals(expected) || value.replace("rgb(", "rgba(").replace(")", ", 1)")
                        .equals(expected),
                () -> "Expected " + property + " " + expected + " but was " + value);
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", feedbackPage.nameField().getAttribute("value"));
        assertEquals("", feedbackPage.ageField().getAttribute("value"));

        for (WebElement language : feedbackPage.languageOptions()) {
            assertFalse(language.isSelected());
        }

        WebElement dontKnow = feedbackPage.unknownGenderOption();
        assertTrue(dontKnow.isSelected());
        assertTrue(feedbackPage.selectedGenderOptions().isEmpty());

        assertEquals("Choose your option", feedbackPage.firstSelectedOptionText());
        assertEquals("", feedbackPage.commentField().getAttribute("value"));

        WebElement send = feedbackPage.submitButton();
        assertRgba(send, "background-color", "rgba(33, 150, 243, 1)");
        assertRgba(send, "color", "rgba(255, 255, 255, 1)");
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        feedbackPage.submit();

        assertEmptyOrNull("name");
        assertEmptyOrNull("age");
        assertEmptyOrNull("language");
        assertEmptyOrNull("gender");
        assertEmptyOrNull("option");
        assertEmptyOrNull("comment");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        feedbackPage.fill("Janis", "30", "male", "Good",
                List.of("English", "French"), "Great site!");
        feedbackPage.submit();

        assertEquals("Janis", feedbackPage.submittedValue("name"));
        assertEquals("30", feedbackPage.submittedValue("age"));
        assertEquals("English,French", feedbackPage.submittedValue("language"));
        assertEquals("male", feedbackPage.submittedValue("gender"));
        assertEquals("Good", feedbackPage.submittedValue("option"));
        assertEquals("Great site!", feedbackPage.submittedValue("comment"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        feedbackPage.nameField().sendKeys("Janis");
        feedbackPage.submit();
        feedbackPage.confirmYes();
        assertEquals("Thank you, Janis, for your feedback!", feedbackPage.thankYouMessage());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        feedbackPage.submit();
        feedbackPage.confirmYes();
        assertEquals("Thank you for your feedback!", feedbackPage.thankYouMessage());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        feedbackPage.fill("Janis", "30", "female", "Ok, i guess",
                List.of("Spanish", "Chinese"), "Could improve.");
        feedbackPage.submit();
        feedbackPage.chooseNo();

        assertEquals("Janis", feedbackPage.nameField().getAttribute("value"));
        assertEquals("30", feedbackPage.ageField().getAttribute("value"));
        assertTrue(feedbackPage.languageOption("Spanish").isSelected());
        assertTrue(feedbackPage.languageOption("Chinese").isSelected());
        assertTrue(feedbackPage.genderOption("female").isSelected());
        assertEquals("Ok, i guess", feedbackPage.firstSelectedOptionText());
        assertEquals("Could improve.", feedbackPage.commentField().getAttribute("value"));
    }
}