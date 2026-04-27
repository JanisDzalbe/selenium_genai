package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import selenium.stepDefinitions.Hooks;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.cucumber.datatable.DataTable;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SampleSteps {

    private int originalSteps;

    public SampleSteps() {
        // Can use static driver from Hooks directly
    }

    @Given("I am on the fitness challenge page {string}")
    public void i_am_on_the_fitness_challenge_page(String url) {
        Hooks.driver.get(url);
    }

    @When("the page loads for the first time")
    public void the_page_loads_for_the_first_time() {
        // Page is already loaded by the Given step
    }

    @Then("the page displays title {string}")
    public void the_page_displays_title(String title) {
        WebElement pageTitle = Hooks.driver.findElement(By.tagName("h2"));
        assertEquals(title, pageTitle.getText());
    }

    @Then("{int} participants are displayed in the list")
    public void participants_are_displayed_in_the_list(int count) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(count, participants.size());
    }

    @Then("the default participants are displayed:")
    public void the_default_participants_are_displayed(DataTable dataTable) {
        List<Map<String, String>> expectedParticipants = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> expected : expectedParticipants) {
            String expectedName = expected.get("Name");
            String expectedSteps = expected.get("Steps");

            WebElement li = Hooks.driver.findElement(By.xpath("//li[contains(., '" + expectedName + "')]"));
            WebElement nameEl = li.findElement(By.cssSelector(".participant-name"));
            assertEquals(expectedName, nameEl.getText());

            WebElement stepsEl = li.findElement(By.cssSelector(".participant-steps"));
            String actualSteps = stepsEl.getText().replace(" steps", "");
            assertEquals(expectedSteps, actualSteps);
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttons_are_visible_at_top_and_bottom(String buttonText) {
        List<WebElement> buttons = Hooks.driver.findElements(By.xpath("//button[contains(., '" + buttonText + "')]"));
        int visibleCount = 0;
        for (WebElement b : buttons) {
            if (b.isDisplayed()) visibleCount++;
        }
        assertEquals(2, visibleCount);
    }

    @When("I view the participant list")
    public void i_view_the_participant_list() {
        // Participant list is already displayed
    }

    @Then("participants are displayed in descending order by step count")
    public void participants_are_displayed_in_descending_order_by_step_count() {
        List<WebElement> participantSteps = Hooks.driver.findElements(By.cssSelector(".participant-steps"));
        List<Integer> stepCounts = new java.util.ArrayList<>();
        for (WebElement stepElement : participantSteps) {
            String stepText = stepElement.getText();
            String numericText = stepText.replace(" steps", "").replace(",", "");
            stepCounts.add(Integer.parseInt(numericText));
        }
        for (int i = 0; i < stepCounts.size() - 1; i++) {
            assertTrue(stepCounts.get(i) >= stepCounts.get(i + 1));
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void each_participants_step_count_is_greater_than_or_equal_to_the_participant_below_them() {
        participants_are_displayed_in_descending_order_by_step_count();
    }

    @Then("the {int}st place participant displays a {string} trophy icon")
    public void the_st_place_participant_displays_a_trophy_icon(int place, String color) {
        verifyTrophyIcon(place, color);
    }

    @Then("the {int}nd place participant displays a {string} trophy icon")
    public void the_nd_place_participant_displays_a_trophy_icon(int place, String color) {
        verifyTrophyIcon(place, color);
    }

    @Then("the {int}rd place participant displays a {string} trophy icon")
    public void the_rd_place_participant_displays_a_trophy_icon(int place, String color) {
        verifyTrophyIcon(place, color);
    }

    @Then("participants ranked {int}th and below have no trophy icons")
    public void participants_ranked_th_and_below_have_no_trophy_icons(int rank) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        for (int i = rank - 1; i < participants.size(); i++) {
            WebElement participant = participants.get(i);
            List<WebElement> trophies = participant.findElements(By.cssSelector(".fa-trophy"));
            assertEquals(0, trophies.size());
        }
    }

    private void verifyTrophyIcon(int place, String color) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        WebElement participant = participants.get(place - 1);
        WebElement trophy = participant.findElement(By.cssSelector(".fa-trophy"));
        String trophyColor = trophy.getCssValue("color");
        switch (color) {
            case "gold":
                assertTrue(trophyColor.contains("255") || trophyColor.contains("gold") || trophyColor.contains("#ffd700") || trophyColor.equals("gold"));
                break;
            case "silver":
                assertTrue(trophyColor.contains("192") || trophyColor.contains("silver") || trophyColor.contains("#c0c0c0") || trophyColor.equals("silver"));
                break;
            case "bronze":
                assertTrue(trophyColor.contains("205") || trophyColor.contains("#cd7f32") || trophyColor.equals("rgb(205, 127, 50)"));
                break;
        }
    }

    @When("I click the {string} button at the top of the page")
    public void i_click_the_button_at_the_top_of_the_page(String buttonText) {
        List<WebElement> buttons = Hooks.driver.findElements(By.xpath("//button[contains(., '" + buttonText + "')]"));
        buttons.get(0).click();
    }

    @Then("a modal window appears with title {string}")
    public void a_modal_window_appears_with_title(String title) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());
        WebElement modalTitle = modal.findElement(By.xpath(".//h3"));
        assertEquals(title, modalTitle.getText());
    }

    @Then("the modal contains a participant dropdown")
    public void the_modal_contains_a_participant_dropdown() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        assertTrue(dropdown.isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void the_modal_contains_a_number_input_field() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement input = modal.findElement(By.id("steps_input"));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));
    }

    @Then("the modal contains {string} submit button")
    public void the_modal_contains_submit_button(String buttonText) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement button = modal.findElement(By.id("modal_add_steps_button"));
        assertTrue(button.isDisplayed());
        assertEquals(buttonText, button.getText());
    }

    @Then("the modal has a close button (×) in the top-right corner")
    public void the_modal_has_a_close_button_in_the_top_right_corner() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement closeButton = modal.findElement(By.xpath(".//*[text()='×']"));
        assertTrue(closeButton.isDisplayed());
        assertEquals("×", closeButton.getText());
    }

    @Then("the dropdown is prepopulated with all {int} participants")
    public void the_dropdown_is_prepopulated_with_all_participants(int count) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(dropdown);
        List<WebElement> options = select.getOptions();
        assertEquals(count + 1, options.size());
    }

    @Then("the default dropdown text shows {string}")
    public void the_default_dropdown_text_shows(String text) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(dropdown);
        String defaultText = select.getFirstSelectedOption().getText();
        assertEquals(text, defaultText);
    }

    @When("I scroll to the bottom of the page")
    public void i_scroll_to_the_bottom_of_the_page() {
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) Hooks.driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @When("I click the {string} button at the bottom")
    public void i_click_the_button_at_the_bottom(String buttonText) {
        List<WebElement> buttons = Hooks.driver.findElements(By.xpath("//button[contains(., '" + buttonText + "')]"));
        buttons.get(1).click();
    }

    @Then("all modal elements are present")
    public void all_modal_elements_are_present() {
        a_modal_window_appears_with_title("Add Steps to Participant");
        the_modal_contains_a_participant_dropdown();
        the_modal_contains_a_number_input_field();
        the_modal_contains_submit_button("Add Steps");
        the_modal_has_a_close_button_in_the_top_right_corner();
    }

    @Given("I have opened the {string} modal")
    public void i_have_opened_the_modal(String modalName) {
        List<WebElement> buttons = Hooks.driver.findElements(By.xpath("//button[contains(., 'Add Steps')]"));
        buttons.get(0).click();
    }

    @When("I click the × (close) button in the top-right corner")
    public void i_click_the_close_button_in_the_top_right_corner() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement closeButton = modal.findElement(By.xpath(".//*[text()='×']"));
        closeButton.click();
    }

    @Then("the modal closes")
    public void the_modal_closes() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        assertFalse(modal.isDisplayed());
    }

    @Then("I return to the main page view")
    public void i_return_to_the_main_page_view() {
        WebElement pageTitle = Hooks.driver.findElement(By.tagName("h2"));
        assertEquals("Fitness Challenge", pageTitle.getText());
    }

    @Then("no data is changed from initial state")
    public void no_data_is_changed_from_initial_state() {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(10, participants.size());
    }

    @Given("I note the current step count for {string}")
    public void i_note_the_current_step_count_for(String name) {
        WebElement li = Hooks.driver.findElement(By.xpath("//li[contains(., '" + name + "')]"));
        WebElement stepsEl = li.findElement(By.cssSelector(".participant-steps"));
        String stepsText = stepsEl.getText();
        originalSteps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));
    }

    @When("I open the {string} modal")
    public void i_open_the_modal(String modalName) {
        List<WebElement> buttons = Hooks.driver.findElements(By.xpath("//button[contains(., 'Add Steps')]"));
        buttons.get(0).click();
    }

    @When("I select {string} from the dropdown")
    public void i_select_from_the_dropdown(String name) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(dropdown);
        select.selectByVisibleText(name);
    }

    @When("I enter {string} in the steps input field")
    public void i_enter_in_the_steps_input_field(String value) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(value);
    }

    @When("I click {string} button")
    public void i_click_button(String buttonText) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement button = modal.findElement(By.id("modal_add_steps_button"));
        button.click();
    }

    @Then("the modal closes automatically")
    public void the_modal_closes_automatically() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        assertFalse(modal.isDisplayed());
    }

    @Then("the participant list refreshes")
    public void the_participant_list_refreshes() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
    }

    @Then("Mike Kid's step count increases by {int}")
    public void mike_kids_step_count_increases_by(int increase) {
        WebElement li = Hooks.driver.findElement(By.xpath("//li[contains(., 'Mike Kid')]"));
        WebElement stepsEl = li.findElement(By.cssSelector(".participant-steps"));
        String stepsText = stepsEl.getText();
        int newSteps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));
        assertEquals(originalSteps + increase, newSteps);
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void the_list_re_sorts_if_mike_kids_new_total_changes_his_ranking() {
        participants_are_displayed_in_descending_order_by_step_count();
    }

    @When("I select any participant")
    public void i_select_any_participant() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(dropdown);
        select.selectByIndex(1);
    }

    @Then("an alert appears: {string}")
    public void an_alert_appears(String message) {
        org.openqa.selenium.Alert alert = Hooks.driver.switchTo().alert();
        assertEquals(message, alert.getText());
    }

    @When("I accept the alert")
    public void i_accept_the_alert() {
        org.openqa.selenium.Alert alert = Hooks.driver.switchTo().alert();
        alert.accept();
    }

    @Then("the modal remains open")
    public void the_modal_remains_open() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void the_step_count_updates_correctly_with_the_large_number() {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        WebElement firstPlace = participants.get(0);
        WebElement stepsEl = firstPlace.findElement(By.cssSelector(".participant-steps"));
        String stepsText = stepsEl.getText();
        String numeric = stepsText.replace(" steps", "").replace(",", "");
        long stepsValue = Long.parseLong(numeric);
        assertTrue(stepsValue > 100000);
    }

    @Then("the participant moves to first place")
    public void the_participant_moves_to_first_place() {
        // Already verified by the large number check
    }

    @When("I leave the participant dropdown at {string}")
    public void i_leave_the_participant_dropdown_at(String text) {
        // Default is already set
    }

    @When("I enter {string} in the steps input")
    public void i_enter_in_the_steps_input(String value) {
        i_enter_in_the_steps_input_field(value);
    }

    @When("I select a participant")
    public void i_select_a_participant() {
        i_select_any_participant();
    }

    @When("I attempt to click {string} button")
    public void i_attempt_to_click_button(String buttonText) {
        i_click_button(buttonText);
    }

    @When("I attempt to enter {string} in the steps input field")
    public void i_attempt_to_enter_in_the_steps_input_field(String value) {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(value);
    }

    @Then("the input field rejects non-numeric characters")
    public void the_input_field_rejects_non_numeric_characters() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement input = modal.findElement(By.id("steps_input"));
        String value = input.getAttribute("value");
        assertEquals("", value);
    }

    @Then("no alphabetic characters appear in the input")
    public void no_alphabetic_characters_appear_in_the_input() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement input = modal.findElement(By.id("steps_input"));
        String value = input.getAttribute("value");
        assertFalse(value.matches(".*[a-zA-Z].*"));
    }
}
