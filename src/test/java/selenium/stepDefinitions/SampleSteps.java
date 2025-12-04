package selenium.stepDefinitions;

import io.cucumber.java.en.Given;
import selenium.stepDefinitions.Hooks;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SampleSteps {

    public SampleSteps() {
        // Can use static driver from Hooks directly
    }

    @Given("^Some Example$")
    public void someStep() throws Throwable {
        // TODO: remove this example and implement actual steps
    }
}
