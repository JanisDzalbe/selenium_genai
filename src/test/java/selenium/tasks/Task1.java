package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utility.ChromeDriverUtility;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    public void openPage() {
        // TODO
        //  initialize the driver
        //  open page https://janisdzalbe.github.io/example-site/tasks/provide_feedback
        driver = ChromeDriverUtility.createChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
    }

    @AfterEach
    public void closeBrowser() {
        // TODO
        //  close the browser
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        // TODO:
        //  check that all field are empty and no ticks are clicked
        //  "Don't know" is selected in "Genre"
        //  "Choose your option" in "How do you like us?"
        //  check that the button send is blue with white letters
        assertEquals("", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertEquals("", driver.findElement(By.name("comment")).getAttribute("value"));

        List<WebElement> languages = driver.findElements(By.cssSelector("input[name='language']"));
        for (WebElement language : languages) {
            assertFalse(language.isSelected());
        }

        WebElement dontKnow = driver.findElement(
                By.cssSelector("input[name='gender'][disabled]"));
        assertTrue(dontKnow.isSelected());

        Select option = new Select(driver.findElement(By.id("like_us")));
        assertEquals("Choose your option", option.getFirstSelectedOption().getText());

        WebElement send = driver.findElement(By.cssSelector("button[type='submit']"));
        assertTrue(send.getCssValue("background-color").matches("rgba?\\(33, ?150, ?243(?:, ?1)?\\)"));
        assertTrue(send.getCssValue("color").matches("rgba?\\(255, ?255, ?255(?:, ?1)?\\)"));
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        // TODO:
        //  click "Send" without entering any data
        //  check fields are empty or "null"
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));

        assertEmptyOrNull("name");
        assertEmptyOrNull("age");
        assertEmptyOrNull("language");
        assertEmptyOrNull("gender");
        assertEmptyOrNull("option");
        assertEmptyOrNull("comment");
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        // TODO:
        //  fill the whole form, click "Send"
        //  check fields are filled correctly
        fillFeedback("Janis", "30", "English", "male", "Good", "Great experience");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));

        assertEquals("Janis", driver.findElement(By.id("name")).getText());
        assertEquals("30", driver.findElement(By.id("age")).getText());
        assertEquals("English", driver.findElement(By.id("language")).getText());
        assertEquals("male", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("Great experience", driver.findElement(By.id("comment")).getText());
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // TODO:
        //  enter only name
        //  click "Send"
        //  click "Yes"
        //  check message text: "Thank you, NAME, for your feedback!"
        driver.findElement(By.id("fb_name")).sendKeys("Janis");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        wait.until(d -> d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        wait.until(d -> !driver.findElement(By.id("message")).getText().isEmpty());
        assertEquals("Thank you, Janis, for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        // TODO:
        //  click "Send" (without entering anything)
        //  click "Yes"
        //  check message text: "Thank you for your feedback!"
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();

        wait.until(d -> d.getCurrentUrl().contains("thank_you_for_feedback.html"));
        wait.until(d -> !driver.findElement(By.id("message")).getText().isEmpty());
        assertEquals("Thank you for your feedback!",
                driver.findElement(By.id("message")).getText());
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        // TODO:
        //  fill the whole form
        //  click "Send"
        //  click "No"
        //  check fields are filled correctly
        fillFeedback("Janis", "30", "English", "male", "Good", "Great experience");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));

        assertEquals("Janis", driver.findElement(By.id("name")).getText());
        assertEquals("30", driver.findElement(By.id("age")).getText());
        assertEquals("English", driver.findElement(By.id("language")).getText());
        assertEquals("male", driver.findElement(By.id("gender")).getText());
        assertEquals("Good", driver.findElement(By.id("option")).getText());
        assertEquals("Great experience", driver.findElement(By.id("comment")).getText());

        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
        wait.until(d -> d.getCurrentUrl().contains("provide_feedback"));
        assertEquals("Janis", driver.findElement(By.id("fb_name")).getAttribute("value"));
        assertEquals("30", driver.findElement(By.id("fb_age")).getAttribute("value"));
        assertTrue(driver.findElement(By.cssSelector("input[name='language'][value='English']")).isSelected());
        assertTrue(driver.findElement(By.cssSelector("input[name='gender'][value='male']")).isSelected());
        assertEquals("Good", new Select(driver.findElement(By.id("like_us")))
                .getFirstSelectedOption().getText());
        assertEquals("Great experience", driver.findElement(By.name("comment")).getAttribute("value"));
    }

    private void fillFeedback(String name, String age, String language,
                              String gender, String option, String comment) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
        driver.findElement(By.id("fb_age")).sendKeys(age);
        driver.findElement(By.cssSelector("input[name='language'][value='" + language + "']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='" + gender + "']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(option);
        driver.findElement(By.name("comment")).sendKeys(comment);
    }

//    helper method to assert that a field is empty or null
    private void assertEmptyOrNull(String id) {
        String value = driver.findElement(By.id(id)).getText().trim();

        assertTrue(value.isEmpty() || value.equalsIgnoreCase("null"),
                id + " should be empty or null, but was: " + value);
    }
}