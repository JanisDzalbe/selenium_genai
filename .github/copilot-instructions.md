# Copilot instructions

## Project shape
- This is a Java Selenium training starter. The implementation and tests are under `src/test`; there is no production source tree, page-object package, or shared WebDriver utility.
- `src/test/java/selenium/tasks/Task1.java` and `Task2.java` are JUnit 5 exercise templates. Their setup, teardown, and test bodies are TODOs; use their comments as the assignment requirements, not as evidence of implemented behavior.
- `src/test/resources/features/FitnessChallenge1.feature` through `FitnessChallenge8.feature` describe the fitness-challenge scenarios. They cover initial state/ranking, modal behavior, step updates, validation, reset, edge cases, and browsers.
- Cucumber wiring is separate: `CucumberRunner.java` points at `src/test/resources/features/`, uses glue `selenium.stepDefinitions`, emits `pretty` output, and excludes `@bug`. `SampleSteps.java` is only a placeholder; feature scenarios do not yet have matching step implementations.
- `Hooks.java` owns a static `WebDriver` shared with step definitions. Its browser setup is still TODO; the `@After` hook calls `quit()`. Coordinate driver lifecycle through this hook for Cucumber scenarios.

## Editing conventions
- Keep JUnit exercises in package `selenium.tasks`; keep Cucumber hooks/steps in `selenium.stepDefinitions` and runners in `selenium.runners`.
- Exercise target URLs and expected UI behavior are specified inline in `Task1.java`, `Task2.java`, and the feature files; use these as the source of truth when implementing scenarios.
- `Task2` tests the fitness-challenge page; the feature files use the same published URL. `Task1` targets the feedback page.
- `prompts.md` records generated setup prompts, not implemented project architecture. Do not treat its requested Maven setup or driver utility as existing code.
- `pom.xml` targets Java 27 and declares Selenium, Cucumber, and JUnit dependencies. Packages such as `selenium.utility` and `selenium.pages` are described as intended structure in the README but are not currently present in tracked files.
- `lib/put_drivers_here.txt` marks the intended location for browser drivers. `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`; avoid adding driver binaries to source control.

## Build and test workflow
- Run `mvn test` to compile and execute tests; Surefire explicitly includes `Task*.java` and `*Runner.java` because those project names do not match Maven's default discovery patterns.
- The task classes use JUnit Jupiter; `CucumberRunner` uses the JUnit 4 runner API and is executed through the JUnit Vintage engine. Keep these engines/dependencies aligned if changing test-framework versions.
- Browser tests navigate to the public `janisdzalbe.github.io/example-site` pages and need browser access. Cucumber feature scenarios currently have no matching step implementations, so the Cucumber suite is expected to report undefined steps until `SampleSteps.java` is implemented.
