package selenium.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverUtil {

    public static WebDriver getChromeDriver() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverPath;

        if (os.contains("win")) {
            driverPath = "lib/chromedriver.exe";
        } else {
            driverPath = "lib/chromedriver";
        }

        System.setProperty("webdriver.chrome.driver", driverPath);
        return new ChromeDriver();
    }
}
