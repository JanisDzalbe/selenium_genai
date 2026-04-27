package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Utility class for initializing and managing Selenium WebDriver instances.
 * Provides cross-platform support for Windows and UNIX-like operating systems.
 */
public class WebDriverUtil {

    private static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";
    private static final String WINDOWS_DRIVER_PATH = "lib/chromedriver.exe";
    private static final String UNIX_DRIVER_PATH = "lib/chromedriver";

    /**
     * Returns a new instance of ChromeDriver with system property configured
     * for the detected operating system.
     *
     * @return a new ChromeDriver instance
     */
    public static ChromeDriver getChromeDriver() {
        setWebDriverPath();
        return new ChromeDriver(getDefaultChromeOptions());
    }

    /**
     * Sets the system property for Chrome driver location based on the
     * operating system (Windows or UNIX-like).
     */
    private static void setWebDriverPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverPath;

        if (os.contains("win")) {
            // Windows operating system
            driverPath = WINDOWS_DRIVER_PATH;
        } else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            // UNIX-like operating systems (Linux, Mac)
            driverPath = UNIX_DRIVER_PATH;
        } else {
            // Default to UNIX path for unknown systems
            driverPath = UNIX_DRIVER_PATH;
        }

        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath);
    }

    /**
     * Returns default ChromeOptions for WebDriver initialization.
     * Can be extended with additional options as needed.
     *
     * @return ChromeOptions instance
     */
    private static ChromeOptions getDefaultChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        // Add any default options here if needed
        return options;
    }
}
