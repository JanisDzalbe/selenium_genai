Feature: RESET FUNCTIONALITY

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: Reset via top button
    Given I have added steps to at least 2 participants to modify the default state
    When I click the "Reset List" button at the top of the page
    And I wait for page reload
    Then all participants return to their default step counts
    And the default ranking order is restored
    And trophy icons display for correct default top 3

  Scenario: Reset via bottom button
    Given I have modified participant data
    When I scroll to bottom of page
    And I click the "Reset List" button at the bottom
    And I wait for page reload
    Then all participants return to their default step counts
    And the default ranking order is restored
