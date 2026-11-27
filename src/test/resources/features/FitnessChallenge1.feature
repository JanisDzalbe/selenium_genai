Feature: INITIAL PAGE LOAD

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: First time page load displays default participants and data
    When the page loads for the first time
    Then the page displays title "Fitness Challenge"
    And 10 participants are displayed in the list
    And the default participants are displayed:
      | Name              | Steps   |
      | John Smith        | 15,000  |
      | David Brown       | 13,500  |
      | Jill Watson       | 12,000  |
      | Carlos Garcia     | 11,200  |
      | Maria Rodriguez   | 10,500  |
      | Sarah Johnson     | 9,800   |
      | Alex Taylor       | 8,900   |
      | Mike Kid          | 8,500   |
      | Emily Chen        | 7,300   |
      | Jane Doe          | 6,500   |
    And "Add Steps" buttons are visible at top and bottom
    And "Reset List" buttons are visible at top and bottom
