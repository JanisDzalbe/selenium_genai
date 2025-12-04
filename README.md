# Selenium Java Project

## Project Overview
Selenium WebDriver project teaching test automation fundamentals.
Uses **Java 24**, **Selenium 4**, and **JUnit 5** with Maven build system.

## Architecture Patterns

### Driver Initialization (Cross-Platform)

**Driver location**: Place WebDriver executables in `lib/` directory

Use custom utility function to initialize WebDriver instances based on OS.


### Test Structure Convention:
- Tests are in `src/test/java/selenium/tasks` package
- Utility classes are in `src/test/java/selenium/utility/` package
- Page Object classes are in `src/test/java/selenium/pages/` package
- Feature files for Cucumber are in `src/test/resources/features/` directory
- Step definitions for Cucumber are in `src/test/java/selenium/stepDefinitions` package
- Cucumber runner classes are in `src/test/java/selenium/runners` package

## Build & Run
- **Maven project**: Run via IDE's JUnit integration or `mvn test`
- **Java version**: Requires Java 24 (configured in pom.xml)
- No custom build tasks configured - standard Maven lifecycle applies

## Project tasks:
- [ ] Set up Java Selenium project with Maven (pom.xml)
- [ ] Implement cross-platform WebDriver initialization in utility class
- [ ] Download and manage WebDriver executables in `lib/` directory
- [ ] Implement sample test cases in `src/test/java/selenium/tasks` package using JUnit 5
- [ ] Implement features in `src/test/resources/features/` directory using Cucumber
- [ ] Update tests to use Page Object Model pattern in `src/test/java/selenium/pages/` package

## Copilot
[Quickstart for GitHub Copilot](https://docs.github.com/en/copilot/get-started/quickstart)