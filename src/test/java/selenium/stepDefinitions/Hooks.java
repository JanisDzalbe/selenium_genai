package selenium.stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.net.MalformedURLException;
import java.time.Duration;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.net.MalformedURLException;
import java.time.Duration;

public class Hooks {
    public static WebDriver driver;
    static String libWithDriversLocation = System.getProperty("user.dir") + File.separator + "lib" + File.separator;

    @Before
    public void openBrowser() throws MalformedURLException {
        if (System.getProperty("os.name").contains("Mac") || System.getProperty("os.name").contains("mac"))
            System.setProperty("webdriver.chrome.driver", libWithDriversLocation + "chromedriver");
        else
            System.setProperty("webdriver.chrome.driver", libWithDriversLocation + "chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @After
    public void embedScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            try {
                scenario.log("Current Page URL is " + driver.getCurrentUrl());
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "screenshot");
            } catch (WebDriverException somePlatformsDontSupportScreenshots) {
                System.err.println(somePlatformsDontSupportScreenshots.getMessage());
            }
        }
        driver.quit();
    }
    @After
    public void closeBrowser(Scenario scenario) {
        driver.quit();
    }
}
