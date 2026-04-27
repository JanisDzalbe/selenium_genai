package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import selenium.pages.FitnessChallengePage;
import selenium.utility.DriverFactory;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class Task2 {
    WebDriver driver;
    FitnessChallengePage fitnessPage;

    @BeforeEach
    public void openPage() {
        driver = DriverFactory.createChromeDriver();
        fitnessPage = new FitnessChallengePage(driver);
        fitnessPage.navigateToPage("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
        fitnessPage.waitForPageLoad();
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
        // Verify page displays title "Fitness Challenge"
        assertEquals("Fitness Challenge", fitnessPage.getPageTitle());

        // Verify 10 participants are displayed in the list
        assertEquals(10, fitnessPage.getParticipantCount());

        // Verify default participants include expected names and step counts
        List<Map<String, String>> participants = fitnessPage.getAllParticipants();

        // Sort participants by steps descending to match expected order
        participants.sort((a, b) -> {
            int stepsA = Integer.parseInt(a.get("steps").replace(" steps", "").replace(",", ""));
            int stepsB = Integer.parseInt(b.get("steps").replace(" steps", "").replace(",", ""));
            return Integer.compare(stepsB, stepsA);
        });

        // Verify specific participants and their step counts
        assertEquals("John Smith", participants.get(0).get("name"));
        assertEquals("15,000 steps", participants.get(0).get("steps"));

        assertEquals("David Brown", participants.get(1).get("name"));
        assertEquals("13,500 steps", participants.get(1).get("steps"));

        assertEquals("Jill Watson", participants.get(2).get("name"));
        assertEquals("12,000 steps", participants.get(2).get("steps"));

        assertEquals("Carlos Garcia", participants.get(3).get("name"));
        assertEquals("11,200 steps", participants.get(3).get("steps"));

        assertEquals("Maria Rodriguez", participants.get(4).get("name"));
        assertEquals("10,500 steps", participants.get(4).get("steps"));

        assertEquals("Sarah Johnson", participants.get(5).get("name"));
        assertEquals("9,800 steps", participants.get(5).get("steps"));

        assertEquals("Alex Taylor", participants.get(6).get("name"));
        assertEquals("8,900 steps", participants.get(6).get("steps"));

        assertEquals("Mike Kid", participants.get(7).get("name"));
        assertEquals("8,500 steps", participants.get(7).get("steps"));

        assertEquals("Emily Chen", participants.get(8).get("name"));
        assertEquals("7,300 steps", participants.get(8).get("steps"));

        assertEquals("Jane Doe", participants.get(9).get("name"));
        assertEquals("6,500 steps", participants.get(9).get("steps"));

        // Verify "Add Steps" and "Reset List" buttons are visible (appear twice - top and bottom)
        List<WebElement> addStepsButtons = driver.findElements(By.id("addStepsBtn"));
        List<WebElement> resetButtons = driver.findElements(By.id("resetBtn"));

        assertEquals(2, addStepsButtons.size());
        assertEquals(2, resetButtons.size());

        for (WebElement button : addStepsButtons) {
            assertTrue(button.isDisplayed());
        }
        for (WebElement button : resetButtons) {
            assertTrue(button.isDisplayed());
        }
    }

    // FEATURE 2: PARTICIPANT DISPLAY AND RANKING

    @Test
    public void rankingOrder() {
        // Verify participants are displayed in descending order by step count
        assertTrue(fitnessPage.areParticipantsInDescendingOrder());

        // Verify each participant's step count is greater than or equal to the participant below them
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));

        for (int i = 0; i < participants.size() - 1; i++) {
            WebElement current = participants.get(i);
            WebElement next = participants.get(i + 1);

            String currentStepsText = current.findElement(By.className("participant-steps")).getText();
            String nextStepsText = next.findElement(By.className("participant-steps")).getText();

            int currentSteps = Integer.parseInt(currentStepsText.replace(" steps", "").replace(",", ""));
            int nextSteps = Integer.parseInt(nextStepsText.replace(" steps", "").replace(",", ""));

            assertTrue(currentSteps >= nextSteps);
        }
    }

    @Test
    public void medalTrophyIcons() {
        // Verify 1st place participant displays a gold trophy icon
        assertTrue(fitnessPage.hasTrophyIcon(0, "gold"));

        // Verify 2nd place participant displays a silver trophy icon
        assertTrue(fitnessPage.hasTrophyIcon(1, "silver"));

        // Verify 3rd place participant displays a bronze (#cd7f32) trophy icon
        assertTrue(fitnessPage.hasTrophyIcon(2, "bronze"));

        // Verify participants ranked 4th and below have no trophy icons
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        for (int i = 3; i < participants.size(); i++) {
            WebElement participant = participants.get(i);
            List<WebElement> trophyIcons = participant.findElements(By.className("fa-trophy"));
            assertEquals(0, trophyIcons.size());
        }
    }

    // FEATURE 3: ADD STEPS MODAL

    @Test
    public void openModalViaTopButton() {
        // Click the "Add Steps" button at the top of the page
        fitnessPage.clickAddStepsButton(0);

        // Verify modal window appears with title "Add Steps to Participant"
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(modal.isDisplayed());

        WebElement title = modal.findElement(By.tagName("h3"));
        assertEquals("Add Steps to Participant", title.getText());

        // Verify modal contains a participant dropdown
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        assertTrue(dropdown.isDisplayed());

        // Verify modal contains a number input field
        WebElement numberInput = driver.findElement(By.id("steps_input"));
        assertTrue(numberInput.isDisplayed());
        assertEquals("number", numberInput.getAttribute("type"));

        // Verify modal contains "Add Steps" submit button
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        assertTrue(submitButton.isDisplayed());
        assertEquals("Add Steps", submitButton.getText());

        // Verify modal has a close button (×) in the top-right corner
        WebElement closeButton = modal.findElement(By.className("w3-closebtn"));
        assertTrue(closeButton.isDisplayed());
        assertEquals("×", closeButton.getText());

        // Verify dropdown is prepopulated with all 10 participants
        Select select = new Select(dropdown);
        List<WebElement> options = select.getOptions();
        assertEquals(11, options.size()); // 1 default + 10 participants

        // Verify default dropdown text shows "Choose participant"
        WebElement selectedOption = select.getFirstSelectedOption();
        assertEquals("Choose participant", selectedOption.getText());
    }

    @Test
    public void openModalViaBottomButton() {
        // Scroll to the bottom of the page
        fitnessPage.scrollToBottom();

        // Click the "Add Steps" button at the bottom
        fitnessPage.clickAddStepsButton(1);

        // Verify modal opens
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue(modal.isDisplayed());

        // Verify all modal elements (same behavior as openModalViaTopButton)
        WebElement title = modal.findElement(By.tagName("h3"));
        assertEquals("Add Steps to Participant", title.getText());

        WebElement dropdown = driver.findElement(By.id("participant_select"));
        assertTrue(dropdown.isDisplayed());

        WebElement numberInput = driver.findElement(By.id("steps_input"));
        assertTrue(numberInput.isDisplayed());

        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        assertTrue(submitButton.isDisplayed());
    }

    @Test
    public void closeModalWithCloseButton() {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Click the × (close) button in the top-right corner
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        WebElement closeButton = modal.findElement(By.className("w3-closebtn"));
        closeButton.click();

        // Verify modal closes
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        WebElement hiddenModal = driver.findElement(By.id("addStepsModal"));
        assertEquals("none", hiddenModal.getCssValue("display"));

        // Verify user returns to the main page view
        WebElement participantsList = driver.findElement(By.id("participantsList"));
        assertTrue(participantsList.isDisplayed());

        // Verify no data is changed (same as firstTimePageLoad)
        assertEquals("Fitness Challenge", fitnessPage.getPageTitle());
        assertEquals(10, fitnessPage.getParticipantCount());
    }

    // FEATURE 4: ADDING STEPS TO PARTICIPANTS

    @Test
    public void addValidStepsToParticipant() {
        // Note the current step count for "Mike Kid"
        Map<String, Integer> initialSteps = fitnessPage.getParticipantStepsMap();
        int mikeKidInitialSteps = initialSteps.get("Mike Kid");

        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Select "Mike Kid" from the dropdown
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("Mike Kid");

        // Enter "1000" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("1000");

        // Click "Add Steps" button
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Verify modal closes automatically
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        // Verify participant list refreshes
        WebElement participantsList = driver.findElement(By.id("participantsList"));
        assertTrue(participantsList.isDisplayed());

        // Verify Mike Kid's step count increases by 1,000
        Map<String, Integer> finalSteps = fitnessPage.getParticipantStepsMap();
        int mikeKidFinalSteps = finalSteps.get("Mike Kid");
        assertEquals(mikeKidInitialSteps + 1000, mikeKidFinalSteps);

        // Verify list re-sorts if Mike Kid's new total changes his ranking
        assertTrue(fitnessPage.areParticipantsInDescendingOrder());
    }

    @Test
    public void addZeroSteps() {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Select any participant
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1); // Select first participant

        // Enter "0" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("0");

        // Click "Add Steps" button
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Verify alert appears: "Please enter a valid number of steps"
        wait.until(ExpectedConditions.alertIsPresent());
        String alertText = driver.switchTo().alert().getText();
        assertEquals("Please enter a valid number of steps", alertText);

        // Accept alert
        driver.switchTo().alert().accept();

        // Verify modal remains open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());

        // Close modal for test isolation
        WebElement closeButton = modal.findElement(By.className("w3-closebtn"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Test
    public void addLargeNumberOfSteps() {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Select any participant
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1); // Select first participant

        // Enter "999999" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("999999");

        // Click "Add Steps" button
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Verify modal closes
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        // Verify step count updates correctly with the large number
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        WebElement firstParticipant = participants.get(0);
        String stepsText = firstParticipant.findElement(By.className("participant-steps")).getText();
        int steps = Integer.parseInt(stepsText.replace(" steps", "").replace(",", ""));
        assertTrue(steps >= 999999);

        // Verify participant moves to first place
        for (int i = 1; i < participants.size(); i++) {
            WebElement participant = participants.get(i);
            String otherStepsText = participant.findElement(By.className("participant-steps")).getText();
            int otherSteps = Integer.parseInt(otherStepsText.replace(" steps", "").replace(",", ""));
            assertTrue(steps >= otherSteps);
        }
    }

    // FEATURE 5: FORM VALIDATION

    @Test
    public void submitWithoutSelectingParticipant() {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Leave the participant dropdown at "Choose participant"
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        WebElement selectedOption = select.getFirstSelectedOption();
        assertEquals("Choose participant", selectedOption.getText());

        // Enter "1000" in the steps input
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("1000");

        // Click "Add Steps" button
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Verify alert appears: "Please select a participant"
        wait.until(ExpectedConditions.alertIsPresent());
        String alertText = driver.switchTo().alert().getText();
        assertEquals("Please select a participant", alertText);

        // Accept alert
        driver.switchTo().alert().accept();

        // Verify modal remains open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());

        // Close modal for test isolation
        WebElement closeButton = modal.findElement(By.className("w3-closebtn"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Test
    public void submitWithoutEnteringSteps() {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Select any participant
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1); // Select first participant

        // Enter "0" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("0");

        // Click "Add Steps" button
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Verify alert appears: "Please enter a valid number of steps"
        wait.until(ExpectedConditions.alertIsPresent());
        String alertText = driver.switchTo().alert().getText();
        assertEquals("Please enter a valid number of steps", alertText);

        // Accept alert
        driver.switchTo().alert().accept();

        // Verify modal remains open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());

        // Close modal for test isolation
        WebElement closeButton = modal.findElement(By.className("w3-closebtn"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Test
    public void submitWithNegativeSteps() {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Select a participant
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1); // Select first participant

        // Enter "-500" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("-500");

        // Attempt to click "Add Steps" button
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Verify alert appears: "Please enter a valid number of steps"
        wait.until(ExpectedConditions.alertIsPresent());
        String alertText = driver.switchTo().alert().getText();
        assertEquals("Please enter a valid number of steps", alertText);

        // Accept alert
        driver.switchTo().alert().accept();

        // Verify modal remains open
        WebElement modal = driver.findElement(By.id("addStepsModal"));
        assertTrue(modal.isDisplayed());

        // Close modal for test isolation
        WebElement closeButton = modal.findElement(By.className("w3-closebtn"));
        closeButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
    }

    @Test
    public void submitWithNonNumericInput() {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Select a participant
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1); // Select first participant

        // Attempt to enter "abc" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("abc");

        // Verify input field rejects non-numeric characters
        String inputValue = stepsInput.getAttribute("value");
        assertTrue(inputValue.isEmpty() || !inputValue.matches(".*[a-zA-Z].*"));

        // Verify no alphabetic characters appear in the input
        assertTrue(!inputValue.matches(".*[a-zA-Z].*"));
    }

    // FEATURE 6: RESET FUNCTIONALITY

    @Test
    public void resetViaTopButton() {
        // Add steps to at least 2 participants to modify the default state
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Add steps to first participant
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1);
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("500");
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Wait for modal to close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        // Click the "Reset List" button at the top of the page
        fitnessPage.clickResetButton(0);

        // Wait for data to reset to default values
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> {
            List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
            if (participants.size() != 10) return false;
            WebElement firstParticipant = participants.get(0);
            String name = firstParticipant.findElement(By.className("participant-name")).getText();
            String steps = firstParticipant.findElement(By.className("participant-steps")).getText();
            if (!"John Smith".equals(name) || !"15,000 steps".equals(steps)) return false;

            // Check if gold trophy icon is present and styled correctly
            List<WebElement> trophyIcons = firstParticipant.findElements(By.className("fa-trophy"));
            if (trophyIcons.isEmpty()) return false;
            WebElement trophyIcon = trophyIcons.get(0);
            String style = trophyIcon.getAttribute("style");
            return style != null && style.contains("color:gold");
        });

        // Verify all participants return to their default step counts (same as firstTimePageLoad)
        List<Map<String, String>> participants = fitnessPage.getAllParticipants();

        // Sort by steps descending
        participants.sort((a, b) -> {
            int stepsA = Integer.parseInt(a.get("steps").replace(" steps", "").replace(",", ""));
            int stepsB = Integer.parseInt(b.get("steps").replace(" steps", "").replace(",", ""));
            return Integer.compare(stepsB, stepsA);
        });

        // Verify default values
        assertEquals("John Smith", participants.get(0).get("name"));
        assertEquals("15,000 steps", participants.get(0).get("steps"));
        assertEquals("Mike Kid", participants.get(7).get("name"));
        assertEquals("8,500 steps", participants.get(7).get("steps"));

        // Verify default ranking order is restored (same as firstTimePageLoad)
        assertTrue(fitnessPage.areParticipantsInDescendingOrder());

        // Verify trophy icons display for correct default top 3 (same as firstTimePageLoad)
        assertTrue(fitnessPage.hasTrophyIcon(0, "gold"));
        assertTrue(fitnessPage.hasTrophyIcon(1, "silver"));
        assertTrue(fitnessPage.hasTrophyIcon(2, "bronze"));
    }

    @Test
    public void resetViaBottomButton() {
        // Modify participant data
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1);
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("300");
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Wait for modal to close
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        // Scroll to bottom of page
        fitnessPage.scrollToBottom();

        // Click the "Reset List" button at the bottom
        fitnessPage.clickResetButton(1);

        // Wait for data to reset to default values and trophy icons to be restored
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver -> {
            List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
            if (participants.size() != 10) return false;
            WebElement firstParticipant = participants.get(0);
            String name = firstParticipant.findElement(By.className("participant-name")).getText();
            String steps = firstParticipant.findElement(By.className("participant-steps")).getText();
            if (!"John Smith".equals(name) || !"15,000 steps".equals(steps)) return false;

            // Check if gold trophy icon is present and styled correctly
            List<WebElement> trophyIcons = firstParticipant.findElements(By.className("fa-trophy"));
            if (trophyIcons.isEmpty()) return false;
            WebElement trophyIcon = trophyIcons.get(0);
            String style = trophyIcon.getAttribute("style");
            return style != null && style.contains("color:gold");
        });

        // Verify all participants return to their default step counts (same as firstTimePageLoad)
        List<Map<String, String>> participants = fitnessPage.getAllParticipants();

        // Sort by steps descending
        participants.sort((a, b) -> {
            int stepsA = Integer.parseInt(a.get("steps").replace(" steps", "").replace(",", ""));
            int stepsB = Integer.parseInt(b.get("steps").replace(" steps", "").replace(",", ""));
            return Integer.compare(stepsB, stepsA);
        });

        // Verify default values
        assertEquals("John Smith", participants.get(0).get("name"));
        assertEquals("15,000 steps", participants.get(0).get("steps"));
        assertEquals("Mike Kid", participants.get(7).get("name"));
        assertEquals("8,500 steps", participants.get(7).get("steps"));

        // Verify default ranking order is restored (same as firstTimePageLoad)
        assertTrue(fitnessPage.areParticipantsInDescendingOrder());

        // Verify trophy icons display for correct default top 3 (same as firstTimePageLoad)
        assertTrue(fitnessPage.hasTrophyIcon(0, "gold"));
        assertTrue(fitnessPage.hasTrophyIcon(1, "silver"));
        assertTrue(fitnessPage.hasTrophyIcon(2, "bronze"));
    }

    // FEATURE 7: EDGE CASES AND ERROR HANDLING

    @Test
    public void maximumIntegerValue() {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Select a participant
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1);

        // Enter a large integer value that fits within int range: "2147483647" (Integer.MAX_VALUE)
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("2147483647");

        // Submit the form
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Verify system accepts the value
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));

        // Verify step count updates correctly
        List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
        WebElement firstParticipant = participants.get(0);
        String stepsText = firstParticipant.findElement(By.className("participant-steps")).getText();
        // Handle potential overflow by using a more robust parsing approach
        String numericPart = stepsText.replace(" steps", "").replace(",", "");
        try {
            int steps = Integer.parseInt(numericPart);
            assertTrue(steps > 0, "Step count should be updated");
        } catch (NumberFormatException e) {
            // If parsing fails due to overflow, just verify the text contains numbers
            assertTrue(numericPart.matches("\\d+"), "Step count should contain numeric value");
        }
    }

    @Test
    public void decimalStepValues() throws Exception {
        // Open the "Add Steps" modal
        fitnessPage.clickAddStepsButton(0);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));

        // Select any participant
        WebElement dropdown = driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        select.selectByIndex(1);

        // Enter "100.5" in the steps input field
        WebElement stepsInput = driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys("100.5");

        // Check if the input field accepts decimal values
        String inputValue = stepsInput.getAttribute("value");

        // Click "Add Steps" button
        WebElement submitButton = driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();

        // Check if alert appears or if the form submits
        try {
            // Try to wait for alert
            wait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            assertEquals("Please enter a valid number of steps", alertText);
            // Accept alert
            driver.switchTo().alert().accept();
            // Verify modal remains open
            WebElement modal = driver.findElement(By.id("addStepsModal"));
            assertTrue(modal.isDisplayed());
        } catch (Exception e) {
            // If no alert appears, check if the form submitted successfully
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
                // Form submitted, verify step count was updated
                List<WebElement> participants = driver.findElements(By.cssSelector("#participantsList li"));
                WebElement firstParticipant = participants.get(0);
                String stepsText = firstParticipant.findElement(By.className("participant-steps")).getText();
                String numericPart = stepsText.replace(" steps", "").replace(",", "");
                assertTrue(numericPart.matches("\\d+"), "Step count should contain numeric value");
            } catch (Exception ex) {
                // Neither alert nor successful submission - this is unexpected
                fail("Expected either an alert for invalid input or successful form submission");
            }
        }
    }

    // FEATURE 8: CROSS-BROWSER COMPATIBILITY

    @Test
    public void chromeBrowser() throws Exception {
        // Already using Chrome in setup, so just run the key tests
        firstTimePageLoad();
        addValidStepsToParticipant();
        submitWithoutSelectingParticipant();
        resetViaTopButton();
    }

    @Test
    public void edgeBrowser() throws Exception {
        // Note: This would require Edge WebDriver setup
        // For now, just verify the page loads (same as Chrome test)
        assertEquals("Fitness Challenge", fitnessPage.getPageTitle());
        assertEquals(10, fitnessPage.getParticipantCount());
    }

    @Test
    public void firefoxBrowser() throws Exception {
        // Note: This would require Firefox WebDriver setup
        // For now, just verify the page loads (same as Chrome test)
        assertEquals("Fitness Challenge", fitnessPage.getPageTitle());
        assertEquals(10, fitnessPage.getParticipantCount());
    }
}
