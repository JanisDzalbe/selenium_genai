# Copilot Instructions

## Project shape
- This repository is a Java Selenium test-automation scaffold; current Java sources are under `src/test/java`, with no `src/main` tree.
- JUnit task classes live in `selenium.tasks` (`Task1` covers the feedback form; `Task2` covers the fitness challenge). Their setup, browser actions, and assertions are mostly TODOs, so do not treat them as working examples.
- Cucumber features live in `src/test/resources/features/FitnessChallenge*.feature`; step definitions and hooks are in `selenium.stepDefinitions`, and `selenium.runners.CucumberRunner` is the JUnit 4 runner.
- The Cucumber runner discovers `src/test/resources/features/`, uses glue `selenium.stepDefinitions`, enables pretty output, and excludes `@bug` scenarios. Feature files describe behavior that is not necessarily implemented yet.
- The README describes intended Page Object and utility packages, but those directories/classes are not present yet. Follow the existing `selenium.*` package naming when adding them.

## Browser and test conventions
- JUnit task classes use Jupiter annotations (`@BeforeEach`, `@AfterEach`, `@Test`); Cucumber uses `io.cucumber` annotations and a shared static `Hooks.driver`. Keep lifecycle changes consistent with the relevant runner.
- Task classes declare the driver as the Selenium `WebDriver` interface; preserve that abstraction when adding browser setup.
- `Hooks.openBrowser()` is still TODO; `Hooks.closeBrowser()` calls `driver.quit()`. Ensure browser setup succeeds before teardown can use the driver.
- The README expects browser drivers in `lib/`. `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`, so local driver executables are not intended to be committed.
- The task URLs are hosted at `janisdzalbe.github.io/example-site`; `Task1` targets `tasks/provide_feedback` and `Task2` targets `tasks/fitness_challenge`.
- Fitness challenge scenarios cover initial participant data/ranking, modal and validation behavior, resetting, edge cases, and Chrome/Edge/Firefox compatibility. Keep expected values and behavior aligned with the corresponding feature files and `Task2`.
- The feature files use Gherkin `Background`, `Scenario`, and data tables; the sample step definition is only a placeholder and does not implement those steps yet.

## Build and environment
- `pom.xml` targets Java 27 (matching `.idea/misc.xml`) and configures Selenium, JUnit Jupiter, the JUnit Vintage engine, and Cucumber's JUnit 4 runner.
- Run the suites with `mvn test`; Surefire explicitly includes `Task*.java` and `CucumberRunner.java` because those names do not match Maven's default test patterns.
- The project combines JUnit Jupiter task classes with a Cucumber JUnit 4 runner; keep both test engines available if changing the Maven test configuration.
- `lib/put_drivers_here.txt` marks the local driver directory. The checked-in `chromedriver.exe` may not be available in another checkout because the ignore rule excludes it.
