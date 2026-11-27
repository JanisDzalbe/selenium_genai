package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtility;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    WebDriverWait wait;

    private static final String NAME = "Alex Example";
    private static final String AGE = "30";
    private static final String LANGUAGE = "English";
    private static final String GENDER = "female";
    private static final String OPTION = "Good";
    private static final String COMMENT = "Great service!";

    private static final Map<String, String> FEEDBACK = new LinkedHashMap<>();

    static {
        FEEDBACK.put("name", NAME);
        FEEDBACK.put("age", AGE);
        FEEDBACK.put("language", LANGUAGE);
        FEEDBACK.put("gender", GENDER);
        FEEDBACK.put("option", OPTION);
        FEEDBACK.put("comment", COMMENT);
    }

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtility.getChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fb_form")));
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private List<WebElement> inputs(String name) {
        return driver.findElements(By.name(name));
    }

    private void fillWholeForm() {
        driver.findElement(By.id("fb_name")).sendKeys(NAME);
        driver.findElement(By.id("fb_age")).sendKeys(AGE);
        driver.findElement(By.cssSelector("input[name='language'][value='" + LANGUAGE + "']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='" + GENDER + "']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByValue(OPTION);
        driver.findElement(By.name("comment")).sendKeys(COMMENT);
    }

    private void clickSend() {
        driver.findElement(By.cssSelector("#fb_form form button[type='submit']")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fb_thx")));
    }

    private Map<String, String> readFeedbackResult() {
        Map<String, String> values = new LinkedHashMap<>();
        for (String field : FEEDBACK.keySet()) {
            values.put(field, wait.until(ExpectedConditions.presenceOfElementLocated(By.id(field))).getText());
        }
        return values;
    }

    private void assertFeedbackResult(Map<String, String> expected) {
        assertEquals(expected, readFeedbackResult());
    }

    private void assertEmptyOrNullFeedbackResult() {
        Map<String, String> result = readFeedbackResult();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            assertTrue(entry.getValue().isEmpty() || "null".equals(entry.getValue()),
                    entry.getKey() + " should be empty or null, but was: " + entry.getValue());
        }
    }

    private String normalizeColor(String color) {
        return color.replaceAll("\\s+", "").toLowerCase();
    }

    private void assertFeedbackFormValues() {
        assertEquals(NAME, driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals(AGE, driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector(
                "input[name='language'][value='" + LANGUAGE + "']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + GENDER + "']")).isSelected());
        assertEquals(OPTION, new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getAttribute("value"));
        assertEquals(COMMENT, driver.findElement(By.name("comment")).getAttribute("value"));
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.name("comment")).getAttribute("value"));

        for (WebElement language : inputs("language")) {
            assertFalse(language.isSelected());
        }
        WebElement unknownGender = driver.findElement(By.cssSelector("input[name='gender'][value='']:checked"));
        assertTrue(unknownGender.isSelected(), "\"Don't know\" should be selected by default");
        for (WebElement gender : inputs("gender")) {
            if (!gender.equals(unknownGender)) {
                assertFalse(gender.isSelected());
            }
        }

        Select option = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", option.getFirstSelectedOption().getText());

        WebElement sendButton = driver.findElement(By.cssSelector("#fb_form form button[type='submit']"));
        assertEquals("rgba(33,150,243,1)", normalizeColor(sendButton.getCssValue("background-color")));
        assertEquals("rgba(255,255,255,1)", normalizeColor(sendButton.getCssValue("color")));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        clickSend();
        assertEmptyOrNullFeedbackResult();
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillWholeForm();
        clickSend();
        assertFeedbackResult(FEEDBACK);
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        driver.findElement(By.id("fb_name")).sendKeys(NAME);
        clickSend();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Thank you, " + NAME + ", for your feedback!", message.getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        clickSend();
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Thank you for your feedback!", message.getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillWholeForm();
        clickSend();
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_form")));
        assertFeedbackFormValues();
    }
}