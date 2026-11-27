# Copilot Instructions for Selenium Java Project

## Overview
This project is a Selenium WebDriver-based test automation framework using Java 24, Selenium 4, JUnit 5, and Maven. It is designed to teach test automation fundamentals and follows a structured architecture for scalability and maintainability.

## Key Architecture and Patterns

### Driver Initialization
- WebDriver executables should be placed in the `lib/` directory.
- Use a custom utility class to initialize WebDriver instances based on the operating system (Windows or UNIX-like).
- Ensure cross-platform compatibility by setting the system property for the ChromeDriver location dynamically.

### Test Structure
- **Test Classes**: Located in `src/test/java/selenium/tasks`.
- **Utility Classes**: Located in `src/test/java/selenium/utility/`.
- **Page Object Classes**: Located in `src/test/java/selenium/pages/`.
- **Cucumber Feature Files**: Located in `src/test/resources/features/`.
- **Step Definitions**: Located in `src/test/java/selenium/stepDefinitions`.
- **Cucumber Runner Classes**: Located in `src/test/java/selenium/runners`.

### Page Object Model (POM)
- Follow the Page Object Model pattern for test design.
- Encapsulate web elements and interactions within dedicated classes in the `pages` package.

## Developer Workflows

### Build and Run
- Use Maven to manage dependencies and build the project.
- Run tests via your IDE's JUnit integration or using the command: `mvn test`.
- Ensure Java 24 is installed and configured.

### Adding New Tests
1. Create feature files in the `features` directory for Cucumber scenarios.
2. Implement step definitions in the `stepDefinitions` package.
3. Add corresponding test logic in the `tasks` package.
4. Use the Page Object Model for reusable components.

### Managing WebDriver Executables
- Place WebDriver executables (e.g., `chromedriver.exe`) in the `lib/` directory.
- Use the utility class to manage WebDriver initialization.

## Integration Points
- **JUnit 5**: Used for test execution.
- **Cucumber**: Used for behavior-driven development (BDD) with feature files.
- **Selenium WebDriver**: Used for browser automation.

## Examples

### Example: Cross-Platform WebDriver Initialization
```java
public class WebDriverUtil {
    public static WebDriver getDriver() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "lib/chromedriver.exe");
        } else {
            System.setProperty("webdriver.chrome.driver", "lib/chromedriver");
        }
        return new ChromeDriver();
    }
}
```

### Example: Cucumber Feature File
```gherkin
Feature: Fitness Challenge
  Scenario: User logs in successfully
    Given the user is on the login page
    When the user enters valid credentials
    Then the user is redirected to the dashboard
```

## Notes
- Follow the directory structure strictly to maintain consistency.
- Use Maven for dependency management and build tasks.
- Ensure WebDriver executables are up-to-date and compatible with the browser version.

For further details, refer to the `README.md` file.
