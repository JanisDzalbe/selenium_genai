package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FeedbackPage {
    private static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final By NAME = By.id("fb_name");
    private static final By AGE = By.id("fb_age");
    private static final By COMMENT = By.name("comment");
    private static final By LANGUAGES = By.cssSelector("#lang_check input[type='checkbox']");
    private static final By GENDERS = By.cssSelector("input[name='gender']");
    private static final By PREFERENCE = By.id("like_us");
    private static final By SUBMIT = By.cssSelector("#fb_form button[type='submit']");

    private final WebDriver driver;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
    }

    public FeedbackPage open() {
        driver.get(URL);
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.findElement(NAME).isDisplayed());
        return this;
    }

    public FeedbackPage fill(String name, String age, List<String> languages, String gender,
                             String preference, String comment) {
        enterName(name);
        driver.findElement(AGE).sendKeys(age);
        for (String language : languages) {
            driver.findElement(languageLocator(language)).click();
        }
        driver.findElement(genderLocator(gender)).click();
        new Select(driver.findElement(PREFERENCE)).selectByVisibleText(preference);
        driver.findElement(COMMENT).sendKeys(comment);
        return this;
    }

    public FeedbackPage enterName(String name) {
        driver.findElement(NAME).sendKeys(name);
        return this;
    }

    public FeedbackSummaryPage submit() {
        driver.findElement(SUBMIT).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.getCurrentUrl() != null
                        && webDriver.getCurrentUrl().contains("check_feedback.html"));
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.findElement(By.id("fb_thx")).isDisplayed());
        return new FeedbackSummaryPage(driver);
    }

    public String nameValue() {
        return inputValue(NAME);
    }

    public String ageValue() {
        return inputValue(AGE);
    }

    public String commentValue() {
        return inputValue(COMMENT);
    }

    public int languageCount() {
        return driver.findElements(LANGUAGES).size();
    }

    public boolean noLanguagesSelected() {
        return driver.findElements(LANGUAGES).stream().noneMatch(WebElement::isSelected);
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(languageLocator(language)).isSelected();
    }

    public int genderCount() {
        return driver.findElements(GENDERS).size();
    }

    public boolean isGenderSelected(String gender) {
        return driver.findElement(genderLocator(gender)).isSelected();
    }

    public boolean isGenderSelectedAt(int index) {
        return driver.findElements(GENDERS).get(index).isSelected();
    }

    public boolean isGenderEnabledAt(int index) {
        return driver.findElements(GENDERS).get(index).isEnabled();
    }

    public String selectedPreference() {
        return new Select(driver.findElement(PREFERENCE)).getFirstSelectedOption().getText();
    }

    public String submitBackgroundColor() {
        return driver.findElement(SUBMIT).getCssValue("background-color");
    }

    public String submitTextColor() {
        return driver.findElement(SUBMIT).getCssValue("color");
    }

    private String inputValue(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }

    private By languageLocator(String language) {
        return By.cssSelector("#lang_check input[value='" + language + "']");
    }

    private By genderLocator(String gender) {
        return By.cssSelector("input[name='gender'][value='" + gender + "']");
    }
}
