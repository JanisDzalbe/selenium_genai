package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Page Object for the feedback form page.
 */
public class FeedbackPage {
    public static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";

    private final WebDriver driver;

    @FindBy(id = "fb_name")
    private WebElement nameField;

    @FindBy(id = "fb_age")
    private WebElement ageField;

    @FindBy(css = "textarea[name='comment']")
    private WebElement commentField;

    @FindBy(css = "input[name='language']")
    private List<WebElement> languageCheckboxes;

    @FindBy(css = "input[name='gender']")
    private List<WebElement> genderRadios;

    @FindBy(id = "like_us")
    private WebElement likeUsSelect;

    @FindBy(css = "button[type='submit']")
    private WebElement sendButton;

    // PageFactory initializes elements decorated with @FindBy.
    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get(URL);
    }

    public String getName() {
        return nameField.getAttribute("value");
    }

    public String getAge() {
        return ageField.getAttribute("value");
    }

    public String getComment() {
        return commentField.getAttribute("value");
    }

    public List<WebElement> getLanguageCheckboxes() {
        return languageCheckboxes;
    }

    public List<WebElement> getGenderRadios() {
        return genderRadios;
    }

    public String getSelectedOption() {
        return new Select(likeUsSelect).getFirstSelectedOption().getText();
    }

    public String getSendButtonClass() {
        return sendButton.getAttribute("class");
    }

    public String getSendButtonBackgroundColor() {
        return sendButton.getCssValue("background-color");
    }

    public String getSendButtonTextColor() {
        return sendButton.getCssValue("color");
    }

    public void enterName(String name) {
        nameField.clear();
        nameField.sendKeys(name);
    }

    public void enterAge(String age) {
        ageField.clear();
        ageField.sendKeys(age);
    }

    public void enterComment(String comment) {
        commentField.clear();
        commentField.sendKeys(comment);
    }

    public void selectLanguage(String value) {
        for (WebElement checkbox : languageCheckboxes) {
            if (checkbox.getAttribute("value").equals(value)) {
                if (!checkbox.isSelected()) {
                    checkbox.click();
                }
                return;
            }
        }
        throw new IllegalArgumentException("Unknown language: " + value);
    }

    public void selectGender(String value) {
        for (WebElement radio : genderRadios) {
            if (radio.getAttribute("value").equals(value)) {
                radio.click();
                return;
            }
        }
        throw new IllegalArgumentException("Unknown gender: " + value);
    }

    public void selectOption(String visibleText) {
        new Select(likeUsSelect).selectByVisibleText(visibleText);
    }

    public void fillWholeForm(String name, String age) {
        enterName(name);
        enterAge(age);
        selectLanguage("English");
        selectLanguage("French");
        selectGender("male");
        selectOption("Good");
        enterComment("Great service");
    }

    public FeedbackReviewPage submit() {
        sendButton.click();
        return new FeedbackReviewPage(driver);
    }
}
