package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void openBrowser() {
        Path chromeDriver = Path.of("lib", "chromedriver.exe");
        if (Files.exists(chromeDriver)) {
            System.setProperty("webdriver.chrome.driver", chromeDriver.toAbsolutePath().toString());
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1920,1080", "--no-sandbox",
                "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @After
    public void closeBrowser(Scenario scenario) {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
