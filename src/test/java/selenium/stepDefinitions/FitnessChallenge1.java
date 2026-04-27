package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import selenium.stepDefinitions.Hooks;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallenge1 {

    @Given("^I am on the fitness challenge page \"(.*)\"$")
    public void navigateToFitnessChallengePage(String url) {
        Hooks.driver.get(url);
    }

    @When("^the page loads for the first time$")
    public void pageLoadsForFirstTime() {
        // Page loading is handled by the Given step, this is just a placeholder
    }

    @Then("^the page displays title \"(.*)\"$")
    public void verifyPageTitle(String expectedTitle) {
        WebElement titleElement = Hooks.driver.findElement(By.tagName("h2"));
        assertEquals(expectedTitle, titleElement.getText());
    }

    @Then("^(\\d+) participants are displayed in the list$")
    public void verifyParticipantCount(int expectedCount) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(expectedCount, participants.size());
    }

    @Then("^the default participants are displayed:$")
    public void verifyDefaultParticipants(DataTable dataTable) {
        List<Map<String, String>> expectedParticipants = new java.util.ArrayList<>(dataTable.asMaps(String.class, String.class));

        // Sort expected participants by steps in descending order to match webpage display
        expectedParticipants.sort((a, b) -> {
            int stepsA = Integer.parseInt(a.get("Steps").replace(",", ""));
            int stepsB = Integer.parseInt(b.get("Steps").replace(",", ""));
            return Integer.compare(stepsB, stepsA); // Descending order
        });

        List<WebElement> participantElements = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        
        for (int i = 0; i < expectedParticipants.size(); i++) {
            Map<String, String> expectedParticipant = expectedParticipants.get(i);
            WebElement participantElement = participantElements.get(i);
            
            // Get name and steps from the participant element
            String actualName = participantElement.findElement(By.className("participant-name")).getText();
            String actualSteps = participantElement.findElement(By.className("participant-steps")).getText();
            
            // Remove "steps" from the actual steps text to match expected format
            actualSteps = actualSteps.replace(" steps", "").replace(",", "");
            
            // Expected steps also have commas, so remove them for comparison
            String expectedSteps = expectedParticipant.get("Steps").replace(",", "");
            
            assertEquals(expectedParticipant.get("Name"), actualName);
            assertEquals(expectedSteps, actualSteps);
        }
    }

    @Then("^\"Add Steps\" buttons are visible at top and bottom$")
    public void verifyAddStepsButtonsVisible() {
        List<WebElement> addStepsButtons = Hooks.driver.findElements(By.id("addStepsBtn"));
        assertEquals(2, addStepsButtons.size()); // Should have top and bottom buttons
        
        for (WebElement button : addStepsButtons) {
            assertTrue(button.isDisplayed());
            assertEquals("Add Steps", button.getText());
        }
    }

    @Then("^\"Reset List\" buttons are visible at top and bottom$")
    public void verifyResetListButtonsVisible() {
        List<WebElement> resetButtons = Hooks.driver.findElements(By.id("resetBtn"));
        assertEquals(2, resetButtons.size()); // Should have top and bottom buttons
        
        for (WebElement button : resetButtons) {
            assertTrue(button.isDisplayed());
            assertEquals("Reset List", button.getText());
        }
    }
}
