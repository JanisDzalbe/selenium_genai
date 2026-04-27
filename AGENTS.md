# AGENTS.md - Selenium GenAI Project Guide

## Architecture Overview
This is a Selenium WebDriver automation project using Java 17+, focusing on testing web applications via JUnit and Cucumber BDD frameworks. Tests target example sites like feedback forms and fitness challenges.

- **Dual Test Frameworks**: JUnit tests in `src/test/java/selenium/tasks/` for unit-style automation; Cucumber features in `src/test/resources/features/` with step definitions in `selenium.stepDefinitions/`.
- **Driver Management**: Place WebDriver executables (chromedriver.exe, geckodriver.exe) in `lib/` directory. Initialize drivers cross-platform using OS detection for paths (e.g., `lib/chromedriver.exe` on Windows).
- **Shared State**: Cucumber steps access a static `WebDriver driver` from `Hooks.java` for browser session sharing across scenarios.

## Key Patterns
- **Driver Initialization**: In `Hooks.java`, implement `@Before` to set `System.setProperty("webdriver.chrome.driver", path)` based on OS, then `driver = new ChromeDriver()`. Example: Use `System.getProperty("os.name")` to append `.exe` on Windows.
- **Step Definitions**: Steps in `SampleSteps.java` directly use `Hooks.driver` without passing instances. Avoid creating new drivers in steps.
- **Test Structure**: JUnit tests like `Task1.java` have per-test driver lifecycle (`@BeforeEach` init, `@AfterEach` quit). Cucumber uses global hooks.
- **BDD Scenarios**: Features like `FitnessChallenge1.feature` define Given/When/Then with data tables for participant lists. Implement steps to parse tables and assert UI elements.
- **Assertions**: Use JUnit assertions (e.g., `assertEquals`, `assertTrue`) for element text, visibility, and counts. For modals/alerts, handle `driver.switchTo().alert()`.

## Workflows
- **Build/Run**: Use Maven (`mvn test`) or IDE JUnit runner. For Cucumber, run `CucumberRunner.java` class. No custom scripts; standard Maven lifecycle.
- **Debugging**: Add `Thread.sleep(5000)` or breakpoints in steps/tests. Inspect elements with `driver.findElement(By.xpath("//button[text()='Add Steps']"))`.
- **Adding Tests**: For new features, create `.feature` files with scenarios, then implement steps in `stepDefinitions/`. Use existing patterns from `FitnessChallenge1.feature` (e.g., data tables for lists).
- **Driver Setup**: Download drivers matching browser versions, place in `lib/`. Update paths in hooks if needed. Supports Chrome, Edge, Firefox as per `Task2.java` cross-browser tests.

## Dependencies & Conventions
- **External**: Selenium WebDriver 4+, JUnit 5, Cucumber Java. No Page Object classes implemented yet (aspirational per README).
- **Naming**: Packages follow `selenium.{runners,stepDefinitions,tasks}`. Feature files named `FitnessChallenge{N}.feature` for sequential tasks.
- **Tags**: Cucumber runner excludes `@bug` tagged scenarios.
- **Data Handling**: For forms, use `sendKeys()` for inputs, `click()` for buttons. Verify with `getText()`, `isDisplayed()`.

Reference: `README.md` for structure, `Hooks.java` for driver setup, `Task2.java` for complex UI interactions.</content>
<parameter name="filePath">C:\Users\seva2\IdeaProjects\selenium_genai\AGENTS.md
