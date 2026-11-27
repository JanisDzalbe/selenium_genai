# Copilot instructions

## Project shape
- This is a Java Selenium WebDriver learning project; browser tests and Cucumber glue live under `src/test/java`, and Gherkin specifications live under `src/test/resources/features`.
- `selenium.tasks.Task1` and `Task2` are JUnit Jupiter browser tests for the feedback form and fitness challenge pages; their page interactions are implemented with Page Objects.
- Cucumber is a separate, currently skeletal test path: `selenium.runners.CucumberRunner` points at the feature directory and `selenium.stepDefinitions` glue; `Hooks` is intended to own a shared static `WebDriver`.
- The feature files describe tests against the public example site at `https://janisdzalbe.github.io/example-site/tasks/`. Fitness Challenge scenarios cover initial data, ranking, modal validation, reset, and Chrome/Edge/Firefox.
- Page Objects for the feedback flow and fitness challenge live under `selenium.pages`; use Selenium `PageFactory` and `@FindBy` for page elements. `selenium.utility.WebDriverUtils` provides cross-platform ChromeDriver initialization.

## Build and test setup
- The project uses Java 24, Selenium 4, JUnit 5, and Maven; run tests with `mvn test` after installing the configured JDK and Maven.
- The task tests exercise browser-visible behavior on the external example site; Cucumber hooks and some step definitions may still contain TODOs.
- `CucumberRunner` uses the JUnit 4 Cucumber runner, while the task classes import JUnit Jupiter. Keep both test entry points and their engine/runner dependencies aligned if adding Maven configuration.
- Driver binaries are expected under `lib/` per `README.md`; `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`, so local executables are not committed. `lib/put_drivers_here.txt` marks the directory.
- Use `WebDriverUtils.createChromeDriver()` to choose `lib/chromedriver.exe` on Windows or `lib/chromedriver` on other OSes; it sets `webdriver.chrome.driver` before constructing `ChromeDriver`.

## Existing conventions
- Keep task tests in package `selenium.tasks` and the existing `Task1` / `Task2` files; use JUnit Jupiter lifecycle annotations (`@BeforeEach`, `@AfterEach`) and `@Test` for these classes.
- Keep Cucumber glue in `selenium.stepDefinitions`, runner classes in `selenium.runners`, and Gherkin files in `src/test/resources/features`. Existing feature names use `FitnessChallenge<number>.feature`.
- Cucumber feature steps are plain-language specifications; implement matching annotated glue methods rather than changing scenarios just to fit implementation.
- `Hooks` currently declares a static `WebDriver driver`; feature step classes may use this shared driver. Initialize it before scenarios and quit it after each scenario when completing this path.
- Tests are expected to open the specified external example-site pages and verify browser-visible behavior. Avoid assuming local application source or a local server exists.

## Key files
- `README.md` — project overview, intended packages, browser-driver location, and setup guidance.
- `src/test/java/selenium/tasks/Task1.java` and `Task2.java` — JUnit task outlines and acceptance criteria.
- `src/test/java/selenium/pages/` — Page Objects used by the JUnit task tests.
- `src/test/java/selenium/utility/WebDriverUtils.java` — cross-platform ChromeDriver initialization.
- `src/test/java/selenium/stepDefinitions/Hooks.java` — intended Cucumber browser lifecycle.
- `src/test/java/selenium/stepDefinitions/SampleSteps.java` — placeholder step definition.
- `src/test/java/selenium/runners/CucumberRunner.java` — Cucumber feature path, glue package, and `not @bug` tag filter.
- `src/test/resources/features/` — Gherkin acceptance scenarios.
