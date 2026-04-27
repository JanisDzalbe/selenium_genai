package selenium.stepDefinitions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import selenium.stepDefinitions.Hooks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallenge5 {

    @When("^I leave the participant dropdown at \"Choose participant\"$")
    public void leaveDropdownAtDefault() {
        // The dropdown starts at default "Choose participant", just verify it
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        WebElement selectedOption = select.getFirstSelectedOption();
        assertEquals("Default option should remain selected", "Choose participant", selectedOption.getText());
    }

    @When("^I select a participant$")
    public void selectAParticipant() {
        WebElement dropdown = Hooks.driver.findElement(By.id("participant_select"));
        Select select = new Select(dropdown);
        // Select the first non-default option
        select.selectByIndex(1);
    }

    @When("^I attempt to click \"Add Steps\" button$")
    public void attemptClickAddStepsButton() {
        // Same as regular click - just a semantic difference in the feature
        WebElement submitButton = Hooks.driver.findElement(By.id("modal_add_steps_button"));
        submitButton.click();
    }

    @When("^I attempt to enter \"(.*)\" in the steps input field$")
    public void attemptEnterNonNumericValue(String value) {
        // Attempt to enter non-numeric characters
        WebElement stepsInput = Hooks.driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys(value);
    }

    @When("^I enter \"(.*)\" in the steps input$")
    public void enterStepsInInput(String steps) {
        WebElement stepsInput = Hooks.driver.findElement(By.id("steps_input"));
        stepsInput.clear();
        stepsInput.sendKeys(steps);
    }

    @Then("^the input field rejects non-numeric characters$")
    public void verifyInputFieldRejectsNonNumeric() {
        WebElement stepsInput = Hooks.driver.findElement(By.id("steps_input"));
        String inputValue = stepsInput.getAttribute("value");

        // The input type="number" should reject non-numeric characters
        // The value should be empty or not contain the alphabetic characters
        assertTrue("Input field should reject non-numeric characters",
            inputValue.isEmpty() || !inputValue.matches(".*[a-zA-Z].*"));
    }

    @Then("^no alphabetic characters appear in the input$")
    public void verifyNoAlphabeticInInput() {
        WebElement stepsInput = Hooks.driver.findElement(By.id("steps_input"));
        String inputValue = stepsInput.getAttribute("value");

        // Verify no alphabetic characters in the input value
        assertTrue("Input should not contain alphabetic characters",
            !inputValue.matches(".*[a-zA-Z].*"));
    }
}
