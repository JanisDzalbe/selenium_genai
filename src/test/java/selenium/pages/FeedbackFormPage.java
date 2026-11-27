package selenium.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FeedbackFormPage {
    private static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final By NAME = By.id("fb_name");
    private static final By AGE = By.id("fb_age");
    private static final By COMMENT = By.name("comment");
    private static final By LIKE_US = By.id("like_us");
    private static final By SEND = By.cssSelector("#fb_form button[type='submit']");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;

    public FeedbackFormPage(WebDriver driver) {
        this.driver = driver;
    }

    public FeedbackFormPage open() {
        driver.get(URL);
        return this;
    }

    public String nameValue() {
        return value(NAME);
    }

    public String ageValue() {
        return value(AGE);
    }

    public String commentValue() {
        return value(COMMENT);
    }

    public List<Boolean> languageSelections() {
        return driver.findElements(By.cssSelector("#lang_check input[type='checkbox']"))
                .stream()
                .map(WebElement::isSelected)
                .toList();
    }

    public boolean languageSelected(String value) {
        return driver.findElement(By.cssSelector(
                "#lang_check input[name='language'][value='" + value + "']")).isSelected();
    }

    public boolean genderSelected(String value) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + value + "']")).isSelected();
    }

    public boolean unknownGenderSelectedAndDisabled() {
        WebElement unknown = driver.findElement(By.cssSelector("input[name='gender'][disabled]"));
        return unknown.isSelected() && !unknown.isEnabled();
    }

    public String selectedLikeUsText() {
        return new Select(driver.findElement(LIKE_US)).getFirstSelectedOption().getText();
    }

    public String selectedLikeUsValue() {
        return new Select(driver.findElement(LIKE_US)).getFirstSelectedOption().getAttribute("value");
    }

    public String sendButtonBackground() {
        return cssColor(SEND, "background-color");
    }

    public String sendButtonTextColor() {
        return cssColor(SEND, "color");
    }

    public FeedbackFormPage fill(String name, String age, String comment) {
        driver.findElement(NAME).sendKeys(name);
        driver.findElement(AGE).sendKeys(age);
        driver.findElement(language("English")).click();
        driver.findElement(language("French")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='female']")).click();
        new Select(driver.findElement(LIKE_US)).selectByValue("Good");
        driver.findElement(COMMENT).sendKeys(comment);
        return this;
    }

    public FeedbackFormPage enterName(String name) {
        driver.findElement(NAME).sendKeys(name);
        return this;
    }

    public FeedbackReviewPage submit() {
        driver.findElement(SEND).click();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(webDriver -> webDriver.getCurrentUrl().contains("check_feedback.html"));
        return new FeedbackReviewPage(driver);
    }

    private String value(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }

    private String cssColor(By locator, String property) {
        return Color.fromString(driver.findElement(locator).getCssValue(property)).asHex();
    }

    private By language(String value) {
        return By.cssSelector("#lang_check input[name='language'][value='" + value + "']");
    }
}
