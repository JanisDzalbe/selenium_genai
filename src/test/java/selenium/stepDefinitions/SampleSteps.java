package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static selenium.stepDefinitions.Hooks.driver;

public class SampleSteps {
    private final WebDriver driver;
    
    public SampleSteps() {
        // Can use static driver from Hooks directly
            this.driver = Hooks.driver;
    }

    @Given("^I am on the fitness challenge page \"([^\"]*)\"$")
    public void iAmOnTheFitnessChallengePage(String url) throws Throwable {
        driver.get(url);
    }

    @When("^I view the participant list$")
    public void iViewTheParticipantList() throws Throwable {
        // The participant list is already displayed on the page
        // This step just ensures we're looking at the list
        WebElement participantsList = driver.findElement(By.id("participantsList"));
        assertTrue("Participants list should be visible", participantsList.isDisplayed());
    }

    @Then("^participants are displayed in descending order by step count$")
    public void participantsAreDisplayedInDescendingOrder() throws Throwable {
        List<WebElement> participants = driver.findElements(By.xpath("//ul[@id='participantsList']//li"));
        
        int previousStepCount = Integer.MAX_VALUE;
        
        for (WebElement participant : participants) {
            WebElement stepsElement = participant.findElement(By.className("participant-steps"));
            String stepsText = stepsElement.getText();
            
            // Extract the number from text like "15,000 steps"
            int currentStepCount = extractStepCount(stepsText);
            
            assertTrue("Steps should be in descending order. Previous: " + previousStepCount + ", Current: " + currentStepCount,
                    currentStepCount <= previousStepCount);
            
            previousStepCount = currentStepCount;
        }
    }

    @Then("^each participant's step count is greater than or equal to the participant below them$")
    public void eachParticipantStepsGreaterThanOrEqualBelow() throws Throwable {
        List<WebElement> participants = driver.findElements(By.xpath("//ul[@id='participantsList']//li"));
        
        for (int i = 0; i < participants.size() - 1; i++) {
            WebElement currentParticipant = participants.get(i);
            WebElement nextParticipant = participants.get(i + 1);
            
            int currentSteps = extractStepCount(currentParticipant.findElement(By.className("participant-steps")).getText());
            int nextSteps = extractStepCount(nextParticipant.findElement(By.className("participant-steps")).getText());
            
            assertTrue("Participant " + i + " steps (" + currentSteps + ") should be >= Participant " + (i + 1) + " steps (" + nextSteps + ")",
                    currentSteps >= nextSteps);
        }
    }

    @Then("^the 1st place participant displays a gold trophy icon$")
    public void firstPlaceDisplaysGoldTrophy() throws Throwable {
        WebElement firstParticipant = driver.findElement(By.id("participant_display0"));
        WebElement trophy = firstParticipant.findElement(By.className("fa-trophy"));
        String color = trophy.getCssValue("color");
        
        // Check for gold color (RGB format or similar)
        assertTrue("First place should have gold trophy", isGoldColor(color));
    }

    @Then("^the 2nd place participant displays a silver trophy icon$")
    public void secondPlaceDisplaysSilverTrophy() throws Throwable {
        WebElement secondParticipant = driver.findElement(By.id("participant_display1"));
        WebElement trophy = secondParticipant.findElement(By.className("fa-trophy"));
        String color = trophy.getCssValue("color");
        
        // Check for silver color
        assertTrue("Second place should have silver trophy", isSilverColor(color));
    }

    @Then("^the 3rd place participant displays a bronze trophy icon$")
    public void thirdPlaceDisplaysBronzeTrophy() throws Throwable {
        WebElement thirdParticipant = driver.findElement(By.id("participant_display2"));
        WebElement trophy = thirdParticipant.findElement(By.className("fa-trophy"));
        String color = trophy.getCssValue("color");
        
        // Check for bronze color
        assertTrue("Third place should have bronze trophy", isBronzeColor(color));
    }

    @Then("^participants ranked 4th and below have no trophy icons$")
    public void fourthAndBelowNoTrophy() throws Throwable {
        for (int i = 3; i <= 9; i++) {
            WebElement participant = driver.findElement(By.id("participant_display" + i));
            List<WebElement> trophies = participant.findElements(By.className("fa-trophy"));
            
            assertTrue("Participant ranked " + (i + 1) + " should not have trophy icon", trophies.isEmpty());
        }
    }

    /**
     * Extracts the step count from text like "15,000 steps"
     */
    private int extractStepCount(String stepsText) {
        // Remove commas and extract the number
        Pattern pattern = Pattern.compile("([0-9,]+)");
        Matcher matcher = pattern.matcher(stepsText);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1).replace(",", ""));
        }
        
        throw new IllegalArgumentException("Could not extract step count from: " + stepsText);
    }

    /**
     * Check if color is gold
     */
    private boolean isGoldColor(String color) {
        // Gold or similar yellow colors
        return color.contains("gold") || color.toLowerCase().contains("255, 215, 0") || 
               color.toLowerCase().contains("ffd700") || color.toLowerCase().contains("gold");
    }

    /**
     * Check if color is silver
     */
    private boolean isSilverColor(String color) {
        // Silver or similar white/light gray colors
        return color.contains("silver") || color.toLowerCase().contains("192, 192, 192") ||
               color.toLowerCase().contains("c0c0c0") || color.toLowerCase().contains("silver");
    }

    /**
     * Check if color is bronze
     */
    private boolean isBronzeColor(String color) {
        // Bronze or similar brown colors (#cd7f32)
        return color.contains("bronze") || color.toLowerCase().contains("205, 127, 50") ||
               color.toLowerCase().contains("cd7f32") || color.toLowerCase().contains("bronze");
    }
}
