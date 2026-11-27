package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverFactory;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final String NAME = "Jane Doe";
    private static final String AGE = "30";
    private static final String COMMENT = "A helpful feedback comment.";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverFactory.createChromeDriver();
        driver.get(FEEDBACK_URL);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.name("comment")).getAttribute("value"));

        assertTrue(driver.findElements(By.cssSelector("input[name='language']")).stream()
                .noneMatch(WebElement::isSelected));
        assertFalse(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertFalse(driver.findElement(By.cssSelector("input[name='gender'][value='female']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender']:disabled")).isSelected());
        WebElement selectedOption = new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption();
        assertEquals("", selectedOption.getAttribute("value"));
        assertEquals("Choose your option", selectedOption.getText());

        WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
        assertEquals("rgb(33, 150, 243)", Color.fromString(sendButton.getCssValue("background-color")).asRgb());
        assertEquals("rgb(255, 255, 255)", Color.fromString(sendButton.getCssValue("color")).asRgb());
    }

    @Test
    public void emptyFeedbackPage() {
        submitForm();

        assertSummaryFieldIsEmptyOrNull("name");
        assertSummaryFieldIsEmptyOrNull("age");
        assertSummaryFieldIsEmptyOrNull("language");
        assertSummaryFieldIsEmptyOrNull("gender");
        assertSummaryFieldIsEmptyOrNull("option");
        assertSummaryFieldIsEmptyOrNull("comment");
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillWholeForm();
        submitForm();
        assertWholeFormSummary();
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        driver.findElement(By.id("fb_name")).sendKeys(NAME);
        submitForm();
        clickWhenReady("Yes");
        assertThankYouMessage("Thank you, " + NAME + ", for your feedback!");
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        submitForm();
        clickWhenReady("Yes");
        assertThankYouMessage("Thank you for your feedback!");
    }

    @Test
    public void noOnFeedbackPage() {
        fillWholeForm();
        submitForm();
        clickWhenReady("No");
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_name")));

        assertWholeFormValues();
    }

    private void fillWholeForm() {
        driver.findElement(By.id("fb_name")).sendKeys(NAME);
        driver.findElement(By.id("fb_age")).sendKeys(AGE);
        driver.findElement(By.cssSelector("input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector("input[name='language'][value='French']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='female']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByValue("Good");
        driver.findElement(By.name("comment")).sendKeys(COMMENT);
    }

    private void submitForm() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }
    private void clickWhenReady(String buttonText) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='" + buttonText + "']")))
                .click();
    }

    private void assertWholeFormValues() {
        assertEquals(NAME, driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals(AGE, driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='English']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='French']")).isSelected());
        assertFalse(driver.findElement(By.cssSelector("input[name='language'][value='Spanish']")).isSelected());
        assertFalse(driver.findElement(By.cssSelector("input[name='language'][value='Chinese']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='female']")).isSelected());
        assertEquals("Good", new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption()
                .getAttribute("value"));
        assertEquals(COMMENT, driver.findElement(By.name("comment")).getAttribute("value"));
    }

    private void assertWholeFormSummary() {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.textToBe(By.id("name"), NAME));
        assertEquals(NAME, driver.findElement(By.id("name")).getText());
        assertEquals(AGE, driver.findElement(By.id("age")).getText());
        assertEquals("English,French", driver.findElement(By.id("language")).getText());
        assertEquals("female", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals(COMMENT, driver.findElement(By.id("comment")).getText());
    }

    private void assertSummaryFieldIsEmptyOrNull(String fieldId) {
        String text = new WebDriverWait(driver, WAIT_TIMEOUT).until(ExpectedConditions.presenceOfElementLocated(By.id(fieldId))).getText();
        assertTrue(text.isEmpty() || "null".equals(text),
                () -> fieldId + " should be empty or null, but was: " + text);
    }

    private void assertThankYouMessage(String expectedMessage) {
        String message = new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("message")))
                .getText();
        assertEquals(expectedMessage, message);
    }
}