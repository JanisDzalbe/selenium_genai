package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

/**
 * Creates WebDriver instances using driver executables from the project's {@code lib} directory.
 * Works on Windows (drivers with ".exe" extension) and on UNIX-like systems (no extension).
 */
public final class WebDriverFactory {

    /** Browsers supported by the factory and names of their driver executables. */
    public enum Browser {
        CHROME("chromedriver", "webdriver.chrome.driver"),
        EDGE("msedgedriver", "webdriver.edge.driver"),
        FIREFOX("geckodriver", "webdriver.gecko.driver");

        private final String driverName;
        private final String driverProperty;

        Browser(String driverName, String driverProperty) {
            this.driverName = driverName;
            this.driverProperty = driverProperty;
        }
    }

    private static final String LIB_DIRECTORY = System.getProperty("user.dir") + File.separator + "lib" + File.separator;

    private WebDriverFactory() {
    }

    /**
     * Sets system property for chrome driver location and returns new ChromeDriver.
     */
    public static WebDriver createChromeDriver() {
        return createDriver(Browser.CHROME);
    }

    /**
     * Sets system property for the browser's driver location and returns new driver instance.
     */
    public static WebDriver createDriver(Browser browser) {
        File driverFile = getDriverFile(browser);
        if (!driverFile.isFile()) {
            throw new IllegalStateException("Driver not found: " + driverFile + ". Put it in 'lib' directory.");
        }
        System.setProperty(browser.driverProperty, driverFile.getAbsolutePath());
        return switch (browser) {
            // English UI, so that numbers are formatted the same on every machine (e.g. "8,500")
            case CHROME -> new ChromeDriver(new ChromeOptions().addArguments("--lang=en-US"));
            case EDGE -> new EdgeDriver(new EdgeOptions().addArguments("--lang=en-US"));
            case FIREFOX -> new FirefoxDriver();
        };
    }

    /**
     * Returns true if driver executable for the browser is present in 'lib' directory.
     */
    public static boolean isDriverAvailable(Browser browser) {
        return getDriverFile(browser).isFile();
    }

    private static File getDriverFile(Browser browser) {
        return new File(LIB_DIRECTORY + browser.driverName + (isWindows() ? ".exe" : ""));
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
