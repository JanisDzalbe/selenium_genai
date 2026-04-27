package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.FileSystems;

public class DriverFactory {

    /**
     * Returns a new instance of ChromeDriver with system property set for driver location.
     * Detects the operating system and sets the chrome driver path accordingly.
     *
     * @return ChromeDriver instance
     */
    public static ChromeDriver getChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase();
        String driverPath = getDriverPath(osName);

        // Set system property for WebDriver
        System.setProperty("webdriver.chrome.driver", driverPath);

        // Create ChromeDriver with options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        return new ChromeDriver(options);
    }

    /**
     * Determines the chrome driver path based on the operating system.
     *
     * @param osName the operating system name
     * @return the path to the chrome driver executable
     */
    private static String getDriverPath(String osName) {
        String driverName;
        String libPath = "lib" + FileSystems.getDefault().getSeparator();

        if (isWindows(osName)) {
            driverName = "chromedriver.exe";
        } else if (isUnix(osName)) {
            driverName = "chromedriver";
        } else {
            throw new RuntimeException("Unsupported operating system: " + osName);
        }

        return libPath + driverName;
    }

    /**
     * Checks if the operating system is Windows.
     *
     * @param osName the operating system name
     * @return true if Windows, false otherwise
     */
    private static boolean isWindows(String osName) {
        return osName.contains("win");
    }

    /**
     * Checks if the operating system is UNIX-like (Linux, Mac, etc.).
     *
     * @param osName the operating system name
     * @return true if UNIX-like, false otherwise
     */
    private static boolean isUnix(String osName) {
        return osName.contains("nix") || osName.contains("nux") ||
               osName.contains("aix") || osName.contains("mac");
    }
}

