# Copilot instructions

## What this repository is
- This is a Java/Selenium training project that tests externally hosted example pages; it does not contain the pages or their application code.
- The main targets are the feedback form and fitness challenge at `https://janisdzalbe.github.io/example-site/tasks/...`.
- The README describes a Maven/Selenium/JUnit 5 setup, but there is currently no `pom.xml` or Maven wrapper. Do not assume `mvn test` is runnable until build metadata is added. `.idea/misc.xml` selects JDK 27, while the README says Java 24; resolve that mismatch before changing Java compatibility.

## Test layout and execution flow
- JUnit exercise classes live in `src/test/java/selenium/tasks`: `Task1` covers feedback behavior and `Task2` covers the fitness challenge. Both currently contain TODO test bodies.
- Cucumber specifications are in `src/test/resources/features/FitnessChallenge*.feature`; they describe page load/ranking, modal use, step updates, validation, reset, edge cases, and browser compatibility.
- `selenium.runners.CucumberRunner` points Cucumber at that feature directory and the `selenium.stepDefinitions` glue package, and excludes scenarios tagged `@bug`.
- Cucumber's intended lifecycle is `Hooks` (`@Before`/`@After`) owning a shared static `WebDriver`, with step definitions using `Hooks.driver`; `Hooks.openBrowser()` is still TODO and `closeBrowser()` quits the driver. Implement setup and teardown consistently with scenario scope.
- The README mentions utility and Page Object packages, but neither currently exists. Follow the existing exercise structure unless adding that architecture is part of the task.

## Browser and external integration
- Tests depend on a locally installed browser and matching WebDriver executable. `lib/put_drivers_here.txt` marks the intended driver location; `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`, so do not add driver binaries to commits.
- When implementing driver setup, support Chrome/Edge/Firefox only where the corresponding scenario requests them, and keep OS-specific executable selection in shared setup rather than duplicating it across tests.
- Browser tests navigate to the public GitHub Pages site, so they require network access and assertions should follow the behavior specified in the corresponding `.feature` file or test TODO.
- The source currently mixes JUnit Jupiter exercise classes with a Cucumber runner using `io.cucumber.junit.Cucumber` and JUnit 4's `@RunWith`; retain that distinction when configuring test dependencies and runners.

## Project conventions
- Keep JUnit test setup/cleanup in `@BeforeEach`/`@AfterEach`; each test should start from a fresh page/browser state and close its browser afterward.
- Keep Cucumber step definitions under `selenium.stepDefinitions` so they are found by the runner's `glue` setting. `SampleSteps` is only a placeholder; replace or extend it with steps that match the feature wording.
- Treat the feature files as the behavioral contract: preserve concrete defaults, alert text, rank ordering, and the Chrome/Edge/Firefox scenario outline when implementing coverage.
- `src/test/java` is explicitly marked as a test source in `selenium_genai.iml`; there is no production source tree in the current project.
