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
            case "Add Steps"  -> ADD_STEPS_BUTTONS;
            case "Reset List" -> RESET_BUTTONS;
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

    // ── When (ranking scenario) ───────────────────────────────────────────────

    @When("I view the participant list")
    public void viewParticipantList() {
        assertTrue(
                driver.findElement(PARTICIPANTS_LIST).isDisplayed(),
                "Participants list should be visible"
        );
    }

    // ── Then (ranking scenario) ───────────────────────────────────────────────

    @Then("participants are displayed in descending order by step count")
    public void verifyDescendingOrder() {
        List<WebElement> stepElements = driver.findElements(PARTICIPANT_STEPS);

        int previousSteps = Integer.MAX_VALUE;
        for (WebElement stepElement : stepElements) {
            // Text is e.g. "15,000 steps" — strip commas and the " steps" suffix.
            int currentSteps = Integer.parseInt(
                    stepElement.getText().replace(",", "").replace(" steps", "").trim()
            );
            assertTrue(currentSteps <= previousSteps,
                    "Expected descending order but " + currentSteps + " > " + previousSteps);
            previousSteps = currentSteps;
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void verifyStepCountOrdering() {
        // Same invariant as the previous step — reuse to avoid duplicating DOM reads.
        verifyDescendingOrder();
    }

    /**
     * Core helper — not a step itself. Finds participant_displayN, asserts a
     * trophy icon exists and that its computed colour matches the expected RGB.
     */
    private void assertTrophyColor(int position, String colorName, String expectedRgb) {
        WebElement participant = driver.findElement(
                By.id("participant_display" + (position - 1))
        );

        List<WebElement> trophyIcons = participant.findElements(By.className("fa-trophy"));
        assertFalse(trophyIcons.isEmpty(),
                "Position " + position + " should have a trophy icon");

        WebElement trophy = trophyIcons.getFirst();
        assertTrue(trophy.isDisplayed(),
                "Trophy icon at position " + position + " should be visible");

        // Inline style="color:gold" is normalised to rgb(...) by the browser.
        String actualColor = trophy.getCssValue("color");
        assertEquals(expectedRgb, actualColor,
                "Position " + position + " trophy should be " + colorName
                        + " (" + expectedRgb + ") but got: " + actualColor);
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void verifyFirstPlaceGoldTrophy() {
        assertTrophyColor(1, "gold", "rgba(255, 215, 0, 1)");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void verifySecondPlaceSilverTrophy() {
        assertTrophyColor(2, "silver", "rgba(192, 192, 192, 1)");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void verifyThirdPlaceBronzeTrophy() {
        assertTrophyColor(3, "bronze", "rgba(205, 127, 50, 1)");
    }

    @Then("participants ranked {int}th and below have no trophy icons")
    public void verifyNoTrophyIconsBelowRank(int startingRank) {
        List<WebElement> allParticipants = driver.findElements(PARTICIPANT_NAMES);

        for (int i = startingRank - 1; i < allParticipants.size(); i++) {
            WebElement participant = driver.findElement(By.id("participant_display" + i));
            List<WebElement> trophies = participant.findElements(By.className("fa-trophy"));
            assertTrue(trophies.isEmpty(),
                    "Participant at position " + (i + 1) + " (rank " + (i + 1)
                            + ") should NOT have a trophy icon");
        }
    }
}