# Petstore API Automation with Playwright & Cucumber

An automated API test suite for the Swagger Petstore API designed to maximize test coverage, defect detection, and execution efficiency using **Microsoft Playwright for Java**, **Cucumber (BDD)**, **JUnit 5 Platform Suite**, and comprehensive **HTML Reporting**.

---

## 🚀 Key Features & Tech Stack
- **API Engine**: Microsoft Playwright (`APIRequestContext` for ultra-fast, lightweight HTTP network operations without browser overhead)
- **BDD Framework**: Cucumber Java 7 & Gherkin Features for human-readable specification
- **Test Runner**: JUnit Platform Suite (`@Suite`)
- **Assertions & Object Mapping**: AssertJ & Jackson Databind
- **Reporting**:
  - Built-in Cucumber HTML Reporter (`target/cucumber-reports/cucumber.html`)
  - Masterthought Cucumber HTML Overview Reporter (`target/cucumber-html-reports/cucumber-html-reports/overview-features.html`)

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
│       │           │   └── TestContext.java          # Thread-safe context management & session state
│       │           ├── hooks
│       │           │   └── Hooks.java                # Setup/TearDown Playwright lifecycle & report attachments
│       │           ├── model
│       │           │   ├── Category.java             # Category model
│       │           │   ├── Pet.java                  # Pet domain model
│       │           │   └── Tag.java                  # Tag model
│       │           ├── runner
│       │           │   └── TestRunner.java           # JUnit 5 suite runner for Cucumber features
│       │           └── steps
│       │               └── PetStepDefinitions.java   # Step definitions using Playwright APIRequestContext
│       └── resources
│           ├── cucumber.properties                   # Cucumber runtime & plugin configurations
│           └── features
│               ├── pet_post.feature                  # POST /pet scenarios (creation & validation)
│               ├── pet_find_by_status.feature        # GET /pet/findByStatus scenarios (status filters)
│               └── pet_delete.feature                # DELETE /pet/{petId} scenarios (deletion & idempotency)
```

---

## 🧪 Testing & Test Execution

### 1. Execute the Full Test Suite & Generate Reports
To achieve comprehensive verification across all 18 test cases and produce complete execution analytics:
```bash
mvn clean verify
```

### 2. Execute Fast Test Runs
To run all automated scenarios quickly during active development:
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
Execute targeted subsets based on operational priority and risk level:
```bash
# Smoke test suite (critical path checks)
mvn test -Dcucumber.filter.tags="@smoke"

# Positive validation suite
mvn test -Dcucumber.filter.tags="@positive"

# Negative validation & boundary suite
mvn test -Dcucumber.filter.tags="@negative"

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

## 📊 Viewing HTML Reports

Automated test reporting provides stakeholders with clear visibility into test health and failure diagnostics.

### 1. Report File Locations
- **Standard Cucumber HTML Report**:
  `target/cucumber-reports/cucumber.html`
- **Masterthought Overview Report** (Includes detailed step breakdowns, tag analytics, and charts):
  `target/cucumber-html-reports/cucumber-html-reports/overview-features.html`

### 2. Open Reports Directly in Browser

**macOS:**
```bash
# Open Standard Report
open target/cucumber-reports/cucumber.html

# Open Masterthought Overview Report
open target/cucumber-html-reports/cucumber-html-reports/overview-features.html
```

**Linux:**
```bash
xdg-open target/cucumber-reports/cucumber.html
xdg-open target/cucumber-html-reports/cucumber-html-reports/overview-features.html
```

**Windows (PowerShell / Command Prompt):**
```powershell
start target/cucumber-reports/cucumber.html
start target/cucumber-html-reports/cucumber-html-reports/overview-features.html
```

---

## 📋 Test Matrix Summary

| Endpoint | Feature File | Scenarios Covered | Key Verifications |
|---|---|---|---|
| `POST /pet` | `pet_post.feature` | 6 Scenarios | Dynamic ID creation, status variants (`available`, `pending`, `sold`), missing fields, malformed JSON |
| `GET /pet/findByStatus` | `pet_find_by_status.feature` | 7 Scenarios | Single status filter, multi-status query, unsupported status, omitted param, non-matching query |
| `DELETE /pet/{petId}` | `pet_delete.feature` | 5 Scenarios | Created pet deletion, verify 404 after deletion, non-existent ID, non-numeric ID, double deletion idempotency |
