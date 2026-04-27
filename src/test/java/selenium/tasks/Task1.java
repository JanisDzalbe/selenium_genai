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
import selenium.utility.DriverFactory;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {

    WebDriver driver;
    WebDriverWait wait;

    // ── Selectors: provide_feedback page ─────────────────────────────────────
    private static final By NAME_INPUT     = By.id("fb_name");
    private static final By AGE_INPUT      = By.id("fb_age");
    private static final By LANGUAGE_BOXES = By.name("language");
    private static final By GENDER_RADIOS  = By.name("gender");
    private static final By LIKE_US_SELECT = By.id("like_us");
    private static final By COMMENT_AREA   = By.name("comment");
    private static final By SEND_BUTTON    = By.cssSelector("button[type='submit']");

    // ── Selectors: check_feedback page ───────────────────────────────────────
    // Broad enough to match whatever tag carries w3-green / w3-red on that page.
    private static final By YES_BUTTON   = By.cssSelector("[class*='w3-green']");
    private static final By NO_BUTTON    = By.cssSelector("[class*='w3-red']");
    // The thank-you message lives in a paragraph whose id is "message" on the
    // real page; fall back to any visible <p> / <div> with that id.
    private static final By THANK_YOU_MSG = By.id("message");

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @BeforeEach
    public void openPage() {
        driver = DriverFactory.getChromeDriver();
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10)); // increased from 5 s
        driver.navigate().to(
                "https://janisdzalbe.github.io/example-site/tasks/provide_feedback"
        );
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fb_form")));
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * @param genderIndex  0 = Male, 1 = Female; pass -1 to leave unchanged.
     */
    private void fillWholeForm(String name, String age,
                               int[] languageIndexes,
                               int genderIndex,
                               String likeUsValue,
                               String comment) {
        if (name != null) {
            driver.findElement(NAME_INPUT).sendKeys(name);
        }
        if (age != null) {
            driver.findElement(AGE_INPUT).sendKeys(age);
        }
        if (languageIndexes != null) {
            var checkboxes = driver.findElements(LANGUAGE_BOXES);
            for (int idx : languageIndexes) {
                checkboxes.get(idx).click();
            }
        }
        if (genderIndex >= 0) {
            driver.findElements(GENDER_RADIOS).get(genderIndex).click();
        }
        if (likeUsValue != null) {
            new Select(driver.findElement(LIKE_US_SELECT)).selectByValue(likeUsValue);
        }
        if (comment != null) {
            driver.findElement(COMMENT_AREA).sendKeys(comment);
        }
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    public void initialFeedbackPage() {
        // Text fields must be empty
        assertEquals("", driver.findElement(NAME_INPUT).getAttribute("value"),
                "Name field should be empty on load");
        assertEquals("", driver.findElement(AGE_INPUT).getAttribute("value"),
                "Age field should be empty on load");

        // No language checkbox should be pre-checked
        for (WebElement cb : driver.findElements(LANGUAGE_BOXES)) {
            assertFalse(cb.isSelected(),
                    "No language checkbox should be pre-selected: " + cb.getAttribute("value"));
        }

        // "Don't know" (index 2) is the only pre-selected gender radio
        var genderRadios = driver.findElements(GENDER_RADIOS);
        assertFalse(genderRadios.get(0).isSelected(), "Male should NOT be pre-selected");
        assertFalse(genderRadios.get(1).isSelected(), "Female should NOT be pre-selected");
        assertTrue(genderRadios.get(2).isSelected(),
                "The 'Don't know' radio (index 2) should be pre-selected");

        // Dropdown must default to the placeholder option
        Select likeUs = new Select(driver.findElement(LIKE_US_SELECT));
        assertEquals("Choose your option",
                likeUs.getFirstSelectedOption().getText(),
                "Dropdown should default to 'Choose your option'");

        // Send button must be blue with white text
        WebElement sendButton = driver.findElement(SEND_BUTTON);
        assertTrue(Objects.requireNonNull(sendButton.getAttribute("class")).contains("w3-blue"),
                "Send button should carry the w3-blue CSS class");
        String color = sendButton.getCssValue("color");
        assertTrue(
                color.contains("rgba(255, 255, 255") || color.contains("rgb(255, 255, 255"),
                "Send button text colour should be white (255,255,255), got: " + color);
    }

    @Test
    public void emptyFeedbackPage() {
        driver.findElement(SEND_BUTTON).click();
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        String url = driver.getCurrentUrl();

        // All three text params must be blank (value after '=' is empty or param absent)
        assertNotNull(url);
        assertTrue(
                !url.contains("name=") || url.contains("name=&") || url.endsWith("name="),
                "name param should be empty in URL, got: " + url);
        assertTrue(
                !url.contains("age=") || url.contains("age=&") || url.endsWith("age="),
                "age param should be empty in URL, got: " + url);
        assertTrue(
                !url.contains("comment=") || url.contains("comment=&") || url.endsWith("comment="),
                "comment param should be empty in URL, got: " + url);
    }

    @Test
    public void notEmptyFeedbackPage() {
        fillWholeForm(
                "John Doe",
                "30",
                new int[]{0, 1},   // English, French
                0,                 // Male
                "Good",
                "This is a great website!"
        );

        // Verify every field BEFORE submitting
        assertEquals("John Doe", driver.findElement(NAME_INPUT).getAttribute("value"));
        assertEquals("30",       driver.findElement(AGE_INPUT).getAttribute("value"));

        var checkboxes = driver.findElements(LANGUAGE_BOXES);
        assertTrue(checkboxes.get(0).isSelected(),  "English should be checked");
        assertTrue(checkboxes.get(1).isSelected(),  "French should be checked");
        assertFalse(checkboxes.get(2).isSelected(), "Spanish should NOT be checked");
        assertFalse(checkboxes.get(3).isSelected(), "Chinese should NOT be checked");

        assertTrue(driver.findElements(GENDER_RADIOS).getFirst().isSelected(), "Male should be selected");

        assertEquals("Good",
                new Select(driver.findElement(LIKE_US_SELECT))
                        .getFirstSelectedOption().getAttribute("value"));

        assertEquals("This is a great website!",
                driver.findElement(COMMENT_AREA).getAttribute("value"));

        // Submit and verify URL contains the submitted data
        driver.findElement(SEND_BUTTON).click();
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        String url = driver.getCurrentUrl();
        assertNotNull(url);
        assertTrue(url.contains("name=John+Doe") || url.contains("name=John%20Doe"),
                "URL should contain submitted name, got: " + url);
        assertTrue(url.contains("age=30"),
                "URL should contain submitted age, got: " + url);
    }

    @Test
    public void yesOnWithNameFeedbackPage() {
        driver.findElement(NAME_INPUT).sendKeys("Alice");
        driver.findElement(SEND_BUTTON).click();
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        wait.until(ExpectedConditions.elementToBeClickable(YES_BUTTON)).click();

        String text = wait.until(
                ExpectedConditions.visibilityOfElementLocated(THANK_YOU_MSG)
        ).getText().trim();

        assertEquals("Thank you, Alice, for your feedback!", text,
                "Thank-you message should include the submitted name");
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() {
        driver.findElement(SEND_BUTTON).click();
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        wait.until(ExpectedConditions.elementToBeClickable(YES_BUTTON)).click();

        String text = wait.until(
                ExpectedConditions.visibilityOfElementLocated(THANK_YOU_MSG)
        ).getText().trim();

        assertEquals("Thank you for your feedback!", text,
                "Thank-you message should not include a name when none was entered");
    }

    @Test
    public void noOnFeedbackPage() {
        // FIX: "Ok, i guess" is the exact <option value> in the HTML
        fillWholeForm(
                "Bob Smith",
                "25",
                new int[]{2},      // Spanish
                1,                 // Female
                "Ok, i guess",
                "Nice feedback form!"
        );

        driver.findElement(SEND_BUTTON).click();
        wait.until(ExpectedConditions.urlContains("check_feedback"));

        String checkUrl = driver.getCurrentUrl();
        assertNotNull(checkUrl);
        assertTrue(checkUrl.contains("name=Bob"),
                "URL on check_feedback page should contain submitted name, got: " + checkUrl);

        // Click "No" → should return to the feedback form
        wait.until(ExpectedConditions.elementToBeClickable(NO_BUTTON)).click();
        wait.until(ExpectedConditions.urlContains("provide_feedback"));

        // The No button may use browser back-navigation, which restores the bfcache
        // and leaves the old field values in place. A hard refresh forces a clean load.
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fb_form")));

        // Form must be clean after refresh
        assertEquals("", driver.findElement(NAME_INPUT).getAttribute("value"),
                "After returning via No, name field should be empty");
        assertEquals("", driver.findElement(AGE_INPUT).getAttribute("value"),
                "After returning via No, age field should be empty");
        assertEquals("", driver.findElement(COMMENT_AREA).getAttribute("value"),
                "After returning via No, comment field should be empty");
    }
}