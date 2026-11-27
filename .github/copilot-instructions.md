# Copilot instructions

## Project shape
- This is a Java Selenium WebDriver training repository. The checked-in exercises target the hosted sample site at `https://janisdzalbe.github.io/example-site/tasks/`.
- `src/test/resources/features/FitnessChallenge*.feature` defines eight Fitness Challenge behavior areas: initial data, ranking, modal, step updates, validation, reset, edge cases, and browser coverage.
- `src/test/java/selenium/stepDefinitions/` contains the Cucumber glue. `Hooks.java` is intended to own browser setup/teardown (`Hooks.driver` is static); `SampleSteps.java` is only a placeholder today.
- `src/test/java/selenium/runners/CucumberRunner.java` runs Cucumber with glue `selenium.stepDefinitions`, feature path `src/test/resources/features/`, pretty output, and excludes scenarios tagged `@bug`.
- `src/test/java/selenium/tasks/Task1.java` contains JUnit Jupiter coverage for the feedback form, its confirmation page, and the Yes/No navigation flow. `Task2.java` covers Fitness Challenge behaviors in features 1–7; its feature 8 cross-browser placeholders are intentionally not implemented.

## Working in this repository
- Treat the feature files and task comments as the current acceptance criteria. Keep feature wording/step intent and Java glue aligned when implementing Cucumber scenarios.
- Cucumber execution uses the JUnit 4 `@RunWith(Cucumber.class)` runner; the direct task classes use JUnit 5 (`org.junit.jupiter`). Avoid assuming those are interchangeable runner styles.
- The feature backgrounds visit the fitness page; Task1 targets `/tasks/provide_feedback`, while Task2 targets `/tasks/fitness_challenge`.
- Browser lifecycle belongs in the hooks for Cucumber scenarios and in `@BeforeEach`/`@AfterEach` for direct JUnit tasks. Always account for teardown when setup can fail; `Hooks.driver` is currently uninitialized and its hooks are unfinished.
- `lib/` is reserved for local WebDriver executables. `.gitignore` excludes Chrome, Gecko/Firefox, and Edge driver binaries, so do not add driver executables to commits. `selenium.utility.ChromeDriverFactory.createDriver()` loads `lib/chromedriver.exe` on Windows or `lib/chromedriver` on Linux/macOS and requires the Unix driver to be executable.

## Build and verification
- `pom.xml` defines a Maven test project compiled with Java 24; run `mvn test` with Maven and a JDK 24 installation.
- Surefire includes `Task*.java` and `*Runner.java` explicitly because these repository class names do not match Maven's standard test-name patterns. JUnit Vintage lets the JUnit Platform run the JUnit 4 Cucumber runner alongside JUnit Jupiter task tests.
- `Task1` and `Task2` browser tests run against live example pages; Cucumber step definitions are still unfinished. Browser tests need an available Chrome browser and driver; Selenium Manager may resolve drivers, or use local drivers in `lib/`.
- Use IntelliJ's Java/Cucumber support for the current project setup; the runner class is `selenium.runners.CucumberRunner`. Confirm the configured JDK before interpreting IDE run failures as test failures.

## Conventions and integration details
- Keep Java packages consistent with their paths: `selenium.tasks`, `selenium.stepDefinitions`, and `selenium.runners`.
- The external example site is the system under test; tests interact with it through Selenium `WebDriver`, not application source in this repository.
- The feature specs expect actions that affect participant rankings, modal state, JavaScript alerts, page reload/reset, and Chrome/Edge/Firefox compatibility. Use Selenium browser APIs for those interactions and keep assertions tied to the exact expected UI text/data in the `.feature` files.
- The README's suggested `selenium.pages` location is not present in this checkout; do not assume there is an existing Page Object pattern.
