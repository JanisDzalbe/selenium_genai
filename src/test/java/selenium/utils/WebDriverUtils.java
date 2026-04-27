package selenium.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Utility class for WebDriver initialization with cross-platform support.
 * Handles ChromeDriver setup for both Windows and Unix-like operating systems.
 */
public class WebDriverUtils {

    private static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";
    private static final String LIB_DIRECTORY = "lib";
    private static final String CHROME_DRIVER_WINDOWS = "chromedriver.exe";
    private static final String CHROME_DRIVER_UNIX = "chromedriver";

    /**
     * Creates and returns a new ChromeDriver instance with proper system property configuration.
     * Automatically detects the operating system and sets the appropriate ChromeDriver path.
     *
     * @return new ChromeDriver instance
     * @throws FileNotFoundException if the ChromeDriver executable is not found in the lib directory
     */
    public static WebDriver createChromeDriver() throws FileNotFoundException {
        String driverPath = getChromeDriverPath();
        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath);

        ChromeOptions options = new ChromeOptions();
        // Add any common Chrome options here if needed
        // options.addArguments("--headless"); // Uncomment for headless mode

        return new ChromeDriver(options);
    }

    /**
     * Creates and returns a new ChromeDriver instance with custom ChromeOptions.
     * Automatically detects the operating system and sets the appropriate ChromeDriver path.
     *
     * @param options custom ChromeOptions to use
     * @return new ChromeDriver instance
     * @throws FileNotFoundException if the ChromeDriver executable is not found in the lib directory
     */
    public static WebDriver createChromeDriver(ChromeOptions options) throws FileNotFoundException {
        String driverPath = getChromeDriverPath();
        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath);

        return new ChromeDriver(options);
    }

    /**
     * Determines the correct ChromeDriver path based on the operating system.
     *
     * @return absolute path to the ChromeDriver executable
     * @throws FileNotFoundException if the ChromeDriver executable is not found
     */
    private static String getChromeDriverPath() throws FileNotFoundException {
        String osName = System.getProperty("os.name").toLowerCase();
        String driverFileName;

        if (osName.contains("win")) {
            driverFileName = CHROME_DRIVER_WINDOWS;
        } else {
            // Unix-like systems (Linux, macOS, etc.)
            driverFileName = CHROME_DRIVER_UNIX;
        }

        File driverFile = new File(LIB_DIRECTORY, driverFileName);
        if (!driverFile.exists()) {
            throw new FileNotFoundException(
                String.format("ChromeDriver executable not found at: %s. " +
                    "Please ensure %s is placed in the lib/ directory.",
                    driverFile.getAbsolutePath(), driverFileName)
            );
        }

        return driverFile.getAbsolutePath();
    }

    /**
     * Checks if the ChromeDriver executable exists for the current operating system.
     *
     * @return true if the driver exists, false otherwise
     */
    public static boolean isChromeDriverAvailable() {
        try {
            getChromeDriverPath();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}
