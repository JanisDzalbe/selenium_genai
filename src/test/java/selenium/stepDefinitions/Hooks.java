package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import selenium.utility.WebDriverUtils;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() {
        driver = WebDriverUtils.createChromeDriver();
    }

    @After
    public void closeBrowser(Scenario scenario) {
        driver.quit();
    }
}
