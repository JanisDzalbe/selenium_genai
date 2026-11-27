package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WebDriverUtil {

    // Creates and returns a ChromeDriver instance based on OS and available local driver
    public static WebDriver getDriver() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String driverPath = os.contains("win") ? "lib/chromedriver.exe" : "lib/chromedriver";
        Path path = Paths.get(driverPath);

        // Only set webdriver.chrome.driver if the local binary exists; otherwise use Selenium Manager
        if (Files.exists(path)) {
            System.setProperty("webdriver.chrome.driver", path.toAbsolutePath().toString());
        } else {
            System.clearProperty("webdriver.chrome.driver");
        }

        return new ChromeDriver();
    }
}

