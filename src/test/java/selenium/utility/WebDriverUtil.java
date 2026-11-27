package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverUtil {

    public static WebDriver getDriver() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "lib/chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "lib/chromedriver");
        }
        return new ChromeDriver();
    }
}
