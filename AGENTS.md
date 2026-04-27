# Selenium Test Automation Project Guide

## Project Overview
This is a Java-based Selenium WebDriver project for test automation training, using Cucumber for BDD and JUnit 5 for unit tests. Tests automate interactions on example sites hosted at `https://janisdzalbe.github.io/example-site/`.

## Architecture
- **Test Structure**: JUnit tests in `src/test/java/selenium/tasks/`, Cucumber features in `src/test/resources/features/`, step definitions in `src/test/java/selenium/stepDefinitions/`.
- **Driver Management**: WebDriver executables placed in `lib/` directory. Use cross-platform initialization utility (see `Hooks.java` for setup pattern).
- **Shared State**: Static `WebDriver` instance in `Hooks.java` shared across step definitions.
- **Runner**: `CucumberRunner.java` configured with features path `src/test/resources/features/`, glue `selenium.stepDefinitions`, excluding `@bug` tagged scenarios.

## Key Patterns
- **Browser Lifecycle**: Initialize driver in `@Before` hook, quit in `@After` (see `Hooks.java`).
- **Step Definitions**: Access shared driver via `Hooks.driver` (example in `SampleSteps.java`).
- **Test Data**: Inline tables in feature files for participant lists (see `FitnessChallenge1.feature`).
- **Assertions**: Use JUnit assertions in tasks, Cucumber steps handle UI interactions.

## Workflows
- **Build**: Maven project (requires `pom.xml` with Java 24, Selenium 4, JUnit 5, Cucumber dependencies). Run tests with `mvn test` or IDE JUnit runner.
- **Driver Setup**: Download ChromeDriver to `lib/`, set system property `webdriver.chrome.driver` to `lib/chromedriver.exe` (Windows) or `lib/chromedriver` (Unix).
- **Debugging**: Use `Scenario` parameter in `@After` hook for screenshot on failure (extend `Hooks.java`).
- **New Tests**: Add `.feature` files in `src/test/resources/features/`, implement steps in `stepDefinitions/`, update `CucumberRunner.java` tags if needed.

## Dependencies
- Selenium WebDriver 4.x
- Cucumber JVM (JUnit integration)
- JUnit 5
- ChromeDriver (managed in `lib/`)

## Conventions
- Package structure: `selenium.{runners,stepDefinitions,tasks}`
- Feature files use Gherkin syntax with data tables for test data.
- Tasks follow JUnit lifecycle: `@BeforeEach` for setup, `@AfterEach` for teardown.</content>
<parameter name="filePath">C:\Users\ievaj\OneDrive\Documents\TA bootcamp\selenium_genai\AGENTS.md
