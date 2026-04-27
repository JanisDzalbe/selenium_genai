package selenium.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class Task1 {
    WebDriver driver;

    @BeforeEach
    public void openPage() {
        // TODO
        //  initialize the driver
        //  open page https://janisdzalbe.github.io/example-site/tasks/provide_feedback
    }

    @AfterEach
    public void closeBrowser() {
        // TODO
        //  close the browser
    }

    @Test
    public void initialFeedbackPage() throws Exception {
        // TODO:
        //  check that all field are empty and no ticks are clicked
        //  "Don't know" is selected in "Genre"
        //  "Choose your option" in "How do you like us?"
        //  check that the button send is blue with white letters
    }

    @Test
    public void emptyFeedbackPage() throws Exception {
        // TODO:ss
        //  click "Send" without entering any data
        //  check fields are empty or "null"
    }

    @Test
    public void notEmptyFeedbackPage() throws Exception {
        // TODO:
        //  fill the whole form, click "Send"
        //  check fields are filled correctly
    }

    @Test
    public void yesOnWithNameFeedbackPage() throws Exception {
        // TODO:
        //  enter only name
        //  click "Send"
        //  click "Yes"
        //  check message text: "Thank you, NAME, for your feedback!"
    }

    @Test
    public void yesOnWithoutNameFeedbackPage() throws Exception {
        // TODO:
        //  click "Send" (without entering anything)
        //  click "Yes"
        //  check message text: "Thank you for your feedback!"
    }

    @Test
    public void noOnFeedbackPage() throws Exception {
        // TODO:
        //  fill the whole form
        //  click "Send"
        //  click "No"
        //  check fields are filled correctly
    }
}