package selenium.tasks;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.DriverFactory;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = DriverFactory.createChromeDriver();
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

        List<WebElement> languages = driver.findElements(
                By.cssSelector("#lang_check input[type='checkbox']"));
        assertEquals(4, languages.size());
        assertTrue(languages.stream().noneMatch(WebElement::isSelected));

        assertFalse(driver.findElement(By.cssSelector("input[name='gender'][value='male']"))
                .isSelected());
        assertFalse(driver.findElement(By.cssSelector("input[name='gender'][value='female']"))
                .isSelected());
        WebElement unknownGender = driver.findElement(
                By.cssSelector("input[name='gender'][disabled]"));
        assertTrue(unknownGender.isSelected());
        assertFalse(unknownGender.isEnabled());

        Select likeUs = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", likeUs.getFirstSelectedOption().getText());

        WebElement sendButton = driver.findElement(
                By.cssSelector("#fb_form button[type='submit']"));
        assertEquals("#2196f3",
                Color.fromString(sendButton.getCssValue("background-color")).asHex());
        assertEquals("#ffffff", Color.fromString(sendButton.getCssValue("color")).asHex());
    }

    @Test
    public void emptyFeedbackPage() {
        submitFeedback();

        for (String id : List.of("name", "age", "language", "gender", "option", "comment")) {
            assertEmptyOrNull(By.id(id));
        }
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillFeedbackForm("Taylor Example");
        submitFeedback();

        assertFeedbackSummary("Taylor Example");
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        driver.findElement(By.id("fb_name")).sendKeys("Taylor");
        submitFeedback();
        confirmFeedback();

        waitForUrl("thank_you_for_feedback.html");
        waitForMessage("Thank you, Taylor, for your feedback!");
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        submitFeedback();
        confirmFeedback();

        waitForUrl("thank_you_for_feedback.html");
        waitForMessage("Thank you for your feedback!");
    }

    @Test
    public void noOnFeedbackPage() {
        fillFeedbackForm("Taylor Example");
        submitFeedback();
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();

        waitForUrl("provide_feedback");
        assertEquals("Taylor Example", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("32", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(
                By.cssSelector("#lang_check input[name='language'][value='English']"))
                .isSelected());
        assertTrue(driver.findElement(
                By.cssSelector("#lang_check input[name='language'][value='French']"))
                .isSelected());
        assertTrue(driver.findElement(
                By.cssSelector("input[name='gender'][value='female']")).isSelected());
        assertEquals("Good", new Select(driver.findElement(By.id("like_us")))
                .getFirstSelectedOption().getAttribute("value"));
        assertEquals("The form is easy to use.",
                driver.findElement(By.name("comment")).getAttribute("value"));
    }

    private void fillFeedbackForm(String name) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
        driver.findElement(By.id("fb_age")).sendKeys("32");
        driver.findElement(By.cssSelector(
                "#lang_check input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector(
                "#lang_check input[name='language'][value='French']")).click();
        driver.findElement(By.cssSelector(
                "input[name='gender'][value='female']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByValue("Good");
        driver.findElement(By.name("comment")).sendKeys("The form is easy to use.");
    }

    private void submitFeedback() {
        driver.findElement(By.cssSelector("#fb_form button[type='submit']")).click();
        waitForUrl("check_feedback.html");
    }

    private void confirmFeedback() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
    }

    private void assertFeedbackSummary(String name) {
        assertEquals(name, driver.findElement(By.id("name")).getText());
        assertEquals("32", driver.findElement(By.id("age")).getText());
        assertEquals("English,French", driver.findElement(By.id("language")).getText());
        assertEquals("female", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("The form is easy to use.", driver.findElement(By.id("comment")).getText());
    }

    private void assertEmptyOrNull(By locator) {
        String text = driver.findElement(locator).getText();
        assertTrue(text.isEmpty() || "null".equals(text),
                () -> "Expected empty or null text but was: " + text);
    }

    private void waitForUrl(String urlPart) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.getCurrentUrl().contains(urlPart));
    }

    private void waitForMessage(String expectedMessage) {
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> expectedMessage.equals(
                        webDriver.findElement(By.id("message")).getText()));
    }
}