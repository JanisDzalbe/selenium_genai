package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverFactory;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = ChromeDriverFactory.createDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));

        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected());
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='French']")).isSelected());
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).isSelected());
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).isSelected());
        assertFalse(driver.findElement(By.xpath("//input[@name='gender' and @value='male']")).isSelected());
        assertFalse(driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).isSelected());
        assertTrue(driver.findElement(By.xpath("//input[@name='gender' and @disabled]")).isSelected());

        Select likeUs = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", likeUs.getFirstSelectedOption().getText());

        String buttonColor = driver.findElement(By.xpath("//button[text()='Send']")).getCssValue("background-color");
        String textColor = driver.findElement(By.xpath("//button[text()='Send']")).getCssValue("color");
        assertTrue(buttonColor.contains("33, 150, 243"));
        assertTrue(textColor.contains("255, 255, 255"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        clickSend();
        waitForCheckFeedbackPage();

        assertEmptyOrNull(driver.findElement(By.id("name")).getText());
        assertEmptyOrNull(driver.findElement(By.id("age")).getText());
        assertEmptyOrNull(driver.findElement(By.id("language")).getText());
        assertEmptyOrNull(driver.findElement(By.id("gender")).getText());
        assertEmptyOrNull(driver.findElement(By.id("option")).getText());
        assertEmptyOrNull(driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillWholeForm();
        clickSend();
        waitForCheckFeedbackPage();

        assertEquals("Anna", driver.findElement(By.id("name")).getText());
        assertEquals("22", driver.findElement(By.id("age")).getText());
        assertTrue(driver.findElement(By.id("language")).getText().contains("English"));
        assertTrue(driver.findElement(By.id("language")).getText().contains("French"));
        assertEquals("female", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("Very good page", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(By.id("fb_name")).sendKeys("Anna");
        clickSend();
        waitForCheckFeedbackPage();
        clickYes();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Thank you, Anna, for your feedback!", driver.findElement(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        clickSend();
        waitForCheckFeedbackPage();
        clickYes();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Thank you for your feedback!", driver.findElement(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm();
        clickSend();
        waitForCheckFeedbackPage();
        clickNo();

        wait.until(ExpectedConditions.urlContains("provide_feedback"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_name")));

        assertEquals("Anna", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("22", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected());
        assertTrue(driver.findElement(By.xpath("//input[@name='language' and @value='French']")).isSelected());
        assertTrue(driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).isSelected());
        assertEquals("Good", new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("Very good page", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }

    private void fillWholeForm() {
        driver.findElement(By.id("fb_name")).sendKeys("Anna");
        driver.findElement(By.id("fb_age")).sendKeys("22");
        driver.findElement(By.xpath("//input[@name='language' and @value='English']")).click();
        driver.findElement(By.xpath("//input[@name='language' and @value='French']")).click();
        driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText("Good");
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys("Very good page");
    }

    private void clickSend() {
        driver.findElement(By.xpath("//button[text()='Send']")).click();
    }

    private void clickYes() {
        driver.findElement(By.xpath("//button[text()='Yes']")).click();
    }

    private void clickNo() {
        driver.findElement(By.xpath("//button[text()='No']")).click();
    }

    private void waitForCheckFeedbackPage() {
        wait.until(ExpectedConditions.urlContains("check_feedback"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));
    }

    private void assertEmptyOrNull(String text) {
        assertTrue(text.isEmpty() || text.equals("null"));
    }
}
