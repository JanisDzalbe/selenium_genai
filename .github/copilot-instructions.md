# Copilot instructions

## Project shape
- This is a Java Selenium training project; the current code is under `src/test/java`, with Gherkin specifications under `src/test/resources/features`.
- `selenium.tasks.Task1` and `Task2` are JUnit Jupiter exercise skeletons. Their `@BeforeEach`, `@AfterEach`, and `@Test` methods are mostly TODOs; implement the feedback and fitness-challenge exercises there.
- Cucumber is a separate, currently unfinished path: `selenium.runners.CucumberRunner` points at the feature directory and `selenium.stepDefinitions` glue; `Hooks` and `SampleSteps` are placeholders.
- The eight `FitnessChallenge*.feature` files describe the fitness-challenge behaviors (initial data, ranking, modal, adding steps, validation, reset, edge cases, and browsers). Keep step definitions aligned with their Given/When/Then wording and shared background URL.
- Exercise pages are hosted externally: feedback at `https://janisdzalbe.github.io/example-site/tasks/provide_feedback` and fitness challenge at `https://janisdzalbe.github.io/example-site/tasks/fitness_challenge`. Tests need network access and a compatible browser.

## Test and browser setup
- Use a Java 27 JDK (`JAVA_HOME` must point to it when running Maven); `pom.xml` configures Java release 27. The README documents `mvn test`. Maven dependencies cover Selenium, JUnit Jupiter/Vintage, JUnit 4, and Cucumber.
- Browser drivers belong in `lib/`; `.gitignore` excludes `lib/chrome*`, `lib/gecko*`, and `lib/msedge*`. Do not add driver executables to commits. `selenium.utility.WebDriverUtility.createChromeDriver()` selects `chromedriver.exe` on Windows and `chromedriver` on Unix-like systems, sets `webdriver.chrome.driver`, and requires the executable to exist (and be executable on Unix).
- `Task1` creates its Chrome driver with `WebDriverUtility` and uses the `selenium.pages.FeedbackPage` page object for feedback-form locators, interactions, and waits.
- Cucumber `Hooks.openBrowser()` creates a Chrome driver with `WebDriverUtility`; its static `Hooks.driver` is shared with the step definitions.
- `FitnessChallenge8.feature` expects Chrome, Edge, and Firefox coverage; the local driver setup is not yet implemented for all three.
- `selenium.pages.FeedbackPage` is the Page Object Model example: keep browser locators and user-facing page operations there, leaving test methods to express assertions and scenarios.

## Existing patterns to preserve
- JUnit exercises use per-test lifecycle methods (`@BeforeEach` / `@AfterEach`) and keep a `WebDriver` field on the test class; close the browser during teardown.
- JUnit exercises use Jupiter (`org.junit.jupiter`), while `CucumberRunner` uses JUnit 4 (`@RunWith(Cucumber.class)`). Treat these as separate test entry points; the runner explicitly loads the feature directory.
- Cucumber currently uses a static `Hooks.driver`, a `@Before`/`@After` hook, glue package `selenium.stepDefinitions`, and `plugin = {"pretty"}`. The runner excludes `@bug` scenarios with `tags = "not @bug"`.
- Each feature has a `Background` that opens its page before scenarios; keep browser setup and teardown consistent with that per-scenario lifecycle.
- `Task1.java` specifies feedback-form defaults and exact confirmation text; preserve those expected values when implementing its assertions.
- `Task2` documents exact expected participant names/counts, validation messages, ranking, and top/bottom controls. Use the feature files and test TODOs as the source of expected behavior rather than inventing different fixtures.
- Keep Java package declarations consistent with the directory tree (`selenium.tasks`, `selenium.stepDefinitions`, `selenium.runners`) and place new Gherkin specs in `src/test/resources/features`.
