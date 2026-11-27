# Selenium Java Project

## Project Overview
Selenium WebDriver project teaching test automation fundamentals.
Uses **Java 21**, **Selenium 4**, and **JUnit 5** with Maven build system.

## Architecture Patterns

### Driver Initialization (Cross-Platform)

**Driver location**: Place the ChromeDriver executable in `lib/` as `chromedriver.exe` on Windows or `chromedriver` on UNIX-like operating systems.

`selenium.utility.WebDriverFactory` selects local driver executables from `lib/` for Chrome, Edge, and Firefox. Name them `chromedriver`, `msedgedriver`, or `geckodriver`, adding `.exe` on Windows. Edge and Firefox can also use Selenium Manager to resolve their drivers when local executables are absent.


### Test Structure Convention:
- Tests are in `src/test/java/selenium/tasks` package
- Utility classes are in `src/test/java/selenium/utility/` package
- Page Object classes are in `src/test/java/selenium/pages/` package
- Feature files for Cucumber are in `src/test/resources/features/` directory
- Step definitions for Cucumber are in `src/test/java/selenium/stepDefinitions` package
- Cucumber runner classes are in `src/test/java/selenium/runners` package

## Build & Run
- **Maven project**: Run via IDE's JUnit integration or `mvn test`
- **Run Fitness Challenge features 1-4**: `mvn -Dtest=CucumberRunner test`
- **Run one task class**: `mvn -Dtest=Task1 test` or `mvn -Dtest=Task2 test`
- **Java version**: Requires Java 21 (configured in pom.xml)
- Standard Maven lifecycle applies

## Project tasks:
- [x] Set up Java Selenium project with Maven (pom.xml)
- [x] Implement cross-platform WebDriver initialization in utility class
- [x] Download and manage WebDriver executables in `lib/` directory
- [x] Implement sample test cases in `src/test/java/selenium/tasks` package using JUnit 5
- [x] Implement features in `src/test/resources/features/` directory using Cucumber
- [x] Update tests to use Page Object Model pattern in `src/test/java/selenium/pages/` package

## Copilot
[Quickstart for GitHub Copilot](https://docs.github.com/en/copilot/get-started/quickstart)

## General Guides
From Selenium project:
- [Setting up project using IntelliJ IDEA](https://github.com/JanisDzalbe/selenium_java/wiki/Project-Setup)
- [WebDriver Setup](https://github.com/JanisDzalbe/selenium_java/wiki/WebDriver-Setup)
- [Create new branch in IntelliJ IDEA](https://github.com/JanisDzalbe/selenium_java/wiki/Create-new-branch)
- [Committing code and pushing to remote with IntelliJ IDEA](https://github.com/JanisDzalbe/selenium_java/wiki/Committing-code-and-pushing-to-remote)
