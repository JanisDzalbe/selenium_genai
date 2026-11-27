package selenium.pages;

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

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public FeedbackPage open() {
        driver.get(PAGE_URL);
        wait.until(d -> d.findElement(By.id("fb_name")).isDisplayed());
        return this;
    }

    public String nameValue() {
        return value(By.id("fb_name"));
    }

    public String ageValue() {
        return value(By.id("fb_age"));
    }

    public String commentValue() {
        return value(By.cssSelector("textarea[name='comment']"));
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    public boolean isGenderSelected(String gender) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).isSelected();
    }

    public List<Boolean> languageSelections() {
        return driver.findElements(By.cssSelector("input[name='language']"))
                .stream()
                .map(WebElement::isSelected)
                .toList();
    }

    public List<Boolean> enabledGenderSelections() {
        return driver.findElements(By.cssSelector("input[name='gender']"))
                .stream()
                .filter(WebElement::isEnabled)
                .map(WebElement::isSelected)
                .toList();
    }

    public boolean isUnknownGenderSelected() {
        return driver.findElement(
                By.cssSelector("input[name='gender'][disabled][checked]")).isSelected();
    }

    public boolean isUnknownGenderEnabled() {
        return driver.findElement(
                By.cssSelector("input[name='gender'][disabled][checked]")).isEnabled();
    }

    public String selectedRating() {
        return new Select(driver.findElement(By.id("like_us")))
                .getFirstSelectedOption().getText();
    }

    public String sendButtonClass() {
        return driver.findElement(By.cssSelector("button[type='submit']")).getAttribute("class");
    }

    public String sendButtonBackgroundColor() {
        return driver.findElement(By.cssSelector("button[type='submit']"))
                .getCssValue("background-color");
    }

    public String sendButtonTextColor() {
        return driver.findElement(By.cssSelector("button[type='submit']"))
                .getCssValue("color");
    }

    public FeedbackPage enterName(String name) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
        return this;
    }

    public FeedbackPage fillWholeForm() {
        driver.findElement(By.id("fb_name")).sendKeys("Janis");
        driver.findElement(By.id("fb_age")).sendKeys("30");
        driver.findElement(By.cssSelector("input[name='language'][value='English']")).click();
        driver.findElement(By.cssSelector("input[name='language'][value='French']")).click();
        driver.findElement(By.cssSelector("input[name='gender'][value='male']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText("Good");
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys("Great example site");
        return this;
    }

    public FeedbackConfirmationPage submit() {
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));
        wait.until(d -> d.findElement(By.cssSelector("div#fb_thx")).isDisplayed());
        return new FeedbackConfirmationPage(driver);
    }

    private String value(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }
}
