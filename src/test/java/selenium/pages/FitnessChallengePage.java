package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class FitnessChallengePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By pageTitleLocator        = By.tagName("h2");
    private final By participantsListLocator = By.cssSelector("#participantsList li");
    private final By modalLocator            = By.id("addStepsModal");
    private final By modalTitleLocator       = By.cssSelector("#addStepsModal h3");
    private final By dropdownLocator         = By.id("participant_select");
    private final By stepsInputLocator       = By.id("steps_input");
    private final By submitButtonLocator     = By.id("modal_add_steps_button");
    private final By closeButtonLocator      = By.className("w3-closebtn");

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void openPage() {
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        // On first visit JS initialises localStorage then calls location.reload(); wait for the list
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(participantsListLocator));
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Page-level queries ────────────────────────────────────────────────────

    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitleLocator)).getText();
    }

    public void waitForParticipantList() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(participantsListLocator));
    }

    public int getParticipantCount() {
        return getParticipants().size();
    }

    public List<WebElement> getParticipants() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(participantsListLocator));
    }

    public String getParticipantName(WebElement participant) {
        return participant.findElement(By.className("participant-name")).getText();
    }

    public long getParticipantSteps(WebElement participant) {
        String text = participant.findElement(By.className("participant-steps")).getText();
        return Long.parseLong(text.replace(",", "").replace(" steps", "").trim());
    }

    public WebElement getParticipantByName(String name) {
        return getParticipants().stream()
                .filter(p -> getParticipantName(p).equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Participant not found: " + name));
    }

    // Trophy icons: <i class="fa fa-trophy" style="color:gold/silver/#cd7f32">
    public boolean hasTrophy(WebElement participant, String type) {
        String color = switch (type) {
            case "gold"   -> "gold";
            case "silver" -> "silver";
            case "bronze" -> "#cd7f32";
            default -> throw new IllegalArgumentException("Unknown trophy type: " + type);
        };
        return !participant.findElements(
                By.xpath(".//i[contains(@class,'fa-trophy') and contains(@style,'" + color + "')]")
        ).isEmpty();
    }

    public int getVisibleButtonCount(String label) {
        return (int) driver
                .findElements(By.xpath("//button[normalize-space()='" + label + "']"))
                .stream()
                .filter(WebElement::isDisplayed)
                .count();
    }

    // ── Add Steps modal ───────────────────────────────────────────────────────

    public void clickAddStepsTop() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalLocator));
    }

    public void clickAddStepsBottom() {
        driver.findElements(By.id("addStepsBtn")).get(1).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalLocator));
    }

    public boolean isModalVisible() {
        List<WebElement> elements = driver.findElements(modalLocator);
        return !elements.isEmpty() && elements.get(0).isDisplayed();
    }

    public String getModalTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitleLocator)).getText();
    }

    public boolean isParticipantDropdownPresent() {
        return !driver.findElements(dropdownLocator).isEmpty();
    }

    public boolean isStepsInputPresent() {
        return !driver.findElements(stepsInputLocator).isEmpty();
    }

    public boolean isSubmitButtonPresent() {
        return !driver.findElements(submitButtonLocator).isEmpty();
    }

    public String getSubmitButtonText() {
        return driver.findElement(submitButtonLocator).getText();
    }

    public boolean isCloseButtonPresent() {
        return !driver.findElements(closeButtonLocator).isEmpty();
    }

    public int getParticipantDropdownOptionCount() {
        // Exclude the default "Choose participant" placeholder option
        return new Select(driver.findElement(dropdownLocator)).getOptions().size() - 1;
    }

    public String getFirstSelectedDropdownOption() {
        return new Select(driver.findElement(dropdownLocator)).getFirstSelectedOption().getText();
    }

    public void selectParticipant(String name) {
        Select select = new Select(wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator)));
        select.selectByVisibleText(name);
    }

    public void enterSteps(String steps) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(stepsInputLocator));
        input.clear();
        input.sendKeys(steps);
    }

    public String getStepsInputValue() {
        return driver.findElement(stepsInputLocator).getAttribute("value");
    }

    public void submitAddSteps() {
        wait.until(ExpectedConditions.elementToBeClickable(submitButtonLocator)).click();
    }

    public void closeModal() {
        WebElement closeBtn = wait.until(ExpectedConditions.elementToBeClickable(closeButtonLocator));
        // W3.CSS close button may be obscured; JS click is more reliable here
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalLocator));
    }

    // ── Alerts ────────────────────────────────────────────────────────────────

    public String getAlertText() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    public void acceptAlert() {
        driver.switchTo().alert().accept();
    }

    // ── Reset buttons ─────────────────────────────────────────────────────────

    public void clickResetTop() {
        if (isModalVisible()) closeModal();
        driver.findElements(By.id("resetBtn")).get(0).click();
        waitForParticipantList();
    }

    public void clickResetBottom() {
        if (isModalVisible()) closeModal();
        driver.findElements(By.id("resetBtn")).get(1).click();
        waitForParticipantList();
    }

    // ── Driver access (use sparingly) ─────────────────────────────────────────

    public WebDriver getDriver() {
        return driver;
    }
}
