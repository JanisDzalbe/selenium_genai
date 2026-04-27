# Copilot Instructions for Selenium AI Project

## Project Overview
This is a Selenium WebDriver test automation project in Java, using Maven for build management. It teaches fundamentals of test automation with JUnit 5 unit tests and Cucumber BDD scenarios.

## Architecture
- **Test Structure**: Follow the package convention in `src/test/java/selenium/`:
  - `tasks/`: JUnit 5 test classes (e.g., `Task1.java` with `@Test` methods)
  - `stepDefinitions/`: Cucumber step definitions (e.g., `Hooks.java` for setup/teardown, `SampleSteps.java` for Gherkin steps)
  - `runners/`: Cucumber runner classes (e.g., `CucumberRunner.java` with `@RunWith(Cucumber.class)`)
  - `utils/`: Utility classes (e.g., `WebDriverUtil.java` for driver initialization)
- **Resources**: Feature files in `src/test/resources/features/` (e.g., `FitnessChallenge1.feature` with Gherkin scenarios)
- **Drivers**: WebDriver executables in `lib/` directory (e.g., `chromedriver.exe`)

## Key Patterns
- **Driver Initialization**: Use `WebDriverUtil.getChromeDriver()` for cross-platform setup. It detects OS via `System.getProperty("os.name")` and sets `webdriver.chrome.driver` to "lib/chromedriver.exe" (Windows) or "lib/chromedriver" (Unix).
- **Shared Driver in Cucumber**: `Hooks.java` uses a static `WebDriver driver` initialized in `@Before` and quit in `@After`.
- **Assertions**: JUnit 4 style (`org.junit.Assert.assertEquals`) in `SampleSteps.java`; JUnit 5 (`org.junit.jupiter.api.Assertions.assertEquals`) in `Task1.java`.
- **Page Object Model**: Planned for `selenium.pages` package, but not implemented yet.

## Build and Run
- Build: `mvn clean compile`
- Run all tests: `mvn test` (runs both JUnit and Cucumber)
- Run JUnit tasks: IDE or `mvn test -Dtest=selenium.tasks.Task1`
- Run Cucumber: Execute `CucumberRunner.java` in IDE, features filtered by tags like `not @bug`

## Dependencies
- Selenium Java 4.15.0
- Cucumber Java 7.14.0 with JUnit 4 platform
- JUnit 4.13.2 and JUnit Jupiter 5.10.0
- Hamcrest 2.2

## Conventions
- Use relative paths for drivers from project root.
- TODO comments mark incomplete code (e.g., test implementations in `Task1.java`).
- Feature files use Gherkin with `Given/When/Then` steps mapped in `stepDefinitions`.
- No custom Maven plugins; standard lifecycle.</content>
<parameter name="filePath">C:\Users\User\Desktop\SeleniumAi\.github\copilot-instructions.md
