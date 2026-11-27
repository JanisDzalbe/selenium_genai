# Copilot instructions for selenium_genai

## Big picture
Training project for UI test automation of the example site `https://janisdzalbe.github.io/example-site/`.
There is no production code: everything lives in `src/test`. Two independent test styles share the same helpers:
- **JUnit 5 tests** in `src/test/java/selenium/tasks` (`Task1` - feedback form `tasks/provide_feedback`, `Task2` - `tasks/fitness_challenge`).
- **Cucumber features** in `src/test/resources/features` (`FitnessChallenge1..8.feature`) with step definitions in
  `src/test/java/selenium/stepDefinitions` and the runner `selenium/runners/CucumberRunner.java`.

## Stack and build
- Java 24, Maven (`pom.xml`), Selenium 4, JUnit 5 (Jupiter), Cucumber 7 with `cucumber-junit`.
- `CucumberRunner` is a **JUnit 4** runner (`@RunWith(Cucumber.class)`), so `mvn test` needs `junit-vintage-engine` to pick it up.
- Run everything: `mvn test`; one class: `mvn test -Dtest=Task1`. In IntelliJ run a class, a test or a `.feature` file directly.
- `CucumberRunner` runs all features with `tags = "not @bug"`: tag scenarios that fail because of a known site bug with `@bug`
  (and add a comment describing the bug) instead of deleting them.

## WebDriver
- Driver executables are **not** committed: they are placed in `lib/` (`chromedriver`, `msedgedriver`, `geckodriver`,
  with `.exe` on Windows) and ignored by `.gitignore`.
- Never call `new ChromeDriver()` directly: use the cross-platform utility class in `src/test/java/selenium/utility/`,
  which sets the `webdriver.*.driver` system property from `lib/` based on the OS and returns the driver.
- Cucumber shares one driver through the static `Hooks.driver` (`@Before` opens the browser, `@After` quits it).
  Step classes read `Hooks.driver`; they do not create drivers themselves.

## Conventions
- Page Objects go to `src/test/java/selenium/pages/` and use `PageFactory` with `@FindBy(how = How.X, using = "...")`.
- Keep the `// TODO` comment texts from the task templates as comments above the code that implements them.
- Assertions use `org.junit.jupiter.api.Assertions` (static import), always `assertEquals(expected, actual)`.
- Prefer explicit waits (`WebDriverWait` + `ExpectedConditions`) over `Thread.sleep`.

## Site specifics that break naive tests
- The top menu (`#header`) is loaded by script after the page and pushes content down: wait for `#header > *` before clicking.
- Pages that change data reload or re-render lists: wait for `stalenessOf` the old element before asserting.
- Numbers are formatted with the browser locale (`8,500` in English): start the browser with an English UI.
- Modals open with a zoom animation (`w3-animate-zoom`): wait until it finishes before clicking or measuring elements.
