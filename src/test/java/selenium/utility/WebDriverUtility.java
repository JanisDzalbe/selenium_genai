package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public final class WebDriverUtility {
    private static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";

    private WebDriverUtility() {
    }

    public static ChromeDriver getChromeDriver() {
        Path driverPath = getConfiguredDriverPath();
        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException(
                    "ChromeDriver was not found at " + driverPath.toAbsolutePath()
                            + ". Place chromedriver.exe or chromedriver in the lib directory."
            );
        }

        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath.toAbsolutePath().toString());
        return new ChromeDriver();
    }

    public static WebDriver getDriver(String browser) {
        return switch (browser.toLowerCase(Locale.ROOT)) {
            case "chrome" -> getChromeDriver();
            case "edge" -> new EdgeDriver();
            case "firefox" -> new FirefoxDriver();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };
    }

    private static Path getConfiguredDriverPath() {
        String configuredPath = System.getProperty(CHROME_DRIVER_PROPERTY);
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv(CHROME_DRIVER_PROPERTY);
        }
        if (configuredPath != null && !configuredPath.isBlank()) {
            return Paths.get(configuredPath);
        }

        String osName = System.getProperty("os.name").toLowerCase();
        String executableName = osName.contains("win") ? "chromedriver.exe" : "chromedriver";
        return Paths.get(System.getProperty("user.dir"), "lib", executableName);
    }
}
