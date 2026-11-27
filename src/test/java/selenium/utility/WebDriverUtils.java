package selenium.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

public final class WebDriverUtils {
    private static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";

    private WebDriverUtils() {
    }

    public static ChromeDriver createChromeDriver() {
        Path driverPath = getDriverPath("chromedriver");

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable was not found: " + driverPath);
        }
        if (!isWindows() && !Files.isExecutable(driverPath)) {
            throw new IllegalStateException("ChromeDriver is not executable: " + driverPath);
        }

        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }

    public static EdgeDriver createEdgeDriver() {
        return new EdgeDriver();
    }

    private static Path getDriverPath(String driverName) {
        String executableName = isWindows() ? driverName + ".exe" : driverName;
        return Path.of("lib", executableName).toAbsolutePath().normalize();
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows");
    }
}
