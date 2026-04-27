package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class ChromeDriverFactory {
    private static final String WINDOWS_DRIVER_NAME = "chromedriver.exe";
    private static final String UNIX_DRIVER_NAME = "chromedriver";

    private ChromeDriverFactory() {
    }

    public static ChromeDriver createDriver() {
        Path driverPath = resolveDriverPath();
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }

    private static Path resolveDriverPath() {
        boolean windows = System.getProperty("os.name")
                .toLowerCase(Locale.ROOT)
                .contains("win");

        Path driverPath = Path.of("lib", windows ? WINDOWS_DRIVER_NAME : UNIX_DRIVER_NAME)
                .toAbsolutePath()
                .normalize();

        if (!Files.isRegularFile(driverPath)) {
            throw new IllegalStateException(
                    "ChromeDriver was not found. Expected file: " + driverPath
                            + ". Place the driver binary in the project's lib directory."
            );
        }

        if (!windows && !Files.isExecutable(driverPath)) {
            throw new IllegalStateException(
                    "ChromeDriver exists but is not executable: " + driverPath
                            + ". Run chmod +x on the driver file."
            );
        }

        return driverPath;
    }
}
