package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallengeSteps {
    private static final Duration WAIT_SECONDS = Duration.ofSeconds(10);
    private static final By PARTICIPANT_ITEMS = By.cssSelector("#participantsList li");
    private static final By MODAL = By.id("addStepsModal");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final By MODAL_SUBMIT = By.id("modal_add_steps_button");

    private Map<String, Long> initialParticipantSteps;
    private Map<String, Long> participantStepsBeforeUpdate;
    private long currentParticipantSteps;
    private long enteredSteps;
    private String selectedParticipant;

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        Hooks.driver.get(url);
        new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h2")));
        initialParticipantSteps = readParticipantSteps();
    }

    @When("the page loads for the first time")
    @When("I view the participant list")
    public void viewParticipants() {
        participantItems();
    }

    @When("I click the {string} button at the top of the page")
    public void clickTopButton(String text) {
        getButton(text, 0).click();
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottom() {
        WebElement bottomButton = getButton("Add Steps", 1);
        new Actions(Hooks.driver).moveToElement(bottomButton).perform();
        new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.elementToBeClickable(bottomButton));
    }

    @When("I click the {string} button at the bottom")
    public void clickBottomButton(String text) {
        getButton(text, 1).click();
    }

    @Given("I have opened the {string} modal")
    @When("I open the {string} modal")
    public void openModal(String text) {
        getButton(text, 0).click();
        waitForModalOpen();
        participantStepsBeforeUpdate = readParticipantSteps();
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void clickModalClose() {
        new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("#addStepsModal .w3-closebtn")))
                .click();
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentSteps(String name) {
        currentParticipantSteps = getParticipantSteps(name);
        selectedParticipant = name;
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String name) {
        selectParticipant(name, false);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        selectParticipant(null, true);
    }

    @When("I enter {string} in the steps input field")
    public void enterSteps(String steps) {
        WebElement input = new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.elementToBeClickable(STEPS_INPUT));
        input.clear();
        input.sendKeys(steps);
        enteredSteps = parseSteps(steps);
    }

    @When("I click {string} button")
    public void clickModalButton(String text) {
        WebElement button = new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.elementToBeClickable(MODAL_SUBMIT));
        assertEquals(text, button.getText().trim());
        button.click();
    }

    @Then("an alert appears: {string}")
    public void assertAlert(String message) {
        Alert alert = new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.alertIsPresent());
        assertEquals(message, alert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        Hooks.driver.switchTo().alert().accept();
    }

    @Then("the page displays title {string}")
    public void assertTitle(String title) {
        assertEquals(title, Hooks.driver.findElement(By.cssSelector("h2")).getText());
    }

    @Then("a modal window appears with title {string}")
    public void assertModalTitle(String title) {
        waitForModalOpen();
        assertEquals(title, Hooks.driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void assertParticipantDropdown() {
        assertTrue(Hooks.driver.findElement(PARTICIPANT_SELECT).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void assertStepsInput() {
        WebElement input = Hooks.driver.findElement(STEPS_INPUT);
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void assertModalSubmit(String text) {
        WebElement button = Hooks.driver.findElement(MODAL_SUBMIT);
        assertTrue(button.isDisplayed());
        assertEquals(text, button.getText().trim());
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void assertModalCloseButton() {
        WebElement closeButton = Hooks.driver.findElement(
                By.cssSelector("#addStepsModal .w3-closebtn"));
        assertTrue(closeButton.isDisplayed());
        assertEquals("×", closeButton.getText());
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void assertDropdownParticipants() {
        Select select = new Select(Hooks.driver.findElement(PARTICIPANT_SELECT));
        List<WebElement> options = select.getOptions();
        assertEquals(11, options.size());

        List<String> names = options.subList(1, options.size()).stream()
                .map(WebElement::getText)
                .toList();
        assertEquals(initialParticipantSteps.keySet().stream().sorted().toList(),
                names.stream().sorted().toList());
    }

    @Then("the default dropdown text shows {string}")
    public void assertDefaultDropdownText(String text) {
        Select select = new Select(Hooks.driver.findElement(PARTICIPANT_SELECT));
        assertEquals(text, select.getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void assertAllModalElements() {
        assertModalTitle("Add Steps to Participant");
        assertParticipantDropdown();
        assertStepsInput();
        assertModalSubmit("Add Steps");
        assertModalCloseButton();
    }

    @Then("the modal closes")
    @Then("the modal closes automatically")
    public void assertModalClosed() {
        new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    @Then("I return to the main page view")
    public void assertMainPageView() {
        assertTrue(Hooks.driver.findElement(By.cssSelector("h2")).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void assertInitialState() {
        assertEquals(initialParticipantSteps, readParticipantSteps());
        assertRankingDescending();
    }

    @Then("the modal remains open")
    public void assertModalOpen() {
        waitForModalOpen();
    }

    @Then("the participant list refreshes")
    public void assertParticipantListRefreshes() {
        assertEquals(initialParticipantSteps.size(), participantItems().size());
        new WebDriverWait(Hooks.driver, WAIT_SECONDS).until(driver ->
                !participantStepsBeforeUpdate.equals(readParticipantSteps()));
    }

    @Then("^(.+)'s step count increases by ([\\d,]+)$")
    public void assertStepIncrease(String name, String increaseText) {
        long increase = parseSteps(increaseText);
        assertParticipantSteps(name, currentParticipantSteps + increase);
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void assertRankingAfterUpdate() {
        assertRankingDescending();
    }

    @Then("the step count updates correctly with the large number")
    public void assertLargeStepUpdate() {
        assertParticipantSteps(selectedParticipant, currentParticipantSteps + enteredSteps);
    }

    @Then("the participant moves to first place")
    public void assertSelectedParticipantFirst() {
        assertEquals(selectedParticipant,
                participantItems().get(0).findElement(
                        By.cssSelector(".participant-name")).getText());
        assertRankingDescending();
    }

    @Then("{int} participants are displayed in the list")
    public void assertParticipantCount(int count) {
        assertEquals(count, participantItems().size());
    }

    @Then("the default participants are displayed:")
    public void assertDefaultParticipants(DataTable dataTable) {
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            String name = row.get("Name");
            long expectedSteps = parseSteps(row.get("Steps"));
            assertEquals(expectedSteps, getParticipantSteps(name));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void assertButtonsVisible(String text) {
        List<WebElement> buttons = getButtons(text);
        assertEquals(2, buttons.size());
        assertTrue(buttons.stream().allMatch(button ->
                text.equals(button.getText().trim())));
    }

    @Then("participants are displayed in descending order by step count")
    @Then("each participant's step count is greater than or equal to the participant below them")
    public void assertRanking() {
        assertRankingDescending();
    }

    @Then("the {word} place participant displays a {word} trophy icon")
    public void assertTrophy(String place, String colorName) {
        int index = switch (place) {
            case "1st" -> 0;
            case "2nd" -> 1;
            case "3rd" -> 2;
            default -> throw new IllegalArgumentException("Unsupported place: " + place);
        };
        String[] expectedColors = switch (colorName) {
            case "gold" -> new String[]{"rgb(255, 215, 0)", "rgba(255, 215, 0, 1)"};
            case "silver" -> new String[]{"rgb(192, 192, 192)", "rgba(192, 192, 192, 1)"};
            case "bronze" -> new String[]{"rgb(205, 127, 50)", "rgba(205, 127, 50, 1)"};
            default -> throw new IllegalArgumentException("Unsupported color: " + colorName);
        };
        assertTrophyColor(index, expectedColors);
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void assertNoTrophies() {
        List<WebElement> items = participantItems();
        for (int i = 3; i < items.size(); i++) {
            assertTrue(items.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    private WebElement getButton(String text, int index) {
        List<WebElement> buttons = getButtons(text);
        assertEquals(2, buttons.size());
        assertEquals(text, buttons.get(index).getText().trim());
        return buttons.get(index);
    }

    private List<WebElement> getButtons(String text) {
        String id = text.equals("Add Steps") ? "addStepsBtn" : "resetBtn";
        return new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id(id)));
    }

    private void selectParticipant(String name, boolean anyParticipant) {
        Select select = new Select(new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.elementToBeClickable(PARTICIPANT_SELECT)));
        if (anyParticipant) {
            select.selectByIndex(1);
            selectedParticipant = select.getFirstSelectedOption().getText();
            currentParticipantSteps = getParticipantSteps(selectedParticipant);
        } else {
            select.selectByVisibleText(name);
            selectedParticipant = name;
            currentParticipantSteps = getParticipantSteps(name);
        }
    }

    private void waitForModalOpen() {
        new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.visibilityOfElementLocated(MODAL));
    }

    private void assertParticipantSteps(String name, long expectedSteps) {
        new WebDriverWait(Hooks.driver, WAIT_SECONDS).until(driver -> {
            try {
                return getParticipantSteps(name) == expectedSteps;
            } catch (AssertionError exception) {
                return false;
            }
        });
        assertEquals(expectedSteps, getParticipantSteps(name));
    }

    private long getParticipantSteps(String name) {
        for (WebElement item : participantItems()) {
            String participantName = item.findElement(
                    By.cssSelector(".participant-name")).getText();
            if (participantName.equals(name)) {
                return parseSteps(item.findElement(
                        By.cssSelector(".participant-steps")).getText());
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    private Map<String, Long> readParticipantSteps() {
        Map<String, Long> participants = new LinkedHashMap<>();
        for (WebElement item : participantItems()) {
            String name = item.findElement(
                    By.cssSelector(".participant-name")).getText();
            long steps = parseSteps(item.findElement(
                    By.cssSelector(".participant-steps")).getText());
            participants.put(name, steps);
        }
        return participants;
    }

    private List<WebElement> participantItems() {
        return new WebDriverWait(Hooks.driver, WAIT_SECONDS)
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(PARTICIPANT_ITEMS));
    }

    private void assertRankingDescending() {
        long previousSteps = Long.MAX_VALUE;
        for (WebElement item : participantItems()) {
            long steps = parseSteps(item.findElement(
                    By.cssSelector(".participant-steps")).getText());
            assertTrue(steps <= previousSteps);
            previousSteps = steps;
        }
    }

    private void assertTrophyColor(int index, String[] expectedColors) {
        WebElement trophy = participantItems().get(index)
                .findElement(By.cssSelector("i.fa-trophy"));
        String color = trophy.getCssValue("color");
        assertTrue("Unexpected trophy color: " + color,
                expectedColors[0].equalsIgnoreCase(color)
                        || expectedColors[1].equalsIgnoreCase(color));
    }

    private long parseSteps(String text) {
        return Long.parseLong(text.replace(" steps", "").replace(",", "").trim());
    }
}
