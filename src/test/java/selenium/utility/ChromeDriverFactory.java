package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class ChromeDriverFactory {
    private ChromeDriverFactory() {
    }

    public static ChromeDriver createChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String executableName = osName.contains("win") ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of("lib", executableName).toAbsolutePath().normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable not found: " + driverPath);
        }
        if (!Files.isExecutable(driverPath)) {
            throw new IllegalStateException("ChromeDriver is not executable: " + driverPath);
        }

        System.setProperty("webdriver.chrome.driver", driverPath.toString());
        return new ChromeDriver();
    }
}
