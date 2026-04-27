package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import selenium.utility.WebDriverUtil;
import selenium.pages.FitnessChallengePage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class Task2 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        driver = WebDriverUtil.getChromeDriver();
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
        // Initialize the page object with the current driver instance
        FitnessChallengePage fitnessPage = new FitnessChallengePage(driver);

        // Verify the page displays the correct title
        String pageTitle = fitnessPage.getPageTitle();
        assertEquals("Fitness Challenge", pageTitle, "Page title should be 'Fitness Challenge'");

        // Get all participants from the page
        List<WebElement> participants = fitnessPage.getParticipants();
        assertEquals(10, participants.size(), "Should display 10 participants");

        // Define the expected participants and their default step counts
        String[][] expectedParticipants = {
                {"John Smith", "15,000"},
                {"David Brown", "13,500"},
                {"Jill Watson", "12,000"},
                {"Carlos Garcia", "11,200"},
                {"Maria Rodriguez", "10,500"},
                {"Sarah Johnson", "9,800"},
                {"Alex Taylor", "8,900"},
                {"Mike Kid", "8,500"},
                {"Emily Chen", "7,300"},
                {"Jane Doe", "6,500"}
        };

        // Verify each participant's name and step count using the page object
        for (int i = 0; i < expectedParticipants.length; i++) {
            WebElement participantElement = participants.get(i);
            String name = fitnessPage.getParticipantName(participantElement);
            String steps = fitnessPage.getParticipantSteps(participantElement);
            
            assertEquals(expectedParticipants[i][0], name, "Participant " + (i + 1) + " name mismatch");
            assertEquals(expectedParticipants[i][1] + " steps", steps, "Participant " + (i + 1) + " step count mismatch");
        }

        // Verify that "Add Steps" buttons appear twice (top and bottom of the page)
        List<WebElement> addStepsButtons = fitnessPage.getAddStepsButtons();
        assertEquals(2, addStepsButtons.size(), "Should have 2 'Add Steps' buttons (top and bottom)");
        
        // Verify that "Reset List" buttons appear twice (top and bottom of the page)
        List<WebElement> resetButtons = fitnessPage.getResetButtons();
        assertEquals(2, resetButtons.size(), "Should have 2 'Reset List' buttons (top and bottom)");

        // Verify all "Add Steps" buttons are displayed and visible on the page
        for (WebElement button : addStepsButtons) {
            assertTrue(fitnessPage.isButtonDisplayed(button), "'Add Steps' button should be visible");
        }

        // Verify all "Reset List" buttons are displayed and visible on the page
        for (WebElement button : resetButtons) {
            assertTrue(fitnessPage.isButtonDisplayed(button), "'Reset List' button should be visible");
        }
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        // review the order of participants in the list
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        
        // verify participants are displayed in descending order by step count
        int previousSteps = Integer.MAX_VALUE;
        
        for (WebElement participant : participants) {
            String stepsText = participant.findElement(By.cssSelector(".participant-steps")).getText();
            // Extract number from "X,XXX steps" format
            int steps = Integer.parseInt(stepsText.replace(",", "").replace(" steps", ""));
            
            assertTrue(steps <= previousSteps, "Participants should be in descending order by step count");
            previousSteps = steps;
        }
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        // Initialize the page object with the current driver instance
        FitnessChallengePage fitnessPage = new FitnessChallengePage(driver);

        // Get all participants from the page
        List<WebElement> participants = fitnessPage.getParticipants();

        // Identify and verify the first three participants have trophy icons with correct colors
        
        // Verify 1st place participant displays a gold trophy icon
        WebElement firstPlace = participants.get(0);
        assertTrue(fitnessPage.hasTrophyIcon(firstPlace), "1st place participant should have a trophy icon");
        String firstPlaceColor = fitnessPage.getTrophyIconColor(firstPlace);
        assertTrue(firstPlaceColor.contains("gold") || firstPlaceColor.contains("255, 215, 0"), 
                "1st place should display a gold trophy icon");

        // Verify 2nd place participant displays a silver trophy icon
        WebElement secondPlace = participants.get(1);
        assertTrue(fitnessPage.hasTrophyIcon(secondPlace), "2nd place participant should have a trophy icon");
        String secondPlaceColor = fitnessPage.getTrophyIconColor(secondPlace);
        assertTrue(secondPlaceColor.contains("silver") || secondPlaceColor.contains("192, 192, 192"), 
                "2nd place should display a silver trophy icon");

        // Verify 3rd place participant displays a bronze (#cd7f32) trophy icon
        WebElement thirdPlace = participants.get(2);
        assertTrue(fitnessPage.hasTrophyIcon(thirdPlace), "3rd place participant should have a trophy icon");
        String thirdPlaceColor = fitnessPage.getTrophyIconColor(thirdPlace);
        assertTrue(thirdPlaceColor.contains("205, 127, 50") || thirdPlaceColor.contains("cd7f32"), 
                "3rd place should display a bronze (#cd7f32) trophy icon");

        // Verify participants ranked 4th and below have no trophy icons
        for (int i = 3; i < participants.size(); i++) {
            WebElement participant = participants.get(i);
            assertFalse(fitnessPage.hasTrophyIcon(participant), 
                    "Participant ranked " + (i + 1) + " should not have a trophy icon");
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        // Initialize the page object with the current driver instance
        FitnessChallengePage fitnessPage = new FitnessChallengePage(driver);

        // Locate the "Add Steps" button at the top of the page and click it
        fitnessPage.clickTopAddStepsButton();

        // Verify modal window appears and is displayed
        assertTrue(fitnessPage.isModalDisplayed(), "Modal window should be displayed after clicking 'Add Steps' button");

        // Verify modal displays the correct title
        String modalTitle = fitnessPage.getModalTitle();
        assertEquals("Add Steps to Participant", modalTitle, "Modal title should be 'Add Steps to Participant'");

        // Verify modal contains a participant dropdown
        assertTrue(fitnessPage.hasParticipantDropdown(), "Modal should contain a participant dropdown");

        // Verify modal contains a number input field
        assertTrue(fitnessPage.hasStepsInputField(), "Modal should contain a number input field for steps");

        // Verify modal contains an "Add Steps" submit button
        assertTrue(fitnessPage.hasModalSubmitButton(), "Modal should contain an 'Add Steps' submit button");

        // Verify modal has a close button (×) in the top-right corner
        assertTrue(fitnessPage.hasModalCloseButton(), "Modal should have a close button (×) in the top-right corner");

        // Verify dropdown is prepopulated with all 10 participants
        List<WebElement> dropdownOptions = fitnessPage.getDropdownOptions();
        assertEquals(11, dropdownOptions.size(), "Dropdown should have 11 options (1 default + 10 participants)");

        // Verify default dropdown text shows "Choose participant"
        String defaultText = fitnessPage.getDefaultDropdownText();
        assertEquals("Choose participant", defaultText, "Default dropdown text should be 'Choose participant'");
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        // Initialize the page object with the current driver instance
        FitnessChallengePage fitnessPage = new FitnessChallengePage(driver);

        // Scroll to the bottom of the page to locate the bottom "Add Steps" button
        fitnessPage.scrollToBottom();

        // Locate the "Add Steps" button at the bottom and click it
        fitnessPage.clickBottomAddStepsButton();

        // Verify modal window appears and is displayed
        assertTrue(fitnessPage.isModalDisplayed(), "Modal window should be displayed after clicking bottom 'Add Steps' button");

        // Verify modal displays the correct title
        String modalTitle = fitnessPage.getModalTitle();
        assertEquals("Add Steps to Participant", modalTitle, "Modal title should be 'Add Steps to Participant'");

        // Verify modal contains a participant dropdown
        assertTrue(fitnessPage.hasParticipantDropdown(), "Modal should contain a participant dropdown");

        // Verify modal contains a number input field
        assertTrue(fitnessPage.hasStepsInputField(), "Modal should contain a number input field for steps");

        // Verify modal contains an "Add Steps" submit button
        assertTrue(fitnessPage.hasModalSubmitButton(), "Modal should contain an 'Add Steps' submit button");

        // Verify modal has a close button (×) in the top-right corner
        assertTrue(fitnessPage.hasModalCloseButton(), "Modal should have a close button (×) in the top-right corner");

        // Verify dropdown is prepopulated with all 10 participants
        List<WebElement> dropdownOptions = fitnessPage.getDropdownOptions();
        assertEquals(11, dropdownOptions.size(), "Dropdown should have 11 options (1 default + 10 participants)");

        // Verify default dropdown text shows "Choose participant"
        String defaultText = fitnessPage.getDefaultDropdownText();
        assertEquals("Choose participant", defaultText, "Default dropdown text should be 'Choose participant'");
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        // Initialize the page object with the current driver instance
        FitnessChallengePage fitnessPage = new FitnessChallengePage(driver);

        // Capture initial page state before opening modal
        String initialPageTitle = fitnessPage.getPageTitle();
        List<WebElement> initialParticipants = fitnessPage.getParticipants();
        int initialParticipantCount = initialParticipants.size();
        
        // Store initial participant data to verify nothing changed after closing modal
        String[][] initialParticipantData = new String[initialParticipantCount][2];
        for (int i = 0; i < initialParticipantCount; i++) {
            initialParticipantData[i][0] = fitnessPage.getParticipantName(initialParticipants.get(i));
            initialParticipantData[i][1] = fitnessPage.getParticipantSteps(initialParticipants.get(i));
        }

        // Open the "Add Steps" modal
        fitnessPage.clickTopAddStepsButton();

        // Verify modal is displayed
        assertTrue(fitnessPage.isModalDisplayed(), "Modal should be displayed after clicking Add Steps button");

        // Click the × (close) button in the top-right corner of the modal
        fitnessPage.clickModalCloseButton();

        // Verify modal closes and is no longer displayed
        assertFalse(fitnessPage.isModalDisplayed(), "Modal should be closed after clicking close button");

        // Verify user returns to the main page view
        String finalPageTitle = fitnessPage.getPageTitle();
        assertEquals(initialPageTitle, finalPageTitle, "Page title should remain unchanged");

        // Verify no data is changed - compare current participant data with initial data
        List<WebElement> finalParticipants = fitnessPage.getParticipants();
        assertEquals(initialParticipantCount, finalParticipants.size(), "Participant count should not change");
        
        for (int i = 0; i < initialParticipantCount; i++) {
            String finalName = fitnessPage.getParticipantName(finalParticipants.get(i));
            String finalSteps = fitnessPage.getParticipantSteps(finalParticipants.get(i));
            
            assertEquals(initialParticipantData[i][0], finalName, 
                    "Participant " + (i + 1) + " name should not change after closing modal");
            assertEquals(initialParticipantData[i][1], finalSteps, 
                    "Participant " + (i + 1) + " steps should not change after closing modal");
        }
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() throws Exception {
        // TODO:
        //  note the current step count for "Mike Kid"
        //  open the "Add Steps" modal
        //  select "Mike Kid" from the dropdown
        //  enter "1000" in the steps input field
        //  click "Add Steps" button
        //  verify modal closes automatically
        //  verify participant list refreshes
        //  verify Mike Kid's step count increases by 1,000
        //  verify list re-sorts if Mike Kid's new total changes his ranking
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
        // TODO:
        //  open the "Add Steps" modal
        //  select any participant
        //  enter "999999" in the steps input field
        //  click "Add Steps" button
        //  verify modal closes
        //  verify step count updates correctly with the large number
        //  verify participant moves to first place
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  leave the participant dropdown at "Choose participant"
        //  enter "1000" in the steps input
        //  click "Add Steps" button
        //  verify alert appears: "Please select a participant"
        //  accept alert
        //  verify modal remains open
    }

    @Test
    public void submitWithoutEnteringSteps() throws Exception {
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
    public void submitWithNegativeSteps() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select a participant
        //  enter "-500" in the steps input field
        //  attempt to click "Add Steps" button
        //  verify alert appears: "Please enter a valid number of steps"
        //  accept alert
        //  verify modal remains open
    }

    @Test
    public void submitWithNonNumericInput() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select a participant
        //  attempt to enter "abc" in the steps input field
        //  verify input field rejects non-numeric characters
        //  verify no alphabetic characters appear in the input
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() throws Exception {
        // TODO:
        //  add steps to at least 2 participants to modify the default state
        //  click the "Reset List" button at the top of the page
        //  wait for page reload
        //  verify all participants return to their default step counts (same as firstTimePageLoad)
        //  verify default ranking order is restored (same as firstTimePageLoad)
        //  verify trophy icons display for correct default top 3 (same as firstTimePageLoad)
    }

    @Test
    public void resetViaBottomButton() throws Exception {
        // TODO:
        //  modify participant data
        //  scroll to bottom of page
        //  click the "Reset List" button at the bottom
        //  wait for page reload
        //  verify same behavior as resetViaTopButton
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select a participant
        //  enter the maximum safe integer: "9007199254740991"
        //  submit the form
        //  verify system accepts the value
        //  verify step count updates correctly
    }

    @Test
    public void decimalStepValues() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  select any participant
        //  enter "100.5" in the steps input field
        //  click "Add Steps" button
        //  verify alert appears: "Please enter a valid number of steps"
        //  accept alert
        //  verify modal remains open
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
