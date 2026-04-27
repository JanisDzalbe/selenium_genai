package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.stepDefinitions.Hooks;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallenge4 {

    // Store participant step counts for comparison
    private Map<String, Integer> participantSteps = new HashMap<>();

    @Given("^I note the current step count for \"(.*)\"$")
    public void noteCurrentStepCount(String participantName) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));

        for (WebElement participant : participants) {
            String name = participant.findElement(By.className("participant-name")).getText();
            if (name.equals(participantName)) {
                String stepsText = participant.findElement(By.className("participant-steps")).getText();
                int steps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));
                participantSteps.put(participantName, steps);
                return;
            }
        }
        throw new RuntimeException("Participant not found: " + participantName);
    }

    @When("^I open the \"Add Steps\" modal$")
    public void openAddStepsModal() {
        List<WebElement> addStepsButtons = Hooks.driver.findElements(By.id("addStepsBtn"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        WebDriverWait wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    @When("^I select \"(.*)\" from the dropdown$")
    public void selectParticipantFromDropdown(String participantName) {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByVisibleText(participantName);
    }

    @When("^I select any participant$")
    public void selectAnyParticipant() {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        // Select the first non-default option (index 1, since 0 is the default)
        List<WebElement> options = select.getOptions();
        if (options.size() > 1) {
            select.selectByIndex(1);
        } else {
            throw new RuntimeException("No participants available in dropdown");
        }
    }

    @When("^I enter \"(.*)\" in the steps input field$")
    public void enterStepsInInputField(String steps) {
        WebElement stepsInput = Hooks.driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys(steps);
    }

    @When("^I click \"Add Steps\" button$")
    public void clickAddStepsButton() {
        WebElement submitButton = Hooks.driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();
    }

    @Then("^the modal closes automatically$")
    public void verifyModalClosesAutomatically() {
        WebDriverWait wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        String display = modal.getCssValue("display");
        assertEquals("Modal should be hidden", "none", display);
    }

    @Then("^the participant list refreshes$")
    public void verifyParticipantListRefreshes() {
        WebElement participantsList = Hooks.driver.findElement(By.id("participantsList"));
        assertTrue("Participant list should be visible", participantsList.isDisplayed());
    }

    @Then("^Mike Kid's step count increases by 1,000$")
    public void verifyMikeKidsStepIncreaseBy1000() {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));

        for (WebElement participant : participants) {
            String name = participant.findElement(By.className("participant-name")).getText();
            if (name.equals("Mike Kid")) {
                String stepsText = participant.findElement(By.className("participant-steps")).getText();
                int currentSteps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));
                int previousSteps = participantSteps.getOrDefault("Mike Kid", 0);
                int expectedSteps = previousSteps + 1000;

                assertEquals("Mike Kid should have 1000 more steps", expectedSteps, currentSteps);
                return;
            }
        }
        throw new RuntimeException("Mike Kid not found in participant list");
    }

    @Then("^the list re-sorts if Mike Kid's new total changes his ranking$")
    public void verifyListReSortsIfRankingChanged() {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));

        // Verify participants are still in descending order by steps
        int previousSteps = Integer.MAX_VALUE;
        for (WebElement participant : participants) {
            String stepsText = participant.findElement(By.className("participant-steps")).getText();
            int currentSteps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));

            assertTrue("Participants should be in descending order", currentSteps <= previousSteps);
            previousSteps = currentSteps;
        }
    }

    @Then("^an alert appears: \"(.*)\"$")
    public void verifyAlertMessage(String expectedMessage) {
        WebDriverWait wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.alertIsPresent());

        String alertText = Hooks.driver.switchTo().alert().getText();
        assertEquals("Alert message should match", expectedMessage, alertText);
    }

    @When("^I accept the alert$")
    public void acceptAlert() {
        Hooks.driver.switchTo().alert().accept();
    }

    @Then("^the modal remains open$")
    public void verifyModalRemainsOpen() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        assertTrue("Modal should still be visible", modal.isDisplayed());
    }


    @Then("^the step count updates correctly with the large number$")
    public void verifyStepCountUpdatesWithLargeNumber() {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));

        // Find the participant with the largest step count (should be first)
        WebElement firstParticipant = participants.get(0);
        String stepsText = firstParticipant.findElement(By.className("participant-steps")).getText();
        int steps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));

        // Verify the large number was added (should be > 999999 if there were initial steps)
        assertTrue("Step count should reflect the large addition", steps >= 999999);
    }

    @Then("^the participant moves to first place$")
    public void verifyParticipantMovesToFirstPlace() {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));

        // Get the first participant
        WebElement firstParticipant = participants.get(0);
        String stepsText = firstParticipant.findElement(By.className("participant-steps")).getText();
        int firstParticipantSteps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));

        // Verify all other participants have fewer steps
        for (int i = 1; i < participants.size(); i++) {
            WebElement participant = participants.get(i);
            String otherStepsText = participant.findElement(By.className("participant-steps")).getText();
            int otherSteps = Integer.parseInt(otherStepsText.replace(" steps", "").replace(",", ""));

            assertTrue("First place participant should have the most steps",
                    firstParticipantSteps >= otherSteps);
        }
    }
}
