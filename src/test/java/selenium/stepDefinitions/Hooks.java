package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import selenium.utility.DriverFactory;

import java.net.MalformedURLException;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() throws MalformedURLException {
        driver = DriverFactory.getChromeDriver();
    }

    @After
    public void closeBrowser(Scenario scenario) {
        if (driver != null) {
            driver.quit();
        }
    }
}
