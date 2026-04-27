package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import selenium.utility.DriverManager;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() {
        driver = DriverManager.createChromeDriver();
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        // Do NOT set implicitlyWait here — all page objects use explicit WebDriverWait.
        // Mixing implicit and explicit waits causes compounding timeouts.
    }

    @After
    public void embedScreenshot(Scenario scenario) {
        if (scenario.isFailed() && driver != null) {
            try {
                scenario.log("Current Page URL is " + driver.getCurrentUrl());
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "screenshot");
            } catch (WebDriverException e) {
                System.err.println(e.getMessage());
            }
        }
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
