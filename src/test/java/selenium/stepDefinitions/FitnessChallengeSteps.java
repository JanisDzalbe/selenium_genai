package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallengeSteps {
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");

    private WebDriverWait wait;

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(10));
        Hooks.driver.get(url);
        waitForParticipants();

        ((JavascriptExecutor) Hooks.driver).executeScript(
                "for (var i = localStorage.length - 1; i >= 0; i--) {" +
                "  var key = localStorage.key(i);" +
                "  if (key && (key.startsWith('participant') || key === 'fitnessReload')) {" +
                "    localStorage.removeItem(key);" +
                "  }" +
                "}");
        Hooks.driver.navigate().refresh();
        waitForParticipants();
    }

    @When("the page loads for the first time")
    public void pageLoadsForTheFirstTime() {
        waitForParticipants();
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        waitForParticipants();
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, wait.until(
                driver -> driver.findElement(By.cssSelector("h2")).getText()));
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable participantsTable) {
        Map<String, String> expected = new LinkedHashMap<>();
        for (Map<String, String> participant : participantsTable.asMaps(String.class, String.class)) {
            expected.put(participant.get("Name"), participant.get("Steps"));
        }
        assertEquals(expected, readParticipants());
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        String buttonId = switch (buttonText) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported button: " + buttonText);
        };
        List<WebElement> buttons = Hooks.driver.findElements(By.id(buttonId));
        assertEquals(2, buttons.size());

        WebElement list = Hooks.driver.findElement(By.id("participantsList"));
        int listTop = list.getLocation().getY();
        int listBottom = listTop + list.getSize().getHeight();
        for (WebElement button : buttons) {
            assertTrue(button.isDisplayed());
            assertEquals(buttonText, button.getText());
        }
        assertTrue("The top button should appear before the participant list",
                buttons.get(0).getLocation().getY() < listTop);
        assertTrue("The bottom button should appear after the participant list",
                buttons.get(1).getLocation().getY() > listBottom);
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        List<BigInteger> steps = participantStepCounts();
        for (int i = 1; i < steps.size(); i++) {
            assertTrue("Step counts are not in descending order",
                    steps.get(i - 1).compareTo(steps.get(i)) >= 0);
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantsStepCountIsAtLeastTheParticipantBelow() {
        List<BigInteger> steps = participantStepCounts();
        for (int i = 1; i < steps.size(); i++) {
            assertTrue("A participant has fewer steps than the participant below",
                    steps.get(i - 1).compareTo(steps.get(i)) >= 0);
        }
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceDisplaysGoldTrophy() {
        assertTrophy(0, "gold");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceDisplaysSilverTrophy() {
        assertTrophy(1, "silver");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceDisplaysBronzeTrophy() {
        assertTrophy(2, "#cd7f32");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsRankedFourthAndBelowHaveNoTrophyIcons() {
        List<WebElement> rows = participantRows();
        for (int i = 3; i < rows.size(); i++) {
            assertTrue("Unexpected trophy icon at rank " + (i + 1),
                    rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    private void waitForParticipants() {
        wait.until(driver -> driver.findElements(PARTICIPANT_ROWS).size() == 10);
    }

    private List<WebElement> participantRows() {
        return Hooks.driver.findElements(PARTICIPANT_ROWS);
    }

    private Map<String, String> readParticipants() {
        Map<String, String> participants = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            String name = row.findElement(By.cssSelector(".participant-name")).getText();
            String steps = row.findElement(By.cssSelector(".participant-steps"))
                    .getText()
                    .replace(" steps", "")
                    .trim();
            participants.put(name, steps);
        }
        return participants;
    }

    private List<BigInteger> participantStepCounts() {
        List<BigInteger> steps = new ArrayList<>();
        for (WebElement row : participantRows()) {
            String displayedSteps = row.findElement(By.cssSelector(".participant-steps"))
                    .getText()
                    .replace(",", "")
                    .replace(" steps", "")
                    .trim();
            steps.add(new BigInteger(displayedSteps));
        }
        return steps;
    }

    private void assertTrophy(int rank, String expectedColor) {
        List<WebElement> rows = participantRows();
        assertTrue("No participant exists at rank " + (rank + 1), rows.size() > rank);
        List<WebElement> trophies = rows.get(rank).findElements(By.cssSelector("i.fa-trophy"));
        assertEquals(1, trophies.size());
        String style = trophies.get(0).getAttribute("style").replace(" ", "");
        assertTrue("Unexpected trophy color: " + style, style.contains("color:" + expectedColor));
    }
}
