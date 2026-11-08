# Petstore API Automation with Playwright & Cucumber

An automated API test suite for the Swagger Petstore API designed to maximize test coverage, defect detection, and execution efficiency using **Microsoft Playwright for Java**, **Cucumber (BDD)**, **JUnit 6 Platform Suite**, and comprehensive **HTML Reporting**.

---

## 🚀 Key Features & Tech Stack
- **Runtime**: Java 21 (LTS) — required to build and run
- **API Engine**: Microsoft Playwright 1.63 (`APIRequestContext` for ultra-fast, lightweight HTTP network operations without browser overhead)
- **BDD Framework**: Cucumber Java 8 & Gherkin Features for human-readable specification
- **Test Runner**: JUnit Platform Suite 6 (`@Suite`)
- **Assertions & Object Mapping**: AssertJ 3.27 & Jackson 3 Databind
- **Reporting**:
  - Built-in Cucumber HTML Reporter (`target/cucumber-reports/cucumber.html`)
  - Masterthought Cucumber HTML Overview Reporter (`target/cucumber-html-reports/overview-features.html`)

---

## 📁 Project Structure

```
├── pom.xml
├── README.md
├── src
│   └── test
│       ├── java
│       │   └── com
│       │       └── almezi
│       │           ├── context
│       │           │   └── TestContext.java          # Thread-safe context management & dynamic pet IDs
│       │           ├── hooks
│       │           │   └── Hooks.java                # Shared Playwright lifecycle, request setup & report attachments
│       │           ├── model
│       │           │   ├── Category.java             # Category model
│       │           │   ├── Pet.java                  # Pet domain model
│       │           │   └── Tag.java                  # Tag model
│       │           ├── runner
│       │           │   └── TestRunner.java           # JUnit 6 suite runner for Cucumber features
│       │           └── steps
│       │               └── PetStepDefinitions.java   # Step definitions using Playwright APIRequestContext
│       └── resources
│           ├── cucumber.properties                   # Cucumber runtime & plugin configurations
│           ├── junit-platform.properties             # Default @regression tag filter (-Dcucumber.filter.tags overrides it)
│           └── features
│               ├── pet_post.feature                  # POST /pet scenarios (creation & validation)
│               ├── pet_find_by_status.feature        # GET /pet/findByStatus scenarios (status filters)
│               └── pet_delete.feature                # DELETE /pet/{petId} scenarios (deletion & idempotency)
```

---

## 🧪 Testing & Test Execution

> **Requirement**: JDK 21 (LTS). The build compiles with `--release 21`, so Maven must run on JDK 21+ (`JAVA_HOME` pointed at a JDK 21 install).

### Runtime Configuration
`Hooks` reads two system properties, so the same suite can target another environment without code changes:

| Property | Default | Purpose |
|---|---|---|
| `base.url` | `https://petstore.swagger.io/v2/` | Target API base URL, e.g. `mvn test -Dbase.url=https://staging.example.com/v2/` |
| `api.key` | `special-key` | Value sent as the `api_key` request header, e.g. `mvn test -Dapi.key=...` |

### 1. Execute the Default Test Suite & Generate Reports
The suite contains 20 scenarios; the default run selects the 17 tagged `@regression` and skips the 3 `@sanity` scenarios (execute those via `mvn test -Dcucumber.filter.tags="@sanity"`), producing complete execution analytics:
```bash
mvn clean verify
```

### 2. Execute Fast Test Runs
To run the default `@regression` selection quickly during active development:
```bash
mvn test
```

### 3. Run Specific Feature Suites
Target specific endpoints to isolate test feedback and reduce feedback loops:
```bash
# Run POST /pet scenarios
mvn test -Dcucumber.filter.tags="@post"

# Run GET /pet/findByStatus scenarios
mvn test -Dcucumber.filter.tags="@get"

# Run DELETE /pet/{petId} scenarios
mvn test -Dcucumber.filter.tags="@delete"
```

### 4. Run by Functional Category Tags
The default suite runs `@regression` only; these `-Dcucumber.filter.tags` values change that selection. Execute targeted subsets based on operational priority and risk level:
```bash
# Sanity test suite (critical path checks)
mvn test -Dcucumber.filter.tags="@sanity"

# Positive validation suite
mvn test -Dcucumber.filter.tags="@positive"

# Negative validation & boundary suite
mvn test -Dcucumber.filter.tags="@negative"

# API integration suite
mvn test -Dcucumber.filter.tags="@integration"

# Regression test suite
mvn test -Dcucumber.filter.tags="@regression"
```

---

## 🐞 Debugging Tests

### Option 1: Debug via IntelliJ IDEA (Recommended)
1. **Direct Runner Execution**:
   - Open `src/test/java/com/almezi/runner/TestRunner.java`.
   - Set breakpoints in `PetStepDefinitions.java` or `TestContext.java`.
   - Right-click `TestRunner` and select **Debug 'TestRunner'**.

2. **Direct Feature / Scenario Execution**:
   - Open any `.feature` file in `src/test/resources/features/`.
   - Right-click the green play icon next to a Feature or Scenario and select **Debug 'Scenario: ...'**.

### Option 2: Maven Command Line Debugging
To pause execution and attach a remote debugger on port 5005:
```bash
mvn test -Dmaven.surefire.debug
```
Then attach your IDE's Remote JVM Debugger configuration to port `5005`.

---

## 📊 Viewing HTML Reports & Examples

Automated test reporting provides stakeholders and engineers with immediate feedback, visual metrics, and failure diagnostics.

### 1. Report File Locations
- **Standard Cucumber HTML Report**:
  `target/cucumber-reports/cucumber.html`
- **Masterthought Overview Report** (Includes interactive charts, tag statistics, scenario execution timelines, and step-level response logging):
  `target/cucumber-html-reports/overview-features.html`

### 2. Open Reports Directly in Browser

**macOS:**
```bash
# Open Standard Cucumber Report
open target/cucumber-reports/cucumber.html

# Open Masterthought Overview Report
open target/cucumber-html-reports/overview-features.html
```

**Linux:**
```bash
xdg-open target/cucumber-reports/cucumber.html
xdg-open target/cucumber-html-reports/overview-features.html
```

**Windows (PowerShell / Command Prompt):**
```powershell
start target/cucumber-reports/cucumber.html
start target/cucumber-html-reports/overview-features.html
```

---

### 3. Example Test Report HTML Output

#### A. Masterthought Features Overview Dashboard
When generated via `mvn clean verify` or `mvn net.masterthought:maven-cucumber-reporting:generate`, the overview report displays aggregate KPIs, interactive doughnut charts (Features, Scenarios, Steps), and per-feature execution metrics:

```
========================================================================================================
                                   Petstore API Automation Report
========================================================================================================
 Features: 3 Passed / 0 Failed (100%)  |  Scenarios: 17 Passed / 0 Failed (100%)  |  Steps: 71 Passed
 Total Duration: 24.99s                |  Status: PASSED                         |  Build: #1
========================================================================================================

--------------------------------------------------------------------------------------------------------
Feature                                              Steps (Pass/Fail)    Scenarios (Pass/Fail)   Status
--------------------------------------------------------------------------------------------------------
POST /pet - Pet Creation Scenarios                   29 / 0 (100%)        7 / 0 (100%)            PASSED
GET /pet/findByStatus - Find Pets by Status          24 / 0 (100%)        6 / 0 (100%)            PASSED
DELETE /pet/{petId} - Pet Deletion Scenarios         18 / 0 (100%)        4 / 0 (100%)            PASSED
--------------------------------------------------------------------------------------------------------
TOTAL                                                71 / 0 (100%)        17 / 0 (100%)           PASSED
========================================================================================================
```

#### B. HTML Feature & Scenario Details Example
Each scenario provides expandable step execution records, timestamps, and embedded API response payloads:

```html
<!-- Example of scenario structure rendered in Cucumber HTML Reports -->
<div class="card scenario-container">
  <div class="card-header bg-success text-white">
    <span class="badge badge-light">@sanity</span>
    <span class="badge badge-light">@post</span>
    <strong>Scenario:</strong> Successfully add a new valid pet
    <span class="float-right badge badge-success">PASSED (421ms)</span>
  </div>
  <div class="card-body">
    <ul class="steps-list">
      <li class="step passed">✔ <strong>Given</strong> the pet API endpoint is available</li>
      <li class="step passed">✔ <strong>When</strong> I create a pet with dynamic ID, name "BuddyPlaywright" and status "available"</li>
      <li class="step passed">✔ <strong>Then</strong> the response status code should be 200</li>
      <li class="step passed">✔ <strong>And</strong> the response pet name should be "BuddyPlaywright"</li>
      <li class="step passed">✔ <strong>And</strong> the response pet status should be "available"</li>
      <li class="step passed">✔ <strong>And</strong> the response pet id should match the created dynamic ID</li>
    </ul>

    <!-- Embedded HTTP API Response Attachment -->
    <div class="hook-attachment">
      <button class="btn btn-sm btn-outline-info" data-toggle="collapse" data-target="#apiResponsePayload">
        View API Response [status: 200 OK]
      </button>
      <div id="apiResponsePayload" class="collapse show mt-2">
        <pre class="bg-dark text-light p-2 rounded"><code>{
  "id": 1729613524001,
  "category": { "id": 1, "name": "Dogs" },
  "name": "BuddyPlaywright",
  "photoUrls": ["https://example.com/photo.jpg"],
  "tags": [{ "id": 1, "name": "friendly" }],
  "status": "available"
}</code></pre>
      </div>
    </div>
  </div>
</div>
```

---

## 📋 Test Matrix Summary

| Endpoint | Feature File | Scenarios Covered | Key Verifications |
|---|---|---|---|
| `POST /pet` | `pet_post.feature` | 8 Scenarios | Dynamic ID creation, status variants (`available`, `pending`, `sold`), missing fields, malformed JSON |
| `GET /pet/findByStatus` | `pet_find_by_status.feature` | 7 Scenarios | Single status filter, multi-status query, unsupported status, omitted param, non-matching query |
| `DELETE /pet/{petId}` | `pet_delete.feature` | 5 Scenarios | Created pet deletion, verify 404 after deletion, non-existent ID, non-numeric ID, double deletion idempotency |
