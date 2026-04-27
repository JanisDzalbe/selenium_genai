package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SampleSteps {

    private int notedSteps;
    private String selectedParticipant;
    private int previousSteps;
    private String enteredSteps;

    WebDriver driver;
    WebDriverWait wait;

    public SampleSteps() {
        this.driver = Hooks.driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @Given("I am on the fitness challenge page {string}")
    public void i_am_on_the_fitness_challenge_page(String url) {
        driver.get(url);
        ((JavascriptExecutor) driver).executeScript("localStorage.clear();");
        driver.navigate().refresh();

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10
        ));
    }

    @When("the page loads for the first time")
    public void the_page_loads_for_the_first_time() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("participantsList")));
    }

    @Then("the page displays title {string}")
    public void the_page_displays_title(String expectedTitle) {
        assertEquals(expectedTitle, driver.findElement(By.tagName("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void participants_are_displayed_in_the_list(Integer count) {
        assertEquals(count.intValue(), getParticipants().size());
    }

    @Then("the default participants are displayed:")
    public void the_default_participants_are_displayed(DataTable table) {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            String name = row.get("Name").trim();
            String expectedSteps = row.get("Steps").replace(",", "").trim();
            assertEquals(expectedSteps, String.valueOf(getSteps(findParticipant(name))));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttons_are_visible_at_top_and_bottom(String text) {
        List<WebElement> buttons;

        if (text.equals("Add Steps")) {
            buttons = driver.findElements(By.id("addStepsBtn"));
        } else if (text.equals("Reset List")) {
            buttons = driver.findElements(By.id("resetBtn"));
        } else {
            buttons = driver.findElements(By.xpath("//button[normalize-space()='" + text + "']"));
        }

        assertEquals(2, buttons.size());
        assertTrue(buttons.get(0).isDisplayed());
        assertTrue(buttons.get(1).isDisplayed());
    }

    @When("I view the participant list")
    public void i_view_the_participant_list() {
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10
        ));
    }

    @Then("participants are displayed in descending order by step count")
    public void participants_are_displayed_in_descending_order_by_step_count() {
        List<WebElement> participants = getParticipants();

        for (int i = 0; i < participants.size() - 1; i++) {
            assertTrue(getSteps(participants.get(i)) >= getSteps(participants.get(i + 1)));
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void each_participant_step_count_is_greater_than_or_equal_to_below() {
        participants_are_displayed_in_descending_order_by_step_count();
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void the_1st_place_participant_displays_a_gold_trophy_icon() {
        String color = getParticipants().get(0).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color");
        assertEquals("rgba(255, 215, 0, 1)", color);
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void the_2nd_place_participant_displays_a_silver_trophy_icon() {
        String color = getParticipants().get(1).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color");
        assertEquals("rgba(192, 192, 192, 1)", color);
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void the_3rd_place_participant_displays_a_bronze_trophy_icon() {
        String color = getParticipants().get(2).findElement(By.cssSelector("i.fa-trophy")).getCssValue("color");
        assertEquals("rgba(205, 127, 50, 1)", color);
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participants_ranked_4th_and_below_have_no_trophy_icons() {
        List<WebElement> participants = getParticipants();

        for (int i = 3; i < participants.size(); i++) {
            assertEquals(0, participants.get(i).findElements(By.cssSelector("i.fa-trophy")).size());
        }
    }

    @When("I click the {string} button at the top of the page")
    public void i_click_the_button_at_the_top_of_the_page(String buttonText) {
        if (buttonText.equals("Reset List")) {
            driver.findElements(By.id("resetBtn")).get(0).click();
        } else if (buttonText.equals("Add Steps")) {
            driver.findElements(By.id("addStepsBtn")).get(0).click();
        }
    }

    @When("I click the {string} button at the bottom")
    public void i_click_the_button_at_the_bottom(String buttonText) {
        if (buttonText.equals("Reset List")) {
            driver.findElements(By.id("resetBtn")).get(1).click();
        } else if (buttonText.equals("Add Steps")) {
            driver.findElements(By.id("addStepsBtn")).get(1).click();
        }
    }

    @Then("a modal window appears with title {string}")
    public void a_modal_window_appears_with_title(String expectedTitle) {
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(modal.isDisplayed());
        assertEquals(expectedTitle, driver.findElement(By.cssSelector("#addStepsModal h3")).getText());
    }

    @Then("the modal contains a participant dropdown")
    public void the_modal_contains_a_participant_dropdown() {
        assertTrue(driver.findElement(By.id("participant_select")).isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void the_modal_contains_a_number_input_field() {
        assertTrue(driver.findElement(By.id("steps_input")).isDisplayed());
    }

    @Then("the modal contains {string} submit button")
    public void the_modal_contains_submit_button(String buttonText) {
        WebElement button = driver.findElement(By.id("modal_add_steps_button"));
        assertTrue(button.isDisplayed());
        assertEquals(buttonText, button.getText());
    }

    @Then("the modal has a close button \\(×) in the top-right corner")
    public void the_modal_has_a_close_button_in_the_top_right_corner() {
        assertTrue(driver.findElement(By.cssSelector(".w3-closebtn")).isDisplayed());
    }

    @Then("the dropdown is prepopulated with all {int} participants")
    public void the_dropdown_is_prepopulated_with_all_participants(Integer count) {
        Select dropdown = new Select(driver.findElement(By.id("participant_select")));
        assertEquals(count + 1, dropdown.getOptions().size());
    }

    @Then("the default dropdown text shows {string}")
    public void the_default_dropdown_text_shows(String expectedText) {
        Select dropdown = new Select(driver.findElement(By.id("participant_select")));
        assertEquals(expectedText, dropdown.getFirstSelectedOption().getText());
    }

    @When("I scroll to the bottom of the page")
    public void i_scroll_to_the_bottom_of_the_page() {
        scrollToBottom();
    }

    @When("I scroll to bottom of page")
    public void i_scroll_to_bottom_of_page() {
        scrollToBottom();
    }

    @Then("all modal elements are present")
    public void all_modal_elements_are_present() {
        the_modal_contains_a_participant_dropdown();
        the_modal_contains_a_number_input_field();
        the_modal_contains_submit_button("Add Steps");
        the_modal_has_a_close_button_in_the_top_right_corner();
    }

    @Given("I have opened the {string} modal")
    public void i_have_opened_the_modal(String modalName) {
        openAddStepsModal();
    }

    @When("I click the × \\(close) button in the top-right corner")
    public void i_click_the_close_button_in_the_top_right_corner() {
        driver.findElement(By.cssSelector(".w3-closebtn")).click();
    }

    @Then("the modal closes")
    public void the_modal_closes() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Then("I return to the main page view")
    public void i_return_to_the_main_page_view() {
        assertTrue(driver.findElement(By.id("participantsList")).isDisplayed());
    }

    @Then("no data is changed from initial state")
    public void no_data_is_changed_from_initial_state() {
        assertEquals(10, getParticipants().size());
        assertEquals(15000, getSteps(findParticipant("John Smith")));
        assertEquals(13500, getSteps(findParticipant("David Brown")));
        assertEquals(12000, getSteps(findParticipant("Jill Watson")));
    }

    @Given("I note the current step count for {string}")
    public void i_note_the_current_step_count_for(String name) {
        selectedParticipant = name;
        notedSteps = getSteps(findParticipant(name));
    }

    @When("I open the {string} modal")
    public void i_open_the_modal(String modalName) {
        openAddStepsModal();
    }

    @When("I select {string} from the dropdown")
    public void i_select_from_the_dropdown(String name) {
        selectedParticipant = name;
        Select dropdown = new Select(driver.findElement(By.id("participant_select")));
        dropdown.selectByVisibleText(name);
    }

    @When("I select any participant")
    public void i_select_any_participant() {
        selectedParticipant = "Mike Kid";
        Select dropdown = new Select(driver.findElement(By.id("participant_select")));
        dropdown.selectByVisibleText(selectedParticipant);
    }

    @When("I select a participant")
    public void i_select_a_participant() {
        selectedParticipant = "Mike Kid";
        Select dropdown = new Select(driver.findElement(By.id("participant_select")));
        dropdown.selectByVisibleText(selectedParticipant);
    }

    @When("I enter {string} in the steps input field")
    public void i_enter_in_the_steps_input_field(String steps) {
        WebElement input = driver.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(steps);
    }

    @When("I enter {string} in the steps input")
    public void i_enter_in_the_steps_input(String steps) {
        i_enter_in_the_steps_input_field(steps);
    }

    @When("I click {string} button")
    public void i_click_button(String buttonText) {
        if (buttonText.equals("Add Steps")) {
            driver.findElement(By.id("modal_add_steps_button")).click();
        }
    }

    @When("I attempt to click {string} button")
    public void i_attempt_to_click_button(String buttonText) {
        i_click_button(buttonText);
    }

    @Then("the modal closes automatically")
    public void the_modal_closes_automatically() {
        the_modal_closes();
    }

    @Then("the participant list refreshes")
    public void the_participant_list_refreshes() {
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10
        ));
    }

    @Then("Mike Kid's step count increases by 1,000")
    public void mike_kids_step_count_increases_by_1000() {
        assertEquals(notedSteps + 1000, getSteps(findParticipant("Mike Kid")));
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void the_list_re_sorts_if_mike_kids_new_total_changes_his_ranking() {
        participants_are_displayed_in_descending_order_by_step_count();
    }

    @Then("an alert appears: {string}")
    public void an_alert_appears(String expectedText) {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals(expectedText, alert.getText());
    }

    @When("I accept the alert")
    public void i_accept_the_alert() {
        driver.switchTo().alert().accept();
    }

    @Then("the modal remains open")
    public void the_modal_remains_open() {
        assertTrue(driver.findElement(By.id("addStepsModal")).isDisplayed());
    }

    @Then("the step count updates correctly with the large number")
    public void the_step_count_updates_correctly_with_the_large_number() {
        int newSteps = getSteps(findParticipant(selectedParticipant));
        assertTrue(newSteps >= 999999);
    }

    @Then("the participant moves to first place")
    public void the_participant_moves_to_first_place() {
        assertEquals(selectedParticipant, getParticipantName(getParticipants().get(0)));
    }

    @When("I leave the participant dropdown at {string}")
    public void i_leave_the_participant_dropdown_at(String expectedText) {
        Select dropdown = new Select(driver.findElement(By.id("participant_select")));
        assertEquals(expectedText, dropdown.getFirstSelectedOption().getText());
    }

    @When("I attempt to enter {string} in the steps input field")
    public void i_attempt_to_enter_in_the_steps_input_field(String value) {
        WebElement input = driver.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(value);
    }

    @Then("the input field rejects non-numeric characters")
    public void the_input_field_rejects_non_numeric_characters() {
        String value = driver.findElement(By.id("steps_input")).getAttribute("value");
        assertEquals("", value);
    }

    @Then("no alphabetic characters appear in the input")
    public void no_alphabetic_characters_appear_in_the_input() {
        String value = driver.findElement(By.id("steps_input")).getAttribute("value");
        assertFalse(value.matches(".*[a-zA-Z].*"));
    }

    @Given("I have added steps to at least {int} participants to modify the default state")
    public void i_have_added_steps_to_at_least_participants_to_modify_the_default_state(Integer count) {
        addStepsToParticipant("Mike Kid", "1000");
        addStepsToParticipant("Jane Doe", "2000");
    }

    @Given("I have modified participant data")
    public void i_have_modified_participant_data() {
        addStepsToParticipant("Mike Kid", "1000");
    }

    @When("I wait for page reload")
    public void i_wait_for_page_reload() {
        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10
        ));
    }

    @Then("all participants return to their default step counts")
    public void all_participants_return_to_their_default_step_counts() {
        assertEquals(8500, getSteps(findParticipant("Mike Kid")));
        assertEquals(12000, getSteps(findParticipant("Jill Watson")));
        assertEquals(6500, getSteps(findParticipant("Jane Doe")));
        assertEquals(15000, getSteps(findParticipant("John Smith")));
        assertEquals(9800, getSteps(findParticipant("Sarah Johnson")));
        assertEquals(11200, getSteps(findParticipant("Carlos Garcia")));
        assertEquals(7300, getSteps(findParticipant("Emily Chen")));
        assertEquals(13500, getSteps(findParticipant("David Brown")));
        assertEquals(10500, getSteps(findParticipant("Maria Rodriguez")));
        assertEquals(8900, getSteps(findParticipant("Alex Taylor")));
    }

    @Then("the default ranking order is restored")
    public void the_default_ranking_order_is_restored() {
        List<WebElement> participants = getParticipants();

        assertEquals("John Smith", getParticipantName(participants.get(0)));
        assertEquals("David Brown", getParticipantName(participants.get(1)));
        assertEquals("Jill Watson", getParticipantName(participants.get(2)));
    }

    @Then("trophy icons display for correct default top 3")
    public void trophy_icons_display_for_correct_default_top_3() {
        the_1st_place_participant_displays_a_gold_trophy_icon();
        the_2nd_place_participant_displays_a_silver_trophy_icon();
        the_3rd_place_participant_displays_a_bronze_trophy_icon();
    }

    // ---------------- Helper methods ----------------

    private List<WebElement> getParticipants() {
        return driver.findElements(By.cssSelector("#participantsList li"));
    }

    private WebElement findParticipant(String name) {
        for (WebElement participant : getParticipants()) {
            String actualName = participant.findElement(By.cssSelector(".participant-name")).getText();

            if (actualName.equals(name)) {
                return participant;
            }
        }

        throw new NoSuchElementException("Participant not found: " + name);
    }

    private int getSteps(WebElement participant) {
        String stepsText = participant
                .findElement(By.cssSelector(".participant-steps"))
                .getText()
                .replaceAll("[^0-9]", "");

        return Integer.parseInt(stepsText);
    }

    private String getParticipantName(WebElement participant) {
        return participant.findElement(By.cssSelector(".participant-name")).getText();
    }

    private void openAddStepsModal() {
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void addStepsToParticipant(String name, String steps) {
        openAddStepsModal();

        Select dropdown = new Select(driver.findElement(By.id("participant_select")));
        dropdown.selectByVisibleText(name);

        WebElement input = driver.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(steps);

        driver.findElement(By.id("modal_add_steps_button")).click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    private void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    private long getStepsLong(WebElement participant) {
        String stepsText = participant
                .findElement(By.cssSelector(".participant-steps"))
                .getText()
                .replaceAll("[^0-9]", "");

        return Long.parseLong(stepsText);
    }

    @When("I enter the maximum safe integer {string}")
    public void i_enter_the_maximum_safe_integer(String steps) {
        enteredSteps = steps;
        previousSteps = getSteps(findParticipant(selectedParticipant));

        WebElement input = driver.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(steps);
    }

    @When("I submit the form")
    public void i_submit_the_form() {
        driver.findElement(By.id("modal_add_steps_button")).click();
    }

    @Then("the system accepts the value")
    public void the_system_accepts_the_value() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Then("the step count updates correctly")
    public void the_step_count_updates_correctly() {
        long actual = getStepsLong(findParticipant(selectedParticipant));

        assertTrue(actual >= Long.parseLong(enteredSteps));
    }

    @Then("the step count updates after decimal value")
    public void the_step_count_updates_after_decimal_value() {
        assertTrue(getSteps(findParticipant(selectedParticipant)) > 8500);
    }
}