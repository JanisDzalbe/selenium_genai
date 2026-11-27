package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FeedbackPage {
    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public FeedbackPage open() {
        driver.get(FEEDBACK_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_form")));
        return this;
    }

    public String getName() {
        return valueOf(By.id("fb_name"));
    }

    public String getAge() {
        return valueOf(By.id("fb_age"));
    }

    public String getComment() {
        return valueOf(By.name("comment"));
    }

    public List<String> getSelectedLanguages() {
        List<String> languages = new ArrayList<>();
        for (WebElement checkbox : driver.findElements(By.cssSelector("input[name='language']"))) {
            if (checkbox.isSelected()) {
                languages.add(checkbox.getAttribute("value"));
            }
        }
        return languages;
    }

    public boolean isGenderSelected(String gender) {
        return driver.findElement(By.cssSelector("input[name='gender'][value='" + gender + "']")).isSelected();
    }

    public boolean isDefaultGenderSelected() {
        return driver.findElement(By.cssSelector("input[name='gender']:disabled")).isSelected();
    }

    public String getSelectedOptionValue() {
        return selectedOption().getAttribute("value");
    }

    public String getSelectedOptionText() {
        return selectedOption().getText();
    }

    public String getSubmitButtonBackgroundColor() {
        return Color.fromString(driver.findElement(By.cssSelector("button[type='submit']"))
                .getCssValue("background-color")).asRgb();
    }

    public String getSubmitButtonTextColor() {
        return Color.fromString(driver.findElement(By.cssSelector("button[type='submit']"))
                .getCssValue("color")).asRgb();
    }

    public FeedbackPage fillForm(String name, String age, List<String> languages,
                                 String gender, String option, String comment) {
        enterName(name);
        enterAge(age);
        for (String language : languages) {
            selectLanguage(language);
        }
        selectGender(gender);
        selectOption(option);
        enterComment(comment);
        return this;
    }

    public FeedbackPage enterName(String name) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
        return this;
    }

    public FeedbackPage submit() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        return this;
    }

    public FeedbackPage confirmFeedback() {
        clickConfirmationButton("Yes");
        return this;
    }

    public FeedbackPage rejectFeedback() {
        clickConfirmationButton("No");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_name")));
        return this;
    }

    public String getSummaryValue(String fieldId) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.id(fieldId))).getText();
    }

    public String getThankYouMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message"))).getText();
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    private String valueOf(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }

    private WebElement selectedOption() {
        return new Select(driver.findElement(By.id("like_us"))).getFirstSelectedOption();
    }

    private void enterAge(String age) {
        driver.findElement(By.id("fb_age")).sendKeys(age);
    }

    private void selectLanguage(String language) {
        driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).click();
    }

    private void selectGender(String gender) {
        driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).click();
    }

    private void selectOption(String option) {
        new Select(driver.findElement(By.id("like_us"))).selectByValue(option);
    }

    private void enterComment(String comment) {
        driver.findElement(By.name("comment")).sendKeys(comment);
    }

    private void clickConfirmationButton(String label) {
        By locator = By.xpath("//button[normalize-space()='" + label + "']");
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }
}
