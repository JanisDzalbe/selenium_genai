package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FitnessChallengeSteps {
    private static final By PARTICIPANT_ROWS = By.cssSelector("#participantsList li");

    @Given("^I am on the fitness challenge page \"([^\"]*)\"$")
    public void openFitnessChallengePage(String url) {
        WebDriver driver = Hooks.driver;
        driver.get(url);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("participantsList")));
    }

    @When("the page loads for the first time")
    public void waitForInitialParticipants() {
        WebDriver driver = Hooks.driver;
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(currentDriver -> currentDriver.findElements(PARTICIPANT_ROWS).size() == 10);
    }

    @Then("the page displays title {string}")
    public void assertPageTitle(String expectedTitle) {
        assertEquals(expectedTitle, Hooks.driver.findElement(By.cssSelector("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void assertParticipantCount(int expectedCount) {
        assertEquals(expectedCount, Hooks.driver.findElements(PARTICIPANT_ROWS).size());
    }

    @Then("the default participants are displayed:")
    public void assertDefaultParticipants(DataTable participantTable) {
        List<Map<String, String>> expectedParticipants = participantTable.asMaps(String.class, String.class);
        List<WebElement> actualRows = Hooks.driver.findElements(PARTICIPANT_ROWS);

        assertEquals("Participant count must match the feature table",
                expectedParticipants.size(), actualRows.size());
        for (int i = 0; i < expectedParticipants.size(); i++) {
            Map<String, String> expected = expectedParticipants.get(i);
            WebElement row = actualRows.get(i);
            String actualName = row.findElement(By.cssSelector(".participant-name")).getText().trim();
            String actualSteps = row.findElement(By.cssSelector(".participant-steps"))
                    .getText().replaceAll("[^0-9]", "");
            String expectedSteps = expected.get("Steps").replaceAll("[^0-9]", "");

            assertEquals("Participant name at displayed position " + (i + 1),
                    expected.get("Name").trim(), actualName);
            assertEquals("Step count for " + actualName,
                    expectedSteps, actualSteps);
        }
    }

    @Then("{string} buttons are visible at top and bottom")
    public void assertTopAndBottomButtons(String buttonText) {
        List<WebElement> buttons = Hooks.driver.findElements(
                By.xpath("//button[normalize-space()='" + buttonText
                        + "' and not(ancestor::div[@id='addStepsModal'])]")
        );
        assertEquals("Expected exactly two " + buttonText + " buttons", 2, buttons.size());
        for (WebElement button : buttons) {
            assertTrue(buttonText + " button should be displayed", button.isDisplayed());
        }
    }
}
