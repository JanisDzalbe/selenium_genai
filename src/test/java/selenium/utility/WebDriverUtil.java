package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for cross-platform WebDriver initialization.
 * Handles EdgeDriver setup for both Windows and Unix-like operating systems.
 */
public class WebDriverUtil {

    /**
     * Creates and returns a new EdgeDriver instance with cross-platform driver setup.
     * Automatically detects the operating system and sets the appropriate webdriver.edge.driver property.
     *
     * @return EdgeDriver instance ready for use
     */
    public static WebDriver createEdgeDriver() {
        // Detect operating system
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isWindows = osName.contains("windows");

        // Set driver executable name based on OS
        String driverExecutable = isWindows ? "msedgedriver.exe" : "msedgedriver";

        // Build path to driver in lib directory
        Path driverPath = Paths.get("lib", driverExecutable);
        String driverPathString = driverPath.toString();

        // Set system property for EdgeDriver
        System.setProperty("webdriver.edge.driver", driverPathString);

        // Configure Edge options (optional but recommended)
        EdgeOptions options = new EdgeOptions();
        // Add any common options here if needed
        // options.addArguments("--headless"); // Uncomment for headless mode

        // Return new EdgeDriver instance
        return new EdgeDriver(options);
    }

    /**
     * Alternative method that allows custom EdgeOptions to be passed.
     *
     * @param options Custom EdgeOptions configuration
     * @return EdgeDriver instance with custom options
     */
    public static WebDriver createEdgeDriver(EdgeOptions options) {
        // Detect operating system
        String osName = System.getProperty("os.name").toLowerCase();
        boolean isWindows = osName.contains("windows");

        // Set driver executable name based on OS
        String driverExecutable = isWindows ? "msedgedriver.exe" : "msedgedriver";

        // Build path to driver in lib directory
        Path driverPath = Paths.get("lib", driverExecutable);
        String driverPathString = driverPath.toString();

        // Set system property for EdgeDriver
        System.setProperty("webdriver.edge.driver", driverPathString);

        // Return new EdgeDriver instance with provided options
        return new EdgeDriver(options);
    }
}
