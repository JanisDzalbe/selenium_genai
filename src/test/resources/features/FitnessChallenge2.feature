Feature: PARTICIPANT DISPLAY AND RANKING

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: Participants are displayed in ranking order by step count
    When I view the participant list
    Then participants are displayed in descending order by step count
    And each participant's step count is greater than or equal to the participant below them

  Scenario: Top three participants display trophy icons
    When I view the participant list
    Then the following participants display trophy icons:
      | place | color  |
      | 1     | gold   |
      | 2     | silver |
      | 3     | bronze |
    And participants ranked 4th and below have no trophy icons
