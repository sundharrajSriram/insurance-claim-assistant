@end-to-end
Feature: End-to-End Claim Workflows
  As a claims processing system
  I want to process claims through the complete workflow
  So that claims are properly approved, rejected, or sent for manual review

  Background:
    Given the application is open

  Scenario: Full approval workflow
    When I submit an "approved" sample claim
    Then the decision title should be "Claim Approved"
    When I navigate to "Dashboard"
    Then the "Approved" stat should be at least 1
    When I navigate to "History"
    Then the history table should have an "APPROVED" badge

  Scenario: Full rejection workflow
    When I submit a "rejected" sample claim
    Then the decision title should be "Claim Rejected"
    When I navigate to "Dashboard"
    Then the "Rejected" stat should be at least 1
    When I navigate to "History"
    Then the history table should have a "REJECTED" badge

  Scenario: Full manual review and approve workflow
    When I submit a "manual review" sample claim
    Then the decision title should be "Sent to Manual Review"
    When I navigate to "Manual Review"
    Then at least one review card should be present
    When I add a comment "Verified offline"
    And I click the Approve button
    Then the review card count should decrease
    When I navigate to "History"
    Then the history table should have an "APPROVED" badge

  Scenario: Full manual review and reject workflow
    When I submit a "manual review" sample claim
    When I navigate to "Manual Review"
    And I click the Reject button
    Then the review card count should decrease
    When I navigate to "History"
    Then the history table should have a "REJECTED" badge

  Scenario: Multiple claims update dashboard stats
    When I submit an "approved" sample claim
    And I submit a "rejected" sample claim
    And I submit a "manual review" sample claim
    And I navigate to "Dashboard"
    Then the "Total claims" stat should be at least 3
    And the "Approved" stat should be at least 1
    And the "Rejected" stat should be at least 1
    And the "Awaiting review" stat should be at least 1

  Scenario: Dashboard shows progression badges after submission
    When I submit an "approved" sample claim
    And I navigate to "Dashboard"
    Then at least one claim row should be visible on the dashboard
