package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = selenium.utility.DriverManager.initializeChromeDriver();
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() {
        // Text fields should be empty
        WebElement nameField = driver.findElement(By.id("fb_name"));
        WebElement ageField = driver.findElement(By.id("fb_age"));
        WebElement commentField = driver.findElement(By.name("comment"));

        assertEquals("", nameField.getAttribute("value"));
        assertEquals("", ageField.getAttribute("value"));
        assertEquals("", commentField.getAttribute("value"));

        // Language checkboxes should not be selected
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected());
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='French']")).isSelected());
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).isSelected());
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).isSelected());

        // "Don't know" radio should be selected and disabled
        WebElement dontKnowRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='']"));
        assertTrue(dontKnowRadio.isSelected());
        assertFalse(dontKnowRadio.isEnabled());

        // Default dropdown option should be selected
        Select likeUsSelect = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", likeUsSelect.getFirstSelectedOption().getText());

        // Send button should have blue class and white text
        WebElement sendButton = driver.findElement(By.xpath("//button[text()='Send']"));

        assertTrue(sendButton.getAttribute("class").contains("w3-blue"));
        assertEquals("rgba(255, 255, 255, 1)", sendButton.getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() {
        driver.findElement(By.xpath("//button[text()='Send']")).click();

        String url = driver.getCurrentUrl();

        assertTrue(url.contains("check_feedback"));
    }

    @Test
    public void notEmptyFeedbackPage() {

        // TODO:
        //  fill the whole form, click "Send"
        //  check fields are filled correctly

        driver.findElement(By.id("fb_name")).sendKeys("Ann");
        driver.findElement(By.id("fb_age")).sendKeys("25");
        driver.findElement(By.xpath("//input[@name='language' and @value='English']")).click();
        driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).click();
        driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).click();

        Select likeUsSelect = new Select(driver.findElement(By.id("like_us")));
        likeUsSelect.selectByVisibleText("Good");

        driver.findElement(By.name("comment")).sendKeys("Nice page");
        driver.findElement(By.xpath("//button[text()='Send']")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        String url = driver.getCurrentUrl();

        assertTrue(url.contains("Ann"));
        assertTrue(url.contains("25"));
        assertTrue(url.contains("English"));
        assertTrue(url.contains("Spanish"));
        assertTrue(url.contains("female"));
        assertTrue(url.contains("Good"));
        assertTrue(url.contains("Nice+page") || url.contains("Nice%20page"));
    }

    @Test
    public void yesOnWithNameFeedbackPage() {

        // TODO:
        //  enter only name
        //  click "Send"
        //  click "Yes"
        //  check message text: "Thank you, NAME, for your feedback!"

        String name = "Ann";

        driver.findElement(By.id("fb_name")).sendKeys(name);
        driver.findElement(By.xpath("//button[text()='Send']")).click();

        driver.findElement(By.xpath("//button[text()='Yes']")).click();

        assertTrue(driver.getPageSource().contains("Thank you, " + name + ", for your feedback!"));
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {

        // TODO:
        //  click "Send" (without entering anything)
        //  click "Yes"
        //  check message text: "Thank you for your feedback!"

        driver.findElement(By.xpath("//button[text()='Send']")).click();

        driver.findElement(By.xpath("//button[text()='Yes']")).click();

        assertTrue(driver.getPageSource().contains("Thank you for your feedback!"));
    }

    @Test
    public void noOnFeedbackPage() {

        // TODO:
        //  fill the whole form
        //  click "Send"
        //  click "No"
        //  check fields are filled correctly

        driver.findElement(By.id("fb_name")).sendKeys("Ann");
        driver.findElement(By.id("fb_age")).sendKeys("25");
        driver.findElement(By.xpath("//input[@name='language' and @value='English']")).click();
        driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).click();

        Select likeUsSelect = new Select(driver.findElement(By.id("like_us")));
        likeUsSelect.selectByVisibleText("Good");

        driver.findElement(By.name("comment")).sendKeys("Nice page");
        driver.findElement(By.xpath("//button[text()='Send']")).click();

        driver.findElement(By.xpath("//button[text()='No']")).click();

        assertEquals("Ann", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("25", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected());
        assertTrue(driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).isSelected());
        assertEquals("Good", new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("Nice page", driver.findElement(By.name("comment")).getAttribute("value"));
    }
}