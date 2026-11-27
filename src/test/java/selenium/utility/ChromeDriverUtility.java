package selenium.utility;

import org.openqa.selenium.chrome.ChromeDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ChromeDriverUtility {
    private ChromeDriverUtility() {
    }

    public static ChromeDriver createChromeDriver() {
        boolean windows = System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows");
        Path driverPath = findDriverPath(windows);

        if (!windows && !Files.isExecutable(driverPath)
                && !driverPath.toFile().setExecutable(true)) {
            throw new IllegalStateException("ChromeDriver is not executable: " + driverPath);
        }

        System.setProperty("webdriver.chrome.driver", driverPath.toString());
        return new ChromeDriver();
    }

    private static Path findDriverPath(boolean windows) {
        Path driverDirectory = Path.of(System.getProperty("user.dir"), "lib");
        String executable = windows ? "chromedriver.exe" : "chromedriver";
        List<Path> candidates = new ArrayList<>();
        candidates.add(driverDirectory.resolve(executable));

        if (windows) {
            candidates.add(driverDirectory.resolve("chromedriver-win64").resolve(executable));
        } else {
            String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            String architecture = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

            if (osName.contains("linux")) {
                candidates.add(driverDirectory.resolve("chromedriver-linux64").resolve(executable));
            } else if (osName.contains("mac") || osName.contains("darwin")) {
                String macDirectory = architecture.contains("aarch64") || architecture.contains("arm64")
                        ? "chromedriver-mac-arm64"
                        : "chromedriver-mac-x64";
                candidates.add(driverDirectory.resolve(macDirectory).resolve(executable));
            }
        }

        return candidates.stream()
                .filter(Files::isRegularFile)
                .map(Path::toAbsolutePath)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "ChromeDriver not found. Expected one of: " + candidates));
    }
}
