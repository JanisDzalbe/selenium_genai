Feature: ADDING STEPS TO PARTICIPANTS

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: Add valid steps to a participant
    Given I note the current step count for "Mike Kid"
    When I open the "Add Steps" modal
    And I select "Mike Kid" from the dropdown
    And I enter "1000" in the steps input field
    And I click "Add Steps" button
    Then the modal closes automatically
    And the participant list refreshes
    And Mike Kid's step count increases by 1,000
    And the list re-sorts if Mike Kid's new total changes his ranking

  Scenario: Add zero steps to a participant
    When I open the "Add Steps" modal
    And I select any participant
    And I enter "0" in the steps input field
    And I click "Add Steps" button
    Then an alert appears: "Please enter a valid number of steps"
    When I accept the alert
    Then the modal remains open

  Scenario: Add large number of steps
    When I open the "Add Steps" modal
    And I select any participant
    And I enter "999999" in the steps input field
    And I click "Add Steps" button
    Then the modal closes
    And the step count updates correctly with the large number
    And the participant moves to first place
