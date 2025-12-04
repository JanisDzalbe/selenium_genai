Feature: PARTICIPANT DISPLAY AND RANKING

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: Participants are displayed in ranking order by step count
    When I view the participant list
    Then participants are displayed in descending order by step count
    And each participant's step count is greater than or equal to the participant below them

  Scenario: Top three participants display trophy icons
    When I view the participant list
    Then the 1st place participant displays a gold trophy icon
    And the 2nd place participant displays a silver trophy icon
    And the 3rd place participant displays a bronze trophy icon
    And participants ranked 4th and below have no trophy icons
