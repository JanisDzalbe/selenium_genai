# Copilot Instructions

## Project shape
- This is a Java 27 Maven teaching project for Selenium 4; nearly all Java code is under `src/test/java`, with no production `src/main` code.
- `selenium.tasks.Task1` and `Task2` are JUnit 5 exercise skeletons for the feedback and fitness-challenge pages. Their methods are TODOs, not finished browser tests.
- Cucumber scenarios live in `src/test/resources/features/`. `selenium.runners.CucumberRunner` runs them with the JUnit 4 Cucumber integration, and `selenium.stepDefinitions` contains shared hooks and step glue.
- Cucumber `Hooks` exposes its WebDriver as a static field for step definitions. It creates Chrome with `selenium.utility.ChromeDriverUtility`; use this helper rather than duplicating platform-specific driver paths.
- `ChromeDriverUtility` expects `lib/chromedriver.exe` on Windows and `lib/chromedriver` on Linux/macOS. The driver binaries are ignored by `.gitignore`; do not add them to the repository.
- Feature URLs and expected page behavior are specified in the `.feature` files and exercise TODOs. Keep scenario wording, step annotations, and glue package `selenium.stepDefinitions` aligned.

## Build and test workflow
- Build from the repository root with `mvn test`; Maven compiles both Java test sources and loads dependencies declared in `pom.xml`.
- The existing classes are named `Task1`, `Task2`, and `CucumberRunner`, so they do not match Maven Surefire's default test-name patterns. Do not treat a green default `mvn test` as proof that the exercise or Cucumber scenarios ran.
- Java sources use the Maven test source set (`src/test/java`); feature resources are loaded from `src/test/resources/features/` by `CucumberRunner`.
- Browser tests need a locally installed Chrome browser and the matching executable in `lib/`. `ChromeDriverUtility` sets `webdriver.chrome.driver` to its absolute path before constructing `ChromeDriver`.

## Local conventions
- Keep exercise classes in the existing `selenium.tasks`, `selenium.stepDefinitions`, and `selenium.runners` packages; utility code belongs in `selenium.utility`.
- JUnit exercise classes use Jupiter annotations (`@BeforeEach`, `@AfterEach`, `@Test`). Cucumber uses `io.cucumber` annotations and a JUnit 4 `@RunWith(Cucumber.class)` runner; preserve the corresponding Maven dependencies.
- Tests target the public sample pages hosted at `janisdzalbe.github.io/example-site`; avoid introducing assumptions about local application servers or fixtures that are not present in this repository.
