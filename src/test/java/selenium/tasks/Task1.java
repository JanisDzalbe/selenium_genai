package selenium.tasks;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverUtility;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverUtility.createDriver();
        driver.get(FEEDBACK_URL);
        waitFor(By.id("fb_form"));
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.name("comment")).getAttribute("value"));

        List<WebElement> languages = driver.findElements(By.cssSelector("input[name='language']"));
        assertEquals(4, languages.size());
        assertTrue(languages.stream().noneMatch(WebElement::isSelected));

        List<WebElement> genres = driver.findElements(By.cssSelector("input[name='gender']"));
        assertEquals(3, genres.size());
        assertTrue(genres.get(2).isSelected());
        assertFalse(genres.get(2).isEnabled());
        assertTrue(genres.subList(0, 2).stream().noneMatch(WebElement::isSelected));

        Select likeUs = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", likeUs.getFirstSelectedOption().getText());

        WebElement sendButton = driver.findElement(By.cssSelector("#fb_form button[type='submit']"));
        assertEquals("rgba(33, 150, 243, 1)", sendButton.getCssValue("background-color"));
        assertEquals("rgba(255, 255, 255, 1)", sendButton.getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() {
        submitFeedback();

        assertEmptyOrNull("name");
        assertEmptyOrNull("age");
        assertEmptyOrNull("language");
        assertEmptyOrNull("gender");
        assertEmptyOrNull("option");
        assertEmptyOrNull("comment");
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillFeedbackForm();
        submitFeedback();

        assertSummaryValue("name", "Taylor Tester");
        assertSummaryValue("age", "29");
        assertSummaryValue("language", "English,French");
        assertSummaryValue("gender", "female");
        assertSummaryValue("option", "Good");
        assertSummaryValue("comment", "Helpful feedback");
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        driver.findElement(By.id("fb_name")).sendKeys("Taylor");
        submitFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        assertEquals("Thank you, Taylor, for your feedback!",
                waitFor(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        submitFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        assertEquals("Thank you for your feedback!",
                waitFor(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() {
        fillFeedbackForm();
        submitFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();

        waitFor(By.id("fb_form"));
        assertEquals("Taylor Tester", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("29", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElements(By.cssSelector("input[name='language']")).get(0).isSelected());
        assertTrue(driver.findElements(By.cssSelector("input[name='language']")).get(1).isSelected());
        assertTrue(driver.findElements(By.cssSelector("input[name='gender']")).get(1).isSelected());
        assertEquals("Good",
                new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getAttribute("value"));
        assertEquals("Helpful feedback", driver.findElement(By.name("comment")).getAttribute("value"));
    }

    private void fillFeedbackForm() {
        driver.findElement(By.id("fb_name")).sendKeys("Taylor Tester");
        driver.findElement(By.id("fb_age")).sendKeys("29");
        driver.findElements(By.cssSelector("input[name='language']")).get(0).click();
        driver.findElements(By.cssSelector("input[name='language']")).get(1).click();
        driver.findElements(By.cssSelector("input[name='gender']")).get(1).click();
        new Select(driver.findElement(By.id("like_us"))).selectByValue("Good");
        driver.findElement(By.name("comment")).sendKeys("Helpful feedback");
    }

    private void submitFeedback() {
        driver.findElement(By.cssSelector("#fb_form button[type='submit']")).click();
        waitFor(By.id("fb_thx"));
    }

    private WebElement waitFor(By locator) {
        return new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void assertSummaryValue(String field, String expected) {
        By locator = By.id(field);
        assertTrue(new WebDriverWait(driver, WAIT_TIMEOUT)
                        .until(ExpectedConditions.textToBePresentInElementLocated(locator, expected)),
                () -> "Expected " + field + " to contain: " + expected);
        assertEquals(expected, driver.findElement(locator).getText());
    }

    private void assertEmptyOrNull(String field) {
        String value = waitFor(By.id(field)).getText();
        assertTrue(value.isEmpty() || "null".equals(value),
                () -> "Expected " + field + " to be empty or 'null' but was: " + value);
    }
}