package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Page object for the feedback confirmation page. */
public class ThankYouPage {
    private final WebDriver driver;

    @FindBy(id = "message")
    private WebElement message;

    /** Initializes this page's elements for the supplied browser. */
    public ThankYouPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /** Waits for and returns the confirmation message. */
    public String getMessage() {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOf(message))
                .getText();
    }
}
