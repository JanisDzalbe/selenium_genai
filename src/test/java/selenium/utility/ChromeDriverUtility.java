package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class ChromeDriverUtility {
    private ChromeDriverUtility() {
    }

    public static ChromeDriver createChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String executableName = osName.startsWith("windows") ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of("lib", executableName).toAbsolutePath().normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable not found: " + driverPath);
        }

        System.setProperty("webdriver.chrome.driver", driverPath.toString());
        return new ChromeDriver();
    }
}
