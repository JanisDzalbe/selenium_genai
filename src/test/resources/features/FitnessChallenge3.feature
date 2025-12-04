Feature: ADD STEPS MODAL

  Background:
    Given I am on the fitness challenge page "https://janisdzalbe.github.io/example-site/tasks/fitness_challenge"

  Scenario: Opening modal via top button
    When I click the "Add Steps" button at the top of the page
    Then a modal window appears with title "Add Steps to Participant"
    And the modal contains a participant dropdown
    And the modal contains a number input field
    And the modal contains "Add Steps" submit button
    And the modal has a close button (×) in the top-right corner
    And the dropdown is prepopulated with all 10 participants
    And the default dropdown text shows "Choose participant"

  Scenario: Opening modal via bottom button
    When I scroll to the bottom of the page
    And I click the "Add Steps" button at the bottom
    Then a modal window appears with title "Add Steps to Participant"
    And all modal elements are present

  Scenario: Closing modal with close button
    Given I have opened the "Add Steps" modal
    When I click the × (close) button in the top-right corner
    Then the modal closes
    And I return to the main page view
    And no data is changed from initial state
