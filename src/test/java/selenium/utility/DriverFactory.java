package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            // Detect OS
            String os = System.getProperty("os.name").toLowerCase();
            String driverName = "chromedriver";
            if (os.contains("win")) {
                driverName += ".exe";
            }
            // Set property for driver
            System.setProperty("webdriver.chrome.driver", "lib/" + driverName);

            ChromeOptions options = new ChromeOptions();
            // Add any additional options here if needed, e.g., options.addArguments("--headless");
            driver.set(new ChromeDriver(options));
        }
        return driver.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}
