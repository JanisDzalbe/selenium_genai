# Copilot instructions

## Project shape
- This is a Java Selenium training project. All Java currently lives under `src/test/java`; there is no `src/main` code.
- `selenium.tasks.Task1` and `Task2` are JUnit Jupiter exercise scaffolds for the feedback and fitness-challenge pages. Their methods are TODOs, not working tests yet.
- The parallel BDD suite is in `src/test/resources/features/FitnessChallenge*.feature`; it describes the fitness challenge at `https://janisdzalbe.github.io/example-site/tasks/fitness_challenge`.
- Cucumber is wired through `selenium.runners.CucumberRunner`: feature files are discovered from `src/test/resources/features/`, step glue is `selenium.stepDefinitions`, and scenarios tagged `@bug` are excluded.
- The Cucumber implementation is incomplete: `SampleSteps` is only a placeholder. `Hooks` initializes and closes a static Chrome `WebDriver`; add step definitions matching the feature wording and use the shared hook driver.

## Test and browser conventions
- Keep JUnit Jupiter task tests in `selenium.tasks`; the Cucumber runner currently uses Cucumber's JUnit 4 runner. Preserve this distinction when wiring or changing test dependencies.
- Each task test is intended to open its target page in `@BeforeEach` and close its browser in `@AfterEach`. Cucumber scenarios use `@Before`/`@After` in `Hooks`.
- Feature files are organized by behavior (initial load, ranking, modal, adding steps, validation, reset, edge cases, cross-browser). Keep scenarios in the matching `FitnessChallengeN.feature`; use scenario outlines for repeated browser coverage as in feature 8.
- `lib/` is reserved for WebDriver binaries, but `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`. Do not assume drivers are checked in; `lib/put_drivers_here.txt` is only a placeholder.
- `pom.xml` targets Java 24 and defines Selenium, Cucumber, JUnit 4 (for the Cucumber runner), and JUnit Jupiter (for task tests). Run `mvn test` to compile and run the JUnit task classes; `CucumberRunner` is not included in Surefire's class-name patterns. The task methods are currently TODO scaffolds, so a passing run does not exercise browser behavior.
- `selenium.utility.ChromeDriverUtility.createDriver()` chooses `lib/chromedriver.exe` on Windows and `lib/chromedriver` elsewhere, sets `webdriver.chrome.driver`, then creates a `ChromeDriver`. Add local driver binaries yourself; `lib/` executables are ignored by Git.

## Useful references
- `README.md`: project setup and task layout, including the intended `selenium.utility` package and local `lib/` driver location.
- `src/test/java/selenium/tasks/Task1.java` and `Task2.java`: exercise requirements and target URLs.
- `src/test/java/selenium/utility/ChromeDriverUtility.java`: local, OS-specific ChromeDriver setup.
- `src/test/java/selenium/stepDefinitions/Hooks.java` and `SampleSteps.java`: Cucumber lifecycle and step-definition entry points.
- `src/test/java/selenium/runners/CucumberRunner.java`: feature discovery, glue, and tag filtering.
- `src/test/resources/features/`: BDD specifications; table examples use Gherkin data tables.
