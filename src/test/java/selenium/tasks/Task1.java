package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import selenium.utils.WebDriverUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() throws Exception {
        driver = WebDriverUtils.createChromeDriver();
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
        // Check that all fields are empty
        WebElement nameField = driver.findElement(By.id("fb_name"));
        assertEquals("", nameField.getAttribute("value"));

        WebElement ageField = driver.findElement(By.id("fb_age"));
        assertEquals("", ageField.getAttribute("value"));

        WebElement commentField = driver.findElement(By.name("comment"));
        assertEquals("", commentField.getAttribute("value"));

        // Check that no ticks (checkboxes) are clicked
        List<WebElement> checkboxes = driver.findElements(By.name("language"));
        for (WebElement checkbox : checkboxes) {
            assertFalse(checkbox.isSelected());
        }

        // Check "Don't know (Disabled)" is selected in "Genre" (radio buttons)
        List<WebElement> genreRadios = driver.findElements(By.name("gender"));
        WebElement selectedGenre = null;
        for (WebElement radio : genreRadios) {
            if (radio.isSelected()) {
                selectedGenre = radio;
                break;
            }
        }
        assertNotNull(selectedGenre, "A genre radio button should be selected");
        // The selected one is the disabled one with value=""
        assertEquals("", selectedGenre.getAttribute("value"));
        // To check the label, find the following label
        WebElement genreLabel = driver.findElement(By.xpath("//input[@name='gender' and @checked]/following-sibling::label"));
        assertEquals("Don't know (Disabled)", genreLabel.getText());

        // Check "Choose your option" in "How do you like us?"
        Select ratingSelect = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", ratingSelect.getFirstSelectedOption().getText());

        // Check that the button send is blue with white letters
        WebElement sendButton = driver.findElement(By.xpath("//button[text()='Send']"));
        String backgroundColor = sendButton.getCssValue("background-color");
        assertTrue(backgroundColor.equals("rgba(33, 150, 243, 1)"),
                   "Button background should be blue, but was: " + backgroundColor);

        String textColor = sendButton.getCssValue("color");
        assertTrue(textColor.equals("rgba(255, 255, 255, 1)"),
                   "Button text should be white, but was: " + textColor);
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        // Click "Send" without entering any data
        driver.findElement(By.xpath("//button[text()='Send']")).click();
        // Check fields are empty or "null"
        assertEquals("", driver.findElement(By.id("name")).getText());
        assertEquals("", driver.findElement(By.id("age")).getText());
        assertEquals("", driver.findElement(By.id("language")).getText());
        assertEquals("null", driver.findElement(By.id("gender")).getText());
        assertEquals("null", driver.findElement(By.id("option")).getText());
        assertEquals("", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        // Fill the whole form
        driver.findElement(By.id("fb_name")).sendKeys("Name");
        driver.findElement(By.id("fb_age")).sendKeys("20");
        // Select languages: English and French
        driver.findElement(By.xpath("//input[@value='English']")).click();
        driver.findElement(By.xpath("//input[@value='French']")).click();
        // Select genre: male
        driver.findElement(By.xpath("//input[@value='male']")).click();
        // Select option: Good
        Select ratingSelect = new Select(driver.findElement(By.id("like_us")));
        ratingSelect.selectByVisibleText("Good");
        // Enter comment
        driver.findElement(By.name("comment")).sendKeys("Some Comment");
        // Click "Send"
        driver.findElement(By.xpath("//button[text()='Send']")).click();
        // Check fields are filled correctly
        assertEquals("Name", driver.findElement(By.id("name")).getText());
        assertEquals("20", driver.findElement(By.id("age")).getText());
        assertEquals("English,French", driver.findElement(By.id("language")).getText());
        assertEquals("male", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("Some Comment", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // Enter only name
        driver.findElement(By.id("fb_name")).sendKeys("John");
        // Click "Send"
        driver.findElement(By.xpath("//button[text()='Send']")).click();
        // Click "Yes"
        driver.findElement(By.xpath("//button[text()='Yes']")).click();
        // Check message text: "Thank you, NAME, for your feedback!"
        assertTrue(driver.getPageSource().contains("Thank you, John, for your feedback!"));
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        // Click "Send" (without entering anything)
        driver.findElement(By.xpath("//button[text()='Send']")).click();
        // Click "Yes"
        driver.findElement(By.xpath("//button[text()='Yes']")).click();
        // Check message text: "Thank you for your feedback!"
        assertTrue(driver.getPageSource().contains("Thank you for your feedback!"));
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        // Fill the whole form
        driver.findElement(By.id("fb_name")).sendKeys("Name");
        driver.findElement(By.id("fb_age")).sendKeys("20");
        // Select languages: English and French
        driver.findElement(By.xpath("//input[@value='English']")).click();
        driver.findElement(By.xpath("//input[@value='French']")).click();
        // Select genre: male
        driver.findElement(By.xpath("//input[@value='male']")).click();
        // Select option: Good
        Select ratingSelect = new Select(driver.findElement(By.id("like_us")));
        ratingSelect.selectByVisibleText("Good");
        // Enter comment
        driver.findElement(By.name("comment")).sendKeys("Some Comment");
        // Click "Send"
        driver.findElement(By.xpath("//button[text()='Send']")).click();
        // Click "No"
        driver.findElement(By.xpath("//button[text()='No']")).click();
        // Check fields are filled correctly (back to form)
        assertEquals("Name", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("20", driver.findElement(By.id("fb_age")).getAttribute("value"));
        // Check languages selected
        assertTrue(driver.findElement(By.xpath("//input[@value='English']")).isSelected());
        assertTrue(driver.findElement(By.xpath("//input[@value='French']")).isSelected());
        // Check genre male selected
        assertTrue(driver.findElement(By.xpath("//input[@value='male']")).isSelected());
        // Check option Good selected
        Select ratingSelectAfter = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Good", ratingSelectAfter.getFirstSelectedOption().getText());
        // Check comment
        assertEquals("Some Comment", driver.findElement(By.name("comment")).getAttribute("value"));
    }
}