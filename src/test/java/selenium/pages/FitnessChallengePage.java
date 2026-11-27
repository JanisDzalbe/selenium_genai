package selenium.pages;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FitnessChallengePage {
    public static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";

    private static final Duration WAIT_SECONDS = Duration.ofSeconds(10);
    private static final By PARTICIPANT_ITEMS = By.cssSelector("#participantsList li");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By MODAL = By.id("addStepsModal");
    private static final By MODAL_SUBMIT = By.id("modal_add_steps_button");
    private static final By MODAL_TITLE = By.cssSelector("#addStepsModal h3");
    private static final By MODAL_CLOSE = By.cssSelector("#addStepsModal .w3-closebtn");
    private static final Map<String, Long> DEFAULT_PARTICIPANTS = Map.ofEntries(
            Map.entry("John Smith", 15000L),
            Map.entry("David Brown", 13500L),
            Map.entry("Jill Watson", 12000L),
            Map.entry("Carlos Garcia", 11200L),
            Map.entry("Maria Rodriguez", 10500L),
            Map.entry("Sarah Johnson", 9800L),
            Map.entry("Mike Kid", 8500L),
            Map.entry("Alex Taylor", 8900L),
            Map.entry("Emily Chen", 7300L),
            Map.entry("Jane Doe", 6500L));

    private final WebDriver driver;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
    }

    public void openAndReset() {
        driver.get(URL);
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver ->
                "complete".equals(((JavascriptExecutor) currentDriver)
                        .executeScript("return document.readyState"))
                        && Boolean.TRUE.equals(((JavascriptExecutor) currentDriver)
                        .executeScript("return typeof resetFitnessChallenge === 'function'")));
        ((JavascriptExecutor) driver).executeScript("resetFitnessChallenge()");
        waitForReset();
    }

    public Map<String, Long> getDefaultParticipants() {
        return DEFAULT_PARTICIPANTS;
    }

    public String getTitle() {
        return driver.findElement(By.cssSelector("h2")).getText();
    }

    public List<WebElement> getParticipantItems() {
        return new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver -> {
            try {
                if (readParticipantSteps(currentDriver) == null) {
                    return null;
                }
                return currentDriver.findElements(PARTICIPANT_ITEMS);
            } catch (StaleElementReferenceException | NoSuchElementException
                     | NumberFormatException ex) {
                return null;
            }
        });
    }

    public String getParticipantName(String name) {
        return getParticipantItems().stream()
                .map(item -> item.findElement(By.cssSelector(".participant-name")).getText())
                .filter(name::equals)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    public long getParticipantSteps(String name) {
        return new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver -> {
            try {
                if (readParticipantSteps(currentDriver) == null) {
                    return null;
                }
                for (WebElement item : currentDriver.findElements(PARTICIPANT_ITEMS)) {
                    String participantName =
                            item.findElement(By.cssSelector(".participant-name")).getText();
                    if (participantName.equals(name)) {
                        String steps = item.findElement(By.cssSelector(".participant-steps")).getText();
                        return steps.isBlank() ? null : parseSteps(steps);
                    }
                }
                return null;
            } catch (StaleElementReferenceException | NoSuchElementException
                     | NumberFormatException ex) {
                return null;
            }
        });
    }

    public long parseSteps(String text) {
        return Long.parseLong(text.replace(" steps", "").replace(",", "").trim());
    }

    public long getItemSteps(WebElement item) {
        return parseSteps(item.findElement(By.cssSelector(".participant-steps")).getText());
    }

    public String getItemName(WebElement item) {
        return item.findElement(By.cssSelector(".participant-name")).getText();
    }

    public String getTrophyColor(int index) {
        return getParticipantItems().get(index)
                .findElement(By.cssSelector("i.fa-trophy"))
                .getCssValue("color");
    }

    public int getTrophyCount(int index) {
        return getParticipantItems().get(index)
                .findElements(By.cssSelector("i.fa-trophy")).size();
    }

    public boolean hasTrophy(int index) {
        return getTrophyCount(index) > 0;
    }

    public Map<String, Long> readParticipantSteps() {
        return readParticipantSteps(driver);
    }

    public void waitForReset() {
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver -> {
            try {
                return DEFAULT_PARTICIPANTS.equals(readParticipantSteps(currentDriver));
            } catch (StaleElementReferenceException | NoSuchElementException
                     | NumberFormatException ex) {
                return false;
            }
        });
    }

    public WebElement getTopButton(String id) {
        return driver.findElements(By.id(id)).get(0);
    }

    public WebElement getBottomButton(String id) {
        return driver.findElements(By.id(id)).get(1);
    }

    public int getButtonCount(String id) {
        return driver.findElements(By.id(id)).size();
    }

    public String getTopButtonText(String id) {
        return getTopButton(id).getText().trim();
    }

    public String getBottomButtonText(String id) {
        return getBottomButton(id).getText().trim();
    }

    public boolean areButtonsDisplayed(String id) {
        return driver.findElements(By.id(id)).stream().allMatch(WebElement::isDisplayed);
    }

    public void scrollToBottomButton(String id) {
        WebElement button = getBottomButton(id);
        new Actions(driver).moveToElement(button).perform();
    }

    public void openModal() {
        getTopButton("addStepsBtn").click();
        waitForModalOpen();
    }

    public void closeModal() {
        driver.findElement(MODAL_CLOSE).click();
    }

    public void submitModal() {
        driver.findElement(MODAL_SUBMIT).click();
    }

    public void addSteps(String participantName, String steps) {
        openModal();
        selectParticipant(participantName);
        enterSteps(steps);
        submitModal();
    }

    public void selectParticipant(String participantName) {
        new Select(driver.findElement(PARTICIPANT_SELECT))
                .selectByVisibleText(participantName);
    }

    public void enterSteps(String steps) {
        driver.findElement(STEPS_INPUT).sendKeys(steps);
    }

    public String getModalTitle() {
        return driver.findElement(MODAL_TITLE).getText();
    }

    public String getDefaultModalSelection() {
        return new Select(driver.findElement(PARTICIPANT_SELECT))
                .getFirstSelectedOption().getText();
    }

    public int getParticipantOptionCount() {
        return new Select(driver.findElement(PARTICIPANT_SELECT)).getOptions().size();
    }

    public String getStepsInputType() {
        return driver.findElement(STEPS_INPUT).getAttribute("type");
    }

    public String getStepsInputValue() {
        return driver.findElement(STEPS_INPUT).getAttribute("value");
    }

    public WebElement getStepsInput() {
        return driver.findElement(STEPS_INPUT);
    }

    public boolean isStepsInputMismatch() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript(
                "return arguments[0].validity.stepMismatch;", getStepsInput()));
    }

    public boolean isModalSubmitDisplayed() {
        return driver.findElement(MODAL_SUBMIT).isDisplayed();
    }

    public boolean isModalCloseDisplayed() {
        return driver.findElement(MODAL_CLOSE).isDisplayed();
    }

    public void waitForModalOpen() {
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver ->
                "block".equals(currentDriver.findElement(MODAL).getCssValue("display"))
                        || "block".equals(currentDriver.findElement(MODAL).getAttribute("style")));
    }

    public void waitForModalClosed() {
        new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver ->
                "none".equals(currentDriver.findElement(MODAL).getCssValue("display")));
    }

    public String acceptAlert(String expectedText) {
        Alert alert = new WebDriverWait(driver, WAIT_SECONDS).until(currentDriver -> {
            try {
                return currentDriver.switchTo().alert();
            } catch (NoAlertPresentException ex) {
                return null;
            }
        });
        String text = alert.getText();
        alert.accept();
        return text;
    }

    private Map<String, Long> readParticipantSteps(WebDriver currentDriver) {
        List<WebElement> items = currentDriver.findElements(PARTICIPANT_ITEMS);
        if (items.size() != DEFAULT_PARTICIPANTS.size()) {
            return null;
        }

        Map<String, Long> participants = new LinkedHashMap<>();
        for (WebElement item : items) {
            String name = item.findElement(By.cssSelector(".participant-name")).getText().trim();
            String steps = item.findElement(By.cssSelector(".participant-steps")).getText();
            if (name.isEmpty() || steps.isBlank()) {
                return null;
            }
            participants.put(name, parseSteps(steps));
        }
        return participants.keySet().equals(DEFAULT_PARTICIPANTS.keySet())
                ? participants
                : null;
    }
}
