@navigation
Feature: Sidebar Navigation
  As a user of the Insurance Claim Assistant
  I want to navigate between pages using the sidebar
  So that I can access different application features

  Background:
    Given the application is open

  Scenario: App loads and shows Dashboard by default
    Then the page heading should be "Dashboard"

  Scenario: Sidebar brand is visible
    Then the brand mark should display "CF"
    And the brand name should display "ClaimFlow"

  Scenario: All four nav items are present
    Then there should be exactly 4 navigation items
    And the navigation should contain "Dashboard"
    And the navigation should contain "Submit Claim"
    And the navigation should contain "Manual Review"
    And the navigation should contain "History"

  Scenario: Navigate to Submit Claim page
    When I navigate to "Submit Claim"
    Then the page heading should be "Submit a Claim"

  Scenario: Navigate to Manual Review page
    When I navigate to "Manual Review"
    Then the page heading should be "Manual Review Queue"

  Scenario: Navigate to History page
    When I navigate to "History"
    Then the page heading should be "Claim History / Audit Trail"

  Scenario: Navigate back to Dashboard from History
    When I navigate to "History"
    And I navigate to "Dashboard"
    Then the page heading should be "Dashboard"

  Scenario: Active nav item is highlighted
    When I navigate to "Submit Claim"
    Then the "Submit Claim" nav item should have the active class
