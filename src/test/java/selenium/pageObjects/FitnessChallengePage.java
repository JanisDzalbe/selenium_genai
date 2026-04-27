package selenium.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FitnessChallengePage {
    private WebDriver driver;

    @FindBy(tagName = "h2")
    private WebElement pageTitle;

    @FindBy(css = ".participant-name")
    private List<WebElement> participantNames;

    @FindBy(css = ".participant-steps")
    private List<WebElement> participantSteps;

    @FindBy(id = "addStepsBtn")
    private List<WebElement> addStepsButtons;

    @FindBy(id = "resetBtn")
    private List<WebElement> resetButtons;

    @FindBy(id = "addStepsModal")
    private WebElement addStepsModal;

    @FindBy(css = "#addStepsModal h3")
    private WebElement modalTitle;

    @FindBy(id = "participant_select")
    private WebElement participantSelect;

    @FindBy(id = "steps_input")
    private WebElement stepsInput;

    @FindBy(id = "modal_add_steps_button")
    private WebElement addStepsSubmitButton;

    @FindBy(css = "#addStepsModal .w3-closebtn")
    private WebElement modalCloseButton;

    @FindBy(css = ".fa.fa-trophy")
    private List<WebElement> trophyIcons;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getPageTitle() {
        return pageTitle.getText();
    }

    public List<String> getParticipantNames() {
        return participantNames.stream().map(WebElement::getText).toList();
    }

    public Map<String, Long> getParticipantStepsMap() {
        Map<String, Long> map = new HashMap<>();
        for (int i = 0; i < participantNames.size(); i++) {
            String name = participantNames.get(i).getText();
            String stepsText = participantSteps.get(i).getText().replace(" steps", "").replace(",", "");
            long steps = Long.parseLong(stepsText);
            map.put(name, steps);
        }
        return map;
    }

    public boolean areAddStepsButtonsVisible() {
        return addStepsButtons.size() == 2 && addStepsButtons.stream().allMatch(WebElement::isDisplayed);
    }

    public boolean areResetButtonsVisible() {
        return resetButtons.size() == 2 && resetButtons.stream().allMatch(WebElement::isDisplayed);
    }

    public List<Integer> getParticipantStepsInOrder() {
        return participantSteps.stream()
            .map(e -> Integer.parseInt(e.getText().replace(" steps", "").replace(",", "")))
            .toList();
    }

    public void clickTopAddStepsButton() {
        addStepsButtons.get(0).click();
    }

    public void clickBottomAddStepsButton() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addStepsButtons.get(1));
        addStepsButtons.get(1).click();
    }

    public boolean isModalVisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            return wait.until(ExpectedConditions.visibilityOf(addStepsModal)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isModalNotVisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            return wait.until(ExpectedConditions.invisibilityOf(addStepsModal));
        } catch (Exception e) {
            return true;
        }
    }

    public String getModalTitle() {
        return modalTitle.getText();
    }

    public List<String> getDropdownOptions() {
        return new Select(participantSelect).getOptions().stream().map(WebElement::getText).toList();
    }

    public String getDefaultDropdownText() {
        return new Select(participantSelect).getFirstSelectedOption().getText();
    }

    public void selectParticipant(String name) {
        new Select(participantSelect).selectByVisibleText(name);
    }

    public void enterSteps(String steps) {
        stepsInput.clear();
        stepsInput.sendKeys(steps);
    }

    public void clickAddStepsSubmit() {
        addStepsSubmitButton.click();
    }

    public void closeModal() {
        modalCloseButton.click();
    }

    public List<String> getTrophyColors() {
        return trophyIcons.stream().map(e -> {
            String style = e.getAttribute("style");
            if (style != null && style.contains("color:")) {
                return style.split("color:")[1].split(";")[0].trim();
            }
            return "";
        }).toList();
    }

    public void clickTopResetButton() {
        resetButtons.get(0).click();
    }

    public void clickBottomResetButton() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", resetButtons.get(1));
        resetButtons.get(1).click();
    }

    public void waitForPageReload() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElement(pageTitle, "Fitness Challenge"));
    }

    public String getStepsInputValue() {
        return stepsInput.getAttribute("value");
    }
}
