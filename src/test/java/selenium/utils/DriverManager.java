package selenium.utils;

import org.openqa.selenium.chrome.ChromeDriver;
import java.io.File;

/**
 * Utility class for managing Selenium WebDriver instances.
 * Handles cross-platform chromedriver setup for Windows and UNIX-like systems.
 */
public class DriverManager {

    /**
     * Creates and returns a new ChromeDriver instance with appropriate
     * system properties set for the current operating system.
     *
     * @return a new ChromeDriver instance
     */
    public static ChromeDriver createChromeDriver() {
        setChromeDriverPath();
        return new ChromeDriver();
    }

    /**
     * Sets the system property for the chromedriver location based on
     * the current operating system.
     *
     * Windows: lib/chromedriver.exe
     * UNIX-like: lib/chromedriver
     */
    private static void setChromeDriverPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        String projectRoot = System.getProperty("user.dir");
        String driverPath;

        if (osName.contains("win")) {
            // Windows system
            driverPath = projectRoot + File.separator + "lib" + File.separator + "chromedriver.exe";
        } else {
            // UNIX-like system (Linux, macOS, etc.)
            driverPath = projectRoot + File.separator + "lib" + File.separator + "chromedriver";
        }

        // Verify that the driver file exists
        File driverFile = new File(driverPath);
        if (!driverFile.exists()) {
            throw new RuntimeException(
                "ChromeDriver not found at: " + driverPath + "\n" +
                "Please ensure the chromedriver executable is placed in the 'lib' directory."
            );
        }

        // Set the system property for Selenium WebDriver
        System.setProperty("webdriver.chrome.driver", driverPath);
    }
}

