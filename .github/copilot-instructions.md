# Copilot instructions

## Project shape
- This repository is a Java/Selenium learning project; the checked-in work is currently test scaffolding, not an application under test. The browser tests target hosted pages at `janisdzalbe.github.io/example-site`.
- `src/test/java/selenium/tasks/Task1.java` and `Task2.java` are JUnit 5 test classes for the feedback form and fitness challenge. Their setup, teardown, and test bodies are TODOs.
- The parallel Cucumber path is `src/test/resources/features/` → `src/test/java/selenium/stepDefinitions/` → `src/test/java/selenium/runners/CucumberRunner.java`. Feature files specify behavior, step definitions implement it, and the runner discovers glue in `selenium.stepDefinitions`.
- Cucumber scenarios cover fitness-challenge defaults/ranking, modal use, adding steps, validation, reset, edge cases, and browser compatibility (`FitnessChallenge1.feature`–`FitnessChallenge8.feature`). `SampleSteps.java` and `Hooks.java` are incomplete placeholders; don't assume the feature suite currently runs.
- No Page Object or WebDriver utility is present in the current checkout. `pom.xml` configures the Maven test build; the README describes intended packages, but treat the actual tree as authoritative and avoid inventing existing helpers.

## Browser and test conventions
- Keep JUnit task tests under `selenium.tasks`; Cucumber glue and hooks are under `selenium.stepDefinitions`, and the runner is under `selenium.runners`.
- `Task1`/`Task2` use per-test `@BeforeEach` and `@AfterEach` lifecycle methods with a `WebDriver` field. Cucumber's `Hooks` instead exposes a `public static WebDriver driver`; step definitions can share that instance.
- Use Selenium WebDriver interactions for the hosted pages, and preserve the behavior specified in the Gherkin tables/scenarios. Fitness step counts are initially comma-formatted (for example `8,500`); validation scenarios expect browser alerts and the modal to remain open after invalid submissions.
- `CucumberRunner` is the JUnit 4 `@RunWith(Cucumber.class)` runner (not the JUnit 5 task-test style); it points at `src/test/resources/features/`, uses `selenium.stepDefinitions` glue, enables `pretty`, and excludes `@bug` scenarios.
- Driver binaries belong in `lib/` per `lib/put_drivers_here.txt`; `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`. A Windows ChromeDriver executable is present locally, but do not assume it is portable or available in other environments. `Hooks.openBrowser()` still needs cross-platform driver setup.

## Build and verification
- Java 27 is the intended version (as confirmed by the user and named in IntelliJ metadata); the README's Java 24 reference is outdated. Run the tests with `mvn test` from the repository root; no Maven wrapper is checked in.
- `pom.xml` configures Selenium, JUnit 5, and Cucumber's JUnit 4 runner. Surefire includes `Task*.java` and `*Runner.java`, and Vintage bridges the JUnit 4 Cucumber runner into the JUnit Platform.
- Feature files describe desired coverage, not proof of implemented step definitions. Check for undefined Cucumber steps and unfinished TODOs before treating a green/available build as complete.
