package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverManager {

    /**
     * Initializes and returns a ChromeDriver instance with cross-platform support.
     * Automatically detects the operating system and sets the appropriate chromedriver path.
     *
     * Supported OS:
     * - Windows: Uses lib/chromedriver.exe
     * - Linux/Mac: Uses lib/chromedriver
     *
     * @return WebDriver instance of ChromeDriver
     */
    public static WebDriver initializeChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase();
        String driverPath;

        // Detect OS and set appropriate driver path
        if (osName.contains("win")) {
            // Windows OS
            driverPath = "lib/chromedriver.exe";
        } else {
            // Unix-like OS (Linux, Mac, etc.)
            driverPath = "lib/chromedriver";
        }

        // Set the system property for WebDriver to locate the chromedriver
        System.setProperty("webdriver.chrome.driver", driverPath);

        // Return new ChromeDriver instance
        return new ChromeDriver();
    }
}

