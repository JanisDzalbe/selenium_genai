package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtility;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    WebDriverWait wait;

    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(PAGE_URL);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement sendButton() {
        return driver.findElement(By.xpath("//button[@type='submit' and normalize-space()='Send']"));
    }

    private void submitFeedback() {
        sendButton().click();
        wait.until(d -> d.getCurrentUrl() != null
                && d.getCurrentUrl().contains("check_feedback.html"));
    }

    private String resultText(String id) {
        wait.until(d -> d.findElements(By.id(id)).size() == 1);
        return driver.findElement(By.id(id)).getText();
    }

    private void assertEmptyOrNull(String id) {
        String text = resultText(id);
        assertTrue(text.isBlank() || "null".equals(text),
                "Expected an empty or null value for " + id + ", but was '" + text + "'");
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));

        List<WebElement> languages = driver.findElements(By.cssSelector("input[name='language']"));
        languages.forEach(language -> assertFalse(language.isSelected()));

        WebElement male = driver.findElement(By.cssSelector("input[name='gender'][value='male']"));
        WebElement female = driver.findElement(By.cssSelector("input[name='gender'][value='female']"));
        WebElement unknown = driver.findElement(By.cssSelector("input[name='gender'][value='']"));
        assertFalse(male.isSelected());
        assertFalse(female.isSelected());
        assertTrue(unknown.isSelected());
        assertTrue(unknown.isDisplayed());
        assertFalse(unknown.isEnabled());

        Select options = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", options.getFirstSelectedOption().getText());
        assertEquals("", options.getFirstSelectedOption().getAttribute("value"));

        WebElement send = sendButton();
        assertTrue(send.getCssValue("background-color").matches("rgba?\\(33, 150, 243(?:, 1)?\\)")
                || send.getCssValue("background-color").contains("33, 150, 243"));
        assertTrue(send.getCssValue("color").matches("rgba?\\(255, 255, 255(?:, 1)?\\)")
                || send.getCssValue("color").contains("255, 255, 255"));
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
        String name = "John";
        String age = "30";
        String comment = "Great experience";

        driver.findElement(By.id("fb_name")).sendKeys(name);
        driver.findElement(By.id("fb_age")).sendKeys(age);
        driver.findElement(By.cssSelector("input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector("input[name='language'][value='French']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='male']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText("Good");
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys(comment);
        submitFeedback();

        assertEquals(name, resultText("name"));
        assertEquals(age, resultText("age"));
        assertTrue(resultText("language").contains("English"));
        assertTrue(resultText("language").contains("French"));
        assertEquals("male", resultText("gender"));
        assertEquals("Good", resultText("option"));
        assertEquals(comment, resultText("comment"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        driver.findElement(By.id("fb_name")).sendKeys("Alice");
        submitFeedback();

        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(d -> d.getCurrentUrl() != null
                && d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        wait.until(d -> d.findElement(By.id("message")).getText().equals("Thank you, Alice, for your feedback!"));
        assertEquals("Thank you, Alice, for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        submitFeedback();

        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(d -> d.getCurrentUrl() != null
                && d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        wait.until(d -> d.findElement(By.id("message")).getText().equals("Thank you for your feedback!"));
        assertEquals("Thank you for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() {
        driver.findElement(By.id("fb_name")).sendKeys("Bob");
        driver.findElement(By.id("fb_age")).sendKeys("42");
        driver.findElement(By.cssSelector("input[name='language'][value='Spanish']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='female']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText("Ok, i guess");
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys("Could be better");
        submitFeedback();

        assertEquals("Bob", resultText("name"));
        assertEquals("42", resultText("age"));
        assertEquals("Spanish", resultText("language"));
        assertEquals("female", resultText("gender"));
        assertEquals("Ok, i guess", resultText("option"));
        assertEquals("Could be better", resultText("comment"));

        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(d -> d.getCurrentUrl() != null
                && d.getCurrentUrl().contains("provide_feedback"));

        assertEquals("Bob", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("42", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='Spanish']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='female']")).isSelected());
        assertEquals("Ok, i guess",
                new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("Could be better",
                driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }
}