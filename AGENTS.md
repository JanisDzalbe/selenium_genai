# AGENTS.md - Selenium GenAI Project

## Quick Start for AI Agents

**Tech Stack**: Java 24 + Selenium 4 + JUnit 5 + Cucumber BDD + Maven

## Critical Workflow
1. **Test Execution**: `mvn test` runs both JUnit and Cucumber tests
2. **Missing pom.xml**: Project needs Maven build config with Selenium 4, Cucumber, JUnit 5 dependencies
3. **Missing Drivers**: Place ChromeDriver, EdgeDriver, FirefoxDriver in `lib/` directory
4. **Missing Utility**: Create `src/test/java/selenium/utility/DriverManager.java` for cross-platform driver init

## Architecture Essentials

**Two test frameworks live together**:
- **JUnit Tasks** (`src/test/java/selenium/tasks/`): Direct WebDriver tests. Task1 = feedback form tests, Task2 = fitness challenge tests
- **Cucumber BDD** (`src/test/resources/features/`): 8 FitnessChallenge features, uses static `WebDriver` from `Hooks.java`

**Driver Management** (critical connection point):
- `Hooks.java`: Has `@Before` (initializes driver) and `@After` (quits driver) - step definitions access via `Hooks.driver`
- For JUnit: Tasks instantiate their own `WebDriver driver` field, call it in `@BeforeEach`
- OS-detection pattern: `System.getProperty("os.name").toLowerCase().contains("win")` for Win/Unix paths

**Test Data Source**: Gherkin tables in features (e.g., `FitnessChallenge1.feature` has participant names/steps)

## Package Structure

```
selenium/
  ├─ tasks/           # JUnit tests; each Task = one page/feature set
  ├─ stepDefinitions/ # Cucumber steps; use @Given/@When/@Then with Hooks.driver
  ├─ runners/         # CucumberRunner.java: tags="not @bug", glue="selenium.stepDefinitions"
  ├─ pages/           # PAGE OBJECTS (planned): Implement reusable locators
  └─ utility/         # DRIVER MANAGER (planned): Cross-platform driver init
```

## Immediate Tasks for Agents

1. **Create pom.xml**: Java 24, Selenium 4.x, Cucumber 7.x, JUnit 5.x
2. **Create DriverManager**: Static method returning `new ChromeDriver()` after setting `webdriver.chrome.driver` property
3. **Implement Task1 tests**: Fill feedback form test cases targeting https://janisdzalbe.github.io/example-site/tasks/provide_feedback
4. **Implement Task2 tests**: Fill fitness challenge test cases targeting https://janisdzalbe.github.io/example-site/tasks/fitness_challenge
5. **Create Page Objects**: Locators for feedback form, fitness challenge list/modal
6. **Implement Cucumber Steps**: Connect BDD scenarios to page objects

## Integration Notes
- `@bug` tag in features skips scenario in CucumberRunner (line 11)
- Participant data in tests: Mike Kid 8,500 + 9 others with preset step counts
- Cross-browser testing planned (Chrome, Edge, Firefox) but only Chrome driver setup required initially
- Tests poll remote site; no backend dependency
