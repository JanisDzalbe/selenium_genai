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
    private static final String FEEDBACK_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public void open() {
        driver.manage().window().maximize();
        driver.get(FEEDBACK_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_form")));
    }

    public String getNameValue() {
        return driver.findElement(By.id("fb_name")).getAttribute("value");
    }

    public String getAgeValue() {
        return driver.findElement(By.id("fb_age")).getAttribute("value");
    }

    public List<String> getLanguageValues() {
        return driver.findElements(By.cssSelector("input[name='language']")).stream()
                .map(language -> language.getAttribute("value"))
                .toList();
    }

    public boolean isLanguageSelected(String value) {
        return findOption("language", value).isSelected();
    }

    public List<String> getGenderValues() {
        return driver.findElements(By.cssSelector("input[name='gender']")).stream()
                .map(gender -> gender.getAttribute("value"))
                .toList();
    }

    public boolean isGenderSelected(String value) {
        return findOption("gender", value).isSelected();
    }

    public boolean isGenderDisplayed(String value) {
        return findOption("gender", value).isDisplayed();
    }

    public String getSelectedRating() {
        return new Select(driver.findElement(By.id("like_us")))
                .getFirstSelectedOption().getText();
    }

    public String getCommentValue() {
        return driver.findElement(By.cssSelector("textarea[name='comment']")).getAttribute("value");
    }

    public String getSendButtonBackgroundColor() {
        return sendButton().getCssValue("background-color");
    }

    public String getSendButtonColor() {
        return sendButton().getCssValue("color");
    }

    public void enterName(String name) {
        driver.findElement(By.id("fb_name")).sendKeys(name);
    }

    public void enterAge(String age) {
        driver.findElement(By.id("fb_age")).sendKeys(age);
    }

    public void selectLanguage(String language) {
        findOption("language", language).click();
    }

    public void selectGender(String gender) {
        findOption("gender", gender).click();
    }

    public void selectRating(String rating) {
        new Select(driver.findElement(By.id("like_us"))).selectByVisibleText(rating);
    }

    public void enterComment(String comment) {
        driver.findElement(By.cssSelector("textarea[name='comment']")).sendKeys(comment);
    }

    public FeedbackConfirmationPage submit() {
        sendButton().click();
        wait.until(ExpectedConditions.urlContains("check_feedback.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fb_thx")));
        return new FeedbackConfirmationPage(driver);
    }

    private WebElement sendButton() {
        return wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and normalize-space()='Send']")));
    }

    private WebElement findOption(String name, String value) {
        return driver.findElements(By.cssSelector("input[name='" + name + "']")).stream()
                .filter(option -> value.equals(option.getAttribute("value")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No " + name + " option found with value: " + value));
    }
}
