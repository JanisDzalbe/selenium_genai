package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final By NAME_FIELD = By.id("fb_name");
    private static final By AGE_FIELD = By.id("fb_age");
    private static final By LANGUAGE_CHECKBOXES = By.cssSelector("input[name='language']");
    private static final By GENDER_RADIOS = By.cssSelector("input[name='gender']");
    private static final By OPTION_SELECT = By.id("like_us");
    private static final By COMMENT_FIELD = By.name("comment");
    private static final By SEND_BUTTON = By.cssSelector("#fb_form button[type='submit']");

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = selenium.utility.WebDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(FEEDBACK_URL);
        wait.until(webDriver -> webDriver.findElement(NAME_FIELD).isDisplayed());
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", driver.findElement(NAME_FIELD).getAttribute("value"));
        assertEquals("", driver.findElement(AGE_FIELD).getAttribute("value"));
        assertEquals("", driver.findElement(COMMENT_FIELD).getAttribute("value"));

        for (WebElement checkbox : driver.findElements(LANGUAGE_CHECKBOXES)) {
            assertFalse(checkbox.isSelected(), "Language checkboxes should start unchecked");
        }

        List<WebElement> genderRadios = driver.findElements(GENDER_RADIOS);
        assertEquals(3, genderRadios.size());
        assertFalse(genderRadios.get(0).isSelected(), "Male should start unchecked");
        assertFalse(genderRadios.get(1).isSelected(), "Female should start unchecked");
        assertTrue(genderRadios.get(2).isSelected(), "\"Don't know\" should be selected");
        assertTrue(genderRadios.get(2).isDisplayed());
        assertFalse(genderRadios.get(2).isEnabled(), "\"Don't know\" should be disabled");

        Select option = new Select(driver.findElement(OPTION_SELECT));
        assertEquals("Choose your option", option.getFirstSelectedOption().getText());

        WebElement sendButton = driver.findElement(SEND_BUTTON);
        assertEquals("rgba(33, 150, 243, 1)", sendButton.getCssValue("background-color"));
        assertEquals("rgba(255, 255, 255, 1)", sendButton.getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        submitFeedback();

        assertFeedbackSummary("", "", "", "", "", "");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillFeedbackForm();
        submitFeedback();

        assertFeedbackSummary(
                "Ada Lovelace",
                "36",
                "English,French",
                "female",
                "Good",
                "Selenium feedback");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(NAME_FIELD).sendKeys("Ada Lovelace");
        submitFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        wait.until(webDriver -> webDriver.findElement(By.id("message")).isDisplayed());
        assertEquals("Thank you, Ada Lovelace, for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        submitFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        wait.until(webDriver -> webDriver.findElement(By.id("message")).isDisplayed());
        assertEquals("Thank you for your feedback!", driver.findElement(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillFeedbackForm();
        submitFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();

        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/provide_feedback"));
        assertEquals("Ada Lovelace", driver.findElement(NAME_FIELD).getAttribute("value"));
        assertEquals("36", driver.findElement(AGE_FIELD).getAttribute("value"));
        assertTrue(driver.findElements(LANGUAGE_CHECKBOXES).get(0).isSelected());
        assertTrue(driver.findElements(LANGUAGE_CHECKBOXES).get(1).isSelected());
        assertTrue(driver.findElements(GENDER_RADIOS).get(1).isSelected());
        assertEquals("Good", new Select(driver.findElement(OPTION_SELECT))
                .getFirstSelectedOption().getAttribute("value"));
        assertEquals("Selenium feedback", driver.findElement(COMMENT_FIELD).getAttribute("value"));
    }

    private void fillFeedbackForm() {
        driver.findElement(NAME_FIELD).sendKeys("Ada Lovelace");
        driver.findElement(AGE_FIELD).sendKeys("36");

        List<WebElement> languages = driver.findElements(LANGUAGE_CHECKBOXES);
        languages.get(0).click();
        languages.get(1).click();
        driver.findElements(GENDER_RADIOS).get(1).click();
        new Select(driver.findElement(OPTION_SELECT)).selectByValue("Good");
        driver.findElement(COMMENT_FIELD).sendKeys("Selenium feedback");
    }

    private void submitFeedback() {
        driver.findElement(SEND_BUTTON).click();
        wait.until(webDriver -> webDriver.findElement(By.id("fb_thx")).isDisplayed());
    }

    private void assertFeedbackSummary(
            String name,
            String age,
            String language,
            String gender,
            String option,
            String comment) {
        assertSummaryValue(By.id("name"), name);
        assertSummaryValue(By.id("age"), age);
        assertSummaryValue(By.id("language"), language);
        assertSummaryValue(By.id("gender"), gender);
        assertSummaryValue(By.id("option"), option);
        assertSummaryValue(By.id("comment"), comment);
    }

    private void assertSummaryValue(By locator, String expected) {
        String actual = driver.findElement(locator).getText();
        if (expected.isEmpty()) {
            assertTrue(actual.isEmpty() || "null".equals(actual),
                    () -> locator + " should be empty or null, but was: " + actual);
        } else {
            assertEquals(expected, actual, locator.toString());
        }
    }
}