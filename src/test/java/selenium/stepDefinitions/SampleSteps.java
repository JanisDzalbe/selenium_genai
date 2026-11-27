package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class SampleSteps {

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);
    private Map<String, Long> initialParticipantData;
    private final Map<String, Long> capturedStepCounts = new LinkedHashMap<>();
    private String selectedParticipantName;
    private long selectedParticipantOriginalSteps;

    public SampleSteps() {
        // Can use static driver from Hooks directly
    }

    @Given("I am on the fitness challenge page {string}")
    public void openFitnessChallengePage(String url) {
        Hooks.driver.manage().window().maximize();
        Hooks.driver.get(url);
        waitForParticipants();
    }

    @When("the page loads for the first time")
    public void whenThePageLoadsForTheFirstTime() {
        waitForParticipants();
    }

    @When("I view the participant list")
    public void viewTheParticipantList() {
        waitForParticipants();
    }

    @Given("I note the current step count for {string}")
    public void noteCurrentStepCount(String participantName) {
        waitForParticipants();
        capturedStepCounts.put(participantName, displayedStepCount(participantName));
    }

    @When("I open the {string} modal")
    public void openModalFromTopButton(String modalName) {
        openAddStepsModalAtPosition(modalName, 0);
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String participantName) {
        selectParticipantAndEnterSteps(participantName, "");
        selectedParticipantName = participantName;
        selectedParticipantOriginalSteps = displayedStepCount(participantName);
    }

    @When("I select any participant")
    public void selectAnyParticipant() {
        Select dropdown = new Select(waitForModal().findElement(By.id("participant_select")));
        dropdown.selectByIndex(1);
        selectedParticipantName = dropdown.getFirstSelectedOption().getText().trim();
        selectedParticipantOriginalSteps = displayedStepCount(selectedParticipantName);
    }

    @When("I enter {string} in the steps input field")
    public void enterSteps(String stepCount) {
        WebElement stepsInput = waitForModal().findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys(stepCount);
    }

    @When("I click {string} button")
    public void clickModalSubmitButton(String buttonText) {
        if (!"Add Steps".equals(buttonText)) {
            fail("Unsupported submit button text: " + buttonText);
        }
        WebElement submitButton = waitForModal().findElement(By.id("modal_add_steps_button"));
        waitUntilClickable(submitButton).click();
    }

    @When("I click the {string} button at the top of the page")
    public void clickAddStepsButtonAtTop(String buttonText) {
        openAddStepsModalAtPosition(buttonText, 0);
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) Hooks.driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void clickAddStepsButtonAtBottom(String buttonText) {
        openAddStepsModalAtPosition(buttonText, 1);
    }

    @Given("I have opened the {string} modal")
    public void openAddStepsModal(String modalName) {
        waitForParticipants();
        initialParticipantData = displayedStepCounts();
        openAddStepsModalAtPosition(modalName, 0);
    }

    @When("I click the × \\(close\\) button in the top-right corner")
    public void clickModalCloseButton() {
        WebElement modal = waitForModal();
        WebElement closeButton = modal.findElement(
                By.cssSelector(".w3-closebtn[title='Close Modal']"));
        waitUntilClickable(closeButton).click();
    }

    @Then("the page displays title {string}")
    public void verifyPageTitle(String expectedTitle) {
        String title = new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")))
                .getText()
                .trim();
        assertEquals(expectedTitle, title);
    }

    @Then("{int} participants are displayed in the list")
    public void verifyParticipantCount(int expectedCount) {
        assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void verifyDefaultParticipants(DataTable dataTable) {
        Map<String, Long> expected = new LinkedHashMap<>();
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            expected.put(
                    row.get("Name"),
                    Long.parseLong(row.get("Steps").replaceAll("[^0-9]", ""))
            );
        }

        assertEquals(expected, displayedStepCounts());
    }

    @Then("{string} buttons are visible at top and bottom")
    public void verifyButtonsAtTopAndBottom(String expectedText) {
        String id;
        if ("Add Steps".equals(expectedText)) {
            id = "addStepsBtn";
        } else if ("Reset List".equals(expectedText)) {
            id = "resetBtn";
        } else {
            fail("Unsupported button text: " + expectedText);
            return;
        }
        List<WebElement> buttons = Hooks.driver.findElements(By.id(id));

        assertEquals(2, buttons.size(), expectedText + " should appear at the top and bottom");
        for (WebElement button : buttons) {
            assertTrue(button.isDisplayed());
            assertEquals(expectedText, button.getText().trim());
        }
    }

    @Then("participants are displayed in descending order by step count")
    public void verifyParticipantsAreInDescendingOrder() {
        List<Long> stepCounts = displayedStepCountsInOrder();
        for (int i = 0; i < stepCounts.size() - 1; i++) {
            assertTrue(stepCounts.get(i) >= stepCounts.get(i + 1),
                    "Participants should be displayed in descending step order");
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void verifyEachParticipantIsNotBelowBySteps() {
        List<Long> stepCounts = displayedStepCountsInOrder();
        for (int i = 0; i < stepCounts.size() - 1; i++) {
            assertTrue(stepCounts.get(i) >= stepCounts.get(i + 1),
                    "Participant ranked " + (i + 1) + " should have at least as many steps as rank " + (i + 2));
        }
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void verifyFirstPlaceGoldTrophy() {
        assertTrophy(0, "rgb(255, 215, 0)");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void verifySecondPlaceSilverTrophy() {
        assertTrophy(1, "rgb(192, 192, 192)");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void verifyThirdPlaceBronzeTrophy() {
        assertTrophy(2, "rgb(205, 127, 50)");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void verifyNoTrophiesFromFourthPlaceDown() {
        List<WebElement> rows = participantRows();
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector("i.fa.fa-trophy")).isEmpty(),
                    "Participant ranked " + (i + 1) + " should not have a trophy");
        }
    }

    @Then("a modal window appears with title {string}")
    public void verifyModalTitle(String expectedTitle) {
        WebElement modal = waitForModal();
        assertTrue(modal.isDisplayed());
        assertEquals(expectedTitle, modal.findElement(By.tagName("h3")).getText().trim());
    }

    @Then("the modal contains a participant dropdown")
    public void verifyModalParticipantDropdown() {
        assertTrue(waitForModal().findElement(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void verifyModalNumberInput() {
        WebElement input = waitForModal().findElement(By.id("steps_input"));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void verifyModalSubmitButton(String expectedText) {
        WebElement submitButton = waitForModal().findElement(By.id("modal_add_steps_button"));
        assertTrue(submitButton.isDisplayed());
        assertEquals(expectedText, submitButton.getText().trim());
    }

    @Then("the modal has a close button \\(×\\) in the top-right corner")
    public void verifyModalCloseButton() {
        WebElement closeButton = waitForModal().findElement(
                By.cssSelector(".w3-closebtn[title='Close Modal']"));
        assertTrue(closeButton.isDisplayed());
        assertTrue(closeButton.getAttribute("class").contains("w3-display-topright"));
        assertEquals("×", closeButton.getText().trim());
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void verifyAllParticipantsInDropdown() {
        Select dropdown = new Select(waitForModal().findElement(By.id("participant_select")));
        List<String> participantNames = dropdown.getOptions().stream()
                .skip(1)
                .map(option -> option.getText().trim())
                .toList();
        assertEquals(10, participantNames.size());
        assertEquals(List.of(
                "Mike Kid", "Jill Watson", "Jane Doe", "John Smith", "Sarah Johnson",
                "Carlos Garcia", "Emily Chen", "David Brown", "Maria Rodriguez", "Alex Taylor"),
                participantNames);
    }

    @Then("the default dropdown text shows {string}")
    public void verifyDefaultDropdownText(String expectedText) {
        Select dropdown = new Select(waitForModal().findElement(By.id("participant_select")));
        assertEquals(expectedText, dropdown.getOptions().get(0).getText().trim());
        assertEquals("", dropdown.getFirstSelectedOption().getAttribute("value"));
    }

    @Then("all modal elements are present")
    public void verifyAllModalElementsArePresent() {
        verifyModalTitle("Add Steps to Participant");
        verifyModalParticipantDropdown();
        verifyModalNumberInput();
        verifyModalSubmitButton("Add Steps");
        verifyModalCloseButton();
        verifyAllParticipantsInDropdown();
        verifyDefaultDropdownText("Choose participant");
    }

    @Then("the modal closes")
    public void verifyModalCloses() {
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Then("I return to the main page view")
    public void verifyMainPageView() {
        assertTrue(new WebDriverWait(Hooks.driver, WAIT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")))
                .isDisplayed());
        waitForParticipants();
    }

    @Then("no data is changed from initial state")
    public void verifyParticipantDataIsUnchanged() {
        assertEquals(initialParticipantData, displayedStepCounts());
    }

    @Then("the modal closes automatically")
    public void verifyModalClosesAutomatically() {
        verifyModalCloses();
    }

    @Then("the participant list refreshes")
    public void verifyParticipantListRefreshes() {
        waitForParticipants();
        assertEquals(10, participantRows().size());
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void verifyMikeKidStepIncrease() {
        long originalSteps = capturedStepCounts.getOrDefault("Mike Kid",
                selectedParticipantOriginalSteps);
        assertEquals(originalSteps + 1_000, displayedStepCount("Mike Kid"));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void verifyListResortsAfterMikeKidUpdate() {
        verifyParticipantsAreInDescendingOrder();
    }

    @Then("an alert appears: {string}")
    public void verifyAlertText(String expectedText) {
        Alert alert = new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(
                ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        Alert alert = new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(
                ExpectedConditions.alertIsPresent());
        alert.accept();
    }

    @Then("the modal remains open")
    public void verifyModalRemainsOpen() {
        assertTrue(waitForModal().isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void verifyLargeStepUpdate() {
        waitForParticipants();
        assertEquals(selectedParticipantOriginalSteps + 999_999,
                displayedStepCount(selectedParticipantName));
    }

    @Then("the participant moves to first place")
    public void verifySelectedParticipantMovesToFirstPlace() {
        waitForParticipants();
        assertEquals(selectedParticipantName,
                participantRows().get(0).findElement(By.className("participant-name"))
                        .getText().trim());
    }

    private void waitForParticipants() {
        new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(
                ExpectedConditions.numberOfElementsToBe(By.cssSelector("#participantsList > div > li"), 10)
        );
    }

    private void openAddStepsModalAtPosition(String buttonText, int position) {
        if (!"Add Steps".equals(buttonText)) {
            fail("Unsupported modal button text: " + buttonText);
        }
        List<WebElement> buttons = Hooks.driver.findElements(By.id("addStepsBtn"));
        assertEquals(2, buttons.size());
        WebElement button = buttons.get(position);
        if (position == 1) {
            ((JavascriptExecutor) Hooks.driver).executeScript("arguments[0].scrollIntoView(true);", button);
        }
        waitUntilClickable(button).click();
        waitForModal();
    }

    private WebElement waitForModal() {
        return new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private WebElement waitUntilClickable(WebElement element) {
        return new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(
                ExpectedConditions.elementToBeClickable(element));
    }

    private List<WebElement> participantRows() {
        return Hooks.driver.findElements(By.cssSelector("#participantsList > div > li"));
    }

    private WebElement participantRow(String participantName) {
        return new WebDriverWait(Hooks.driver, WAIT_TIMEOUT).until(driver -> {
            for (WebElement row : participantRows()) {
                try {
                    String name = row.findElement(By.className("participant-name")).getText().trim();
                    if (participantName.equals(name)) {
                        return row;
                    }
                } catch (org.openqa.selenium.StaleElementReferenceException ignored) {
                    // The list changed. Try again.
                }
            }
            return null;
        });
    }

    private long displayedStepCount(String participantName) {
        String stepsText = participantRow(participantName)
                .findElement(By.className("participant-steps"))
                .getText()
                .trim();
        return Long.parseLong(stepsText.replaceAll("[^0-9]", ""));
    }

    private void selectParticipantAndEnterSteps(String participantName, String stepCount) {
        new Select(waitForModal().findElement(By.id("participant_select")))
                .selectByVisibleText(participantName);
        if (!stepCount.isEmpty()) {
            WebElement stepsInput = waitForModal().findElement(By.id("steps_input"));
            stepsInput.clear();
            stepsInput.sendKeys(stepCount);
        }
    }

    private Map<String, Long> displayedStepCounts() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (WebElement row : participantRows()) {
            String name = row.findElement(By.className("participant-name")).getText().trim();
            String stepsText = row.findElement(By.className("participant-steps")).getText().trim();
            counts.put(name, Long.parseLong(stepsText.replaceAll("[^0-9]", "")));
        }
        return counts;
    }

    private List<Long> displayedStepCountsInOrder() {
        List<Long> stepCounts = new ArrayList<>();
        for (WebElement row : participantRows()) {
            String stepsText = row.findElement(By.className("participant-steps")).getText().trim();
            stepCounts.add(Long.parseLong(stepsText.replaceAll("[^0-9]", "")));
        }
        return stepCounts;
    }

    private void assertTrophy(int position, String expectedColor) {
        WebElement trophy = participantRows().get(position)
                .findElement(By.cssSelector("i.fa.fa-trophy"));
        String actualColor = trophy.getCssValue("color")
                .replace("rgba(", "rgb(")
                .replace(", 1)", ")");
        assertEquals(expectedColor, actualColor);
    }
}
