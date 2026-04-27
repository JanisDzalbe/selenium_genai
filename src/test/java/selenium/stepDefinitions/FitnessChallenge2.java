package selenium.stepDefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import selenium.stepDefinitions.Hooks;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallenge2 {


    @When("^I view the participant list$")
    public void viewParticipantList() {
        // Participants are already displayed on page load, just ensure we're on the page
        assertTrue(Hooks.driver.findElement(By.id("participantsList")).isDisplayed());
    }

    @Then("^participants are displayed in descending order by step count$")
    public void verifyParticipantsDescendingOrder() {
        List<WebElement> participantElements = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        
        int previousSteps = Integer.MAX_VALUE;
        for (WebElement participantElement : participantElements) {
            String stepsText = participantElement.findElement(By.className("participant-steps")).getText();
            int currentSteps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));
            
            // Each participant's steps should be less than or equal to the previous one
            assertTrue("Participants should be in descending order", currentSteps <= previousSteps);
            previousSteps = currentSteps;
        }
    }

    @Then("^each participant's step count is greater than or equal to the participant below them$")
    public void verifyStepCountOrdering() {
        List<WebElement> participantElements = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        
        for (int i = 0; i < participantElements.size() - 1; i++) {
            WebElement currentParticipant = participantElements.get(i);
            WebElement nextParticipant = participantElements.get(i + 1);
            
            String currentStepsText = currentParticipant.findElement(By.className("participant-steps")).getText();
            String nextStepsText = nextParticipant.findElement(By.className("participant-steps")).getText();
            
            int currentSteps = Integer.parseInt(currentStepsText.replace(" steps", "").replace(",", ""));
            int nextSteps = Integer.parseInt(nextStepsText.replace(" steps", "").replace(",", ""));
            
            // Current participant's steps should be >= next participant's steps
            assertTrue("Each participant should have steps >= participant below them", currentSteps >= nextSteps);
        }
    }

    @Then("^the (\\d+)(?:st|nd|rd|th) place participant displays a (gold|silver|bronze) trophy icon$")
    public void verifyTrophyIcon(int position, String color) {
        List<WebElement> participantElements = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        WebElement participantElement = participantElements.get(position - 1); // 0-based index
        
        // Check if trophy icon exists
        List<WebElement> trophyIcons = participantElement.findElements(By.className("fa-trophy"));
        assertEquals("Trophy icon should be present for " + position + " place", 1, trophyIcons.size());
        
        WebElement trophyIcon = trophyIcons.get(0);
        String iconStyle = trophyIcon.getAttribute("style");
        // Remove spaces from style for more flexible matching
        String normalizedStyle = iconStyle.replaceAll("\\s", "");

        // Check the color based on position
        boolean colorMatches = false;
        switch (color) {
            case "gold":
                colorMatches = normalizedStyle.contains("color:gold");
                break;
            case "silver":
                colorMatches = normalizedStyle.contains("color:silver");
                break;
            case "bronze":
                // Bronze can be represented as hex #cd7f32 or RGB(205,127,50)
                colorMatches = normalizedStyle.contains("color:#cd7f32") ||
                              normalizedStyle.contains("color:rgb(205,127,50)") ||
                              normalizedStyle.contains("color:rgb(205127,50)") ||
                              normalizedStyle.contains("cd7f32");
                break;
            default:
                throw new IllegalArgumentException("Unknown trophy color: " + color);
        }
        
        assertTrue(position + " place trophy should be " + color + ". Actual style: " + iconStyle, colorMatches);
    }

    @Then("^participants ranked (\\d+)(?:st|nd|rd|th) and below have no trophy icons$")
    public void verifyNoTrophyIconsFromPosition(int startPosition) {
        List<WebElement> participantElements = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        
        // Check from startPosition onwards (0-based index)
        for (int i = startPosition - 1; i < participantElements.size(); i++) {
            WebElement participantElement = participantElements.get(i);
            List<WebElement> trophyIcons = participantElement.findElements(By.className("fa-trophy"));
            assertEquals("Participant at position " + (i + 1) + " should have no trophy icon", 0, trophyIcons.size());
        }
    }
}
