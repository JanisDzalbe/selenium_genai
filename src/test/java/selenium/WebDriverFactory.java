package selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverFactory {
    public static WebDriver createChromeDriver() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverName = "chromedriver";
        if (os.contains("win")) {
            driverName += ".exe";
        }
        String driverPath = "lib/" + driverName;
        System.setProperty("webdriver.chrome.driver", driverPath);
        return new ChromeDriver();
    }
}
