package selenium.stepDefinitions;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.openqa.selenium.WebElement;
import selenium.stepDefinitions.Hooks;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SampleSteps {

    public SampleSteps() {
        // Can use static driver from Hooks directly
    }

    @Given("^Some Example$")
    public void someStep() throws Throwable {
        // TODO: remove this example and implement actual steps
    }

    @Given("I am on the fitness challenge page {string}")
    public void iAmOnTheFitnessChallengePage(String url) {
        Hooks.driver.get(url);
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String title) {
        assertEquals(title, Hooks.driver.getTitle());
    }

    @And("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int n) {
        List<WebElement> participants = Hooks.driver.findElement(By.id("participantsList")).findElements(By.tagName("div"));
        assertEquals(n, participants.size());
    }

    @And("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(List<List<String>> expectedParticipants) {
        List<WebElement> participantElements = Hooks.driver.findElement(By.id("participantsList")).findElements(By.tagName("div"));
        HashMap<String, Integer> infoOnWebsite = new HashMap<>();
        for (WebElement element : participantElements) {
            String name = element.findElement(By.className("participant-name")).getText();
            int steps = Integer.parseInt(element.findElement(By.className("participant-steps")).getText().replaceAll("[^0-9]", ""));
            infoOnWebsite.put(name, steps);
        }

        HashMap<String, Integer> expectedInfo = new HashMap<>();
        expectedParticipants.removeFirst();
        for (List<String> participant : expectedParticipants) {
            String name = participant.getFirst();
            int steps = Integer.parseInt(participant.getLast().replace(",", ""));
            expectedInfo.put(name, steps);
        }

        assertEquals(expectedInfo, infoOnWebsite);
    }

    @And("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String text) {
        String id = text.equals("Add Steps") ? "addStepsBtn" : "resetBtn";
        List<WebElement> buttons = Hooks.driver.findElements(By.id(id));
        assertEquals(2, buttons.size());
        for (WebElement button : buttons) {
            assertTrue(button.isDisplayed());
            assertEquals(text, button.getText());
        }
    }
}
