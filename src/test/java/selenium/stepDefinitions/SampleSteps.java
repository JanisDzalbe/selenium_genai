package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import selenium.stepDefinitions.Hooks;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SampleSteps {

    @Given("I am on the fitness challenge page {string}")
    public void iAmOnTheFitnessChallengePage(String url) {
        Hooks.driver.navigate().to(url);
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
        // Wait for participants to load
        WebDriverWait wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#participantsList li"), 10));
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String title) {
        assertEquals(title, Hooks.driver.getTitle());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int count) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(count, participants.size());
    }

    @Then("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(DataTable dataTable) {
        List<Map<String, String>> rows = new ArrayList<>(dataTable.asMaps(String.class, String.class));
        // Sort rows by steps descending
        rows.sort(Comparator.comparingInt((Map<String, String> row) -> Integer.parseInt(row.get("Steps").replace(",", ""))).reversed());

        List<WebElement> names = Hooks.driver.findElements(By.cssSelector(".participant-name"));
        List<WebElement> steps = Hooks.driver.findElements(By.cssSelector(".participant-steps"));

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            assertEquals(row.get("Name"), names.get(i).getText());
            assertEquals(row.get("Steps") + " steps", steps.get(i).getText());
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        String id = buttonText.equals("Add Steps") ? "addStepsBtn" : "resetBtn";
        List<WebElement> buttons = Hooks.driver.findElements(By.id(id));
        assertEquals(2, buttons.size());
        for (WebElement btn : buttons) {
            assertTrue(btn.isDisplayed());
        }
    }

    @When("I view the participant list")
    public void iViewTheParticipantList() {
        // Wait for participants to load
        WebDriverWait wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#participantsList li"), 10));
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        // Get the step counts
        List<WebElement> stepsElements = Hooks.driver.findElements(By.cssSelector(".participant-steps"));
        List<Integer> stepCounts = stepsElements.stream()
            .map(element -> element.getText().replace(" steps", "").replace(",", ""))
            .map(Integer::parseInt)
            .toList();

        // Verify participants are displayed in descending order by step count
        for (int i = 0; i < stepCounts.size() - 1; i++) {
            assertTrue(stepCounts.get(i) >= stepCounts.get(i + 1),
                "Step count at position " + i + " (" + stepCounts.get(i) + ") is not >= next (" + stepCounts.get(i + 1) + ")");
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantSStepCountIsGreaterThanOrEqualToTheParticipantBelowThem() {
        // This is the same as above, so perhaps combine or just call the same logic
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("the {int}st place participant displays a gold trophy icon")
    public void theStPlaceParticipantDisplaysAGoldTrophyIcon(int place) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        WebElement goldIcon = participants.get(place - 1).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(255, 215, 0, 1)", goldIcon.getCssValue("color"));
    }

    @Then("the {int}nd place participant displays a silver trophy icon")
    public void theNdPlaceParticipantDisplaysASilverTrophyIcon(int place) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        WebElement silverIcon = participants.get(place - 1).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(192, 192, 192, 1)", silverIcon.getCssValue("color"));
    }

    @Then("the {int}rd place participant displays a bronze trophy icon")
    public void theRdPlaceParticipantDisplaysABronzeTrophyIcon(int place) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        WebElement bronzeIcon = participants.get(place - 1).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(205, 127, 50, 1)", bronzeIcon.getCssValue("color"));
    }

    @Then("participants ranked {int}th and below have no trophy icons")
    public void participantsRankedThAndBelowHaveNoTrophyIcons(int rank) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        for (int i = rank - 1; i < participants.size(); i++) {
            List<WebElement> icons = participants.get(i).findElements(By.cssSelector("i.fa.fa-trophy"));
            assertEquals(0, icons.size(), "Participant at position " + i + " should not have a trophy icon");
        }
    }

    @When("I click the {string} button at the top of the page")
    public void iClickTheButtonAtTheTopOfThePage(String buttonText) {
        List<WebElement> buttons = Hooks.driver.findElements(By.id(buttonText.equals("Add Steps") ? "addStepsBtn" : "resetBtn"));
        buttons.get(0).click(); // top button
    }

    @Then("a modal window appears with title {string}")
    public void aModalWindowAppearsWithTitle(String title) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());
        WebElement modalTitle = modal.findElement(By.tagName("h3"));
        assertEquals(title, modalTitle.getText());
    }

    @Then("the modal contains a participant dropdown")
    public void theModalContainsAParticipantDropdown() {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        assertTrue(dropdown.isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void theModalContainsANumberInputField() {
        WebElement input = Hooks.driver.findElement(By.id("steps_input"));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void theModalContainsSubmitButton(String buttonText) {
        WebElement button = Hooks.driver.findElement(By.id("modal_add_steps_button"));
        assertTrue(button.isDisplayed());
        assertEquals(buttonText, button.getText());
    }

    @Then("the modal has a close button (×) in the top-right corner")
    public void theModalHasACloseButtonInTheTopRightCorner() {
        WebElement closeButton = Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        assertTrue(closeButton.isDisplayed());
    }

    @Then("the dropdown is prepopulated with all {int} participants")
    public void theDropdownIsPrepopulatedWithAllParticipants(int count) {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        assertEquals(count + 1, options.size()); // +1 for default
    }

    @Then("the default dropdown text shows {string}")
    public void theDefaultDropdownTextShows(String text) {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        WebElement firstOption = dropdown.findElement(By.tagName("option"));
        assertEquals(text, firstOption.getText());
    }

    @When("I scroll to the bottom of the page")
    public void iScrollToTheBottomOfThePage() {
        // Scroll to bottom
        ((JavascriptExecutor) Hooks.driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @When("I click the {string} button at the bottom")
    public void iClickTheButtonAtTheBottom(String buttonText) {
        List<WebElement> buttons = Hooks.driver.findElements(By.id(buttonText.equals("Add Steps") ? "addStepsBtn" : "resetBtn"));
        buttons.get(1).click(); // bottom button
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        // Check title
        aModalWindowAppearsWithTitle("Add Steps to Participant");
        // Check dropdown
        theModalContainsAParticipantDropdown();
        // Check input
        theModalContainsANumberInputField();
        // Check button
        theModalContainsSubmitButton("Add Steps");
        // Check close
        theModalHasACloseButtonInTheTopRightCorner();
        // Check dropdown populated
        theDropdownIsPrepopulatedWithAllParticipants(10);
        // Check default text
        theDefaultDropdownTextShows("Choose participant");
    }

    @Given("I have opened the {string} modal")
    public void iHaveOpenedTheModal(String modalName) {
        // Click top button to open
        iClickTheButtonAtTheTopOfThePage("Add Steps");
    }

    @When("I click the .* \\(close\\) button in the top-right corner")
    public void iClickTheCloseButtonInTheTopRightCorner() {
        WebElement closeButton = Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        closeButton.click();
    }

    @Then("the modal closes")
    public void theModalCloses() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        assertFalse(modal.isDisplayed());
    }

    @Then("I return to the main page view")
    public void iReturnToTheMainPageView() {
        // Check participants list is visible
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(10, participants.size());
    }

    @Then("no data is changed from initial state")
    public void noDataIsChangedFromInitialState() {
        // Check the list is in initial state
        List<WebElement> names = Hooks.driver.findElements(By.cssSelector(".participant-name"));
        List<String> expectedNames = List.of("John Smith", "David Brown", "Jill Watson", "Carlos Garcia", "Maria Rodriguez", "Sarah Johnson", "Alex Taylor", "Mike Kid", "Emily Chen", "Jane Doe");
        for (int i = 0; i < names.size(); i++) {
            assertEquals(expectedNames.get(i), names.get(i).getText());
        }
        List<WebElement> steps = Hooks.driver.findElements(By.cssSelector(".participant-steps"));
        List<String> expectedSteps = List.of("15,000 steps", "13,500 steps", "12,000 steps", "11,200 steps", "10,500 steps", "9,800 steps", "8,900 steps", "8,500 steps", "7,300 steps", "6,500 steps");
        for (int i = 0; i < steps.size(); i++) {
            assertEquals(expectedSteps.get(i), steps.get(i).getText());
        }
    }
}
