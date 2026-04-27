package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import selenium.utility.DriverFactory;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = DriverFactory.getDriver();
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
        WebElement nameField = driver.findElement(By.id("fb_name"));
        assertEquals("", nameField.getAttribute("value"), "Name field should be empty");
        WebElement ageField = driver.findElement(By.id("fb_age"));
        assertEquals("", ageField.getAttribute("value"), "Age field should be empty");
        WebElement commentField = driver.findElement(By.name("comment"));
        assertEquals("", commentField.getAttribute("value"), "Comment field should be empty");

        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected(), "English checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='French']")).isSelected(), "French checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).isSelected(), "Spanish checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).isSelected(), "Chinese checkbox should not be selected");

        WebElement dontKnowRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='']"));
        assertTrue(dontKnowRadio.isSelected(), "'Don't know' should be selected");

        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        String selectedOption = likeUsSelect.getAttribute("value");
        assertEquals("", selectedOption, "'Choose your option' should be selected");

        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        String backgroundColor = sendButton.getCssValue("background-color");
        String color = sendButton.getCssValue("color");
        assertEquals("rgba(33, 150, 243, 1)", backgroundColor);
        assertEquals("rgba(255, 255, 255, 1)", color, "Button text should be white");
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("", nameSpan.getText(), "Name should be empty on confirmation page");

        WebElement ageSpan = driver.findElement(By.id("age"));
        assertEquals("", ageSpan.getText(), "Age should be empty on confirmation page");

        WebElement genderSpan = driver.findElement(By.id("gender"));
        assertEquals("null", genderSpan.getText(), "Gender should be null on confirmation page");

        WebElement optionSpan = driver.findElement(By.id("option"));
        assertEquals("null", optionSpan.getText(), "Option should be null on confirmation page");

        WebElement commentSpan = driver.findElement(By.id("comment"));
        assertEquals("", commentSpan.getText(), "Comment should be empty on confirmation page");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Jana");

        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("22");

        driver.findElement(By.xpath("//input[@name='language' and @value='English']")).click();
        driver.findElement(By.xpath("//input[@name='language' and @value='French']")).click();

        driver.findElement(By.xpath("//input[@name='gender' and @value='male']")).click();

        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        likeUsSelect.findElement(By.xpath(".//option[@value='Good']")).click();

        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("Nice");

        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("Jana", nameSpan.getText(), "Name should be displayed on confirmation page");

        WebElement ageSpan = driver.findElement(By.id("age"));
        assertEquals("22", ageSpan.getText(), "Age should be displayed on confirmation page");

        WebElement languageSpan = driver.findElement(By.id("language"));
        assertTrue(languageSpan.getText().contains("English") && languageSpan.getText().contains("French"), "Selected languages should be displayed on confirmation page");

        WebElement genderSpan = driver.findElement(By.id("gender"));
        assertEquals("male", genderSpan.getText(), "Gender should be displayed on confirmation page");

        WebElement optionSpan = driver.findElement(By.id("option"));
        assertEquals("Good", optionSpan.getText(), "Option should be displayed on confirmation page");

        WebElement commentSpan = driver.findElement(By.id("comment"));
        assertEquals("Nice", commentSpan.getText(), "Comment should be displayed on confirmation page");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Jana");

        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("Jana", nameSpan.getText(), "Name should be displayed on confirmation page");

        WebElement yesButton = driver.findElement(By.xpath("//button[contains(text(), 'Yes')]"));
        yesButton.click();

        WebElement messageElement = driver.findElement(By.xpath("//*[contains(text(), 'Thank you, Jana, for your feedback!')]"));
        assertNotNull(messageElement, "Thank you message with name should be displayed");
        assertEquals("Thank you, Jana, for your feedback!", messageElement.getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("", nameSpan.getText(), "Name should be empty on confirmation page");

        WebElement yesButton = driver.findElement(By.xpath("//button[contains(text(), 'Yes')]"));
        yesButton.click();

        WebElement messageElement = driver.findElement(By.xpath("//*[contains(text(), 'Thank you for your feedback!')]"));
        assertNotNull(messageElement, "Thank you message should be displayed");
        assertEquals("Thank you for your feedback!", messageElement.getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Jana");

        // Fill age
        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("23");

        // Select languages
        driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).click();
        driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).click();

        driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).click();

        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        likeUsSelect.findElement(By.xpath(".//option[@value='Ok, i guess']")).click();

        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("it was okay");

        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("Jana", nameSpan.getText(), "Name should be displayed on confirmation page");

        WebElement ageSpan = driver.findElement(By.id("age"));
        assertEquals("23", ageSpan.getText(), "Age should be displayed on confirmation page");

        WebElement noButton = driver.findElement(By.xpath("//button[contains(text(), 'No')]"));
        noButton.click();

        nameField = driver.findElement(By.id("fb_name"));
        assertEquals("Jana", nameField.getAttribute("value"), "Name field should still be filled");

        ageField = driver.findElement(By.id("fb_age"));
        assertEquals("23", ageField.getAttribute("value"), "Age field should still be filled");
        assertTrue(driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).isSelected(), "Spanish checkbox should be selected");
        assertTrue(driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).isSelected(), "Chinese checkbox should be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected(), "English checkbox should not be selected");
        assertTrue(driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).isSelected(), "Female radio should be selected");

        commentField = driver.findElement(By.name("comment"));
        assertEquals("it was okay", commentField.getAttribute("value"), "Comment field should still be filled");
    }
}