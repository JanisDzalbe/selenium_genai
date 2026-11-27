package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class WebDriverUtils {
    private static final Path DRIVER_DIRECTORY = Path.of("lib");

    private WebDriverUtils() {
    }

    public static ChromeDriver createChromeDriver() {
        boolean windows = System.getProperty("os.name", "")
                .toLowerCase(Locale.ROOT)
                .startsWith("windows");
        String driverFileName = windows ? "chromedriver.exe" : "chromedriver";
        Path driverPath = DRIVER_DIRECTORY.resolve(driverFileName).toAbsolutePath().normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable was not found: " + driverPath);
        }
        if (!windows && !Files.isExecutable(driverPath)) {
            throw new IllegalStateException("ChromeDriver is not executable: " + driverPath);
        }

        System.setProperty("webdriver.chrome.driver", driverPath.toString());
        return new ChromeDriver();
    }
}
