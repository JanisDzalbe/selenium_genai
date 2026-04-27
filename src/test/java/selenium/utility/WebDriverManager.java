package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.nio.file.Paths;

/**
 * Utility class for managing WebDriver setup with cross-platform support.
 * Automatically detects the operating system and sets the chromedriver path accordingly.
 */
public class WebDriverManager {

    /**
     * Initializes and returns a new ChromeDriver instance.
     * Automatically sets the system property for the chromedriver location based on the OS.
     *
     * @return a new ChromeDriver instance
     */
    public static WebDriver initializeChromeDriver() {
        String chromeDriverPath = getChromeDriverPath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");

        return new ChromeDriver(options);
    }

    /**
     * Determines the chromedriver executable name based on the operating system.
     *
     * @return the full path to the chromedriver executable
     */
    private static String getChromeDriverPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverName;

        if (os.contains("win")) {
            driverName = "chromedriver.exe";
        } else if (os.contains("mac")) {
            driverName = "chromedriver";
        } else if (os.contains("nix") || os.contains("nux")) {
            driverName = "chromedriver";
        } else {
            driverName = "chromedriver";
        }

        // Path to lib directory relative to project root
        String libPath = Paths.get("lib", driverName).toAbsolutePath().toString();
        return libPath;
    }

    /**
     * Gets the operating system name for logging/debugging purposes.
     *
     * @return a string describing the operating system
     */
    public static String getOSName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "Windows";
        } else if (os.contains("mac")) {
            return "macOS";
        } else if (os.contains("nix") || os.contains("nux")) {
            return "Linux";
        } else {
            return os;
        }
    }
}

