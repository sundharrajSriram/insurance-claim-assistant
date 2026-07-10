@review-queue
Feature: Manual Review Queue
  As a claims reviewer
  I want to review claims flagged for manual review
  So that I can approve or reject them with my judgment

  Background:
    Given the application is open

  Scenario: Page heading is correct
    When I navigate to "Manual Review"
    Then the page heading should be "Manual Review Queue"

  Scenario: Reviewer field has default value
    When I navigate to "Manual Review"
    Then the reviewer field should contain "Claims Officer"

  Scenario: Empty queue shows message
    When I navigate to "Manual Review"
    Then the empty state or review cards should be visible

  Scenario: Change reviewer name
    When I navigate to "Manual Review"
    And I set the reviewer name to "Senior Adjuster"
    Then the reviewer field should contain "Senior Adjuster"

  Scenario: Submit manual review claim appears in queue
    When I submit a "manual review" sample claim
    And I navigate to "Manual Review"
    Then at least one review card should be present

  Scenario: Review card shows claim details
    When I submit a "manual review" sample claim
    And I navigate to "Manual Review"
    Then the review card should contain customer name "Rohan Verma"
    And the review card should contain claim type "Property"

  Scenario: Review card has score bars
    When I submit a "manual review" sample claim
    And I navigate to "Manual Review"
    Then the review card should have at least 2 score bars

  Scenario: Review card has all action buttons
    When I submit a "manual review" sample claim
    And I navigate to "Manual Review"
    Then the review card should have an "Approve" button
    And the review card should have a "Reject" button
    And the review card should have a "Request Documents" button

  Scenario: Add comment and approve claim
    When I submit a "manual review" sample claim
    And I navigate to "Manual Review"
    And I add a comment "Claim verified - approving."
    And I click the Approve button
    Then the review card count should decrease

  Scenario: Approved claim shows in history
    When I submit a "manual review" sample claim
    And I navigate to "Manual Review"
    And I click the Approve button
    And I navigate to "History"
    Then the history table should have an "APPROVED" badge
