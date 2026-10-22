@get @regression
Feature: GET /pet/findByStatus - Find Pets by Status Scenarios

  Background:
    Given the pet API endpoint is available

  @smoke @positive
  Scenario: GET-01 Successfully find available pets
    When I find pets by status "available"
    Then the response status code should be 200
    And the response should contain a list of pets with status "available"

  @positive
  Scenario: GET-02 Successfully find pending pets
    When I find pets by status "pending"
    Then the response status code should be 200
    And the response should contain a list of pets with status "pending"

  @positive
  Scenario: GET-03 Successfully find sold pets
    When I find pets by status "sold"
    Then the response status code should be 200
    And the response should contain a list of pets with status "sold"

  @positive @filter
  Scenario: GET-04 Successfully search pets using multiple statuses
    When I find pets by multiple statuses "available" and "pending"
    Then the response status code should be 200
    And the response should contain only pets matching one of statuses "available,pending"

  @negative @boundary
  Scenario: GET-05 Query pets with unsupported status
    When I find pets by status "unsupported_status"
    Then the response status code should be one of 200 or 400
    And the response should contain an empty list of pets

  @boundary
  Scenario: GET-06 Query pets omitting status parameter
    When I find pets without specifying the status parameter
    Then the response status code should be one of 200 or 400
    And the response should be a valid JSON array

  @boundary
  Scenario: GET-07 Search pets for a status with no matching pets
    When I find pets by status "no_match_status_99999"
    Then the response status code should be 200
    And the response should contain an empty list of pets
