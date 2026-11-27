package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackPage {
    private static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final By NAME_INPUT = By.id("fb_name");
    private static final By AGE_INPUT = By.id("fb_age");
    private static final By COMMENT_INPUT = By.name("comment");
    private static final By LANGUAGE_OPTIONS = By.cssSelector("input[name='language']");
    private static final By LIKE_US_SELECT = By.id("like_us");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public FeedbackPage open() {
        driver.get(URL);
        return this;
    }

    public String getNameInputValue() {
        return driver.findElement(NAME_INPUT).getAttribute("value");
    }

    public String getAgeInputValue() {
        return driver.findElement(AGE_INPUT).getAttribute("value");
    }

    public String getCommentInputValue() {
        return driver.findElement(COMMENT_INPUT).getAttribute("value");
    }

    public boolean areLanguagesUnselected() {
        return driver.findElements(LANGUAGE_OPTIONS).stream()
                .noneMatch(option -> option.isSelected());
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    public boolean isGenderSelected(String gender) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).isSelected();
    }

    public boolean isDefaultGenderSelected() {
        return driver.findElement(By.cssSelector("input[name='gender'][disabled]"))
                .isSelected();
    }

    public String getSelectedLikeUsOption() {
        return new Select(driver.findElement(LIKE_US_SELECT))
                .getFirstSelectedOption().getText();
    }

    public String getSubmitButtonBackgroundColor() {
        return driver.findElement(SUBMIT_BUTTON).getCssValue("background-color");
    }

    public String getSubmitButtonTextColor() {
        return driver.findElement(SUBMIT_BUTTON).getCssValue("color");
    }

    public FeedbackPage fill(String name, String age, String language,
                             String gender, String option, String comment) {
        driver.findElement(NAME_INPUT).sendKeys(name);
        driver.findElement(AGE_INPUT).sendKeys(age);
        driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).click();
        driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).click();
        new Select(driver.findElement(LIKE_US_SELECT)).selectByVisibleText(option);
        driver.findElement(COMMENT_INPUT).sendKeys(comment);
        return this;
    }

    public FeedbackPage enterName(String name) {
        driver.findElement(NAME_INPUT).sendKeys(name);
        return this;
    }

    public FeedbackReviewPage submit() {
        driver.findElement(SUBMIT_BUTTON).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));
        return new FeedbackReviewPage(driver);
    }
}
