package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import selenium.utility.WebDriverManager;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() {
        driver = WebDriverManager.initializeChromeDriver();
        System.out.println("Browser opened on " + WebDriverManager.getOSName());
    }

    @After
    public void closeBrowser(Scenario scenario) {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed. Scenario: " + scenario.getName());
        }
    }
}
