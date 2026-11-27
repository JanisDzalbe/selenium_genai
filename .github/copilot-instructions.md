# Copilot instructions

## Project overview

- This is a Java 24 Selenium 4 test-automation project built with Maven.
- Tests are written as JUnit 5 classes and Cucumber feature files.
- The tested sites are external demo pages under `https://janisdzalbe.github.io/example-site/tasks/`.
- No application source code or local server is in this repository; browser tests run against remote pages.

## Layout

- JUnit exercises: `src/test/java/selenium/tasks/Task1.java` and `Task2.java`.
- Cucumber runner: `src/test/java/selenium/runners/CucumberRunner.java`.
- Cucumber hooks and step definitions: `src/test/java/selenium/stepDefinitions/`.
- Feature files: `src/test/resources/features/*.feature`.
- Shared Selenium utilities: `src/test/java/selenium/utility/`.
- Browser executables belong in `lib/`; they are intentionally ignored by Git.
- Intended page-object packages are `selenium/pages` and `selenium/utility`, matching `README.md`.

## Build and test

- Maven configuration is in `pom.xml`; Java release is 24.
- Run the full test suite from the repository root: `mvn test`.
- Run a clean rebuild and tests with `mvn clean test`.
- Run one JUnit class with `mvn -Dtest=selenium.tasks.Task1 test` and one Cucumber runner with `mvn -Dtest=CucumberRunner test`.
- There is no Maven Wrapper (`mvnw`/`mvnw.cmd`), so Maven must be installed or available through the IDE.
- Install Maven first if `mvn` is not available on `PATH`.
- JUnit tests can also be run directly through the IDE.
- Cucumber features are discovered by `CucumberRunner`; excluded scenarios use `@bug`.
- Surefire runs JUnit tests, while the Cucumber runner controls feature discovery and the `selenium.stepDefinitions` glue package.

## WebDriver conventions

- Use `selenium.utility.WebDriverUtility.getChromeDriver()` instead of creating browser instances directly.
- The utility checks the `webdriver.chrome.driver` system property, then the `webdriver.chrome.driver` environment variable, and otherwise uses `lib/chromedriver.exe` on Windows or `lib/chromedriver` on UNIX-like systems.
- JUnit setup should create and open the driver in `@BeforeEach`; cleanup belongs in `@AfterEach`.
- Cucumber setup/cleanup belongs in `Hooks`, with the shared driver exposed as `Hooks.driver`.
- Always call `quit()` on the driver. Do not leave browser sessions running.

## Code conventions

- Keep test code under the existing `selenium.tasks`, `selenium.stepDefinitions`, `selenium.runners`, and `selenium.utility` packages.
- JUnit 5 assertions live in `Task1` and JUnit 4 assertions may appear in Cucumber step code.
- Preserve Cucumber's `@CucumberOptions` feature path and glue package unless a request explicitly changes them.
- Keep Gherkin steps and Java step methods synchronized; each scenario belongs to a feature file and implementation.
- Many task and step methods are currently TODOs. Implement the described behavior rather than removing TODO sections unless the task has already been completed.
- Follow the Page Object Model when implementing reusable locators or interactions.
