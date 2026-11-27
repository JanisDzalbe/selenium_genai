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
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String driverName = osName.startsWith("windows") ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of(System.getProperty("user.dir"), "lib", driverName)
                .toAbsolutePath()
                .normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable was not found: " + driverPath);
        }
        if (!Files.isExecutable(driverPath)) {
            throw new IllegalStateException("ChromeDriver is not executable: " + driverPath);
        }

        System.setProperty(DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }
}
