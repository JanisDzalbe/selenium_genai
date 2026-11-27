package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assume;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriverException;
import selenium.utility.WebDriverUtility;

import java.math.BigInteger;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallengeSteps {
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");
    private static final By ADD_STEPS_MODAL = By.id("addStepsModal");
    private static final By MODAL_ADD_BUTTON = By.id("modal_add_steps_button");
    private static final By PARTICIPANT_SELECT = By.id("participant_select");
    private static final By STEPS_INPUT = By.id("steps_input");
    private static final List<String> DEFAULT_RANKED_NAMES = List.of(
            "John Smith", "David Brown", "Jill Watson", "Carlos Garcia", "Maria Rodriguez",
            "Sarah Johnson", "Alex Taylor", "Mike Kid", "Emily Chen", "Jane Doe"
    );
    private static final List<Long> DEFAULT_RANKED_STEPS = List.of(
            15000L, 13500L, 12000L, 11200L, 10500L,
            9800L, 8900L, 8500L, 7300L, 6500L
    );

    private String selectedParticipant;
    private long stepsBeforeAddition;
    private String stepsToAdd;
    private final Map<String, Long> participantStepsBeforeAddition = new LinkedHashMap<>();

    @Given("^I am on the fitness challenge page \"([^\"]*)\"$")
    public void openFitnessChallengePage(String url) {
        WebDriver driver = Hooks.driver;
        driver.get(url);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
    }

    @Given("I open the fitness challenge page in {word}")
    public void openFitnessChallengePageInBrowser(String browser) {
        Hooks.driver.quit();
        Hooks.driver = null;
        try {
            Hooks.driver = WebDriverUtility.getDriver(browser);
        } catch (WebDriverException | IllegalStateException exception) {
            if ("edge".equalsIgnoreCase(browser) || "firefox".equalsIgnoreCase(browser)) {
                Assume.assumeNoException("Could not start " + browser + " browser", exception);
                return;
            }
            throw exception;
        }
        Hooks.driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
    }

    @When("the page loads for the first time")
    public void waitForInitialParticipants() {
        WebDriver driver = Hooks.driver;
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(currentDriver -> currentDriver.findElements(PARTICIPANT_ROWS).size() == 10);
    }

    @When("I view the participant list")
    public void waitForParticipantList() {
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(currentDriver -> currentDriver.findElements(PARTICIPANT_ROWS).size() == 10);
    }

    @Then("the page displays title {string}")
    public void assertPageTitle(String expectedTitle) {
        assertEquals(expectedTitle, Hooks.driver.findElement(By.cssSelector("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void assertParticipantCount(int expectedCount) {
        assertEquals(expectedCount, Hooks.driver.findElements(PARTICIPANT_ROWS).size());
    }

    @Then("the default participants are displayed:")
    public void assertDefaultParticipants(DataTable participantTable) {
        List<Map<String, String>> expectedParticipants = participantTable.asMaps(String.class, String.class);
        List<String> expectedNames = expectedParticipants.stream()
                .map(participant -> participant.get("Name").trim())
                .collect(Collectors.toList());
        List<Long> expectedSteps = expectedParticipants.stream()
                .map(participant -> Long.parseLong(participant.get("Steps").replaceAll("[^0-9]", "")))
                .collect(Collectors.toList());
        assertParticipantRows(expectedNames, expectedSteps);
    }

    @Then("{string} buttons are visible at top and bottom")
    public void assertTopAndBottomButtons(String buttonText) {
        List<WebElement> buttons = Hooks.driver.findElements(
                By.xpath("//button[normalize-space()='" + buttonText
                        + "' and not(ancestor::div[@id='addStepsModal'])]")
        );
        assertEquals("Expected exactly two " + buttonText + " buttons", 2, buttons.size());
        for (WebElement button : buttons) {
            assertTrue(buttonText + " button should be displayed", button.isDisplayed());
        }
    }

    @Then("participants are displayed in descending order by step count")
    public void assertParticipantsSortedDescending() {
        List<Long> actualSteps = readParticipantStepCounts();
        List<Long> expectedSteps = new ArrayList<>(actualSteps);
        expectedSteps.sort(Comparator.reverseOrder());
        assertEquals("Participant steps must be sorted in descending order",
                expectedSteps, actualSteps);
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void assertEachParticipantHasAtLeastAsManyStepsAsTheNext() {
        List<Long> steps = readParticipantStepCounts();
        for (int i = 0; i < steps.size() - 1; i++) {
            assertTrue("Participant at rank " + (i + 1) + " has " + steps.get(i)
                            + " steps, fewer than rank " + (i + 2) + " with " + steps.get(i + 1),
                    steps.get(i) >= steps.get(i + 1));
        }
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void assertGoldTrophyForFirstPlace() {
        assertTrophyColorAtRank(1, "rgba(255, 215, 0, 1)");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void assertSilverTrophyForSecondPlace() {
        assertTrophyColorAtRank(2, "rgba(192, 192, 192, 1)");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void assertBronzeTrophyForThirdPlace() {
        assertTrophyColorAtRank(3, "rgba(205, 127, 50, 1)");
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void assertNoTrophiesBelowThirdPlace() {
        List<WebElement> rows = Hooks.driver.findElements(PARTICIPANT_ROWS);
        assertEquals("Expected 10 participants before checking ranks 4-10", 10, rows.size());
        for (int i = 3; i < rows.size(); i++) {
            assertTrue("Participant at rank " + (i + 1) + " must not have a trophy icon",
                    rows.get(i).findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    @When("I click the {string} button at the top of the page")
    public void clickTopPageButton(String buttonText) {
        List<WebElement> buttons = pageButtons(buttonText);
        assertEquals("Expected two page buttons named " + buttonText, 2, buttons.size());
        buttons.get(0).click();
        if ("Add Steps".equals(buttonText)) {
            waitForVisible(ADD_STEPS_MODAL);
        }
    }

    @When("I scroll to the bottom of the page")
    @When("I scroll to bottom of page")
    public void scrollToBottomOfPage() {
        ((JavascriptExecutor) Hooks.driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight)"
        );
    }

    @When("I click the {string} button at the bottom")
    public void clickBottomPageButton(String buttonText) {
        List<WebElement> buttons = pageButtons(buttonText);
        assertEquals("Expected two page buttons named " + buttonText, 2, buttons.size());
        WebElement bottomButton = buttons.get(1);
        ((JavascriptExecutor) Hooks.driver).executeScript("arguments[0].scrollIntoView(true)", bottomButton);
        bottomButton.click();
        if ("Add Steps".equals(buttonText)) {
            waitForVisible(ADD_STEPS_MODAL);
        }
    }

    @Then("a modal window appears with title {string}")
    public void assertModalTitle(String title) {
        WebElement heading = waitForVisible(By.cssSelector("#addStepsModal h3"));
        assertEquals(title, heading.getText());
    }

    @Then("the modal contains a participant dropdown")
    public void assertModalHasParticipantDropdown() {
        assertTrue(waitForVisible(PARTICIPANT_SELECT).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void assertModalHasNumberInput() {
        WebElement input = waitForVisible(STEPS_INPUT);
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void assertModalHasSubmitButton(String buttonText) {
        WebElement button = waitForVisible(MODAL_ADD_BUTTON);
        assertEquals(buttonText, button.getText().trim());
        assertTrue(button.isDisplayed());
    }

    @Then("the modal has a close button \\(×\\) in the top-right corner")
    public void assertModalHasTopRightCloseButton() {
        WebElement closeButton = waitForVisible(By.cssSelector("#addStepsModal .w3-closebtn"));
        assertEquals("×", closeButton.getText().trim());
        assertTrue(closeButton.isDisplayed());
        assertTrue(closeButton.getAttribute("class").contains("w3-display-topright"));
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void assertDropdownHasAllParticipants() {
        List<WebElement> options = new Select(waitForVisible(PARTICIPANT_SELECT)).getOptions();
        assertEquals("Expected the placeholder plus 10 participants", 11, options.size());
        assertEquals("Choose participant", options.get(0).getText());
        List<String> participantNames = Hooks.driver.findElements(PARTICIPANT_ROWS).stream()
                .map(row -> row.findElement(By.cssSelector(".participant-name")).getText().trim())
                .collect(Collectors.toList());
        List<String> dropdownNames = options.subList(1, options.size()).stream()
                .map(option -> option.getText().trim())
                .collect(Collectors.toList());
        assertEquals(new HashSet<>(participantNames), new HashSet<>(dropdownNames));
    }

    @Then("the default dropdown text shows {string}")
    public void assertDefaultDropdownText(String text) {
        assertEquals(text, new Select(waitForVisible(PARTICIPANT_SELECT))
                .getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void assertAllModalElementsPresent() {
        assertModalElements();
    }

    @When("I click the × \\(close\\) button in the top-right corner")
    public void clickModalCloseButton() {
        WebElement closeButton = waitForVisible(By.cssSelector("#addStepsModal .w3-closebtn"));
        ((JavascriptExecutor) Hooks.driver).executeScript("arguments[0].click()", closeButton);
    }

    @Then("the modal closes")
    @Then("the modal closes automatically")
    public void assertModalCloses() {
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(ADD_STEPS_MODAL));
    }

    @Then("I return to the main page view")
    public void assertReturnedToMainPage() {
        assertTrue(Hooks.driver.findElement(By.id("participantsList")).isDisplayed());
        assertTrue(pageButtons("Add Steps").get(0).isDisplayed());
        assertTrue(pageButtons("Reset List").get(0).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void assertInitialListDataUnchanged() {
        assertParticipantRows(DEFAULT_RANKED_NAMES, DEFAULT_RANKED_STEPS);
    }

    @Given("I note the current step count for {string}")
    public void noteParticipantSteps(String participant) {
        waitForParticipantList();
        selectedParticipant = participant;
        captureParticipantState(participant);
    }

    @Given("I have added steps to at least 2 participants to modify the default state")
    @Given("I have modified participant data")
    public void modifyParticipantData() {
        long mikeBefore = participantSteps("Mike Kid");
        long janeBefore = participantSteps("Jane Doe");
        addStepsThroughModal("Mike Kid", "1000");
        addStepsThroughModal("Jane Doe", "2000");
        assertEquals(mikeBefore + 1000, participantSteps("Mike Kid"));
        assertEquals(janeBefore + 2000, participantSteps("Jane Doe"));
    }

    @When("I wait for page reload")
    public void waitForPageReload() {
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(driver -> hasDefaultParticipantState());
    }

    @Then("all participants return to their default step counts")
    public void assertDefaultCountsRestored() {
        assertDefaultParticipantCounts();
    }

    @Then("the default ranking order is restored")
    public void assertDefaultRankingOrderRestored() {
        List<String> actualNames = Hooks.driver.findElements(PARTICIPANT_ROWS).stream()
                .map(row -> row.findElement(By.cssSelector(".participant-name")).getText().trim())
                .collect(Collectors.toList());
        assertEquals(DEFAULT_RANKED_NAMES, actualNames);
    }

    @Then("trophy icons display for correct default top 3")
    public void assertDefaultTopThreeTrophies() {
        List<WebElement> rows = Hooks.driver.findElements(PARTICIPANT_ROWS);
        assertEquals(10, rows.size());
        assertEquals(List.of("John Smith", "David Brown", "Jill Watson"),
                rows.subList(0, 3).stream()
                        .map(row -> row.findElement(By.cssSelector(".participant-name")).getText().trim())
                        .collect(Collectors.toList()));
        assertTrophyColorAtRank(1, "rgba(255, 215, 0, 1)");
        assertTrophyColorAtRank(2, "rgba(192, 192, 192, 1)");
        assertTrophyColorAtRank(3, "rgba(205, 127, 50, 1)");
        for (int rank = 4; rank <= 10; rank++) {
            assertTrue("Participant at rank " + rank + " must not have a trophy icon",
                    rows.get(rank - 1).findElements(By.cssSelector("i.fa-trophy")).isEmpty());
        }
    }

    @Given("I have opened the {string} modal")
    @When("I open the {string} modal")
    public void openAddStepsModal(String modalName) {
        assertEquals("Add Steps", modalName);
        clickTopPageButton("Add Steps");
    }

    @When("I select {string} from the dropdown")
    public void selectParticipant(String participant) {
        selectedParticipant = participant;
        captureParticipantState(participant);
        new Select(waitForClickable(PARTICIPANT_SELECT)).selectByVisibleText(participant);
    }

    @When("I select any participant")
    @When("I select a participant")
    public void selectFixedParticipant() {
        selectParticipant("Mike Kid");
    }

    @When("I leave the participant dropdown at {string}")
    public void leaveParticipantUnselected(String placeholder) {
        Select select = new Select(waitForClickable(PARTICIPANT_SELECT));
        assertEquals(placeholder, select.getFirstSelectedOption().getText());
    }

    @When("I enter {string} in the steps input field")
    @When("I enter {string} in the steps input")
    public void enterStepsInField(String steps) {
        WebElement input = waitForClickable(STEPS_INPUT);
        input.clear();
        input.sendKeys(steps);
        stepsToAdd = steps;
    }

    @When("I click {string} button")
    @When("I attempt to click {string} button")
    public void clickNamedButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        clickModalAddButton();
    }

    @When("I submit the form")
    public void submitForm() {
        clickModalAddButton();
    }

    @When("I enter the maximum safe integer {string}")
    public void enterMaximumSafeInteger(String value) {
        enterStepsInField(value);
    }

    @Then("the system accepts the value")
    public void assertSystemAcceptsValue() {
        assertModalCloses();
    }

    @Then("the step count updates correctly")
    public void assertExactStepCountUpdate() {
        BigInteger expected = BigInteger.valueOf(stepsBeforeAddition)
                .add(new BigInteger(stepsToAdd));
        BigInteger actual = BigInteger.valueOf(participantSteps(selectedParticipant));
        assertEquals("Participant total must equal the exact mathematical sum", expected, actual);
    }

    @Then("an alert appears: {string}")
    public void assertAlertAppears(String expectedText) {
        Alert alert = new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
    }

    @When("I accept the alert")
    public void acceptAlert() {
        Hooks.driver.switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void assertModalRemainsOpen() {
        assertTrue(waitForVisible(ADD_STEPS_MODAL).isDisplayed());
    }

    @When("I attempt to enter {string} in the steps input field")
    public void attemptToEnterTextInStepsInput(String text) {
        WebElement input = waitForClickable(STEPS_INPUT);
        input.sendKeys(text);
    }

    @Then("the input field rejects non-numeric characters")
    public void assertNumberInputRejectsText() {
        assertEquals("number", Hooks.driver.findElement(STEPS_INPUT).getAttribute("type"));
    }

    @Then("no alphabetic characters appear in the input")
    public void assertNoAlphabeticCharactersInInput() {
        assertEquals("", Hooks.driver.findElement(STEPS_INPUT).getAttribute("value"));
    }

    @Then("the participant list refreshes")
    public void waitForUpdatedParticipantList() {
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(driver -> driver.findElements(PARTICIPANT_ROWS).size() == 10
                        && participantSteps(selectedParticipant) == stepsBeforeAddition + parsedStepsToAdd());
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void assertParticipantStepCountIncreased() {
        assertEquals(1000L, parsedStepsToAdd());
        assertEquals(stepsBeforeAddition + parsedStepsToAdd(),
                participantSteps(selectedParticipant));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void assertListResortedIfRankChanges() {
        assertEquals("Mike Kid", selectedParticipant);
        List<String> expectedNames = new ArrayList<>(participantStepsBeforeAddition.keySet());
        expectedNames.sort(Comparator.comparingLong((String name) ->
                participantStepsBeforeAddition.get(name)
                        + (name.equals(selectedParticipant) ? parsedStepsToAdd() : 0)).reversed());
        List<String> actualNames = Hooks.driver.findElements(PARTICIPANT_ROWS).stream()
                .map(row -> row.findElement(By.cssSelector(".participant-name")).getText().trim())
                .collect(Collectors.toList());
        assertEquals(expectedNames, actualNames);
        List<Long> actualSteps = readParticipantStepCounts();
        List<Long> sortedSteps = new ArrayList<>(actualSteps);
        sortedSteps.sort(Comparator.reverseOrder());
        assertEquals("Participant list must remain sorted in descending step count",
                sortedSteps, actualSteps);
        int newRank = participantRank(selectedParticipant);
        int expectedRank = expectedNames.indexOf(selectedParticipant) + 1;
        assertEquals("Participant should move to the rank implied by the updated count",
                expectedRank, newRank);
    }

    @Then("the step count updates correctly with the large number")
    public void assertLargeStepCountUpdate() {
        assertEquals(stepsBeforeAddition + parsedStepsToAdd(), participantSteps(selectedParticipant));
    }

    @Then("the participant moves to first place")
    public void assertParticipantMovesToFirstPlace() {
        assertEquals(selectedParticipant,
                Hooks.driver.findElement(PARTICIPANT_ROWS).findElement(By.cssSelector(".participant-name")).getText().trim());
    }

    @When("I add valid steps to a participant")
    public void addValidStepsToParticipant() {
        addStepsThroughModal("Mike Kid", "1000");
    }

    @When("I submit without selecting participant")
    public void submitWithoutSelectingParticipant() {
        openAddStepsModal("Add Steps");
        enterStepsInField("1000");
        clickModalAddButton();
        assertAlertAppears("Please select a participant");
        acceptAlert();
        assertModalRemainsOpen();
        clickModalCloseButton();
        assertModalCloses();
    }

    @When("I reset via top button")
    public void resetViaTopButton() {
        clickTopPageButton("Reset List");
        waitForPageReload();
    }

    @Then("all features work as expected")
    public void assertAllFeaturesWorkAsExpected() {
        assertDefaultParticipantCounts();
        assertDefaultRankingOrderRestored();
    }

    private List<Long> readParticipantStepCounts() {
        return Hooks.driver.findElements(PARTICIPANT_ROWS).stream()
                .map(row -> row.findElement(By.cssSelector(".participant-steps"))
                        .getText().replaceAll("[^0-9]", ""))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private void assertTrophyColorAtRank(int rank, String expectedColor) {
        List<WebElement> rows = Hooks.driver.findElements(PARTICIPANT_ROWS);
        assertTrue("Expected at least " + rank + " participant rows", rows.size() >= rank);
        List<WebElement> trophies = rows.get(rank - 1).findElements(By.cssSelector("i.fa-trophy"));
        assertEquals("Participant at rank " + rank + " must have exactly one trophy icon",
                1, trophies.size());
        assertEquals("Trophy color at rank " + rank, expectedColor, trophies.get(0).getCssValue("color"));
    }

    private void assertModalElements() {
        assertModalHasParticipantDropdown();
        assertModalHasNumberInput();
        assertModalHasSubmitButton("Add Steps");
        assertModalHasTopRightCloseButton();
        assertDropdownHasAllParticipants();
        assertDefaultDropdownText("Choose participant");
    }

    private void assertParticipantRows(List<String> expectedNames, List<Long> expectedSteps) {
        List<WebElement> actualRows = Hooks.driver.findElements(PARTICIPANT_ROWS);
        assertEquals("Participant count must match expected data", expectedNames.size(), actualRows.size());
        assertEquals("Expected name and step lists must have matching lengths",
                expectedNames.size(), expectedSteps.size());
        for (int i = 0; i < expectedNames.size(); i++) {
            WebElement row = actualRows.get(i);
            String actualName = row.findElement(By.cssSelector(".participant-name")).getText().trim();
            long actualSteps = parseSteps(row.findElement(By.cssSelector(".participant-steps")));
            assertEquals("Participant name at displayed position " + (i + 1),
                    expectedNames.get(i), actualName);
            assertEquals("Step count for " + actualName, expectedSteps.get(i).longValue(), actualSteps);
        }
    }

    private WebElement waitForVisible(By locator) {
        return new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private WebElement waitForClickable(By locator) {
        return new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    private List<WebElement> pageButtons(String buttonText) {
        return Hooks.driver.findElements(By.xpath("//button[normalize-space()='" + buttonText
                + "' and not(ancestor::div[@id='addStepsModal'])]"));
    }

    private long participantSteps(String name) {
        for (WebElement row : Hooks.driver.findElements(PARTICIPANT_ROWS)) {
            if (row.findElement(By.cssSelector(".participant-name")).getText().trim().equals(name)) {
                return parseSteps(row.findElement(By.cssSelector(".participant-steps")));
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    private long parseSteps(WebElement stepElement) {
        return Long.parseLong(stepElement.getText().replaceAll("[^0-9]", ""));
    }

    private long parsedStepsToAdd() {
        return Long.parseLong(stepsToAdd);
    }

    private void addStepsThroughModal(String participant, String steps) {
        openAddStepsModal("Add Steps");
        selectParticipant(participant);
        enterStepsInField(steps);
        clickModalAddButton();
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(ADD_STEPS_MODAL));
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(driver -> participantSteps(participant)
                        == stepsBeforeAddition + parsedStepsToAdd());
        assertEquals("Added step count for " + participant,
                stepsBeforeAddition + parsedStepsToAdd(), participantSteps(participant));
    }

    private void clickModalAddButton() {
        waitForClickable(MODAL_ADD_BUTTON).click();
    }

    private boolean hasDefaultParticipantState() {
        if (Hooks.driver.findElements(PARTICIPANT_ROWS).size() != DEFAULT_RANKED_NAMES.size()) {
            return false;
        }
        for (int i = 0; i < DEFAULT_RANKED_NAMES.size(); i++) {
            if (participantSteps(DEFAULT_RANKED_NAMES.get(i)) != DEFAULT_RANKED_STEPS.get(i)) {
                return false;
            }
        }
        return true;
    }

    private void assertDefaultParticipantCounts() {
        assertEquals(DEFAULT_RANKED_NAMES.size(), Hooks.driver.findElements(PARTICIPANT_ROWS).size());
        for (int i = 0; i < DEFAULT_RANKED_NAMES.size(); i++) {
            assertEquals("Default step count for " + DEFAULT_RANKED_NAMES.get(i),
                    DEFAULT_RANKED_STEPS.get(i).longValue(),
                    participantSteps(DEFAULT_RANKED_NAMES.get(i)));
        }
    }

    private int participantRank(String participant) {
        List<WebElement> rows = Hooks.driver.findElements(PARTICIPANT_ROWS);
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).findElement(By.cssSelector(".participant-name")).getText().trim().equals(participant)) {
                return i + 1;
            }
        }
        throw new AssertionError("Participant not found in ranking: " + participant);
    }

    private void captureParticipantState(String selectedName) {
        new WebDriverWait(Hooks.driver, Duration.ofSeconds(10))
                .until(driver -> driver.findElements(PARTICIPANT_ROWS).size() == 10);
        List<WebElement> rows = Hooks.driver.findElements(PARTICIPANT_ROWS);
        participantStepsBeforeAddition.clear();
        for (WebElement row : rows) {
            String name = row.findElement(By.cssSelector(".participant-name")).getText().trim();
            participantStepsBeforeAddition.put(name,
                    parseSteps(row.findElement(By.cssSelector(".participant-steps"))));
        }
        assertTrue("Selected participant must be in the displayed ranking",
                participantStepsBeforeAddition.containsKey(selectedName));
        stepsBeforeAddition = participantStepsBeforeAddition.get(selectedName);
    }
}
