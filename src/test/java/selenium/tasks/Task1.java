package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static selenium.utility.WebDriverUtils.createChromeDriver;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = createChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
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
        assertTrue(driver.findElements(By.cssSelector("input[name='language']:checked")).isEmpty());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][disabled]")).isSelected());
        assertEquals("Choose your option",
                new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
        WebElement send = driver.findElement(By.cssSelector("button[type='submit']"));
        assertEquals("Send", send.getText());
        assertEquals("rgba(33, 150, 243, 1)", send.getCssValue("background-color"));
        assertEquals("rgba(255, 255, 255, 1)", send.getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForUrl("check_feedback.html");
        assertEquals("", driver.findElement(By.id("name")).getText());
        assertEquals("", driver.findElement(By.id("age")).getText());
        assertEquals("", driver.findElement(By.id("language")).getText());
        assertEquals("null", driver.findElement(By.id("gender")).getText());
        assertEquals("null", driver.findElement(By.id("option")).getText());
        assertEquals("", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillForm("Jane Doe", "30", "English", "female", "Good", "Excellent service");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForUrl("check_feedback.html");
        assertEquals("Jane Doe", driver.findElement(By.id("name")).getText());
        assertEquals("30", driver.findElement(By.id("age")).getText());
        assertEquals("English", driver.findElement(By.id("language")).getText());
        assertEquals("female", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("Excellent service", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(By.id("fb_name")).sendKeys("Jane");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForUrl("check_feedback.html");
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        waitForUrl("thank_you_for_feedback.html");
        assertEquals("Thank you, Jane, for your feedback!",
                waitVisible(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForUrl("check_feedback.html");
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        waitForUrl("thank_you_for_feedback.html");
        assertEquals("Thank you for your feedback!", waitVisible(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillForm("John Smith", "25", "French", "male", "Ok, i guess", "Could be better");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForUrl("check_feedback.html");
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        waitForUrl("provide_feedback");
        assertEquals("John Smith", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("25", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='French']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertEquals("Ok, i guess",
                new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("Could be better",
                driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }

    private void fillForm(String name, String age, String language, String gender,
                          String option, String comment) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
        driver.findElement(By.id("fb_age")).sendKeys(age);
        driver.findElement(By.cssSelector("input[name='language'][value='" + language + "']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='" + gender + "']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(option);
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys(comment);
    }

    private void waitForUrl(String fragment) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains(fragment));
    }

    private WebElement waitVisible(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}