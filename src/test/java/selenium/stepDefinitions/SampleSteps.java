package selenium.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SampleSteps {
    public SampleSteps() {
        // Step definitions can use Hooks.driver directly.
    }

    @Given("I am on the fitness challenge page {string}")
    public void iAmOnTheFitnessChallengePage(String url) {
        Hooks.driver.get(url);
    }

    @When("the page loads for the first time")
    public void thePageLoadsForTheFirstTime() {
        JavascriptExecutor js = (JavascriptExecutor) Hooks.driver;
        js.executeScript(
                "localStorage.removeItem('fitnessReload');" +
                "for (let i = localStorage.length - 1; i >= 0; i--) {" +
                "  const key = localStorage.key(i);" +
                "  if (key && key.startsWith('participant')) {" +
                "    localStorage.removeItem(key);" +
                "  }" +
                "}" +
                "location.reload();"
        );

        waitForPage().until(ExpectedConditions.titleIs("Fitness Challenge"));
        waitForPage().until(driver -> Hooks.driver.findElements(By.cssSelector("#participantsList li")).size() == 10);
    }

    @Then("the page displays title {string}")
    public void thePageDisplaysTitle(String expectedTitle) {
        assertEquals(expectedTitle, Hooks.driver.getTitle());
        assertEquals(expectedTitle, Hooks.driver.findElement(By.tagName("h2")).getText());
    }

    @Then("{int} participants are displayed in the list")
    public void participantsAreDisplayedInTheList(int expectedCount) {
        List<WebElement> participants = Hooks.driver.findElements(By.cssSelector("#participantsList li"));
        assertEquals(expectedCount, participants.size());
    }

    @Then("the default participants are displayed:")
    public void theDefaultParticipantsAreDisplayed(DataTable dataTable) {
        Map<String, String> expectedParticipants = new LinkedHashMap<>();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            expectedParticipants.put(row.get("Name"), row.get("Steps"));
        }

        Map<String, String> actualParticipants = new LinkedHashMap<>();
        List<WebElement> participantRows = Hooks.driver.findElements(By.cssSelector("#participantsList li"));

        for (WebElement participantRow : participantRows) {
            String name = participantRow.findElement(By.cssSelector(".participant-name")).getText().trim();
            String steps = normalizeStepCount(
                    participantRow.findElement(By.cssSelector(".participant-steps")).getText()
            );
            actualParticipants.put(name, steps);
        }

        expectedParticipants.replaceAll((name, steps) -> normalizeStepCount(steps));

        assertEquals(expectedParticipants, actualParticipants);
    }

    @Then("{string} buttons are visible at top and bottom")
    public void buttonsAreVisibleAtTopAndBottom(String buttonText) {
        List<WebElement> buttons = Hooks.driver.findElements(
                By.xpath("//button[normalize-space()='" + buttonText + "']")
        );
        long visibleButtons = buttons.stream()
                .filter(WebElement::isDisplayed)
                .count();

        assertEquals(2, visibleButtons);
    }

    private WebDriverWait waitForPage() {
        return new WebDriverWait(Hooks.driver, Duration.ofSeconds(10));
    }

    private String normalizeStepCount(String value) {
        return value.replace(" steps", "")
                .replace(",", "")
                .replace(" ", "")
                .replace("\u00A0", "")
                .trim();
    }
}
