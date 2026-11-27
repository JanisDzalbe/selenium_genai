package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() {
        String osName = System.getProperty("os.name").toLowerCase();
        Path driverPath = osName.contains("win")
                ? Paths.get("lib", "chromedriver.exe")
                : Paths.get("lib", "chromedriver");

        if (driverPath.toFile().exists()) {
            System.setProperty("webdriver.chrome.driver", driverPath.toAbsolutePath().toString());
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
    }

    @After
    public void closeBrowser(Scenario scenario) {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}