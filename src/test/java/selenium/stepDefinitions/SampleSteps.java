package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SampleSteps {

    private final WebDriver driver;
    private WebDriverWait wait;

    // ── Selectors ─────────────────────────────────────────────────────────────
    private static final By PARTICIPANTS_LIST  = By.id("participantsList");
    private static final By PARTICIPANT_NAMES  = By.className("participant-name");
    private static final By PARTICIPANT_STEPS  = By.className("participant-steps");
    private static final By ADD_STEPS_BUTTONS  = By.id("addStepsBtn");
    private static final By RESET_BUTTONS      = By.id("resetBtn");

    public SampleSteps() {
        this.driver = Hooks.driver;
    }

    // ── Given ─────────────────────────────────────────────────────────────────

    @Given("I am on the fitness challenge page {string}")
    public void navigateToFitnessChallenge(String url) {
        driver.navigate().to(url);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Wait until at least one participant name is rendered (JS must have run)
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(PARTICIPANT_NAMES));
    }

    // ── When ──────────────────────────────────────────────────────────────────

    @When("the page loads for the first time")
    public void pageLoads() {
        // Navigation already happened in Background; just confirm the list is visible.
        assertTrue(
                driver.findElement(PARTICIPANTS_LIST).isDisplayed(),
                "Participants list should be visible"
        );
    }

    // ── Then ──────────────────────────────────────────────────────────────────

    @Then("the page displays title {string}")
    public void verifyPageTitle(String expectedTitle) {
        // The <h2> text is exactly the title — no partial match needed.
        WebElement title = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[text()='" + expectedTitle + "']"))
        );
        assertEquals(expectedTitle, title.getText(), "Page title should match");
    }

    @Then("{int} participants are displayed in the list")
    public void verifyParticipantCount(int expectedCount) {
        List<WebElement> participants = driver.findElements(PARTICIPANT_NAMES);
        assertEquals(expectedCount, participants.size(),
                "Number of participants should be " + expectedCount);
    }

    @Then("the default participants are displayed:")
    public void verifyDefaultParticipants(DataTable dataTable) {
        List<Map<String, String>> expectedRows = dataTable.asMaps(String.class, String.class);

        // Build a name → steps map from the actual page so order does not matter.
        List<WebElement> nameElements  = driver.findElements(PARTICIPANT_NAMES);
        List<WebElement> stepsElements = driver.findElements(PARTICIPANT_STEPS);

        assertEquals(expectedRows.size(), nameElements.size(),
                "Participant count must match the DataTable row count");

        Map<String, String> actualStepsByName = new HashMap<>();
        for (int i = 0; i < nameElements.size(); i++) {
            actualStepsByName.put(
                    nameElements.get(i).getText().trim(),
                    stepsElements.get(i).getText().trim()   // e.g. "15,000 steps"
            );
        }

        for (Map<String, String> row : expectedRows) {
            String expectedName  = row.get("Name").trim();
            // DataTable has bare numbers like "15,000"; the page appends " steps".
            String expectedSteps = row.get("Steps").trim() + " steps";

            assertTrue(actualStepsByName.containsKey(expectedName),
                    "Participant not found on page: " + expectedName);
            assertEquals(expectedSteps, actualStepsByName.get(expectedName),
                    "Steps mismatch for " + expectedName);
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void verifyButtonsVisibility(String buttonType) {
        By locator = switch (buttonType) {
            case "Add Steps"   -> ADD_STEPS_BUTTONS;
            case "Reset List"  -> RESET_BUTTONS;
            default -> throw new IllegalArgumentException("Unknown button type: " + buttonType);
        };

        // The HTML has duplicate IDs (top + bottom), so findElements returns both.
        List<WebElement> buttons = driver.findElements(locator);

        assertTrue(buttons.size() >= 2,
                "Expected at least 2 '" + buttonType + "' buttons (top and bottom), found: " + buttons.size());

        for (int i = 0; i < 2; i++) {
            assertTrue(buttons.get(i).isDisplayed(),
                    "Button " + (i + 1) + " '" + buttonType + "' should be visible");
        }
    }
}