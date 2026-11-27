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
import selenium.utility.WebDriverUtility;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    WebDriverWait wait;

    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(FEEDBACK_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_form")));
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    private WebElement sendButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and normalize-space()='Send']")));
    }

    private void submitFeedback() {
        sendButton().click();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_thx")));
    }

    private void fillWholeForm() {
        driver.findElement(By.id("fb_name")).sendKeys("Alice");
        driver.findElement(By.id("fb_age")).sendKeys("30");

        driver.findElement(By.cssSelector("input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector("input[name='language'][value='French']")).click();

        driver.findElement(By.cssSelector("input[name='gender'][value='female']")).click();

        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText("Good");
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys("Excellent service");
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));

        List<WebElement> languages = driver.findElements(By.cssSelector("input[name='language']"));
        for (WebElement language : languages) {
            assertFalse(language.isSelected(), language.getAttribute("value") + " should not be selected");
        }

        List<WebElement> genders = driver.findElements(By.cssSelector("input[name='gender']"));
        for (WebElement gender : genders) {
            if (gender.getAttribute("value").isEmpty()) {
                assertTrue(gender.isSelected());
                assertTrue(gender.isDisplayed());
            } else {
                assertFalse(gender.isSelected());
            }
        }

        Select likeUs = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", likeUs.getFirstSelectedOption().getText());
        assertEquals("", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));

        WebElement send = sendButton();
        assertTrue(send.getCssValue("background-color").contains("33, 150, 243")
                || send.getCssValue("background-color").equals("rgb(33, 150, 243)"));
        assertTrue(send.getCssValue("color").equals("rgba(255, 255, 255, 1)")
                || send.getCssValue("color").equals("rgb(255, 255, 255)"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        submitFeedback();

        assertTrue(List.of("", "null").contains(driver.findElement(By.id("name")).getText()));
        assertTrue(List.of("", "null").contains(driver.findElement(By.id("age")).getText()));
        assertTrue(List.of("", "null").contains(driver.findElement(By.id("language")).getText()));
        assertTrue(List.of("", "null").contains(driver.findElement(By.id("gender")).getText()));
        assertTrue(List.of("", "null").contains(driver.findElement(By.id("option")).getText()));
        assertTrue(List.of("", "null").contains(driver.findElement(By.id("comment")).getText()));
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillWholeForm();
        submitFeedback();

        wait.until(ExpectedConditions.textToBe(By.id("name"), "Alice"));
        assertEquals("Alice", driver.findElement(By.id("name")).getText());
        assertEquals("30", driver.findElement(By.id("age")).getText());
        assertTrue(driver.findElement(By.id("language")).getText().contains("English"));
        assertTrue(driver.findElement(By.id("language")).getText().contains("French"));
        assertEquals("female", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("Excellent service", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(By.id("fb_name")).sendKeys("Alice");
        submitFeedback();

        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));
        wait.until(ExpectedConditions.textToBe(By.id("message"),
                "Thank you, Alice, for your feedback!"));
        assertEquals("Thank you, Alice, for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        submitFeedback();

        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));
        wait.until(ExpectedConditions.textToBe(By.id("message"),
                "Thank you for your feedback!"));
        assertEquals("Thank you for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm();
        submitFeedback();

        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(ExpectedConditions.urlContains("provide_feedback"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_form")));

        assertEquals("Alice", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("30", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='English']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='French']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='female']")).isSelected());
        assertEquals("Good", new Select(driver.findElement(By.id("like_us")))
                .getFirstSelectedOption().getText());
        assertEquals("Excellent service",
                driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }
}