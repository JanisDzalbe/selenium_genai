package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the feedback review and confirmation page.
 */
public class FeedbackReviewPage {
    public static final String URL_PREFIX =
            "https://janisdzalbe.github.io/example-site/tasks/check_feedback.html";

    private final WebDriver driver;

    @FindBy(id = "name")
    private WebElement name;

    @FindBy(id = "age")
    private WebElement age;

    @FindBy(id = "language")
    private WebElement language;

    @FindBy(id = "gender")
    private WebElement gender;

    @FindBy(id = "option")
    private WebElement option;

    @FindBy(id = "comment")
    private WebElement comment;

    @FindBy(xpath = "//button[normalize-space()='Yes']")
    private WebElement yesButton;

    @FindBy(xpath = "//button[normalize-space()='No']")
    private WebElement noButton;

    public FeedbackReviewPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void waitUntilLoaded() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getCurrentUrl().startsWith(URL_PREFIX));
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> name.isDisplayed());
    }

    public String getSummary(String field) {
        return switch (field) {
            case "name" -> name.getText();
            case "age" -> age.getText();
            case "language" -> language.getText();
            case "gender" -> gender.getText();
            case "option" -> option.getText();
            case "comment" -> comment.getText();
            default -> throw new IllegalArgumentException("Unknown summary field: " + field);
        };
    }

    public FeedbackThankYouPage clickYes() {
        yesButton.click();
        return new FeedbackThankYouPage(driver);
    }

    public FeedbackPage clickNo() {
        noButton.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> d.getCurrentUrl().equals(FeedbackPage.URL));
        return new FeedbackPage(driver);
    }
}
