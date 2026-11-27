package selenium.pomPage;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FitnessChallengePage {
    public static final String URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";

    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");
    private static final By MODAL = By.id("addStepsModal");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FitnessChallengePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public FitnessChallengePage open() {
        driver.get(URL);
        wait.until(d -> d.findElements(PARTICIPANT_ROWS).size() == 10);
        return this;
    }

    public String title() {
        return driver.getTitle();
    }

    public String heading() {
        return driver.findElement(By.cssSelector("h2")).getText();
    }

    public List<WebElement> participantRows() {
        return driver.findElements(PARTICIPANT_ROWS);
    }

    public WebElement participantRow(String name) {
        return participantRows().stream()
                .filter(row -> row.findElement(By.cssSelector(".participant-name")).getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    public int stepsOf(String name) {
        String text = participantRow(name)
                .findElement(By.cssSelector(".participant-steps"))
                .getText()
                .replace("steps", "")
                .replace(",", "")
                .trim();
        return Integer.parseInt(text);
    }

    public WebElement topButton(String id) {
        return driver.findElements(By.id(id)).getFirst();
    }

    public WebElement bottomButton(String id) {
        List<WebElement> buttons = driver.findElements(By.id(id));
        if (buttons.size() < 2) {
            throw new AssertionError("Expected top and bottom " + id + " buttons");
        }
        return buttons.get(1);
    }

    public void openModal() {
        topButton("addStepsBtn").click();
        wait.until(d -> "block".equals(d.findElement(MODAL).getCssValue("display")));
    }

    public void openModalFromBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        bottomButton("addStepsBtn").click();
        wait.until(d -> "block".equals(d.findElement(MODAL).getCssValue("display")));
    }

    public WebElement modal() {
        return driver.findElement(MODAL);
    }

    public Select participantSelect() {
        return new Select(driver.findElement(By.id("participant_select")));
    }

    public WebElement stepsInput() {
        return driver.findElement(By.id("steps_input"));
    }

    public void closeModalWithButton() {
        modal().findElement(By.cssSelector(".w3-closebtn")).click();
        wait.until(d -> "none".equals(d.findElement(MODAL).getCssValue("display")));
    }

    public void addSteps(String participantName, String steps) {
        openModal();
        if (participantName != null) {
            participantSelect().selectByVisibleText(participantName);
        }
        stepsInput().sendKeys(steps);
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    public Alert alert() {
        return wait.until(d -> d.switchTo().alert());
    }

    public void resetFromTop() {
        topButton("resetBtn").click();
        wait.until(d -> d.findElements(PARTICIPANT_ROWS).size() == 10);
    }

    public void resetFromBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        bottomButton("resetBtn").click();
        wait.until(d -> d.findElements(PARTICIPANT_ROWS).size() == 10);
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public boolean modalIsOpen() {
        return "block".equals(modal().getCssValue("display"));
    }

    public Object executeScript(String script, Object... arguments) {
        return ((JavascriptExecutor) driver).executeScript(script, arguments);
    }

    public void waitForModalToClose() {
        wait.until(d -> "none".equals(d.findElement(MODAL).getCssValue("display")));
    }
}
