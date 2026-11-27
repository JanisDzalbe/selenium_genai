# Copilot instructions

## Project shape
- This is a Java/Selenium browser-automation training project. The checked-in code is under `src/test/`; there is no application `src/main` layer.
- `selenium.tasks.Task1` and `Task2` are JUnit Jupiter test skeletons for the feedback and fitness-challenge pages. They still contain TODOs for page setup and assertions.
- The BDD path is separate: scenarios live in `src/test/resources/features/`, `selenium.stepDefinitions.SampleSteps` contains glue, `selenium.stepDefinitions.Hooks` owns the shared static `WebDriver`, and `selenium.runners.CucumberRunner` launches Cucumber.
- Feature files describe the fitness challenge at `https://janisdzalbe.github.io/example-site/tasks/fitness_challenge`; `Task1` targets the feedback page. Keep browser interactions aligned with those externally hosted pages and the assertions specified in the relevant feature/test skeleton.

## Test and browser conventions
- Keep direct test cases in package `selenium.tasks` and use JUnit 5 lifecycle annotations (`@BeforeEach`, `@AfterEach`) as in `Task1` and `Task2`.
- Keep Cucumber glue in the exact package `selenium.stepDefinitions` (capital `D`); the runner's `glue` setting depends on it. Put feature files under `src/test/resources/features/`.
- Cucumber currently uses the JUnit 4 runner (`@RunWith(Cucumber.class)`) while direct tests use JUnit 5. Preserve this distinction unless the test/build configuration is deliberately migrated.
- `CucumberRunner` selects `features = "src/test/resources/features/"`, uses the `pretty` plugin, and excludes `@bug` scenarios with `tags = "not @bug"`. Keep feature paths, glue, and tag behavior in sync when changing the runner.
- `selenium.utility.WebDriverUtility.createChromeDriver()` selects `lib/chromedriver.exe` on Windows or `lib/chromedriver` on Unix-like OSes, sets `webdriver.chrome.driver`, validates the local executable, and returns a `ChromeDriver`. The paths are relative to the project working directory.
- `Hooks` creates one Chrome browser per Cucumber scenario through `WebDriverUtility` and quits it in `@After`. Step definitions can access `Hooks.driver` directly.
- Browser executables are intended for `lib/`; `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`. Do not add local driver binaries to version control. The hook's TODO calls for platform-aware driver setup.
- `src/test/java/selenium/pages/` is mentioned in the README as an intended location but does not currently exist. Do not assume a Page Object layer is already implemented.

## Build and verification
- Target Java version is 27. The Maven configuration pins Selenium Java 4.54.0, JUnit 5.14.4, and Cucumber 8.1.0; the selected Selenium version is the latest stable release verified on 2026-11-27 ([official release](https://github.com/SeleniumHQ/selenium/releases/tag/selenium-4.54.0)).
- Use `mvn test` from the project root after installing Maven and Java 27. The project has no Maven wrapper. JUnit 5 is used for direct tests, and Cucumber's JUnit 4 runner is supported via the JUnit Vintage engine. Current test classes (`Task1`, `Task2`, `CucumberRunner`) do not match Surefire's default naming patterns, so `mvn test` currently compiles them but runs no tests.
- `selenium_genai.iml` marks `src/test/java` as a test source folder and inherits the project JDK; dependency and compiler versions are defined in `pom.xml`.
- Existing Cucumber examples cover initial load, ranking, modal interactions, validation, reset, edge cases, and Chrome/Edge/Firefox in `FitnessChallenge1.feature` through `FitnessChallenge8.feature`. Add or update scenarios alongside their step definitions; `FitnessChallenge1and2SampleSteps` is only a placeholder, so the feature suite is not yet runnable end-to-end.
