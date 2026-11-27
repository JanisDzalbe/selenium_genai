package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FitnessSampleSteps {
    private int mikeKidStepsBeforeUpdate;

    private WebDriverWait waitFor() {
        return new WebDriverWait(Hooks.driver, Duration.ofSeconds(10));
    }

    private List<WebElement> participantRows() {
        return Hooks.driver.findElements(By.cssSelector("#participantsList li"));
    }

    private String participantName(WebElement row) {
        return row.findElement(By.cssSelector(".participant-name")).getText();
    }

    private int participantSteps(WebElement row) {
        String text = row.findElement(By.cssSelector(".participant-steps")).getText();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    private WebElement findParticipantRow(String name) {
        for (WebElement row : participantRows()) {
            if (participantName(row).equals(name)) {
                return row;
            }
        }
        throw new AssertionError("Participant not found: " + name);
    }

    private void openAddStepsModal() {
        Hooks.driver.findElements(By.id("addStepsBtn")).get(0).click();
        waitFor().until(driver -> {
            WebElement modal = driver.findElement(By.id("addStepsModal"));
            return modal.isDisplayed() && "block".equals(modal.getCssValue("display"));
        });
    }

    private void assertModalElements() {
        waitFor().until(driver -> driver.findElement(By.id("addStepsModal")).isDisplayed());
        assertEquals("Add Steps to Participant",
                Hooks.driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
        assertNotNull(Hooks.driver.findElement(By.id("participant_select")));
        assertNotNull(Hooks.driver.findElement(By.id("steps_input")));
        assertNotNull(Hooks.driver.findElement(By.id("modal_add_steps_button")));
        assertEquals("Close Modal",
                Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"))
                        .getAttribute("title"));

        Select select = new Select(Hooks.driver.findElement(By.id("participant_select")));
        assertEquals("Choose participant", select.getFirstSelectedOption().getText());
    }

    @Given("^I am on the fitness challenge page \"([^\"]+)\"$")
    public void iAmOnTheFitnessChallengePage(String pageUrl) {
        Hooks.driver.get(pageUrl);
        waitFor().until(driver -> driver.findElements(
                By.cssSelector("#participantsList .participant-name")).size() == 10);

        JavascriptExecutor js = (JavascriptExecutor) Hooks.driver;
        js.executeScript(
                "Object.keys(localStorage).forEach(function(key) {" +
                        " if (key.startsWith('participant') || key === 'fitnessReload') " +
                        " localStorage.removeItem(key);" +
                        "});");
        Hooks.driver.navigate().refresh();
        waitFor().until(driver -> driver.findElements(
                By.cssSelector("#participantsList .participant-name")).size() == 10);
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
        waitFor().until(driver -> participantRows().size() == 10);
    }

    @When("I view the participant list")
    public void iViewTheParticipantList() {
        waitFor().until(driver -> participantRows().size() == 10);
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, Hooks.driver.findElement(By.cssSelector("h2")).getText());
        assertEquals(expectedTitle, Hooks.driver.getTitle());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        assertEquals(expectedCount, participantRows().size());
    }

    @Then("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(DataTable dataTable) {
        Map<String, Integer> expected = new LinkedHashMap<>();
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            expected.put(row.get("Name"),
                    Integer.parseInt(row.get("Steps").replace(",", "").trim()));
        }

        assertEquals(expected.size(), participantRows().size());
        for (WebElement row : participantRows()) {
            String name = participantName(row);
            assertTrue("Unexpected participant: " + name, expected.containsKey(name));
            assertEquals(expected.get(name).intValue(), participantSteps(row));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        String id = buttonText.equals("Add Steps") ? "addStepsBtn" : "resetBtn";
        List<WebElement> buttons = Hooks.driver.findElements(By.id(id));
        assertEquals(2, buttons.size());
        for (WebElement button : buttons) {
            assertEquals(buttonText, button.getText());
            assertTrue(button.isDisplayed());
        }
    }

    @Then("participants are displayed in descending order by step count")
    public void participantsAreDisplayedInDescendingOrderByStepCount() {
        List<WebElement> rows = participantRows();
        assertEquals(10, rows.size());
        for (int i = 1; i < rows.size(); i++) {
            assertTrue(participantSteps(rows.get(i - 1)) >= participantSteps(rows.get(i)));
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void eachParticipantStepCountIsGreaterThanOrEqualToTheParticipantBelowThem() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void firstPlaceDisplaysGoldTrophy() {
        WebElement trophy = participantRows().get(0).findElement(By.cssSelector(".fa-trophy"));
        assertTrue(trophy.getCssValue("color").contains("255"));
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void secondPlaceDisplaysSilverTrophy() {
        WebElement trophy = participantRows().get(1).findElement(By.cssSelector(".fa-trophy"));
        assertTrue(trophy.getCssValue("color").contains("192"));
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void thirdPlaceDisplaysBronzeTrophy() {
        WebElement trophy = participantRows().get(2).findElement(By.cssSelector(".fa-trophy"));
        assertTrue(trophy.getCssValue("color").contains("205"));
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participantsFourthAndBelowHaveNoTrophyIcons() {
        List<WebElement> rows = participantRows();
        for (int i = 3; i < rows.size(); i++) {
            assertTrue(rows.get(i).findElements(By.cssSelector(".fa-trophy")).isEmpty());
        }
    }

    @When("I click the {string} button at the top of the page")
    public void iClickTheAddStepsButtonAtTheTop(String buttonText) {
        assertEquals("Add Steps", buttonText);
        Hooks.driver.findElements(By.id("addStepsBtn")).get(0).click();
    }

    @When("I scroll to the bottom of the page")
    public void iScrollToTheBottomOfThePage() {
        ((JavascriptExecutor) Hooks.driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void iClickTheAddStepsButtonAtTheBottom(String buttonText) {
        assertEquals("Add Steps", buttonText);
        List<WebElement> buttons = Hooks.driver.findElements(By.id("addStepsBtn"));
        buttons.get(buttons.size() - 1).click();
    }

    @Then("a modal window appears with title {string}")
    public void aModalWindowAppearsWithTitle(String title) {
        assertModalElements();
        assertEquals(title,
                Hooks.driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void theModalContainsAParticipantDropdown() {
        assertNotNull(Hooks.driver.findElement(By.id("participant_select")));
    }

    @Then("the modal contains a number input field")
    public void theModalContainsANumberInputField() {
        assertNotNull(Hooks.driver.findElement(By.id("steps_input")));
        assertEquals("number",
                Hooks.driver.findElement(By.id("steps_input")).getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void theModalContainsSubmitButton(String buttonText) {
        WebElement button = Hooks.driver.findElement(By.id("modal_add_steps_button"));
        assertEquals(buttonText, button.getText());
    }

    @Then("^the modal has a close button .* in the top-right corner$")
    public void theModalHasACloseButton() {
        WebElement closeButton =
                Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        assertEquals("Close Modal", closeButton.getAttribute("title"));
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void theDropdownIsPrepopulatedWithAllTenParticipants() {
        Select select = new Select(Hooks.driver.findElement(By.id("participant_select")));
        assertEquals(11, select.getOptions().size());
    }

    @Then("the default dropdown text shows {string}")
    public void theDefaultDropdownTextShows(String defaultText) {
        Select select = new Select(Hooks.driver.findElement(By.id("participant_select")));
        assertEquals(defaultText, select.getFirstSelectedOption().getText());
    }

    @Then("all modal elements are present")
    public void allModalElementsArePresent() {
        assertModalElements();
        assertEquals(11, new Select(Hooks.driver.findElement(By.id("participant_select")))
                .getOptions().size());
    }

    @Given("I have opened the {string} modal")
    public void iHaveOpenedTheAddStepsModal(String modalButtonText) {
        assertEquals("Add Steps", modalButtonText);
        openAddStepsModal();
    }

    @When("^I click the .* \\(close\\) button in the top-right corner$")
    public void iClickTheCloseButton() {
        WebElement closeButton =
                Hooks.driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        ((JavascriptExecutor) Hooks.driver).executeScript("arguments[0].click();", closeButton);
    }

    @Then("the modal closes")
    public void theModalCloses() {
        waitFor().until(driver -> !driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Then("I return to the main page view")
    public void iReturnToTheMainPageView() {
        assertTrue(Hooks.driver.findElement(By.id("participantsList")).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void noDataIsChangedFromInitialState() {
        assertEquals(8500, participantSteps(findParticipantRow("Mike Kid")));
        assertEquals(15000, participantSteps(findParticipantRow("John Smith")));
        assertEquals(10, participantRows().size());
    }

    @Given("I note the current step count for {string}")
    public void iNoteTheCurrentStepCount(String participantName) {
        if (participantName.equals("Mike Kid")) {
            mikeKidStepsBeforeUpdate = participantSteps(findParticipantRow(participantName));
        }
    }

    @When("I open the {string} modal")
    public void iOpenTheModal(String modalButtonText) {
        assertEquals("Add Steps", modalButtonText);
        openAddStepsModal();
    }

    @When("I select {string} from the dropdown")
    public void iSelectParticipantFromTheDropdown(String participantName) {
        new Select(Hooks.driver.findElement(By.id("participant_select")))
                .selectByVisibleText(participantName);
    }

    @When("I select any participant")
    public void iSelectAnyParticipant() {
        new Select(Hooks.driver.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");
    }

    @When("I enter {string} in the steps input field")
    public void iEnterStepsInTheInputField(String steps) {
        Hooks.driver.findElement(By.id("steps_input")).sendKeys(steps);
    }

    @When("I click {string} button")
    public void iClickButton(String buttonText) {
        assertEquals("Add Steps", buttonText);
        Hooks.driver.findElement(By.id("modal_add_steps_button")).click();
    }

    @Then("the modal closes automatically")
    public void theModalClosesAutomatically() {
        theModalCloses();
    }

    @Then("the participant list refreshes")
    public void theParticipantListRefreshes() {
        waitFor().until(driver -> participantRows().size() == 10);
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mikeKidStepCountIncreasesByOneThousand() {
        assertEquals(mikeKidStepsBeforeUpdate + 1000,
                participantSteps(findParticipantRow("Mike Kid")));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void theListResortsIfMikeKidRankingChanges() {
        participantsAreDisplayedInDescendingOrderByStepCount();
    }

    @Then("an alert appears: {string}")
    public void anAlertAppears(String expectedText) {
        org.openqa.selenium.Alert alert =
                waitFor().until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
    }

    @When("I accept the alert")
    public void iAcceptTheAlert() {
        Hooks.driver.switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void theModalRemainsOpen() {
        assertTrue(Hooks.driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void theStepCountUpdatesCorrectlyWithTheLargeNumber() {
        assertEquals(1008499, participantSteps(findParticipantRow("Mike Kid")));
    }

    @Then("the participant moves to first place")
    public void theParticipantMovesToFirstPlace() {
        assertEquals("Mike Kid", participantName(participantRows().get(0)));
    }
}
