package selenium.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Paths;

/**
 * Utility class for cross-platform WebDriver initialization.
 * Handles OS detection and sets system properties for ChromeDriver.
 */
public class WebDriverUtils {

    /**
     * Detects the operating system type.
     *
     * @return true if running on Windows, false for Unix-like systems (macOS, Linux)
     */
    private static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("win");
    }

    /**
     * Gets the ChromeDriver executable name based on the operating system.
     *
     * @return "chromedriver.exe" for Windows, "chromedriver" for Unix-like systems
     */
    private static String getDriverFileName() {
        return isWindows() ? "chromedriver.exe" : "chromedriver";
    }

    /**
     * Initializes and returns a new ChromeDriver instance.
     * Automatically sets the system property for the driver location based on OS.
     * The driver must be located in the 'lib' directory.
     *
     * @return a new ChromeDriver instance
     * @throws IllegalStateException if the driver file is not found in the lib directory
     */
    public static WebDriver initializeChromeDriver() {
        String driverFileName = getDriverFileName();
        String driverPath = Paths.get("lib", driverFileName).toAbsolutePath().toString();

        System.setProperty("webdriver.chrome.driver", driverPath);

        return new ChromeDriver();
    }

    /**
     * Alternative initialization method that allows custom driver path.
     * Useful for testing or non-standard setups.
     *
     * @param customDriverPath absolute path to the ChromeDriver executable
     * @return a new ChromeDriver instance
     */
    public static WebDriver initializeChromeDriver(String customDriverPath) {
        System.setProperty("webdriver.chrome.driver", customDriverPath);
        return new ChromeDriver();
    }

    /**
     * Gets the detected operating system type as a readable string.
     *
     * @return "Windows" or "Unix-like" based on detected OS
     */
    public static String getOperatingSystem() {
        return isWindows() ? "Windows" : "Unix-like";
    }
}

