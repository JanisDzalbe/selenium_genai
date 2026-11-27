package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ChromeDriverFactory {
    private static final String CHROME_DRIVER_PROPERTY = "webdriver.chrome.driver";

    private ChromeDriverFactory() {
    }

    public static ChromeDriver createChromeDriver() {
        String driverFileName = isWindows()
                ? "chromedriver.exe"
                : "chromedriver";
        Path driverPath = Paths.get(System.getProperty("user.dir"), "lib", driverFileName)
                .toAbsolutePath()
                .normalize();
        File driverFile = driverPath.toFile();

        if (!driverFile.isFile()) {
            throw new IllegalStateException(
                    "ChromeDriver was not found at " + driverPath
                            + ". Place " + driverFileName + " in the lib directory.");
        }

        if (!isWindows() && !driverFile.canExecute()) {
            throw new IllegalStateException(
                    "ChromeDriver is not executable: " + driverPath
                            + ". Run 'chmod +x " + driverPath + "'.");
        }

        System.setProperty(CHROME_DRIVER_PROPERTY, driverPath.toString());
        return new ChromeDriver();
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "")
                .toLowerCase()
                .startsWith("windows");
    }
}
