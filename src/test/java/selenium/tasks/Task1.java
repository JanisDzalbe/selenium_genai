package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.utility.WebDriverUtil;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtil.getChromeDriver();
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
        // Check that name field is empty
        WebElement nameField = driver.findElement(By.id("fb_name"));
        assertEquals("", nameField.getAttribute("value"), "Name field should be empty");

        // Check that age field is empty
        WebElement ageField = driver.findElement(By.id("fb_age"));
        assertEquals("", ageField.getAttribute("value"), "Age field should be empty");

        // Check that comment field is empty
        WebElement commentField = driver.findElement(By.name("comment"));
        assertEquals("", commentField.getAttribute("value"), "Comment field should be empty");

        // Check that no language checkboxes are selected
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='English']")).isSelected(),
                "English checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='French']")).isSelected(),
                "French checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']")).isSelected(),
                "Spanish checkbox should not be selected");
        assertFalse(driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']")).isSelected(),
                "Chinese checkbox should not be selected");

        // Check that "Don't know" is selected in Genre
        WebElement dontKnowRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='' and @disabled]"));
        assertTrue(dontKnowRadio.isSelected(), "'Don't know' should be selected in Genre");

        // Check that "Choose your option" is selected in "How do you like us?"
        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        String selectedOption = likeUsSelect.getAttribute("value");
        assertEquals("", selectedOption, "'Choose your option' should be selected");

        // Check that the Send button is blue with white letters
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        String buttonColor = sendButton.getCssValue("background-color");
        String textColor = sendButton.getCssValue("color");

        // Check if button has blue background (could be rgb or hex)
        assertTrue(buttonColor.contains("blue") || buttonColor.equals("rgba(33, 150, 243, 1)") || buttonColor.equals("#2196F3"),
                "Send button should have blue background, but was: " + buttonColor);

        // Check if text is white
        assertTrue(textColor.contains("white") || textColor.equals("rgba(255, 255, 255, 1)") || textColor.equals("#ffffff"),
                "Send button text should be white, but was: " + textColor);
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        // Click "Send" without entering any data
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for the confirmation page to load
        Thread.sleep(1000); // Simple wait, in production use WebDriverWait

        // Check that fields are empty or "null" on the confirmation page
        assertEquals("", driver.findElement(By.id("name")).getText(), "Name should be empty");
        assertEquals("", driver.findElement(By.id("age")).getText(), "Age should be empty");
        assertEquals("", driver.findElement(By.id("language")).getText(), "Language should be empty");
        assertEquals("null", driver.findElement(By.id("gender")).getText(), "Gender should be null");
        assertEquals("null", driver.findElement(By.id("option")).getText(), "Option should be null");
        assertEquals("", driver.findElement(By.id("comment")).getText(), "Comment should be empty");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        // Fill the whole form
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Sonakshi");

        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("31");

        // Select English language
        WebElement englishCheckbox = driver.findElement(By.xpath("//input[@name='language' and @value='English']"));
        englishCheckbox.click();

        // Select Female gender
        WebElement femaleRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='female']"));
        femaleRadio.click();

        // Select "Good" option
        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        likeUsSelect.sendKeys("Good");

        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("Great service!");

        // Click "Send"
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for the confirmation page to load
        Thread.sleep(1000); // Simple wait, in production use WebDriverWait

        // Check fields are filled correctly on the confirmation page
        assertEquals("Sonakshi", driver.findElement(By.id("name")).getText(), "Name should be 'Sonakshi'");
        assertEquals("31", driver.findElement(By.id("age")).getText(), "Age should be '31'");
        assertEquals("English", driver.findElement(By.id("language")).getText(), "Language should be 'English'");
        assertEquals("female", driver.findElement(By.id("gender")).getText(), "Gender should be 'female'");
        assertEquals("Good", driver.findElement(By.id("option")).getText(), "Option should be 'Good'");
        assertEquals("Great service!", driver.findElement(By.id("comment")).getText(), "Comment should be 'Great service!'");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // Enter only name
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Sonakshi");

        // Click "Send"
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for the confirmation page to load
        Thread.sleep(1000); // Simple wait, in production use WebDriverWait

        // Click "Yes"
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        yesButton.click();

        // Wait for the feedback message to appear
        Thread.sleep(500); // Simple wait, in production use WebDriverWait

        // Check message text: "Thank you, NAME, for your feedback!"
        WebElement messageElement = driver.findElement(By.id("message"));
        String expectedMessage = "Thank you, Sonakshi, for your feedback!";
        assertEquals(expectedMessage, messageElement.getText(), "Message should be '" + expectedMessage + "'");
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        // TODO:
        //  click "Send" (without entering anything)
        //  click "Yes"
        //  check message text: "Thank you for your feedback!"
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        // TODO:
        //  fill the whole form
        //  click "Send"
        //  click "No"
        //  check fields are filled correctly
    }
}