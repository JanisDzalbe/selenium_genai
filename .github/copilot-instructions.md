# Copilot Instructions

## Project shape
- This is a Maven-based Java Selenium teaching project. Keep implementation code and tests under `src/test/java`; reusable browser helpers belong in `selenium.utility`, not `src/main/java`.
- JUnit 5 tests live in `selenium.tasks`; Cucumber runners and steps live in `selenium.runners` and `selenium.stepDefinitions`.
- Feature requirements are stored in `src/test/resources/features/`; implementation tasks and intended behavior are also described in `Task1.java` and `Task2.java`.
- The external test sites are hosted pages: `https://janisdzalbe.github.io/example-site/tasks/provide_feedback` and `.../tasks/fitness_challenge`.

## Maven and Java
- Use Maven 3 and Java 17 (`pom.xml` is the source of build configuration). The README mentions Java 24, but no Java 24 SDK is required or configured here.
- Run the full suite with `mvn test`.
- Run one JUnit class with `mvn -Dtest=selenium.tasks.Task1 test` (use the fully qualified class name).
- Run Cucumber through `selenium.runners.CucumberRunner`; it uses the JUnit 4 runner and scans `src/test/resources/features/` with glue `selenium.stepDefinitions`.

## Browser setup
- Place the Chrome executable in `lib/chromedriver.exe` on Windows or `lib/chromedriver` on Unix-like systems.
- Use `selenium.utility.ChromeDriverFactory.createChromeDriver()` to initialize Chrome. It sets `webdriver.chrome.driver` and throws a clear error when the local executable is missing or not executable.
- Cucumber tests share `Hooks.driver` as `public static WebDriver`. Initialize it in `@Before` and quit it in `@After`.
- JUnit task classes use an instance `WebDriver driver`, initialized in `@BeforeEach` and quit in `@AfterEach`.
- Drivers are intentionally local; do not add Selenium Manager or download logic outside the utility.

## Test conventions
- Preserve TODO-heavy test and feature structures; implement TODOs rather than replacing the task layout.
- Use JUnit Jupiter assertions in `selenium.tasks.Task1` and Task2-style `@Test` methods.
- Keep Cucumber step phrases in `selenium.stepDefinitions.SampleSteps` aligned with feature text, and use `Hooks.driver` rather than creating a second browser.
- The Cucumber runner excludes scenarios tagged `@bug`.
- Inspect the relevant feature file and task comments before editing tests because they define exact labels, values, alerts, and ranking behavior.

## Maintenance
- Update `README.md` only for user-facing setup or workflow changes.
- Do not commit downloaded drivers, build output, IDE metadata, or OS-specific files; `lib/chromedriver*` and `target/` are ignored.
