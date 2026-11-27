package selenium.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public final class ChromeDriverUtility {

    private ChromeDriverUtility() {
    }

    public static WebDriver createDriver() {
        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String driverFileName = osName.startsWith("windows") ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of("lib", driverFileName).toAbsolutePath().normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable was not found: " + driverPath);
        }

        System.setProperty("webdriver.chrome.driver", driverPath.toString());
        return new ChromeDriver();
    }
}
