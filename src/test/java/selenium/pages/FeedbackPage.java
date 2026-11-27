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
    public static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String name() {
        return driver.findElement(By.id("fb_name")).getAttribute("value");
    }

    public String age() {
        return driver.findElement(By.id("fb_age")).getAttribute("value");
    }

    public String comment() {
        return driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value");
    }

    public boolean areLanguagesUnselected() {
        return driver.findElements(By.cssSelector("input[name='language']"))
                .stream()
                .noneMatch(WebElement::isSelected);
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    public boolean isGenderSelected(String gender) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).isSelected();
    }

    public boolean isUnknownGenderSelected() {
        return driver.findElement(By.cssSelector("input[name='gender'][disabled][checked]"))
                .isSelected();
    }

    public boolean isGenderEnabled(String gender) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).isEnabled();
    }

    public String selectedOption() {
        return new Select(driver.findElement(By.id("like_us")))
                .getFirstSelectedOption().getText();
    }

    public String sendButtonClass() {
        return driver.findElement(sendButton()).getAttribute("class");
    }

    public String sendButtonColor() {
        return driver.findElement(sendButton()).getCssValue("color");
    }

    public void enterName(String name) {
        WebElement nameField = driver.findElement(By.id("fb_name"));
        nameField.clear();
        nameField.sendKeys(name);
    }

    public void fillForm(String name, String age, String language,
                         String gender, String option, String comment) {
        enterName(name);
        WebElement ageField = driver.findElement(By.id("fb_age"));
        ageField.clear();
        ageField.sendKeys(age);

        List<WebElement> languages = driver.findElements(By.cssSelector("input[name='language']"));
        for (WebElement checkbox : languages) {
            if (!checkbox.isSelected() && checkbox.getAttribute("value").equals(language)) {
                checkbox.click();
            }
        }

        driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).click();
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(option);
        WebElement commentField = driver.findElement(By.cssSelector("textarea[name='comment']"));
        commentField.clear();
        commentField.sendKeys(comment);
    }

    public FeedbackConfirmationPage submit() {
        wait.until(ExpectedConditions.elementToBeClickable(sendButton())).click();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));
        return new FeedbackConfirmationPage(driver);
    }

    private By sendButton() {
        return By.xpath("//button[normalize-space()='Send']");
    }
}
