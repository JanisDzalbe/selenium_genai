package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import selenium.utils.WebDriverUtils;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtils.initializeChromeDriver();
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
        WebElement nameInput = driver.findElement(By.id("fb_name"));
        assertTrue(nameInput.getAttribute("value").isEmpty(), "Name field should be empty initially");

        // Check that age field is empty
        WebElement ageInput = driver.findElement(By.id("fb_age"));
        assertTrue(ageInput.getAttribute("value").isEmpty(), "Age field should be empty initially");

        // Check that comment field is empty
        WebElement commentTextarea = driver.findElement(By.name("comment"));
        assertTrue(commentTextarea.getAttribute("value").isEmpty(), "Comment field should be empty initially");

        // Check that no language checkboxes are checked
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.name("language"));
        for (WebElement checkbox : languageCheckboxes) {
            assertFalse(checkbox.isSelected(), "Language checkbox should not be checked initially");
        }

        // Check "Don't know" is selected (disabled) in "Genre" radio buttons
        WebElement dontKnowRadio = driver.findElements(By.name("gender")).stream()
                .filter(radio -> radio.getAttribute("disabled") != null)
                .findFirst()
                .orElse(null);
        assertNotNull(dontKnowRadio, "Don't know radio button should exist");
        assertTrue(dontKnowRadio.isSelected(), "Don't know should be selected in Genre");

        // Check "Choose your option" is selected in "How do you like us?"
        WebElement likeUsDropdown = driver.findElement(By.id("like_us"));
        String selectedOption = likeUsDropdown.findElement(By.cssSelector("option:checked")).getAttribute("value");
        assertEquals("", selectedOption, "Like us option should default to empty 'Choose your option'");

        // Check that the send button is blue with white text
        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        String buttonColor = sendButton.getCssValue("background-color");
        String textColor = sendButton.getCssValue("color");
        assertNotNull(buttonColor, "Send button should have a background color");
        assertNotNull(textColor, "Send button should have a text color");
        assertTrue(sendButton.getAttribute("class").contains("w3-blue"), "Send button should have blue class");
    }

    @Test
    public void emptyFeedbackPage() {
        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        sendButton.click();
        // Check that name field is not present ("null")
        assertTrue(driver.findElements(By.id("fb_name")).isEmpty(), "Name field should not be present after send");
        // Check that age field is not present ("null")
        assertTrue(driver.findElements(By.id("fb_age")).isEmpty(), "Age field should not be present after send");
        // Check that comment field is not present ("null")
        assertTrue(driver.findElements(By.name("comment")).isEmpty(), "Comment field should not be present after send");
        // Check that language checkboxes are not present ("null")
        assertTrue(driver.findElements(By.name("language")).isEmpty(), "Language checkboxes should not be present after send");
        // Check that gender radios are not present ("null")
        assertTrue(driver.findElements(By.name("gender")).isEmpty(), "Gender radios should not be present after send");
        // Check that like_us dropdown is not present ("null")
        assertTrue(driver.findElements(By.id("like_us")).isEmpty(), "Like us dropdown should not be present after send");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        // Fill name field
        WebElement nameInput = driver.findElement(By.id("fb_name"));
        nameInput.sendKeys("John Doe");

        // Fill age field
        WebElement ageInput = driver.findElement(By.id("fb_age"));
        ageInput.sendKeys("30");

        // Fill comment field
        WebElement commentTextarea = driver.findElement(By.name("comment"));
        commentTextarea.sendKeys("Great experience!");

        // Check language checkbox
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.name("language"));
        if (!languageCheckboxes.isEmpty()) {
            languageCheckboxes.getFirst().click();
        }

        // Select a gender radio button (not "Don't know")
        java.util.List<WebElement> genderRadios = driver.findElements(By.name("gender"));
        for (WebElement radio : genderRadios) {
            if (radio.getAttribute("disabled") == null) {
                radio.click();
                break;
            }
        }

        // Select an option from "How do you like us?" dropdown
        WebElement likeUsDropdown = driver.findElement(By.id("like_us"));
        new Select(likeUsDropdown).selectByIndex(1);

        // Click Send button
        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        sendButton.click();

        // Verify fields are filled correctly - they should be gone after successful submission
        assertTrue(driver.findElements(By.id("fb_name")).isEmpty(), "Name field should not be present after send");
        assertTrue(driver.findElements(By.id("fb_age")).isEmpty(), "Age field should not be present after send");
        assertTrue(driver.findElements(By.name("comment")).isEmpty(), "Comment field should not be present after send");
        assertTrue(driver.findElements(By.name("language")).isEmpty(), "Language checkboxes should not be present after send");
        assertTrue(driver.findElements(By.name("gender")).isEmpty(), "Gender radios should not be present after send");
        assertTrue(driver.findElements(By.id("like_us")).isEmpty(), "Like us dropdown should not be present after send");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // enter only name
        WebElement nameInput = driver.findElement(By.id("fb_name"));
        nameInput.sendKeys("John");
        // click "Send"
        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        sendButton.click();
        // click "Yes"
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        yesButton.click();
        // check message text: "Thank you, NAME, for your feedback!"
        String expectedMessage = "Thank you, John, for your feedback!";
        assertTrue(driver.getPageSource().contains(expectedMessage), "Message should be: " + expectedMessage);
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // click "Send" (without entering anything)
        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        sendButton.click();
        // click "Yes"
        WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Yes']")));
        yesButton.click();
        // check message text: "Thank you for your feedback!"
        String expectedMessage = "Thank you for your feedback!";
        assertTrue(driver.getPageSource().contains(expectedMessage), "Message should be: " + expectedMessage);
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Fill name field
        WebElement nameInput = driver.findElement(By.id("fb_name"));
        nameInput.sendKeys("Jane Smith");

        // Fill age field
        WebElement ageInput = driver.findElement(By.id("fb_age"));
        ageInput.sendKeys("25");

        // Fill comment field
        WebElement commentTextarea = driver.findElement(By.name("comment"));
        commentTextarea.sendKeys("This is excellent!");

        // Check language checkbox
        java.util.List<WebElement> languageCheckboxes = driver.findElements(By.name("language"));
        if (!languageCheckboxes.isEmpty()) {
            languageCheckboxes.getFirst().click();
        }

        // Select a gender radio button (not "Don't know")
        java.util.List<WebElement> genderRadios = driver.findElements(By.name("gender"));
        for (WebElement radio : genderRadios) {
            if (radio.getAttribute("disabled") == null) {
                radio.click();
                break;
            }
        }

        // Select an option from "How do you like us?" dropdown
        WebElement likeUsDropdown = driver.findElement(By.id("like_us"));
        new Select(likeUsDropdown).selectByIndex(1);

        // Click Send button
        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        sendButton.click();

        // Click "No" button
        WebElement noButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='No']")));
        noButton.click();

        // Check that fields are filled correctly
        WebElement nameFieldAfter = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fb_name")));
        assertEquals("Jane Smith", nameFieldAfter.getAttribute("value"), "Name field should be filled");

        WebElement ageFieldAfter = driver.findElement(By.id("fb_age"));
        assertEquals("25", ageFieldAfter.getAttribute("value"), "Age field should be filled");

        WebElement commentFieldAfter = driver.findElement(By.name("comment"));
        assertEquals("This is excellent!", commentFieldAfter.getAttribute("value"), "Comment field should be filled");

        // Verify language checkbox is still selected
        java.util.List<WebElement> languageCheckboxesAfter = driver.findElements(By.name("language"));
        assertTrue(languageCheckboxesAfter.getFirst().isSelected(), "Language checkbox should still be selected");

        // Verify a gender radio is still selected
        java.util.List<WebElement> genderRadiosAfter = driver.findElements(By.name("gender"));
        boolean genderSelected = false;
        for (WebElement radio : genderRadiosAfter) {
            if (radio.isSelected() && radio.getAttribute("disabled") == null) {
                genderSelected = true;
                break;
            }
        }
        assertTrue(genderSelected, "A gender option should still be selected");

        // Verify dropdown selection is preserved
        WebElement likeUsDropdownAfter = driver.findElement(By.id("like_us"));
        String selectedValue = likeUsDropdownAfter.findElement(By.cssSelector("option:checked")).getAttribute("value");
        assertNotEquals("", selectedValue, "Dropdown selection should be preserved");
    }
}