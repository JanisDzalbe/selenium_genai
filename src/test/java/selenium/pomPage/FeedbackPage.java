package selenium.pomPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FeedbackPage {
    public static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By name = By.id("fb_name");
    private final By age = By.id("fb_age");
    private final By comment = By.cssSelector("textarea[name='comment']");
    private final By languages = By.cssSelector("input[name='language']");
    private final By option = By.id("like_us");
    private final By sendButton = By.xpath("//button[@type='submit' and normalize-space()='Send']");

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public FeedbackPage open() {
        driver.get(URL);
        return this;
    }

    public String nameValue() {
        return driver.findElement(name).getAttribute("value");
    }

    public String ageValue() {
        return driver.findElement(age).getAttribute("value");
    }

    public String commentValue() {
        return driver.findElement(comment).getAttribute("value");
    }

    public List<WebElement> languages() {
        return driver.findElements(languages);
    }

    public WebElement gender(String value) {
        return driver.findElement(By.cssSelector("input[name='gender'][value='" + value + "']"));
    }

    public WebElement unknownGender() {
        return gender("");
    }

    public Select options() {
        return new Select(driver.findElement(option));
    }

    public WebElement sendButton() {
        return driver.findElement(sendButton);
    }

    public void enterName(String value) {
        driver.findElement(name).sendKeys(value);
    }

    public void enterAge(String value) {
        driver.findElement(age).sendKeys(value);
    }

    public void enterComment(String value) {
        driver.findElement(comment).sendKeys(value);
    }

    public void selectLanguage(String value) {
        driver.findElement(By.cssSelector("input[name='language'][value='" + value + "']")).click();
    }

    public void selectGender(String value) {
        gender(value).click();
    }

    public void selectOption(String visibleText) {
        options().selectByVisibleText(visibleText);
    }

    public FeedbackResultPage submit() {
        sendButton().click();
        wait.until(d -> d.getCurrentUrl() != null
                && d.getCurrentUrl().contains("check_feedback.html"));
        return new FeedbackResultPage(driver);
    }
}
