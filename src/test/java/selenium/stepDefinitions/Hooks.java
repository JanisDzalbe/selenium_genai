package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import selenium.utility.WebDriverFactory;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() {
        // Chrome driver from 'lib' directory (chromedriver.exe on Windows, chromedriver on UNIX-like systems)
        driver = WebDriverFactory.createChromeDriver();
    }

    @After
    public void closeBrowser(Scenario scenario) {
        driver.quit();
    }
}
