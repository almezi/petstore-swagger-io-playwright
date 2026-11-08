@delete @integration
Feature: DELETE /pet/{petId} - Pet Deletion Scenarios

  Background:
    Given the pet API endpoint is available

  @sanity @positive
  Scenario: Successfully delete an existing pet
    Given a pet exists with dynamic ID and name "DeleteMePet"
    When I delete the previously created pet
    Then the response status code should be 200

  @positive @regression
  Scenario: Verify the deleted pet is no longer retrievable
    Given a pet exists with dynamic ID and name "VerifyGonePet"
    When I delete the previously created pet
    Then the response status code should be 200
    When I retrieve the previously created pet
    Then the response status code should be 404

  @negative @regression
  Scenario: Delete a pet with non-existent ID
    When I delete a pet with non-existent ID 987654321098
    Then the response status code should be 404

  @negative @boundary @regression
  Scenario: Delete a pet with invalid non-numeric ID
    When I delete a pet with invalid non-numeric ID "invalid_string_id"
    Then the response status code should be one of 400 or 404

  @negative @idempotency @regression
  Scenario: Delete the same pet twice
    Given a pet exists with dynamic ID and name "DoubleDeletePet"
    When I delete the previously created pet
    Then the response status code should be 200
    When I repeat the deletion of the same pet
    Then the response status code should be 404
