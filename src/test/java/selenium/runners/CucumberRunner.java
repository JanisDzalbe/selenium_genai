package selenium.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {
                "src/test/resources/features/FitnessChallenge1.feature",
                "src/test/resources/features/FitnessChallenge2.feature",
                "src/test/resources/features/FitnessChallenge3.feature",
                "src/test/resources/features/FitnessChallenge4.feature",
                "src/test/resources/features/FitnessChallenge5.feature",
                "src/test/resources/features/FitnessChallenge6.feature",
                "src/test/resources/features/FitnessChallenge7.feature"
        },
        plugin = {"pretty"},
        tags = "not @bug",
        glue = {"selenium.stepDefinitions"}
)
public class CucumberRunner {

}
