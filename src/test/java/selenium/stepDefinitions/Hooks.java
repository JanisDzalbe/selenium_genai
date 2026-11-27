package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import selenium.utility.DriverFactory;

public class Hooks {
    public static WebDriver driver;

    @Before(order = 0)
    public void openBrowser() {
        driver = DriverFactory.createChromeDriver();
    }

    @After
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
