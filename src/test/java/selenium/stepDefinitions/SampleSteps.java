package selenium.stepDefinitions;

import io.cucumber.java.PendingException;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class SampleSteps {

    private WebDriver driver;
    private List<WebElement> participants;

    public SampleSteps() {
        this.driver = Hooks.driver;
    }

    // ---------- GIVEN ----------
    @Given("^I am on the fitness challenge page \"([^\"]*)\"$")
    public void challengePage(String url){
        driver.get(url);
    }

    // ---------- WHEN ----------
    @When("the page loads for the first time")
    public void pageLoads() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
    }

    // ---------- THEN ----------
    @Then("the page displays title {string}")
    public void checkTitle(String expectedTitle) {
        String actual = driver.findElement(By.tagName("h2")).getText();
        Assertions.assertEquals(expectedTitle, actual);
    }

    @Then("{int} participants are displayed in the list")
    public void checkParticipantsCount(int expectedCount) {
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        Assertions.assertEquals(expectedCount, participants.size());
    }

    @Then("the default participants are displayed:")
    public void checkParticipants(io.cucumber.datatable.DataTable table) {


        List<Map<String, String>> expected = table.asMaps();


        List<WebElement> rows = driver.findElements(By.cssSelector("#participantsList li"));

        List<String> actualData = rows.stream()
                .map(el -> {
                    String name = el.findElement(By.className("participant-name")).getText();
                    String steps = el.findElement(By.className("participant-steps"))
                            .getText()
                            .replace(" steps", "")
                            .trim();
                    return name + "|" + steps;
                })
                .collect(Collectors.toList());


        for (Map<String, String> row : expected) {
            String expectedEntry = row.get("Name") + "|" + row.get("Steps");
            Assertions.assertTrue(
                    actualData.contains(expectedEntry),
                    "Missing: " + expectedEntry
            );
        }
    }

    @Then("\"Add Steps\" buttons are visible at top and bottom")
    public void checkAddStepsButtons() {
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        Assertions.assertEquals(2, buttons.size());
        buttons.forEach(btn -> Assertions.assertTrue(btn.isDisplayed()));
    }

    @Then("\"Reset List\" buttons are visible at top and bottom")
    public void checkResetButtons() {
        List<WebElement> buttons = driver.findElements(By.id("resetBtn"));
        Assertions.assertEquals(2, buttons.size());
        buttons.forEach(btn -> Assertions.assertTrue(btn.isDisplayed()));
    }

    @When("I view the participant list")
    public void viewParticipantList() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));

        participants = driver.findElements(By.cssSelector("#participantsList li"));
    }

    // ---------- THEN (SORTING) ----------
    @Then("participants are displayed in descending order by step count")
    public void checkDescendingOrder() {

        List<Integer> steps = participants.stream()
                .map(el -> el.findElement(By.className("participant-steps")).getText())
                .map(text -> text.replace(" steps", "").replace(",", "").trim())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<Integer> sorted = new ArrayList<>(steps);
        sorted.sort(Comparator.reverseOrder());

        Assertions.assertEquals(sorted, steps, "List is not sorted in descending order");
    }

    @Then("each participant's step count is greater than or equal to the participant below them")
    public void checkEachGreaterThanNext() {

        List<Integer> steps = participants.stream()
                .map(el -> el.findElement(By.className("participant-steps")).getText())
                .map(text -> text.replace(" steps", "").replace(",", "").trim())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        for (int i = 0; i < steps.size() - 1; i++) {
            Assertions.assertTrue(
                    steps.get(i) >= steps.get(i + 1),
                    "Order broken at index " + i
            );
        }
    }

    // ---------- THEN (TROPHIES) ----------
    @Then("the 1st place participant displays a gold trophy icon")
    public void checkGoldTrophy() {
        checkTrophyColor(0, "255, 215, 0");
    }

    @Then("the 2nd place participant displays a silver trophy icon")
    public void checkSilverTrophy() {
        checkTrophyColor(1, "192, 192, 192");
    }

    @Then("the 3rd place participant displays a bronze trophy icon")
    public void checkBronzeTrophy() {
        checkTrophyColor(2, "205, 127, 50"); // бронза в html
    }

    @Then("participants ranked 4th and below have no trophy icons")
    public void checkNoTrophiesBelow() {

        for (int i = 3; i < participants.size(); i++) {
            List<WebElement> icons = participants.get(i)
                    .findElements(By.cssSelector("i.fa-trophy"));

            Assertions.assertTrue(icons.isEmpty(), "Participant at index " + i + " has a trophy");
        }
    }

    // ---------- HELPER ----------
    private void checkTrophyColor(int index, String expectedColor) {

        WebElement participant = participants.get(index);
        WebElement trophy = participant.findElement(By.cssSelector("i.fa-trophy"));

        String color = trophy.getCssValue("color"); // ← ВАЖНО

        System.out.println("Index " + index + " color: " + color); // для дебага

        Assertions.assertTrue(
                color.contains(expectedColor),
                "Expected " + expectedColor + " but got " + color
        );
    }

    // ---------- WHEN ----------
    @When("I click the {string} button at the top of the page")
    public void clickTopButton(String buttonText) {

        // на странице 2 одинаковые кнопки → берем первую (top)
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));

        WebElement topButton = buttons.get(0);
        topButton.click();
    }

    // ---------- THEN ----------
    @Then("a modal window appears with title {string}")
    public void checkModalTitle(String expectedTitle) {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );

        String title = modal.findElement(By.tagName("h3")).getText();

        Assertions.assertEquals(expectedTitle, title);
    }

    @Then("the modal contains a participant dropdown")
    public void checkDropdown() {
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Assertions.assertTrue(dropdown.isDisplayed());
    }

    @Then("the modal contains a number input field")
    public void checkNumberInput() {
        WebElement input = driver.findElement(By.id("steps_input"));
        Assertions.assertTrue(input.isDisplayed());
    }

    @Then("the modal contains {string} submit button")
    public void checkSubmitButton(String text) {
        WebElement button = driver.findElement(By.id("modal_add_steps_button"));
        Assertions.assertTrue(button.isDisplayed());
        Assertions.assertEquals(text, button.getText());
    }

    @Then("the modal has a close button \\(×) in the top-right corner")
    public void checkCloseButton() {

        WebElement closeBtn = driver.findElement(By.cssSelector(".w3-closebtn"));

        Assertions.assertTrue(closeBtn.isDisplayed());
        Assertions.assertEquals("×", closeBtn.getText().trim());
    }

    @Then("the dropdown is prepopulated with all 10 participants")
    public void checkDropdownOptions() {

        Select select = new Select(driver.findElement(By.id("participant_select")));

        List<WebElement> options = select.getOptions();

        // 1 default + 10 participants
        Assertions.assertEquals(11, options.size());
    }

    @Then("the default dropdown text shows {string}")
    public void checkDefaultDropdown(String expectedText) {

        Select select = new Select(driver.findElement(By.id("participant_select")));

        WebElement selected = select.getFirstSelectedOption();

        Assertions.assertEquals(expectedText, selected.getText());
    }

    @When("I scroll to the bottom of the page")
    public void scrollToBottom() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    @When("I click the {string} button at the bottom")
    public void clickBottomButton(String buttonText) {

        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));


        WebElement bottomButton = buttons.get(buttons.size() - 1);

        bottomButton.click();
    }

    @Then("all modal elements are present")
    public void checkAllModalElements() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );

        // dropdown
        Assertions.assertTrue(modal.findElement(By.id("participant_select")).isDisplayed());

        // input
        Assertions.assertTrue(modal.findElement(By.id("steps_input")).isDisplayed());

        // submit button
        WebElement submit = modal.findElement(By.id("modal_add_steps_button"));
        Assertions.assertTrue(submit.isDisplayed());

        // close button
        WebElement close = modal.findElement(By.cssSelector(".w3-closebtn"));
        Assertions.assertTrue(close.isDisplayed());
    }

    @Given("I have opened the {string} modal")
    public void openModal(String modalName) {

        if (modalName.equals("Add Steps")) {

            WebElement button = driver.findElements(By.id("addStepsBtn")).get(0);
            button.click();

            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        }
    }

    @When("I click the × \\(close) button in the top-right corner")
    public void clickCloseButton() {

        WebElement closeBtn = driver.findElement(By.cssSelector("#addStepsModal .w3-closebtn"));
        closeBtn.click();
    }

    @Then("the modal closes")
    public void modalCloses() {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        boolean invisible = wait.until(
                ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal"))
        );

        Assertions.assertTrue(invisible);
    }

    @Then("I return to the main page view")
    public void returnToMainPage() {

        WebElement list = driver.findElement(By.id("participantsList"));
        WebElement title = driver.findElement(By.tagName("h2"));

        Assertions.assertTrue(list.isDisplayed());
        Assertions.assertEquals("Fitness Challenge", title.getText());
    }

    @Then("no data is changed from initial state")
    public void checkNoDataChanged() {

        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        List<String> actual = participants.stream()
                .map(el -> el.findElement(By.className("participant-name")).getText()
                        + "|"
                        + el.findElement(By.className("participant-steps")).getText()
                )
                .toList();

        List<String> expected = List.of(
                "John Smith|15,000 steps",
                "David Brown|13,500 steps",
                "Jill Watson|12,000 steps",
                "Carlos Garcia|11,200 steps",
                "Maria Rodriguez|10,500 steps",
                "Sarah Johnson|9,800 steps",
                "Alex Taylor|8,900 steps",
                "Mike Kid|8,500 steps",
                "Emily Chen|7,300 steps",
                "Jane Doe|6,500 steps"
        );

        Assertions.assertEquals(expected, actual);
    }
}