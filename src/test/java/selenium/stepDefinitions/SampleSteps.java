package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SampleSteps {
    private static final By PARTICIPANTS = By.cssSelector("#participantsList li");
    private static final By PARTICIPANT_NAME = By.cssSelector(".participant-name");
    private static final By PARTICIPANT_STEPS = By.cssSelector(".participant-steps");
    private static final By TROPHY = By.cssSelector("i.fa-trophy");

    @Given("^I am on the fitness challenge page \"([^\"]+)\"$")
    public void openFitnessChallengePage(String url) {
        driver().get(url);
    }

    @When("the page loads for the first time")
    public void waitForInitialPageLoad() {
        new WebDriverWait(driver(), Duration.ofSeconds(10))
                .until(d -> d.findElements(PARTICIPANTS).size() == 10);
    }

    @When("I view the participant list")
    public void waitForParticipantList() {
        new WebDriverWait(driver(), Duration.ofSeconds(10))
                .until(d -> d.findElements(PARTICIPANTS).size() == 10);
    }

    @Then("^the page displays title \"([^\"]+)\"$")
    public void pageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, driver().findElement(By.cssSelector("h2")).getText());
    }

    @Then("^(\\d+) participants are displayed in the list$")
    public void participantCountIsDisplayed(int expectedCount) {
        assertEquals(expectedCount, driver().findElements(PARTICIPANTS).size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable participantsTable) {
        Map<String, Integer> displayedParticipants = getParticipantSteps();
        for (Map<String, String> row : participantsTable.asMaps(String.class, String.class)) {
            String name = row.get("Name");
            int expectedSteps = Integer.parseInt(row.get("Steps").replace(",", ""));
            assertTrue("Missing participant: " + name, displayedParticipants.containsKey(name));
            assertEquals("Incorrect steps for " + name, expectedSteps, (int) displayedParticipants.get(name));
        }
    }

    @Then("^\"([^\"]+)\" buttons are visible at top and bottom$")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        String buttonId;
        if ("Add Steps".equals(buttonText)) {
            buttonId = "addStepsBtn";
        } else if ("Reset List".equals(buttonText)) {
            buttonId = "resetBtn";
        } else {
            throw new IllegalArgumentException("Unexpected fitness challenge button: " + buttonText);
        }

        List<WebElement> buttons = driver().findElements(By.cssSelector("button[id='" + buttonId + "']"));
        assertEquals("Expected a button at the top and bottom of the page", 2, buttons.size());
        for (WebElement button : buttons) {
            assertTrue(buttonText + " button should be visible", button.isDisplayed());
        }
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreInDescendingOrder() {
        List<WebElement> participants = driver().findElements(PARTICIPANTS);
        for (int i = 1; i < participants.size(); i++) {
            assertTrue("Participants are not in descending step-count order",
                    getStepCount(participants.get(i - 1)) >= getStepCount(participants.get(i)));
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantHasAtLeastAsManyStepsAsParticipantBelow() {
        List<WebElement> participants = driver().findElements(PARTICIPANTS);
        for (int i = 0; i + 1 < participants.size(); i++) {
            assertTrue("A participant has fewer steps than the participant below",
                    getStepCount(participants.get(i)) >= getStepCount(participants.get(i + 1)));
        }
    }

    @Then("^the (1st|2nd|3rd) place participant displays a (gold|silver|bronze) trophy icon$")
    public void placeParticipantDisplaysTrophy(String place, String color) {
        int participantIndex;
        if ("1st".equals(place)) {
            participantIndex = 0;
        } else if ("2nd".equals(place)) {
            participantIndex = 1;
        } else {
            participantIndex = 2;
        }

        List<WebElement> participants = driver().findElements(PARTICIPANTS);
        assertTrue("The participant list does not contain " + place + " place",
                participants.size() > participantIndex);
        List<WebElement> trophies = participants.get(participantIndex).findElements(TROPHY);
        assertEquals("Expected one trophy icon for " + place + " place", 1, trophies.size());
        assertTrue("Unexpected " + color + " trophy color: " + trophies.get(0).getCssValue("color"),
                trophies.get(0).getCssValue("color").replace(" ", "")
                        .contains(trophyRgb(color)));
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsFromFourthPlaceHaveNoTrophies() {
        List<WebElement> participants = driver().findElements(PARTICIPANTS);
        for (int i = 3; i < participants.size(); i++) {
            assertTrue("Participant ranked " + (i + 1) + " unexpectedly has a trophy",
                    participants.get(i).findElements(TROPHY).isEmpty());
        }
    }

    private WebDriver driver() {
        return Hooks.driver;
    }

    private Map<String, Integer> getParticipantSteps() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (WebElement participant : driver().findElements(PARTICIPANTS)) {
            String name = participant.findElement(PARTICIPANT_NAME).getText();
            result.put(name, getStepCount(participant));
        }
        return result;
    }

    private int getStepCount(WebElement participant) {
        String steps = participant.findElement(PARTICIPANT_STEPS).getText()
                .replace(" steps", "")
                .replace(",", "")
                .trim();
        return Integer.parseInt(steps);
    }

    private String trophyRgb(String color) {
        switch (color) {
            case "gold":
                return "255,215,0";
            case "silver":
                return "192,192,192";
            default:
                return "205,127,50";
        }
    }
}
