package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Path;
import java.util.Locale;

public final class ChromeDriverUtility {
    private static final String DRIVER_PROPERTY = "webdriver.chrome.driver";

    private ChromeDriverUtility() {
    }

    public static ChromeDriver createChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String driverFileName = osName.startsWith("windows") ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of("lib", driverFileName).toAbsolutePath().normalize();

        System.setProperty(DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }
}
