package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the thank-you page.
 */
public class FeedbackThankYouPage {
    private final WebDriver driver;

    @FindBy(id = "message")
    private WebElement message;

    public FeedbackThankYouPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getMessage() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> !message.getText().isEmpty());
        return message.getText();
    }
}
