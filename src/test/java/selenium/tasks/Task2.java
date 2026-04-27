package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.utility.WebDriverUtil;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        // Initialize EdgeDriver using cross-platform utility
        driver = WebDriverUtil.createEdgeDriver();
        // Navigate to fitness challenge page
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
    }

    @AfterEach
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    // FEATURE 1: INITIAL PAGE LOAD

    @Test
    public void firstTimePageLoad() throws Exception {
        // Verify page displays title "Fitness Challenge"
        WebElement pageTitle = driver.findElement(By.tagName("h2"));
        assertEquals("Fitness Challenge", pageTitle.getText(), "Page should display title 'Fitness Challenge'");

        // Verify 10 participants are displayed in the list
        java.util.List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(10, participants.size(), "Should display 10 participants");

        // Verify default participants include the expected names
        java.util.List<String> expectedNames = java.util.Arrays.asList(
            "Mike Kid", "Jill Watson", "Jane Doe", "John Smith", "Sarah Johnson",
            "Carlos Garcia", "Emily Chen", "David Brown", "Maria Rodriguez", "Alex Taylor"
        );

        java.util.List<WebElement> participantNames = driver.findElements(By.cssSelector(".participant-name"));
        for (int i = 0; i < participantNames.size(); i++) {
            String actualName = participantNames.get(i).getText();
            assertTrue(expectedNames.contains(actualName),
                "Participant " + actualName + " should be in the expected list");
        }

        // Verify all participants display their initial step counts
        java.util.Map<String, String> expectedSteps = new java.util.HashMap<>();
        expectedSteps.put("Mike Kid", "8,500");
        expectedSteps.put("Jill Watson", "12,000");
        expectedSteps.put("Jane Doe", "6,500");
        expectedSteps.put("John Smith", "15,000");
        expectedSteps.put("Sarah Johnson", "9,800");
        expectedSteps.put("Carlos Garcia", "11,200");
        expectedSteps.put("Emily Chen", "7,300");
        expectedSteps.put("David Brown", "13,500");
        expectedSteps.put("Maria Rodriguez", "10,500");
        expectedSteps.put("Alex Taylor", "8,900");

        java.util.List<WebElement> participantSteps = driver.findElements(By.cssSelector(".participant-steps"));
        for (WebElement stepElement : participantSteps) {
            String stepText = stepElement.getText();
            // Extract the numeric part (remove " steps" suffix)
            String stepsValue = stepText.replace(" steps", "");
            assertTrue(expectedSteps.containsValue(stepsValue),
                "Step count " + stepsValue + " should be in expected values");
        }

        // Verify "Add Steps" and "Reset List" buttons are visible (appear twice - top and bottom)
        java.util.List<WebElement> allAddStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        int visibleAddSteps = 0;
        for (WebElement b : allAddStepsButtons) {
            if (b.isDisplayed()) visibleAddSteps++;
        }
        assertEquals(2, visibleAddSteps, "Should have 2 visible 'Add Steps' buttons");

        java.util.List<WebElement> allResetButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Reset List')]"));
        int visibleReset = 0;
        for (WebElement b : allResetButtons) {
            if (b.isDisplayed()) visibleReset++;
        }
        assertEquals(2, visibleReset, "Should have 2 visible 'Reset List' buttons");

        // Verify all buttons are visible
        // (Already verified by counting visible buttons above)
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        // Get all participant step counts
        java.util.List<WebElement> participantSteps = driver.findElements(By.cssSelector(".participant-steps"));

        // Extract numeric values and verify descending order
        java.util.List<Integer> stepCounts = new java.util.ArrayList<>();
        for (WebElement stepElement : participantSteps) {
            String stepText = stepElement.getText();
            // Remove " steps" and commas, then parse to int
            String numericText = stepText.replace(" steps", "").replace(",", "");
            stepCounts.add(Integer.parseInt(numericText));
        }

        // Verify participants are displayed in descending order by step count
        for (int i = 0; i < stepCounts.size() - 1; i++) {
            assertTrue(stepCounts.get(i) >= stepCounts.get(i + 1),
                "Participant at position " + i + " (" + stepCounts.get(i) + " steps) should have more or equal steps than participant at position " + (i + 1) + " (" + stepCounts.get(i + 1) + " steps)");
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        // Get all participant list items
        java.util.List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        // Verify 1st place participant displays a gold trophy icon
        WebElement firstPlace = participants.get(0);
        WebElement firstPlaceTrophy = firstPlace.findElement(By.cssSelector(".fa-trophy"));
        String firstPlaceColor = firstPlaceTrophy.getCssValue("color");
        assertTrue(firstPlaceColor.contains("255") || firstPlaceColor.contains("gold") || firstPlaceColor.contains("#ffd700") || firstPlaceColor.equals("gold"), "1st place should have gold trophy");

        // Verify 2nd place participant displays a silver trophy icon
        WebElement secondPlace = participants.get(1);
        WebElement secondPlaceTrophy = secondPlace.findElement(By.cssSelector(".fa-trophy"));
        String secondPlaceColor = secondPlaceTrophy.getCssValue("color");
        assertTrue(secondPlaceColor.contains("192") || secondPlaceColor.contains("silver") || secondPlaceColor.contains("#c0c0c0") || secondPlaceColor.equals("silver"), "2nd place should have silver trophy");

        // Verify 3rd place participant displays a bronze trophy icon
        WebElement thirdPlace = participants.get(2);
        WebElement thirdPlaceTrophy = thirdPlace.findElement(By.cssSelector(".fa-trophy"));
        String thirdPlaceColor = thirdPlaceTrophy.getCssValue("color");
        assertTrue(thirdPlaceColor.contains("205") || thirdPlaceColor.contains("#cd7f32") || thirdPlaceColor.equals("rgb(205, 127, 50)"), "3rd place should have bronze trophy");
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        // Locate the "Add Steps" button at the top of the page (first one)
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        WebElement topAddStepsButton = addStepsButtons.get(0);
        topAddStepsButton.click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Verify modal window appears with title "Add Steps to Participant"
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed(), "Modal should be displayed");

        WebElement modalTitle = modal.findElement(By.xpath(".//h3"));
        assertEquals("Add Steps to Participant", modalTitle.getText(), "Modal should have correct title");

        // Verify modal contains a participant dropdown
        WebElement participantDropdown = modal.findElement(By.id("participant_select"));
        assertTrue(participantDropdown.isDisplayed(), "Participant dropdown should be visible");

        // Verify modal contains a number input field
        WebElement stepsInput = modal.findElement(By.id("steps_input"));
        assertTrue(stepsInput.isDisplayed(), "Steps input field should be visible");
        assertEquals("number", stepsInput.getAttribute("type"), "Input should be of type number");

        // Verify modal contains "Add Steps" submit button
        WebElement addStepsButton = modal.findElement(By.id("modal_add_steps_button"));
        assertTrue(addStepsButton.isDisplayed(), "Add Steps button should be visible");
        assertEquals("Add Steps", addStepsButton.getText(), "Button should have correct text");

        // Verify modal has a close button (×) in the top-right corner
        WebElement closeButton = modal.findElement(By.xpath(".//*[text()='×']"));
        assertTrue(closeButton.isDisplayed(), "Close button should be visible");
        assertEquals("×", closeButton.getText(), "Close button should display ×");

        // Verify dropdown is prepopulated with all 10 participants
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        java.util.List<WebElement> options = select.getOptions();
        assertEquals(11, options.size(), "Dropdown should have 11 options (10 participants + default)");

        // Verify default dropdown text shows "Choose participant"
        String defaultOptionText = select.getFirstSelectedOption().getText();
        assertEquals("Choose participant", defaultOptionText, "Default dropdown option should be 'Choose participant'");
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        // Scroll to the bottom of the page
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        // Locate the "Add Steps" button at the bottom (second one)
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        WebElement bottomAddStepsButton = addStepsButtons.get(1);
        bottomAddStepsButton.click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Verify modal opens with all modal elements (same behavior as openModalViaTopButton)
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed(), "Modal should be displayed");

        WebElement modalTitle = modal.findElement(By.xpath(".//h3"));
        assertEquals("Add Steps to Participant", modalTitle.getText(), "Modal should have correct title");

        WebElement participantDropdown = modal.findElement(By.id("participant_select"));
        assertTrue(participantDropdown.isDisplayed(), "Participant dropdown should be visible");

        WebElement stepsInput = modal.findElement(By.id("steps_input"));
        assertTrue(stepsInput.isDisplayed(), "Steps input field should be visible");

        WebElement addStepsButton = modal.findElement(By.id("modal_add_steps_button"));
        assertTrue(addStepsButton.isDisplayed(), "Add Steps button should be visible");

        WebElement closeButton = modal.findElement(By.xpath(".//*[text()='×']"));
        assertTrue(closeButton.isDisplayed(), "Close button should be visible");
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Verify modal is open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed(), "Modal should be displayed initially");

        // Click the × (close) button in the top-right corner
        WebElement closeButton = modal.findElement(By.xpath(".//*[text()='×']"));
        closeButton.click();

        // Wait for modal to close
        Thread.sleep(2000);

        // Verify modal closes
        assertFalse(modal.isDisplayed(), "Modal should be closed after clicking close button");

        // Verify user returns to the main page view (no data changed)
        WebElement pageTitle = driver.findElement(By.tagName("h2"));
        assertEquals("Fitness Challenge", pageTitle.getText(), "Should return to main page");

        // Verify participant list is unchanged (same as firstTimePageLoad)
        java.util.List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(10, participants.size(), "Should still display 10 participants");
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        // Note the current step count for "Mike Kid"
        WebElement mikeKidLi = driver.findElement(By.xpath("//li[contains(., 'Mike Kid')]"));
        WebElement mikeKidStepsElement = mikeKidLi.findElement(By.cssSelector(".participant-steps"));
        String originalStepsText = mikeKidStepsElement.getText();
        int originalSteps = Integer.parseInt(originalStepsText.replace(" steps", "").replace(",", ""));

        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Select "Mike Kid" from the dropdown
        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByVisibleText("Mike Kid");

        // Enter "2000" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("1000");

        // Click "Add Steps" button
        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();

        // Wait for modal to close and page to refresh
        Thread.sleep(2000);

        // Verify modal closes automatically
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertFalse(modal.isDisplayed(), "Modal should close automatically after adding steps");

        // Verify participant list refreshes and Mike Kid's step count increases by 1,000
        WebElement updatedMikeKidLi = driver.findElement(By.xpath("//li[contains(., 'Mike Kid')]"));
        WebElement updatedMikeKidStepsElement = updatedMikeKidLi.findElement(By.cssSelector(".participant-steps"));
        String updatedStepsText = updatedMikeKidStepsElement.getText();
        int updatedSteps = Integer.parseInt(updatedStepsText.replace(" steps", "").replace(",", ""));
        assertEquals(originalSteps + 1000, updatedSteps, "Mike Kid's step count should increase by 1000");

        // Verify list re-sorts if Mike Kid's new total changes his ranking
        // (Mike Kid starts at 8,500, adding 1,000 makes 9,500 - should move up in ranking)
        rankingOrder(); // Re-run ranking verification to ensure proper sorting
    }

    @Test
    public void addZeroSteps() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Select any participant
        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByIndex(1); // Select first participant (not the default "Choose participant")

        // Enter "0" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("0");

        // Click "Add Steps" button
        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();

        // Wait for alert to appear
        Thread.sleep(500);

        // Verify alert appears: "Please enter a valid number of steps"
        org.openqa.selenium.Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("Please enter a valid number of steps", alertText, "Alert should display correct message");

        // Accept alert
        alert.accept();

        // Verify modal remains open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed(), "Modal should remain open after alert");
    }

    @Test
    public void addLargeNumberOfSteps() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Select any participant
        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByIndex(1); // Select first participant

        // Enter "999999" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("999999");

        // Click "Add Steps" button
        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();

        // Wait for modal to close and page to refresh
        Thread.sleep(2000);

        // Verify modal closes
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertFalse(modal.isDisplayed(), "Modal should close after adding large number of steps");

        // Verify step count updates correctly with the large number
        // The participant should now have moved to first place
        java.util.List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        WebElement firstPlace = participants.get(0);
        WebElement firstPlaceSteps = firstPlace.findElement(By.cssSelector(".participant-steps"));
        String stepsText = firstPlaceSteps.getText();
        String numeric = stepsText.replace(" steps", "").replace(",", "");
        long stepsValue = Long.parseLong(numeric);
        assertTrue(stepsValue > 100000, "Step count should be very large");
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Leave the participant dropdown at "Choose participant" (default)
        // Enter "1000" in the steps input
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("1000");

        // Click "Add Steps" button
        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();

        // Wait for alert to appear
        Thread.sleep(500);

        // Verify alert appears: "Please select a participant"
        org.openqa.selenium.Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("Please select a participant", alertText, "Alert should display correct message");

        // Accept alert
        alert.accept();

        // Verify modal remains open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed(), "Modal should remain open after alert");
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Select any participant
        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByIndex(1); // Select first participant

        // Leave steps input empty (or enter "0")
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("0");

        // Click "Add Steps" button
        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();

        // Wait for alert to appear
        Thread.sleep(500);

        // Verify alert appears: "Please enter a valid number of steps"
        org.openqa.selenium.Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("Please enter a valid number of steps", alertText, "Alert should display correct message");

        // Accept alert
        alert.accept();

        // Verify modal remains open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed(), "Modal should remain open after alert");
    }

    @Test
    public void submitWithNegativeSteps() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Select a participant
        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByIndex(1); // Select first participant

        // Enter "-500" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("-500");

        // Attempt to click "Add Steps" button
        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();

        // Wait for alert to appear
        Thread.sleep(500);

        // Verify alert appears: "Please enter a valid number of steps"
        org.openqa.selenium.Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("Please enter a valid number of steps", alertText, "Alert should display correct message");

        // Accept alert
        alert.accept();

        // Verify modal remains open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed(), "Modal should remain open after alert");
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Select a participant
        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByIndex(1); // Select first participant

        // Attempt to enter "abc" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("abc");

        // Verify input field rejects non-numeric characters
        String inputValue = stepsInput.getAttribute("value");
        assertEquals("", inputValue, "Input field should reject non-numeric characters");

        // Verify no alphabetic characters appear in the input
        assertFalse(inputValue.matches(".*[a-zA-Z].*"), "Input should not contain alphabetic characters");
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        // Add steps to at least 2 participants to modify the default state
        // Add steps to Mike Kid
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(., 'Add Steps')]"));
        addStepsButtons.get(0).click();
        Thread.sleep(2000);

        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByVisibleText("Mike Kid");

        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("1000");

        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();
        Thread.sleep(2000);

        // Add steps to Jane Doe
        addStepsButtons = driver.findElements(By.xpath("//button[contains(., 'Add Steps')]"));
        addStepsButtons.get(0).click();
        Thread.sleep(2000);

        select = new org.openqa.selenium.support.ui.Select(driver.findElement(By.id("participant_select")));
        select.selectByVisibleText("Jane Doe");

        stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("500");

        addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();
        Thread.sleep(2000);

        // Click the "Reset List" button at the top of the page
        java.util.List<WebElement> resetButtons = driver.findElements(By.xpath("//button[contains(., 'Reset List')]"));
        resetButtons.get(0).click();

        // Wait for page reload
        Thread.sleep(3000);

        // Verify all participants return to their default step counts (same as firstTimePageLoad)
        firstTimePageLoad();

        // Verify default ranking order is restored (same as firstTimePageLoad)
        rankingOrder();


        // Verify trophy icons display for correct default top 3 (same as firstTimePageLoad)
        medalTrophyIcons();
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        // Modify participant data (add steps to one participant)
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();
        Thread.sleep(2000);

        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByVisibleText("Mike Kid");

        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("2000");

        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();
        Thread.sleep(2000);

        // Scroll to bottom of page
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        // Click the "Reset List" button at the bottom
        java.util.List<WebElement> resetButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Reset List')]"));
        resetButtons.get(1).click();

        // Wait for page reload
        Thread.sleep(3000);

        // Verify same behavior as resetViaTopButton
        firstTimePageLoad();
        rankingOrder();
        medalTrophyIcons();
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(normalize-space(text()), 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Select a participant
        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByIndex(1); // Select first participant

        // Enter the maximum safe integer: "9007199254740991"
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("9007199254740991");

        // Submit the form
        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();

        // Wait for processing
        Thread.sleep(2000);

        // Verify system accepts the value (modal closes)
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertFalse(modal.isDisplayed(), "Modal should close after submitting maximum integer");

        // Verify step count updates correctly
        java.util.List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        WebElement firstPlace = participants.get(0);
        WebElement firstPlaceSteps = firstPlace.findElement(By.cssSelector(".participant-steps"));
        String stepsText = firstPlaceSteps.getText();
        // Should contain the large number
        assertTrue(stepsText.replace(" steps", "").replace(",", "").length() > 10,
                  "Step count should be updated with the large number");
    }

    @Test
    public void decimalStepValues() throws Exception {
        // Open the "Add Steps" modal
        java.util.List<WebElement> addStepsButtons = driver.findElements(By.xpath("//button[contains(., 'Add Steps')]"));
        addStepsButtons.get(0).click();

        // Wait for modal to appear
        Thread.sleep(2000);

        // Select any participant
        WebElement participantDropdown = driver.findElement(By.id("participant_select"));
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(participantDropdown);
        select.selectByIndex(1); // Select first participant

        // Enter "100.5" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("100.5");

        // Click "Add Steps" button
        WebElement addStepsButton = driver.findElement(By.id("modal_add_steps_button"));
        addStepsButton.click();

        // Wait for potential alert or modal close
        Thread.sleep(500);

        try {
            // Try to switch to alert
            org.openqa.selenium.Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            assertEquals("Please enter a valid number of steps", alertText, "Alert should display correct message");
            alert.accept();
            // Verify modal remains open
            WebElement modal = driver.findElement(By.id("addStepsModal"));
            assertTrue(modal.isDisplayed(), "Modal should remain open after decimal input alert");
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            // No alert, perhaps decimal is accepted
            WebElement modal = driver.findElement(By.id("addStepsModal"));
            assertFalse(modal.isDisplayed(), "Modal should close if decimal is accepted");
        }
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        // TODO:
        //  open https://janisdzalbe.github.io/example-site/tasks/fitness_challenge in Chrome
        //  execute addValidStepsToParticipant, submitWithoutSelectingParticipant, and resetViaTopButton tests
        //  verify all features work as expected
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
