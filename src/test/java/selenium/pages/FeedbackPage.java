package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class FeedbackPage {
    static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get(PAGE_URL);
        wait.until(webDriver -> webDriver.getCurrentUrl().equals(PAGE_URL));
    }

    public String getName() {
        return driver.findElement(By.id("fb_name")).getAttribute("value");
    }

    public String getAge() {
        return driver.findElement(By.id("fb_age")).getAttribute("value");
    }

    public String getComment() {
        return driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value");
    }

    public boolean areAllLanguageOptionsUnselected() {
        return driver.findElements(By.cssSelector("input[name='language']"))
                .stream()
                .noneMatch(WebElement::isSelected);
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    public boolean isUnknownGenderSelected() {
        return driver.findElement(By.cssSelector("input[name='gender'][disabled]")).isSelected();
    }

    public boolean isGenderSelected(String gender) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).isSelected();
    }

    public String getSelectedLikeUsOption() {
        return new Select(driver.findElement(By.id("like_us")))
                .getFirstSelectedOption()
                .getText();
    }

    public String getSubmitButtonBackgroundColor() {
        return submitButton().getCssValue("background-color");
    }

    public String getSubmitButtonTextColor() {
        return submitButton().getCssValue("color");
    }

    public FeedbackPage enterName(String name) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
        return this;
    }

    public FeedbackPage enterAge(String age) {
        driver.findElement(By.id("fb_age")).sendKeys(age);
        return this;
    }

    public FeedbackPage selectLanguage(String language) {
        driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).click();
        return this;
    }

    public FeedbackPage selectGender(String gender) {
        driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).click();
        return this;
    }

    public FeedbackPage selectLikeUsOption(String option) {
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(option);
        return this;
    }

    public FeedbackPage enterComment(String comment) {
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys(comment);
        return this;
    }

    public FeedbackReviewPage submit() {
        submitButton().click();
        wait.until(webDriver -> webDriver.getCurrentUrl().contains("check_feedback.html"));
        return new FeedbackReviewPage(driver);
    }

    private WebElement submitButton() {
        return driver.findElement(By.cssSelector("button[type='submit']"));
    }
}
