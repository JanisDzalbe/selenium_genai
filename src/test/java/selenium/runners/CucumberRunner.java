package selenium.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/",
        plugin = {"pretty"},
        tags = "not @bug",
        glue = {"selenium.stepDefinitions"}
)
public class CucumberRunner {

}
