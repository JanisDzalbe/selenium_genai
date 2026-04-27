package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utils.DriverManager;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = DriverManager.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(8));
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
        WebElement nameField = driver.findElement(By.id("fb_name"));
        WebElement ageField = driver.findElement(By.id("fb_age"));
        WebElement textArea = driver.findElement(By.tagName("textarea"));

        assertEquals("", nameField.getAttribute("value"), "Name field should be empty");
        assertEquals("", ageField.getAttribute("value"), "Age field should be empty");
        assertEquals("", textArea.getAttribute("value"), "Text area should be empty");

        WebElement languageSelect = driver.findElement(By.id("lang_check"));
        List<WebElement> ticks = languageSelect.findElements(By.cssSelector("input[type='checkbox']"));
        for (WebElement tick : ticks) {
            assertFalse(tick.isSelected(), "All language checkboxes should be unchecked");
        }

        WebElement genreSelect = driver.findElement(By.xpath("//div[h3[text()='Select Your Genre:']]"));
        List<WebElement> inputs = genreSelect.findElements(By.cssSelector("input[type='radio']"));
        List<WebElement> labels = genreSelect.findElements(By.tagName("label"));
        assertEquals(3, inputs.size(), "There should be 3 genre options");
        assertEquals(3, labels.size(), "There should be 3 genre labels");

        int index = -1;
        for (int i = 0; i < labels.size(); i++) {
            if ("Don't know (Disabled)".equals(labels.get(i).getText())) {
                index = i;
                break;
            }
        }

        assertTrue(index != -1, "There should be an option 'Don't know (Disabled)'");
        assertTrue(inputs.get(index).isSelected(), "The 'Don't know (Disabled)' option should be selected by default");
        assertFalse(inputs.get(index).isEnabled(), "The 'Don't know (Disabled)' option should be disabled");

        WebElement selectElement = driver.findElement(By.id("like_us"));
        WebElement defaultOption = selectElement.findElement(By.xpath("./option[text()='Choose your option']"));
        assertTrue(defaultOption.isSelected(), "The default option 'Choose your option' should be selected");
        assertFalse(defaultOption.isEnabled(), "The default option 'Choose your option' should be disabled");

        WebElement button = driver.findElement(By.tagName("button"));
        assertEquals("Send", button.getText(), "Button text should be 'Send'");
        assertEquals("rgba(33, 150, 243, 1)", button.getCssValue("background-color"),
                "Button background color should be rgba(33, 150, 243, 1)");
        assertEquals("rgba(255, 255, 255, 1)", button.getCssValue("color"),
                "Button text color should be white");
    }

    @Test
    public void emptyFeedbackPage() {
        clickSend();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.description")));
        List<WebElement> spans = driver.findElements(By.xpath("//div[@class='description']//span"));
        assertFalse(spans.isEmpty(), "There should be spans inside description");

        for (WebElement span : spans) {
            String text = span.getText().trim();
            assertTrue(text.isEmpty() || "null".equalsIgnoreCase(text),
                    "Fields should be empty or 'null'");
        }
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillWholeForm();
        clickSend();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.description")));

        String[] ids = {"name", "age", "language", "gender", "option", "comment"};
        String[] expected = {"John Doe", "30", "English", "male", "Good", "This is a feedback message."};

        for (int i = 0; i < ids.length; i++) {
            String actual = driver.findElement(By.id(ids[i])).getText();
            assertEquals(expected[i], actual, ids[i] + " should be displayed correctly");
        }
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.clear();
        nameField.sendKeys("John");

        clickSend();
        clickYes();

        String text = getWholePageText();
        assertTrue(text.contains("Thank you, John, for your feedback!"),
                "Expected message: Thank you, John, for your feedback!");
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        clickSend();
        clickYes();

        String text = getWholePageText();
        assertTrue(text.contains("Thank you for your feedback!"),
                "Expected message: Thank you for your feedback!");
    }

    @Test
    public void noOnFeedbackPage() {
        fillWholeForm();
        clickSend();
        clickNo();

        // after clicking No, user should stay/return on form with data preserved
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_name")));

        assertEquals("John Doe", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("30", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("This is a feedback message.", driver.findElement(By.tagName("textarea")).getAttribute("value"));

        List<WebElement> ticks = driver.findElement(By.id("lang_check"))
                .findElements(By.cssSelector("input[type='checkbox']"));
        assertTrue(ticks.get(0).isSelected(), "English checkbox should stay selected");

        List<WebElement> radios = driver.findElement(By.xpath("//div[h3[text()='Select Your Genre:']]"))
                .findElements(By.cssSelector("input[type='radio']"));
        assertTrue(radios.get(0).isSelected(), "Male should stay selected");

        Select likeUs = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Good", likeUs.getFirstSelectedOption().getText(), "Selected option should stay 'Good'");
    }

    // ---------- helpers ----------

    private void fillWholeForm() {
        WebElement nameField = driver.findElement(By.id("fb_name"));
        WebElement ageField = driver.findElement(By.id("fb_age"));
        WebElement textArea = driver.findElement(By.tagName("textarea"));

        nameField.clear();
        nameField.sendKeys("John Doe");

        ageField.clear();
        ageField.sendKeys("30");

        textArea.clear();
        textArea.sendKeys("This is a feedback message.");

        List<WebElement> ticks = driver.findElement(By.id("lang_check"))
                .findElements(By.cssSelector("input[type='checkbox']"));
        if (!ticks.get(0).isSelected()) {
            ticks.get(0).click(); // English
        }

        List<WebElement> radios = driver.findElement(By.xpath("//div[h3[text()='Select Your Genre:']]"))
                .findElements(By.cssSelector("input[type='radio']"));
        if (!radios.get(0).isSelected()) {
            radios.get(0).click(); // male
        }

        Select likeUs = new Select(driver.findElement(By.id("like_us")));
        likeUs.selectByVisibleText("Good");
    }

    private void clickSend() {
        WebElement button = driver.findElement(By.tagName("button"));
        assertEquals("Send", button.getText(), "Button text should be 'Send'");
        button.click();
    }

    private void clickYes() {
        if (handleAlert(true)) return;

        List<WebElement> yesButtons = driver.findElements(
                By.xpath("//button[normalize-space()='Yes'] | //a[normalize-space()='Yes']")
        );
        if (!yesButtons.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(yesButtons.get(0))).click();
        }
    }

    private void clickNo() {
        if (handleAlert(false)) return;

        List<WebElement> noButtons = driver.findElements(
                By.xpath("//button[normalize-space()='No'] | //a[normalize-space()='No']")
        );
        if (!noButtons.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(noButtons.get(0))).click();
        }
    }

    private boolean handleAlert(boolean accept) {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.alertIsPresent());
            if (accept) alert.accept();
            else alert.dismiss();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private String getWholePageText() {
        return driver.findElement(By.tagName("body")).getText();
    }
}
