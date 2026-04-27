package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import selenium.stepDefinitions.Hooks;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallenge3 {

    @When("^I click the \"Add Steps\" button at the top of the page$")
    public void clickAddStepsButtonTop() {
        List<WebElement> addStepsButtons = Hooks.driver.findElements(By.id("addStepsBtn"));
        // Click the first button (top)
        addStepsButtons.get(0).click();
    }

    @Then("^a modal window appears with title \"Add Steps to Participant\"$")
    public void verifyModalAppears() {
        WebDriverWait wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(5));
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
        assertTrue("Modal should be visible", modal.isDisplayed());
        
        // Check modal title
        WebElement title = modal.findElement(By.tagName("h3"));
        assertEquals("Modal title should be 'Add Steps to Participant'", "Add Steps to Participant", title.getText());
    }

    @Then("^the modal contains a participant dropdown$")
    public void verifyModalHasDropdown() {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        assertTrue("Dropdown should be visible", dropdown.isDisplayed());
    }

    @Then("^the modal contains a number input field$")
    public void verifyModalHasNumberInput() {
        WebElement numberInput = Hooks.driver.findElement(By.id("steps_input"));
        assertTrue("Number input should be visible", numberInput.isDisplayed());
        assertEquals("Input type should be number", "number", numberInput.getAttribute("type"));
    }

    @Then("^the modal contains \"Add Steps\" submit button$")
    public void verifyModalHasSubmitButton() {
        WebElement submitButton = Hooks.driver.findElement(By.id("modal_add_steps_button"));
        assertTrue("Submit button should be visible", submitButton.isDisplayed());
        assertEquals("Button text should be 'Add Steps'", "Add Steps", submitButton.getText());
    }

    @Then("^the modal has a close button \\(×\\) in the top-right corner$")
    public void verifyModalHasCloseButton() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement closeButton = modal.findElement(By.className("w3-closebtn"));
        assertTrue("Close button should be visible", closeButton.isDisplayed());
        assertEquals("Close button text should be '×'", "×", closeButton.getText());
    }

    @Then("^the dropdown is prepopulated with all 10 participants$")
    public void verifyDropdownHasAllParticipants() {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        
        // Should have default option + 10 participants = 11 options total
        assertEquals("Dropdown should have 11 options (1 default + 10 participants)", 11, options.size());
    }

    @Then("^the default dropdown text shows \"Choose participant\"$")
    public void verifyDropdownDefaultText() {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        String selectedText = dropdown.findElement(By.cssSelector("option[selected]")).getText();
        assertEquals("Default option should be 'Choose participant'", "Choose participant", selectedText);
    }

    @When("^I scroll to the bottom of the page$")
    public void scrollToBottom() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) Hooks.driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(500);
    }

    @When("^I click the \"Add Steps\" button at the bottom$")
    public void clickAddStepsButtonBottom() {
        List<WebElement> addStepsButtons = Hooks.driver.findElements(By.id("addStepsBtn"));
        // Click the second button (bottom)
        addStepsButtons.get(1).click();
    }

    @Then("^all modal elements are present$")
    public void verifyAllModalElementsPresent() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        assertTrue("Modal should be visible", modal.isDisplayed());
        
        // Verify all key elements exist
        assertTrue("Modal title should exist", modal.findElements(By.tagName("h3")).size() > 0);
        assertTrue("Dropdown should exist", modal.findElements(By.id("participant_select")).size() > 0);
        assertTrue("Number input should exist", modal.findElements(By.id("steps_input")).size() > 0);
        assertTrue("Submit button should exist", modal.findElements(By.id("modal_add_steps_button")).size() > 0);
        assertTrue("Close button should exist", modal.findElements(By.className("w3-closebtn")).size() > 0);
    }

    @Given("^I have opened the \"Add Steps\" modal$")
    public void openAddStepsModal() {
        List<WebElement> addStepsButtons = Hooks.driver.findElements(By.id("addStepsBtn"));
        addStepsButtons.get(0).click();
        
        // Wait for modal to appear
        WebDriverWait wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("addStepsModal")));
    }

    @When("^I click the × \\(close\\) button in the top-right corner$")
    public void clickCloseButton() {
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        WebElement closeButton = modal.findElement(By.className("w3-closebtn"));
        closeButton.click();
    }

    @Then("^the modal closes$")
    public void verifyModalCloses() {
        // Wait for modal to be hidden
        WebDriverWait wait = new WebDriverWait(Hooks.driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("addStepsModal")));
        
        WebElement modal = Hooks.driver.findElement(By.id("addStepsModal"));
        String display = modal.getCssValue("display");
        assertEquals("Modal should be hidden", "none", display);
    }

    @Then("^I return to the main page view$")
    public void verifyReturnToMainPage() {
        // Verify that the main participant list is visible
        WebElement participantsList = Hooks.driver.findElement(By.id("participantsList"));
        assertTrue("Participant list should be visible", participantsList.isDisplayed());
    }

    @Then("^no data is changed from initial state$")
    public void verifyNoDataChanged() {
        // Verify that the first participant is still John Smith with 15,000 steps
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        WebElement firstParticipant = participants.get(0);
        
        String name = firstParticipant.findElement(By.className("participant-name")).getText();
        String steps = firstParticipant.findElement(By.className("participant-steps")).getText();
        
        assertEquals("First participant should still be John Smith", "John Smith", name);
        assertEquals("First participant should still have 15,000 steps", "15,000 steps", steps);
    }
}
