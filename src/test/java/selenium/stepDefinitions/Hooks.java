
package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import selenium.utility.WebDriverUtility;

public class Hooks {

    public static WebDriver driver;

    @Before
    public void openBrowser() {
        driver = WebDriverUtility.createChromeDriver();
    }

    @After
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
