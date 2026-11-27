package selenium.tasks;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtils;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String FEEDBACK_PAGE =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final String TEST_NAME = "Taylor Example";
    private static final String TEST_AGE = "34";
    private static final String TEST_COMMENT = "The site is easy to use.";

    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtils.createChromeDriver();
        driver.get(FEEDBACK_PAGE);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertAll(
                () -> assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value")),
                () -> assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value")),
                () -> assertEquals("", driver.findElement(By.name("comment")).getAttribute("value")),
                () -> assertTrue(driver.findElements(By.cssSelector(
                        "#lang_check input[type='checkbox']")).stream().noneMatch(WebElement::isSelected)),
                () -> assertFalse(driver.findElement(By.cssSelector(
                        "input[name='gender'][value='male']")).isSelected()),
                () -> assertFalse(driver.findElement(By.cssSelector(
                        "input[name='gender'][value='female']")).isSelected()),
                () -> assertTrue(driver.findElement(By.cssSelector(
                        "input[name='gender'][disabled]")).isSelected()),
                () -> assertEquals("Choose your option",
                        new Select(driver.findElement(By.id("like_us")))
                                .getFirstSelectedOption().getText()));

        WebElement sendButton = driver.findElement(By.cssSelector("#fb_form button[type='submit']"));
        assertAll(
                () -> assertEquals("Send", sendButton.getText()),
                () -> assertEquals("rgba(33, 150, 243, 1)", sendButton.getCssValue("background-color")),
                () -> assertEquals("rgba(255, 255, 255, 1)", sendButton.getCssValue("color")));
    }

    @Test
    public void emptyFeedbackPage() {
        submitFeedback();

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
        fillFeedbackForm();
        submitFeedback();

        assertFeedbackSummary(TEST_NAME, TEST_AGE, "English,French", "female", "Good", TEST_COMMENT);
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        driver.findElement(By.id("fb_name")).sendKeys(TEST_NAME);
        submitFeedback();
        clickYes();

        assertEquals("Thank you, " + TEST_NAME + ", for your feedback!",
                waitForText(By.id("message")));
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        submitFeedback();
        clickYes();

        assertEquals("Thank you for your feedback!", waitForText(By.id("message")));
    }

    @Test
    public void noOnFeedbackPage() {
        fillFeedbackForm();
        submitFeedback();
        waitForResultPage();
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();

        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                currentDriver -> currentDriver.getCurrentUrl().contains("provide_feedback")
                        && currentDriver.findElement(By.id("fb_name")).isDisplayed());
        assertAll(
                () -> assertEquals(TEST_NAME, driver.findElement(By.id("fb_name")).getAttribute("value")),
                () -> assertEquals(TEST_AGE, driver.findElement(By.id("fb_age")).getAttribute("value")),
                () -> assertTrue(driver.findElement(
                        By.cssSelector("input[name='language'][value='English']")).isSelected()),
                () -> assertTrue(driver.findElement(
                        By.cssSelector("input[name='language'][value='French']")).isSelected()),
                () -> assertTrue(driver.findElement(
                        By.cssSelector("input[name='gender'][value='female']")).isSelected()),
                () -> assertEquals("Good",
                        new Select(driver.findElement(By.id("like_us")))
                                .getFirstSelectedOption().getAttribute("value")),
                () -> assertEquals(TEST_COMMENT,
                        driver.findElement(By.name("comment")).getAttribute("value")));
    }

    private void fillFeedbackForm() {
        driver.findElement(By.id("fb_name")).sendKeys(TEST_NAME);
        driver.findElement(By.id("fb_age")).sendKeys(TEST_AGE);
        driver.findElement(By.cssSelector(
                "input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector(
                "input[name='language'][value='French']")).click();
        driver.findElement(By.cssSelector(
                "input[name='gender'][value='female']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByValue("Good");
        driver.findElement(By.name("comment")).sendKeys(TEST_COMMENT);
    }

    private void submitFeedback() {
        driver.findElement(By.cssSelector("#fb_form button[type='submit']")).click();
        waitForResultPage();
    }

    private void waitForResultPage() {
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                currentDriver -> currentDriver.getCurrentUrl().contains("check_feedback.html")
                        && currentDriver.findElement(By.id("fb_thx")).isDisplayed());
    }

    private void clickYes() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
    }

    private String waitForText(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                currentDriver -> {
                    String text = currentDriver.findElement(locator).getText();
                    return text.isBlank() ? null : text;
                });
    }

    private void assertFeedbackSummary(
            String name, String age, String languages, String gender, String option, String comment) {
        assertAll(
                () -> assertEquals(name, driver.findElement(By.id("name")).getText()),
                () -> assertEquals(age, driver.findElement(By.id("age")).getText()),
                () -> assertEquals(languages, driver.findElement(By.id("language")).getText()),
                () -> assertEquals(gender, driver.findElement(By.id("gender")).getText()),
                () -> assertEquals(option, driver.findElement(By.id("option")).getText()),
                () -> assertEquals(comment, driver.findElement(By.id("comment")).getText()));
    }

    private void assertEmptyOrNull(String fieldId) {
        String value = driver.findElement(By.id(fieldId)).getText();
        assertTrue(value.isEmpty() || "null".equals(value),
                () -> fieldId + " should be empty or 'null' but was '" + value + "'");
    }
}
