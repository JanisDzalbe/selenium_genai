package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    /**
     * Initializes and returns a ChromeDriver instance with cross-platform support.
     * Automatically detects OS and sets the appropriate chromedriver path.
     *
     * @return a new instance of ChromeDriver
     */
    public static ChromeDriver getChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase();
        String driverPath;

        if (osName.contains("win")) {
            // Windows
            driverPath = "lib/chromedriver.exe";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            // Unix-like systems (Linux, macOS)
            driverPath = "lib/chromedriver";
        } else {
            throw new RuntimeException("Unsupported operating system: " + osName);
        }

        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        return new ChromeDriver(options);
    }

    /**
     * Initializes and returns a ChromeDriver instance with custom options.
     *
     * @param options ChromeOptions to be applied to the driver
     * @return a new instance of ChromeDriver
     */
    public static ChromeDriver getChromeDriver(ChromeOptions options) {
        String osName = System.getProperty("os.name").toLowerCase();
        String driverPath;

        if (osName.contains("win")) {
            // Windows
            driverPath = "lib/chromedriver.exe";
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            // Unix-like systems (Linux, macOS)
            driverPath = "lib/chromedriver";
        } else {
            throw new RuntimeException("Unsupported operating system: " + osName);
        }

        System.setProperty("webdriver.chrome.driver", driverPath);
        return new ChromeDriver(options);
    }
}

