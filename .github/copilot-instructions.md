# Selenium GenAI Repository Notes

## Project shape
- This repository is a test-only Java/Selenium training project; there is no `src/main/java` application code.
- JUnit task skeletons live in `src/test/java/selenium/tasks/Task1.java` and `Task2.java`.
- Cucumber feature files are split by topic in `src/test/resources/features/FitnessChallenge1.feature` through `FitnessChallenge8.feature`.
- The Cucumber entry point is `src/test/java/selenium/runners/CucumberRunner.java`, and shared browser lifecycle for Cucumber is in `src/test/java/selenium/stepDefinitions/Hooks.java`.

## Build and test workflow
- The project is now Maven-based via `pom.xml`.
- Use `mvn test` to run the JUnit task classes; Surefire is configured to include `Task*.java` because these files do not follow Maven's default `*Test` naming.
- `CucumberRunner.java` is present for running the feature suite manually from the IDE after step definitions are implemented.
- The code targets Java 24 in Maven (`maven.compiler.release=24`), even if newer JDKs are installed locally.

## Browser setup
- Local browser driver binaries are expected in `lib/`.
- `src/test/java/selenium/utility/ChromeDriverFactory.java` is the single place that resolves the ChromeDriver path, sets `webdriver.chrome.driver`, and creates `ChromeDriver`.
- On Windows the expected binary name is `lib/chromedriver.exe`; on UNIX-like systems it is `lib/chromedriver`.
- On UNIX-like systems the driver file must also be executable.

## Existing coding patterns
- JUnit tasks own their own `WebDriver` field and open the target page in `@BeforeEach`.
- Cucumber step definitions share the static `Hooks.driver` instance created in `@Before`.
- External pages under test are hosted at `https://janisdzalbe.github.io/example-site/tasks/...`; this repo automates against those pages rather than serving a local app.
- `Task1` targets the `provide_feedback` page and `Task2` targets the `fitness_challenge` page.

## When extending the project
- Reuse `ChromeDriverFactory.createDriver()` instead of duplicating OS detection or `System.setProperty(...)` logic.
- Put new reusable Selenium helpers under `src/test/java/selenium/utility/`.
- Keep page-specific assertions close to the task or step-definition code unless you introduce a dedicated `pages/` package with real Page Objects.
- Treat most of the current tests and step definitions as scaffolding: many methods are still TODO-based and should be completed rather than rewritten into a different structure.
