package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import selenium.utility.WebDriverManager;

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
    public void firstTimePageLoad() throws Exception {
        // TODO:
        //  verify page displays title "Fitness Challenge"
        //  verify 10 participants are displayed in the list
        //  verify default participants include:
        //   Mike Kid, Jill Watson, Jane Doe, John Smith, Sarah Johnson, Carlos Garcia, Emily Chen, David Brown, Maria Rodriguez, Alex Taylor
        //  verify all participants display their initial step counts
        //   Mike Kid: 8,500,
        //   Jill Watson: 12,000,
        //   Jane Doe: 6,500,
        //   John Smith: 15,000,
        //   Sarah Johnson: 9,800,
        //   Carlos Garcia: 11,200,
        //   Emily Chen: 7,300,
        //   David Brown: 13,500,
        //   Maria Rodriguez: 10,500,
        //   Alex Taylor: 8,900
        //  verify "Add Steps" and "Reset List" buttons are visible (appear twice - top and bottom)
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() throws Exception {
        // TODO:
        //  review the order of participants in the list
        //  verify participants are displayed in descending order by step count
        //  verify each participant's step count is greater than or equal to the participant below them
    }

    @Test
    public void medalTrophyIcons() throws Exception {
        // TODO:
        //  identify the first three participants in the list
        //  verify 1st place participant displays a gold trophy icon
        //  verify 2nd place participant displays a silver trophy icon
        //  verify 3rd place participant displays a bronze (#cd7f32) trophy icon
        //  verify participants ranked 4th and below have no trophy icons
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() throws Exception {
        // TODO:
        //  locate the "Add Steps" button at the top of the page
        //  click the button
        //  verify modal window appears with title "Add Steps to Participant"
        //  verify modal contains a participant dropdown
        //  verify modal contains a number input field
        //  verify modal contains "Add Steps" submit button
        //  verify modal has a close button (×) in the top-right corner
        //  verify dropdown is prepopulated with all 10 participants
        //  verify default dropdown text shows "Choose participant"
    }

    @Test
    public void openModalViaBottomButton() throws Exception {
        // TODO:
        //  scroll to the bottom of the page
        //  locate the "Add Steps" button at the bottom
        //  click the button
        //  verify modal opens
        //  verify all modal elements (same behavior as openModalViaTopButton)
    }

    @Test
    public void closeModalWithCloseButton() throws Exception {
        // TODO:
        //  open the "Add Steps" modal
        //  click the × (close) button in the top-right corner
        //  verify modal closes
        //  verify user returns to the main page view
        //  verify no data is changed (same as firstTimePageLoad)
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
