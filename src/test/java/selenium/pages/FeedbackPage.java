package selenium.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FeedbackPage {
    public static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    private static final Duration WAIT_SECONDS = Duration.ofSeconds(10);
    private static final By NAME = By.id("fb_name");
    private static final By AGE = By.id("fb_age");
    private static final By COMMENT = By.name("comment");
    private static final By LANGUAGES = By.cssSelector("#lang_check input[type='checkbox']");
    private static final By LIKE_US = By.id("like_us");
    private static final By SUBMIT = By.cssSelector("#fb_form button[type='submit']");
    private static final By THANK_YOU = By.id("fb_thx");
    private static final By MESSAGE = By.id("message");

    private final WebDriver driver;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open() {
        driver.get(URL);
    }

    public String getNameValue() {
        return driver.findElement(NAME).getAttribute("value");
    }

    public String getAgeValue() {
        return driver.findElement(AGE).getAttribute("value");
    }

    public String getCommentValue() {
        return driver.findElement(COMMENT).getAttribute("value");
    }

    public boolean areLanguagesSelected() {
        return driver.findElements(LANGUAGES).stream().anyMatch(WebElement::isSelected);
    }

    public boolean isGenderSelected(String value) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + value + "']")).isSelected();
    }

    public boolean isDisabledGenderSelected() {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][disabled]")).isSelected();
    }

    public String getSelectedOptionText() {
        return new Select(driver.findElement(LIKE_US)).getFirstSelectedOption().getText();
    }

    public String getSelectedOptionValue() {
        return new Select(driver.findElement(LIKE_US)).getFirstSelectedOption().getAttribute("value");
    }

    public WebElement getSendButton() {
        return driver.findElement(SUBMIT);
    }

    public void enterName(String name) {
        driver.findElement(NAME).sendKeys(name);
    }

    public void enterAge(String age) {
        driver.findElement(AGE).sendKeys(age);
    }

    public void enterComment(String comment) {
        driver.findElement(COMMENT).sendKeys(comment);
    }

    public void selectLanguage(String language) {
        driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).click();
    }

    public void selectGender(String gender) {
        driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).click();
    }

    public void selectOption(String option) {
        new Select(driver.findElement(LIKE_US)).selectByValue(option);
    }

    public void fillForm(String name, String age, String comment) {
        enterName(name);
        enterAge(age);
        selectLanguage("English");
        selectLanguage("French");
        selectGender("female");
        selectOption("Good");
        enterComment(comment);
    }

    public void submit() {
        getSendButton().click();
        waitForResultPage();
    }

    public void clickYes() {
        driver.findElement(By.xpath("//button[normalize-space()='Yes']")).click();
    }

    public void clickNo() {
        driver.findElement(By.xpath("//button[normalize-space()='No']")).click();
    }

    public void waitForFeedbackForm() {
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver ->
                currentDriver.getCurrentUrl().contains("provide_feedback")
                        && currentDriver.findElement(NAME).isDisplayed());
    }

    public String waitForMessage() {
        return new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver -> {
            String text = currentDriver.findElement(MESSAGE).getText();
            return text.isBlank() ? null : text;
        });
    }

    public String getSummaryValue(String fieldId) {
        return driver.findElement(By.id(fieldId)).getText();
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    private void waitForResultPage() {
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver ->
                currentDriver.getCurrentUrl().contains("check_feedback.html")
                        && currentDriver.findElement(THANK_YOU).isDisplayed());
    }
}
