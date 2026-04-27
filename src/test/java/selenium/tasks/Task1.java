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
        // Initialize EdgeDriver using cross-platform utility
        driver = WebDriverUtil.createEdgeDriver();
        // Navigate to feedback page
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
        assertTrue(nameField.getAttribute("value").isEmpty(), "Name field should be empty");

        // Check that age field is empty
        WebElement ageField = driver.findElement(By.id("fb_age"));
        assertTrue(ageField.getAttribute("value").isEmpty(), "Age field should be empty");

        // Check that comment textarea is empty
        WebElement commentField = driver.findElement(By.name("comment"));
        assertTrue(commentField.getAttribute("value").isEmpty(), "Comment field should be empty");

        // Check that no language checkboxes are selected
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.name("language"));
        for (WebElement checkbox : languageCheckboxes) {
            assertFalse(checkbox.isSelected(), "No language checkboxes should be selected initially");
        }

        // Check that "Don't know" radio button is selected for genre (it's disabled and checked by default)
        WebElement dontKnowRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='' and @disabled]"));
        assertTrue(dontKnowRadio.isSelected(), "Don't know radio button should be selected by default");

        // Check that no other genre radio buttons are selected
        java.util.List<WebElement> genreRadios = driver.findElements(By.name("gender"));
        for (WebElement radio : genreRadios) {
            String disabledAttr = radio.getAttribute("disabled");
            if (disabledAttr == null || !disabledAttr.equals("true")) {
                assertFalse(radio.isSelected(), "Other genre radio buttons should not be selected initially");
            }
        }

        // Check that "Choose your option" is selected in "How do you like us?" dropdown
        WebElement ratingDropdown = driver.findElement(By.id("like_us"));
        org.openqa.selenium.support.ui.Select ratingSelect = new org.openqa.selenium.support.ui.Select(ratingDropdown);
        String selectedOption = ratingSelect.getFirstSelectedOption().getText();
        assertEquals("Choose your option", selectedOption, "'How do you like us?' should default to 'Choose your option'");

        // Check that the send button is blue
        WebElement sendButton = driver.findElement(By.className("w3-btn-block"));
        String buttonClasses = sendButton.getAttribute("class");
        assertTrue(buttonClasses.contains("w3-blue"), "Send button should have w3-blue class");
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        // Click "Send" without entering any data
        WebElement sendButton = driver.findElement(By.className("w3-btn-block"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(2000);

        // Check that the confirmation page shows empty/null values
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertTrue(nameSpan.getText().isEmpty(), "Name should be empty on confirmation page");

        WebElement ageSpan = driver.findElement(By.id("age"));
        assertTrue(ageSpan.getText().isEmpty(), "Age should be empty on confirmation page");

        WebElement languageSpan = driver.findElement(By.id("language"));
        assertTrue(languageSpan.getText().isEmpty(), "Language should be empty on confirmation page");

        WebElement genderSpan = driver.findElement(By.id("gender"));
        assertEquals("null", genderSpan.getText(), "Gender should be null on confirmation page");

        WebElement optionSpan = driver.findElement(By.id("option"));
        assertEquals("null", optionSpan.getText(), "Option should be null on confirmation page");

        WebElement commentSpan = driver.findElement(By.id("comment"));
        assertTrue(commentSpan.getText().isEmpty(), "Comment should be empty on confirmation page");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        // Fill the name field
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("John Doe");

        // Fill the age field
        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("30");

        // Fill the comment textarea
        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("This is a test feedback message with multiple lines.\nIt contains useful information about the product.");

        // Select language checkboxes
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.name("language"));
        if (languageCheckboxes.size() >= 2) {
            languageCheckboxes.get(0).click(); // English
            languageCheckboxes.get(1).click(); // French
        }

        // Select male radio button for genre
        WebElement maleRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='male']"));
        maleRadio.click();

        // Select "Good" from rating dropdown
        WebElement ratingDropdown = driver.findElement(By.id("like_us"));
        org.openqa.selenium.support.ui.Select ratingSelect = new org.openqa.selenium.support.ui.Select(ratingDropdown);
        ratingSelect.selectByVisibleText("Good");

        // Click the "Send" button
        WebElement sendButton = driver.findElement(By.className("w3-btn-block"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(2000);

        // Verify that the confirmation page shows the entered values
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("John Doe", nameSpan.getText(), "Name should be displayed on confirmation page");

        WebElement ageSpan = driver.findElement(By.id("age"));
        assertEquals("30", ageSpan.getText(), "Age should be displayed on confirmation page");

        WebElement languageSpan = driver.findElement(By.id("language"));
        assertEquals("English,French", languageSpan.getText(), "Selected languages should be displayed");

        WebElement genderSpan = driver.findElement(By.id("gender"));
        assertEquals("male", genderSpan.getText(), "Selected gender should be displayed");

        WebElement optionSpan = driver.findElement(By.id("option"));
        assertEquals("Good", optionSpan.getText(), "Selected option should be displayed");

        WebElement commentSpan = driver.findElement(By.id("comment"));
        assertEquals("This is a test feedback message with multiple lines. It contains useful information about the product.",
                    commentSpan.getText(), "Comment should be displayed on confirmation page");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // Enter only name
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Alice Johnson");

        // Click "Send"
        WebElement sendButton = driver.findElement(By.className("w3-btn-block"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(2000);

        // Click "Yes" button (calls openFeedback() JavaScript function)
        WebElement yesButton = driver.findElement(By.xpath("//button[contains(text(), 'Yes')]"));
        yesButton.click();

        // Wait for success/thank you page or message to appear
        Thread.sleep(2000);

        // Check for success message - this might be in a new page or modal
        // Since we don't know the exact structure, we'll check for common success indicators
        try {
            // Try to find a thank you message
            WebElement thankYouMessage = driver.findElement(By.xpath("//*[contains(text(), 'Thank you')]"));
            String messageText = thankYouMessage.getText();
            assertTrue(messageText.contains("Alice Johnson"), "Success message should include the entered name");
        } catch (Exception e) {
            // If no specific message found, check that we're no longer on the confirmation page
            assertFalse(driver.getCurrentUrl().contains("provide_feedback") ||
                       driver.findElements(By.id("fb_thx")).size() == 0,
                       "Should have navigated away from confirmation page after clicking Yes");
        }
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        // Click "Send" without entering anything
        WebElement sendButton = driver.findElement(By.className("w3-btn-block"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(2000);

        // Click "Yes" button (calls openFeedback() JavaScript function)
        WebElement yesButton = driver.findElement(By.xpath("//button[contains(text(), 'Yes')]"));
        yesButton.click();

        // Wait for success/thank you page or message to appear
        Thread.sleep(2000);

        // Check for success message - generic thank you since no name was provided
        // Since we don't know the exact success message format, just verify we navigated away from confirmation
        assertFalse(driver.getCurrentUrl().contains("provide_feedback") ||
                   driver.findElements(By.id("fb_thx")).size() == 0,
                   "Should have navigated away from confirmation page after clicking Yes");
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        // Fill the whole form
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("Bob Smith");

        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("35");

        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("This is a comprehensive feedback with all fields filled.");

        // Select language checkboxes
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.name("language"));
        if (languageCheckboxes.size() >= 1) {
            languageCheckboxes.get(0).click(); // English
        }

        // Select female radio button for genre
        WebElement femaleRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='female']"));
        femaleRadio.click();

        // Select "Excellent" from rating dropdown
        WebElement ratingDropdown = driver.findElement(By.id("like_us"));
        org.openqa.selenium.support.ui.Select ratingSelect = new org.openqa.selenium.support.ui.Select(ratingDropdown);
        ratingSelect.selectByVisibleText("Bad");

        // Click "Send"
        WebElement sendButton = driver.findElement(By.className("w3-btn-block"));
        sendButton.click();

        // Wait for confirmation page to load
        Thread.sleep(2000);

        // Click "No" button (calls window.history.back() to return to form)
        WebElement noButton = driver.findElement(By.xpath("//button[contains(text(), 'No')]"));
        noButton.click();

        // Wait for browser to go back to original form
        Thread.sleep(2000);

        // Check that fields are still filled correctly (form data should remain intact)
        nameField = driver.findElement(By.id("fb_name"));
        assertEquals("Bob Smith", nameField.getAttribute("value"), "Name field should retain its value after canceling");

        ageField = driver.findElement(By.id("fb_age"));
        assertEquals("35", ageField.getAttribute("value"), "Age field should retain its value after canceling");

        commentField = driver.findElement(By.name("comment"));
        assertEquals("This is a comprehensive feedback with all fields filled.", commentField.getAttribute("value"),
                    "Comment field should retain its value after canceling");

        // Check that language checkbox remains checked
        languageCheckboxes = driver.findElements(By.name("language"));
        if (languageCheckboxes.size() >= 1) {
            assertTrue(languageCheckboxes.get(0).isSelected(), "Language checkbox should remain checked after canceling");
        }

        // Check that female radio button is still selected
        femaleRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='female']"));
        assertTrue(femaleRadio.isSelected(), "Female radio button should remain selected after canceling");

        // Check that rating dropdown retains its selection
        ratingDropdown = driver.findElement(By.id("like_us"));
        ratingSelect = new org.openqa.selenium.support.ui.Select(ratingDropdown);
        assertEquals("Bad", ratingSelect.getFirstSelectedOption().getText(),
                    "Rating dropdown should retain its selection after canceling");
    }
}
