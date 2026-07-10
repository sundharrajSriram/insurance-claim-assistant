@claim-detail
Feature: Claim Detail Drawer
  As a claims officer
  I want to view detailed information about a specific claim
  So that I can understand the full context of the claim decision

  Background:
    Given the application is open
    And I submit an "approved" sample claim
    And I navigate to "Dashboard"

  Scenario: Open drawer from dashboard
    When I click the first claim row on the dashboard
    Then the claim detail drawer should be visible

  Scenario: Drawer shows claim ID
    When I click the first claim row on the dashboard
    Then the drawer claim ID should start with "CLM-"

  Scenario: Drawer shows customer name
    When I click the first claim row on the dashboard
    Then the drawer customer name should be "Aarav Sharma"

  Scenario: Drawer shows decision badges
    When I click the first claim row on the dashboard
    Then the drawer should have decision badges

  Scenario: Drawer shows claim fields
    When I click the first claim row on the dashboard
    Then the drawer should have exactly 6 detail fields

  Scenario: Drawer shows agent node outputs
    When I click the first claim row on the dashboard
    Then the drawer should have an "Agent Node Outputs" section
    And the drawer should have at least 6 node output cards

  Scenario: Drawer shows workflow path
    When I click the first claim row on the dashboard
    Then the drawer should have a "Workflow Path" section
    And the drawer should have graph nodes

  Scenario: Drawer shows audit trail
    When I click the first claim row on the dashboard
    Then the drawer should have an "Audit Trail" section
    And the drawer should have timeline items

  Scenario: Close drawer by clicking X button
    When I click the first claim row on the dashboard
    And I close the drawer using the X button
    Then the claim detail drawer should not be visible

  Scenario: Close drawer by clicking overlay
    When I click the first claim row on the dashboard
    And I close the drawer by clicking the overlay
    Then the claim detail drawer should not be visible
