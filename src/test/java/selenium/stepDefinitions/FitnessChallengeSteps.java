package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FitnessChallengeSteps {
    private static final String PAGE_URL =
            "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge";

    private WebDriverWait webDriverWait() {
        return new WebDriverWait(Hooks.driver, Duration.ofSeconds(10));
    }

    @Given("I am on the fitness challenge page {string}")
    public void iAmOnTheFitnessChallengePage(String url) {
        Hooks.driver.manage().window().maximize();
        Hooks.driver.get(url);
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
        ((JavascriptExecutor) Hooks.driver).executeScript("localStorage.clear()");
        Hooks.driver.navigate().refresh();
        waitForParticipants();
    }

    @When("I view the participant list")
    public void iViewTheParticipantList() {
        waitForParticipants();
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle,
                Hooks.driver.findElement(By.cssSelector("h2")).getText().trim());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(DataTable table) {
        List<WebElement> rows = participantRows();
        assertEquals(table.asMaps(String.class, String.class).size(), rows.size());

        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            String name = row.get("Name");
            int expectedSteps = Integer.parseInt(row.get("Steps").replace(",", "").trim());
            boolean found = false;

            for (WebElement participantRow : rows) {
                if (participantName(participantRow).equals(name)) {
                    assertEquals(expectedSteps, stepsForRow(participantRow),
                            "Unexpected step count for " + name);
                    found = true;
                    break;
                }
            }

            assertTrue(found, "Participant not displayed: " + name);
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        assertEquals(2, visibleButtons(buttonText).size(),
                "Expected two visible \"" + buttonText + "\" buttons");
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        int previousSteps = Integer.MAX_VALUE;
        for (WebElement row : participantRows()) {
            int steps = stepsForRow(row);
            assertTrue(steps <= previousSteps,
                    "Participants are not in descending step-count order");
            previousSteps = steps;
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantStepCountIsGreaterThanOrEqualToTheParticipantBelow() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceDisplaysGoldTrophy() {
        assertTrophy(participantRows().get(0), "gold");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceDisplaysSilverTrophy() {
        assertTrophy(participantRows().get(1), "silver");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceDisplaysBronzeTrophy() {
        assertTrophy(participantRows().get(2), "bronze");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsRankedFourthAndBelowHaveNoTrophyIcons() {
        List<WebElement> rows = participantRows();
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Participant ranked " + (i + 1) + " should not have a trophy icon");
        }
    }

    private void waitForParticipants() {
        webDriverWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("#participantsList li")));
    }

    private List<WebElement> participantRows() {
        waitForParticipants();
        return Hooks.driver.findElements(By.cssSelector("#participantsList li"));
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText().trim();
    }

    private int stepsForRow(WebElement row) {
        String text = row.findElement(By.cssSelector(".participant-steps"))
                .getText()
                .replace(",", "")
                .replace(" steps", "")
                .trim();
        return Integer.parseInt(text);
    }

    private List<WebElement> visibleButtons(String text) {
        List<WebElement> result = new ArrayList<>();
        for (WebElement button : Hooks.driver.findElements(By.xpath(
                "//button[normalize-space()='" + text + "']"))) {
            if (button.isDisplayed()) {
                result.add(button);
            }
        }
        return result;
    }

    private void assertTrophy(WebElement row, String medal) {
        WebElement trophy = row.findElement(By.cssSelector("i.fa-trophy"));
        String color = trophy.getCssValue("color").replace(" ", "").toLowerCase();

        if ("gold".equals(medal)) {
            assertTrue(color.equals("rgb(255,215,0)")
                            || color.equals("rgba(255,215,0,1)")
                            || color.equals("gold"),
                    "Expected a gold trophy, but found color " + color);
        } else if ("silver".equals(medal)) {
            assertTrue(color.equals("rgb(192,192,192)")
                            || color.equals("rgba(192,192,192,1)")
                            || color.equals("silver"),
                    "Expected a silver trophy, but found color " + color);
        } else {
            assertTrue(color.equals("rgb(205,127,50)")
                            || color.equals("rgba(205,127,50,1)")
                            || color.equals("#cd7f32")
                            || color.equals("bronze"),
                    "Expected a bronze trophy, but found color " + color);
        }
    }
}
