# AGENTS.md - Selenium Java Test Automation Project

## Project Overview
Educational Selenium WebDriver project for test automation. Combines **JUnit 5** for unit-style tests and **Cucumber** for BDD-style feature tests. Runs against external test site: `https://janisdzalbe.github.io/example-site/`

**Stack**: Java 24, Selenium 4, Cucumber JVM, Maven, JUnit 5

## Architecture & Patterns

### Test Structure Convention
- **JUnit Tasks** (direct Selenium): `src/test/java/selenium/tasks/` (e.g., `Task1.java`)
  - Individual test files using `@BeforeEach` / `@AfterEach` for driver lifecycle
  - Each task initializes its own `WebDriver` instance
- **Cucumber BDD Tests**: `src/test/resources/features/` (e.g., `FitnessChallenge1.feature`)
  - Step definitions in: `src/test/java/selenium/stepDefinitions/`
  - Cucumber runner: `src/test/java/selenium/runners/CucumberRunner.java`
  - **Shared driver via Hooks**: `Hooks.java` has static `WebDriver driver` initialized in `@Before`, cleaned in `@After`
  - Tags: `@bug` scenarios excluded from default runs

### Shared WebDriver Pattern
- **Hooks class** (`stepDefinitions/Hooks.java`) provides static `WebDriver` to all step definitions
- Access in steps: `Hooks.driver` (see `SampleSteps.java` for example)
- Driver initialization location: `@Before` method in Hooks (currently TODO - needs cross-platform WebDriver setup)

### Cross-Platform WebDriver  
- WebDriver executables placed in `lib/` directory
- Utility class needed to detect OS and set appropriate system properties
- Must handle both Windows (`.exe`) and Unix-like systems

## Development Commands

### Build & Test
```bash
mvn test                    # Run all tests
mvn test -Dtest=Task1       # Run specific JUnit task
mvn test -Dtest=CucumberRunner  # Run Cucumber features
mvn clean compile           # Build without tests
```

### Key Configurations
- **pom.xml**: Maven configuration (Java 24, dependencies not yet configured)
- **CucumberOptions**: 
  - Features path: `src/test/resources/features/`
  - Glue (step definitions): `selenium.stepDefinitions`
  - Tag filter: `not @bug` (skips @bug-tagged scenarios)

## Critical Implementation Patterns

### WebDriver Initialization (TODO - High Priority)
1. **Location**: `Hooks.java` in `@Before` method
2. **Requirements**:
   - Detect OS (Windows vs Unix)
   - Set `webdriver.chrome.driver` system property to correct lib path
   - Create new ChromeDriver instance
   - Handle FileNotFoundException if driver missing in lib/
3. **Example Usage** (from Task1.java structure):
   ```java
   // Should initialize driver and navigate to page
   WebDriver driver = initializeChromeDriver();
   driver.navigate().to("https://janisdzalbe.github.io/example-site/...");
   ```

### Test Page Selection
- **Task tests** (JUnit): Use specific URLs per test (e.g., `/tasks/provide_feedback` for Task1)
- **Cucumber Scenarios**: Dynamic URL from feature file Background steps

### Assertion Patterns
- **JUnit**: Use `org.junit.jupiter.api.Assertions` static imports (e.g., `assertEquals()`, `assertTrue()`)
- **Cucumber**: Import assertions similarly in step classes
- See Task1.java comments for test expectations (field states, button colors, form validation)

## Key Files & Their Roles

| File | Purpose |
|------|---------|
| `src/test/java/selenium/tasks/*.java` | JUnit 5 tests - direct Selenium use, each file is independent |
| `src/test/resources/features/*.feature` | Cucumber BDD scenarios - readable test cases |
| `src/test/java/selenium/stepDefinitions/Hooks.java` | Setup/teardown for Cucumber; static driver |
| `src/test/java/selenium/stepDefinitions/SampleSteps.java` | Example step patterns |
| `src/test/java/selenium/runners/CucumberRunner.java` | Cucumber execution config |
| `lib/` | Store WebDriver executables here (e.g., `chromedriver.exe`) |

## Integration Points & External Dependencies

### External Test Site
- Base: `https://janisdzalbe.github.io/example-site/`
- Endpoints (from feature files):
  - `/tasks/provide_feedback` - Form feedback page (Task1/FitnessChallenge features)
  - `/tasks/fitness_challenge` - Participant list with add steps (FitnessChallenge scenarios)

### Dependencies to Configure (pom.xml)
- Selenium WebDriver 4.x
- Cucumber JVM (cucumber-java, cucumber-junit)
- JUnit 5 (junit-jupiter-api, junit-jupiter-engine)
- Logging (SLF4J recommended)

### WebDriver Management
- No WebDriver Manager library currently - drivers sourced manually from `lib/`
- ChromeDriver version must match installed Chrome browser

## Development Workflows

### Adding New Test
1. **JUnit**: Create `TaskN.java` in `src/test/java/selenium/tasks/`
   - Copy structure from Task1 (BeforeEach, AfterEach, Test methods)
   - Initialize driver in BeforeEach, close in AfterEach
2. **Cucumber**: Add `.feature` file in `src/test/resources/features/`
   - Write Gherkin syntax (Given, When, Then)
   - Implement matching step definitions in new/existing step class
   - Access shared WebDriver via `Hooks.driver`

### Debugging
- Cucumber: Check `pretty` plugin output in console
- JUnit: Run directly from IDE test runner
- WebDriver fails: Verify `lib/chromedriver[.exe]` exists and is executable
- Missing drivers in lib/: Check README for WebDriver setup links

## Project Status & TODOs
- ✅ Structure & conventions set up
- ❌ WebDriver initialization utility incomplete (Hooks.java)
- ❌ pom.xml dependencies not configured
- ❌ Most test implementations are TODO (see comments in task/step files)
- 🔄 Features created but steps not fully implemented

## Important Notes for Agents
- **Static driver pattern**: All step definitions access `Hooks.driver` - changes to Hooks affect all tests
- **Page loading expectations**: Tests check specific UI states (element visibility, text, colors)
- **No Page Object Model yet**: Direct By selectors in tests; future tasks may require refactoring
- **Feature file structure**: Background sections run before each scenario (see FitnessChallenge*.feature)
- **Cross-platform focus**: Several implementation opportunities to ensure Windows + Unix compatibility

