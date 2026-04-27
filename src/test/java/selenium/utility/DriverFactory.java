package selenium.utility;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverFactory {

    public static WebDriver getDriver() {
        WebDriverManager.chromedriver().setup(); // 🔥 auto-detects Chrome 147
        return new ChromeDriver();
    }
}

//Implement this test. Use page objects. use #file:fitness_challenge.html as a reference