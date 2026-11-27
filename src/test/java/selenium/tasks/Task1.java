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
import selenium.utility.DriverUtility;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        driver = DriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(PAGE_URL);
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }


    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));

        for (WebElement checkbox : driver.findElements(By.cssSelector("input[name='language']"))) {
            assertFalse(checkbox.isSelected());
        }

        WebElement unknownGender =
                driver.findElement(By.cssSelector("input[name='gender'][disabled][checked]"));
        assertTrue(unknownGender.isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isEnabled());

        Select options = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", options.getFirstSelectedOption().getText());

        WebElement send = driver.findElement(By.xpath("//button[normalize-space()='Send']"));
        assertTrue(send.getAttribute("class").contains("w3-blue"));
        assertEquals("rgba(255, 255, 255, 1)", send.getCssValue("color"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        clickSend();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));

        assertSummaryValue("name", "");
        assertSummaryValue("age", "");
        assertSummaryValue("language", "");
        assertSummaryValue("gender", "");
        assertSummaryValue("option", "");
        assertSummaryValue("comment", "");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillCompleteForm("Jane Doe", "32", "English", "female", "Good", "Great website!");
        clickSend();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));

        assertSummaryValue("name", "Jane Doe");
        assertSummaryValue("age", "32");
        assertSummaryValue("language", "English");
        assertSummaryValue("gender", "female");
        assertSummaryValue("option", "Good");
        assertSummaryValue("comment", "Great website!");
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(By.id("fb_name")).sendKeys("Alice");
        clickSend();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));

        clickConfirmationButton("Yes");
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));

        WebElement message = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Thank you, Alice, for your feedback!", message.getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        clickSend();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));

        clickConfirmationButton("Yes");
        wait.until(ExpectedConditions.urlContains("thank_you_for_feedback.html"));

        WebElement message = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Thank you for your feedback!", message.getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillCompleteForm("Bob Smith", "41", "French", "male", "Ok, i guess", "Could be better");
        clickSend();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));

        assertSummaryValue("name", "Bob Smith");
        assertSummaryValue("age", "41");
        assertSummaryValue("language", "French");
        assertSummaryValue("gender", "male");
        assertSummaryValue("option", "Ok, i guess");
        assertSummaryValue("comment", "Could be better");

        clickConfirmationButton("No");
        wait.until(ExpectedConditions.urlToBe(PAGE_URL));

        assertEquals("Bob Smith", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("41", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='French']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertEquals("Ok, i guess",
                new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("Could be better",
                driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }

    private void fillCompleteForm(String name, String age, String language,
                                  String gender, String option, String comment) {
        driver.findElement(By.id("fb_name")).clear();
        driver.findElement(By.id("fb_name")).sendKeys(name);
        driver.findElement(By.id("fb_age")).clear();
        driver.findElement(By.id("fb_age")).sendKeys(age);

        List<WebElement> languages =
                driver.findElements(By.cssSelector("input[name='language']"));
        for (WebElement checkbox : languages) {
            if (!checkbox.isSelected() && checkbox.getAttribute("value").equals(language)) {
                checkbox.click();
            }
        }

        driver.findElement(By.cssSelector("input[name='gender'][value='" + gender + "']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(option);
        driver.findElement(By.cssSelector("textarea[name='comment']")).clear();
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys(comment);
    }

    private void clickSend() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Send']"))).click();
    }

    private void clickConfirmationButton(String text) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='" + text + "']"))).click();
    }

    private void assertSummaryValue(String id, String expected) {
        By locator = By.id(id);
        if (expected.isEmpty()) {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } else {
            wait.until(ExpectedConditions.textToBe(locator, expected));
        }
        WebElement value = driver.findElement(locator);
        String actual = value.getText().trim();
        if (expected.isEmpty()) {
            assertTrue(actual.isEmpty() || "null".equals(actual),
                    "Expected field '" + id + "' to be empty or \"null\", but was: " + actual);
        } else {
            assertEquals(expected, actual);
        }
    }
}