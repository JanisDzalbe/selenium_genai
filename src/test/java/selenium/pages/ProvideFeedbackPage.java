package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class ProvideFeedbackPage {
    private WebDriver driver;

    // Locators
    private By nameField = By.id("name");
    private By emailField = By.id("email");
    private By ageField = By.id("age");
    private By genreDropdown = By.id("genre");
    private By likeUsDropdown = By.id("like");
    private By commentField = By.id("comment");
    private By anonymousCheckbox = By.id("anonymous");
    private By subscribeCheckbox = By.id("subscribe");
    private By sendButton = By.id("send-btn");
    private By messageText = By.id("message");

    public ProvideFeedbackPage(WebDriver driver) {
        this.driver = driver;
    }

    public void fillName(String name) {
        driver.findElement(nameField).sendKeys(name);
    }

    public void fillEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }

    public void fillAge(String age) {
        driver.findElement(ageField).sendKeys(age);
    }

    public void selectGenre(String genre) {
        new Select(driver.findElement(genreDropdown)).selectByVisibleText(genre);
    }

    public void selectHowDoYouLikeUs(String option) {
        new Select(driver.findElement(likeUsDropdown)).selectByVisibleText(option);
    }

    public void fillComment(String comment) {
        driver.findElement(commentField).sendKeys(comment);
    }

    public void checkAnonymous() {
        if (!driver.findElement(anonymousCheckbox).isSelected()) {
            driver.findElement(anonymousCheckbox).click();
        }
    }

    public void checkSubscribe() {
        if (!driver.findElement(subscribeCheckbox).isSelected()) {
            driver.findElement(subscribeCheckbox).click();
        }
    }

    public void clickSend() {
        driver.findElement(sendButton).click();
    }

    public String getMessage() {
        return driver.findElement(messageText).getText();
    }

    public boolean isFieldEmpty(By locator) {
        return driver.findElement(locator).getAttribute("value").isEmpty();
    }

    public boolean areAllFieldsEmpty() {
        return isFieldEmpty(nameField) && isFieldEmpty(emailField) && isFieldEmpty(ageField) &&
               isFieldEmpty(commentField) && !driver.findElement(anonymousCheckbox).isSelected() &&
               !driver.findElement(subscribeCheckbox).isSelected();
    }

    public String getGenreSelected() {
        return new Select(driver.findElement(genreDropdown)).getFirstSelectedOption().getText();
    }

    public String getLikeUsSelected() {
        return new Select(driver.findElement(likeUsDropdown)).getFirstSelectedOption().getText();
    }

    public String getSendButtonColor() {
        return driver.findElement(sendButton).getCssValue("background-color");
    }
}
