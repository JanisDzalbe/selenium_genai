package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SampleSteps {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");
    private static final By ADD_STEPS_MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");

    private long notedParticipantSteps;
    private int notedParticipantRank;
    private long selectedParticipantInitialSteps;
    private String selectedParticipantName;
    private long expectedParticipantSteps;

    @Given("I am on the fitness challenge page {string}")
    public void iAmOnTheFitnessChallengePage(String url) {
        driver().get(url);
        waitForParticipants();
        ((JavascriptExecutor) driver()).executeScript("resetFitnessChallenge()");
        waitForParticipants();
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        assertModalContents();
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
        waitForParticipants();
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, driver().findElement(By.cssSelector("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(DataTable participantsTable) {
        List<Map<String, String>> expectedParticipants =
                participantsTable.asMaps(String.class, String.class);
        assertEquals(10, expectedParticipants.size());

        for (Map<String, String> expected : expectedParticipants) {
            String name = expected.get("Name").trim();
            long steps = Long.parseLong(expected.get("Steps").replace(",", "").trim());
            assertEquals(name + " should have the expected step count",
                    steps, stepsForParticipant(name));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String label) {
        String buttonId = switch (label) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported fitness challenge button: " + label);
        };
        List<WebElement> buttons = driver().findElements(By.id(buttonId));
        assertEquals("Expected a " + label + " button at the top and bottom", 2, buttons.size());
        assertTrue("Both " + label + " buttons should be visible",
                buttons.stream().allMatch(WebElement::isDisplayed));
    }

    @When("I view the participant list")
    public void iViewTheParticipantList() {
        waitForParticipants();
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        List<WebElement> rows = participantRows();
        assertTrue("Expected participants to be displayed", !rows.isEmpty());

        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(participantSteps(rows.get(i)) >= participantSteps(rows.get(i + 1)));
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantsStepCountIsGreaterThanOrEqualToParticipantBelowThem() {
        List<WebElement> rows = participantRows();
        assertTrue("Expected participants to be displayed", !rows.isEmpty());

        for (int i = 0; i < rows.size() - 1; i++) {
            long currentSteps = participantSteps(rows.get(i));
            long nextSteps = participantSteps(rows.get(i + 1));
            assertTrue(participantName(rows.get(i)) + " should not rank below "
                    + participantName(rows.get(i + 1)), currentSteps >= nextSteps);
        }
    }

    @Then("the {int}st place participant displays a gold trophy icon")
    public void firstPlaceParticipantDisplaysGoldTrophy(int place) {
        assertTrophyColorAtPlace(place, "rgb(255, 215, 0)");
    }

    @Then("the {int}nd place participant displays a silver trophy icon")
    public void secondPlaceParticipantDisplaysSilverTrophy(int place) {
        assertTrophyColorAtPlace(place, "rgb(192, 192, 192)");
    }

    @Then("the {int}rd place participant displays a bronze trophy icon")
    public void thirdPlaceParticipantDisplaysBronzeTrophy(int place) {
        assertTrophyColorAtPlace(place, "rgb(205, 127, 50)");
    }

    @Then("participants ranked {int}th and below have no trophy icons")
    public void participantsRankedFourthAndBelowHaveNoTrophyIcons(int firstPlaceWithoutTrophy) {
        List<WebElement> rows = participantRows();
        assertTrue("Expected at least " + firstPlaceWithoutTrophy + " participants",
                rows.size() >= firstPlaceWithoutTrophy);

        for (int i = firstPlaceWithoutTrophy - 1; i < rows.size(); i++) {
            assertTrue("Participant ranked " + (i + 1) + " should not have a trophy",
                    rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    @When("I click the {string} button at the top of the page")
    public void iClickTheButtonAtTheTopOfThePage(String buttonText) {
        assertEquals("Add Steps", buttonText);
        driver().findElements(By.id("addStepsBtn")).get(0).click();
        waitForModal();
    }

    @When("I scroll to the bottom of the page")
    public void iScrollToTheBottomOfThePage() {
        ((JavascriptExecutor) driver()).executeScript(
                "window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void iClickTheButtonAtTheBottom(String buttonText) {
        assertEquals("Add Steps", buttonText);
        List<WebElement> buttons = driver().findElements(By.id("addStepsBtn"));
        assertEquals(2, buttons.size());
        ((JavascriptExecutor) driver()).executeScript("arguments[0].scrollIntoView(true)", buttons.get(1));
        buttons.get(1).click();
        waitForModal();
    }

    @Then("a modal window appears with title {string}")
    public void aModalWindowAppearsWithTitle(String title) {
        waitForModal();
        assertEquals(title, driver().findElement(By.cssSelector("#addStepsModal h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void theModalContainsAParticipantDropdown() {
        assertEquals("select", driver().findElement(PARTICIPANT_SELECT).getTagName());
    }

    @Then("the modal contains a number input field")
    public void theModalContainsANumberInputField() {
        assertEquals("number", driver().findElement(STEPS_INPUT).getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void theModalContainsSubmitButton(String label) {
        assertEquals(label, driver().findElement(By.id("modal_add_steps_button"))
                .getAttribute("textContent").trim());
    }

    @Then("^the modal has a close button \\(.\\) in the top-right corner$")
    public void theModalHasCloseButtonInTopRightCorner() {
        WebElement closeButton = driver().findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        assertEquals("×", closeButton.getText());
        assertEquals("Close Modal", closeButton.getAttribute("title"));
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void theDropdownIsPrepopulatedWithAllParticipants() {
        List<WebElement> options = new Select(driver().findElement(PARTICIPANT_SELECT)).getOptions();
        assertEquals(11, options.size());
        for (int i = 1; i < options.size(); i++) {
            assertFalse("The participant option should not be empty", options.get(i).getText().isBlank());
        }
    }

    @Then("the default dropdown text shows {string}")
    public void theDefaultDropdownTextShows(String expectedText) {
        assertEquals(expectedText,
                new Select(driver().findElement(PARTICIPANT_SELECT)).getFirstSelectedOption().getText());
    }

    @Given("I have opened the {string} modal")
    @When("I open the {string} modal")
    public void iOpenTheModal(String modalName) {
        assertEquals("Add Steps", modalName);
        driver().findElement(By.id("addStepsBtn")).click();
        waitForModal();
    }

    @When("^I click the . \\(close\\) button in the top-right corner$")
    public void iClickTheCloseButtonInTheTopRightCorner() {
        WebElement closeButton = driver().findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        ((JavascriptExecutor) driver()).executeScript("arguments[0].click()", closeButton);
    }

    @Then("the modal closes")
    public void theModalCloses() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(ADD_STEPS_MODAL));
    }

    @Then("I return to the main page view")
    public void iReturnToTheMainPageView() {
        assertTrue(driver().findElement(By.id("participantsList")).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void noDataIsChangedFromInitialState() {
        assertEquals(List.of(
                        "John Smith", "David Brown", "Jill Watson", "Carlos Garcia", "Maria Rodriguez",
                        "Sarah Johnson", "Alex Taylor", "Mike Kid", "Emily Chen", "Jane Doe"),
                participantRows().stream().map(this::participantName).toList());
        assertEquals(8_500L, stepsForParticipant("Mike Kid"));
        assertEquals(12_000L, stepsForParticipant("Jill Watson"));
    }

    @Given("I note the current step count for {string}")
    public void iNoteTheCurrentStepCountFor(String name) {
        selectedParticipantName = name;
        notedParticipantSteps = stepsForParticipant(name);
        notedParticipantRank = participantRank(name);
        selectedParticipantInitialSteps = notedParticipantSteps;
    }

    @When("I select {string} from the dropdown")
    public void iSelectFromTheDropdown(String name) {
        selectParticipant(name);
    }

    @When("I select any participant")
    public void iSelectAnyParticipant() {
        selectParticipant("Mike Kid");
    }

    @When("I enter {string} in the steps input field")
    public void iEnterInTheStepsInputField(String value) {
        enterSteps(value);
        if (selectedParticipantName != null && isSelectedParticipant(selectedParticipantName)) {
            selectedParticipantInitialSteps = stepsForParticipant(selectedParticipantName);
        }
        expectedParticipantSteps = selectedParticipantInitialSteps + parseStepInput(value);
    }

    @When("I click {string} button")
    public void iClickButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        clickAddStepsButton();
    }

    @Then("the modal closes automatically")
    public void theModalClosesAutomatically() {
        theModalCloses();
    }

    @Then("the participant list refreshes")
    public void theParticipantListRefreshes() {
        waitForParticipants();
        assertEquals(10, participantRows().size());
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeKidsStepCountIncreasesBy1000() {
        assertEquals(notedParticipantSteps + 1_000, stepsForParticipant("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void theListResortsIfMikeKidsTotalChangesHisRanking() {
        List<WebElement> rows = participantRows();
        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(participantSteps(rows.get(i)) >= participantSteps(rows.get(i + 1)));
        }
        if (stepsForParticipant("Mike Kid") > notedParticipantSteps) {
            assertTrue("Mike Kid should move up when his step count passes another participant",
                    participantRank("Mike Kid") <= notedParticipantRank);
        }
    }

    @Then("an alert appears: {string}")
    public void anAlertAppears(String expectedText) {
        Alert alert = new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
    }

    @When("I accept the alert")
    public void iAcceptTheAlert() {
        driver().switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void theModalRemainsOpen() {
        assertTrue(driver().findElement(ADD_STEPS_MODAL).isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void theStepCountUpdatesCorrectlyWithTheLargeNumber() {
        assertEquals(expectedParticipantSteps, stepsForParticipant(selectedParticipantName));
    }

    @Then("the participant moves to first place")
    public void theParticipantMovesToFirstPlace() {
        assertEquals(selectedParticipantName, participantName(participantRows().get(0)));
    }

    private WebDriver driver() {
        if (Hooks.driver == null) {
            throw new IllegalStateException("WebDriver has not been initialized by the Cucumber hook");
        }
        return Hooks.driver;
    }

    private void waitForParticipants() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.numberOfElementsToBe(PARTICIPANT_ROWS, 10));
    }

    private List<WebElement> participantRows() {
        return driver().findElements(PARTICIPANT_ROWS);
    }

    private long stepsForParticipant(String name) {
        for (WebElement row : participantRows()) {
            if (name.equals(participantName(row))) {
                return participantSteps(row);
            }
        }
        throw new AssertionError("Participant was not found: " + name);
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText();
    }

    private long participantSteps(WebElement row) {
        String formattedSteps = row.findElement(By.cssSelector(".participant-steps")).getText();
        return Long.parseLong(formattedSteps.replaceAll("[^0-9]", ""));
    }

    private void assertTrophyColorAtPlace(int place, String expectedColor) {
        List<WebElement> rows = participantRows();
        assertTrue("Expected a participant in place " + place, rows.size() >= place);

        WebElement trophy = rows.get(place - 1).findElement(By.cssSelector("i.fa-trophy"));
        assertEquals(expectedColor, Color.fromString(trophy.getCssValue("color")).asRgb());
    }

    private void waitForModal() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(ADD_STEPS_MODAL));
    }

    private void assertModalContents() {
        WebElement modal = driver().findElement(ADD_STEPS_MODAL);
        assertTrue(modal.isDisplayed());
        assertEquals("Add Steps to Participant", modal.findElement(By.cssSelector("h3")).getText());
        assertEquals("select", driver().findElement(PARTICIPANT_SELECT).getTagName());
        assertEquals("number", driver().findElement(STEPS_INPUT).getAttribute("type"));
        assertEquals("Add Steps", driver().findElement(By.id("modal_add_steps_button"))
                .getAttribute("textContent").trim());

        WebElement closeButton = modal.findElement(By.cssSelector(".w3-closebtn"));
        assertEquals("×", closeButton.getText());
        assertEquals("Close Modal", closeButton.getAttribute("title"));

        List<WebElement> options = new Select(driver().findElement(PARTICIPANT_SELECT)).getOptions();
        assertEquals(11, options.size());
        assertEquals("Choose participant", options.get(0).getText());
        for (int i = 1; i < options.size(); i++) {
            assertTrue("Participant dropdown options should have names",
                    !options.get(i).getText().isBlank());
        }
    }

    private void selectParticipant(String name) {
        new Select(driver().findElement(PARTICIPANT_SELECT)).selectByVisibleText(name);
        selectedParticipantName = name;
        selectedParticipantInitialSteps = stepsForParticipant(name);
    }

    private boolean isSelectedParticipant(String name) {
        return name.equals(new Select(driver().findElement(PARTICIPANT_SELECT))
                .getFirstSelectedOption().getText());
    }

    private void enterSteps(String value) {
        WebElement input = driver().findElement(STEPS_INPUT);
        input.clear();
        input.sendKeys(value);
    }

    private void clickAddStepsButton() {
        driver().findElement(By.id("modal_add_steps_button")).click();
    }

    private int participantRank(String name) {
        List<WebElement> rows = participantRows();
        for (int i = 0; i < rows.size(); i++) {
            if (name.equals(participantName(rows.get(i)))) {
                return i + 1;
            }
        }
        throw new AssertionError("Participant was not found: " + name);
    }

    private long parseStepInput(String value) {
        return Long.parseLong(value.replace(",", ""));
    }
}
