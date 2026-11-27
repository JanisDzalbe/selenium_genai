package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import selenium.utility.WebDriverUtility;
import org.openqa.selenium.WebDriver;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() {
        driver = WebDriverUtility.createChromeDriver();
    }

    @After
    public void closeBrowser(Scenario scenario) {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
