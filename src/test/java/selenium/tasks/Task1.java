package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.utility.DriverFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = DriverFactory.getChromeDriver();
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
        WebElement nameField = driver.findElement(By.id("fb_name"));
        WebElement ageField = driver.findElement(By.id("fb_age"));
        WebElement textArea = driver.findElement(By.tagName("textarea"));
        assertEquals("", nameField.getAttribute("value"), "Name field should be empty");
        assertEquals("", ageField.getAttribute("value"), "Age field should be empty");
        assertEquals("", textArea.getText(), "Text area should be empty");

        WebElement languageSelect = driver.findElement(By.id("lang_check"));
        List<WebElement> ticks = languageSelect.findElements(By.tagName("input"));
        for (WebElement tick : ticks) {
            assertFalse(tick.isSelected(), "All language checkboxes should be unchecked");
        }

        WebElement genreSelect = driver.findElement(By.xpath("//div[h3[text()='Select Your Genre:']]"));
        List<WebElement> inputs = genreSelect.findElements(By.tagName("input"));
        List<WebElement> labels = genreSelect.findElements(By.tagName("label"));
        assertEquals(3, inputs.size(), "There should be 3 genre options");
        assertEquals(3, labels.size(), "There should be 3 genre labels");

        int index = labels.stream().filter(label -> label.getText().equals("Don't know (Disabled)"))
                .findFirst()
                .map(labels::indexOf)
                .orElse(-1);
        assertTrue(index != -1, "There should be an option with value 'Don't know (Disabled)'");
        assertTrue(inputs.get(index).isSelected(), "The 'Don't know (Disabled)' option should be selected by default");
        assertFalse(inputs.get(index).isEnabled(), "The 'Don't know (Disabled)' option should be disabled");

        WebElement selectElement = driver.findElement(By.id("like_us"));
        WebElement defaultOption = selectElement.findElement(By.xpath("option[text()='Choose your option']"));
        assertTrue(defaultOption.isSelected(), "The default option 'Choose your option' should be selected");
        assertFalse(defaultOption.isEnabled(), "The default option 'Choose your option' should be disabled");

        WebElement button = driver.findElement(By.tagName("button"));
        assertEquals("Send", button.getText(), "Button text should be 'Send'");
        assertEquals("rgba(33, 150, 243, 1)", button.getCssValue("background-color"), "Button background color should be rgba(33, 150, 243, 1)");
        assertEquals("rgba(255, 255, 255, 1)", button.getCssValue("color"), "Button text color should be white");
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        WebElement button = driver.findElement(By.tagName("button"));
        assertEquals("Send", button.getText(), "Button text should be 'Send'");
        button.click();

        List<WebElement> spans = driver.findElements(By.xpath("//div[@class='description']//span"));
        assertFalse(spans.isEmpty(), "There should be a div with class 'description'");

        for (WebElement span : spans) {
            String text = span.getText();
            assertTrue(text.isEmpty() || text.equals("null"), "Fields should be empty or 'null'");
        }
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        WebElement nameField = driver.findElement(By.id("fb_name"));
        WebElement ageField = driver.findElement(By.id("fb_age"));
        WebElement textArea = driver.findElement(By.tagName("textarea"));
        nameField.clear();
        nameField.sendKeys("John Doe");
        ageField.clear();
        ageField.sendKeys("30");
        textArea.clear();
        textArea.sendKeys("This is a feedback message.");

        WebElement languageSelect = driver.findElement(By.id("lang_check"));
        List<WebElement> ticks = languageSelect.findElements(By.tagName("input"));
        ticks.getFirst().click();

        WebElement genreSelect = driver.findElement(By.xpath("//div[h3[text()='Select Your Genre:']]"));
        List<WebElement> inputs = genreSelect.findElements(By.tagName("input"));
        inputs.getFirst().click();

        WebElement selectElement = driver.findElement(By.id("like_us"));
        selectElement.findElement(By.xpath("option[text()='Good']")).click();

        WebElement button = driver.findElement(By.tagName("button"));
        button.click();

        String[] ids = {"name", "age", "language", "gender", "option", "comment"};
        String[] expectedValues = {"John Doe", "30", "English", "male", "Good", "This is a feedback message."};
        assertEquals(ids.length, expectedValues.length, "IDs and expected values arrays should have the same length");
        for (int i = 0; i < ids.length; i++) {
            assertEquals(expectedValues[i], driver.findElement(By.xpath(".//span[@id='" + ids[i] + "']")).getText(), ids[i] + " should be displayed correctly");
        }
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {

        // TODO:
        //  enter only name
        //  click "Send"
        //  click "Yes"
        //  check message text: "Thank you, NAME, for your feedback!"
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