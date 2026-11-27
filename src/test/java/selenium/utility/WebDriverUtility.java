package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class WebDriverUtility {
    private static final String DRIVER_PROPERTY = "webdriver.chrome.driver";

    private WebDriverUtility() {
    }

    public static ChromeDriver createChromeDriver() {
        String operatingSystem = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String driverFileName;

        if (operatingSystem.startsWith("windows")) {
            driverFileName = "chromedriver.exe";
        } else if (operatingSystem.contains("linux")
                || operatingSystem.contains("mac")
                || operatingSystem.contains("unix")
                || operatingSystem.contains("bsd")
                || operatingSystem.contains("sunos")
                || operatingSystem.contains("aix")) {
            driverFileName = "chromedriver";
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + System.getProperty("os.name"));
        }

        Path driverPath = Path.of("lib", driverFileName).toAbsolutePath().normalize();
        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver was not found at " + driverPath
                    + ". Place the platform-specific executable in the project's lib directory.");
        }
        if (!operatingSystem.startsWith("windows") && !Files.isExecutable(driverPath)) {
            throw new IllegalStateException("ChromeDriver is not executable: " + driverPath
                    + ". Grant execute permission to the file before running tests.");
        }

        System.setProperty(DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }
}
