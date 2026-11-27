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
        driver.get(PAGE_URL);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement nameField() {
        return driver.findElement(By.id("fb_name"));
    }

    private WebElement ageField() {
        return driver.findElement(By.id("fb_age"));
    }

    private WebElement commentField() {
        return driver.findElement(By.cssSelector("textarea[name='comment']"));
    }

    private Select optionSelect() {
        return new Select(driver.findElement(By.id("like_us")));
    }

    private void sendFeedback() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_thx")));
    }

    private void fillWholeForm() {
        nameField().sendKeys("Janis");
        ageField().sendKeys("30");

        for (WebElement checkbox : driver.findElements(By.cssSelector("input[name='language']"))) {
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
        }

        driver.findElement(By.cssSelector("input[name='gender'][value='male']")).click();
        optionSelect().selectByVisibleText("Good");
        commentField().sendKeys("Excellent service");
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", nameField().getAttribute("value"));
        assertEquals("", ageField().getAttribute("value"));
        assertEquals("", commentField().getAttribute("value"));

        List<WebElement> checkboxes =
                driver.findElements(By.cssSelector("input[name='language'][type='checkbox']"));
        for (WebElement checkbox : checkboxes) {
            assertFalse(checkbox.isSelected());
        }

        List<WebElement> radioButtons =
                driver.findElements(By.cssSelector("input[name='gender'][type='radio']"));
        assertEquals(3, radioButtons.size());
        for (WebElement radioButton : radioButtons) {
            if (!radioButton.isEnabled()) {
                assertTrue(radioButton.isSelected());
            } else {
                assertFalse(radioButton.isSelected());
            }
        }
        WebElement unknownGender =
                driver.findElement(By.cssSelector("input[name='gender'][disabled]"));
        assertTrue(unknownGender.isSelected());

        assertEquals("Choose your option", optionSelect().getFirstSelectedOption().getText());

        WebElement sendButton =
                driver.findElement(By.cssSelector("button[type='submit']"));
        assertTrue(sendButton.getText().trim().equals("Send"));
        assertEquals("rgba(33, 150, 243, 1)",
                sendButton.getCssValue("background-color"));
        assertEquals("rgba(255, 255, 255, 1)",
                sendButton.getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        sendFeedback();

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
        sendFeedback();

        assertEquals("Janis", driver.findElement(By.id("name")).getText());
        assertEquals("30", driver.findElement(By.id("age")).getText());
        assertTrue(driver.findElement(By.id("language")).getText().contains("English"));
        assertTrue(driver.findElement(By.id("language")).getText().contains("French"));
        assertTrue(driver.findElement(By.id("language")).getText().contains("Spanish"));
        assertTrue(driver.findElement(By.id("language")).getText().contains("Chinese"));
        assertEquals("male", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("Excellent service", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        nameField().sendKeys("Janis");
        sendFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));
        assertEquals("Thank you, Janis, for your feedback!",
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message"))).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        sendFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));
        assertEquals("Thank you for your feedback!",
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message"))).getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm();
        sendFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();

        wait.until(ExpectedConditions.urlToBe(PAGE_URL));
        assertEquals("Janis", nameField().getAttribute("value"));
        assertEquals("30", ageField().getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='English']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='French']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertEquals("Good", optionSelect().getFirstSelectedOption().getText());
        assertEquals("Excellent service", commentField().getAttribute("value"));
    }
}