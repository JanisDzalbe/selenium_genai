package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallenge1and2SampleSteps {
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");
    private static final By TROPHY_ICON = By.cssSelector(".fa-trophy");
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    @When("the page loads for the first time")
    public void waitForInitialPageLoad() {
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(webDriver ->
                "complete".equals(((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState"))
                        && webDriver.findElements(PARTICIPANT_ROWS).size() == 10);
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, driver().getTitle());
        WebElement heading = driver().findElement(By.cssSelector("h2"));
        assertTrue("The page heading should be visible", heading.isDisplayed());
        assertEquals(expectedTitle, heading.getText());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayed(int expectedCount) {
        waitForParticipants(expectedCount);
        assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable participants) {
        for (Map<String, String> participant : participants.asMaps(String.class, String.class)) {
            WebElement row = participantRow(participant.get("Name"));
            assertEquals("Unexpected steps for " + participant.get("Name"),
                    parseSteps(participant.get("Steps")), stepsOf(row));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonLabel) {
        String buttonId = switch (buttonLabel) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unexpected button label: " + buttonLabel);
        };
        List<WebElement> buttons = driver().findElements(By.id(buttonId));
        assertTrue("Expected top and bottom " + buttonLabel + " buttons", buttons.size() >= 2);
        assertTrue("The top " + buttonLabel + " button should be visible", buttons.get(0).isDisplayed());
        assertTrue("The bottom " + buttonLabel + " button should be visible", buttons.get(1).isDisplayed());
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        waitForParticipants();
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreInDescendingOrder() {
        assertDescendingStepCounts();
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantHasAtLeastAsManyStepsAsTheNext() {
        assertDescendingStepCounts();
    }

    @Then("the {int}{word} place participant displays a {word} trophy icon")
    public void rankedParticipantDisplaysTrophy(int rank, String ordinalSuffix, String trophyColor) {
        List<WebElement> rows = participantRows();
        assertTrue("Rank must identify a participant in the list", rank >= 1 && rank <= rows.size());

        String expectedSuffix = switch (rank) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
        assertEquals("Unexpected ordinal suffix for rank " + rank, expectedSuffix, ordinalSuffix);

        String expectedColor = switch (trophyColor) {
            case "gold" -> "rgba(255, 215, 0, 1)";
            case "silver" -> "rgba(192, 192, 192, 1)";
            case "bronze" -> "rgba(205, 127, 50, 1)";
            default -> throw new IllegalArgumentException("Unexpected trophy color: " + trophyColor);
        };

        List<WebElement> trophies = rows.get(rank - 1).findElements(TROPHY_ICON);
        assertEquals("Expected one trophy icon for rank " + rank, 1, trophies.size());
        assertTrue("The trophy icon should be visible", trophies.get(0).isDisplayed());
        assertEquals(expectedColor, trophies.get(0).getCssValue("color"));
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsRankedFourthAndBelowHaveNoTrophies() {
        List<WebElement> rows = participantRows();
        for (int rank = 4; rank < rows.size(); rank++) {
            assertTrue("Participant ranked " + (rank + 1) + " should not have a trophy",
                    rows.get(rank).findElements(TROPHY_ICON).isEmpty());
        }
    }

    private WebDriver driver() {
        if (Hooks.driver == null) {
            throw new IllegalStateException("The Cucumber browser has not been initialized");
        }
        return Hooks.driver;
    }

    private void waitForParticipants() {
        waitForParticipants(10);
    }

    private void waitForParticipants(int expectedCount) {
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(webDriver ->
                webDriver.findElements(PARTICIPANT_ROWS).size() == expectedCount);
    }

    private List<WebElement> participantRows() {
        return driver().findElements(PARTICIPANT_ROWS);
    }

    private WebElement participantRow(String name) {
        return participantRows().stream()
                .filter(row -> row.findElement(By.cssSelector(".participant-name")).getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private int stepsOf(WebElement row) {
        return parseSteps(row.findElement(By.cssSelector(".participant-steps")).getText());
    }

    private int parseSteps(String stepsText) {
        return Integer.parseInt(stepsText.replaceAll("[^0-9]", ""));
    }

    private void assertDescendingStepCounts() {
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());

        int previousSteps = Integer.MAX_VALUE;
        for (WebElement row : rows) {
            int currentSteps = stepsOf(row);
            assertTrue("Participant step counts should be in descending order",
                    previousSteps >= currentSteps);
            previousSteps = currentSteps;
        }
    }
}
