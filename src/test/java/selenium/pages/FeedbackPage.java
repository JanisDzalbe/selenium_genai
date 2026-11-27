package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FeedbackPage {
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final By NAME_FIELD = By.id("fb_name");
    private static final By AGE_FIELD = By.id("fb_age");
    private static final By COMMENT_FIELD = By.cssSelector("textarea[name='comment']");
    private static final By LANGUAGE_CHECKBOXES =
            By.cssSelector("input[name='language'][type='checkbox']");
    private static final By GENDER_RADIO_BUTTONS =
            By.cssSelector("input[name='gender'][type='radio']");
    private static final By OPTION_SELECT = By.id("like_us");
    private static final By SEND_BUTTON = By.cssSelector("button[type='submit']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public FeedbackPage open() {
        driver.get(PAGE_URL);
        return this;
    }

    public String nameValue() {
        return driver.findElement(NAME_FIELD).getAttribute("value");
    }

    public String ageValue() {
        return driver.findElement(AGE_FIELD).getAttribute("value");
    }

    public String commentValue() {
        return driver.findElement(COMMENT_FIELD).getAttribute("value");
    }

    public List<Boolean> languageSelections() {
        return driver.findElements(LANGUAGE_CHECKBOXES).stream()
                .map(WebElement::isSelected)
                .toList();
    }

    public int genderRadioButtonCount() {
        return driver.findElements(GENDER_RADIO_BUTTONS).size();
    }

    public List<Boolean> genderSelections() {
        return driver.findElements(GENDER_RADIO_BUTTONS).stream()
                .map(WebElement::isSelected)
                .toList();
    }

    public boolean unknownGenderSelected() {
        return driver.findElement(By.cssSelector("input[name='gender'][disabled]")).isSelected();
    }

    public boolean genderSelected(String value) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + value + "']")).isSelected();
    }

    public String selectedOption() {
        return new Select(driver.findElement(OPTION_SELECT))
                .getFirstSelectedOption().getText();
    }

    public String sendButtonText() {
        return driver.findElement(SEND_BUTTON).getText().trim();
    }

    public String sendButtonBackgroundColor() {
        return driver.findElement(SEND_BUTTON).getCssValue("background-color");
    }

    public String sendButtonTextColor() {
        return driver.findElement(SEND_BUTTON).getCssValue("color");
    }

    public FeedbackPage enterName(String name) {
        driver.findElement(NAME_FIELD).sendKeys(name);
        return this;
    }

    public FeedbackPage fillWholeForm() {
        driver.findElement(NAME_FIELD).sendKeys("Janis");
        driver.findElement(AGE_FIELD).sendKeys("30");

        for (WebElement checkbox : driver.findElements(LANGUAGE_CHECKBOXES)) {
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
        }

        driver.findElement(By.cssSelector("input[name='gender'][value='male']")).click();
        new Select(driver.findElement(OPTION_SELECT)).selectByVisibleText("Good");
        driver.findElement(COMMENT_FIELD).sendKeys("Excellent service");
        return this;
    }

    public boolean languageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    public FeedbackReviewPage submit() {
        driver.findElement(SEND_BUTTON).click();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_thx")));
        return new FeedbackReviewPage(driver);
    }
}
