//package selenium.tasks;

import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.api.Test;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import selenium.utils.WebDriverUtil;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {

//
// @BeforeEach
//public void openPage() {
//    driver = WebDriverUtil.getChromeDriver();
//    driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
//}
//
//@AfterEach
//public void closeBrowser() {
//    if (driver != null) {
//        driver.quit();
}
//}
// package selenium.tasks;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.Select;
//import selenium.utils.WebDriverUtil;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class Task1 {
WebDriver driver;

@BeforeEach
public void openPage() {
    driver = WebDriverUtil.getChromeDriver();
    driver.get("https://janisdzalbe.github.io/example-site/tasks/provide_feedback");
}

@AfterEach
public void closeBrowser() {
    if (driver != null) {
        driver.quit();
    }
}

@Test
public void initialFeedbackPage() throws Exception {
    // Check that all fields are empty
    WebElement nameField = driver.findElement(By.id("fb_name"));
    WebElement ageField = driver.findElement(By.id("fb_age"));
    assertEquals("", nameField.getAttribute("value"));
    assertEquals("", ageField.getAttribute("value"));

    // Check that no checkboxes are ticked (assuming checkboxes for options)
    // Adjust selectors based on actual page structure
    java.util.List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
    for (WebElement checkbox : checkboxes) {
        assertFalse(checkbox.isSelected());
    }

    // Check "Don't know" is selected in "Genre"
    Select genreSelect = new Select(driver.findElement(By.id("genre")));
    assertEquals("Don't know", genreSelect.getFirstSelectedOption().getText());

    // Check "Choose your option" in "How do you like us?"
    Select ratingSelect = new Select(driver.findElement(By.id("rating")));
    assertEquals("Choose your option", ratingSelect.getFirstSelectedOption().getText());

    // Check that the send button is blue with white letters
    WebElement sendButton = driver.findElement(By.id("send"));
    String backgroundColor = sendButton.getCssValue("background-color");
    String color = sendButton.getCssValue("color");
    // Assuming blue is rgb(0, 123, 255) or similar, and white is rgb(255, 255, 255)
    assertTrue(backgroundColor.contains("0, 123, 255") || backgroundColor.contains("blue"));
    assertTrue(color.contains("255, 255, 255") || color.contains("white"));
}

@Test
public void emptyFeedbackPage() throws Exception {
    // Click "Send" without entering any data
    WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
    sendButton.click();

    // Check fields are empty or "null"
    WebElement nameField = driver.findElement(By.id("fb_name"));
    WebElement ageField = driver.findElement(By.id("fb_age"));

    // After clicking Send with empty fields, verify they remain empty
    assertTrue(nameField.getAttribute("value").isEmpty() || "null".equals(nameField.getAttribute("value")));
    assertTrue(ageField.getAttribute("value").isEmpty() || "null".equals(ageField.getAttribute("value")));

    // Also verify no checkboxes are selected after form submission
    java.util.List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
    for (WebElement checkbox : checkboxes) {
        assertFalse(checkbox.isSelected());
    }

    // Verify radio buttons are still in default state (Don't know)
    WebElement dontKnowRadio = driver.findElement(By.cssSelector("input[type='radio'][value=''][disabled]"));
    assertTrue(dontKnowRadio.isSelected());

    // Verify dropdown still shows default option
    Select likeUsSelect = new Select(driver.findElement(By.id("like_us")));
    assertEquals("Choose your option", likeUsSelect.getFirstSelectedOption().getText());

    // Verify comment field is empty
    WebElement commentField = driver.findElement(By.cssSelector("textarea[name='comment']"));
    assertTrue(commentField.getAttribute("value").isEmpty() || "null".equals(commentField.getAttribute("value")));
}


// TODO:
//  fill the whole form, click "Send"
//  check fields are filled correctly
@Test
public void notEmptyFeedbackPage() throws Exception {
    // Fill the name field
    WebElement nameField = driver.findElement(By.id("fb_name"));
    nameField.sendKeys("John Doe");

    // Fill the age field
    WebElement ageField = driver.findElement(By.id("fb_age"));
    ageField.sendKeys("30");

    // Select language checkboxes
    java.util.List<WebElement> checkboxes = driver.findElements(By.cssSelector("input[type='checkbox']"));
    if (!checkboxes.isEmpty()) {
        checkboxes.get(0).click(); // English
        checkboxes.get(1).click(); // French
    }

    // Select gender radio button
    WebElement maleRadio = driver.findElement(By.cssSelector("input[type='radio'][value='male']"));
    maleRadio.click();

    // Select option from dropdown
    Select likeUsSelect = new Select(driver.findElement(By.id("like_us")));
    likeUsSelect.selectByVisibleText("Good");

    // Fill comment textarea
    WebElement commentField = driver.findElement(By.cssSelector("textarea[name='comment']"));
    commentField.sendKeys("Great website!");

    // Click "Send"
    WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
    sendButton.click();

    // Check fields are filled correctly (before or after submission, depending on page behavior)
    // If the form submits and redirects, this might need adjustment
    assertEquals("John Doe", nameField.getAttribute("value"));
    assertEquals("30", ageField.getAttribute("value"));
    assertTrue(maleRadio.isSelected());
    assertEquals("Good", likeUsSelect.getFirstSelectedOption().getText());
    assertEquals("Great website!", commentField.getAttribute("value"));

    // Verify checkboxes are selected
    assertTrue(checkboxes.get(0).isSelected()); // English
    assertTrue(checkboxes.get(1).isSelected()); // French
}


@Test
public void yesOnWithNameFeedbackPage() throws Exception {
    // Enter only name
    WebElement nameField = driver.findElement(By.id("fb_name"));
    nameField.sendKeys("Alice");

    // Click "Send"
    WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
    sendButton.click();

    // Assuming redirect to confirmation page, click "Yes"
    // Adjust selector if Yes button has a specific ID or class
    try {
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        yesButton.click();
    } catch (org.openqa.selenium.NoSuchElementException e) {
        // If no Yes button, perhaps it's an alert or different element
        // For now, assume button exists
    }

    // Check message text: "Thank you, Alice, for your feedback!"
    try {
        WebElement message = driver.findElement(By.xpath("//*[contains(text(), 'Thank you')]"));
        assertTrue(message.getText().contains("Alice"));
        assertTrue(message.getText().contains("feedback"));
    } catch (org.openqa.selenium.NoSuchElementException e) {
        // Message might be in a different element, e.g., div or p
        WebElement message = driver.findElement(By.cssSelector(".message, #message, p"));
        assertEquals("Thank you, Alice, for your feedback!", message.getText());
    }
}

@Test
public void yesOnWithoutNameFeedbackPage() throws Exception {
    // Click "Send" without entering anything
    WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
    sendButton.click();

    // Assuming redirect to confirmation page, click "Yes"
    try {
        WebElement yesButton = driver.findElement(By.xpath("//button[text()='Yes']"));
        yesButton.click();
    } catch (org.openqa.selenium.NoSuchElementException e) {
        // Handle if no button
    }

    // Check message text: "Thank you for your feedback!"
    try {
        WebElement message = driver.findElement(By.xpath("//*[contains(text(), 'Thank you for your feedback!')]"));
        assertEquals("Thank you for your feedback!", message.getText());
    } catch (org.openqa.selenium.NoSuchElementException e) {
        WebElement message = driver.findElement(By.cssSelector(".message, #message, p"));
        assertEquals("Thank you for your feedback!", message.getText());
    }
}

@Test
public void noOnFeedbackPage() throws Exception {
    // Fill the whole form
    WebElement nameField = driver.findElement(By.id("fb_name"));
    WebElement ageField = driver.findElement(By.id("fb_age"));
    nameField.sendKeys("Bob");
    ageField.sendKeys("25");

    // Select gender
    WebElement femaleRadio = driver.findElement(By.cssSelector("input[type='radio'][value='female']"));
    femaleRadio.click();

    // Select option from dropdown
    Select likeUsSelect = new Select(driver.findElement(By.id("like_us")));
    likeUsSelect.selectByVisibleText("Ok, i guess");

    // Fill comment
    WebElement commentField = driver.findElement(By.cssSelector("textarea[name='comment']"));
    commentField.sendKeys("Nice experience!");

    // Click "Send"
    WebElement sendButton = driver.findElement(By.cssSelector("button[type='submit']"));
    sendButton.click();

    // Assuming redirect to confirmation page, click "No"
    try {
        WebElement noButton = driver.findElement(By.xpath("//button[text()='No']"));
        noButton.click();
    } catch (org.openqa.selenium.NoSuchElementException e) {
        // Handle if no button
    }

    // Check fields are filled correctly (assuming form is still accessible or page reloads with data)
    // If redirected back, verify the fields
    assertEquals("Bob", nameField.getAttribute("value"));
    assertEquals("25", ageField.getAttribute("value"));
    assertTrue(femaleRadio.isSelected());
    assertEquals("Ok, i guess", likeUsSelect.getFirstSelectedOption().getText());
    assertEquals("Nice experience!", commentField.getAttribute("value"));
}

public static void main(String[] args) {
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectClass(Task1.class))
            .build();

    Launcher launcher = LauncherFactory.create();
    TestExecutionListener listener = new SummaryGeneratingListener();
    launcher.execute(request, listener);

    TestExecutionSummary summary = ((SummaryGeneratingListener) listener).getSummary();
    summary.printTo(new PrintWriter(System.out));
}
}