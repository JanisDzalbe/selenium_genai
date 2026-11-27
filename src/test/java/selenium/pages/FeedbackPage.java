package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FeedbackPage {
    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final By NAME_FIELD = By.id("fb_name");
    private static final By AGE_FIELD = By.id("fb_age");
    private static final By LANGUAGE_CHECKBOXES = By.cssSelector("input[name='language']");
    private static final By GENDER_RADIOS = By.cssSelector("input[name='gender']");
    private static final By OPTION_SELECT = By.id("like_us");
    private static final By COMMENT_FIELD = By.name("comment");
    private static final By SEND_BUTTON = By.cssSelector("#fb_form button[type='submit']");
    private static final By CONFIRMATION_PANEL = By.id("fb_thx");
    private static final By YES_BUTTON = By.xpath("//button[normalize-space()='Yes']");
    private static final By NO_BUTTON = By.xpath("//button[normalize-space()='No']");
    private static final By THANK_YOU_MESSAGE = By.id("message");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public FeedbackPage open() {
        driver.get(FEEDBACK_URL);
        wait.until(webDriver -> webDriver.findElement(NAME_FIELD).isDisplayed());
        return this;
    }

    public String getName() {
        return valueOf(NAME_FIELD);
    }

    public String getAge() {
        return valueOf(AGE_FIELD);
    }

    public String getComment() {
        return valueOf(COMMENT_FIELD);
    }

    public List<Boolean> getLanguageSelections() {
        return driver.findElements(LANGUAGE_CHECKBOXES).stream()
                .map(WebElement::isSelected)
                .toList();
    }

    public int getGenderOptionCount() {
        return driver.findElements(GENDER_RADIOS).size();
    }

    public boolean isGenderSelected(int index) {
        return driver.findElements(GENDER_RADIOS).get(index).isSelected();
    }

    public boolean isDontKnowGenderVisible() {
        return driver.findElements(GENDER_RADIOS).get(2).isDisplayed();
    }

    public boolean isDontKnowGenderEnabled() {
        return driver.findElements(GENDER_RADIOS).get(2).isEnabled();
    }

    public String getSelectedFeedbackOptionText() {
        return new Select(driver.findElement(OPTION_SELECT)).getFirstSelectedOption().getText();
    }

    public String getSelectedFeedbackOptionValue() {
        return new Select(driver.findElement(OPTION_SELECT))
                .getFirstSelectedOption().getAttribute("value");
    }

    public String getSendButtonBackgroundColor() {
        return driver.findElement(SEND_BUTTON).getCssValue("background-color");
    }

    public String getSendButtonTextColor() {
        return driver.findElement(SEND_BUTTON).getCssValue("color");
    }

    public FeedbackPage enterName(String name) {
        driver.findElement(NAME_FIELD).sendKeys(name);
        return this;
    }

    public FeedbackPage fillForm() {
        enterName("Ada Lovelace");
        driver.findElement(AGE_FIELD).sendKeys("36");

        List<WebElement> languages = driver.findElements(LANGUAGE_CHECKBOXES);
        languages.get(0).click();
        languages.get(1).click();
        driver.findElements(GENDER_RADIOS).get(1).click();
        new Select(driver.findElement(OPTION_SELECT)).selectByValue("Good");
        driver.findElement(COMMENT_FIELD).sendKeys("Selenium feedback");
        return this;
    }

    public FeedbackPage submit() {
        driver.findElement(SEND_BUTTON).click();
        wait.until(webDriver -> webDriver.findElement(CONFIRMATION_PANEL).isDisplayed());
        return this;
    }

    public String getConfirmationValue(String field) {
        return driver.findElement(By.id(field)).getText();
    }

    public FeedbackPage confirmFeedback() {
        driver.findElement(YES_BUTTON).click();
        wait.until(webDriver -> webDriver.findElement(THANK_YOU_MESSAGE).isDisplayed());
        return this;
    }

    public FeedbackPage rejectFeedback() {
        driver.findElement(NO_BUTTON).click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("/provide_feedback"));
        return this;
    }

    public String getThankYouMessage() {
        return driver.findElement(THANK_YOU_MESSAGE).getText();
    }

    private String valueOf(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }
}
