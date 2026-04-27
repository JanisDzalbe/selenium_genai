package selenium.stepDefinitions;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class SampleSteps {

    private WebDriver driver;
    private List<WebElement> participants;

    public SampleSteps() {
        this.driver = Hooks.driver;
    }

    // ---------- GIVEN ----------
    @Given("^I am on the fitness challenge page \"([^\"]*)\"$")
    public void challengePage(String url){
        driver.get(url);
    }

    // ---------- WHEN ----------
    @When("the page loads for the first time")
    public void pageLoads() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
    }

    // ---------- THEN ----------
    @Then("the page displays title {string}")
    public void checkTitle(String expectedTitle) {
        String actual = driver.findElement(By.tagName("h2")).getText();
        Assertions.assertEquals(expectedTitle, actual);
    }

    @Then("{int} participants are displayed in the list")
    public void checkParticipantsCount(int expectedCount) {
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        Assertions.assertEquals(expectedCount, participants.size());
    }

    @Then("the default participants are displayed:")
    public void checkParticipants(io.cucumber.datatable.DataTable table) {


        List<Map<String, String>> expected = table.asMaps();


        List<WebElement> rows = driver.findElements(By.cssSelector("#participantsList li"));

        List<String> actualData = rows.stream()
                .map(el -> {
                    String name = el.findElement(By.className("participant-name")).getText();
                    String steps = el.findElement(By.className("participant-steps"))
                            .getText()
                            .replace(" steps", "")
                            .trim();
                    return name + "|" + steps;
                })
                .collect(Collectors.toList());


        for (Map<String, String> row : expected) {
            String expectedEntry = row.get("Name") + "|" + row.get("Steps");
            Assertions.assertTrue(
                    actualData.contains(expectedEntry),
                    "Missing: " + expectedEntry
            );
        }
    }

    @Then("\"Add Steps\" buttons are visible at top and bottom")
    public void checkAddStepsButtons() {
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        Assertions.assertEquals(2, buttons.size());
        buttons.forEach(btn -> Assertions.assertTrue(btn.isDisplayed()));
    }

    @Then("\"Reset List\" buttons are visible at top and bottom")
    public void checkResetButtons() {
        List<WebElement> buttons = driver.findElements(By.id("resetBtn"));
        Assertions.assertEquals(2, buttons.size());
        buttons.forEach(btn -> Assertions.assertTrue(btn.isDisplayed()));
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));

        participants = driver.findElements(By.cssSelector("#participantsList li"));
    }

    // ---------- THEN (SORTING) ----------
    @Then("participants are displayed in descending order by step count")
    public void checkDescendingOrder() {

        List<Integer> steps = participants.stream()
                .map(el -> el.findElement(By.className("participant-steps")).getText())
                .map(text -> text.replace(" steps", "").replace(",", "").trim())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Integer> sorted = new ArrayList<>(steps);
        sorted.sort(Comparator.reverseOrder());

        Assertions.assertEquals(sorted, steps, "List is not sorted in descending order");
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void checkEachGreaterThanNext() {

        List<Integer> steps = participants.stream()
                .map(el -> el.findElement(By.className("participant-steps")).getText())
                .map(text -> text.replace(" steps", "").replace(",", "").trim())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        for (int i = 0; i < steps.size() - 1; i++) {
            Assertions.assertTrue(
                    steps.get(i) >= steps.get(i + 1),
                    "Order broken at index " + i
            );
        }
    }

    // ---------- THEN (TROPHIES) ----------
    @Then("the 1st place participant displays a gold trophy icon")
    public void checkGoldTrophy() {
        checkTrophyColor(0, "255, 215, 0");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void checkSilverTrophy() {
        checkTrophyColor(1, "192, 192, 192");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void checkBronzeTrophy() {
        checkTrophyColor(2, "205, 127, 50"); // бронза в html
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void checkNoTrophiesBelow() {

        for (int i = 3; i < participants.size(); i++) {
            List<WebElement> icons = participants.get(i)
                    .findElements(By.cssSelector("i.fa-trophy"));

            Assertions.assertTrue(icons.isEmpty(), "Participant at index " + i + " has a trophy");
        }
    }

    // ---------- HELPER ----------
    private void checkTrophyColor(int index, String expectedColor) {

        WebElement participant = participants.get(index);
        WebElement trophy = participant.findElement(By.cssSelector("i.fa-trophy"));

        String color = trophy.getCssValue("color"); // ← ВАЖНО

        System.out.println("Index " + index + " color: " + color); // для дебага

        Assertions.assertTrue(
                color.contains(expectedColor),
                "Expected " + expectedColor + " but got " + color
        );
    }
}