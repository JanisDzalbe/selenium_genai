package selenium.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.openqa.selenium.chrome.ChromeDriver;

public final class WebDriverUtils {
    private static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";

    private WebDriverUtils() {
    }

    public static ChromeDriver createChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        boolean windows = osName.startsWith("windows");
        String executableName = windows ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of("lib", executableName).toAbsolutePath().normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable was not found: " + driverPath);
        }
        if (!windows && !Files.isExecutable(driverPath)) {
            throw new IllegalStateException("ChromeDriver is not executable: " + driverPath);
        }

        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }
}
