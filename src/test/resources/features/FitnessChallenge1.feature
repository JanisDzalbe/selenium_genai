Feature: INITIAL PAGE LOAD

  Background:
    Given I am on the fitness challenge page

  Scenario: First time page load displays default participants and data
    When the page loads for the first time
    Then the page displays title "Fitness Challenge"
    And 10 participants are displayed in the list
    And the default participants are displayed:
      | Name              | Steps   |
      | Mike Kid          | 8,500   |
      | Jill Watson       | 12,000  |
      | Jane Doe          | 6,500   |
      | John Smith        | 15,000  |
      | Sarah Johnson     | 9,800   |
      | Carlos Garcia     | 11,200  |
      | Emily Chen        | 7,300   |
      | David Brown       | 13,500  |
      | Maria Rodriguez   | 10,500  |
      | Alex Taylor       | 8,900   |
    And "Add Steps" buttons are visible at top and bottom
    And "Reset List" buttons are visible at top and bottom
