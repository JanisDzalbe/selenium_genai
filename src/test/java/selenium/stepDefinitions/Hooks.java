package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import selenium.utils.WebDriverUtils;

import java.io.FileNotFoundException;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() {
        try {
            driver = WebDriverUtils.createChromeDriver();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to initialize ChromeDriver: " + e.getMessage(), e);
        }
    }

    @After
    public void closeBrowser(Scenario scenario) {
        if (driver != null) {
            driver.quit();
        }
    }
}
