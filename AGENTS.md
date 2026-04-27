# AGENTS.md - Selenium GenAI Testing Framework

## Project Architecture

This is a **Selenium WebDriver testing framework** in Java teaching test automation fundamentals. It uses:
- **Build System**: Maven (requires pom.xml setup)
- **Java Version**: Java 24
- **Test Frameworks**: JUnit 5 + Cucumber v4
- **Primary Dependency**: Selenium 4

### Critical Pattern: Dual Testing Approaches

The project supports **two parallel test execution patterns**:

1. **Task-Based Testing** (`src/test/java/selenium/tasks/`): Direct JUnit 5 tests using `@BeforeEach`, `@AfterEach`, `@Test`. Example: `Task1.java`, `Task2.java`
2. **BDD-Driven Testing** (`src/test/java/selenium/stepDefinitions/`): Cucumber scenarios with Gherkin feature files. Step definitions imported via `glue = {"selenium.stepDefinitions"}` in `CucumberRunner.java`

## Test Execution

- **Run individual Task tests**: Via IDE's JUnit 5 integration (right-click test class)
- **Run Cucumber scenarios**: `CucumberRunner.java` with `@CucumberOptions`
- **Tag filtering**: Cucumber runner excludes `@bug` tagged scenarios (`tags = "not @bug"`)
- **Maven CLI**: `mvn test` when pom.xml is configured

## WebDriver Management

### Cross-Platform Driver Initialization (Critical)

The project **must support Windows and Unix-like systems**. Driver executables go in `lib/` directory. Implementation should:
- Detect OS via `System.getProperty("os.name")` or JUnit platform methods
- Set system property `webdriver.chrome.driver` to path in `lib/` before initialization
- Return `ChromeDriver` instance without hardcoding paths

**Implementation Location**: Utility class (to be created in `src/test/java/selenium/utility/` package, referenced by both Task tests and Hooks.java)

### Cucumber Hooks Pattern

`Hooks.java` provides shared WebDriver setup:
- **Static field**: `public static WebDriver driver;` accessible to all step definitions
- **@Before hook**: Initialize browser (`openBrowser()` method - currently empty TODO)
- **@After hook**: Close browser (`closeBrowser(Scenario scenario)` - calls `driver.quit()`)

Step definitions access driver via `Hooks.driver` (see `SampleSteps.java` pattern).

## Project Structure Convention

```
selenium_genai/
├── src/test/java/selenium/
│   ├── tasks/               # JUnit 5 tests (Task1.java, Task2.java)
│   ├── stepDefinitions/     # Cucumber steps (SampleSteps.java) + Hooks.java
│   ├── runners/             # CucumberRunner.java (no Page Objects yet)
│   └── utility/             # (to be created) WebDriver utility, helpers
├── src/test/resources/features/  # Gherkin feature files (FitnessChallenge*.feature)
└── lib/                     # WebDriver executables (chromedriver, edgedriver, etc.)
```

**Note**: Page Object Model pattern is a planned task (`src/test/java/selenium/pages/`) but not yet implemented.

## Key Files & Patterns

| File | Purpose |
|------|---------|
| `CucumberRunner.java` | Entry point for BDD tests; defines feature path, glue package, plugin config |
| `Hooks.java` | Shared browser setup/teardown; static `driver` field used across steps |
| `SampleSteps.java` | Template for Cucumber step definitions (German/Given/When/Then using @Given, @When, @Then) |
| `Task1.java`, `Task2.java` | Independent JUnit 5 tests; each initializes own `WebDriver driver` field |
| `FitnessChallenge*.feature` | Gherkin scenarios with `Background` sections for common setup (e.g., page URL) |

## Selenium-Specific Patterns Observed

- **Locator Strategy**: Tests use `By.id()`, `By.cssSelector()`, `By.xpath()` (inferred from TODO comments)
- **Element Interactions**: Standard click(), sendKeys(), getText(), getAttribute()
- **Wait Strategy**: Implied but not yet implemented (use Selenium WebDriverWait if needed)
- **Form Testing**: Heavy focus on dropdown selection, text input validation, button states (see Task1.java feedback form, Task2.java fitness app)
- **Alert Handling**: Expected validation alerts (see Task2.java - "Please enter a valid number of steps")

## Developer Workflows

1. **Adding a new Task test**: Create `TaskX.java` in `src/test/java/selenium/tasks/`, implement `@BeforeEach openPage()`, `@AfterEach closeBrowser()`, add `@Test` methods
2. **Adding Cucumber steps**: Implement method in `SampleSteps.java` with `@Given`/`@When`/`@Then` annotations matching feature file regex patterns
3. **New Feature file**: Create `.feature` in `src/test/resources/features/`, CucumberRunner will auto-discover (features path points there)
4. **Cross-browser testing**: Task2.java shows intended testing pattern (chromeBrowser, edgeBrowser, firefoxBrowser test methods indicate multi-browser intent)

## Build Prerequisites

Before running tests, ensure:
- [ ] pom.xml created with Selenium 4, JUnit 5, Cucumber, Java 24
- [ ] WebDriver executables (chromedriver, edgedriver, geckodriver) placed in `lib/`
- [ ] Utility class with OS-aware driver initialization implemented
- [ ] Hooks.java `openBrowser()` method calls utility to init driver

## External Dependencies & Test Sites

- **Test target**: https://janisdzalbe.github.io/example-site/ (feedback forms, fitness challenge app)
- **Referenced in tasks**: `/tasks/provide_feedback` (Task1), `/tasks/fitness_challenge` (Task2)

