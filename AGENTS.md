# Agent Guidance for Selenium GenAI Project

## Project Overview
This is a **Selenium 4 + Java 24 + Cucumber + JUnit 5** test automation learning project. It uses a hybrid testing approach:
- **Behavior-Driven Tests**: Cucumber `.feature` files with step definitions (GWT format)
- **Unit Tests**: Direct JUnit 5 tests in the `tasks/` package

The project teaches test automation fundamentals through progressive fitness challenge test scenarios.

## Critical Architecture Decisions

### 1. Dual Testing Patterns
- **`src/test/java/selenium/tasks/`**: Direct JUnit 5 tests that manually orchestrate test setup/teardown
  - Each test class has its own `WebDriver` instance initialized in `@BeforeEach`
  - Example: `Task1.java`, `Task2.java` - follow this pattern for new task tests
- **`src/test/java/selenium/stepDefinitions/`**: Cucumber BDD tests with shared driver via Hooks
  - `Hooks.java` provides **static `WebDriver driver`** shared across all step definitions
  - Step definitions use Cucumber annotations: `@Given`, `@When`, `@Then`
  - Example: `SampleSteps.java` demonstrates accessing static driver from Hooks

**Key insight**: The static driver in Hooks is intentionally simple for teaching - it's not a production pattern.

### 2. Cross-Platform Driver Management
- WebDriver executables must be placed in `lib/` directory (not committed to git - see `.gitignore`)
- Drivers should work on Windows and Unix-like systems (paths use OS detection)
- TODO comments in `Hooks.java` indicate driver initialization needs implementation based on OS

### 3. Test Targeting & Filtering
- Cucumber uses `@tag` annotations in `.feature` files to skip tests: `tags = "not @bug"` in `CucumberRunner.java`
- Feature files (`src/test/resources/features/FitnessChallenge*.feature`) pair with task tests for validation

## Development Workflows

### Running Tests
```bash
# Run via Maven
mvn test

# Run via IDE JUnit integration (IntelliJ IDEA recommended)
# Right-click test class/method → Run
```

### Adding New Tests
1. **For direct JUnit tests**: Add to `src/test/java/selenium/tasks/Task{N}.java` following the pattern:
   ```java
   @BeforeEach - initialize driver
   @AfterEach - quit driver
   @Test - assertion-based verification
   ```
2. **For Cucumber BDD**: Create `.feature` file in `src/test/resources/features/`, add step defs in `selenium.stepDefinitions` package
3. Both patterns verify the same test targets (external fitness challenge site)

## Project-Specific Conventions

### Package Organization
- **`selenium.runners`**: Cucumber test runner (CucumberRunner.java only)
- **`selenium.stepDefinitions`**: BDD step implementations + Hooks for driver lifecycle
- **`selenium.tasks`**: JUnit 5 direct test scenarios
- **`selenium.utility`**: (Not yet created) Utility/helper classes go here (per README architecture)
- **`selenium.pages`**: (Not yet created) Page Object Model classes go here

### Feature File Structure
- Background: Common setup (e.g., navigate to base URL)
- Scenario: Individual test case with Given/When/Then steps
- Tables: Data tables for parameterized test data
- Example: `FitnessChallenge1.feature` - INITIAL PAGE LOAD feature with 10 default participants

### Assertion Patterns
- JUnit 5: Use `org.junit.jupiter.api.Assertions` (e.g., `assertEquals()`, `assertTrue()`)
- DO NOT use JUnit 4 `org.junit.Assert` in new code
- Verify: page elements, text content, UI state changes, ordering

## Integration Points

### External Dependencies
- **Selenium WebDriver 4**: Browser automation
- **Cucumber**: BDD test framework with JUnit 5 integration
- **JUnit 5 (Jupiter)**: Test execution engine
- **Maven**: Build & dependency management (pom.xml required)

### Test Target Sites
- Primary: `https://janisdzalbe.github.io/example-site/tasks/{task_name}`
- Examples: `/fitness_challenge`, `/provide_feedback`

### Driver Lifecycle
- **Hooks.openBrowser()**: Triggered before each Cucumber scenario (@Before)
- **Hooks.closeBrowser()**: Triggered after scenario (@After), calls `driver.quit()`
- **Task tests**: Manually control driver in @BeforeEach/@AfterEach

## AI Agent Quick Start

1. **Understand test requirements**: Read corresponding `.feature` file (BDD) or task comments (JUnit)
2. **Implement step defs**: Add to existing step definition class OR create new in `selenium.stepDefinitions` package
3. **Use Hooks driver**: Access static driver via `Hooks.driver` in step definitions; DO NOT create new drivers
4. **Element location**: Use `By.*` selectors (CSS, XPath, ID); verify with browser DevTools
5. **Error handling**: Catch Selenium exceptions, provide meaningful assertion messages for failures

## Known Gaps (TODO items in codebase)
- `Hooks.openBrowser()`: Driver initialization based on OS
- `SampleSteps`: Example step definitions to be replaced with real scenarios
- `Task1`, `Task2`: Test implementations (currently have TODO comments)
- Utility classes: Not yet created (plan to add for cross-platform driver setup, page objects)


