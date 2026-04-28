package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import selenium.utility.WebDriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverManager.initializeChromeDriver();
        driver.navigate().to("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() {
        // Wait for participants to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#participantsList li"), 10));

        // Verify page displays title "Fitness Challenge"
        assertEquals("Fitness Challenge", driver.getTitle());

        // Verify 10 participants are displayed in the list
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(10, participants.size());

        // Verify default participants include the specified names
        List<WebElement> names = driver.findElements(By.cssSelector(".participant-name"));
        List<String> expectedNames = List.of("John Smith", "David Brown", "Jill Watson", "Carlos Garcia", "Maria Rodriguez", "Sarah Johnson", "Alex Taylor", "Mike Kid", "Emily Chen", "Jane Doe");
        for (int i = 0; i < names.size(); i++) {
            assertEquals(expectedNames.get(i), names.get(i).getText());
        }

        // Verify all participants display their initial step counts
        List<WebElement> steps = driver.findElements(By.cssSelector(".participant-steps"));
        List<String> expectedSteps = List.of("15,000 steps", "13,500 steps", "12,000 steps", "11,200 steps", "10,500 steps", "9,800 steps", "8,900 steps", "8,500 steps", "7,300 steps", "6,500 steps");
        for (int i = 0; i < steps.size(); i++) {
            assertEquals(expectedSteps.get(i), steps.get(i).getText());
        }

        // Verify "Add Steps" and "Reset List" buttons are visible (appear twice - top and bottom)
        List<WebElement> addButtons = driver.findElements(By.id("addStepsBtn"));
        assertEquals(2, addButtons.size());
        for (WebElement btn : addButtons) {
            assertTrue(btn.isDisplayed());
        }

        List<WebElement> resetButtons = driver.findElements(By.id("resetBtn"));
        assertEquals(2, resetButtons.size());
        for (WebElement btn : resetButtons) {
            assertTrue(btn.isDisplayed());
        }
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        // Wait for participants to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#participantsList li"), 10));

        // Get the step counts
        List<WebElement> stepsElements = driver.findElements(By.cssSelector(".participant-steps"));
        List<Integer> stepCounts = stepsElements.stream()
            .map(element -> element.getText().replace(" steps", "").replace(",", ""))
            .map(Integer::parseInt)
            .toList();

        // Verify participants are displayed in descending order by step count
        for (int i = 0; i < stepCounts.size() - 1; i++) {
            assertTrue(stepCounts.get(i) >= stepCounts.get(i + 1),
                "Step count at position " + i + " (" + stepCounts.get(i) + ") is not >= next (" + stepCounts.get(i + 1) + ")");
        }
    }

    @Test
    public void medalTrophyIcons() {
        // Wait for participants to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector("#participantsList li"), 10));

        // Get all participant list items
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        // Verify 1st place has gold trophy
        WebElement goldIcon = participants.get(0).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(255, 215, 0, 1)", goldIcon.getCssValue("color"));

        // Verify 2nd place has silver trophy
        WebElement silverIcon = participants.get(1).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(192, 192, 192, 1)", silverIcon.getCssValue("color"));

        // Verify 3rd place has bronze trophy
        WebElement bronzeIcon = participants.get(2).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(205, 127, 50, 1)", bronzeIcon.getCssValue("color"));

        // Verify participants 4th and below have no trophy icons
        for (int i = 3; i < participants.size(); i++) {
            List<WebElement> icons = participants.get(i).findElements(By.cssSelector("i.fa.fa-trophy"));
            assertEquals(0, icons.size(), "Participant at position " + i + " should not have a trophy icon");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {

        // Wait setup
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Locate the TOP "Add Steps" button (there are 2 with same ID)
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        WebElement topButton = buttons.get(0);

        // 2. Click the button
        topButton.click();

        // 3. Verify modal is visible
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(modal.isDisplayed());

        // 4. Verify modal title
        WebElement title = modal.findElement(By.tagName("h3"));
        assertEquals("Add Steps to Participant", title.getText());

        // 5. Verify dropdown
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        assertTrue(dropdown.isDisplayed());

        // 6. Verify number input field
        WebElement input = modal.findElement(By.id("steps_input"));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));

        // 7. Verify submit button
        WebElement addButton = modal.findElement(By.id("modal_add_steps_button"));
        assertTrue(addButton.isDisplayed());
        assertEquals("Add Steps", addButton.getText());

        // 8. Verify close button (×)
        WebElement closeButton = modal.findElement(By.cssSelector(".w3-closebtn"));
        assertTrue(closeButton.isDisplayed());

        // 9. Verify dropdown has 10 participants (+1 default option)
        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        assertEquals(11, options.size());

        // 10. Verify default dropdown text
        WebElement firstOption = options.get(0);
        assertEquals("Choose participant", firstOption.getText());
    }

    @Test
    public void openModalViaBottomButton() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Scroll to bottom
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight);");

        // 2. Locate BOTH "Add Steps" buttons
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));

        // 3. Pick the BOTTOM button (index 1)
        WebElement bottomButton = buttons.get(1);

        // 4. Click it
        bottomButton.click();

        // 5. Verify modal is visible
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );
        assertTrue(modal.isDisplayed());

        // 6. Verify modal title
        WebElement title = modal.findElement(By.tagName("h3"));
        assertEquals("Add Steps to Participant", title.getText());

        // 7. Verify dropdown
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        assertTrue(dropdown.isDisplayed());

        // 8. Verify number input
        WebElement input = modal.findElement(By.id("steps_input"));
        assertTrue(input.isDisplayed());
        assertEquals("number", input.getAttribute("type"));

        // 9. Verify submit button
        WebElement addButton = modal.findElement(By.id("modal_add_steps_button"));
        assertTrue(addButton.isDisplayed());
        assertEquals("Add Steps", addButton.getText());

        // 10. Verify close button
        WebElement closeButton = modal.findElement(By.cssSelector(".w3-closebtn"));
        assertTrue(closeButton.isDisplayed());

        // 11. Verify dropdown options (10 participants + default)
        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        assertEquals(11, options.size());

        // 12. Verify default text
        assertEquals("Choose participant", options.get(0).getText());
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Open modal (use TOP button)
        List<WebElement> buttons = driver.findElements(By.id("addStepsBtn"));
        buttons.get(0).click();

        // 2. Wait for modal to appear
        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );
        assertTrue(modal.isDisplayed());

        // 3. Click close (×) button
        WebElement closeButton = modal.findElement(By.cssSelector(".w3-closebtn"));
        closeButton.click();

        // 4. Verify modal is closed
        wait.until(ExpectedConditions.invisibilityOf(modal));
        assertFalse(modal.isDisplayed());

        // 5. Verify user is back on main page (list visible)
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(10, participants.size());

        // 6. Verify data is unchanged (initial state)

        List<WebElement> names = driver.findElements(By.cssSelector(".participant-name"));
        List<String> expectedNames = List.of(
                "John Smith", "David Brown", "Jill Watson", "Carlos Garcia", "Maria Rodriguez",
                "Sarah Johnson", "Alex Taylor", "Mike Kid", "Emily Chen", "Jane Doe"
        );

        for (int i = 0; i < names.size(); i++) {
            assertEquals(expectedNames.get(i), names.get(i).getText());
        }

        List<WebElement> steps = driver.findElements(By.cssSelector(".participant-steps"));
        List<String> expectedSteps = List.of(
                "15,000 steps", "13,500 steps", "12,000 steps", "11,200 steps", "10,500 steps",
                "9,800 steps", "8,900 steps", "8,500 steps", "7,300 steps", "6,500 steps"
        );

        for (int i = 0; i < steps.size(); i++) {
            assertEquals(expectedSteps.get(i), steps.get(i).getText());
        }
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Get current steps for "Mike Kid"
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        int initialSteps = 0;

        for (WebElement p : participants) {
            String name = p.findElement(By.cssSelector(".participant-name")).getText();

            if (name.equals("Mike Kid")) {
                String stepsText = p.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(" steps", "")
                        .replace(",", "");

                initialSteps = Integer.parseInt(stepsText);
                break;
            }
        }

        // 2. Open modal (top button)
        driver.findElements(By.id("addStepsBtn")).get(0).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );

        // 3. Select "Mike Kid" from dropdown
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Mike Kid");

        // 4. Enter 1000 steps
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys("1000");

        // 5. Click "Add Steps"
        WebElement addButton = modal.findElement(By.id("modal_add_steps_button"));
        addButton.click();

        // 6. Verify modal closes
        wait.until(ExpectedConditions.invisibilityOf(modal));

        // 7. Wait for list refresh (simple wait for DOM update)
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.cssSelector("#participantsList li"), 0
        ));

        // 8. Find "Mike Kid" again and verify updated steps
        List<WebElement> updatedParticipants = driver.findElements(By.cssSelector("#participantsList li"));

        int updatedSteps = 0;

        for (WebElement p : updatedParticipants) {
            String name = p.findElement(By.cssSelector(".participant-name")).getText();

            if (name.equals("Mike Kid")) {
                String stepsText = p.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(" steps", "")
                        .replace(",", "");

                updatedSteps = Integer.parseInt(stepsText);
                break;
            }
        }

        // 9. Verify step increase
        assertEquals(initialSteps + 1000, updatedSteps);

        // 10. Verify list is sorted descending
        List<WebElement> stepsElements = driver.findElements(By.cssSelector(".participant-steps"));

        List<Integer> stepValues = stepsElements.stream()
                .map(e -> e.getText().replace(" steps", "").replace(",", ""))
                .map(Integer::parseInt)
                .toList();

        for (int i = 0; i < stepValues.size() - 1; i++) {
            assertTrue(stepValues.get(i) >= stepValues.get(i + 1),
                    "List is not sorted after update");
        }
    }

    @Test
    public void addZeroSteps() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select any participant
        //  enter "0" in the steps input field
        //  click "Add Steps" button
        //  verify alert appears: "Please enter a valid number of steps"
        //  accept alert
        //  verify modal remains open
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Pick a participant (let's use "Mike Kid" again)
        String targetName = "Mike Kid";

        // Get initial steps
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        int initialSteps = 0;

        for (WebElement p : participants) {
            String name = p.findElement(By.cssSelector(".participant-name")).getText();

            if (name.equals(targetName)) {
                String stepsText = p.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(" steps", "")
                        .replace(",", "");

                initialSteps = Integer.parseInt(stepsText);
                break;
            }
        }

        // 2. Open modal
        driver.findElements(By.id("addStepsBtn")).get(0).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );

        // 3. Select participant
        Select select = new Select(modal.findElement(By.id("participant_select")));
        select.selectByVisibleText(targetName);

        // 4. Enter large number
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys("999999");

        // 5. Click Add Steps
        modal.findElement(By.id("modal_add_steps_button")).click();

        // 6. Verify modal closes
        wait.until(ExpectedConditions.invisibilityOf(modal));

        // 7. Re-fetch updated list
        List<WebElement> updatedParticipants = driver.findElements(By.cssSelector("#participantsList li"));

        int updatedSteps = 0;

        for (WebElement p : updatedParticipants) {
            String name = p.findElement(By.cssSelector(".participant-name")).getText();

            if (name.equals(targetName)) {
                String stepsText = p.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(" steps", "")
                        .replace(",", "");

                updatedSteps = Integer.parseInt(stepsText);
                break;
            }
        }

        // 8. Verify steps updated correctly
        assertEquals(initialSteps + 999999, updatedSteps);

        // 9. Verify participant moved to FIRST place
        WebElement firstParticipant = updatedParticipants.get(0);
        String firstName = firstParticipant.findElement(By.cssSelector(".participant-name")).getText();

        assertEquals(targetName, firstName, "Participant did not move to first place");
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Open modal
        driver.findElements(By.id("addStepsBtn")).get(0).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );
        assertTrue(modal.isDisplayed());

        // 2. DO NOT select participant (leave default "Choose participant")

        // 3. Enter 1000 steps
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys("1000");

        // 4. Click Add Steps
        WebElement addButton = modal.findElement(By.id("modal_add_steps_button"));
        addButton.click();

        // 5. Verify alert appears
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please select a participant", alert.getText());

        // 6. Accept alert
        alert.accept();

        // 7. Verify modal is STILL open
        assertTrue(modal.isDisplayed());
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Open modal
        driver.findElements(By.id("addStepsBtn")).get(0).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );
        assertTrue(modal.isDisplayed());

        // 2. Select any participant
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1); // select first valid participant

        // 3. Enter invalid steps (0)
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys("0");

        // 4. Click Add Steps
        modal.findElement(By.id("modal_add_steps_button")).click();

        // 5. Verify alert appears
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());

        // 6. Accept alert
        alert.accept();

        // 7. Verify modal remains open
        assertTrue(modal.isDisplayed());
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Open modal
        driver.findElements(By.id("addStepsBtn")).get(0).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );
        assertTrue(modal.isDisplayed());

        // 2. Select any participant
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1); // first valid participant

        // 3. Enter negative steps
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys("-500");

        // 4. Click Add Steps
        modal.findElement(By.id("modal_add_steps_button")).click();

        // 5. Verify alert appears
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter a valid number of steps", alert.getText());

        // 6. Accept alert
        alert.accept();

        // 7. Verify modal remains open
        assertTrue(modal.isDisplayed());
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // 1. Open modal
        driver.findElements(By.id("addStepsBtn")).get(0).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );
        assertTrue(modal.isDisplayed());

        // 2. Select any participant
        WebElement dropdown = modal.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1);

        // 3. Try entering non-numeric input
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys("abc");

        // 4. Get actual value from input
        String value = input.getAttribute("value");

        // 5. Verify no alphabetic characters are accepted
        assertTrue(value.isEmpty() || value.matches("\\d*"),
                "Input field accepted non-numeric characters: " + value);
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // ---------- MODIFY STATE (add steps to 2 participants) ----------

        // Add steps to Mike Kid
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        new Select(modal.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");

        modal.findElement(By.id("steps_input")).sendKeys("1000");
        modal.findElement(By.id("modal_add_steps_button")).click();

        wait.until(ExpectedConditions.invisibilityOf(modal));

        // Add steps to Jane Doe
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        new Select(modal.findElement(By.id("participant_select")))
                .selectByVisibleText("Jane Doe");

        modal.findElement(By.id("steps_input")).sendKeys("2000");
        modal.findElement(By.id("modal_add_steps_button")).click();

        wait.until(ExpectedConditions.invisibilityOf(modal));

        // ---------- CLICK RESET (TOP BUTTON) ----------

        List<WebElement> resetButtons = driver.findElements(By.id("resetBtn"));
        resetButtons.get(0).click(); // top button

        // ---------- WAIT FOR RESET ----------

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10));

        // ---------- VERIFY DEFAULT STATE ----------

        List<WebElement> names = driver.findElements(By.cssSelector(".participant-name"));
        List<WebElement> steps = driver.findElements(By.cssSelector(".participant-steps"));

        List<String> expectedNames = List.of(
                "John Smith", "David Brown", "Jill Watson", "Carlos Garcia",
                "Maria Rodriguez", "Sarah Johnson", "Alex Taylor",
                "Mike Kid", "Emily Chen", "Jane Doe"
        );

        List<String> expectedSteps = List.of(
                "15,000 steps", "13,500 steps", "12,000 steps", "11,200 steps",
                "10,500 steps", "9,800 steps", "8,900 steps",
                "8,500 steps", "7,300 steps", "6,500 steps"
        );

        for (int i = 0; i < names.size(); i++) {
            assertEquals(expectedNames.get(i), names.get(i).getText());
            assertEquals(expectedSteps.get(i), steps.get(i).getText());
        }

        // ---------- VERIFY TROPHIES ----------

        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        // 1st place → gold
        WebElement gold = participants.get(0).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(255, 215, 0, 1)", gold.getCssValue("color"));

        // 2nd place → silver
        WebElement silver = participants.get(1).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(192, 192, 192, 1)", silver.getCssValue("color"));

        // 3rd place → bronze
        WebElement bronze = participants.get(2).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(205, 127, 50, 1)", bronze.getCssValue("color"));

        // Others → no trophy
        for (int i = 3; i < participants.size(); i++) {
            List<WebElement> icons = participants.get(i).findElements(By.cssSelector("i.fa.fa-trophy"));
            assertEquals(0, icons.size());
        }
    }

    @Test
    public void resetViaBottomButton() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // ---------- MODIFY STATE ----------

        // Add steps to Mike Kid
        driver.findElements(By.id("addStepsBtn")).get(0).click();
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        new Select(modal.findElement(By.id("participant_select")))
                .selectByVisibleText("Mike Kid");

        modal.findElement(By.id("steps_input")).sendKeys("1000");
        modal.findElement(By.id("modal_add_steps_button")).click();

        wait.until(ExpectedConditions.invisibilityOf(modal));

        // ---------- SCROLL TO BOTTOM ----------

        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight);");

        // ---------- CLICK BOTTOM RESET BUTTON ----------

        List<WebElement> resetButtons = driver.findElements(By.id("resetBtn"));
        resetButtons.get(1).click(); // bottom button

        // ---------- WAIT FOR RESET ----------

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10));

        // ---------- VERIFY DEFAULT STATE ----------

        List<WebElement> names = driver.findElements(By.cssSelector(".participant-name"));
        List<WebElement> steps = driver.findElements(By.cssSelector(".participant-steps"));

        List<String> expectedNames = List.of(
                "John Smith", "David Brown", "Jill Watson", "Carlos Garcia",
                "Maria Rodriguez", "Sarah Johnson", "Alex Taylor",
                "Mike Kid", "Emily Chen", "Jane Doe"
        );

        List<String> expectedSteps = List.of(
                "15,000 steps", "13,500 steps", "12,000 steps", "11,200 steps",
                "10,500 steps", "9,800 steps", "8,900 steps",
                "8,500 steps", "7,300 steps", "6,500 steps"
        );

        for (int i = 0; i < names.size(); i++) {
            assertEquals(expectedNames.get(i), names.get(i).getText());
            assertEquals(expectedSteps.get(i), steps.get(i).getText());
        }

        // ---------- VERIFY TROPHIES ----------

        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        // Gold
        WebElement gold = participants.get(0).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(255, 215, 0, 1)", gold.getCssValue("color"));

        // Silver
        WebElement silver = participants.get(1).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(192, 192, 192, 1)", silver.getCssValue("color"));

        // Bronze
        WebElement bronze = participants.get(2).findElement(By.cssSelector("i.fa.fa-trophy"));
        assertEquals("rgba(205, 127, 50, 1)", bronze.getCssValue("color"));

        // Others → no trophy
        for (int i = 3; i < participants.size(); i++) {
            assertEquals(0,
                    participants.get(i).findElements(By.cssSelector("i.fa.fa-trophy")).size());
        }
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String targetName = "Mike Kid";
        long maxValue = 9007199254740991L;

        // ---------- GET INITIAL STEPS ----------

        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        long initialSteps = 0;

        for (WebElement p : participants) {
            String name = p.findElement(By.cssSelector(".participant-name")).getText();

            if (name.equals(targetName)) {
                String stepsText = p.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(" steps", "")
                        .replace(",", "");

                initialSteps = Long.parseLong(stepsText);
                break;
            }
        }

        // ---------- OPEN MODAL ----------

        driver.findElements(By.id("addStepsBtn")).get(0).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );

        // ---------- SELECT PARTICIPANT ----------

        Select select = new Select(modal.findElement(By.id("participant_select")));
        select.selectByVisibleText(targetName);

        // ---------- ENTER MAX VALUE ----------

        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys(String.valueOf(maxValue));

        // ---------- SUBMIT ----------

        modal.findElement(By.id("modal_add_steps_button")).click();

        // ---------- VERIFY NO ALERT ----------

        try {
            wait.until(ExpectedConditions.alertIsPresent());
            fail("Alert appeared but should not for max integer value");
        } catch (TimeoutException ignored) {
            // Expected → no alert
        }

        // ---------- VERIFY MODAL CLOSED ----------

        wait.until(ExpectedConditions.invisibilityOf(modal));

        // ---------- VERIFY UPDATED VALUE ----------

        List<WebElement> updatedParticipants = driver.findElements(By.cssSelector("#participantsList li"));

        long updatedSteps = 0;

        for (WebElement p : updatedParticipants) {
            String name = p.findElement(By.cssSelector(".participant-name")).getText();

            if (name.equals(targetName)) {
                String stepsText = p.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(" steps", "")
                        .replace(",", "");

                updatedSteps = Long.parseLong(stepsText);
                break;
            }
        }

        assertEquals(initialSteps + maxValue, updatedSteps);
    }

    @Test
    public void decimalStepValues() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String targetName = "Mike Kid";

        // ---------- GET INITIAL STEPS ----------
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        int initialSteps = 0;

        for (WebElement p : participants) {
            String name = p.findElement(By.cssSelector(".participant-name")).getText();

            if (name.equals(targetName)) {
                String stepsText = p.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(" steps", "")
                        .replace(",", "");

                initialSteps = Integer.parseInt(stepsText);
                break;
            }
        }

        // ---------- OPEN MODAL ----------
        driver.findElements(By.id("addStepsBtn")).get(0).click();

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal"))
        );

        // ---------- SELECT PARTICIPANT ----------
        Select select = new Select(modal.findElement(By.id("participant_select")));
        select.selectByVisibleText(targetName);

        // ---------- ENTER DECIMAL ----------
        WebElement input = modal.findElement(By.id("steps_input"));
        input.clear();
        input.sendKeys("100.5");

        // ---------- SUBMIT ----------
        modal.findElement(By.id("modal_add_steps_button")).click();

        // ---------- VERIFY MODAL CLOSED ----------
        wait.until(ExpectedConditions.invisibilityOf(modal));

        // ---------- VERIFY RESULT ----------
        List<WebElement> updatedParticipants = driver.findElements(By.cssSelector("#participantsList li"));

        int updatedSteps = 0;

        for (WebElement p : updatedParticipants) {
            String name = p.findElement(By.cssSelector(".participant-name")).getText();

            if (name.equals(targetName)) {
                String stepsText = p.findElement(By.cssSelector(".participant-steps"))
                        .getText()
                        .replace(" steps", "")
                        .replace(",", "");

                updatedSteps = Integer.parseInt(stepsText);
                break;
            }
        }

        // ---------- ASSERT ----------
        assertTrue(updatedSteps >= initialSteps,
                "Decimal value should not reduce steps");

    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // ---------- OPEN PAGE ----------
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#participantsList li"), 10));

        // ---------- TEST 1 ----------
        addValidStepsToParticipant();

        closeModalIfOpen();

        // ---------- TEST 2 ----------
        submitWithoutSelectingParticipant();

        closeModalIfOpen();

        // ---------- TEST 3 ----------
        resetViaTopButton();

        // ---------- FINAL VALIDATION ----------
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(10, participants.size(), "List should be reset to default");
    }

    private void closeModalIfOpen() {

        List<WebElement> modals = driver.findElements(By.id("addStepsModal"));

        if (!modals.isEmpty() && modals.get(0).isDisplayed()) {

            WebElement modal = modals.get(0);

            // click close (×)
            modal.findElement(By.cssSelector(".w3-closebtn")).click();

            // wait until modal disappears
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOf(modal));
        }
    }

    @Test
    public void edgeBrowser() throws Exception {
        // TODO:
        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Edge
        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
        //  verify all features work as expected
    }

    @Test
    public void firefoxBrowser() throws Exception {
        // TODO:
        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Firefox
        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
        //  verify all features work as expected
    }
}
