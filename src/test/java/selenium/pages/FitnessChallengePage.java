package selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

public class FitnessChallengePage {
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public FitnessChallengePage open() {
        driver.manage().window().maximize();
        driver.get(PAGE_URL);
        wait.until(d -> d.findElements(By.cssSelector(
                "#participantsList .participant-name")).size() == 10);
        return this;
    }

    public String title() {
        return driver.getTitle();
    }

    public String heading() {
        return driver.findElement(By.cssSelector("h2")).getText();
    }

    public List<Participant> participants() {
        return driver.findElements(PARTICIPANT_ROWS).stream()
                .map(this::readParticipant)
                .toList();
    }

    public Participant participant(String name) {
        return participants().stream()
                .filter(participant -> participant.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Participant not found: " + name));
    }

    public int addStepsButtonCount() {
        return driver.findElements(By.id("addStepsBtn")).size();
    }

    public int resetButtonCount() {
        return driver.findElements(By.id("resetBtn")).size();
    }

    public boolean areAddStepsButtonsDisplayed() {
        return driver.findElements(By.id("addStepsBtn")).stream().allMatch(WebElement::isDisplayed);
    }

    public boolean areResetButtonsDisplayed() {
        return driver.findElements(By.id("resetBtn")).stream().allMatch(WebElement::isDisplayed);
    }

    public void openAddStepsModal() {
        openAddStepsModal(0, false);
    }

    public void openAddStepsModalAtBottom() {
        openAddStepsModal(-1, true);
    }

    private void openAddStepsModal(int buttonIndex, boolean scrollToBottom) {
        if (scrollToBottom) {
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        }
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        buttons.get(buttonIndex < 0 ? buttons.size() + buttonIndex : buttonIndex).click();
        wait.until(d -> {
            WebElement modal = d.findElement(By.id("addStepsModal"));
            return modal.isDisplayed() && "block".equals(modal.getCssValue("display"));
        });
    }

    public boolean isModalDisplayed() {
        return driver.findElement(By.id("addStepsModal")).isDisplayed();
    }

    public void waitForModalToClose() {
        wait.until(d -> !d.findElement(By.id("addStepsModal")).isDisplayed());
    }

    public void waitForParticipantSteps(String name, BigInteger expectedSteps) {
        wait.until(d -> participant(name).steps().equals(expectedSteps));
    }

    public ModalDetails modalDetails() {
        wait.until(d -> d.findElement(By.id("addStepsModal")).isDisplayed());
        Select participants = new Select(driver.findElement(By.id("participant_select")));
        return new ModalDetails(
                driver.findElement(By.cssSelector("#addStepsModal h3")).getText(),
                !driver.findElements(By.id("participant_select")).isEmpty(),
                !driver.findElements(By.id("steps_input")).isEmpty(),
                !driver.findElements(By.id("modal_add_steps_button")).isEmpty(),
                driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"))
                        .getAttribute("title"),
                participants.getFirstSelectedOption().getText(),
                participants.getOptions().size());
    }

    public void closeModal() {
        WebElement closeButton = driver.findElement(
                By.cssSelector("#addStepsModal .w3-closebtn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeButton);
        wait.until(d -> !d.findElement(By.id("addStepsModal")).isDisplayed());
    }

    public void submitSteps(String participantName, String steps) {
        openAddStepsModal();
        if (participantName != null) {
            selectParticipant(participantName);
        }
        enterSteps(steps);
        clickAddSteps();
    }

    public void selectParticipant(String name) {
        new Select(driver.findElement(By.id("participant_select"))).selectByVisibleText(name);
    }

    public void enterSteps(String steps) {
        driver.findElement(By.id("steps_input")).sendKeys(steps);
    }

    public String stepsInputValue() {
        return driver.findElement(By.id("steps_input")).getAttribute("value");
    }

    public void clickAddSteps() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    public String acceptAlert() {
        org.openqa.selenium.Alert alert =
                wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }

    public void reset(int buttonIndex) {
        reset(buttonIndex, false);
    }

    public void resetAtBottom() {
        reset(1, true);
    }

    private void reset(int buttonIndex, boolean scrollToBottom) {
        if (scrollToBottom) {
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight)");
        }
        driver.findElements(By.id("resetBtn")).get(buttonIndex).click();
        wait.until(d -> d.findElements(
                By.cssSelector("#participantsList .participant-name")).size() == 10);
    }

    private Participant readParticipant(WebElement row) {
        String name = row.findElement(By.cssSelector(".participant-name")).getText();
        WebElement stepsElement = row.findElement(By.cssSelector(".participant-steps"));
        String displayedSteps = stepsElement.getText();
        String digits = displayedSteps.replaceAll("[^0-9]", "").trim();
        List<WebElement> trophies = row.findElements(By.cssSelector(".fa-trophy"));
        String trophyColor = trophies.isEmpty() ? "" : trophies.get(0).getCssValue("color");
        return new Participant(
                name,
                new BigInteger(digits),
                displayedSteps,
                !trophies.isEmpty() && trophies.get(0).isDisplayed(),
                trophyColor);
    }

    public record Participant(
            String name,
            BigInteger steps,
            String displayedSteps,
            boolean trophyDisplayed,
            String trophyColor) {
    }

    public record ModalDetails(
            String title,
            boolean hasParticipantSelect,
            boolean hasStepsInput,
            boolean hasAddButton,
            String closeButtonTitle,
            String selectedParticipant,
            int participantOptionCount) {
    }
}
