package selenium.pomPage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ThankYouPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public ThankYouPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String message() {
        wait.until(d -> d.findElement(By.id("message")).getText().length() > 0);
        return driver.findElement(By.id("message")).getText();
    }
}
