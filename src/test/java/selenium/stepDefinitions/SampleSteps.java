package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.Color;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.utils.DriverManager;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class SampleSteps {

    private WebDriverWait wait;
    private String trackedParticipant;
    private long trackedBeforeSteps;
    private String lastEnteredSteps;
    private Map<String, Long> baselineState = new LinkedHashMap<>();

    private static final LinkedHashMap<String, Long> DEFAULTS = new LinkedHashMap<>();

    static {
        DEFAULTS.put("Mike Kid", 8500L);
        DEFAULTS.put("Jill Watson", 12000L);
        DEFAULTS.put("Jane Doe", 6500L);
        DEFAULTS.put("John Smith", 15000L);
        DEFAULTS.put("Sarah Johnson", 9800L);
        DEFAULTS.put("Carlos Garcia", 11200L);
        DEFAULTS.put("Emily Chen", 7300L);
        DEFAULTS.put("David Brown", 13500L);
        DEFAULTS.put("Maria Rodriguez", 10500L);
        DEFAULTS.put("Alex Taylor", 8900L);
    }

    private WebDriver driver() {
        return Hooks.driver;
    }

    private WebDriverWait waiter() {
        if (wait == null) {
            wait = new WebDriverWait(driver(), Duration.ofSeconds(8));
        }
        return wait;
    }

    // ---------- Background ----------

    @Given("I am on the fitness challenge page {string}")
    public void i_am_on_the_fitness_challenge_page(String url) {
        driver().navigate().to(url);
        waiter().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
    }

    // ---------- Feature 1 ----------

    @When("the page loads for the first time")
    public void the_page_loads_for_the_first_time() {
        waiter().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
    }

    @Then("the page displays title {string}")
    public void the_page_displays_title(String title) {
        assertTrue(driver().findElement(By.tagName("body")).getText().contains(title));
    }

    @Then("{int} participants are displayed in the list")
    public void participants_are_displayed_in_the_list(Integer expectedCount) {
        assertEquals(expectedCount.intValue(), getParticipantRows().size());
    }

    @Then("the default participants are displayed:")
    public void the_default_participants_are_displayed(DataTable table) {
        Map<String, Long> expected = new LinkedHashMap<>();
        for (Map<String, String> row : table.asMaps(String.class, String.class)) {
            expected.put(row.get("Name").trim(), parseNumber(row.get("Steps")));
        }

        Map<String, Long> actual = getNameStepsMap();
        for (Map.Entry<String, Long> e : expected.entrySet()) {
            assertTrue("Missing participant: " + e.getKey(), actual.containsKey(e.getKey()));
            assertEquals("Wrong steps for " + e.getKey(), e.getValue(), actual.get(e.getKey()));
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttons_are_visible_at_top_and_bottom(String label) {
        List<WebElement> buttons = getPageButtonsByText(label);
        assertTrue("Expected at least 2 buttons for " + label, buttons.size() >= 2);
        assertTrue(buttons.get(0).isDisplayed());
        assertTrue(buttons.get(buttons.size() - 1).isDisplayed());
    }

    // ---------- Feature 2 ----------

    @When("I view the participant list")
    public void i_view_the_participant_list() {
        assertFalse(getParticipantRows().isEmpty());
    }

    @Then("participants are displayed in descending order by step count")
    public void participants_are_displayed_in_descending_order_by_step_count() {
        List<ParticipantRow> rows = getParticipantRows();
        for (int i = 0; i < rows.size() - 1; i++) {
            assertTrue(rows.get(i).steps >= rows.get(i + 1).steps);
        }
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void each_participant_s_step_count_is_greater_than_or_equal_to_the_participant_below_them() {
        participants_are_displayed_in_descending_order_by_step_count();
    }

    @Then("the 1st place participant displays a gold trophy icon")
    public void the_1st_place_participant_displays_a_gold_trophy_icon() {
        ParticipantRow first = getParticipantRows().get(0);
        assertTrue(hasTrophy(first.element));
        assertTrue(hasColor(first.element, 255, 215, 0) || first.element.getText().contains("🥇"));
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void the_2nd_place_participant_displays_a_silver_trophy_icon() {
        ParticipantRow second = getParticipantRows().get(1);
        assertTrue(hasTrophy(second.element));
        assertTrue(hasColor(second.element, 192, 192, 192) || second.element.getText().contains("🥈"));
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void the_3rd_place_participant_displays_a_bronze_trophy_icon() {
        ParticipantRow third = getParticipantRows().get(2);
        assertTrue(hasTrophy(third.element));
        assertTrue(hasColor(third.element, 205, 127, 50) || third.element.getText().contains("🥉"));
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void participants_ranked_4th_and_below_have_no_trophy_icons() {
        List<ParticipantRow> rows = getParticipantRows();
        for (int i = 3; i < rows.size(); i++) {
            assertFalse("Rank " + (i + 1) + " should not have trophy", hasTrophy(rows.get(i).element));
        }
    }

    // ---------- Feature 3 ----------

    @When("I click the {string} button at the top of the page")
    public void i_click_the_button_at_the_top_of_the_page(String label) {
        List<WebElement> buttons = getPageButtonsByText(label);
        assertFalse(buttons.isEmpty());
        click(buttons.get(0));
    }

    @When("I scroll to the bottom of the page")
    public void i_scroll_to_the_bottom_of_the_page() {
        ((JavascriptExecutor) driver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    @When("I click the {string} button at the bottom")
    public void i_click_the_button_at_the_bottom(String label) {
        List<WebElement> buttons = getPageButtonsByText(label);
        assertTrue(buttons.size() >= 2);
        click(buttons.get(buttons.size() - 1));
    }

    @Then("a modal window appears with title {string}")
    public void a_modal_window_appears_with_title(String title) {
        assertTrue("Add Steps modal/form should be visible", waitForModalOpen()); // fixed order
        String pageText = driver().findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains(title));
    }

    @Then("the modal contains a participant dropdown")
    public void the_modal_contains_a_participant_dropdown() {
        assertNotNull(getVisibleParticipantSelect());
    }

    @Then("the modal contains a number input field")
    public void the_modal_contains_a_number_input_field() {
        assertNotNull(getVisibleStepsInput());
    }

    @Then("the modal contains {string} submit button")
    public void the_modal_contains_submit_button(String text) {
        WebElement btn = getVisibleModalAddStepsSubmitButton();
        assertNotNull(btn);
        assertTrue(btn.getText().trim().contains(text));
    }

    @Then("the modal has a close button (×) in the top-right corner")
    public void the_modal_has_a_close_button_in_the_top_right_corner() {
        assertNotNull(getVisibleModalCloseButton());
    }

    @Then("the dropdown is prepopulated with all {int} participants")
    public void the_dropdown_is_prepopulated_with_all_participants(Integer count) {
        Select s = new Select(getVisibleParticipantSelect());
        assertEquals(count + 1, s.getOptions().size());
    }

    @Then("the default dropdown text shows {string}")
    public void the_default_dropdown_text_shows(String expected) {
        Select s = new Select(getVisibleParticipantSelect());
        assertEquals(expected, s.getFirstSelectedOption().getText().trim());
    }

    @Then("all modal elements are present")
    public void all_modal_elements_are_present() {
        the_modal_contains_a_participant_dropdown();
        the_modal_contains_a_number_input_field();
        the_modal_contains_submit_button("Add Steps");
        the_modal_has_a_close_button_in_the_top_right_corner();
    }

    @Given("I have opened the {string} modal")
    public void i_have_opened_the_modal(String ignored) {
        baselineState = getNameStepsMap();
        openAddStepsModal();
        assertTrue(waitForModalOpen());
    }

    @When("I click the × (close) button in the top-right corner")
    public void i_click_the_close_button_in_the_top_right_corner() {
        click(getVisibleModalCloseButton());
    }

    @Then("the modal closes")
    public void the_modal_closes() {
        waiter().until((ExpectedCondition<Boolean>) d -> !isModalOpen());
    }

    @Then("I return to the main page view")
    public void i_return_to_the_main_page_view() {
        assertTrue(driver().findElement(By.tagName("body")).getText().contains("Fitness Challenge"));
    }

    @Then("no data is changed from initial state")
    public void no_data_is_changed_from_initial_state() {
        assertEquals(baselineState, getNameStepsMap());
    }

    // ---------- Feature 4 ----------

    @Given("I note the current step count for {string}")
    public void i_note_the_current_step_count_for(String name) {
        trackedParticipant = name;
        trackedBeforeSteps = getStepsFor(name);
    }

    @When("I open the {string} modal")
    public void i_open_the_modal(String ignored) {
        openAddStepsModal();
        assertTrue(waitForModalOpen());
    }

    @When("I select {string} from the dropdown")
    public void i_select_from_the_dropdown(String name) {
        trackedParticipant = name;
        new Select(getVisibleParticipantSelect()).selectByVisibleText(name);
    }

    @When("I enter {string} in the steps input field")
    public void i_enter_in_the_steps_input_field(String value) {
        lastEnteredSteps = value;
        WebElement input = getVisibleStepsInput();
        input.clear();
        input.sendKeys(value);
    }

    @When("I click {string} button")
    public void i_click_button(String buttonText) {
        if ("Add Steps".equals(buttonText) && isModalOpen()) {
            click(getVisibleModalAddStepsSubmitButton());
        } else {
            List<WebElement> buttons = getPageButtonsByText(buttonText);
            assertFalse(buttons.isEmpty());
            click(buttons.get(0));
        }
    }

    @Then("the modal closes automatically")
    public void the_modal_closes_automatically() {
        the_modal_closes();
    }

    @Then("the participant list refreshes")
    public void the_participant_list_refreshes() {
        assertFalse(getParticipantRows().isEmpty());
    }

    @Then("^Mike Kid's step count increases by ([\\d,]+)$")
    public void mike_kid_s_step_count_increases_by(String deltaText) {
        long delta = Long.parseLong(deltaText.replace(",", ""));
        long after = getStepsFor("Mike Kid");
        assertEquals(trackedBeforeSteps + delta, after);
    }

    @Then("the list re-sorts if Mike Kid's new total changes his ranking")
    public void the_list_re_sorts_if_mike_kid_s_new_total_changes_his_ranking() {
        participants_are_displayed_in_descending_order_by_step_count();
    }

    @When("I select any participant")
    public void i_select_any_participant() {
        Select s = new Select(getVisibleParticipantSelect());
        s.selectByIndex(1);
        trackedParticipant = s.getFirstSelectedOption().getText().trim();
        trackedBeforeSteps = getStepsFor(trackedParticipant);
    }

    @Then("an alert appears: {string}")
    public void an_alert_appears(String expected) {
        Alert alert = waiter().until(ExpectedConditions.alertIsPresent());
        assertEquals(expected, alert.getText());
    }

    @When("I accept the alert")
    public void i_accept_the_alert() {
        Alert alert = waiter().until(ExpectedConditions.alertIsPresent());
        alert.accept();
    }

    @Then("the modal remains open")
    public void the_modal_remains_open() {
        assertTrue(isModalOpen());
    }

    @Then("the step count updates correctly with the large number")
    public void the_step_count_updates_correctly_with_the_large_number() {
        long after = getStepsFor(trackedParticipant);
        assertEquals(trackedBeforeSteps + Long.parseLong(lastEnteredSteps), after);
    }

    @Then("the participant moves to first place")
    public void the_participant_moves_to_first_place() {
        assertEquals(trackedParticipant, getParticipantRows().get(0).name);
    }

    // ---------- Feature 5 ----------

    @When("I leave the participant dropdown at {string}")
    public void i_leave_the_participant_dropdown_at(String ignored) {
        new Select(getVisibleParticipantSelect()).selectByIndex(0);
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
        i_enter_in_the_steps_input_field(value);
    }

    @Then("the input field rejects non-numeric characters")
    public void the_input_field_rejects_non_numeric_characters() {
        String value = Optional.ofNullable(getVisibleStepsInput().getAttribute("value")).orElse("");
        assertTrue(value.isEmpty() || value.matches("[-+]?\\d*"));
    }

    @Then("no alphabetic characters appear in the input")
    public void no_alphabetic_characters_appear_in_the_input() {
        String value = Optional.ofNullable(getVisibleStepsInput().getAttribute("value")).orElse("");
        assertFalse(value.matches(".*[A-Za-z].*"));
    }

    // ---------- Feature 6 ----------

    @Given("I have added steps to at least 2 participants to modify the default state")
    public void i_have_added_steps_to_at_least_2_participants_to_modify_the_default_state() {
        addSteps("Mike Kid", "1000");
        addSteps("Jane Doe", "500");
    }

    @When("I wait for page reload")
    public void i_wait_for_page_reload() {
        waiter().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
    }

    @Then("all participants return to their default step counts")
    public void all_participants_return_to_their_default_step_counts() {
        assertEquals(DEFAULTS, getNameStepsMap());
    }

    @Then("the default ranking order is restored")
    public void the_default_ranking_order_is_restored() {
        participants_are_displayed_in_descending_order_by_step_count();
        List<ParticipantRow> rows = getParticipantRows();
        assertEquals("John Smith", rows.get(0).name);
        assertEquals("David Brown", rows.get(1).name);
        assertEquals("Jill Watson", rows.get(2).name);
    }

    @Then("trophy icons display for correct default top 3")
    public void trophy_icons_display_for_correct_default_top_3() {
        the_1st_place_participant_displays_a_gold_trophy_icon();
        the_2nd_place_participant_displays_a_silver_trophy_icon();
        the_3rd_place_participant_displays_a_bronze_trophy_icon();
    }

    @Given("I have modified participant data")
    public void i_have_modified_participant_data() {
        i_have_added_steps_to_at_least_2_participants_to_modify_the_default_state();
    }

    @When("I scroll to bottom of page")
    public void i_scroll_to_bottom_of_page() {
        i_scroll_to_the_bottom_of_the_page();
    }

    // ---------- Feature 7 ----------

    @When("I enter the maximum safe integer {string}")
    public void i_enter_the_maximum_safe_integer(String value) {
        i_enter_in_the_steps_input_field(value);
    }

    @When("I submit the form")
    public void i_submit_the_form() {
        click(getVisibleModalAddStepsSubmitButton());
    }

    @Then("the system accepts the value")
    public void the_system_accepts_the_value() {
        the_modal_closes_automatically();
    }

    @Then("the step count updates correctly")
    public void the_step_count_updates_correctly() {
        long after = getStepsFor(trackedParticipant);
        long expected = Math.round((double) trackedBeforeSteps + Double.parseDouble(lastEnteredSteps));
        assertEquals(expected, after);
    }

    // ---------- Feature 8 ----------

    @Given("I open the fitness challenge page in {word}")
    public void i_open_the_fitness_challenge_page_in_browser(String browser) {
        switchBrowser(browser);
        driver().navigate().to("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        waiter().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
    }

    @When("I add valid steps to a participant")
    public void i_add_valid_steps_to_a_participant() {
        trackedParticipant = "Mike Kid";
        trackedBeforeSteps = getStepsFor(trackedParticipant);
        addSteps(trackedParticipant, "1000");
        assertEquals(trackedBeforeSteps + 1000, getStepsFor(trackedParticipant));
    }

    @When("I submit without selecting participant")
    public void i_submit_without_selecting_participant() {
        openAddStepsModal();
        new Select(getVisibleParticipantSelect()).selectByIndex(0);
        WebElement input = getVisibleStepsInput();
        input.clear();
        input.sendKeys("1000");
        click(getVisibleModalAddStepsSubmitButton());
        Alert alert = waiter().until(ExpectedConditions.alertIsPresent());
        assertEquals("Please select a participant", alert.getText());
        alert.accept();
    }

    @When("I reset via top button")
    public void i_reset_via_top_button() {
        List<WebElement> resetButtons = getPageButtonsByText("Reset List");
        click(resetButtons.get(0));
        waiter().until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
    }

    @Then("all features work as expected")
    public void all_features_work_as_expected() {
        all_participants_return_to_their_default_step_counts();
        participants_are_displayed_in_descending_order_by_step_count();
    }

    // ---------- Helpers ----------

    private void openAddStepsModal() {
        List<WebElement> buttons = getPageButtonsByText("Add Steps");
        assertTrue("No visible Add Steps button found", !buttons.isEmpty());

        for (WebElement b : buttons) {
            click(b);
            if (waitForModalOpenShort()) return;
        }

        List<WebElement> all = driver().findElements(By.xpath("//button[normalize-space()='Add Steps']"));
        for (WebElement b : all) {
            try {
                click(b);
                if (waitForModalOpenShort()) return;
            } catch (Exception ignored) {
            }
        }

        throw new IllegalStateException("Modal not found");
    }

    private boolean waitForModalOpen() {
        try {
            waiter().until(d -> isModalOpen());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private boolean waitForModalOpenShort() {
        try {
            new WebDriverWait(driver(), Duration.ofSeconds(2)).until(d -> isModalOpen());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private boolean isModalOpen() {
        return getVisibleParticipantSelectOrNull() != null && getVisibleStepsInputOrNull() != null;
    }

    private WebElement getVisibleParticipantSelect() {
        WebElement e = getVisibleParticipantSelectOrNull();
        if (e == null) throw new IllegalStateException("Participant dropdown not visible");
        return e;
    }

    private WebElement getVisibleParticipantSelectOrNull() {
        List<WebElement> selects = driver().findElements(By.tagName("select"));
        for (WebElement s : selects) {
            if (!s.isDisplayed() || !s.isEnabled()) continue;
            try {
                Select ss = new Select(s);
                for (WebElement o : ss.getOptions()) {
                    if (o.getText().toLowerCase(Locale.ROOT).contains("choose participant")) {
                        return s;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private WebElement getVisibleStepsInput() {
        WebElement e = getVisibleStepsInputOrNull();
        if (e == null) throw new IllegalStateException("Steps input not visible");
        return e;
    }

    private WebElement getVisibleStepsInputOrNull() {
        List<WebElement> inputs = driver().findElements(By.cssSelector("input[type='number']"));
        for (WebElement i : inputs) {
            if (i.isDisplayed() && i.isEnabled()) return i;
        }
        return null;
    }

    private WebElement getVisibleModalAddStepsSubmitButton() {
        WebElement anchorInput = getVisibleStepsInput();
        List<WebElement> addButtons = driver().findElements(By.xpath("//button[normalize-space()='Add Steps']"));
        WebElement best = null;
        int bestDistance = Integer.MAX_VALUE;

        Point p = anchorInput.getLocation();
        for (WebElement b : addButtons) {
            if (!b.isDisplayed() || !b.isEnabled()) continue;
            int d = Math.abs(b.getLocation().getY() - p.getY());
            if (d < bestDistance) {
                bestDistance = d;
                best = b;
            }
        }

        if (best == null) throw new IllegalStateException("Modal Add Steps submit button not found");
        return best;
    }

    private WebElement getVisibleModalCloseButton() {
        List<WebElement> candidates = driver().findElements(By.xpath(
                "//*[normalize-space()='×' or contains(@class,'close')]"
        ));

        for (WebElement c : candidates) {
            if (c.isDisplayed() && c.isEnabled()) return c;
        }
        throw new IllegalStateException("Modal close button not found");
    }

    private void addSteps(String participant, String steps) {
        openAddStepsModal();
        new Select(getVisibleParticipantSelect()).selectByVisibleText(participant);
        WebElement input = getVisibleStepsInput();
        input.clear();
        input.sendKeys(steps);
        click(getVisibleModalAddStepsSubmitButton());
        waiter().until(d -> !isModalOpen());
    }

    private void click(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", element);
        }
    }

    private List<WebElement> getPageButtonsByText(String label) {
        List<WebElement> all = driver().findElements(By.xpath("//button[normalize-space()='" + label + "']"));
        List<WebElement> visible = new ArrayList<>();
        for (WebElement b : all) {
            if (b.isDisplayed() && b.isEnabled()) visible.add(b);
        }

        visible.sort(Comparator.comparingInt(o -> o.getLocation().getY()));

        List<WebElement> dedup = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (WebElement b : visible) {
            String key = b.getLocation().getX() + ":" + b.getLocation().getY();
            if (!seen.contains(key)) {
                seen.add(key);
                dedup.add(b);
            }
        }

        return dedup;
    }

    private Map<String, Long> getNameStepsMap() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (ParticipantRow r : getParticipantRows()) {
            map.put(r.name, r.steps);
        }
        return map;
    }

    private long getStepsFor(String name) {
        for (ParticipantRow r : getParticipantRows()) {
            if (name.equals(r.name)) return r.steps;
        }
        throw new IllegalStateException("Participant not found: " + name);
    }

    private List<ParticipantRow> getParticipantRows() {
        List<WebElement> candidates = new ArrayList<>();
        List<By> selectors = Arrays.asList(
                By.cssSelector("#participantsList li"),
                By.cssSelector("#participants li"),
                By.cssSelector("ul li"),
                By.cssSelector("tbody tr")
        );

        for (By s : selectors) {
            List<WebElement> found = driver().findElements(s);
            List<WebElement> filtered = new ArrayList<>();
            for (WebElement e : found) {
                String t = e.getText();
                if (e.isDisplayed() && t != null && containsAnyName(t)) filtered.add(e);
            }
            if (filtered.size() >= 10) {
                candidates = filtered;
                break;
            }
        }

        Map<String, ParticipantRow> unique = new LinkedHashMap<>();
        for (WebElement e : candidates) {
            ParticipantRow p = parseRow(e);
            if (p != null) unique.putIfAbsent(p.name, p);
        }
        return new ArrayList<>(unique.values());
    }

    private ParticipantRow parseRow(WebElement row) {
        String text = row.getText();
        if (text == null) return null;

        String name = null;
        for (String n : DEFAULTS.keySet()) {
            if (text.contains(n)) {
                name = n;
                break;
            }
        }
        if (name == null) return null;

        Matcher m = Pattern.compile("(\\d[\\d,]*)").matcher(text);
        long max = -1;
        while (m.find()) {
            long val = Long.parseLong(m.group(1).replace(",", ""));
            if (val > max) max = val;
        }

        if (max < 0) return null;
        return new ParticipantRow(name, max, row);
    }

    private boolean containsAnyName(String text) {
        for (String n : DEFAULTS.keySet()) {
            if (text.contains(n)) return true;
        }
        return false;
    }

    private boolean hasTrophy(WebElement row) {
        String t = row.getText();
        if (t.contains("🏆") || t.contains("🥇") || t.contains("🥈") || t.contains("🥉")) return true;

        for (WebElement i : row.findElements(By.cssSelector("i,svg,span"))) {
            String cls = Optional.ofNullable(i.getAttribute("class")).orElse("").toLowerCase(Locale.ROOT);
            if (cls.contains("trophy") || cls.contains("medal") || cls.contains("cup")) return true;
        }
        return false;
    }

    private boolean hasColor(WebElement row, int r, int g, int b) {
        for (WebElement i : row.findElements(By.cssSelector("i,svg,span"))) {
            try {
                java.awt.Color c = Color.fromString(i.getCssValue("color")).getColor();
                if (c.getRed() == r && c.getGreen() == g && c.getBlue() == b) return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    private long parseNumber(String formatted) {
        return Long.parseLong(formatted.replace(",", "").trim());
    }

    private void switchBrowser(String browser) {
        if (Hooks.driver != null) Hooks.driver.quit();
        Hooks.driver = createDriver(browser);
        wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(8));
    }

    private WebDriver createDriver(String browser) {
        String b = browser.toLowerCase(Locale.ROOT);
        List<String> methods = new ArrayList<>();
        if ("edge".equals(b)) methods.add("createEdgeDriver");
        if ("firefox".equals(b)) methods.add("createFirefoxDriver");
        methods.add("createChromeDriver");

        for (String m : methods) {
            try {
                Method method = DriverManager.class.getMethod(m);
                Object out = method.invoke(null);
                if (out instanceof WebDriver) return (WebDriver) out;
            } catch (Exception ignored) {
            }
        }
        throw new IllegalStateException("No compatible driver method found for " + browser);
    }

    private static class ParticipantRow {
        final String name;
        final long steps;
        final WebElement element;

        ParticipantRow(String name, long steps, WebElement element) {
            this.name = name;
            this.steps = steps;
            this.element = element;
        }
    }
}
