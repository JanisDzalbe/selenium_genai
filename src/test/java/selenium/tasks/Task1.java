package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverFactory;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String FEEDBACK_PAGE =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createDriver();
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
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.name("comment")).getAttribute("value"));

        List<WebElement> languages = driver.findElements(By.cssSelector("#lang_check input[type='checkbox']"));
        assertEquals(4, languages.size());
        assertTrue(languages.stream().noneMatch(WebElement::isSelected), "No language should be selected initially");

        List<WebElement> genres = driver.findElements(By.cssSelector("input[name='gender']"));
        assertEquals(3, genres.size());
        assertFalse(genres.get(0).isSelected());
        assertFalse(genres.get(1).isSelected());
        assertTrue(genres.get(2).isSelected(), "The disabled 'Don't know' option should be selected");
        assertFalse(genres.get(2).isEnabled(), "The 'Don't know' option should be disabled");

        Select preference = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", preference.getFirstSelectedOption().getText());

        WebElement sendButton = driver.findElement(By.cssSelector("#fb_form button[type='submit']"));
        assertEquals("rgba(33, 150, 243, 1)", sendButton.getCssValue("background-color"));
        assertEquals("rgba(255, 255, 255, 1)", sendButton.getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() {
        submitFeedback();

        for (String field : List.of("name", "age", "language", "gender", "option", "comment")) {
            String value = driver.findElement(By.id(field)).getText();
            assertTrue(value.isEmpty() || "null".equals(value),
                    () -> "Expected empty or null value for " + field + " but was: " + value);
        }
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillFeedback("Alex Example", "32", List.of("English", "French"), "female", "Good",
                "The site was easy to use.");
        submitFeedback();

        waitForNameSummaryValue("Alex Example");
        assertEquals("Alex Example", summaryValue("name"));
        assertEquals("32", summaryValue("age"));
        assertEquals("English,French", summaryValue("language"));
        assertEquals("female", summaryValue("gender"));
        assertEquals("Good", summaryValue("option"));
        assertEquals("The site was easy to use.", summaryValue("comment"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        driver.findElement(By.id("fb_name")).sendKeys("Alex");
        submitFeedback();
        waitForNameSummaryValue("Alex");

        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        waitForThankYouMessage("Thank you, Alex, for your feedback!");
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        submitFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        waitForThankYouMessage("Thank you for your feedback!");
    }

    @Test
    public void noOnFeedbackPage() {
        fillFeedback("Jamie Example", "28", List.of("Spanish"), "male", "Ok, i guess",
                "There is room to improve.");
        submitFeedback();

        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.getCurrentUrl() != null
                        && webDriver.getCurrentUrl().contains("provide_feedback"));

        assertEquals("Jamie Example", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("28", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("#lang_check input[value='Spanish']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertEquals("Ok, i guess", new Select(driver.findElement(By.id("like_us")))
                .getFirstSelectedOption().getText());
        assertEquals("There is room to improve.", driver.findElement(By.name("comment")).getAttribute("value"));
    }

    private void fillFeedback(String name, String age, List<String> languages, String gender,
                              String preference, String comment) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
        driver.findElement(By.id("fb_age")).sendKeys(age);
        for (String language : languages) {
            driver.findElement(By.cssSelector("#lang_check input[value='" + language + "']")).click();
        }
        driver.findElement(By.cssSelector("input[name='gender'][value='" + gender + "']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(preference);
        driver.findElement(By.name("comment")).sendKeys(comment);
    }

    private void submitFeedback() {
        driver.findElement(By.cssSelector("#fb_form button[type='submit']")).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.getCurrentUrl() != null
                        && webDriver.getCurrentUrl().contains("check_feedback.html"));
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.findElement(By.id("fb_thx")).isDisplayed());
    }

    private String summaryValue(String fieldId) {
        return driver.findElement(By.id(fieldId)).getText();
    }

    private void waitForNameSummaryValue(String expected) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> expected.equals(webDriver.findElement(By.id("name")).getText()));
    }

    private void waitForThankYouMessage(String expected) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> expected.equals(webDriver.findElement(By.id("message")).getText()));
        assertEquals(expected, driver.findElement(By.id("message")).getText());
    }
}