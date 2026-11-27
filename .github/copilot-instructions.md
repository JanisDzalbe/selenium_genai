# Copilot instructions

## Project shape
- This is a Java Selenium UI-testing exercise project; it tests the externally hosted example site, not an application implemented in this repository: `https://janisdzalbe.github.io/example-site/tasks/`.
- `src/test/java/selenium/tasks/Task1.java` and `Task2.java` are JUnit 5 exercise templates. Their TODOs describe expected scenarios; browser setup, assertions, and teardown are not implemented yet.
- BDD scenarios live in `src/test/resources/features/FitnessChallenge1.feature` through `FitnessChallenge8.feature`. They cover fitness page defaults/ranking, modal and validation behavior, resets, edge cases, and browser compatibility.
- `src/test/java/selenium/runners/CucumberRunner.java` runs those features with Cucumber's JUnit 4 runner, glue package `selenium.stepDefinitions`, pretty output, and excludes `@bug` scenarios. Step implementations belong in `SampleSteps.java`; shared browser lifecycle belongs in `Hooks.java`.
- `Hooks.driver` is static, so Cucumber steps access the scenario's browser through that shared field. `Hooks.openBrowser()` is currently a TODO; `closeBrowser()` calls `quit()`.
- The repository mixes JUnit 5 annotations in `tasks` with JUnit 4/Cucumber in `runners`; preserve the framework imports appropriate to each test style when wiring dependencies.

## Working in this repository
- `pom.xml` targets Java 21 and defines Selenium 4, JUnit 5, JUnit 4/Vintage (for the Cucumber JUnit 4 runner), and Cucumber. Run with `mvn test`; Surefire explicitly includes `Task*.java` and `CucumberRunner.java` because those names do not match Maven's default test patterns. There is no Maven wrapper.
- No browser setup utility or Page Object classes currently exist, despite the README describing those intended locations. Existing test classes use direct Selenium `By`, `WebDriver`, and `WebElement` APIs; follow that style unless adding a deliberate shared abstraction.
- `Task1` targets the feedback page; `Task2` targets the fitness challenge page. The feature files use the fitness page URL in their backgrounds.
- WebDriver executables are expected under `lib/` per the README, but `.gitignore` ignores `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`. Avoid depending on a committed browser binary; make driver setup compatible with the host environment.
- The task templates and feature files are specifications, not evidence that the remote page has been verified. Keep selectors and expected behavior grounded in the live page or its documented acceptance criteria.

## Test and browser conventions
- Keep JUnit exercise tests in package `selenium.tasks`; each test currently has `@BeforeEach`/`@AfterEach` lifecycle methods and a per-instance `WebDriver` field.
- Keep Cucumber glue in `selenium.stepDefinitions`, use `@Given`/`@When`/`@Then` annotations, and express multi-row expected participant data with Gherkin tables as in `FitnessChallenge1.feature`.
- The first fitness feature provides the canonical participant names and initial step-count table; reuse it rather than duplicating inconsistent fixture data.
- Browser alerts are part of the fitness validation scenarios; accept them before asserting that the modal remains open. Tests that mutate participant state should reset it or use a fresh page/session as the scenarios specify.
- Cross-browser coverage is an explicit requirement (Chrome, Edge, Firefox), but the current `Task2` browser tests are TODOs and there is no implemented browser factory yet.
