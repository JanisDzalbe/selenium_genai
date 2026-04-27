# AGENTS.md

## Project Overview
This is a Selenium Java test automation project using Java 24, Selenium 4, JUnit 5, and Cucumber for BDD. It focuses on automating tests for a fitness challenge web application at https://janisdzalbe.github.io/example-site/tasks/fitness_challenge.

## Architecture
- **Test Structure**: JUnit tests in `src/test/java/selenium/tasks/`, Cucumber features in `src/test/resources/features/`, step definitions in `src/test/java/selenium/stepDefinitions/`, runners in `src/test/java/selenium/runners/`.
- **Driver Management**: WebDriver executables in `lib/` directory. Use cross-platform initialization (e.g., set system property for chromedriver based on OS).
- **Shared State**: Static `WebDriver driver` in `Hooks.java` for Cucumber scenarios; instance `WebDriver driver` in JUnit test classes.
- **No Page Objects Yet**: Implement Page Object Model in `src/test/java/selenium/pages/` when adding new tests.
- **Utility Classes**: Implement cross-platform WebDriver setup in `src/test/java/selenium/utility/`.

## Key Patterns
- **Cucumber Runner**: `@CucumberOptions(features = "src/test/resources/features/", glue = {"selenium.stepDefinitions"}, plugin = {"pretty"}, tags = "not @bug")` in `CucumberRunner.java`.
- **Hooks Setup**: `@Before` initializes browser, `@After` quits driver (takes `Scenario scenario` for reporting). Example: `public static WebDriver driver;` in `Hooks.java`.
- **Step Definitions**: Use static driver from Hooks, e.g., `WebDriver driver = Hooks.driver;` in step classes. Example: `SampleSteps.java`.
- **JUnit Tests**: `@BeforeEach` for driver init and page open, `@AfterEach` for close. Example: Open page in `openPage()` method in `Task1.java`.
- **Feature Files**: Use Gherkin syntax with tables for data, e.g., participant lists in `FitnessChallenge1.feature` through `FitnessChallenge8.feature`.

## Workflows
- **Build/Run**: Use Maven (`mvn test`) or IDE JUnit integration. Cucumber tests run via `CucumberRunner.java`.
- **Driver Setup**: Download chromedriver.exe (Windows) or chromedriver (Linux/Mac) to `lib/`, set path in utility class.
- **Debugging**: Run individual JUnit tests or Cucumber scenarios; check browser launches from Hooks.
- **Adding Tests**: Implement TODOs in `Task1.java` (feedback page) and `Task2.java` (fitness challenge covering 8 features); write corresponding Cucumber steps if needed.

## Conventions
- **Assertions**: Use JUnit `assertEquals`, `assertTrue`; Cucumber verifies via step definitions.
- **Locators**: Use `By.id`, `By.xpath`, etc., directly in tests (refactor to Page Objects later).
- **Data Handling**: Hardcode test data in features/tables; no external data sources.
- **Error Handling**: Basic try/catch not used; rely on Selenium waits implicitly.

## Dependencies
- Selenium WebDriver 4.x
- JUnit 5
- Cucumber Java
- Maven for build

Reference: `README.md` for setup guides, `prompts.md` for task prompts.
