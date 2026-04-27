# AGENTS.md - AI Agent Guidelines for Selenium GenAI Project

## Project Overview
This is a Selenium WebDriver test automation project using Java 24, Selenium 4, JUnit 5, and Cucumber BDD. It tests a fitness challenge web application at https://janisdzalbe.github.io/example-site/tasks/fitness_challenge and feedback forms.

## Architecture
- **Test Types**: JUnit unit tests in `src/test/java/selenium/tasks/` (e.g., `Task1.java` for feedback page tests) and Cucumber BDD in `src/test/resources/features/` (e.g., `FitnessChallenge1.feature` for page load scenarios).
- **Page Object Model**: Implement page classes in `src/test/java/selenium/pages/` for reusable element locators and actions.
- **Driver Management**: Static WebDriver in `Hooks.java` for Cucumber tests; initialize per test in JUnit tasks. Drivers stored in `lib/` directory.
- **Cross-Platform**: Use OS detection in utility classes to set correct driver paths (e.g., `lib/chromedriver` on macOS, `lib/chromedriver.exe` on Windows).

## Key Workflows
- **Build & Test**: Use `mvn test` to run all tests. JUnit tests execute via Maven Surefire; Cucumber via `CucumberRunner.java` with JUnit integration.
- **Driver Setup**: Download ChromeDriver to `lib/` matching Selenium version. Set system property `webdriver.chrome.driver` to absolute path in `lib/`.
- **Debugging**: Run individual JUnit tests in IDE; for Cucumber, use tags like `@bug` to exclude failing scenarios in `CucumberRunner.java`.

## Conventions & Patterns
- **Package Structure**: Follow README.md layout - tasks for JUnit, stepDefinitions for Cucumber steps, runners for Cucumber execution.
- **Hooks Usage**: `Hooks.java` manages browser lifecycle with `@Before`/`@After`. Access static `driver` in step definitions (e.g., `SampleSteps.java`).
- **Step Definitions**: Use regex in annotations (e.g., `^Some Example$`). Import static assertions from JUnit.
- **Feature Files**: Scenarios in `src/test/resources/features/` with Background for common setup (e.g., navigating to fitness page).
- **Test Data**: Inline tables in features for expected participant lists (e.g., Name/Steps pairs in `FitnessChallenge1.feature`).

## Dependencies & Integration
- **Maven**: pom.xml must include Selenium 4, JUnit 5, Cucumber-Java/JUnit. Java 24 compatibility required.
- **External Sites**: Tests hit live demo sites (janisdzalbe.github.io); no local servers.
- **No Custom Frameworks**: Standard Selenium WebDriver API; avoid over-engineering with custom wrappers unless specified.

## Examples
- Initialize driver: `System.setProperty("webdriver.chrome.driver", Paths.get("lib", "chromedriver").toAbsolutePath().toString()); driver = new ChromeDriver();`
- Page assertion: `assertEquals("Fitness Challenge", driver.getTitle());`
- Step implementation: `@Given("I am on the fitness challenge page {string}") public void navigateToPage(String url) { driver.get(url); }`</content>
<parameter name="filePath">/Users/shila/Documents/Development/Java/selenium_genai/AGENTS.md
