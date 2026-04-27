# GitHub Copilot Instructions

## Project Overview
This is a Selenium WebDriver project for testing web applications, focusing on a fitness challenge app and feedback form. Uses Java 24, Selenium 4, JUnit 5, and Cucumber for BDD testing.

## Architecture
- **Test Structure**: JUnit tests in `src/test/java/selenium/tasks/`, Cucumber features in `src/test/resources/features/`, step definitions in `selenium.stepDefinitions`, runners in `selenium.runners`.
- **Driver Management**: WebDriver executables in `lib/` directory. Implement cross-platform initialization in utility class (see Hooks.java TODO).
- **Browser Lifecycle**: Static WebDriver in `selenium.stepDefinitions.Hooks` with @Before/@After hooks for setup/teardown.
- **Data Flow**: Tests interact with live web pages (e.g., https://janisdzalbe.github.io/example-site/tasks/fitness_challenge), no local data storage.

## Key Patterns
- **Page Object Model**: Planned for `src/test/java/selenium/pages/` package (see README.md task list).
- **Cucumber Integration**: Features map to JUnit tests in Task2.java; e.g., FitnessChallenge1.feature covers initial page load scenarios.
- **Cross-Browser Testing**: Tests for Chrome, Edge, Firefox in Task2.java; requires driver setup per OS.
- **Validation**: Uses alerts for form errors (e.g., "Please enter a valid number of steps" in addZeroSteps test).

## Workflows
- **Build & Run**: Maven project (`mvn test`); Java 24 required. Run Cucumber via `selenium.runners.CucumberRunner` class.
- **Driver Setup**: Set system property `webdriver.chrome.driver` to `lib/chromedriver.exe` (Windows) or equivalent for Unix.
- **Debugging**: Tests fail on TODO implementations; implement Hooks.openBrowser() first for basic functionality.
- **Adding Tests**: Write Gherkin in features/, implement steps in SampleSteps.java, update CucumberRunner if needed.

## Conventions
- **Package Naming**: `selenium.tasks` for JUnit tests, `selenium.stepDefinitions` for Cucumber steps.
- **Assertions**: Use JUnit 5 assertions (e.g., `assertEquals`, `assertTrue` in SampleSteps.java).
- **Step Definitions**: Regex patterns for Cucumber (e.g., `^Some Example$` in SampleSteps.java).
- **Test Data**: Hardcoded in tests/features; e.g., default participants list in FitnessChallenge1.feature.
- **Error Handling**: Alerts for invalid inputs; accept with `driver.switchTo().alert().accept()`.

## Dependencies
- Selenium WebDriver 4
- JUnit 5
- Cucumber (JUnit integration)
- ChromeDriver (place in lib/)
