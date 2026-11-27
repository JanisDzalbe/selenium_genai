# Selenium Java Project

## Project Overview
Selenium WebDriver project teaching test automation fundamentals.
Uses **Java 27**, **Selenium 4**, and **JUnit 5** with Maven build system.

## Architecture Patterns

### Driver Initialization (Cross-Platform)

**Driver location**: Place WebDriver executables in `lib/` directory

Use `selenium.utility.WebDriverUtils.createChromeDriver()` to initialize Chrome.
It selects `lib/chromedriver.exe` on Windows and `lib/chromedriver` on UNIX-like
systems. On UNIX-like systems, make sure the driver has executable permissions.


### Test Structure Convention:
- Tests are in `src/test/java/selenium/tasks` package
- Utility classes are in `src/test/java/selenium/utility/` package
- Page Object classes are in `src/test/java/selenium/pages/` package
- Feature files for Cucumber are in `src/test/resources/features/` directory
- Step definitions for Cucumber are in `src/test/java/selenium/stepDefinitions` package
- Cucumber runner classes are in `src/test/java/selenium/runners` package

## Build & Run
- **Maven project**: Run all JUnit and Cucumber tests with `mvn test`
- **Java version**: Requires Java 27, configured in `pom.xml`
- The Maven test configuration includes the `Task*.java` classes and Cucumber's `CucumberRunner`.

## Project tasks:
- [x] Set up Java Selenium project with Maven (pom.xml)
- [x] Implement cross-platform WebDriver initialization in utility class
- [ ] Download and manage WebDriver executables in `lib/` directory
- [ ] Implement sample test cases in `src/test/java/selenium/tasks` package using JUnit 5
- [ ] Implement features in `src/test/resources/features/` directory using Cucumber
- [ ] Update tests to use Page Object Model pattern in `src/test/java/selenium/pages/` package

## Copilot
[Quickstart for GitHub Copilot](https://docs.github.com/en/copilot/get-started/quickstart)

## General Guides
From Selenium project:
- [Setting up project using IntelliJ IDEA](https://github.com/JanisDzalbe/selenium_java/wiki/Project-Setup)
- [WebDriver Setup](https://github.com/JanisDzalbe/selenium_java/wiki/WebDriver-Setup)
- [Create new branch in IntelliJ IDEA](https://github.com/JanisDzalbe/selenium_java/wiki/Create-new-branch)
- [Committing code and pushing to remote with IntelliJ IDEA](https://github.com/JanisDzalbe/selenium_java/wiki/Committing-code-and-pushing-to-remote)
