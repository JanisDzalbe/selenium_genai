package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FitnessChallengeSteps {
    private String selectedParticipantName;
    private long selectedParticipantBeforeSteps;
    private long mikeKidBeforeSteps;
    private long enteredSteps;
    private Map<String, Long> participantStepsBeforeModal;

    private WebDriver driver() {
        return Hooks.driver;
    }

    private WebDriverWait getWait() {
        return new WebDriverWait(driver(), Duration.ofSeconds(10));
    }

    private List<WebElement> getParticipants() {
        getWait().until(d -> d.findElements(By.cssSelector("#participantsList li")).size() == 10);
        return driver().findElements(By.cssSelector("#participantsList li"));
    }

    private long readSteps(WebElement participant) {
        String text = participant.findElement(By.cssSelector(".participant-steps"))
                .getText()
                .replace("steps", "")
                .replace(",", "")
                .trim();
        return Long.parseLong(text);
    }
// challenge 1 and 2
    @Given("I am on the fitness challenge page {string}")
    public void iAmOnTheFitnessChallengePage(String url) {
        driver().get(url);
        getWait().until(d -> d.findElements(By.cssSelector("#participantsList li")).size() >= 10);
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript(
                "for (var i = localStorage.length - 1; i >= 0; i--) {" +
                        "var key = localStorage.key(i);" +
                        "if (key && (key.startsWith('participant') || key === 'fitnessReload')) localStorage.removeItem(key);" +
                        "}");
        driver().navigate().refresh();
        getParticipants();
    }

    @When("I view the participant list")
    public void iViewTheParticipantList() {
        getParticipants();
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, driver().getTitle());
        assertEquals(expectedTitle, driver().findElement(By.xpath("//h2")).getText().trim());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        assertEquals(expectedCount, getParticipants().size());
    }

    @Then("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(DataTable table) {
        Map<String, Long> expected = new LinkedHashMap<>();
        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            expected.put(row.get("Name").trim(),
                    Long.parseLong(row.get("Steps").replace(",", "").trim()));
        }

        Map<String, Long> actual = new LinkedHashMap<>();
        Set<String> names = new HashSet<>();
        for (WebElement participant : getParticipants()) {
            String name = participant.findElement(By.cssSelector(".participant-name")).getText().trim();
            assertTrue("Duplicate participant: " + name, names.add(name));
            actual.put(name, readSteps(participant));
        }

        assertEquals(expected, actual);
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {

        List<WebElement> buttons = driver().findElements(
                By.xpath("//button[normalize-space()='" + buttonText + "']")
        );

        List<WebElement> visibleButtons = buttons.stream()
                .filter(WebElement::isDisplayed)
                .toList();

        assertEquals(2, visibleButtons.size());

        long firstPosition = visibleButtons.get(0).getRect().getY();
        long secondPosition = visibleButtons.get(1).getRect().getY();

        assertTrue("The first button is not positioned above the second",
                firstPosition < secondPosition);
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        List<WebElement> participants = getParticipants();
        long previous = Long.MAX_VALUE;
        for (WebElement participant : participants) {
            long steps = readSteps(participant);
            assertTrue("Participants are not in descending step-count order",
                    steps <= previous);
            previous = steps;
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachStepCountIsGreaterThanOrEqualToTheParticipantBelow() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void theFirstPlaceParticipantDisplaysGoldTrophy() {
        assertTrophy(0, "gold");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void theSecondPlaceParticipantDisplaysSilverTrophy() {
        assertTrophy(1, "silver");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void theThirdPlaceParticipantDisplaysBronzeTrophy() {
        assertTrophy(2, "#cd7f32");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsRankedFourthAndBelowHaveNoTrophyIcons() {
        List<WebElement> participants = getParticipants();
        for (int i = 3; i < participants.size(); i++) {
            assertTrue("Participant rank " + (i + 1) + " unexpectedly has a trophy",
                    participants.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
    }

    private void assertTrophy(int index, String color) {
        WebElement trophy = getParticipants().get(index)
                .findElement(By.cssSelector(".fa-trophy"));
        String style = trophy.getAttribute("style").toLowerCase();
        String cssColor = trophy.getCssValue("color").toLowerCase();
        assertTrue("Trophy has the wrong medal color",
                style.contains(color) || cssColor.contains(color));
    }

//    challenge 4
    @Given("I note the current step count for {string}")
    public void iNoteTheCurrentStepCountFor(String participantName) {
        selectedParticipantName = participantName;
        selectedParticipantBeforeSteps = readSteps(findParticipant(participantName));
        mikeKidBeforeSteps = selectedParticipantBeforeSteps;
    }

    @When("I open the {string} modal")
    public void iOpenTheModal(String buttonText) {
        participantStepsBeforeModal = readParticipantSteps();
        List<WebElement> buttons = driver().findElements(
                By.xpath("//button[@id='addStepsBtn'][normalize-space()='" + buttonText + "']")
        );
        assertTrue("No Add Steps button was found", !buttons.isEmpty());
        buttons.get(0).click();
        getWait().until(d -> "block".equals(d.findElement(By.id("addStepsModal"))
                .getCssValue("display")));
    }

    @When("I click the {string} button at the top of the page")
    public void iClickTopButton(String buttonText) {
        iOpenTheModal(buttonText);
    }

    @When("I scroll to the bottom of the page")
    public void iScrollToTheBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void iClickBottomButton(String buttonText) {
        participantStepsBeforeModal = readParticipantSteps();
        List<WebElement> buttons = driver().findElements(
                By.xpath("//button[@id='addStepsBtn'][normalize-space()='" + buttonText + "']")
        );
        assertEquals(2, buttons.size());
        WebElement bottomButton = buttons.get(1);
        JavascriptExecutor js = (JavascriptExecutor) driver();
        js.executeScript("arguments[0].scrollIntoView(true);", bottomButton);
        bottomButton.click();
        getWait().until(d -> "block".equals(d.findElement(By.id("addStepsModal"))
                .getCssValue("display")));
    }

    @Given("I have opened the {string} modal")
    public void iHaveOpenedTheModal(String buttonText) {
        iOpenTheModal(buttonText);
    }

    @Then("a modal window appears with title {string}")
    public void aModalWindowAppearsWithTitle(String expectedTitle) {
        assertTrue(driver().findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals(expectedTitle,
                driver().findElement(By.xpath("//div[@id='addStepsModal']//h3"))
                        .getText().trim());
    }

    @Then("the modal contains a participant dropdown")
    public void theModalContainsAParticipantDropdown() {
        assertTrue(driver().findElement(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void theModalContainsANumberInputField() {
        WebElement input = driver().findElement(By.id("steps_input"));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void theModalContainsSubmitButton(String buttonText) {
        WebElement button = driver().findElement(By.id("modal_add_steps_button"));
        assertTrue(button.isDisplayed());
        assertEquals(buttonText, button.getText().trim());
    }

    @Then("^the modal has a close button .* in the top-right corner$")
    public void theModalHasCloseButton() {
        assertTrue(driver().findElement(By.cssSelector("#addStepsModal .w3-closebtn"))
                .isDisplayed());
    }

    @Then("the dropdown is prepopulated with all {int} participants")
    public void theDropdownIsPrepopulatedWithParticipants(int expectedCount) {
        Select select = new Select(driver().findElement(By.id("participant_select")));
        assertEquals(expectedCount, select.getOptions().size() - 1);
    }

    @Then("the default dropdown text shows {string}")
    public void theDefaultDropdownTextShows(String expectedText) {
        Select select = new Select(driver().findElement(By.id("participant_select")));
        assertEquals(expectedText, select.getFirstSelectedOption().getText().trim());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        aModalWindowAppearsWithTitle("Add Steps to Participant");
        theModalContainsAParticipantDropdown();
        theModalContainsANumberInputField();
        theModalContainsSubmitButton("Add Steps");
        theModalHasCloseButton();
    }

    @When("^I click the .*close.* button in the top-right corner$")
    public void iClickTheModalCloseButton() {
        WebElement closeButton = driver().findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", closeButton);
        theModalCloses();
    }

    @Then("I return to the main page view")
    public void iReturnToTheMainPageView() {
        assertEquals("none",
                driver().findElement(By.id("addStepsModal")).getCssValue("display"));
    }

    @Then("no data is changed from initial state")
    public void noDataIsChangedFromInitialState() {
        assertEquals(participantStepsBeforeModal, readParticipantSteps());
    }

    @When("I select {string} from the dropdown")
    public void iSelectFromTheDropdown(String participantName) {
        selectParticipant(participantName);
    }

    @When("I select any participant")
    public void iSelectAnyParticipant() {
        selectParticipant("Mike Kid");
    }

    @When("I select a participant")
    public void iSelectAParticipant() {
        iSelectAnyParticipant();
    }

    @When("I leave the participant dropdown at {string}")
    public void iLeaveTheParticipantDropdownAt(String defaultText) {
        Select select = new Select(driver().findElement(By.id("participant_select")));
        assertEquals(defaultText, select.getFirstSelectedOption().getText().trim());
    }

    private void selectParticipant(String participantName) {
        selectedParticipantName = participantName;
        selectedParticipantBeforeSteps = readSteps(findParticipant(participantName));
        new Select(driver().findElement(By.id("participant_select")))
                .selectByVisibleText(participantName);
    }

    @When("I enter {string} in the steps input field")
    public void iEnterSteps(String steps) {
        WebElement input = driver().findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(steps);
        try {
            enteredSteps = Long.parseLong(steps);
        } catch (NumberFormatException exception) {
            enteredSteps = 0;
        }
    }

    @When("I enter {string} in the steps input")
    public void iEnterStepsWithoutField(String steps) {
        iEnterSteps(steps);
    }

    @When("I attempt to enter {string} in the steps input field")
    public void iAttemptToEnterSteps(String steps) {
        iEnterSteps(steps);
    }

    @When("I click {string} button")
    public void iClickButton(String buttonText) {
        if ("Add Steps".equals(buttonText)) {
            driver().findElement(By.id("modal_add_steps_button")).click();
        } else {
            List<WebElement> buttons = driver().findElements(
                    By.xpath("//button[normalize-space()='" + buttonText + "']")
            );
            assertTrue("Button not found: " + buttonText, !buttons.isEmpty());
            buttons.get(0).click();
        }
    }

    @When("I attempt to click {string} button")
    public void iAttemptToClickButton(String buttonText) {
        iClickButton(buttonText);
    }

    @Then("the modal closes automatically")
    @Then("the modal closes")
    public void theModalCloses() {
        getWait().until(d -> "none".equals(d.findElement(By.id("addStepsModal"))
                .getCssValue("display")));
    }

    @Then("the participant list refreshes")
    public void theParticipantListRefreshes() {
        long expectedSteps = selectedParticipantBeforeSteps + enteredSteps;
        getWait().until(d -> {
            try {
                return readSteps(findParticipant(selectedParticipantName)) == expectedSteps;
            } catch (AssertionError error) {
                return false;
            }
        });
        assertEquals(10, getParticipants().size());
        assertEquals(expectedSteps, readSteps(findParticipant(selectedParticipantName)));
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeKidsStepCountIncreasesByOneThousand() {
        assertEquals(mikeKidBeforeSteps + enteredSteps,
                readSteps(findParticipant("Mike Kid")));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void theListResortsIfMikeKidsNewTotalChangesHisRanking() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("an alert appears: {string}")
    public void anAlertAppears(String expectedMessage) {
        Alert alert = getWait().until(d -> d.switchTo().alert());
        assertEquals(expectedMessage, alert.getText());
    }

    @When("I accept the alert")
    public void iAcceptTheAlert() {
        driver().switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void theModalRemainsOpen() {
        assertTrue(driver().findElement(By.id("addStepsModal")).isDisplayed());
        assertFalse("Modal unexpectedly closed",
                "none".equals(driver().findElement(By.id("addStepsModal"))
                        .getCssValue("display")));
    }

    @Then("the input field rejects non-numeric characters")
    public void theInputFieldRejectsNonNumericCharacters() {
        assertEquals("number",
                driver().findElement(By.id("steps_input")).getAttribute("type"));
    }

    @Then("no alphabetic characters appear in the input")
    public void noAlphabeticCharactersAppearInTheInput() {
        String value = driver().findElement(By.id("steps_input")).getAttribute("value");
        assertTrue(value == null || value.matches("[0-9]*"));
    }

    @Then("the step count updates correctly with the large number")
    public void theStepCountUpdatesWithTheLargeNumber() {
        assertEquals(selectedParticipantBeforeSteps + enteredSteps,
                readSteps(findParticipant(selectedParticipantName)));
    }

    @Then("the participant moves to first place")
    public void theParticipantMovesToFirstPlace() {
        WebElement first = getParticipants().get(0);
        assertEquals(selectedParticipantName,
                first.findElement(By.cssSelector(".participant-name")).getText().trim());
    }

    private WebElement findParticipant(String participantName) {
        for (WebElement participant : getParticipants()) {
            String name = participant.findElement(By.cssSelector(".participant-name"))
                    .getText()
                    .trim();
            if (name.equals(participantName)) {
                return participant;
            }
        }
        throw new AssertionError("Participant not found: " + participantName);
    }

    private Map<String, Long> readParticipantSteps() {
        Map<String, Long> participantSteps = new LinkedHashMap<>();
        for (WebElement participant : getParticipants()) {
            String name = participant.findElement(By.cssSelector(".participant-name"))
                    .getText()
                    .trim();
            participantSteps.put(name, readSteps(participant));
        }
        return participantSteps;
    }
}
