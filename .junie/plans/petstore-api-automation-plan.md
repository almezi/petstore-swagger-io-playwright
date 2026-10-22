---
sessionId: session-261022-123458-3kcu
---

# Requirements

### Overview & Goals
The goal of this initiative is to expand the existing Playwright + Cucumber BDD test automation framework into a comprehensive, robust, and automated test suite covering the 18 defined test cases for three core Petstore API endpoints:
1. `POST /pet` — Add a new pet (Positive, Boundary, and Negative validation)
2. `GET /pet/findByStatus` — Find pets by status (Single status, Multi-status, Unsupported status, Default behavior)
3. `DELETE /pet/{petId}` — Delete a pet (Standard delete, Double delete, Non-existent ID, Non-numeric ID)

By automating these 18 scenarios, we maximize regression protection, achieve predictable test execution against shared demo environments through isolated test data strategies, and provide clear HTML execution reports.

---

### Scope
- **In Scope:**
  - Automated Gherkin feature files covering all 18 test cases across `POST /pet`, `GET /pet/findByStatus`, and `DELETE /pet/{petId}`.
  - Step definition extensions in `PetStepDefinitions.java` to support raw payload transmission (malformed JSON, partial fields), multi-status query parameters, and non-numeric pet IDs.
  - Test data isolation utilities generating unique IDs to prevent state collision on the public Swagger Petstore backend (`https://petstore.swagger.io/v2/`).
  - Validation assertions for status codes (200, 400, 404, 405), response schema, JSON fields, and array filters.
  - Tagging architecture (`@smoke`, `@regression`, `@positive`, `@negative`, `@post`, `@get`, `@delete`) and HTML report verification via `maven-cucumber-reporting`.
- **Out of Scope:**
  - Testing other Petstore endpoints (e.g., `/store`, `/user`) not specified in the test case matrix.
  - Performance/load testing under high concurrency.
  - UI-based test automation.

---

### User Stories
- **As an API Quality Engineer**, I want automated BDD tests for `POST /pet` covering valid and invalid payloads so that any regression in pet creation or input validation is immediately caught.
- **As a Developer**, I want automated verification of `GET /pet/findByStatus` with single and multiple status query combinations to guarantee query filtering accuracy and data contract compliance.
- **As a Release Manager**, I want robust deletion and negative scenario validation (`DELETE /pet/{petId}`) with automated HTML reporting to assess test health and API reliability prior to release.

---

### Functional Requirements & Test Case Matrix

#### 1. `POST /pet` Test Scenarios
- **POST-01 (Add valid pet):** Send JSON body with unique numeric ID, valid `name`, `photoUrls`, and `status="available"`. Expect HTTP 200 and matching returned pet payload.
- **POST-02 (Add pet with valid statuses):** Scenario Outline testing `available`, `pending`, and `sold`. Expect HTTP 200 and retained status.
- **POST-03 (Omit required `name`):** Submit body with `photoUrls` but missing `name`. Expect HTTP 405 or 400 rejection.
- **POST-04 (Omit required `photoUrls`):** Submit body with `name` but missing `photoUrls`. Expect HTTP 405 or 400 rejection.
- **POST-05 (Unsupported status):** Submit valid body with status not in enum (e.g., `"unknown_status"`). Expect rejection or sanitized response according to service contract.
- **POST-06 (Malformed JSON):** Send broken JSON syntax (`{"name": "Pet", "id":`) with `Content-Type: application/json`. Expect HTTP 400/405 request rejection.

#### 2. `GET /pet/findByStatus` Test Scenarios
- **GET-01 (Find available pets):** Request with query `status=available`. Expect HTTP 200, array of pets where each item has `status="available"`.
- **GET-02 (Find pending pets):** Request with query `status=pending`. Expect HTTP 200, array of pets where each item has `status="pending"`.
- **GET-03 (Find sold pets):** Request with query `status=sold`. Expect HTTP 200, array of pets where each item has `status="sold"`.
- **GET-04 (Multi-status search):** Request with multiple query parameters (e.g. `status=available&status=pending`). Expect HTTP 200, returned pets match one of the requested statuses.
- **GET-05 (Unsupported status):** Query with `status=unsupported_status`. Expect HTTP 400 or empty result array according to server behavior.
- **GET-06 (Omit status parameter):** Send request without `status` query param. Verify fallback / error behavior (server defaults to `available` or returns 400/empty).
- **GET-07 (No matching pets):** Query status that yields zero results or empty state. Expect HTTP 200 with an empty JSON array `[]`.

#### 3. `DELETE /pet/{petId}` Test Scenarios
- **DEL-01 (Delete existing pet):** Create a fresh test pet with unique ID, delete via `DELETE /pet/{id}`. Expect HTTP 200.
- **DEL-02 (Verify pet is gone):** After DEL-01, retrieve the pet via `GET /pet/{id}`. Expect HTTP 404.
- **DEL-03 (Delete non-existent ID):** Call `DELETE /pet/{nonExistentId}` for an ID that does not exist. Expect HTTP 404.
- **DEL-04 (Invalid non-numeric ID):** Call `DELETE /pet/invalid_string_id`. Expect HTTP 400 or 404.
- **DEL-05 (Delete same pet twice):** Create a test pet, perform deletion (HTTP 200), then immediately repeat deletion with same ID. Expect second deletion to return HTTP 404.

---

### Non-Functional Requirements
- **Test Isolation & Determinism:** Dynamic ID generation ensures test runs do not conflict with concurrent runs or existing public data.
- **Execution Speed:** Pure Playwright `APIRequestContext` executions without browser launch overhead, running all 18 tests in < 15 seconds.
- **Observability:** Automatic response payload attachment to Cucumber scenario reports on failures for rapid debugging.

# Technical Design

### Current Implementation
The repository already provides:
- Playwright Java (`com.microsoft.playwright:playwright:1.49.0`) integrated with Cucumber Java (`7.20.1`) and JUnit Platform Suite (`junit-platform-suite:1.11.3`).
- `TestContext.java` utilizing `ThreadLocal` for managing `Playwright`, `APIRequestContext`, `APIResponse`, raw response strings, and session attributes.
- `Hooks.java` initializing an `APIRequestContext` targeting `https://petstore.swagger.io/v2/` with default headers (`Content-Type: application/json`, `api_key: special-key`).
- `PetStepDefinitions.java` containing initial CRUD step definitions.
- `TestRunner.java` configured with Cucumber engine and JSON/HTML reporting plugins.

---

### Key Decisions
1. **Separation of Feature Files by Endpoint:**
   - *Decision:* Split scenarios into dedicated feature files (`pet_post.feature`, `pet_find_by_status.feature`, `pet_delete.feature`) rather than placing all 18 scenarios in a monolithic file.
   - *Rationale:* Maximizes modularity, readability, and allows targeted test execution (e.g., `-Dcucumber.filter.tags="@post"`).

2. **Dynamic Pet ID Generation Strategy:**
   - *Decision:* Use `System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000, 9999)` for test pet creation.
   - *Rationale:* Avoids collisions on the public Petstore instance and prevents tests from failing due to shared state or pre-existing IDs.

3. **Raw String & Custom Payload Support in Step Definitions:**
   - *Decision:* Add generic step definitions that allow posting raw string bodies (including docstrings or broken JSON) to test malformed payloads without Jackson serialization exceptions.
   - *Rationale:* Essential for testing POST-06 (malformed JSON) and negative schema cases without breaking Java type safety.

4. **Query Parameter Handling for Multi-Value Statuses:**
   - *Decision:* Use Playwright's `RequestOptions.setQueryParam` or URL query building with repeated params (`status=available&status=pending`) to align with OpenAPI `collectionFormat: multi`.
   - *Rationale:* Accurately reflects how modern REST clients query repeated enum parameters.

---

### Architecture Diagram

```mermaid
graph TD
    subgraph Test Execution Layer
        TR[JUnit 5 TestRunner] --> CF[Cucumber Feature Files]
        CF --> |@post| F1[pet_post.feature]
        CF --> |@get| F2[pet_find_by_status.feature]
        CF --> |@delete| F3[pet_delete.feature]
    end

    subgraph Step Definitions & Context Layer
        F1 & F2 & F3 --> SD[PetStepDefinitions]
        SD --> TC[TestContext - ThreadLocal]
        HK[Hooks: Before / After] --> TC
    end

    subgraph HTTP & API Layer
        TC --> PW[Playwright APIRequestContext]
        PW --> |HTTP POST / GET / DELETE| PS[(Petstore REST API v2)]
    end

    subgraph Reporting Layer
        HK --> |Attach Response Logs| CR[Cucumber JSON Report]
        CR --> MR[Masterthought HTML Overview Reports]
    end
```

---

### Proposed Components & File Structure

```
petstore-swagger-io-playwright/
├── src/test/java/com/almezi/
│   ├── context/
│   │   └── TestContext.java              # Updated: helper methods for dynamic ID & raw payloads
│   ├── hooks/
│   │   └── Hooks.java                    # Existing: lifecycle, request setup, response logging
│   ├── model/
│   │   ├── Category.java
│   │   ├── Pet.java                      # Existing: POJO data model
│   │   └── Tag.java
│   ├── runner/
│   │   └── TestRunner.java               # Existing: JUnit Platform Suite runner
│   └── steps/
│       └── PetStepDefinitions.java       # Updated: step definitions covering all 18 test cases
└── src/test/resources/
    ├── cucumber.properties               # Existing: plugin & glue configuration
    └── features/
        ├── pet_post.feature              # New: POST-01 to POST-06
        ├── pet_find_by_status.feature    # New: GET-01 to GET-07
        └── pet_delete.feature            # New: DEL-01 to DEL-05
```

---

### Key Step Definition Signatures & Gherkin Design

#### POST Step Definitions:
- `When I create a pet with dynamic ID, name {string} and status {string}`
- `When I submit a pet creation request with missing name field`
- `When I submit a pet creation request with missing photoUrls field`
- `When I submit a raw pet creation payload:` (with docstring for malformed JSON)

#### GET Step Definitions:
- `When I find pets by status parameter {string}`
- `When I find pets by multiple statuses {string} and {string}`
- `When I find pets without specifying the status parameter`
- `Then the response should contain only pets matching one of statuses {string}`

#### DELETE Step Definitions:
- `Given a pet exists with dynamic ID and name {string}`
- `When I delete the previously created pet`
- `When I delete a pet with non-existent ID {long}`
- `When I delete a pet with invalid non-numeric ID {string}`
- `When I repeat the deletion of the same pet`
- `Then the pet should no longer exist when retrieved by ID`

---

### Risks & Mitigations
| Risk | Impact | Mitigation |
|---|---|---|
| **Public Petstore API Flakiness** (shared public server returning intermittent 500 or deleting data) | Medium | Use dynamic IDs in high ranges (e.g. 9-digit unique IDs) and self-contained prerequisites (`Given a pet exists...`) for every scenario. |
| **Spec Discrepancies on Error Codes** (OpenAPI v2 lists 405 for invalid input, but server may return 400) | Low | Standardize step definitions to assert against expected status or support compliant status assertion expressions. |
| **Malformed JSON Serialization** | Low | Bypass Jackson serialization and send raw byte arrays/strings via `RequestOptions.setData(rawString)`. |

# Testing

### Validation Approach
Automated validation is executed through Maven Surefire and JUnit 5 Platform Suite executing Cucumber features via Playwright's native Java API client.
Reports are generated in both standard Cucumber HTML format and Masterthought HTML report overview.

---

### Key Scenarios & Verification Matrix

| Scenario Group | Scenarios Covered | Primary Verification | Expected Status |
|---|---|---|---|
| **POST Validations** | POST-01, POST-02 | Dynamic ID created, name & status verified | 200 OK |
| **POST Negative/Malformed** | POST-03, POST-04, POST-05, POST-06 | Missing name/photos, invalid status, broken JSON syntax | 400 / 405 Error |
| **GET Status Filters** | GET-01, GET-02, GET-03 | Array iteration asserting all pets match requested status | 200 OK |
| **GET Multi/Edge Queries** | GET-04, GET-05, GET-06, GET-07 | Multi-param parsing, unsupported status handling, empty array checks | 200 / 400 OK |
| **DELETE Validations** | DEL-01, DEL-02 | Create pet -> Delete -> Retrieve returns 404 Not Found | 200 OK then 404 |
| **DELETE Negative/Repeat** | DEL-03, DEL-04, DEL-05 | Non-existent ID, invalid string ID (`"abc"`), double delete | 400 / 404 Not Found |

---

### Verification Commands
- **Run all automated test cases:**
  ```bash
  mvn clean verify
  ```
- **Run only POST scenarios:**
  ```bash
  mvn test -Dcucumber.filter.tags="@post"
  ```
- **Run only GET scenarios:**
  ```bash
  mvn test -Dcucumber.filter.tags="@get"
  ```
- **Run only DELETE scenarios:**
  ```bash
  mvn test -Dcucumber.filter.tags="@delete"
  ```
- **Inspect generated HTML reports:**
  - Standard HTML: `target/cucumber-reports/cucumber.html`
  - Masterthought HTML: `target/cucumber-html-reports/overview-features.html`

# Delivery Steps

### ✓ Step 1: Implement Dynamic Test Data Utilities and Payload Helpers
Establish dynamic data generation and flexible payload dispatch mechanisms to ensure test isolation and support both valid and invalid/malformed requests.

- Enhance `TestContext` and helper utilities to support dynamic pet ID generation (e.g. timestamp-based or random high-range integers) preventing collisions on the shared public Petstore instance.
- Add support in `TestContext` for sending raw string payloads and custom headers to support malformed JSON and missing field tests.
- Extend `Pet` model builder/helper methods to construct partially populated payloads (omitting `name`, omitting `photoUrls`, custom `status`).

### ✓ Step 2: Implement POST /pet Automation Scenarios and Step Definitions
Implement comprehensive Gherkin scenarios and step definitions covering all 6 POST /pet test cases.

- Implement Gherkin scenarios in `src/test/resources/features/pet_post.feature` for POST-01 through POST-06 (`@smoke`, `@post`, `@negative`, `@boundary`).
- Add step definitions in `PetStepDefinitions.java` for creating pets with explicit status values (`available`, `pending`, `sold`, and invalid status `invalid_status`).
- Add step definitions for submitting payloads missing required fields (`name`, `photoUrls`) and malformed JSON strings (`{"id": 123, "name":`).
- Verify HTTP response status codes (200 for valid creations, 400/405/appropriate error response for negative inputs) and validate error bodies where applicable.

### ✓ Step 3: Implement GET /pet/findByStatus Automation Scenarios and Step Definitions
Implement Gherkin scenarios and step definitions covering all 7 GET /pet/findByStatus test cases.

- Implement Gherkin scenarios in `src/test/resources/features/pet_find_by_status.feature` covering GET-01 through GET-07 (`@get`, `@filter`, `@boundary`).
- Implement step definitions to handle single status queries (`available`, `pending`, `sold`), multiple status queries (repeated query params vs comma-separated), unsupported statuses (`unknown`), omitted status queries, and non-matching status filters.
- Implement response assertions to verify list non-emptiness, status conformance across all returned items, and empty list handling when no items match.

### ✓ Step 4: Implement DELETE /pet/{petId} Automation Scenarios and Step Definitions
Implement Gherkin scenarios and step definitions covering all 5 DELETE /pet/{petId} test cases.

- Implement Gherkin scenarios in `src/test/resources/features/pet_delete.feature` covering DEL-01 through DEL-05 (`@delete`, `@cleanup`, `@negative`).
- Implement step definitions for creating prerequisite isolated pets and executing deletion by numeric ID and non-numeric string ID (e.g., `invalid_string_id`).
- Implement step definitions for idempotency and repeated deletion validation (verifying initial delete 200, followed by subsequent delete/get returning 404).
- Ensure teardown hooks safely handle cleanup without failing if a test already performed deletion.

### ✓ Step 5: Configure Test Tagging, Suite Execution, and Verify HTML Reports
Configure execution tags, JUnit suite filters, and verify HTML report generation.

- Organize Cucumber tags (`@post`, `@get`, `@delete`, `@positive`, `@negative`, `@smoke`, `@regression`) across all feature files.
- Update `TestRunner.java` to support running targeted test suites via Maven CLI arguments (`-Dcucumber.filter.tags="..."`).
- Execute `mvn clean verify` to validate all 18 test scenarios across endpoints.
- Confirm Masterthought HTML report generation in `target/cucumber-html-reports/` and standard report in `target/cucumber-reports/`.