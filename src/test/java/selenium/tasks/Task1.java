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
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
        wait.until(d -> d.findElement(By.id("fb_name")).isDisplayed());
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void fillWholeForm() {
        driver.findElement(By.id("fb_name")).sendKeys("Janis");
        driver.findElement(By.id("fb_age")).sendKeys("30");
        driver.findElement(By.cssSelector("input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector("input[name='language'][value='French']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='male']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText("Good");
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys("Great example site");
    }

    private void submitForm() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));
        wait.until(d -> d.findElement(By.cssSelector("div#fb_thx")).isDisplayed());
    }

    private String summaryValue(String id) {
        return wait.until(d -> d.findElement(By.id(id)).getText());
    }

    private void assertEmptyOrNull(String id) {
        String value = summaryValue(id);
        assertTrue(value.isEmpty() || "null".equalsIgnoreCase(value),
                "Expected an empty or null value for " + id + ", but got: " + value);
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));

        for (WebElement checkbox : driver.findElements(By.cssSelector("input[name='language']"))) {
            assertFalse(checkbox.isSelected());
        }
        for (WebElement radio : driver.findElements(By.cssSelector("input[name='gender']"))) {
            if (radio.isEnabled()) {
                assertFalse(radio.isSelected());
            }
        }

        WebElement unknownGenre = driver.findElement(By.cssSelector("input[name='gender'][disabled][checked]"));
        assertTrue(unknownGenre.isSelected());
        assertFalse(unknownGenre.isEnabled());

        Select rating = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", rating.getFirstSelectedOption().getText());

        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        assertTrue(sendButton.getAttribute("class").contains("w3-blue"));
        String background = sendButton.getCssValue("background-color").toLowerCase(Locale.ROOT);
        String foreground = sendButton.getCssValue("color").toLowerCase(Locale.ROOT);
        assertTrue(background.contains("33, 150, 243") || background.contains("33,150,243"),
                "Send button should be blue, but its background is " + background);
        assertTrue(foreground.contains("255, 255, 255") || foreground.contains("255,255,255"),
                "Send button text should be white, but its color is " + foreground);
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        submitForm();
        assertEmptyOrNull("name");
        assertEmptyOrNull("age");
        assertEmptyOrNull("language");
        assertEmptyOrNull("gender");
        assertEmptyOrNull("option");
        assertEmptyOrNull("comment");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillWholeForm();
        submitForm();

        assertEquals("Janis", summaryValue("name"));
        assertEquals("30", summaryValue("age"));
        assertEquals("English,French", summaryValue("language"));
        assertEquals("male", summaryValue("gender"));
        assertEquals("Good", summaryValue("option"));
        assertEquals("Great example site", summaryValue("comment"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(By.id("fb_name")).sendKeys("Janis");
        submitForm();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        WebElement message = wait.until(d -> d.findElement(By.id("message")));
        wait.until(d -> message.getText().contains("Thank you"));
        assertEquals("Thank you, Janis, for your feedback!", message.getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        submitForm();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        WebElement message = wait.until(d -> d.findElement(By.id("message")));
        wait.until(d -> message.getText().contains("Thank you"));
        assertEquals("Thank you for your feedback!", message.getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm();
        submitForm();
        assertEquals("Janis", summaryValue("name"));

        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(d -> d.findElement(By.id("fb_name")).isDisplayed());

        assertEquals("Janis", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("30", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='English']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='French']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertEquals("Good", new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("Great example site",
                driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }
}