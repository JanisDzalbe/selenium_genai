Feature: CROSS-BROWSER COMPATIBILITY

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario Outline: Verify functionality across different browsers
    Given I open the fitness challenge page in <browser>
    When I add valid steps to a participant
    And I submit without selecting participant
    And I reset via top button
    Then all features work as expected

    Examples:
      | browser |
      | Chrome  |
      | Edge    |
      | Firefox |
