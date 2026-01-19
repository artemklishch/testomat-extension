## Task Description

* Implement a JUnit 5 extension that reports test execution results to the Testomat.io API
* Create a Java library that can be integrated into any JUnit 5 test suite
* The extension should handle test execution lifecycle and report statuses to Testomat.io

## Requirements

* Create a JUnit 5 extension that automatically reports test results to Testomat.io
* Implement support for two custom annotations:
  * `@TestId` - specifies the test ID in Testomat.io
  * `@Title` - specifies a custom title for the test in reports
* Make at least 3 API calls to the Testomat.io API:
  1. Create a test run (at the beginning of test execution)
  2. Report test results (after each test)
  3. Finish the test run (at the end of all tests)
* Handle test statuses: passed, failed, and skipped
* Include proper error handling for API failures
* Provide a simple configuration mechanism via environment variables:
  * `TESTOMATIO` - API key to report for project
* Include basic documentation on how to use the extension

## API Endpoints to Use

* **Create Test Run**: `POST https://app.testomat.io/api/reporter?api_key=tstmt_your_api_key`
  * Request body: `{ "title": "Run Name" }`
  * Response: `{ "uid": "a0b1c2d3", "url": "https://app.testomat.io/projects/<project-id>/runs/a0b1c2d3" }`

* **Report Test**: `POST https://app.testomat.io/api/reporter/a0b1c2d3/testrun?api_key=tstmt_your_api_key`
  * Request body: 
    ```json
    { 
      "title": "Test Name", 
      "test_id": "T12345",
      "suite_title": "Test Suite Name",
      "file": "TestClass.java",
      "status": "passed|failed|skipped",
      "message": "Error message" (for failed tests),
      "stack": "Stack trace" (for failed tests)
    }
    ```

* **Finish Test Run**: `PUT https://app.testomat.io/api/reporter/a0b1c2d3?api_key=tstmt_your_api_key`
  * Request body: `{ "status_event": "finish", "duration": 25.5 }`

## Implementation Guidelines

* Use Java 8 or higher
* Ensure the extension can be discovered via the JUnit 5 service loader mechanism
* Write a good looking Readme 

## Our Expectations

* You can solve real-world tasks autonomously
* We see that you understand purpose of Testomat.io
* We see that you can aggregate information from various sources
* You can understand Testomat.io UI and you can learn from our documentation
* You know how to make REST API calls
* You figure out how to implement JUnit Extension
* You understand what is opensource project and good practices building opensource library
* You will use AI to analyze information and solve the task

## Acceptance Criteria

* Working code
* Code quality 
* Clear readme
* Time consumed

## Reference Links

* Testomat.io application (free to sign up and try): https://app.testomat.io
* Our Reporting API: https://github.com/testomatio/reporter/blob/master/docs/api.md
* Our main reporter in JavaScript: https://github.com/testomatio/reporter
* Our WIP Java Reporter (on hold): https://github.com/testomatio/java-reporter
* Allure (our competitor): https://github.com/allure-framework/allure-java
