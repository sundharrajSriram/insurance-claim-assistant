@history
Feature: Claim History / Audit Trail
  As a claims manager
  I want to view the history of all processed claims
  So that I can audit decisions and track claim outcomes

  Background:
    Given the application is open

  Scenario: Page heading is correct
    When I navigate to "History"
    Then the page heading should be "Claim History / Audit Trail"

  Scenario: Filter tabs are present
    When I navigate to "History"
    Then there should be exactly 5 filter tabs
    And the filter tabs should include "ALL"
    And the filter tabs should include "APPROVED"
    And the filter tabs should include "REJECTED"
    And the filter tabs should include "MANUAL REVIEW"
    And the filter tabs should include "PENDING"

  Scenario: ALL filter is active by default
    When I navigate to "History"
    Then the active filter tab should be "ALL"

  Scenario: Search input exists
    When I navigate to "History"
    Then the search input should be present

  Scenario: Empty search shows no match message
    When I navigate to "History"
    And I search for "ZZZNOMATCH_XYZ_99999"
    Then the table should show "No claims match."

  Scenario: Submitted claim appears in history
    When I submit an "approved" sample claim
    And I navigate to "History"
    Then the history table should contain "Aarav Sharma"

  Scenario: Table has correct headers
    When I navigate to "History"
    Then the table headers should include "CLAIM"
    And the table headers should include "CUSTOMER"
    And the table headers should include "TYPE"
    And the table headers should include "AMOUNT"
    And the table headers should include "RISK"
    And the table headers should include "AI REC."
    And the table headers should include "REVIEWER"
    And the table headers should include "FINAL"
    And the table headers should include "SUBMITTED"

  Scenario: Filter by approved shows approved claims
    When I submit an "approved" sample claim
    And I navigate to "History"
    And I click the "APPROVED" filter tab
    Then the history table should contain "Aarav Sharma"

  Scenario: Search by customer name
    When I submit an "approved" sample claim
    And I navigate to "History"
    And I search for "Aarav Sharma"
    Then the history table should contain "Aarav Sharma"

  Scenario: Expand row shows details
    When I submit an "approved" sample claim
    And I navigate to "History"
    And I click the expand button on the first row
    Then the expanded panel should contain "GRAPH PATH"
    And the expanded panel should have node badges
