package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        Path chromeDriver = Path.of("lib", "chromedriver.exe");
        if (Files.exists(chromeDriver)) {
            System.setProperty("webdriver.chrome.driver", chromeDriver.toAbsolutePath().toString());
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox",
                "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement waitForElement(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void sendFeedback() {
        waitForElement(By.cssSelector("button[type='submit']")).click();
    }

    private void assertEmptyOrNull(By locator) {
        String text = waitForElement(locator).getText().trim();
        assertTrue(text.isEmpty() || text.equalsIgnoreCase("null"),
                () -> "Expected an empty or null value for " + locator + ", but was '" + text + "'");
    }

    private void assertRgba(WebElement element, String property, String expected) {
        String value = element.getCssValue(property);
        assertTrue(value.equals(expected) || value.replace("rgb(", "rgba(").replace(")", ", 1)")
                        .equals(expected),
                () -> "Expected " + property + " " + expected + " but was " + value);
    }

    private void fillFeedback(String name, String age, String gender, String option,
                              List<String> languages, String comment) {
        waitForElement(By.id("fb_name")).clear();
        waitForElement(By.id("fb_name")).sendKeys(name);
        waitForElement(By.id("fb_age")).clear();
        waitForElement(By.id("fb_age")).sendKeys(age);

        for (String language : languages) {
            waitForElement(By.cssSelector("input[name='language'][value='" + language + "']")).click();
        }
        if (gender != null) {
            waitForElement(By.cssSelector("input[name='gender'][value='" + gender + "']")).click();
        }
        new Select(waitForElement(By.id("like_us"))).selectByVisibleText(option);
        WebElement commentField = waitForElement(By.cssSelector("textarea[name='comment']"));
        commentField.clear();
        commentField.sendKeys(comment);
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        assertEquals("", waitForElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", waitForElement(By.id("fb_age")).getAttribute("value"));

        List<WebElement> languages = driver.findElements(By.cssSelector("input[name='language']"));
        for (WebElement language : languages) {
            assertFalse(language.isSelected());
        }

        WebElement dontKnow = driver.findElement(By.cssSelector("input[name='gender'][disabled][checked]"));
        assertTrue(dontKnow.isSelected());
        assertTrue(driver.findElements(By.cssSelector("input[name='gender']:not([disabled]):checked")).isEmpty());

        Select options = new Select(waitForElement(By.id("like_us")));
        assertEquals("Choose your option", options.getFirstSelectedOption().getText());
        assertEquals("", waitForElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));

        WebElement send = waitForElement(By.cssSelector("button[type='submit']"));
        assertRgba(send, "background-color", "rgba(33, 150, 243, 1)");
        assertRgba(send, "color", "rgba(255, 255, 255, 1)");
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        sendFeedback();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("check_feedback"));

        assertEmptyOrNull(By.id("name"));
        assertEmptyOrNull(By.id("age"));
        assertEmptyOrNull(By.id("language"));
        assertEmptyOrNull(By.id("gender"));
        assertEmptyOrNull(By.id("option"));
        assertEmptyOrNull(By.id("comment"));
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        fillFeedback("Janis", "30", "male", "Good",
                List.of("English", "French"), "Great site!");
        sendFeedback();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("check_feedback"));

        assertEquals("Janis", waitForElement(By.id("name")).getText());
        assertEquals("30", waitForElement(By.id("age")).getText());
        assertEquals("English,French", waitForElement(By.id("language")).getText());
        assertEquals("male", waitForElement(By.id("gender")).getText());
        assertEquals("Good", waitForElement(By.id("option")).getText());
        assertEquals("Great site!", waitForElement(By.id("comment")).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        waitForElement(By.id("fb_name")).sendKeys("Janis");
        sendFeedback();
        waitForElement(By.xpath("//button[normalize-space()='Yes']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("thank_you_for_feedback"));
        assertEquals("Thank you, Janis, for your feedback!",
                waitForElement(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        sendFeedback();
        waitForElement(By.xpath("//button[normalize-space()='Yes']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("thank_you_for_feedback"));
        assertEquals("Thank you for your feedback!",
                waitForElement(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        fillFeedback("Janis", "30", "female", "Ok, i guess",
                List.of("Spanish", "Chinese"), "Could improve.");
        sendFeedback();
        waitForElement(By.xpath("//button[normalize-space()='No']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("provide_feedback"));

        assertEquals("Janis", waitForElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("30", waitForElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(waitForElement(By.cssSelector("input[name='language'][value='Spanish']")).isSelected());
        assertTrue(waitForElement(By.cssSelector("input[name='language'][value='Chinese']")).isSelected());
        assertTrue(waitForElement(By.cssSelector("input[name='gender'][value='female']")).isSelected());
        assertEquals("Ok, i guess",
                new Select(waitForElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("Could improve.",
                waitForElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }
}