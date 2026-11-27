package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeedbackPage {
    private static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final List<String> RESULT_FIELDS =
            List.of("name", "age", "language", "gender", "option", "comment");

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By form = By.id("fb_form");
    private final By nameInput = By.id("fb_name");
    private final By ageInput = By.id("fb_age");
    private final By languageInputs = By.name("language");
    private final By genderInputs = By.name("gender");
    private final By optionSelect = By.id("like_us");
    private final By commentInput = By.name("comment");
    private final By sendButton = By.cssSelector("#fb_form form button[type='submit']");
    private final By thankYouContainer = By.id("fb_thx");
    private final By thankYouText = By.id("message");
    private final By yesButton = By.xpath("//button[normalize-space()='Yes']");
    private final By noButton = By.xpath("//button[normalize-space()='No']");

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    public void open() {
        driver.get(URL);
        wait.until(ExpectedConditions.presenceOfElementLocated(form));
    }

    public void enterName(String name) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(nameInput)).sendKeys(name);
    }

    public void enterAge(String age) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(ageInput)).sendKeys(age);
    }

    public void selectLanguage(String language) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='language'][value='" + language + "']"))).click();
    }

    public void selectGender(String gender) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[name='gender'][value='" + gender + "']"))).click();
    }

    public void selectOption(String option) {
        new Select(wait.until(ExpectedConditions.elementToBeClickable(optionSelect))).selectByValue(option);
    }

    public void enterComment(String comment) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(commentInput)).sendKeys(comment);
    }

    public void fillWholeForm(String name, String age, String language,
                              String gender, String option, String comment) {
        enterName(name);
        enterAge(age);
        selectLanguage(language);
        selectGender(gender);
        selectOption(option);
        enterComment(comment);
    }

    public void clickSend() {
        wait.until(ExpectedConditions.elementToBeClickable(sendButton)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(thankYouContainer));
    }

    public void clickYes() {
        wait.until(ExpectedConditions.elementToBeClickable(yesButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(thankYouText));
    }

    public void clickNo() {
        wait.until(ExpectedConditions.elementToBeClickable(noButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(form));
    }

    public Map<String, String> result() {
        Map<String, String> values = new LinkedHashMap<>();
        for (String field : RESULT_FIELDS) {
            values.put(field, wait.until(ExpectedConditions.presenceOfElementLocated(By.id(field))).getText());
        }
        return values;
    }

    public String thankYouMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(thankYouText)).getText();
    }

    public Map<String, String> formValues() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("name", wait.until(ExpectedConditions.presenceOfElementLocated(nameInput)).getAttribute("value"));
        values.put("age", wait.until(ExpectedConditions.presenceOfElementLocated(ageInput)).getAttribute("value"));
        values.put("language", selectedInputValue("language"));
        values.put("gender", selectedInputValue("gender"));
        values.put("option", new Select(wait.until(ExpectedConditions.presenceOfElementLocated(optionSelect)))
                .getFirstSelectedOption().getAttribute("value"));
        values.put("comment", wait.until(ExpectedConditions.presenceOfElementLocated(commentInput))
                .getAttribute("value"));
        return values;
    }

    public boolean languageSelectedByValue(String value) {
        return driver.findElement(By.cssSelector(
                "input[name='language'][value='" + value + "']")).isSelected();
    }

    public List<Boolean> languageSelectionStates() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(languageInputs)).stream()
                .map(WebElement::isSelected)
                .collect(Collectors.toList());
    }

    public boolean genderSelectedByValue(String value) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(
                "input[name='gender'][value='" + value + "']:checked"))).isSelected();
    }

    public boolean genderRadioSelectedByValue(String value) {
        return driver.findElement(By.cssSelector(
                "input[name='gender'][value='" + value + "']")).isSelected();
    }

    public List<String> genderValues() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(genderInputs)).stream()
                .map(gender -> gender.getAttribute("value"))
                .collect(Collectors.toList());
    }

    public List<Boolean> genderSelectionStates() {
        return driver.findElements(genderInputs).stream()
                .map(WebElement::isSelected)
                .collect(Collectors.toList());
    }

    public String selectedOptionText() {
        return new Select(wait.until(ExpectedConditions.presenceOfElementLocated(optionSelect)))
                .getFirstSelectedOption().getText();
    }

    public String selectedOptionValue() {
        return new Select(wait.until(ExpectedConditions.presenceOfElementLocated(optionSelect)))
                .getFirstSelectedOption().getAttribute("value");
    }

    public String nameValue() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(nameInput)).getAttribute("value");
    }

    public String ageValue() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(ageInput)).getAttribute("value");
    }

    public String commentValue() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(commentInput)).getAttribute("value");
    }

    public String sendButtonBackgroundColor() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(sendButton))
                .getCssValue("background-color");
    }

    public String sendButtonTextColor() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(sendButton)).getCssValue("color");
    }

    private String selectedInputValue(String name) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("input[name='" + name + "']:checked")))
                .getAttribute("value");
    }
}
