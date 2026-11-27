# Copilot instructions

## Project shape
- This is a Java Selenium training repository; its checked-in Java code and Gherkin are under `src/test`, with no application source tree.
- `selenium.tasks.Task1` and `Task2` are JUnit Jupiter exercise skeletons for the feedback and fitness-challenge pages. Their lifecycle methods and test bodies are still TODOs.
- The fitness challenge requirements are described in `src/test/resources/features/FitnessChallenge1.feature` through `FitnessChallenge8.feature`: initial state/ranking, modal use, adding steps, validation, reset, edge cases, and browser coverage.
- Cucumber wiring is `selenium.runners.CucumberRunner` → feature files → glue in `selenium.stepDefinitions`. `SampleSteps` is only a placeholder; `Hooks` currently declares a shared static `WebDriver`, but browser setup is TODO and teardown calls `quit()`.
- `Task1`/`Task2` use JUnit 5 imports, while `CucumberRunner` uses the JUnit 4 Cucumber runner. Preserve this distinction when extending either test style.

## Current state and workflow
- `pom.xml` targets Java 27 and provides test-scoped Selenium, JUnit Jupiter, JUnit 4, and Cucumber dependencies. Run `mvn test` from the repository root.
- Use JDK 27 to run Maven: Maven may select a different JDK from `java` on `PATH`, and an older compiler cannot honor `maven.compiler.release=27`.
- Surefire's default naming patterns do not discover `Task1`, `Task2`, or `CucumberRunner`; currently `mvn test` compiles the sources but executes no tests. The JUnit methods and Cucumber steps/features are still scaffolds.
- The Cucumber runner points at `src/test/resources/features/`, uses glue package `selenium.stepDefinitions`, emits `pretty` output, and excludes scenarios tagged `@bug` (`tags = "not @bug"`).
- Feature backgrounds navigate to `https://janisdzalbe.github.io/example-site/tasks/fitness_challenge`; `Task1` targets the feedback page at `/tasks/provide_feedback`. These are external test sites, not local app routes.

## Selenium and test conventions
- Keep test classes in `src/test/java/selenium/tasks`, Cucumber glue in `src/test/java/selenium/stepDefinitions`, and feature specifications in `src/test/resources/features`.
- The existing JUnit test pattern declares a `WebDriver` field, opens a page in `@BeforeEach`, and closes the browser in `@AfterEach`; the methods are scaffolds, so there is not yet a shared driver utility or page-object implementation to reuse.
- Cucumber hooks are scenario-level (`@Before`/`@After`); `Hooks.driver` is static and is intended to be shared with step definitions. Initialize it before steps run and ensure it is quit after each scenario.
- `lib/` is reserved for local browser-driver executables. `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`; do not commit downloaded driver binaries. The README's suggested cross-platform driver utility is not present yet.
- Keep scenarios aligned with the feature files' observable behavior and wording, especially validation alerts, list reordering, default participant counts, and reset behavior. The task classes and feature files are parallel exercise descriptions, not an already implemented automation framework.
- No Page Object classes or shared utility classes currently exist; add one only when implementing a concrete repeated behavior, and keep it within the documented `selenium.pages` or `selenium.utility` test packages if following the README's intended layout.
