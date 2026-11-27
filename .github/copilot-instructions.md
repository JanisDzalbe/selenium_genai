# Copilot instructions

## Project shape
- This is a Java/Selenium teaching scaffold for browser automation against the remote example site, not an application with production services or a local backend.
- The main exercise is the Fitness Challenge page (`https://janisdzalbe.github.io/example-site/tasks/fitness_challenge`); `Task1.java` targets a separate feedback form.
- There are two parallel test descriptions of the exercises: JUnit classes under `src/test/java/selenium/tasks/` and Gherkin scenarios under `src/test/resources/features/`.
- The Gherkin path is `FitnessChallenge*.feature` → `CucumberRunner` → glue in `selenium.stepDefinitions`. `CucumberRunner` uses the JUnit 4 Cucumber runner and excludes scenarios tagged `@bug`.
- `Hooks` is intended to own a shared static `WebDriver` for Cucumber steps. `SampleSteps` is only a placeholder; keep new glue in `selenium.stepDefinitions` and match the feature wording.
- Java test packages mirror their source directories: tasks, step definitions, and runners. The README also mentions `selenium.utility` and `selenium.pages`, but those packages do not currently exist.

## Current state and workflow
- Much of the scaffold is intentionally incomplete: `Task1`, `Task2`, `Hooks`, and `SampleSteps` contain TODOs. Verify implementations against the feature files and task comments rather than assuming these are working tests.
- `pom.xml` targets Java 27 and declares Selenium, JUnit Jupiter, Cucumber Java/JUnit, and the JUnit Vintage engine needed by the JUnit 4-based Cucumber runner. Run the tests from the repository root with `mvn test`.
- The Cucumber runner uses source-relative feature and glue paths. Maven Surefire includes `Task*.java` and `*Runner.java` so both the exercise classes and Cucumber runner are discovered.
- Tests depend on network access to `janisdzalbe.github.io` and a compatible browser plus WebDriver executable. `lib/put_drivers_here.txt` marks the expected driver folder; driver binaries are ignored by `.gitignore` and should not be committed.
- Browser setup is not implemented yet. When adding it, preserve cross-platform behavior and ensure each test/scenario quits its own driver; use the browser-specific executables placed in `lib/` as appropriate.

## Test and implementation conventions
- JUnit exercise classes use JUnit Jupiter lifecycle annotations (`@BeforeEach`, `@AfterEach`) and `@Test`; assertions should use JUnit Jupiter.
- Cucumber hooks use `io.cucumber.java.Before`/`After`, while the runner uses `io.cucumber.junit.Cucumber`. Keep these APIs aligned if changing the runner or dependency setup.
- The fitness scenarios specify concrete user-visible behavior: initial participants/ranking, modal open/close, step updates, validation alerts, reset, and Chrome/Edge/Firefox coverage. Prefer assertions against the rendered page and modal/alert state.
- The same fitness requirements appear in `Task2.java` and `FitnessChallenge1.feature` through `FitnessChallenge8.feature`; keep both representations consistent when changing expected behavior.
- `Task1.java` documents the feedback form contract and remote URL in its setup TODO. Keep its checks focused on displayed field values, selections, and confirmation text.
- No Page Object, shared driver factory, or browser utility is present yet. Avoid assuming one exists; if introducing a helper, place utilities under `src/test/java/selenium/utility/` to match the README's intended layout.
