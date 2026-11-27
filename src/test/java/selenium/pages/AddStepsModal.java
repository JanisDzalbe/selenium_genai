package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AddStepsModal {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT = By.id("participant_select");
    private static final By STEPS = By.id("steps_input");
    private static final By SUBMIT = By.id("modal_add_steps_button");
    private static final By CLOSE = By.cssSelector(".w3-closebtn[title='Close Modal']");

    private final WebDriver driver;

    public AddStepsModal(WebDriver driver) {
        this.driver = driver;
    }

    public String title() {
        return driver.findElement(MODAL).findElement(By.tagName("h3")).getText();
    }

    public boolean participantDropdownVisible() {
        return driver.findElement(PARTICIPANT).isDisplayed();
    }

    public String selectedParticipantText() {
        return new Select(driver.findElement(PARTICIPANT)).getFirstSelectedOption().getText();
    }

    public int participantOptionCount() {
        return new Select(driver.findElement(PARTICIPANT)).getOptions().size();
    }

    public String selectedParticipantIndex() {
        return new Select(driver.findElement(PARTICIPANT))
                .getFirstSelectedOption().getAttribute("value");
    }

    public boolean stepsInputVisible() {
        return driver.findElement(STEPS).isDisplayed();
    }

    public String stepsInputType() {
        return driver.findElement(STEPS).getAttribute("type");
    }

    public String stepsInputValue() {
        return driver.findElement(STEPS).getAttribute("value");
    }

    public boolean submitButtonVisible() {
        return driver.findElement(SUBMIT).isDisplayed();
    }

    public String submitButtonText() {
        return driver.findElement(SUBMIT).getText();
    }

    public boolean closeButtonVisible() {
        return driver.findElement(CLOSE).isDisplayed();
    }

    public AddStepsModal selectParticipant(String name) {
        new Select(driver.findElement(PARTICIPANT)).selectByVisibleText(name);
        return this;
    }

    public AddStepsModal enterSteps(String steps) {
        WebElement input = driver.findElement(STEPS);
        String currentValue = input.getAttribute("value");
        if (currentValue != null && !currentValue.isEmpty()) {
            input.clear();
        }
        input.sendKeys(steps);
        return this;
    }

    public void submit() {
        driver.findElement(SUBMIT).click();
    }

    public void submitAndWaitForClose() {
        submit();
        new WebDriverWait(driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    public String submitAndAcceptAlert() {
        submit();
        new WebDriverWait(driver, WAIT_TIMEOUT).until(ExpectedConditions.alertIsPresent());
        String message = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();
        new WebDriverWait(driver, WAIT_TIMEOUT).until(webDriver -> {
            try {
                webDriver.switchTo().alert();
                return false;
            } catch (NoAlertPresentException e) {
                return true;
            }
        });
        return message;
    }

    public void close() {
        driver.findElement(CLOSE).click();
        new WebDriverWait(driver, WAIT_TIMEOUT).until(webDriver ->
                "none".equals(webDriver.findElement(MODAL).getCssValue("display")));
    }

    public boolean isDisplayed() {
        return driver.findElement(MODAL).isDisplayed();
    }
}
