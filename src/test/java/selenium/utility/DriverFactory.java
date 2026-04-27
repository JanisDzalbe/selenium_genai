package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.nio.file.Paths;

/**
 * Utility class for cross-platform WebDriver initialization.
 * Handles OS detection and loads ChromeDriver from lib/ directory.
 */
public class DriverFactory {

    /**
     * Initializes and returns a new ChromeDriver instance.
     * Automatically detects OS and loads the correct chromedriver executable.
     *
     * @return Initialized ChromeDriver instance
     * @throws IllegalArgumentException if driver executable is not found
     */
    public static WebDriver createChromeDriver() {
        String driverPath = getChromedriverPath();
        System.setProperty("webdriver.chrome.driver", driverPath);
        return new ChromeDriver();
    }

    /**
     * Builds the correct path to chromedriver executable based on the operating system.
     *
     * @return Absolute path to chromedriver executable
     * @throws IllegalArgumentException if driver executable is not found
     */
    private static String getChromedriverPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String driverFileName = getDriverFileName(osName);
        String driverPath = Paths.get("lib", driverFileName).toAbsolutePath().toString();
        return driverPath;
    }

    /**
     * Determines the correct driver executable filename based on OS.
     *
     * @param osName the operating system name (lowercase)
     * @return driver filename (e.g., "chromedriver.exe" for Windows)
     */
    private static String getDriverFileName(String osName) {
        if (osName.contains("win")) {
            return "chromedriver.exe";
        } else if (osName.contains("mac") || osName.contains("linux")) {
            return "chromedriver";
        } else {
            throw new IllegalArgumentException("Unsupported operating system: " + System.getProperty("os.name"));
        }
    }
}

