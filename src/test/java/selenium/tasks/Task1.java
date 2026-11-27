package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverUtility;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Task1 {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(PAGE_URL);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));

        for (WebElement checkbox : driver.findElements(By.cssSelector("input[name='language']"))) {
            assertFalse(checkbox.isSelected());
        }

        WebElement unknownGender = driver.findElement(By.cssSelector("input[name='gender'][disabled]"));
        assertTrue(unknownGender.isSelected());
        assertFalse(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertFalse(driver.findElement(By.cssSelector("input[name='gender'][value='female']")).isSelected());

        Select option = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", option.getFirstSelectedOption().getText());

        WebElement send = driver.findElement(By.cssSelector("button[type='submit']"));
        assertEquals("rgba(33, 150, 243, 1)", send.getCssValue("background-color"));
        assertEquals("rgba(255, 255, 255, 1)", send.getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("check_feedback.html"));

        assertNullOrEmpty(By.id("name"));
        assertNullOrEmpty(By.id("age"));
        assertNullOrEmpty(By.id("language"));
        assertNullOrEmpty(By.id("gender"));
        assertNullOrEmpty(By.id("option"));
        assertNullOrEmpty(By.id("comment"));
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillWholeForm();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("check_feedback.html"));

        assertEquals("Janis", waitForText(By.id("name")));
        assertEquals("30", waitForText(By.id("age")));
        assertEquals("English,French", waitForText(By.id("language")));
        assertEquals("male", waitForText(By.id("gender")));
        assertEquals("Good", waitForText(By.id("option")));
        assertEquals("Automated Selenium feedback", waitForText(By.id("comment")));
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(By.id("fb_name")).sendKeys("Janis");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("check_feedback.html"));

        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("thank_you_for_feedback.html"));
        assertEquals("Thank you, Janis, for your feedback!",
                waitForText(By.id("message")));
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("check_feedback.html"));

        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("thank_you_for_feedback.html"));
        assertEquals("Thank you for your feedback!", waitForText(By.id("message")));
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("check_feedback.html"));

        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().equals(PAGE_URL));

        assertEquals("Janis", driver.findElement(By.id("fb_name")).getDomProperty("value"));
        assertEquals("30", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("Automated Selenium feedback",
                driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='English']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='French']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertEquals("Good", new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
    }

    private void fillWholeForm() {
        driver.findElement(By.id("fb_name")).sendKeys("Janis");
        driver.findElement(By.id("fb_age")).sendKeys("30");
        driver.findElement(By.cssSelector("input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector("input[name='language'][value='French']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='male']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText("Good");
        driver.findElement(By.cssSelector("textarea[name='comment']"))
                .sendKeys("Automated Selenium feedback");
    }

    private void assertNullOrEmpty(By locator) {
        String value = waitForText(locator);
        assertTrue(value.isEmpty() || value.equalsIgnoreCase("null"),
                "Expected a null or empty value, but was '" + value + "'");
    }

    private String waitForText(By locator) {
        return wait.until(webDriver -> {
            WebElement element = webDriver.findElement(locator);
            return element.getText();
        });
    }
}
