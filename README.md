# Selenium Java Project

## Project Overview
Selenium WebDriver project teaching test automation fundamentals.
Uses **Java 27**, **Selenium 4.54.0**, **JUnit 5**, and **Cucumber** with Maven.

## Architecture Patterns

### Driver Initialization (Cross-Platform)

**Driver location**: Place WebDriver executables in `lib/` directory. The Chrome utility expects `chromedriver.exe` on Windows and `chromedriver` on Unix-like systems.

`selenium.utility.WebDriverUtility.createChromeDriver()` selects the platform-specific executable, sets `webdriver.chrome.driver`, and creates a `ChromeDriver`. Cucumber's `Hooks` uses this utility to create and quit the browser for each scenario.


### Test Structure Convention:
- Tests are in `src/test/java/selenium/tasks` package
- Utility classes are in `src/test/java/selenium/utility/` package
- Page Object classes are in `src/test/java/selenium/pages/` package
- Feature files for Cucumber are in `src/test/resources/features/` directory
- Step definitions for Cucumber are in `src/test/java/selenium/stepDefinitions` package
- Cucumber runner classes are in `src/test/java/selenium/runners` package

## Build & Run
- **Maven project**: Run `mvn test` from the project root. Current task/runner class names do not match Maven Surefire's default discovery patterns, so this currently compiles tests without executing them; the Cucumber step definitions are also still TODOs.
- **Java version**: Requires Java 27 (configured in `pom.xml`)
- No custom build tasks configured - standard Maven lifecycle applies

## Project tasks:
- [x] Set up Java Selenium project with Maven (pom.xml)
- [x] Implement cross-platform ChromeDriver initialization in utility class
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
