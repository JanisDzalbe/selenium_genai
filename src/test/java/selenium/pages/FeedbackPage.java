package selenium.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FeedbackPage {
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    private static final By NAME_INPUT = By.id("fb_name");
    private static final By AGE_INPUT = By.id("fb_age");
    private static final By LANGUAGE_CHECKBOXES = By.name("language");
    private static final By GENDER_RADIOS = By.name("gender");
    private static final By DISABLED_GENDER_RADIOS = By.cssSelector("input[name='gender'][disabled]");
    private static final By LIKE_US_SELECT = By.id("like_us");
    private static final By COMMENT_TEXTAREA = By.cssSelector("textarea[name='comment']");
    private static final By SEND_BUTTON = By.cssSelector("button[type='submit']");
    private static final By YES_BUTTON = By.xpath("//button[normalize-space()='Yes']");
    private static final By NO_BUTTON = By.xpath("//button[normalize-space()='No']");
    private static final By CONFIRM_MESSAGE = By.id("message");

    private final WebDriver driver;
    private final Duration timeout;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = timeout;
        this.wait = new WebDriverWait(driver, timeout);
    }

    public FeedbackPage open() {
        driver.get(PAGE_URL);
        wait.until(d -> d.findElement(NAME_INPUT).isDisplayed());
        return this;
    }

    public WebElement sendButton() {
        return driver.findElement(SEND_BUTTON);
    }

    public String getNameValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String getAgeValue() {
        return driver.findElement(AGE_INPUT).getAttribute("value");
    }

    public List<WebElement> getLanguageCheckboxes() {
        return driver.findElements(LANGUAGE_CHECKBOXES);
    }

    public List<WebElement> getGenderRadios() {
        return driver.findElements(GENDER_RADIOS);
    }

    public List<WebElement> getDisabledGenderRadios() {
        return driver.findElements(DISABLED_GENDER_RADIOS);
    }

    public String getSelectedLikeUsOption() {
        return new Select(driver.findElement(LIKE_US_SELECT)).getFirstSelectedOption().getText();
    }

    public String getCommentValue() {
        return driver.findElement(COMMENT_TEXTAREA).getAttribute("value");
    }

    public boolean isLanguageSelected(String value) {
        return driver.findElement(By.cssSelector("input[name='language'][value='" + value + "']"))
                .isSelected();
    }

    public boolean isGenderSelected(String value) {
        return driver.findElement(By.cssSelector("input[name='gender'][value='" + value + "']"))
                .isSelected();
    }

    public void clickSendAndWaitForSummary() {
        sendButton().click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));
    }

    public void submitFeedback(String name, String age, String language,
                               String gender, String option, String comment) {
        if (name != null) {
            WebElement nameInput = driver.findElement(NAME_INPUT);
            nameInput.clear();
            nameInput.sendKeys(name);
        }
        if (age != null) {
            WebElement ageInput = driver.findElement(AGE_INPUT);
            ageInput.clear();
            ageInput.sendKeys(age);
        }
        if (language != null) {
            List<String> languages = List.of(language.split(","));
            for (WebElement checkbox : driver.findElements(LANGUAGE_CHECKBOXES)) {
                if (languages.contains(checkbox.getAttribute("value")) && !checkbox.isSelected()) {
                    checkbox.click();
                }
            }
        }
        if (gender != null) {
            for (WebElement radio : driver.findElements(GENDER_RADIOS)) {
                if (radio.isEnabled() && gender.equals(radio.getAttribute("value"))) {
                    radio.click();
                }
            }
        }
        if (option != null) {
            new Select(driver.findElement(LIKE_US_SELECT)).selectByVisibleText(option);
        }
        if (comment != null) {
            WebElement commentField = driver.findElement(COMMENT_TEXTAREA);
            commentField.clear();
            commentField.sendKeys(comment);
        }
        clickSendAndWaitForSummary();
    }

    public void fillAllFields() {
        submitFeedback("Janis", "30", "English,French", "male", "Good",
                "The service is excellent");
    }

    public String getSummaryValue(String id) {
        return new WebDriverWait(driver, timeout)
                .until(d -> d.findElement(By.id(id)).getText());
    }

    public void assertSummaryValue(String id, String expected) {
        String actual = getSummaryValue(id);
        if ("language".equals(id) && actual != null) {
            Assertions.assertTrue(actual.contains(expected),
                    "Expected language summary to contain " + expected + " but was " + actual);
        } else {
            Assertions.assertEquals(expected, actual);
        }
    }

    public void confirmYes() {
        driver.findElement(YES_BUTTON).click();
        wait.until(d -> d.getCurrentUrl().contains("thank_you_for_feedback.html"));
    }

    public void clickNo() {
        driver.findElement(NO_BUTTON).click();
        wait.until(d -> d.getCurrentUrl().contains("provide_feedback"));
    }

    public String getConfirmationMessage() {
        return driver.findElement(CONFIRM_MESSAGE).getText();
    }
}