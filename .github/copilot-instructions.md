# Copilot instructions

## Project shape
- This is a Java/Selenium learning project; the checked-in code is primarily test code under `src/test`.
- `src/test/java/selenium/tasks/Task1.java` and `Task2.java` are JUnit Jupiter exercise scaffolds for the feedback and fitness-challenge pages.
- The parallel BDD exercises are Gherkin files in `src/test/resources/features/FitnessChallenge*.feature`; `CucumberRunner.java` runs that directory and binds step definitions in `selenium.stepDefinitions`.
- `SampleSteps.java` is only a placeholder, and `Hooks.java` has a TODO for browser setup. Most feature scenarios therefore do not yet have working step implementations.
- The current source has no Page Objects or utility package, despite the older README describing those as planned conventions. Follow actual code unless adding those structures is part of the task.

## Browser and test patterns
- The tests target public pages hosted at `https://janisdzalbe.github.io/example-site/tasks/` (`provide_feedback` and `fitness_challenge`); tests exercise the live UI rather than a local application.
- JUnit task classes use `@BeforeEach`/`@AfterEach` and an instance `WebDriver` field. Initialize and quit the browser per test in these lifecycle methods.
- Cucumber shares its browser through `Hooks.driver` (static); setup and teardown belong in `@Before` and `@After` hooks. Guard teardown if setup can fail before a driver is created.
- The Cucumber runner uses JUnit 4 (`@RunWith(Cucumber.class)`), while task classes use JUnit 5. Preserve this distinction when changing test wiring or dependencies.
- Cucumber is configured with `features = "src/test/resources/features/"`, glue package `selenium.stepDefinitions`, pretty output, and tag filter `not @bug`. Put scenario language in `.feature` files and matching annotated glue methods in `SampleSteps` or focused step-definition classes.
- `Task2` describes the expected fitness challenge behavior in detail; its scenario outline is the clearest source for participant data, validation messages, ranking, reset, and browser coverage.
- `Task1` documents the feedback form contract. Prefer its expected interactions and assertions over inventing new page behavior.

## Drivers and repository workflow
- `lib/chromedriver.exe` is the only browser driver currently present. `lib/put_drivers_here.txt` marks the intended driver location; `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*` binaries.
- `selenium.utility.DriverFactory.createChromeDriver()` configures `webdriver.chrome.driver` from `lib/` and selects `chromedriver.exe` on Windows or `chromedriver` on Unix-like systems. Keep the matching executable in `lib/`; browser initialization should reuse this factory.
- `pom.xml` targets Java 24 and declares Selenium, JUnit Jupiter, JUnit 4/Vintage, and Cucumber dependencies. Run the suite with `mvn test`; Surefire includes the nonstandard `Task*.java` test names and `*Runner.java` Cucumber runner.
- The Cucumber scenarios and task classes are still scaffolds, so `mvn test` may report undefined Cucumber steps or incomplete browser setup until those TODOs are implemented.
- The IntelliJ module file (`selenium_genai.iml`) marks `src/test/java` as a test source root, but does not declare project dependencies. Use IDE test execution only when the IDE has the required Java, Selenium, JUnit, and Cucumber libraries configured.
- The README contains project setup notes and links to the original Selenium setup guides; `prompts.md` records exercise/task prompts, not build configuration.
- `Task1` and `Task2` exercise the feedback and fitness challenge flows end to end. Cucumber hooks and step definitions are still TODO scaffolds, not passing tests or implemented behavior.
