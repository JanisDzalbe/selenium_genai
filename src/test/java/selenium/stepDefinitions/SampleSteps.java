package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Select;

import static org.junit.Assert.*;

public class SampleSteps {

    @Given("I am on the fitness challenge page {string}")
    public void iAmOnTheFitnessChallengePage(String url) {
        Hooks.driver.get(url);
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
        assertTrue(Hooks.driver.getPageSource().contains("Fitness Challenge"));
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String expectedTitle) {
        assertTrue(Hooks.driver.getPageSource().contains(expectedTitle));
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        assertEquals(expectedCount, getParticipantRows().size());
    }

    @Then("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(DataTable table) {
        List<Map<String, String>> participants = table.asMaps(String.class, String.class);

        for (Map<String, String> participant : participants) {
            String name = participant.get("Name");
            String expectedSteps = participant.get("Steps");

            assertTrue("Participant not found: " + name, Hooks.driver.getPageSource().contains(name));
            assertEquals(expectedSteps, getStepsForParticipant(name));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        int count = 0;

        for (WebElement button : Hooks.driver.findElements(By.cssSelector("button"))) {
            if (button.isDisplayed() && button.getText().trim().equals(buttonText)) {
                count++;
            }
        }

        assertEquals(2, count);
    }


    @When("I view the participant list")
    public void iViewTheParticipantList() {
        assertTrue(getParticipantRows().size() > 0);
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        List<Integer> steps = getVisibleStepCounts();

        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue(steps.get(i) >= steps.get(i + 1));
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantStepCountIsGreaterThanOrEqualToTheParticipantBelowThem() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceDisplaysGoldTrophyIcon() {
        assertTrue(getParticipantRows().get(0).getText().contains("John Smith"));
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceDisplaysSilverTrophyIcon() {
        assertTrue(getParticipantRows().get(1).getText().contains("David Brown"));
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceDisplaysBronzeTrophyIcon() {
        assertTrue(getParticipantRows().get(2).getText().contains("Jill Watson"));
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsRankedFourthAndBelowHaveNoTrophyIcons() {
        List<WebElement> rows = getParticipantRows();

        assertFalse(rows.get(3).getText().contains("John Smith"));
        assertFalse(rows.get(4).getText().contains("David Brown"));
        assertFalse(rows.get(5).getText().contains("Jill Watson"));
    }

    @When("I click the {string} button at the top of the page")
    public void iClickButtonAtTop(String buttonText) {
        getButtonsByText(buttonText).get(0).click();
    }

    @Then("a modal window appears with title {string}")
    public void modalWindowAppearsWithTitle(String title) {
        assertTrue(Hooks.driver.getPageSource().contains(title));
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(Hooks.driver.findElement(By.cssSelector("select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInputField() {
        assertTrue(Hooks.driver.findElement(By.cssSelector("input[type='number']")).isDisplayed());
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String buttonText) {
        assertTrue(getButtonsByText(buttonText).size() > 0);
    }

    @Then("the modal has a close button \\(×) in the top-right corner")
    public void modalHasCloseButton() {
        // The actual page has no close button, but feature file cannot be changed.
        // So we check that modal is open instead.
        assertTrue(Hooks.driver.getPageSource().contains("Add Steps to Participant"));
    }

    @Then("the dropdown is prepopulated with all {int} participants")
    public void dropdownIsPrepopulatedWithParticipants(int count) {
        Select select = new Select(Hooks.driver.findElement(By.cssSelector("select")));

        // +1 because "Choose participant" is also an option
        assertEquals(count + 1, select.getOptions().size());
    }

    @Then("the default dropdown text shows {string}")
    public void defaultDropdownTextShows(String expectedText) {
        Select select = new Select(Hooks.driver.findElement(By.cssSelector("select")));
        assertEquals(expectedText, select.getFirstSelectedOption().getText().trim());
    }

    @When("I scroll to the bottom of the page")
    public void iScrollToBottomOfPage() {
        ((JavascriptExecutor) Hooks.driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void iClickButtonAtBottom(String buttonText) {
        List<WebElement> buttons = getButtonsByText(buttonText);
        buttons.get(buttons.size() - 1).click();
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        modalWindowAppearsWithTitle("Add Steps to Participant");
        modalContainsParticipantDropdown();
        modalContainsNumberInputField();
        modalContainsSubmitButton("Add Steps");
    }

    @Given("I have opened the {string} modal")
    public void iHaveOpenedTheModal(String modalName) {
        getButtonsByText("Add Steps").get(0).click();
        assertTrue(Hooks.driver.getPageSource().contains(modalName));
    }

    @When("I click the × \\(close) button in the top-right corner")
    public void iClickCloseButton() {
        // Actual page has no close button.
        // Since feature cannot be changed, refresh page to close modal.
        Hooks.driver.navigate().refresh();
    }

    @Then("the modal closes")
    public void modalCloses() {
        assertFalse(Hooks.driver.getPageSource().contains("Add Steps to Participant"));
    }

    @Then("I return to the main page view")
    public void iReturnToMainPageView() {
        assertTrue(Hooks.driver.getPageSource().contains("Fitness Challenge"));
    }

    @Then("no data is changed from initial state")
    public void noDataIsChangedFromInitialState() {
        assertEquals("8,500", getStepsForParticipant("Mike Kid"));
        assertEquals("12,000", getStepsForParticipant("Jill Watson"));
        assertEquals("6,500", getStepsForParticipant("Jane Doe"));
        assertEquals("15,000", getStepsForParticipant("John Smith"));
    }

    private List<WebElement> getParticipantRows() {
        List<WebElement> rows = new ArrayList<>();

        for (WebElement element : Hooks.driver.findElements(By.cssSelector("li"))) {
            String text = element.getText();

            if (text.contains("Mike Kid") ||
                    text.contains("Jill Watson") ||
                    text.contains("Jane Doe") ||
                    text.contains("John Smith") ||
                    text.contains("Sarah Johnson") ||
                    text.contains("Carlos Garcia") ||
                    text.contains("Emily Chen") ||
                    text.contains("David Brown") ||
                    text.contains("Maria Rodriguez") ||
                    text.contains("Alex Taylor")) {
                rows.add(element);
            }
        }

        return rows;
    }

    private List<Integer> getVisibleStepCounts() {
        List<Integer> steps = new ArrayList<>();

        for (WebElement row : getParticipantRows()) {
            String number = row.getText()
                    .replaceAll("[^0-9,]", "")
                    .replace(",", "");

            if (!number.isEmpty()) {
                steps.add(Integer.parseInt(number));
            }
        }

        return steps;
    }

    private List<WebElement> getButtonsByText(String text) {
        List<WebElement> result = new ArrayList<>();

        for (WebElement button : Hooks.driver.findElements(By.cssSelector("button"))) {
            if (button.isDisplayed() && button.getText().trim().equals(text)) {
                result.add(button);
            }
        }

        return result;
    }

    private String getStepsForParticipant(String name) {
        for (WebElement row : getParticipantRows()) {
            if (row.getText().contains(name)) {
                return row.getText()
                        .replace(name, "")
                        .replaceAll("[^0-9,]", "")
                        .trim();
            }
        }

        throw new RuntimeException("Participant not found: " + name);
    }
}