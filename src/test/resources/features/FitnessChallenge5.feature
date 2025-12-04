Feature: FORM VALIDATION

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: Submit without selecting participant
    When I open the "Add Steps" modal
    And I leave the participant dropdown at "Choose participant"
    And I enter "1000" in the steps input
    And I click "Add Steps" button
    Then an alert appears: "Please select a participant"
    When I accept the alert
    Then the modal remains open

  Scenario: Submit without entering steps
    When I open the "Add Steps" modal
    And I select any participant
    And I enter "0" in the steps input field
    And I click "Add Steps" button
    Then an alert appears: "Please enter a valid number of steps"
    When I accept the alert
    Then the modal remains open

  Scenario: Submit with negative steps
    When I open the "Add Steps" modal
    And I select a participant
    And I enter "-500" in the steps input field
    And I attempt to click "Add Steps" button
    Then an alert appears: "Please enter a valid number of steps"
    When I accept the alert
    Then the modal remains open

  Scenario: Submit with non-numeric input
    When I open the "Add Steps" modal
    And I select a participant
    And I attempt to enter "abc" in the steps input field
    Then the input field rejects non-numeric characters
    And no alphabetic characters appear in the input

