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
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By nameInput = By.id("fb_name");
    private final By ageInput = By.id("fb_age");
    private final By commentInput = By.cssSelector("textarea[name='comment']");
    private final By sendButton = By.cssSelector("button[type='submit']");
    private final By likeUsSelect = By.id("like_us");

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public FeedbackPage open() {
        driver.get(PAGE_URL);
        return this;
    }

    public String name() {
        return driver.findElement(nameInput).getAttribute("value");
    }

    public String age() {
        return driver.findElement(ageInput).getAttribute("value");
    }

    public String comment() {
        return driver.findElement(commentInput).getAttribute("value");
    }

    public boolean areLanguagesUnselected() {
        return driver.findElements(By.cssSelector("input[name='language']")).stream()
                .noneMatch(WebElement::isSelected);
    }

    public boolean isLanguageSelected(String language) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).isSelected();
    }

    public List<Boolean> genderSelections() {
        return driver.findElements(By.cssSelector("input[name='gender']")).stream()
                .map(WebElement::isSelected)
                .toList();
    }

    public String selectedLikeUsOption() {
        return new Select(driver.findElement(likeUsSelect))
                .getFirstSelectedOption().getText();
    }

    public String sendButtonBackgroundColor() {
        return driver.findElement(sendButton).getCssValue("background-color");
    }

    public String sendButtonTextColor() {
        return driver.findElement(sendButton).getCssValue("color");
    }

    public void enterName(String name) {
        driver.findElement(nameInput).sendKeys(name);
    }

    public void fillForm(String name, String age, String language,
                         String gender, String option, String comment) {
        driver.findElement(nameInput).sendKeys(name);
        driver.findElement(ageInput).sendKeys(age);
        driver.findElement(By.cssSelector(
                "input[name='language'][value='" + language + "']")).click();
        driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + gender + "']")).click();
        new Select(driver.findElement(likeUsSelect)).selectByVisibleText(option);
        driver.findElement(commentInput).sendKeys(comment);
    }

    public FeedbackReviewPage submit() {
        driver.findElement(sendButton).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback"));
        return new FeedbackReviewPage(driver);
    }
}
