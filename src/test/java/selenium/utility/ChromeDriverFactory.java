package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class ChromeDriverFactory {
    private static final String DRIVER_PROPERTY = "webdriver.chrome.driver";

    private ChromeDriverFactory() {
    }

    public static ChromeDriver createDriver() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        boolean windows = osName.startsWith("windows");
        Path driverPath = Path.of("lib", windows ? "chromedriver.exe" : "chromedriver")
                .toAbsolutePath()
                .normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException("ChromeDriver executable was not found: " + driverPath);
        }
        if (!windows && !Files.isExecutable(driverPath)) {
            throw new IllegalStateException(
                    "ChromeDriver is not executable: " + driverPath + ". Run chmod +x on the driver file.");
        }

        System.setProperty(DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }
}
