package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import selenium.WebDriverFactory;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverFactory.createChromeDriver();
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
        // Check that all fields are empty
        WebElement nameField = driver.findElement(By.id("fb_name"));
        assertEquals("", nameField.getAttribute("value"), "Name field should be empty");

        WebElement ageField = driver.findElement(By.id("fb_age"));
        assertEquals("", ageField.getAttribute("value"), "Age field should be empty");

        WebElement commentField = driver.findElement(By.name("comment"));
        assertEquals("", commentField.getAttribute("value"), "Comment field should be empty");

        // Check no checkboxes are ticked
        // Language checkboxes
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected(), "English checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='French']")).isSelected(), "French checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).isSelected(), "Spanish checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).isSelected(), "Chinese checkbox should not be selected");

        // "Don't know" is selected in "Genre"
        WebElement dontKnowRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='']"));
        assertTrue(dontKnowRadio.isSelected(), "'Don't know' should be selected");

        // "Choose your option" in "How do you like us?"
        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        String selectedOption = likeUsSelect.getAttribute("value");
        assertEquals("", selectedOption, "'Choose your option' should be selected");

        // Check that the button send is blue with white letters
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        String backgroundColor = sendButton.getCssValue("background-color");
        String color = sendButton.getCssValue("color");
        // w3-blue is rgb(33, 150, 243) or similar blue
        assertEquals("rgba(33, 150, 243, 1)", backgroundColor);
        assertEquals("rgba(255, 255, 255, 1)", color, "Button text should be white");
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        // TODO:
        //  click "Send" without entering any data
        //  check fields are empty or "null"
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(1000);

        // Check that confirmation page shows empty/null values
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
        // TODO:
        //  fill the whole form, click "Send"
        //  check fields are filled correctly
        // Fill name
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("John Doe");

        // Fill age
        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("30");

        // Select languages
        driver.findElement(By.xpath("//input[@name='language' and @value='English']")).click();
        driver.findElement(By.xpath("//input[@name='language' and @value='French']")).click();

        // Select gender
        driver.findElement(By.xpath("//input[@name='gender' and @value='male']")).click();

        // Select from dropdown
        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        likeUsSelect.findElement(By.xpath(".//option[@value='Good']")).click();

        // Fill comment
        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("Great website!");

        // Click Send
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(1000);

        // Verify fields are filled correctly on confirmation page
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("John Doe", nameSpan.getText(), "Name should be displayed on confirmation page");

        WebElement ageSpan = driver.findElement(By.id("age"));
        assertEquals("30", ageSpan.getText(), "Age should be displayed on confirmation page");

        WebElement languageSpan = driver.findElement(By.id("language"));
        assertTrue(languageSpan.getText().contains("English") && languageSpan.getText().contains("French"), "Selected languages should be displayed on confirmation page");

        WebElement genderSpan = driver.findElement(By.id("gender"));
        assertEquals("male", genderSpan.getText(), "Gender should be displayed on confirmation page");

        WebElement optionSpan = driver.findElement(By.id("option"));
        assertEquals("Good", optionSpan.getText(), "Option should be displayed on confirmation page");

        WebElement commentSpan = driver.findElement(By.id("comment"));
        assertEquals("Great website!", commentSpan.getText(), "Comment should be displayed on confirmation page");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // TODO:
        //  enter only name
        //  click "Send"
        //  click "Yes"
        //  check message text: "Thank you, NAME, for your feedback!"
        // Fill name only
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Alice");

        // Click Send to navigate to confirmation page
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(1000);

        // Verify confirmation page shows the name
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("Alice", nameSpan.getText(), "Name should be displayed on confirmation page");

        // Click Yes to confirm and submit
        WebElement yesButton = driver.findElement(By.xpath("//button[contains(text(), 'Yes')]"));
        yesButton.click();

        // Wait for thank you message
        Thread.sleep(1000);

        // Check for thank you message with name
        WebElement messageElement = driver.findElement(By.xpath("//*[contains(text(), 'Thank you, Alice, for your feedback!')]"));
        assertNotNull(messageElement, "Thank you message with name should be displayed");
        assertEquals("Thank you, Alice, for your feedback!", messageElement.getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        // TODO:
        //  click "Send" (without entering anything)
        //  click "Yes"
        //  check message text: "Thank you for your feedback!"
        // Click Send without entering any data
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(1000);

        // Verify confirmation page shows empty/null values
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("", nameSpan.getText(), "Name should be empty on confirmation page");

        // Click Yes to confirm and submit
        WebElement yesButton = driver.findElement(By.xpath("//button[contains(text(), 'Yes')]"));
        yesButton.click();

        // Wait for thank you message
        Thread.sleep(1000);

        // Check for thank you message without name
        WebElement messageElement = driver.findElement(By.xpath("//*[contains(text(), 'Thank you for your feedback!')]"));
        assertNotNull(messageElement, "Thank you message should be displayed");
        assertEquals("Thank you for your feedback!", messageElement.getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        // TODO:
        //  fill the whole form
        //  click "Send"
        //  click "No"
        //  check fields are filled correctly
        // Fill name
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Bob Smith");

        // Fill age
        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("25");

        // Select languages
        driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).click();
        driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).click();

        // Select gender
        driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).click();

        // Select from dropdown
        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        likeUsSelect.findElement(By.xpath(".//option[@value='Ok, i guess']")).click();

        // Fill comment
        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("Nice experience!");

        // Click Send to navigate to confirmation page
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(1000);

        // Verify confirmation page shows filled data
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("Bob Smith", nameSpan.getText(), "Name should be displayed on confirmation page");

        WebElement ageSpan = driver.findElement(By.id("age"));
        assertEquals("25", ageSpan.getText(), "Age should be displayed on confirmation page");

        // Click No to return to form
        WebElement noButton = driver.findElement(By.xpath("//button[contains(text(), 'No')]"));
        noButton.click();

        // Wait for page to return to form
        Thread.sleep(1000);

        // Verify fields are still filled on the form page after clicking No
        nameField = driver.findElement(By.id("fb_name"));
        assertEquals("Bob Smith", nameField.getAttribute("value"), "Name field should still be filled");

        ageField = driver.findElement(By.id("fb_age"));
        assertEquals("25", ageField.getAttribute("value"), "Age field should still be filled");

        assertTrue(driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).isSelected(), "Spanish checkbox should be selected");
        assertTrue(driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).isSelected(), "Chinese checkbox should be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected(), "English checkbox should not be selected");

        assertTrue(driver.findElement(By.xpath("//input[@name='gender' and @value='female']")).isSelected(), "Female radio should be selected");

        commentField = driver.findElement(By.name("comment"));
        assertEquals("Nice experience!", commentField.getAttribute("value"), "Comment field should still be filled");
    }
}