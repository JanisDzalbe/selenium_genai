package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class WebDriverFactory {
    private WebDriverFactory() {
    }

    public static ChromeDriver createChromeDriver() {
        Path driverPath = driverPath("chromedriver");

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable was not found: " + driverPath);
        }

        System.setProperty("webdriver.chrome.driver", driverPath.toString());
        return new ChromeDriver();
    }

    public static EdgeDriver createEdgeDriver() {
        setDriverPropertyIfPresent("webdriver.edge.driver", "msedgedriver");
        return new EdgeDriver();
    }

    public static FirefoxDriver createFirefoxDriver() {
        setDriverPropertyIfPresent("webdriver.gecko.driver", "geckodriver");
        return new FirefoxDriver();
    }

    private static Path driverPath(String executableName) {
        String executable = System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows")
                ? executableName + ".exe"
                : executableName;
        return Path.of(System.getProperty("user.dir"), "lib", executable).toAbsolutePath();
    }

    private static void setDriverPropertyIfPresent(String property, String executableName) {
        Path path = driverPath(executableName);
        if (Files.isRegularFile(path)) {
            System.setProperty(property, path.toString());
        }
    }
}
