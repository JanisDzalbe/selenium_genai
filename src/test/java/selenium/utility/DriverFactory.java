package selenium.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.openqa.selenium.chrome.ChromeDriver;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static ChromeDriver createChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String executable = osName.startsWith("windows") ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of("lib", executable).toAbsolutePath().normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable not found: " + driverPath);
        }

        System.setProperty("webdriver.chrome.driver", driverPath.toString());
        return new ChromeDriver();
    }
}
