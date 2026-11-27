package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
public final class FeedbackPage {
    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private static final By NAME = By.id("fb_name");
    private static final By AGE = By.id("fb_age");
    private static final By COMMENT = By.name("comment");
    private static final By LANGUAGES =
            By.cssSelector("input[type='checkbox'][name='language']");
    private static final By GENDERS = By.cssSelector("input[type='radio'][name='gender']");
    private static final By OPTION = By.id("like_us");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public void open() {
        driver.get(FEEDBACK_URL);
        wait.until(d -> d.findElement(NAME).isDisplayed());
    }

    public String name() {
        return value(NAME);
    }

    public String age() {
        return value(AGE);
    }

    public String comment() {
        return value(COMMENT);
    }

    public int languageCount() {
        return driver.findElements(LANGUAGES).size();
    }

    public boolean anyLanguageSelected() {
        return driver.findElements(LANGUAGES).stream().anyMatch(WebElement::isSelected);
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    public int genderCount() {
        return driver.findElements(GENDERS).size();
    }

    public boolean isGenderSelectedAt(int index) {
        return driver.findElements(GENDERS).get(index).isSelected();
    }

    public boolean isGenderEnabledAt(int index) {
        return driver.findElements(GENDERS).get(index).isEnabled();
    }

    public boolean isGenderSelected(String gender) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).isSelected();
    }

    public String selectedOptionText() {
        return new Select(driver.findElement(OPTION)).getFirstSelectedOption().getText();
    }

    public String submitButtonBackgroundColor() {
        return driver.findElement(SUBMIT_BUTTON).getCssValue("background-color");
    }

    public String submitButtonTextColor() {
        return driver.findElement(SUBMIT_BUTTON).getCssValue("color");
    }

    public void fillDefaultFeedback() {
        driver.findElement(NAME).sendKeys("Alex Tester");
        driver.findElement(AGE).sendKeys("31");
        driver.findElement(By.cssSelector(
                "input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector(
                "input[name='language'][value='French']")).click();
        driver.findElement(By.cssSelector(
                "input[name='gender'][value='female']")).click();
        new Select(driver.findElement(OPTION)).selectByVisibleText("Good");
        driver.findElement(COMMENT).sendKeys("Great experience");
    }

    public void enterName(String name) {
        driver.findElement(NAME).sendKeys(name);
    }

    public void submit() {
        driver.findElement(SUBMIT_BUTTON).click();
        wait.until(d -> d.getCurrentUrl().contains("/check_feedback.html"));
    }

    public String summaryText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    public void confirmSubmission() {
        clickConfirmationButton("Yes");
        wait.until(d -> d.findElement(By.tagName("h2")).isDisplayed());
    }

    public void rejectSubmission() {
        clickConfirmationButton("No");
        wait.until(d -> d.getCurrentUrl().contains("/provide_feedback"));
    }

    public String thankYouHeading() {
        return wait.until(d -> d.findElement(By.tagName("h2"))).getText();
    }

    private String value(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }

    private void clickConfirmationButton(String label) {
        driver.findElement(By.xpath("//button[normalize-space()='" + label + "']")).click();
    }
}
