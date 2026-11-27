package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.WebDriverUtil;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtil.getDriver();
        driver.manage().timeouts().implicitlyWait(TIMEOUT);
        driver.manage().window().maximize();
        driver.get(PAGE_URL);
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.findElement(By.id("fb_name")).isDisplayed());
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement sendButton() {
        return driver.findElement(By.cssSelector("button[type='submit']"));
    }

    private void submitFeedback(String name, String age, String language, String gender, String option, String comment) {
        if (name != null) {
            WebElement nameInput = driver.findElement(By.id("fb_name"));
            nameInput.clear();
            nameInput.sendKeys(name);
        }
        if (age != null) {
            WebElement ageInput = driver.findElement(By.id("fb_age"));
            ageInput.clear();
            ageInput.sendKeys(age);
        }
        if (language != null) {
            List<String> languages = List.of(language.split(","));
            for (WebElement checkbox : driver.findElements(By.name("language"))) {
                if (languages.contains(checkbox.getAttribute("value")) && !checkbox.isSelected()) {
                    checkbox.click();
                }
            }
        }
        if (gender != null) {
            for (WebElement radio : driver.findElements(By.name("gender"))) {
                if (radio.isEnabled() && gender.equals(radio.getAttribute("value"))) {
                    radio.click();
                }
            }
        }
        if (option != null) {
            new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(option);
        }
        if (comment != null) {
            WebElement commentField = driver.findElement(By.cssSelector("textarea[name='comment']"));
            commentField.clear();
            commentField.sendKeys(comment);
        }
        sendButton().click();
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.getCurrentUrl().contains("check_feedback.html"));
    }

    private void assertSummaryValue(String id, String expected) {
        String actual = new WebDriverWait(driver, TIMEOUT)
                .until(d -> d.findElement(By.id(id)).getText());
        if ("language".equals(id) && actual != null) {
            assertTrue(actual.contains(expected),
                    "Expected language summary to contain " + expected + " but was " + actual);
        } else {
            assertEquals(expected, actual);
        }
    }

    private void fillAllFields() {
        submitFeedback("Janis", "30", "English,French", "male", "Good",
                "The service is excellent");
    }

    @Test
    public void initialFeedbackPage() {
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));

        for (WebElement checkbox : driver.findElements(By.name("language"))) {
            assertFalse(checkbox.isSelected());
        }
        for (WebElement radio : driver.findElements(By.name("gender"))) {
            if (radio.isEnabled()) {
                assertFalse(radio.isSelected());
            }
        }

        List<WebElement> disabledGenderRadios =
                driver.findElements(By.cssSelector("input[name='gender'][disabled]"));
        assertEquals(1, disabledGenderRadios.size());
        assertTrue(disabledGenderRadios.get(0).isSelected());
        assertEquals("", disabledGenderRadios.get(0).getAttribute("value"));

        Select likeUs = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", likeUs.getFirstSelectedOption().getText());
        assertEquals("", driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));

        WebElement button = sendButton();
        assertTrue(button.getAttribute("class").contains("w3-blue"));
        String textColor = button.getCssValue("color").replace(" ", "");
        assertTrue(textColor.matches("rgba?\\(255,255,255(?:,1)?\\)"),
                "Send button text is not white: " + textColor);
    }

    @Test
    public void emptyFeedbackPage() {
        sendButton().click();
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.getCurrentUrl().contains("check_feedback.html"));

        for (String id : List.of("name", "age", "language", "gender", "option", "comment")) {
            String value = driver.findElement(By.id(id)).getText();
            assertTrue(value == null || value.isEmpty() || "null".equals(value),
                    "Expected " + id + " to be empty or null, but was " + value);
        }
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillAllFields();
        assertSummaryValue("name", "Janis");
        assertSummaryValue("age", "30");
        assertSummaryValue("language", "English");
        assertSummaryValue("gender", "male");
        assertSummaryValue("option", "Good");
        assertSummaryValue("comment", "The service is excellent");
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        submitFeedback("Janis", null, null, null, null, null);
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        assertEquals("Thank you, Janis, for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        sendButton().click();
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.getCurrentUrl().contains("check_feedback.html"));
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        assertEquals("Thank you for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() {
        fillAllFields();
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        new WebDriverWait(driver, TIMEOUT).until(
                d -> d.getCurrentUrl().contains("provide_feedback"));

        assertEquals("Janis", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("30", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='English']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertEquals("Good",
                new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption().getText());
        assertEquals("The service is excellent",
                driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value"));
    }
}