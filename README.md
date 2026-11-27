# Selenium Java Project

## Project Overview
Selenium WebDriver project teaching test automation fundamentals.
Uses **Java 27**, **Selenium 4**, **JUnit 5**, and **Cucumber** with Maven.

## Architecture Patterns

### Driver Initialization (Cross-Platform)

**Driver location**: Place WebDriver executables in `lib/` directory (`chromedriver.exe` on Windows; `chromedriver` on Linux/macOS).

Use `selenium.utility.WebDriverUtility.createChromeDriver()` to set `webdriver.chrome.driver` and create a ChromeDriver. On Unix-like systems, ensure the driver file has execute permission.


### Test Structure Convention:
- Tests are in `src/test/java/selenium/tasks` package
- Utility classes are in `src/test/java/selenium/utility/` package
- Page Object classes are in `src/test/java/selenium/pages/` package
- Feature files for Cucumber are in `src/test/resources/features/` directory
- Step definitions for Cucumber are in `src/test/java/selenium/stepDefinitions` package
- Cucumber runner classes are in `src/test/java/selenium/runners` package

## Build & Run
- **Maven project**: Run via IDE's JUnit integration or `mvn test`
- **Java version**: Requires a Java 27 JDK (configured in `pom.xml`; Maven uses `JAVA_HOME`)
- No custom build tasks configured - standard Maven lifecycle applies
- The browser setup and exercise implementations are still TODOs, so the current test sources are not yet a complete end-to-end test suite.

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
