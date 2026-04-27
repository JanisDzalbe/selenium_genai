package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import selenium.stepDefinitions.Hooks;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static selenium.stepDefinitions.Hooks.driver;

public class SampleSteps {
    private final WebDriver driver;
    public SampleSteps() {
        // Can use static driver from Hooks directly
            this.driver = Hooks.driver;
    }

    @Given("^I am on the fitness challenge page$")
    public void iAmOnTheFitnessChallengePage() throws Throwable {
        // TODO: remove this example and implement actual steps
        driver.get("https://janisdzalbe.github.io/example-site/tasks/fitness_challenge");
    }
}
