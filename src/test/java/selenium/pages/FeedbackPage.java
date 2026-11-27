package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/** Page object for entering feedback. */
public class FeedbackPage {
    private final WebDriver driver;

    @FindBy(id = "fb_name")
    private WebElement name;

    @FindBy(id = "fb_age")
    private WebElement age;

    @FindBy(css = "input[name='language']")
    private List<WebElement> languages;

    @FindBy(css = "input[name='language']:checked")
    private List<WebElement> selectedLanguages;

    @FindBy(css = "input[name='gender'][disabled]")
    private WebElement disabledGender;

    @FindBy(css = "input[name='gender']")
    private List<WebElement> genders;

    @FindBy(id = "like_us")
    private WebElement likeUs;

    @FindBy(css = "textarea[name='comment']")
    private WebElement comment;

    @FindBy(css = "button[type='submit']")
    private WebElement sendButton;

    /** Initializes this page's elements for the supplied browser. */
    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /** Returns the entered name. */
    public String getName() {
        return name.getAttribute("value");
    }

    /** Enters a name. */
    public void enterName(String value) {
        name.sendKeys(value);
    }

    /** Returns the entered age. */
    public String getAge() {
        return age.getAttribute("value");
    }

    /** Returns the currently selected language inputs. */
    public List<WebElement> getSelectedLanguages() {
        return selectedLanguages;
    }

    /** Reports whether the disabled gender option is selected. */
    public boolean isDisabledGenderSelected() {
        return disabledGender.isSelected();
    }

    /** Reports whether a language is selected. */
    public boolean isLanguageSelected(String value) {
        return languages.stream()
                .anyMatch(language -> value.equals(language.getAttribute("value")) && language.isSelected());
    }

    /** Reports whether a gender is selected. */
    public boolean isGenderSelected(String value) {
        return genders.stream()
                .anyMatch(gender -> value.equals(gender.getAttribute("value")) && gender.isSelected());
    }

    /** Returns the selected feedback option. */
    public String getSelectedOption() {
        return new Select(likeUs).getFirstSelectedOption().getText();
    }

    /** Returns the entered comment. */
    public String getComment() {
        return comment.getAttribute("value");
    }

    /** Returns the form's submit button. */
    public WebElement getSendButton() {
        return sendButton;
    }

    /** Fills all feedback fields. */
    public void fillForm(String nameValue, String ageValue, String language, String gender,
                         String option, String commentValue) {
        name.sendKeys(nameValue);
        age.sendKeys(ageValue);
        languages.stream()
                .filter(element -> language.equals(element.getAttribute("value")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Language not found: " + language))
                .click();
        genders.stream()
                .filter(element -> gender.equals(element.getAttribute("value")))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Gender not found: " + gender))
                .click();
        new Select(likeUs).selectByVisibleText(option);
        comment.sendKeys(commentValue);
    }

    /** Submits the form and returns the review page object. */
    public FeedbackReviewPage submit() {
        sendButton.click();
        return new FeedbackReviewPage(driver);
    }
}
