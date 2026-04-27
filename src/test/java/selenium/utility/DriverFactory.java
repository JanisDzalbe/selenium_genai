package selenium.utility;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverFactory {

    /**
     * Returns a new instance of ChromeDriver with WebDriverManager handling driver setup.
     * WebDriverManager automatically downloads and manages the correct ChromeDriver version.
     *
     * @return ChromeDriver instance
     */
    public static ChromeDriver getChromeDriver() {
        // Let WebDriverManager handle the driver setup
        WebDriverManager.chromedriver().setup();

        // Create ChromeDriver with options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        return new ChromeDriver(options);
    }
}

