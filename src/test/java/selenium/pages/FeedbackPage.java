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

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(PAGE_URL);
    }

    private WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement nameField() {
        return waitForElement(By.id("fb_name"));
    }

    public WebElement ageField() {
        return waitForElement(By.id("fb_age"));
    }

    public WebElement commentField() {
        return waitForElement(By.cssSelector("textarea[name='comment']"));
    }

    public WebElement submitButton() {
        return waitForElement(By.cssSelector("button[type='submit']"));
    }

    public WebElement genderOption(String gender) {
        return waitForElement(By.cssSelector("input[name='gender'][value='" + gender + "']"));
    }

    public WebElement unknownGenderOption() {
        return waitForElement(By.cssSelector("input[name='gender'][disabled][checked]"));
    }

    public List<WebElement> languageOptions() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("input[name='language']")));
    }

    public WebElement languageOption(String language) {
        return waitForElement(By.cssSelector("input[name='language'][value='" + language + "']"));
    }

    public List<WebElement> selectedGenderOptions() {
        return driver.findElements(By.cssSelector("input[name='gender']:not([disabled]):checked"));
    }

    public String firstSelectedOptionText() {
        return new Select(waitForElement(By.id("like_us"))).getFirstSelectedOption().getText();
    }

    public void fill(String name, String age, String gender, String option,
                     List<String> languages, String comment) {
        nameField().clear();
        nameField().sendKeys(name);
        ageField().clear();
        ageField().sendKeys(age);

        for (String language : languages) {
            languageOption(language).click();
        }
        if (gender != null) {
            genderOption(gender).click();
        }

        new Select(waitForElement(By.id("like_us"))).selectByVisibleText(option);
        commentField().clear();
        commentField().sendKeys(comment);
    }

    public void submit() {
        submitButton().click();
        waitForUrl("check_feedback");
    }

    public void confirmYes() {
        waitForElement(By.xpath("//button[normalize-space()='Yes']")).click();
        waitForUrl("thank_you_for_feedback");
    }

    public void chooseNo() {
        waitForElement(By.xpath("//button[normalize-space()='No']")).click();
        waitForUrl("provide_feedback");
    }

    public String submittedValue(String fieldName) {
        return waitForElement(By.id(fieldName)).getText().trim();
    }

    public String thankYouMessage() {
        return waitForElement(By.id("message")).getText();
    }

    public void waitForUrl(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
    }
}
