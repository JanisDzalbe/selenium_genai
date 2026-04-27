# AGENTS.md — Selenium QA Automation Codebase

## Project Architecture

This is a **Java-based Selenium WebDriver QA automation** project using **Cucumber for BDD** and **JUnit 5** for unit tests. The project teaches test automation fundamentals with a dual-approach testing strategy.

### Two Test Execution Paths

1. **Direct JUnit Tests** (`src/test/java/selenium/tasks/Task*.java`): Individual Java test classes with `@BeforeEach`, `@Test`, `@AfterEach` lifecycle
2. **Cucumber BDD Tests** (`src/test/resources/features/*.feature`): Feature-based test scenarios with step definitions and hooks

Both approaches target the same example website: `janisdzalbe.github.io/example-site` and share the same WebDriver initialization pattern.

### Core Components

- **Hooks** (`selenium.stepDefinitions.Hooks`): Contains static `WebDriver driver` instance initialized in `@Before` and closed in `@After`. This is the shared driver across all step definitions.
- **Step Definitions** (`selenium.stepDefinitions.SampleSteps`): Cucumber steps that access `Hooks.driver` to interact with the page
- **Cucumber Runner** (`selenium.runners.CucumberRunner`): JUnit runner configured with:
  - Features location: `src/test/resources/features/`
  - Glue path: `selenium.stepDefinitions`
  - Tag filter: `not @bug` (skips tests tagged with `@bug`)
  - Plugin: `pretty` formatter for console output

---

## Critical Developer Workflows

### Running Tests

**From IDE (IntelliJ IDEA - Recommended for Learning)**
- Right-click any `Task*.java` class → **Run**
- Right-click `CucumberRunner.java` → **Run** (runs all Cucumber features)
- Right-click individual `*.feature` file → **Run** (runs that feature only)

**From Command Line (Maven)**
```bash
mvn test                              # Run all tests
mvn test -Dtest=Task1                 # Run specific task
mvn test -Dtest=CucumberRunner        # Run Cucumber tests only
```

### Build Requirements

- **Java**: Requires Java 24 (configured in `pom.xml`)
- **Maven**: Standard Maven lifecycle (`clean install`, `test`, etc.)
- **No custom build scripts**: Uses Maven defaults

### Driver Setup (Critical for Cross-Platform Support)

WebDriver executables must be placed in `lib/` directory **before** running tests:
- Windows: `lib/chromedriver.exe`, `lib/msedgedriver.exe`, etc.
- Linux/Mac: `lib/chromedriver`, `lib/msedgedriver`, etc.

The `openBrowser()` method in `Hooks.java` is **TODO** — it must conditionally set the driver path based on OS:
- Detect OS via `System.getProperty("os.name")`
- Load browser executable from `lib/` directory
- Initialize `ChromeDriver` (or other browsers) with appropriate path

---

## Project-Specific Patterns & Conventions

### Test Package Structure
- **Tasks**: `src/test/java/selenium/tasks/` — JUnit 5 tests with individual `@Test` methods
- **Features**: `src/test/resources/features/` — Cucumber scenarios in Gherkin syntax
- **Step Definitions**: `src/test/java/selenium/stepDefinitions/` — Maps Gherkin steps to Java code
- **Page Objects** (planned): `src/test/java/selenium/pages/` — Not yet created; implement Page Object Model here
- **Utilities** (planned): `src/test/java/selenium/utility/` — Not yet created; place WebDriver utilities here

### Cucumber Test Structure

**Feature File Format** (`FitnessChallenge1.feature`):
```gherkin
Feature: INITIAL PAGE LOAD

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: First time page load displays default participants and data
    When the page loads for the first time
    Then the page displays title "Fitness Challenge"
    And 10 participants are displayed in the list
```

**Step Definition Format** (in `SampleSteps.java`):
```java
@Given("^I am on the fitness challenge page \"(.*)\"$")
public void navigateToPage(String url) {
    Hooks.driver.get(url);
}
```

### Assertion & Testing Conventions

- Use **JUnit 5 assertions**: `assertEquals()`, `assertTrue()`, `assertNotNull()` (imported from `org.junit.jupiter.api.Assertions`)
- Use **Selenium WebElement locations**: `By.id()`, `By.xpath()`, `By.cssSelector()`, etc.
- **No custom assertion libraries** — rely on standard JUnit assertions

### Cucumber Tag System

Tags filter test execution:
- `@bug` — Tests tagged with this are **skipped** (see CucumberRunner)
- Custom tags can be added for categorization (e.g., `@smoke`, `@regression`)

---

## Integration Points & Testing Targets

### External Dependencies

- **Selenium 4**: WebDriver API for browser automation
- **Cucumber Framework**: BDD test format and execution
- **JUnit 5**: Direct test execution and assertions
- **Maven**: Build and dependency management
- **Example Website**: `janisdzalbe.github.io/example-site`

### Test Scenario Targets

| Task/Feature | URL | Purpose |
|---|---|---|
| Task1 / N/A | `janisdzalbe.github.io/example-site/tasks/provide_feedback` | Form handling & field validation |
| Task2 / FitnessChallenge1-8 | `janisdzalbe.github.io/example-site/tasks/fitness_challenge` | Modal interactions, list sorting, data manipulation |

### Data Flows

1. User/AI writes Gherkin scenario in `.feature` file
2. Scenario steps map to methods in `SampleSteps.java`
3. Step methods access `Hooks.driver` (static WebDriver instance)
4. WebDriver commands interact with browser
5. `@After` hook closes the browser after each scenario
6. Results displayed via `pretty` plugin (console output)

---

## Key Files to Reference When Making Changes

| File | Purpose |
|---|---|
| `src/test/java/selenium/runners/CucumberRunner.java` | Cucumber test runner configuration; modify `@CucumberOptions` to change glue paths, plugins, or tag filters |
| `src/test/java/selenium/stepDefinitions/Hooks.java` | WebDriver lifecycle; implement `@Before` method to initialize driver from `lib/` |
| `src/test/java/selenium/stepDefinitions/SampleSteps.java` | Template for step definitions; access `Hooks.driver` to write new steps |
| `src/test/java/selenium/tasks/Task1.java` | JUnit test template; implement `@BeforeEach` with driver initialization |
| `src/test/resources/features/*.feature` | Gherkin scenarios; follows BDD format with Background, Scenario, Given/When/Then |
| `lib/` | Directory for WebDriver executables; create OS-specific subdirectories if needed |
| `pom.xml` | Maven configuration; update Java version, add dependencies here |

---

## Common Development Tasks

### Adding a New Cucumber Feature
1. Create `.feature` file in `src/test/resources/features/`
2. Write scenarios in Gherkin (Given/When/Then format)
3. Add corresponding `@Given`, `@When`, `@Then` methods in `SampleSteps.java`
4. Run `CucumberRunner.java` to execute

### Adding a New JUnit Task
1. Create `Task*.java` in `src/test/java/selenium/tasks/`
2. Implement `@BeforeEach` to initialize driver
3. Implement `@Test` methods with assertions
4. Implement `@AfterEach` to close driver
5. Run directly from IDE or via `mvn test -Dtest=Task*`

### Implementing Missing Driver Initialization
The `Hooks.openBrowser()` method must:
1. Check OS using `System.getProperty("os.name")`
2. Build driver path: `./lib/chromedriver[.exe on Windows]`
3. Set system property: `System.setProperty("webdriver.chrome.driver", path)`
4. Initialize: `driver = new ChromeDriver()`

---

## Notes for AI Agents

- **No pom.xml exists yet** — this needs to be created first with Java 24 and Selenium 4 dependencies
- **Hooks.openBrowser() is unimplemented** — critical blocker for running any tests
- **No Page Objects exist** — the architecture *expects* them in `selenium.pages` (mentioned in README)
- **Two parallel test systems**: When adding tests, consider whether to use JUnit (Task) or Cucumber (Feature) approach
- **Static driver access pattern**: All step definitions and tests must access `Hooks.driver` (static instance, not dependency injection)
- **No test data management**: Tests use hardcoded URLs and expected values; no external test data sources

