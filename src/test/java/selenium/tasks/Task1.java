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
    WebDriver driver;
    WebDriverWait wait;
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
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

        List<WebElement> languages = driver.findElements(By.cssSelector("input[name='language']"));
        for (WebElement language : languages) {
            assertFalse(language.isSelected());
        }

        List<WebElement> genders = driver.findElements(By.cssSelector("input[name='gender']"));
        assertFalse(genders.get(0).isSelected());
        assertFalse(genders.get(1).isSelected());
        assertTrue(genders.get(2).isSelected());

        Select likeUs = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", likeUs.getFirstSelectedOption().getText());

        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        String backgroundColor = sendButton.getCssValue("background-color").replace(" ", "");
        String textColor = sendButton.getCssValue("color").replace(" ", "");
        assertTrue(backgroundColor.contains("33") && backgroundColor.contains("150")
                        && backgroundColor.contains("243"),
                "Expected blue send button, but background was: " + backgroundColor);
        assertTrue(textColor.contains("255"),
                "Expected white send-button text, but color was: " + textColor);
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback"));

        assertEmptyOrNull(By.id("name"));
        assertEmptyOrNull(By.id("age"));
        assertEmptyOrNull(By.id("language"));
        assertEmptyOrNull(By.id("gender"));
        assertEmptyOrNull(By.id("option"));
        assertEmptyOrNull(By.id("comment"));
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillWholeForm("John Doe", "30", "English", "male", "Good", "Great experience");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback"));

        assertEquals("John Doe", driver.findElement(By.id("name")).getText());
        assertEquals("30", driver.findElement(By.id("age")).getText());
        assertTrue(driver.findElement(By.id("language")).getText().contains("English"));
        assertEquals("male", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("Great experience", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(By.id("fb_name")).sendKeys("Alice");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback"));

        driver.findElement(By.cssSelector("button.w3-green")).click();
        wait.until(d -> d.getCurrentUrl().contains("thank_you_for_feedback"));

        assertEquals("Thank you, Alice, for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback"));

        driver.findElement(By.cssSelector("button.w3-green")).click();
        wait.until(d -> d.getCurrentUrl().contains("thank_you_for_feedback"));

        assertEquals("Thank you for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm("Jane Doe", "28", "French", "female", "Bad", "Could be better");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback"));

        driver.findElement(By.cssSelector("button.w3-red")).click();
        wait.until(d -> d.getCurrentUrl().contains("provide_feedback"));

        assertEquals("Jane Doe", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("28", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='French']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='female']")).isSelected());
        assertEquals("Bad", new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("Could be better",
                driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }

    private void fillWholeForm(String name, String age, String language,
                               String gender, String option, String comment) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
        driver.findElement(By.id("fb_age")).sendKeys(age);
        driver.findElement(By.cssSelector("input[name='language'][value='" + language + "']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='" + gender + "']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(option);
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys(comment);
    }

    private void assertEmptyOrNull(By locator) {
        String text = driver.findElement(locator).getText().trim();
        assertTrue(text.isEmpty() || text.equalsIgnoreCase("null"),
                "Expected field to be empty or \"null\", but was: " + text);
    }
}