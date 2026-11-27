package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FeedbackPage {
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/provide_feedback";
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final By NAME_FIELD = By.id("fb_name");
    private static final By AGE_FIELD = By.id("fb_age");
    private static final By LANGUAGE_CHECKBOXES = By.name("language");
    private static final By GENDER_RADIOS = By.name("gender");
    private static final By LIKE_US_SELECT = By.id("like_us");
    private static final By COMMENT_FIELD = By.cssSelector("textarea[name='comment']");
    private static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FeedbackPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    public FeedbackPage open() {
        driver.get(PAGE_URL);
        wait.until(d -> d.findElement(NAME_FIELD).isDisplayed());
        return this;
    }

    public FeedbackSummaryPage submitFeedback(
            String name, String age, String language, String gender, String option, String comment) {
        if (name != null) {
            setValue(NAME_FIELD, name);
        }
        if (age != null) {
            setValue(AGE_FIELD, age);
        }
        if (language != null) {
            List<String> languages = List.of(language.split(","));
            for (WebElement checkbox : driver.findElements(LANGUAGE_CHECKBOXES)) {
                if (languages.contains(checkbox.getAttribute("value")) && !checkbox.isSelected()) {
                    checkbox.click();
                }
            }
        }
        if (gender != null) {
            for (WebElement radio : driver.findElements(GENDER_RADIOS)) {
                if (radio.isEnabled() && gender.equals(radio.getAttribute("value"))) {
                    radio.click();
                }
            }
        }
        if (option != null) {
            new Select(driver.findElement(LIKE_US_SELECT)).selectByVisibleText(option);
        }
        if (comment != null) {
            setValue(COMMENT_FIELD, comment);
        }

        driver.findElement(SUBMIT_BUTTON).click();
        wait.until(d -> d.getCurrentUrl().contains("check_feedback.html"));
        return new FeedbackSummaryPage(driver);
    }

    public String getName() {
        return getValue(NAME_FIELD);
    }

    public String getAge() {
        return getValue(AGE_FIELD);
    }

    public List<String> getSelectedLanguages() {
        List<String> selected = new ArrayList<>();
        for (WebElement checkbox : driver.findElements(LANGUAGE_CHECKBOXES)) {
            if (checkbox.isSelected()) {
                selected.add(checkbox.getAttribute("value"));
            }
        }
        return selected;
    }

    public List<String> getSelectedEnabledGenders() {
        List<String> selected = new ArrayList<>();
        for (WebElement radio : driver.findElements(GENDER_RADIOS)) {
            if (radio.isEnabled() && radio.isSelected()) {
                selected.add(radio.getAttribute("value"));
            }
        }
        return selected;
    }

    public List<String> getDisabledGenderValues() {
        return driver.findElements(By.cssSelector("input[name='gender'][disabled]")).stream()
                .map(option -> option.getAttribute("value"))
                .toList();
    }

    public List<String> getSelectedDisabledGenderValues() {
        return driver.findElements(By.cssSelector("input[name='gender'][disabled]")).stream()
                .filter(WebElement::isSelected)
                .map(option -> option.getAttribute("value"))
                .toList();
    }

    public String getSelectedLikeUsOption() {
        return new Select(driver.findElement(LIKE_US_SELECT)).getFirstSelectedOption().getText();
    }

    public String getComment() {
        return getValue(COMMENT_FIELD);
    }

    public String getSubmitButtonClass() {
        return driver.findElement(SUBMIT_BUTTON).getAttribute("class");
    }

    public String getSubmitButtonTextColor() {
        return driver.findElement(SUBMIT_BUTTON).getCssValue("color").replace(" ", "");
    }

    private void setValue(By locator, String value) {
        WebElement field = driver.findElement(locator);
        field.clear();
        field.sendKeys(value);
    }

    private String getValue(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }
}
