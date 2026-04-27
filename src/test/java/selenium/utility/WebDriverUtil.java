package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverUtil {

    public static WebDriver createChromeDriver() {
        String os = System.getProperty("os.name").toLowerCase();
        String driverName = os.contains("win") ? "chromedriver.exe" : "chromedriver";
        String driverPath = "lib/" + driverName;
        System.setProperty("webdriver.chrome.driver", driverPath);
        return new ChromeDriver();
    }

}
