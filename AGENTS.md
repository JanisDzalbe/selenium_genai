# AGENTS.md - Selenium GenAI Test Automation Project

## Architecture Overview
This is an educational Selenium WebDriver project for learning test automation fundamentals. It combines JUnit 5 unit tests with Cucumber BDD scenarios to test web applications.

**Key Components:**
- `src/test/java/selenium/tasks/` - JUnit 5 test classes (e.g., `Task1.java` for feedback form tests)
- `src/test/resources/features/` - Cucumber feature files (e.g., `FitnessChallenge1.feature` for page load scenarios)
- `src/test/java/selenium/stepDefinitions/` - Cucumber step implementations (e.g., `Hooks.java` for browser lifecycle)
- `src/test/java/selenium/runners/CucumberRunner.java` - Test execution entry point

**Data Flow:** Tests initialize WebDriver in `Hooks.java`, navigate to target URLs (e.g., `https://janisdzalbe.github.io/example-site/tasks/fitness_challenge`), perform assertions on DOM elements, and close browser.

## Developer Workflows
- **Run Tests:** Execute via IDE JUnit integration or `mvn test` (requires Maven setup)
- **Driver Management:** Place WebDriver executables in `lib/` directory; implement OS-specific initialization in utility classes
- **Debugging:** Use Selenium WebDriver methods like `findElement(By.id())` for element inspection; check browser console for JavaScript errors

## Project Conventions
- **Driver Initialization:** Use static `WebDriver` in `Hooks.java` for shared browser instance across steps
- **Element Locators:** Prefer `By.id()`, `By.cssSelector()` over `By.xpath()` for reliability (see `Task2.java` examples)
- **Test Data:** Hardcode expected values in tests (e.g., participant names in `FitnessChallenge1.feature`)
- **Assertions:** Use JUnit 5 `assertEquals()`, `assertTrue()` for verifications
- **Cucumber Tags:** Exclude `@bug` tagged scenarios in `CucumberRunner.java`

## Integration Points
- **External Sites:** Tests target `janisdzalbe.github.io` example pages (no local server)
- **Dependencies:** Selenium 4, JUnit 5, Cucumber (add to `pom.xml` when setting up Maven)
- **Cross-Component:** Step definitions access shared driver from `Hooks.java`; features reference specific test scenarios

## Key Patterns
- **Page State Checks:** Verify initial loads with exact text matches (e.g., title "Fitness Challenge" in `FitnessChallenge1.feature`)
- **Modal Interactions:** Test form submissions with validation alerts (e.g., "Please select a participant" in `Task2.java`)
- **Ranking Logic:** Assert descending order by numeric values (e.g., step counts in `FitnessChallenge2.feature`)
- **Reset Functionality:** Test page reloads to default state after modifications</content>
<parameter name="filePath">C:\Users\solov\Documents\selenium_genai\AGENTS.md
