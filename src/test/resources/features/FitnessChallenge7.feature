Feature: EDGE CASES AND ERROR HANDLING

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: Handle maximum integer value
    When I open the "Add Steps" modal
    And I select a participant
    And I enter the maximum safe integer "9007199254740991"
    And I submit the form
    Then the system accepts the value
    And the step count updates correctly

  Scenario: Handle decimal step values
    When I open the "Add Steps" modal
    And I select any participant
    And I enter "100.5" in the steps input field
    And I click "Add Steps" button
    Then an alert appears: "Please enter a valid number of steps"
    When I accept the alert
    Then the modal remains open
