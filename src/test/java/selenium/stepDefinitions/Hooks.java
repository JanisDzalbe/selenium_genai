package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() throws MalformedURLException {
        // TODO: set up browser based on OS and driver available in 'lib' directory
    }

    @After
    public void closeBrowser(Scenario scenario) {
        driver.quit();
    }
}
