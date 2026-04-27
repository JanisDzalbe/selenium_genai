package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class FitnessChallengePage {

    private WebDriver driver;
    private WebDriverWait wait;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }
    private By modal = By.id("addStepsModal");
    private By closeButton = By.cssSelector("#addStepsModal .w3-closebtn");

    public void openModalTop() {
        driver.findElement(By.xpath("//button[contains(text(),'Add Steps')]")).click();
    }


    public void open() {
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
    }

    public String getTitle() {
        return driver.findElement(By.tagName("h2")).getText();
    }

    public List<WebElement> getParticipants() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    public List<String> getNames() {
        return driver.findElements(By.cssSelector("#participantsList .participant-name"))
                .stream().map(WebElement::getText).toList();
    }

    public List<Integer> getSteps() {
        return driver.findElements(By.cssSelector("#participantsList .participant-steps"))
                .stream()
                .map(e -> e.getText()
                        .replace(" steps", "")
                        .replace(",", "")
                        .trim())
                .map(Integer::parseInt)
                .toList();
    }

    public List<WebElement> getAddButtons() {
        return driver.findElements(By.id("addStepsBtn"));
    }

    public List<WebElement> getResetButtons() {
        return driver.findElements(By.id("resetBtn"));
    }

    public String getIconColor(WebElement participant) {
        try {
            WebElement icon = participant.findElement(By.cssSelector("i.fa-trophy"));
            return icon.getCssValue("color");
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public List<WebElement> getParticipantItems() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    public void clickTopAddSteps() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
    }

    public boolean isModalVisible() {
        return driver.findElement(By.id("addStepsModal"))
                .getAttribute("style")
                .contains("display: block");
    }

    public String getModalTitle() {
        return driver.findElement(By.cssSelector("#addStepsModal h3")).getText();
    }

    public boolean isDropdownPresent() {
        return !driver.findElements(By.id("participant_select")).isEmpty();
    }

    public boolean isStepsInputPresent() {
        return !driver.findElements(By.id("steps_input")).isEmpty();
    }

    public boolean isSubmitButtonPresent() {
        return !driver.findElements(By.id("modal_add_steps_button")).isEmpty();
    }

    public boolean isCloseButtonPresent() {
        return !driver.findElements(
                By.cssSelector("#addStepsModal .w3-closebtn")
        ).isEmpty();
    }

    public List<WebElement> getDropdownOptions() {
        WebElement select = driver.findElement(By.id("participant_select"));
        return select.findElements(By.tagName("option"));
    }

    public String getDropdownDefaultText() {
        return driver.findElement(By.id("participant_select"))
                .findElement(By.tagName("option"))
                .getText();
    }

    public void clickBottomAddSteps() {
        WebElement btn = driver.findElements(By.id("addStepsBtn")).get(1);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView(true);", btn
        );

        btn.click();
    }

    public void closeModal() {
        driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
    }

    public boolean isModalClosed() {
        String style = driver.findElement(By.id("addStepsModal"))
                .getAttribute("style");
        return style == null || !style.contains("display: block");
    }


}