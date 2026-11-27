package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/** Page object for reviewing submitted feedback. */
public class FeedbackReviewPage {
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

    /** Initializes this page's elements for the supplied browser. */
    public FeedbackReviewPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /** Returns the reviewed name. */
    public String getName() {
        return name.getText();
    }

    /** Returns the reviewed age. */
    public String getAge() {
        return age.getText();
    }

    /** Returns the reviewed language. */
    public String getLanguage() {
        return language.getText();
    }

    /** Returns the reviewed gender. */
    public String getGender() {
        return gender.getText();
    }

    /** Returns the reviewed option. */
    public String getOption() {
        return option.getText();
    }

    /** Returns the reviewed comment. */
    public String getComment() {
        return comment.getText();
    }

    /** Confirms the feedback and returns the thank-you page object. */
    public ThankYouPage confirm() {
        yesButton.click();
        return new ThankYouPage(driver);
    }

    /** Returns to the feedback form and returns its page object. */
    public FeedbackPage edit() {
        noButton.click();
        return new FeedbackPage(driver);
    }
}
