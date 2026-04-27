package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;
import java.util.List;

public class FeedbackPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // provide_feedback.html locators
    public By nameField = By.id("fb_name");
    public By ageField = By.id("fb_age");
    public By commentField = By.cssSelector("textarea[name='comment']");
    public By likeUsSelect = By.id("like_us");
    public By submitButton = By.cssSelector("button[type='submit']");

    // check_feedback.html locators (confirmation page)
    public By confirmationPage = By.id("fb_thx");
    public By confirmationNameSpan = By.id("name");
    public By yesButton = By.xpath("//button[text()='Yes']");
    public By noButton = By.xpath("//button[text()='No']");

    // thank_you_for_feedback.html locators
    public By thankYouMessage = By.id("message");

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openPage() {
        driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
    }

    public void enterName(String name) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(nameField));
        element.clear();
        element.sendKeys(name);
    }

    public void enterAge(String age) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(ageField));
        element.clear();
        element.sendKeys(age);
    }

    public void enterComment(String comment) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(commentField));
        element.clear();
        element.sendKeys(comment);
    }

    public void selectGender(String gender) {
        String value = gender.equalsIgnoreCase("male") ? "male" : "female";
        driver.findElement(By.cssSelector("input[name='gender'][value='" + value + "']")).click();
    }

    public void selectLikeUs(String option) {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(likeUsSelect)));
        select.selectByVisibleText(option);
    }

    public void submitForm() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
    }

    public boolean isConfirmationPageVisible() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationPage)).isDisplayed();
    }

    public void clickYes() {
        wait.until(ExpectedConditions.elementToBeClickable(yesButton)).click();
    }

    public void clickNo() {
        wait.until(ExpectedConditions.elementToBeClickable(noButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameField));
    }

    public String getThankYouMessage() {
        wait.until(driver -> {
            String text = driver.findElement(thankYouMessage).getText();
            return !text.isEmpty() ? text : null;
        });
        return driver.findElement(thankYouMessage).getText();
    }

    public boolean isFieldEmpty(By locator) {
        String value = driver.findElement(locator).getAttribute("value");
        return value == null || value.isEmpty();
    }

    public String getSelectedGender() {
        List<WebElement> checked = driver.findElements(By.cssSelector("input[name='gender']:checked"));
        if (checked.isEmpty()) return "";
        String val = checked.get(0).getAttribute("value");
        if ("male".equals(val)) return "Male";
        if ("female".equals(val)) return "Female";
        return "Don't know";
    }

    public String getSelectedLikeUs() {
        Select select = new Select(driver.findElement(likeUsSelect));
        return select.getFirstSelectedOption().getText();
    }

    public String getButtonColor() {
        return driver.findElement(submitButton).getCssValue("background-color");
    }

    public WebDriver getDriver() {
        return driver;
    }
}