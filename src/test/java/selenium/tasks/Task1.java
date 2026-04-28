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
import selenium.utility.WebDriverManager;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverManager.initializeChromeDriver();
        driver.navigate().to("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
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
        assertEquals("", nameField.getAttribute("value"));

        WebElement ageField = driver.findElement(By.id("fb_age"));
        assertEquals("", ageField.getAttribute("value"));

        WebElement commentField = driver.findElement(By.name("comment"));
        assertEquals("", commentField.getAttribute("value"));

        // Check that no ticks are clicked (language checkboxes)
        List<WebElement> languageCheckboxes = driver.findElements(By.name("language"));
        for (WebElement checkbox : languageCheckboxes) {
            assertFalse(checkbox.isSelected());
        }

        // "Don't know" is selected in "Genre" (gender radio)
        WebElement dontKnowRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='']"));
        assertTrue(dontKnowRadio.isSelected());

        // "Choose your option" in "How do you like us?" (select)
        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        Select select = new Select(likeUsSelect);
        assertEquals("Choose your option", select.getFirstSelectedOption().getText());

        // Check that the button send is blue with white letters
        WebElement sendButton = driver.findElement(By.xpath("//button[text()='Send']"));
        assertTrue(sendButton.getAttribute("class").contains("w3-blue"));
    }

    @Test
    public void emptyFeedbackPage() {
        // Submit the form without entering any data
        WebElement form = driver.findElement(By.tagName("form"));
        form.submit();

        // Wait for the page to navigate to check_feedback.html
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        // Check fields are empty or "null"
        WebElement nameSpan = driver.findElement(By.id("name"));
        String nameText = nameSpan.getText();
        assertTrue(nameText.isEmpty() || "null".equals(nameText));

        WebElement ageSpan = driver.findElement(By.id("age"));
        String ageText = ageSpan.getText();
        assertTrue(ageText.isEmpty() || "null".equals(ageText));

        WebElement languageSpan = driver.findElement(By.id("language"));
        String languageText = languageSpan.getText();
        assertTrue(languageText.isEmpty() || "null".equals(languageText));

        WebElement genderSpan = driver.findElement(By.id("gender"));
        String genderText = genderSpan.getText();
        assertTrue(genderText.isEmpty() || "null".equals(genderText));

        WebElement optionSpan = driver.findElement(By.id("option"));
        String optionText = optionSpan.getText();
        assertTrue(optionText.isEmpty() || "null".equals(optionText));

        WebElement commentSpan = driver.findElement(By.id("comment"));
        String commentText = commentSpan.getText();
        assertTrue(commentText.isEmpty() || "null".equals(commentText));
    }

    @Test
    public void notEmptyFeedbackPage() {
        // Fill the whole form
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("John Doe");

        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("25");

        // Select languages: English and French
        WebElement englishCheckbox = driver.findElement(By.xpath("//input[@name='language' and @value='English']"));
        englishCheckbox.click();
        WebElement frenchCheckbox = driver.findElement(By.xpath("//input[@name='language' and @value='French']"));
        frenchCheckbox.click();

        // Select gender: male
        WebElement maleRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='male']"));
        maleRadio.click();

        // Select option: Good
        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        Select select = new Select(likeUsSelect);
        select.selectByVisibleText("Good");

        // Enter comment
        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("This is a test comment");

        // Submit the form
        WebElement form = driver.findElement(By.tagName("form"));
        form.submit();

        // Wait for the page to navigate to check_feedback.html
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        // Check fields are filled correctly
        WebElement nameSpan = driver.findElement(By.id("name"));
        assertEquals("John Doe", nameSpan.getText());

        WebElement ageSpan = driver.findElement(By.id("age"));
        assertEquals("25", ageSpan.getText());

        WebElement languageSpan = driver.findElement(By.id("language"));
        assertEquals("English,French", languageSpan.getText());

        WebElement genderSpan = driver.findElement(By.id("gender"));
        assertEquals("male", genderSpan.getText());

        WebElement optionSpan = driver.findElement(By.id("option"));
        assertEquals("Good", optionSpan.getText());

        WebElement commentSpan = driver.findElement(By.id("comment"));
        assertEquals("This is a test comment", commentSpan.getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        // Enter only name
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("John Doe");

        // Submit the form
        WebElement form = driver.findElement(By.tagName("form"));
        form.submit();

        // Wait for the page to navigate to check_feedback.html
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        // Click "Yes"
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        yesButton.click();

        // Wait for the page to navigate to thank_you_for_feedback.html
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback"));

        // Check message text: "Thank you, NAME, for your feedback!"
        WebElement messageElement = driver.findElement(By.id("message"));
        assertEquals("Thank you, John Doe, for your feedback!", messageElement.getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        // Submit the form without entering anything
        WebElement form = driver.findElement(By.tagName("form"));
        form.submit();

        // Wait for the page to navigate to check_feedback.html
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        // Click "Yes"
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        yesButton.click();

        // Wait for the page to navigate to thank_you_for_feedback.html
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback"));

        // Check message text: "Thank you for your feedback!"
        WebElement messageElement = driver.findElement(By.id("message"));
        assertEquals("Thank you for your feedback!", messageElement.getText());
    }

    @Test
    public void noOnFeedbackPage() {
        // Fill the whole form
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.sendKeys("John Doe");

        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.sendKeys("25");

        // Select languages: English and French
        WebElement englishCheckbox = driver.findElement(By.xpath("//input[@name='language' and @value='English']"));
        englishCheckbox.click();
        WebElement frenchCheckbox = driver.findElement(By.xpath("//input[@name='language' and @value='French']"));
        frenchCheckbox.click();

        // Select gender: male
        WebElement maleRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='male']"));
        maleRadio.click();

        // Select option: Good
        WebElement likeUsSelect = driver.findElement(By.id("like_us"));
        Select select = new Select(likeUsSelect);
        select.selectByVisibleText("Good");

        // Enter comment
        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.sendKeys("This is a test comment");

        // Submit the form
        WebElement form = driver.findElement(By.tagName("form"));
        form.submit();

        // Wait for the page to navigate to check_feedback.html
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        // Click "No"
        WebElement noButton = driver.findElement(By.xpath("//button[text()='No']"));
        noButton.click();

        // Wait for the page to navigate back to provide_feedback.html
        wait.until(ExpectedConditions.urlContains("provide_feedback"));

        // Check fields are filled correctly
        assertEquals("John Doe", nameField.getAttribute("value"));
        assertEquals("25", ageField.getAttribute("value"));

        // Check languages: English and French selected, others not
        assertTrue(englishCheckbox.isSelected());
        assertTrue(frenchCheckbox.isSelected());
        WebElement spanishCheckbox = driver.findElement(By.xpath("//input[@name='language' and @value='Spanish']"));
        assertFalse(spanishCheckbox.isSelected());
        WebElement chineseCheckbox = driver.findElement(By.xpath("//input[@name='language' and @value='Chinese']"));
        assertFalse(chineseCheckbox.isSelected());

        // Check gender: male selected
        assertTrue(maleRadio.isSelected());
        WebElement femaleRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='female']"));
        assertFalse(femaleRadio.isSelected());
        WebElement dontKnowRadio = driver.findElement(By.xpath("//input[@name='gender' and @value='']"));
        assertFalse(dontKnowRadio.isSelected());

        // Check like_us: Good selected
        assertEquals("Good", select.getFirstSelectedOption().getText());

        // Check comment
        assertEquals("This is a test comment", commentField.getAttribute("value"));
    }
}