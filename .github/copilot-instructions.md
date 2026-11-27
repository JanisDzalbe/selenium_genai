# Copilot instructions

## Project shape
- This is a Java Selenium training project; the current repository is a test-source skeleton, not a finished application.
- `src/test/java/selenium/tasks/Task1.java` and `Task2.java` contain JUnit Jupiter test stubs for the feedback and fitness-challenge pages. Their setup, teardown, and test bodies are TODOs.
- The Cucumber path is separate: `.feature` scenarios under `src/test/resources/features/` describe the fitness challenge, `selenium.stepDefinitions` is the glue package, and `selenium.runners.CucumberRunner` starts the suite.
- `SampleSteps.java` is only a placeholder step; the feature steps are not implemented yet. `Hooks.java` owns the shared static `WebDriver` and quits it after each scenario.
- The browser tests target the externally hosted pages at `janisdzalbe.github.io/example-site`; they do not exercise a locally implemented web app.

## Test and browser conventions
- Keep JUnit Jupiter task tests in `selenium.tasks`; keep Cucumber hooks/step definitions in `selenium.stepDefinitions`, and keep Cucumber feature files in `src/test/resources/features/`.
- The Cucumber runner uses `@RunWith(Cucumber.class)`, glue `selenium.stepDefinitions`, the `pretty` plugin, and excludes scenarios tagged `@bug` (`tags = "not @bug"`). Preserve these settings when adding scenarios unless the suite's intended scope changes.
- Cucumber hooks expose the driver as `Hooks.driver`; steps can use that shared instance. Ensure each scenario gets a browser and that it is quit in the `@After` hook.
- `Task1` tests the feedback form; `Task2` tests fitness-challenge behavior. Their method names/comments describe intended coverage but do not imply working implementations.
- `lib/put_drivers_here.txt` marks the intended location for browser-driver executables. `lib/chrome*`, `lib/gecko*`, and `lib/msedge*` are ignored by Git; do not assume binaries are checked in. Driver selection/OS handling is still a TODO in `Hooks.openBrowser()`.

## Build and repository state
- The intended Java version is 27, configured by `maven.compiler.release` in `pom.xml`; `.idea/misc.xml` also names an `openjdk-27` SDK. The POM supplies Selenium, JUnit Jupiter, JUnit Vintage (for the JUnit 4 Cucumber runner), and Cucumber dependencies.
- Run the suite with `mvn test`. Surefire explicitly includes `Task*.java` and `*Runner.java`, since the task classes do not use the default `*Test` naming pattern. Current feature steps and browser hooks are TODOs, so Cucumber execution is not expected to pass until implemented.
- `.gitignore` excludes `target/`, IDE metadata, and local WebDriver binaries. Keep generated build output and machine-specific driver files out of commits.
- When implementing the skeleton, verify both test styles: task classes use `org.junit.jupiter` (JUnit 5), while the Cucumber runner imports the JUnit 4 integration (`io.cucumber.junit` / `org.junit.runner.RunWith`).
