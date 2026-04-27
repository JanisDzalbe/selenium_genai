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

import static org.junit.Assert.*;

public class SampleSteps {

    public SampleSteps() {
        // Can use static driver from Hooks directly
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

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int n) {
        List<WebElement> participants = Hooks.driver.findElement(By.id("participantsList")).findElements(By.tagName("div"));
        assertEquals(n, participants.size());
    }

    @Then("the default participants are displayed:")
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

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String text) {
        String id = text.equals("Add Steps") ? "addStepsBtn" : "resetBtn";
        List<WebElement> buttons = Hooks.driver.findElements(By.id(id));
        assertEquals(2, buttons.size());
        for (WebElement button : buttons) {
            assertTrue(button.isDisplayed());
            assertEquals(text, button.getText());
        }
    }

    @When("I view the participant list")
    public void iViewTheParticipantList() {
    }


    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        List<WebElement> participantElements = Hooks.driver.findElement(By.id("participantsList")).findElements(By.tagName("div"));
        List<Integer> steps = participantElements.stream()
                .map(element -> Integer.parseInt(element.findElement(By.className("participant-steps"))
                .getText().replaceAll("[^0-9]", ""))).toList();
        for (int i = 1; i < steps.size(); i++) {
            assertTrue(steps.get(i) <= steps.get(i - 1));
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantSStepCountIsGreaterThanOrEqualToTheParticipantBelowThem() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("the following participants display trophy icons:")
    public void theFollowingParticipantsDisplayTrophyIcons(List<List<String>> table) {
        List<WebElement> participantElements = Hooks.driver.findElement(By.id("participantsList")).findElements(By.tagName("div"));
        table.removeFirst(); // Remove header
        for (List<String> row : table) {
            int place = Integer.parseInt(row.get(0));
            String color = row.get(1);
            WebElement trophy = participantElements.get(place - 1).findElement(By.className("fa-trophy"));
            String style = trophy.getAttribute("style");
            for (String part : style.split(";")) {
                if (part.trim().startsWith("color:")) {
                    switch (color) {
                        case "gold" -> assertTrue(part.contains("gold"));
                        case "silver" -> assertTrue(part.contains("silver"));
                        case "bronze" -> assertTrue(part.contains("rgb(205, 127, 50)"));
                    }
                }
            }
        }
    }

    @And("participants ranked {int}th and below have no trophy icons")
    public void participantsRankedThAndBelowHaveNoTrophyIcons(int place) {
        List<WebElement> participantElements = Hooks.driver.findElement(By.id("participantsList")).findElements(By.tagName("div"));
        for (int i = place - 1; i < participantElements.size(); i++) {
            List<WebElement> trophies = participantElements.get(i).findElements(By.className("fa-trophy"));
            assertTrue(trophies.isEmpty());
        }
    }
}
