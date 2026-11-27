package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FitnessSteps {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final Map<String, Long> DEFAULT_PARTICIPANTS = Map.of(
            "Mike Kid", 8_500L,
            "Jill Watson", 12_000L,
            "Jane Doe", 6_500L,
            "John Smith", 15_000L,
            "Sarah Johnson", 9_800L,
            "Carlos Garcia", 11_200L,
            "Emily Chen", 7_300L,
            "David Brown", 13_500L,
            "Maria Rodriguez", 10_500L,
            "Alex Taylor", 8_900L
    );

    private long mikeStepsBeforeAdding;
    private long selectedStepsBeforeAdding;
    private long stepsToAdd;
    private String selectedParticipant;
    private WebElement firstParticipantBeforeSubmit;

    private WebDriver driver() {
        assertTrue(Hooks.driver != null, "ChromeDriver was not initialized by the Cucumber hook");
        return Hooks.driver;
    }

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallenge(String url) {
        resetScenarioState();
        driver().get(url);
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(d -> d.findElements(By.cssSelector("#participantsList li")).size() == 10);
    }

    @When("the page loads for the first time")
    public void waitForInitialPageLoad() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h2")));
        assertDefaultParticipants(DEFAULT_PARTICIPANTS);
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String title) {
        assertEquals(title, driver().findElement(By.tagName("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayed(int expectedCount) {
        assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable participants) {
        Map<String, Long> expected = new HashMap<>();
        for (Map<String, String> row : participants.asMaps()) {
            expected.put(row.get("Name"), parseSteps(row.get("Steps")));
        }
        assertDefaultParticipants(expected);
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String label) {
        String id = switch (label) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new IllegalArgumentException("Unsupported button: " + label);
        };
        List<WebElement> buttons = driver().findElements(By.id(id));
        assertEquals(2, buttons.size());
        assertTrue(buttons.stream().allMatch(WebElement::isDisplayed),
                label + " buttons should be visible at the top and bottom");
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(d -> participantRows().size() == DEFAULT_PARTICIPANTS.size());
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreRankedBySteps() {
        List<Long> steps = participantRows().stream()
                .map(row -> participantSteps(row))
                .toList();
        assertEquals(DEFAULT_PARTICIPANTS.size(), steps.size());
        for (int i = 1; i < steps.size(); i++) {
            assertTrue(steps.get(i - 1) >= steps.get(i),
                    "Participant ranking is not descending at position " + i);
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantHasAtLeastAsManyStepsAsNext() {
        participantsAreRankedBySteps();
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceHasGoldTrophy() {
        assertTrophyColor(0, "255, 215, 0");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceHasSilverTrophy() {
        assertTrophyColor(1, "192, 192, 192");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceHasBronzeTrophy() {
        assertTrophyColor(2, "205, 127, 50");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsBelowThirdHaveNoTrophies() {
        List<WebElement> rows = participantRows();
        assertEquals(DEFAULT_PARTICIPANTS.size(), rows.size());
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty(),
                    "Participant at position " + (i + 1) + " should not have a trophy");
        }
    }

    @When("I click the {string} button at the top of the page")
    public void clickTopButton(String label) {
        assertEquals("Add Steps", label);
        openAddStepsModal(0);
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottom() {
        WebElement bottomButton = driver().findElements(By.id("addStepsBtn")).get(1);
        scrollIntoView(bottomButton);
    }

    @When("I click the {string} button at the bottom")
    public void clickBottomButton(String label) {
        assertEquals("Add Steps", label);
        openAddStepsModal(1);
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        assertModalOpen();
        assertEquals(title, driver().findElement(By.cssSelector("#addStepsModal h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertEquals("select", driver().findElement(By.id("participant_select")).getTagName());
        assertTrue(driver().findElement(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInput() {
        WebElement input = driver().findElement(By.id("steps_input"));
        assertEquals("number", input.getAttribute("type"));
        assertTrue(input.isDisplayed());
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String label) {
        WebElement button = driver().findElement(By.id("modal_add_steps_button"));
        assertEquals(label, button.getText());
        assertTrue(button.isEnabled());
    }

    @Then("the modal has a close button \\(×\\) in the top-right corner")
    public void modalHasTopRightCloseButton() {
        WebElement closeButton = driver().findElement(
                By.cssSelector("#addStepsModal .w3-closebtn"));
        assertEquals("×", closeButton.getText());
        assertTrue(closeButton.getAttribute("class").contains("w3-display-topright"));
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownContainsAllParticipants() {
        List<String> options = new Select(driver().findElement(By.id("participant_select")))
                .getOptions().stream()
                .map(WebElement::getText)
                .toList();
        assertEquals(11, options.size());
        assertEquals(DEFAULT_PARTICIPANTS.keySet(), Set.copyOf(options.subList(1, options.size())));
    }

    @Then("the default dropdown text shows {string}")
    public void dropdownShowsDefaultText(String text) {
        assertEquals(text, new Select(driver().findElement(By.id("participant_select")))
                .getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        modalContainsParticipantDropdown();
        modalContainsNumberInput();
        modalContainsSubmitButton("Add Steps");
        modalHasTopRightCloseButton();
        dropdownContainsAllParticipants();
        dropdownShowsDefaultText("Choose participant");
    }

    @Given("I have opened the {string} modal")
    public void openNamedModal(String name) {
        assertEquals("Add Steps", name);
        openAddStepsModal(0);
    }

    @When("I click the × \\(close\\) button in the top-right corner")
    public void clickModalCloseButton() {
        driver().findElement(By.cssSelector("#addStepsModal .w3-closebtn")).click();
    }

    @Then("the modal closes")
    public void modalCloses() {
        waitForModalToClose();
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(driver().findElement(By.tagName("h2")).isDisplayed());
        assertTrue(driver().findElement(By.id("participantsList")).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        assertDefaultParticipants(DEFAULT_PARTICIPANTS);
    }

    @Given("I note the current step count for {string}")
    public void noteParticipantSteps(String participant) {
        assertEquals("Mike Kid", participant);
        mikeStepsBeforeAdding = displayedSteps(participant);
        selectedParticipant = participant;
        selectedStepsBeforeAdding = mikeStepsBeforeAdding;
    }

    @When("I open the {string} modal")
    public void openAddStepsModal(String name) {
        assertEquals("Add Steps", name);
        openAddStepsModal(0);
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String participant) {
        selectedParticipant = participant;
        selectedStepsBeforeAdding = displayedSteps(participant);
        new Select(driver().findElement(By.id("participant_select")))
                .selectByVisibleText(participant);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        selectParticipant("Mike Kid");
    }

    @When("I enter {string} in the steps input field")
    public void enterStepsInField(String steps) {
        stepsToAdd = Long.parseLong(steps);
        WebElement input = driver().findElement(By.id("steps_input"));
        scrollIntoView(input);
        input.sendKeys(steps);
    }

    @When("I click {string} button")
    public void clickModalSubmitButton(String label) {
        assertEquals("Add Steps", label);
        firstParticipantBeforeSubmit = participantRows().get(0);
        WebElement button = driver().findElement(By.id("modal_add_steps_button"));
        scrollIntoView(button);
        button.click();
    }

    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        waitForModalToClose();
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        assertTrue(firstParticipantBeforeSubmit != null,
                "Submit step did not capture the original participant list");
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.stalenessOf(firstParticipantBeforeSubmit));
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeStepsIncreaseByOneThousand() {
        assertEquals(mikeStepsBeforeAdding + 1_000L, displayedSteps("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listResortsAfterAddingSteps() {
        participantsAreRankedBySteps();
        List<String> names = participantRows().stream()
                .map(row -> row.findElement(By.className("participant-name")).getText())
                .toList();
        assertTrue(names.indexOf("Mike Kid") < 7,
                "Mike Kid should move up after adding 1,000 steps");
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String message) {
        Alert alert = new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.alertIsPresent());
        assertEquals(message, alert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        driver().switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertModalOpen();
    }

    @Then("the step count updates correctly with the large number")
    public void largeStepCountUpdatesCorrectly() {
        assertTrue(selectedParticipant != null, "No participant was selected");
        assertEquals(selectedStepsBeforeAdding + stepsToAdd,
                displayedSteps(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertEquals(selectedParticipant,
                participantRows().get(0).findElement(By.className("participant-name")).getText());
    }

    private void resetScenarioState() {
        mikeStepsBeforeAdding = 0;
        selectedStepsBeforeAdding = 0;
        stepsToAdd = 0;
        selectedParticipant = null;
        firstParticipantBeforeSubmit = null;
    }

    private void openAddStepsModal(int buttonIndex) {
        driver().findElements(By.id("addStepsBtn")).get(buttonIndex).click();
        assertModalOpen();
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(d -> {
            String transform = d.findElement(
                    By.cssSelector("#addStepsModal .w3-modal-content")).getCssValue("transform");
            return "none".equals(transform) || transform.startsWith("matrix(1, 0, 0, 1,");
        });
    }

    private void assertModalOpen() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void waitForModalToClose() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void assertDefaultParticipants(Map<String, Long> expected) {
        assertEquals(expected, displayedParticipants());
    }

    private void assertTrophyColor(int participantIndex, String channels) {
        WebElement icon = participantRows().get(participantIndex)
                .findElement(By.cssSelector("i.fa-trophy"));
        String color = icon.getCssValue("color");
        assertTrue(color.equals("rgb(" + channels + ")")
                        || color.equals("rgba(" + channels + ", 1)"),
                "Unexpected trophy color: " + color);
    }

    private List<WebElement> participantRows() {
        return driver().findElements(By.cssSelector("#participantsList li"));
    }

    private Map<String, Long> displayedParticipants() {
        Map<String, Long> participants = new HashMap<>();
        for (WebElement row : participantRows()) {
            String name = row.findElement(By.className("participant-name")).getText();
            participants.put(name, participantSteps(row));
        }
        return participants;
    }

    private long displayedSteps(String participant) {
        for (WebElement row : participantRows()) {
            if (participant.equals(
                    row.findElement(By.className("participant-name")).getText())) {
                return participantSteps(row);
            }
        }
        throw new AssertionError("Participant was not displayed: " + participant);
    }

    private long participantSteps(WebElement row) {
        return parseSteps(row.findElement(By.className("participant-steps")).getText());
    }

    private long parseSteps(String text) {
        return Long.parseLong(text.replaceAll("[^0-9]", ""));
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
    }
}
