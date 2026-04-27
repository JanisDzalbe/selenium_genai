package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SampleSteps {

    private WebDriver driver;

    public SampleSteps() {
        this.driver = Hooks.driver;
    }

    @Given("^I am on the fitness challenge page \"([^\"]*)\"$")
    public void iAmOnTheFitnessChallengePage(String url) {
        driver.get(url);
    }

    @When("^the page loads for the first time$")
    public void thePageLoadsForTheFirstTime() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
    }

    @Then("^the page displays title \"([^\"]*)\"$")
    public void thePageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, driver.getTitle());
    }

    @Then("^(\\d+) participants are displayed in the list$")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        List<WebElement> participants = driver.findElements(By.className("participant-name"));
        assertEquals(expectedCount, participants.size());
    }

    @Then("^the default participants are displayed:$")
    public void theDefaultParticipantsAreDisplayed(DataTable dataTable) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("participant-name")));

        // DataTable.asMaps() skips the header row automatically, keys are column names
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String expectedName  = row.get("Name");
            // Normalize expected steps: strip commas so "8,500" -> "8500"
            String expectedSteps = row.get("Steps").replace(",", "");

            WebElement nameElement = driver.findElement(
                    By.xpath("//span[contains(@class,'participant-name') and normalize-space()='" + expectedName + "']")
            );
            WebElement stepsElement = nameElement.findElement(
                    By.xpath("following-sibling::span[contains(@class,'participant-steps')]")
            );

            // Page shows "8,500 steps" -> strip " steps" and commas -> "8500"
            String actualSteps = stepsElement.getText()
                    .replace(" steps", "")
                    .replace(",", "")
                    .trim();

            assertEquals("Steps mismatch for " + expectedName, expectedSteps, actualSteps);
        }
    }

    @Then("^\"([^\"]*)\" buttons are visible at top and bottom$")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        // The two page-level buttons both share id="addStepsBtn" / id="resetBtn"
        // We match by id to exclude the modal's internal button
        String expectedId = buttonText.equals("Add Steps") ? "addStepsBtn" : "resetBtn";

        List<WebElement> buttons = driver.findElements(By.id(expectedId));

        assertEquals("Expected 2 '" + buttonText + "' buttons (top and bottom)", 2, buttons.size());

        for (WebElement button : buttons) {
            assertTrue("Button '" + buttonText + "' should be visible", button.isDisplayed());
        }
    }

    @When("^I view the participant list$")
    public void iViewTheParticipantList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
    }

    @Then("^participants are displayed in descending order by step count$")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        List<WebElement> participantLis = driver.findElements(By.cssSelector("#participantsList li"));
        List<Integer> stepCounts = new ArrayList<>();
        for (WebElement li : participantLis) {
            WebElement stepsElement = li.findElement(By.className("participant-steps"));
            String stepsText = stepsElement.getText().replace(" steps", "").replace(",", "");
            int steps = Integer.parseInt(stepsText);
            stepCounts.add(steps);
        }
        for (int i = 0; i < stepCounts.size() - 1; i++) {
            assertTrue("Step count at position " + (i+1) + " (" + stepCounts.get(i) + ") should be >= position " + (i+2) + " (" + stepCounts.get(i+1) + ")", stepCounts.get(i) >= stepCounts.get(i+1));
        }
    }

    @Then("^each participant's step count is greater than or equal to the participant below them$")
    public void eachParticipantStepCountIsGreaterThanOrEqualToTheParticipantBelowThem() {
        List<WebElement> participantLis = driver.findElements(By.cssSelector("#participantsList li"));
        List<Integer> stepCounts = new ArrayList<>();
        for (WebElement li : participantLis) {
            WebElement stepsElement = li.findElement(By.className("participant-steps"));
            String stepsText = stepsElement.getText().replace(" steps", "").replace(",", "");
            int steps = Integer.parseInt(stepsText);
            stepCounts.add(steps);
        }
        for (int i = 0; i < stepCounts.size() - 1; i++) {
            assertTrue("Step count at position " + (i+1) + " (" + stepCounts.get(i) + ") should be >= position " + (i+2) + " (" + stepCounts.get(i+1) + ")", stepCounts.get(i) >= stepCounts.get(i+1));
        }
    }

    @Then("^the (\\d+)(?:st|nd|rd|th) place participant displays a (gold|silver|bronze) trophy icon$")
    public void thePlaceParticipantDisplaysATrophyIcon(int place, String color) {
        List<WebElement> participantLis = driver.findElements(By.cssSelector("#participantsList li"));
        WebElement li = participantLis.get(place - 1);
        WebElement trophy = li.findElement(By.cssSelector("i.fa-trophy"));
        String actualColor = trophy.getCssValue("color");
        String expectedRgb;
        switch (color) {
            case "gold": expectedRgb = "rgba(255, 215, 0, 1)"; break;
            case "silver": expectedRgb = "rgba(192, 192, 192, 1)"; break;
            case "bronze": expectedRgb = "rgba(205, 127, 50, 1)"; break;
            default: throw new IllegalArgumentException("Unknown color: " + color);
        }
        assertEquals(expectedRgb, actualColor);
    }

    @Then("^participants ranked (\\d+)(?:th|st|nd|rd) and below have no trophy icons$")
    public void participantsRankedAndBelowHaveNoTrophyIcons(int rank) {
        List<WebElement> participantLis = driver.findElements(By.cssSelector("#participantsList li"));
        for (int i = rank - 1; i < participantLis.size(); i++) {
            WebElement li = participantLis.get(i);
            List<WebElement> trophies = li.findElements(By.cssSelector("i.fa-trophy"));
            assertEquals("Participant at rank " + (i+1) + " should have no trophy", 0, trophies.size());
        }
    }
}