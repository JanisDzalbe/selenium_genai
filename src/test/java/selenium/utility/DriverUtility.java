package selenium.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.openqa.selenium.chrome.ChromeDriver;

public final class DriverUtility {
    private static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";

    private DriverUtility() {
    }

    public static ChromeDriver createChromeDriver() {
        String executable = System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows")
                ? "chromedriver.exe"
                : "chromedriver";
        Path driverPath = Path.of(System.getProperty("user.dir"), "lib", executable)
                .toAbsolutePath()
                .normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable not found: " + driverPath);
        }

        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }
}
