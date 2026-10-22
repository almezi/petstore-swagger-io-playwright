# Petstore API Automation with Playwright & Cucumber

This repository contains an API test automation framework for the Swagger Petstore API using **Microsoft Playwright for Java**, **Cucumber (BDD)**, **JUnit 5 Platform Suite**, and **HTML Reporting**.

---

## 🚀 Key Features & Stack
- **API Engine**: Microsoft Playwright (`APIRequestContext` for fast, lightweight HTTP execution)
- **BDD Framework**: Cucumber Java 7 & Gherkin Features
- **Test Runner**: JUnit Platform Suite (`@Suite`)
- **Assertions & Object Mapping**: AssertJ & Jackson Databind
- **Reporting**:
  - Built-in Cucumber HTML Reporter (`target/cucumber-reports/cucumber.html`)
  - Masterthought Cucumber HTML Overview Reporter (`target/cucumber-html-reports/cucumber-html-reports/overview-features.html`)

---

## 📁 Project Structure

```
├── pom.xml
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
│               ├── pet_post.feature                  # POST /pet scenarios (POST-01 to POST-06)
│               ├── pet_find_by_status.feature        # GET /pet/findByStatus scenarios (GET-01 to GET-07)
│               └── pet_delete.feature                # DELETE /pet/{petId} scenarios (DEL-01 to DEL-05)
```

---

## 🧪 Running Tests

### 1. Execute All Tests & Generate Reports
```bash
mvn clean verify
```

### 2. Run Specific Endpoint Test Suites
```bash
# POST /pet scenarios
mvn test -Dcucumber.filter.tags="@post"

# GET /pet/findByStatus scenarios
mvn test -Dcucumber.filter.tags="@get"

# DELETE /pet/{petId} scenarios
mvn test -Dcucumber.filter.tags="@delete"
```

### 3. Run by Category Tags (e.g. `@smoke`, `@negative`, `@positive`)
```bash
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@positive"
mvn test -Dcucumber.filter.tags="@negative"
```

---

## 📊 Viewing HTML Reports

After executing tests:
1. **Standard Cucumber Report**: Open `target/cucumber-reports/cucumber.html` in your browser.
2. **Masterthought HTML Report**: Open `target/cucumber-html-reports/cucumber-html-reports/overview-features.html` in your browser.
