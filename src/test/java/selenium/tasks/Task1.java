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
        driver = DriverFactory.createChromeDriver();
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

        // Check that no language checkboxes are ticked
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.xpath("//input[@name='language']"));
        for (WebElement checkbox : languageCheckboxes) {
            assertFalse(checkbox.isSelected(), "Language checkbox should not be selected");
        }

        // Check that "Don't know" is selected in "Genre" (the disabled radio button)
        WebElement dontKnowRadio = driver.findElement(By.xpath("//input[@name='gender'][@disabled]"));
        assertTrue(dontKnowRadio.isSelected(), "'Don't know' option should be selected in Genre");

        // Check that "Choose your option" is selected in "How do you like us?"
        WebElement likeUsDropdown = driver.findElement(By.id("like_us"));
        assertEquals("", likeUsDropdown.getAttribute("value"), "Dropdown should have empty value (default 'Choose your option')");

        // Check that the button is blue with white letters
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        String buttonClass = sendButton.getAttribute("class");
        assertNotNull(buttonClass, "Send button should have class attribute");
        assertTrue(buttonClass.contains("w3-blue"), "Send button should be blue");
        assertEquals("Send", sendButton.getText(), "Send button should have correct text");
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        // Click "Send" without entering any data
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for page to load and check fields are empty or "null"
        Thread.sleep(1000);

        // Check name field is empty
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("", nameSpan.getText(), "Name should be empty");

        // Check age field is empty
        WebElement ageSpan = driver.findElement(By.id("age"));
        assertEquals("", ageSpan.getText(), "Age should be empty");

        // Check language field is empty
        WebElement languageSpan = driver.findElement(By.id("language"));
        assertEquals("", languageSpan.getText(), "Language should be empty");

        // Check gender field shows "null"
        WebElement genderSpan = driver.findElement(By.id("gender"));
        assertEquals("null", genderSpan.getText(), "Gender should be null");

        // Check option field shows "null"
        WebElement optionSpan = driver.findElement(By.id("option"));
        assertEquals("null", optionSpan.getText(), "Option should be null");

        // Check comment field is empty
        WebElement commentSpan = driver.findElement(By.id("comment"));
        assertEquals("", commentSpan.getText(), "Comment should be empty");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        // Fill the whole form
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Priyanka");

        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("30");

        // Select English and French language checkboxes
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.xpath("//input[@name='language']"));
        languageCheckboxes.get(0).click(); // English
        languageCheckboxes.get(1).click(); // French

        // Select Female gender
        java.util.List<WebElement> genderRadios = driver.findElements(By.xpath("//input[@name='gender'][@value!='']"));
        genderRadios.get(1).click(); // Female

        // Select "Good" from dropdown
        WebElement likeUsDropdown = driver.findElement(By.id("like_us"));
        likeUsDropdown.click();
        WebElement goodOption = driver.findElement(By.xpath("//select[@id='like_us']/option[@value='Good']"));
        goodOption.click();

        // Enter comment
        WebElement commentField = driver.findElement(By.xpath("//textarea[@name='comment']"));
        commentField.sendKeys("It's Good!");

        // Click Send
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for page to load
        Thread.sleep(1000);

        // Check fields are filled correctly
        assertEquals("Priyanka", driver.findElement(By.id("name")).getText(), "Name should be Priyanka");
        assertEquals("30", driver.findElement(By.id("age")).getText(), "Age should be 30");
        assertEquals("English,French", driver.findElement(By.id("language")).getText(), "Language should be English,French");
        assertEquals("female", driver.findElement(By.id("gender")).getText(), "Gender should be female");
        assertEquals("Good", driver.findElement(By.id("option")).getText(), "Option should be Good");
        assertEquals("It's Good!", driver.findElement(By.id("comment")).getText(), "Comment should be It's Good!");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // Enter only name
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("John");

        // Click "Send"
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for page to load
        Thread.sleep(1000);

        // Click "Yes"
        WebElement yesButton = driver.findElement(By.xpath("//button[@class='w3-btn w3-green w3-xlarge']"));
        yesButton.click();

        // Wait for success message to appear
        Thread.sleep(1000);

        // Check message text contains the name
        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains("Thank you, John, for your feedback!"),
            "Message should contain 'Thank you, John, for your feedback!'");
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        // Click "Send" without entering anything
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for page to load
        Thread.sleep(1000);

        // Click "Yes"
        WebElement yesButton = driver.findElement(By.xpath("//button[@class='w3-btn w3-green w3-xlarge']"));
        yesButton.click();

        // Wait for success message to appear
        Thread.sleep(1000);

        // Check message text is generic
        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertTrue(bodyText.contains("Thank you for your feedback!"),
            "Message should contain 'Thank you for your feedback!'");
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        // Fill the whole form
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Alice");

        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("25");

        // Select English language checkbox
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.xpath("//input[@name='language']"));
        languageCheckboxes.get(0).click(); // English

        // Select Male gender
        java.util.List<WebElement> genderRadios = driver.findElements(By.xpath("//input[@name='gender'][@value!='']"));
        genderRadios.get(0).click(); // Male

        // Select "Ok, i guess" from dropdown
        WebElement likeUsDropdown = driver.findElement(By.id("like_us"));
        likeUsDropdown.click();
        WebElement okOption = driver.findElement(By.xpath("//select[@id='like_us']/option[@value='Ok, i guess']"));
        okOption.click();

        // Enter comment
        WebElement commentField = driver.findElement(By.xpath("//textarea[@name='comment']"));
        commentField.sendKeys("Nice site!");

        // Click Send
        WebElement sendButton = driver.findElement(By.xpath("//button[@type='submit']"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(1000);

        // Verify we're on confirmation page with filled data
        assertEquals("Alice", driver.findElement(By.id("name")).getText(), "Name should be Alice");
        assertEquals("25", driver.findElement(By.id("age")).getText(), "Age should be 25");

        // Click "No" button (goes back)
        WebElement noButton = driver.findElement(By.xpath("//button[@class='w3-btn w3-red w3-xlarge']"));
        noButton.click();

        // Wait for page to load and go back to feedback form
        Thread.sleep(1000);

        // Check fields are filled correctly (they should still have the data)
        assertEquals("Alice", driver.findElement(By.id("fb_name")).getAttribute("value"), "Name should still be Alice");
        assertEquals("25", driver.findElement(By.id("fb_age")).getAttribute("value"), "Age should still be 25");
    }
}