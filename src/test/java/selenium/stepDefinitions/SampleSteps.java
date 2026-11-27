package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SampleSteps {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");

    private Map<String, Long> initialParticipantSteps;
    private String notedParticipant;
    private long notedParticipantSteps;
    private String selectedParticipant;
    private long selectedParticipantSteps;
    private WebElement participantRowBeforeSubmit;
    private boolean participantListRefreshed;

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        driver().get(url);
        waitForParticipantRows();
        initialParticipantSteps = participantStepsByName();
        notedParticipant = null;
        selectedParticipant = null;
        participantRowBeforeSubmit = null;
        participantListRefreshed = false;
    }

    @When("the page loads for the first time")
    public void waitForFirstPageLoad() {
        waitForParticipantRows();
    }

    @Then("the page displays title {string}")
    public void pageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, waitFor(By.cssSelector("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayed(int expectedCount) {
        assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void defaultParticipantsAreDisplayed(DataTable participantsTable) {
        Map<String, Long> expectedParticipants = participantsTable.asMaps(String.class, String.class).stream()
                .collect(Collectors.toMap(
                        row -> row.get("Name").trim(),
                        row -> parseSteps(row.get("Steps")),
                        (first, duplicate) -> {
                            throw new AssertionError("Duplicate participant in feature table");
                        },
                        LinkedHashMap::new));

        assertEquals(expectedParticipants, participantStepsByName());
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String label) {
        String id = switch (label) {
            case "Add Steps" -> "addStepsBtn";
            case "Reset List" -> "resetBtn";
            default -> throw new AssertionError("Unsupported fitness challenge button: " + label);
        };
        List<WebElement> buttons = driver().findElements(By.id(id));
        assertEquals(2, buttons.size(), "Expected one " + label + " button at the top and one at the bottom");
        assertTrue(buttons.get(0).isDisplayed());
        assertTrue(buttons.get(1).isDisplayed());
        assertEquals(label, buttons.get(0).getText().trim());
        assertEquals(label, buttons.get(1).getText().trim());

        List<WebElement> rows = participantRows();
        int listTop = rows.get(0).getLocation().getY();
        int listBottom = rows.get(rows.size() - 1).getLocation().getY();
        assertTrue(buttons.get(0).getLocation().getY() < listTop,
                "The first " + label + " button should be above the participants list");
        assertTrue(buttons.get(1).getLocation().getY() > listBottom,
                "The second " + label + " button should be below the participants list");
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        waitForParticipantRows();
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreSortedDescending() {
        List<Long> counts = participantRows().stream().map(SampleSteps::stepsFromRow).toList();
        for (int index = 1; index < counts.size(); index++) {
            assertTrue(counts.get(index - 1) >= counts.get(index),
                    "Step counts should be descending at ranks " + index + " and " + (index + 1));
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantHasAtLeastAsManyStepsAsNext() {
        List<Long> counts = participantRows().stream().map(SampleSteps::stepsFromRow).toList();
        assertTrue(counts.size() > 1, "The participant list needs at least two rows to compare");
        for (int index = 0; index < counts.size() - 1; index++) {
            assertTrue(counts.get(index) >= counts.get(index + 1),
                    "Rank " + (index + 1) + " has fewer steps than rank " + (index + 2));
        }
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceHasGoldTrophy() {
        assertTrophyColorAtRank(1, "rgba(255, 215, 0, 1)");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceHasSilverTrophy() {
        assertTrophyColorAtRank(2, "rgba(192, 192, 192, 1)");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceHasBronzeTrophy() {
        assertTrophyColorAtRank(3, "rgba(205, 127, 50, 1)");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsBelowThirdHaveNoTrophies() {
        List<WebElement> rows = participantRows();
        assertTrue(rows.size() >= 4, "Expected at least four participants");
        for (int index = 3; index < rows.size(); index++) {
            assertTrue(rows.get(index).findElements(By.cssSelector(".fa-trophy")).isEmpty(),
                    "Unexpected trophy icon at rank " + (index + 1));
        }
    }

    @When("I click the {string} button at the top of the page")
    public void clickButtonAtTopOfPage(String label) {
        assertEquals("Add Steps", label);
        driver().findElements(By.id("addStepsBtn")).get(0).click();
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) driver()).executeScript(
                "window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void clickButtonAtBottom(String label) {
        assertEquals("Add Steps", label);
        List<WebElement> buttons = driver().findElements(By.id("addStepsBtn"));
        WebElement bottomButton = buttons.get(buttons.size() - 1);
        ((JavascriptExecutor) driver()).executeScript(
                "arguments[0].scrollIntoView({block: 'center'})", bottomButton);
        bottomButton.click();
    }

    @Then("a modal window appears with title {string}")
    public void modalAppearsWithTitle(String title) {
        WebElement modal = waitFor(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertEquals(title, modal.findElement(By.tagName("h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void modalContainsParticipantDropdown() {
        assertTrue(waitFor(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void modalContainsNumberInput() {
        WebElement stepsInput = waitFor(By.id("steps_input"));
        assertTrue(stepsInput.isDisplayed());
        assertEquals("number", stepsInput.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void modalContainsSubmitButton(String label) {
        WebElement submitButton = waitFor(By.id("modal_add_steps_button"));
        assertTrue(submitButton.isDisplayed());
        assertEquals(label, submitButton.getText().trim());
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void modalHasTopRightCloseButton() {
        WebElement closeButton = waitFor(By.cssSelector("#addStepsModal .w3-closebtn"));
        assertTrue(closeButton.isDisplayed());
        assertEquals("×", closeButton.getText().trim());
        assertEquals("Close Modal", closeButton.getAttribute("title"));
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void dropdownIsPrepopulatedWithTenParticipants() {
        List<String> options = participantDropdown().getOptions().stream()
                .skip(1)
                .map(WebElement::getText)
                .toList();
        assertEquals(10, options.size());
        assertEquals(initialParticipantSteps.keySet(), options.stream().collect(Collectors.toSet()));
    }

    @Then("the default dropdown text shows {string}")
    public void defaultDropdownTextShows(String expectedText) {
        assertEquals(expectedText, participantDropdown().getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        modalAppearsWithTitle("Add Steps to Participant");
        modalContainsParticipantDropdown();
        modalContainsNumberInput();
        modalContainsSubmitButton("Add Steps");
        modalHasTopRightCloseButton();
        dropdownIsPrepopulatedWithTenParticipants();
        defaultDropdownTextShows("Choose participant");
    }

    @Given("I have opened the {string} modal")
    public void openNamedModal(String name) {
        assertEquals("Add Steps", name);
        driver().findElements(By.id("addStepsBtn")).get(0).click();
        waitFor(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void clickModalCloseButton() {
        By closeButton = By.cssSelector("#addStepsModal .w3-closebtn");
        waitFor(webDriver -> {
            WebElement element = webDriver.findElement(closeButton);
            return element.isDisplayed()
                    && element.getRect().getWidth() > 0
                    && element.getRect().getHeight() > 0 ? element : null;
        }).click();
    }

    @Then("the modal closes")
    public void modalCloses() {
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Then("I return to the main page view")
    public void returnToMainPageView() {
        assertTrue(waitFor(By.id("participantsList")).isDisplayed());
        assertEquals("Fitness Challenge", waitFor(By.cssSelector("h2")).getText());
    }

    @Then("no data is changed from initial state")
    public void noDataChangedFromInitialState() {
        assertEquals(initialParticipantSteps, participantStepsByName());
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String name) {
        notedParticipant = name;
        notedParticipantSteps = stepsFor(name);
    }

    @When("I open the {string} modal")
    public void openAddStepsModal(String name) {
        assertEquals("Add Steps", name);
        driver().findElements(By.id("addStepsBtn")).get(0).click();
        waitFor(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    @When("I select {string} from the dropdown")
    public void selectParticipantFromDropdown(String name) {
        selectedParticipant = name;
        selectedParticipantSteps = stepsFor(name);
        participantDropdown().selectByVisibleText(name);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select dropdown = participantDropdown();
        dropdown.selectByIndex(1);
        selectedParticipant = dropdown.getFirstSelectedOption().getText();
        selectedParticipantSteps = stepsFor(selectedParticipant);
    }

    @When("I enter {string} in the steps input field")
    public void enterStepsInInput(String steps) {
        WebElement input = driver().findElement(By.id("steps_input"));
        input.sendKeys(steps);
    }

    @When("I click {string} button")
    public void clickNamedButton(String label) {
        assertEquals("Add Steps", label);
        if (selectedParticipant != null) {
            participantRowBeforeSubmit = participantRow(selectedParticipant);
        }
        driver().findElement(By.id("modal_add_steps_button")).click();
    }

    @Then("the modal closes automatically")
    public void modalClosesAutomatically() {
        waitUntil(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Then("the participant list refreshes")
    public void participantListRefreshes() {
        if (participantRowBeforeSubmit != null) {
            waitUntil(ExpectedConditions.stalenessOf(participantRowBeforeSubmit));
            participantListRefreshed = true;
        } else {
            waitForParticipantRows();
            participantListRefreshed = true;
        }
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeKidStepCountIncreasesByOneThousand() {
        assertEquals("Mike Kid", notedParticipant);
        assertEquals(notedParticipantSteps + 1_000, stepsFor("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void listResortsWhenMikesRankChanges() {
        assertTrue(participantListRefreshed, "The participant list should refresh after adding steps");
        List<Long> counts = participantRows().stream().map(SampleSteps::stepsFromRow).toList();
        for (int index = 1; index < counts.size(); index++) {
            assertTrue(counts.get(index - 1) >= counts.get(index),
                    "The list should remain sorted by descending step count");
        }
    }

    @Then("an alert appears: {string}")
    public void alertAppears(String expectedMessage) {
        assertEquals(expectedMessage, waitFor(ExpectedConditions.alertIsPresent()).getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        waitFor(ExpectedConditions.alertIsPresent()).accept();
    }

    @Then("the modal remains open")
    public void modalRemainsOpen() {
        assertTrue(waitFor(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))).isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void largeStepCountUpdatesCorrectly() {
        if (participantRowBeforeSubmit != null && !participantListRefreshed) {
            waitUntil(ExpectedConditions.stalenessOf(participantRowBeforeSubmit));
            participantListRefreshed = true;
        }
        assertTrue(participantListRefreshed, "The participant list should refresh after adding steps");
        assertEquals(selectedParticipantSteps + 999_999, stepsFor(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void participantMovesToFirstPlace() {
        assertEquals(selectedParticipant,
                participantRows().get(0).findElement(By.cssSelector(".participant-name")).getText());
    }

    private WebDriver driver() {
        if (Hooks.driver == null) {
            throw new IllegalStateException("Cucumber browser has not been initialized by Hooks");
        }
        return Hooks.driver;
    }

    private List<WebElement> participantRows() {
        waitForParticipantRows();
        return driver().findElements(PARTICIPANT_ROWS);
    }

    private void waitForParticipantRows() {
        new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.numberOfElementsToBe(PARTICIPANT_ROWS, 10));
    }

    private WebElement waitFor(By locator) {
        return new WebDriverWait(driver(), WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private <T> T waitFor(org.openqa.selenium.support.ui.ExpectedCondition<T> condition) {
        return new WebDriverWait(driver(), WAIT_TIMEOUT).until(condition);
    }

    private void waitUntil(org.openqa.selenium.support.ui.ExpectedCondition<?> condition) {
        new WebDriverWait(driver(), WAIT_TIMEOUT).until(condition);
    }

    private Map<String, Long> participantStepsByName() {
        return participantRows().stream().collect(Collectors.toMap(
                row -> row.findElement(By.cssSelector(".participant-name")).getText(),
                SampleSteps::stepsFromRow,
                (first, duplicate) -> {
                    throw new AssertionError("Duplicate participant name in the page");
                },
                LinkedHashMap::new));
    }

    private Select participantDropdown() {
        return new Select(waitFor(By.id("participant_select")));
    }

    private long stepsFor(String name) {
        return participantRows().stream()
                .filter(row -> row.findElement(By.cssSelector(".participant-name")).getText().equals(name))
                .findFirst()
                .map(SampleSteps::stepsFromRow)
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private WebElement participantRow(String name) {
        return participantRows().stream()
                .filter(row -> row.findElement(By.cssSelector(".participant-name")).getText().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Participant not found: " + name));
    }

    private static long stepsFromRow(WebElement row) {
        return parseSteps(row.findElement(By.cssSelector(".participant-steps")).getText());
    }

    private static long parseSteps(String text) {
        String digits = text.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            throw new AssertionError("Could not read a step count from: " + text);
        }
        return Long.parseLong(digits);
    }

    private void assertTrophyColorAtRank(int rank, String expectedColor) {
        List<WebElement> rows = participantRows();
        assertTrue(rows.size() >= rank, "Expected a participant at rank " + rank);
        WebElement trophy = rows.get(rank - 1).findElement(By.cssSelector(".fa-trophy"));
        assertEquals(expectedColor, trophy.getCssValue("color"),
                "Unexpected trophy color at rank " + rank);
    }
}
