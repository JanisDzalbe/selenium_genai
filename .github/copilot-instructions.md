# Copilot instructions

## Project shape
- This repository is a Java Selenium training scaffold for browser-based tests of two hosted example-site pages: the feedback form and fitness challenge.
- The test source tree is under `src/test/java/selenium`; there is currently no application code under `src/main`.
- `selenium.tasks.Task1` and `Task2` are JUnit 5 test-class skeletons. Their setup, assertions, browser lifecycle, and most test bodies are still `TODO`s.
- The Cucumber flow is `src/test/resources/features/*.feature` → `CucumberRunner` → glue in `selenium.stepDefinitions`. The feature files describe fitness-challenge scenarios, but `SampleSteps` is only a placeholder and does not implement those steps.
- `CucumberRunner` uses the JUnit 4 Cucumber runner, scans `src/test/resources/features/`, binds `selenium.stepDefinitions`, prints `pretty` output, and excludes scenarios tagged `@bug`.
- Cucumber `Hooks` exposes a static `WebDriver` intended for shared use by step definitions; its `@Before` driver setup is unimplemented, while `@After` calls `driver.quit()`.
- README describes intended `selenium.utility`, `selenium.pages`, and Maven conventions, but those classes/directories and a `pom.xml` are not currently present. Do not assume those abstractions or dependencies already exist.

## Test and browser conventions
- Keep JUnit task tests in package `selenium.tasks`; place Cucumber glue in `selenium.stepDefinitions`, runners in `selenium.runners`, and `.feature` files in `src/test/resources/features`.
- The existing task classes use JUnit Jupiter annotations (`@BeforeEach`, `@AfterEach`, `@Test`). The Cucumber runner instead uses JUnit 4 (`@RunWith`), so preserve that split unless the test framework is deliberately migrated.
- The scenarios target `https://janisdzalbe.github.io/example-site/tasks/fitness_challenge` and `/tasks/provide_feedback`. They test a remote site, not a local app.
- Browser binaries are expected under `lib/` according to README and `lib/put_drivers_here.txt`; browser executable paths are gitignored (`lib/chrome*`, `lib/gecko*`, `lib/msedge*`) and may be machine-specific. `Hooks.openBrowser()` still needs cross-platform setup.
- For fitness-challenge behavior, use `Task2.java` and `FitnessChallenge1.feature` through `FitnessChallenge8.feature` as the detailed behavioral specification. Keep modal validation, participant ranking, reset, and browser-compatibility scenarios aligned across both test styles.
- `Task1.java` contains the feedback-form requirements; its initial state and confirmation-message cases are specified in its TODOs.

## Build and run
- `README.md` states Java 27, Selenium 4, JUnit 5, Maven, and `mvn test`, but this checkout has no `pom.xml` or Maven wrapper. Maven test commands therefore cannot run until a build manifest is added.
- There is no configured project build/test command discoverable in the current checkout. IntelliJ project metadata exists, but it does not replace dependency/build configuration.
- When adding or changing browser setup, account for Windows and Unix-like driver paths and ensure teardown matches each framework's lifecycle (`@AfterEach` for JUnit task tests; Cucumber `@After` for scenarios).

## Useful references
- `README.md`: intended dependencies, package layout, driver location, and setup guides.
- `src/test/java/selenium/tasks/Task1.java`, `Task2.java`: JUnit task requirements.
- `src/test/java/selenium/runners/CucumberRunner.java`, `stepDefinitions/Hooks.java`, `stepDefinitions/SampleSteps.java`: current Cucumber wiring and setup gap.
- `src/test/resources/features/`: scenario-level requirements; `lib/put_drivers_here.txt`: local driver placement.
