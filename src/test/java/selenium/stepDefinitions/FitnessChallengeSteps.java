package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FitnessChallengeSteps {
    private static final By PARTICIPANT_ROWS =
            By.cssSelector("#participantsList li[id^='participant_display']");
    private static final By PARTICIPANT_NAMES = By.className("participant-name");
    private static final By PARTICIPANT_STEPS = By.className("participant-steps");
    private static final String[] TROPHY_COLORS = {
            "rgba(255, 215, 0, 1)",
            "rgba(192, 192, 192, 1)",
            "rgba(205, 127, 50, 1)"
    };

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        Hooks.driver.get(url);
        waitForParticipants();
    }

    @When("the page loads for the first time")
    public void waitForInitialPageLoad() {
        waitForParticipants();
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, Hooks.driver.getTitle());
        assertEquals(expectedTitle, Hooks.driver.findElement(By.tagName("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayed(int expectedCount) {
        assertEquals(expectedCount, Hooks.driver.findElements(PARTICIPANT_ROWS).size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable participantsTable) {
        List<Map<String, String>> expectedParticipants =
                participantsTable.asMaps(String.class, String.class);
        List<WebElement> rows = Hooks.driver.findElements(PARTICIPANT_ROWS);

        assertEquals(expectedParticipants.size(), rows.size(),
                "The displayed participant list should match the table size");
        Map<String, String> actualParticipants = new HashMap<>();
        for (WebElement row : rows) {
            actualParticipants.put(
                    row.findElement(PARTICIPANT_NAMES).getText(),
                    row.findElement(PARTICIPANT_STEPS).getText());
        }
        for (Map<String, String> expected : expectedParticipants) {
            assertEquals(expected.get("Steps") + " steps",
                    actualParticipants.get(expected.get("Name")),
                    "Unexpected displayed steps for " + expected.get("Name"));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        String buttonId = switch (buttonText) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported button: " + buttonText);
        };
        List<WebElement> buttons = Hooks.driver.findElements(By.id(buttonId));
        assertEquals(2, buttons.size(), "Expected one " + buttonText + " button at each page end");
        assertTrue(buttons.get(0).isDisplayed(), "Top " + buttonText + " button should be visible");
        assertTrue(buttons.get(1).isDisplayed(), "Bottom " + buttonText + " button should be visible");
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        waitForParticipants();
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrder() {
        List<Integer> stepCounts = readDisplayedStepCounts();
        assertEquals(10, stepCounts.size());
        for (int i = 1; i < stepCounts.size(); i++) {
            assertTrue(stepCounts.get(i - 1) >= stepCounts.get(i),
                    "Step counts must be in descending order: " + stepCounts);
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantHasAtLeastAsManyStepsAsNext() {
        List<Integer> stepCounts = readDisplayedStepCounts();
        for (int i = 1; i < stepCounts.size(); i++) {
            assertTrue(stepCounts.get(i - 1) >= stepCounts.get(i),
                    "Participant at rank " + i + " should have at least as many steps as rank "
                            + (i + 1));
        }
    }

    @Then("^the (\\d+)(st|nd|rd|th) place participant displays a (\\w+) trophy icon$")
    public void participantAtPlaceDisplaysTrophy(int place, String suffix, String medal) {
        assertTrue(place >= 1 && place <= 3, "Trophy place must be between 1 and 3");
        String expectedSuffix = switch (place) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
        assertEquals(expectedSuffix, suffix);

        WebElement row = Hooks.driver.findElements(PARTICIPANT_ROWS).get(place - 1);
        WebElement trophy = row.findElement(By.cssSelector("i.fa-trophy"));
        assertTrue(trophy.isDisplayed());
        assertEquals(TROPHY_COLORS[place - 1], trophy.getCssValue("color"));
        assertEquals(medal.toLowerCase(), expectedMedal(place));
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsRankedFourthAndBelowHaveNoTrophies() {
        List<WebElement> rows = Hooks.driver.findElements(PARTICIPANT_ROWS);
        assertEquals(10, rows.size());
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Participant ranked " + (i + 1) + " should not have a trophy");
        }
    }

    private void waitForParticipants() {
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfElementsToBe(PARTICIPANT_ROWS, 10));
    }

    private List<Integer> readDisplayedStepCounts() {
        List<Integer> values = new ArrayList<>();
        for (WebElement row : Hooks.driver.findElements(PARTICIPANT_ROWS)) {
            String displayedSteps = row.findElement(PARTICIPANT_STEPS).getText();
            values.add(Integer.parseInt(displayedSteps.replaceAll("[^0-9]", "")));
        }
        return values;
    }

    private String expectedMedal(int place) {
        return switch (place) {
            case 1 -> "gold";
            case 2 -> "silver";
            case 3 -> "bronze";
            default -> throw new IllegalArgumentException("Unsupported trophy place: " + place);
        };
    }
}
