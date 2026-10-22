@post @regression
Feature: POST /pet - Pet Creation Scenarios

  Background:
    Given the pet API endpoint is available

  @smoke @positive
  Scenario: POST-01 Successfully add a new valid pet
    When I create a pet with dynamic ID, name "BuddyPlaywright" and status "available"
    Then the response status code should be 200
    And the response pet name should be "BuddyPlaywright"
    And the response pet status should be "available"
    And the response pet id should match the created dynamic ID

  @positive
  Scenario Outline: POST-02 Add pet with each valid status <status>
    When I create a pet with dynamic ID, name "StatusPet" and status "<status>"
    Then the response status code should be 200
    And the response pet status should be "<status>"
    And the response pet id should match the created dynamic ID

    Examples:
      | status    |
      | available |
      | pending   |
      | sold      |

  @negative @boundary
  Scenario: POST-03 Submit pet creation request with missing name field
    When I submit a pet creation request with missing name field
    Then the response status code should be one of 200, 400, or 405

  @negative @boundary
  Scenario: POST-04 Submit pet creation request with missing photoUrls field
    When I submit a pet creation request with missing photoUrls field
    Then the response status code should be one of 200, 400, or 405

  @negative @boundary
  Scenario: POST-05 Submit pet creation request with unsupported status
    When I create a pet with dynamic ID, name "InvalidStatusPet" and status "unknown_status"
    Then the response status code should be one of 200, 400, or 405
    And the response pet status should be "unknown_status"

  @negative
  Scenario: POST-06 Submit pet creation request with malformed JSON
    When I submit a raw pet creation payload:
      """
      {"name": "Pet", "id":
      """
    Then the response status code should be 400
    And the response error message should contain "bad input"
