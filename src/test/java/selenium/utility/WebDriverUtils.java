package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class WebDriverUtils {
    private static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";

    private WebDriverUtils() {
    }

    public static ChromeDriver createChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String driverFileName = osName.startsWith("windows") ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of("lib", driverFileName).toAbsolutePath().normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException(
                    "ChromeDriver executable not found at " + driverPath
                            + ". Place the platform-specific driver in the lib directory."
            );
        }

        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }
}
