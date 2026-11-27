package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import selenium.utility.WebDriverUtils;

import java.net.MalformedURLException;

/**
 * Cucumber lifecycle hooks that initialize and tear down the shared Chrome WebDriver instance.
 */
public class Hooks {
    public static WebDriver driver;

    /**
     * Initializes a cross-platform ChromeDriver instance from {@code lib/} before each scenario.
     *
     * @throws MalformedURLException retained from starter signature
     */
    @Before
    public void openBrowser() throws MalformedURLException {
        driver = WebDriverUtils.createChromeDriver();
        driver.manage().window().maximize();
    }

    /**
     * Closes the browser session after each scenario finishes.
     *
     * @param scenario the completed Cucumber scenario
     */
    @After
    public void closeBrowser(Scenario scenario) {
        if (driver != null) {
            driver.quit();
        }
    }
}