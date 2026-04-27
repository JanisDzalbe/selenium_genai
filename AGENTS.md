# AGENTS.md - Selenium GenAI Project Guide

## Architecture Overview
This is a Selenium WebDriver automation project using Java 24, Selenium 4, JUnit 5, and Cucumber for BDD testing. The project follows a hybrid structure with both JUnit unit tests and Cucumber feature-driven tests.

### Key Components
- **JUnit Tests**: Located in `src/test/java/selenium/tasks/` - implement page interactions directly (e.g., `Task1.java` for feedback form testing)
- **Cucumber Features**: BDD scenarios in `src/test/resources/features/` (e.g., `FitnessChallenge1.feature` with Gherkin syntax)
- **Step Definitions**: Glue code in `src/test/java/selenium/stepDefinitions/` (e.g., `SampleSteps.java` uses static driver from `Hooks.java`)
- **Test Runner**: `src/test/java/selenium/runners/CucumberRunner.java` configured with features path and step glue

### Driver Management
- WebDriver executables stored in `lib/` directory
- Cross-platform initialization required (Windows/Unix paths)
- Static `WebDriver driver` in `Hooks.java` shared across step definitions
- `@Before` hook initializes browser, `@After` quits driver

## Critical Workflows
- **Build System**: Maven-based (pom.xml required for dependencies: Selenium, JUnit 5, Cucumber)
- **Test Execution**: 
  - JUnit tests: Run via IDE or `mvn test` after pom setup
  - Cucumber: Execute `CucumberRunner` class directly in IDE
- **Driver Setup**: Implement utility class for OS-specific chromedriver paths (e.g., `lib/chromedriver.exe` on Windows)

## Project Conventions
- **Driver Access**: Always use `Hooks.driver` in step definitions (static reference)
- **Test Structure**: 
  - JUnit: `@BeforeEach` for setup, `@AfterEach` for teardown
  - Cucumber: Background steps in features, hooks in `Hooks.java`
- **Assertions**: Use JUnit 5 assertions (`assertEquals`, `assertTrue`) in both JUnit and Cucumber steps
- **Page Objects**: Planned for `src/test/java/selenium/pages/` package (not yet implemented)
- **Feature Tags**: Cucumber runner excludes `@bug` tagged scenarios

## Integration Points
- **External Sites**: Tests target `https://janisdzalbe.github.io/example-site/` pages (feedback, fitness challenge)
- **Dependencies**: Selenium WebDriver, Cucumber-Java, JUnit-Jupiter (add via Maven once pom.xml exists)
- **Browser Support**: Chrome primary, with Edge/Firefox compatibility tests planned

## Implementation Patterns
- **Modal Interactions**: Use Selenium waits for dynamic elements (e.g., Add Steps modal in fitness tests)
- **Form Validation**: Expect JavaScript alerts for invalid inputs (e.g., "Please enter a valid number of steps")
- **Data Tables**: Cucumber features use table data for participant lists (e.g., default step counts)
- **Ranking Logic**: Client-side sorting by step count descending, with trophy icons for top 3

Reference: `README.md` for setup guides, `prompts.md` for implementation prompts</content>
<parameter name="filePath">C:\Users\meganath\IdeaProjects\selenium_genai\AGENTS.md
