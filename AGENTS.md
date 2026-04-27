# AGENTS.md - Selenium GenAI Test Automation Project

## Architecture Overview
This is a Selenium WebDriver test automation project using Java 24, Selenium 4, JUnit 5, and Cucumber for BDD testing. The project targets a fitness challenge web application at `https://janisdzalbe.github.io/example-site/tasks/fitness_challenge` and a feedback form at `https://janisdzalbe.github.io/example-site/tasks/provide_feedback`.

- **Test Structure**: JUnit tests in `src/test/java/selenium/tasks/`, Cucumber features in `src/test/resources/features/`, step definitions in `src/test/java/selenium/stepDefinitions/`
- **Driver Management**: Place WebDriver executables in `lib/` directory; use cross-platform initialization (see `Hooks.java` for setup)
- **Shared State**: Use static `WebDriver driver` in `Hooks.java` for all tests and steps

## Key Patterns
- **Driver Initialization**: Implement OS-specific path setting in `Hooks.openBrowser()` (e.g., `System.setProperty("webdriver.chrome.driver", "lib/chromedriver.exe")` on Windows)
- **Step Definitions**: Access shared driver via `Hooks.driver`; use JUnit assertions like `assertEquals` and `assertTrue`
- **Feature Files**: Use Gherkin syntax with data tables for participant lists (see `FitnessChallenge1.feature`)
- **Test Data**: Hardcode expected values in tests (e.g., default step counts: Mike Kid=8500, Jill Watson=12000, etc.)

## Workflows
- **Build**: Maven project (`mvn test`); requires Java 24; no custom plugins
- **Run Tests**: Execute `CucumberRunner.java` for BDD tests; run individual JUnit classes for unit tests
- **Debugging**: Use IDE's JUnit runner; inspect page elements with `driver.findElement(By.xpath("//..."))`
- **Adding Tests**: Create new `.feature` files in `src/test/resources/features/`, implement steps in `SampleSteps.java` or new classes

## Conventions
- **Package Naming**: `selenium.tasks` for JUnit tests, `selenium.stepDefinitions` for Cucumber steps, `selenium.runners` for test runners
- **Element Locators**: Prefer XPath for complex elements (e.g., `By.xpath("//button[text()='Add Steps']")`)
- **Assertions**: Use JUnit 5 assertions; check text with `assertEquals("expected", element.getText())`
- **Modal Handling**: Wait for modals to appear/disappear; handle alerts with `driver.switchTo().alert().accept()`
- **Browser Compatibility**: Test across Chrome, Edge, Firefox; initialize different drivers accordingly

## Integration Points
- **External URLs**: Test against live site; no local setup required
- **Dependencies**: Selenium WebDriver, Cucumber-Java, JUnit-Jupiter (via Maven)
- **No Page Objects**: Tests directly interact with elements; consider adding `selenium.pages` package for Page Object Model as per project tasks

Reference: `README.md` for setup, `Task2.java` for comprehensive test examples, `Hooks.java` for driver lifecycle</content>
<parameter name="filePath">C:\Users\ilmar\Documents\Training\selenium_genai\AGENTS.md
    