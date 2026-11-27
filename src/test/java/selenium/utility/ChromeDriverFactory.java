package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class ChromeDriverFactory {
    private static final String DRIVER_PROPERTY = "webdriver.chrome.driver";

    private ChromeDriverFactory() {
    }

    public static ChromeDriver createChromeDriver() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String executable = osName.contains("win") ? "chromedriver.exe" : "chromedriver";
        Path driverPath = Path.of("lib", executable).toAbsolutePath().normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException(
                    "ChromeDriver executable was not found: " + driverPath
                            + ". Place " + executable + " in the project's lib directory.");
        }

        System.setProperty(DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }
}
