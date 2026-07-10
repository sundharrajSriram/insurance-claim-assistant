@submit-claim
Feature: Submit Claim
  As a claims officer
  I want to submit insurance claims through a multi-step form
  So that they can be processed by the multi-agent workflow

  Background:
    Given the application is open
    And I navigate to "Submit Claim"

  Scenario: Page heading is correct
    Then the page heading should be "Submit a Claim"

  Scenario: Stepper shows three steps
    Then the stepper should contain exactly 3 step items

  Scenario: First step is active by default
    Then the first stepper circle should be active

  Scenario: Sample load buttons are present
    Then the "Load approved" sample button should be visible
    And the "Load rejected" sample button should be visible
    And the "Load manual" sample button should be visible

  Scenario: All claim form fields exist
    Then the customer name input should be present
    And the policy number input should be present
    And the claim type select should be present
    And the claim amount input should be present
    And the incident date input should be present
    And the description textarea should be present

  Scenario: Next button is disabled when form is empty
    Then the Next button should be disabled

  Scenario: Policy sidebar shows policies
    Then at least one policy item should be visible

  Scenario: Clicking a policy fills the form
    When I click the first policy item
    Then the customer name input should be populated

  Scenario: Fill form and proceed to document selection
    When I fill the claim form with:
      | customerName | Jane Doe                                              |
      | policyNumber | POL-1001                                              |
      | claimType    | Health                                                |
      | amount       | 5000                                                  |
      | incidentDate | 2024-03-15                                            |
      | description  | Patient required emergency surgery following accident |
    And I click the Next button
    Then the document selection grid should be visible

  Scenario: Load sample and submit produces decision result
    When I load the "approved" sample and submit the workflow
    Then the decision title should be "Claim Approved"
    And the claim ID should start with "CLM-"

  Scenario: Decision result shows node progression
    When I load the "approved" sample and submit the workflow
    Then the node progression section should be visible

  Scenario: Decision result shows mini audit trail
    When I load the "approved" sample and submit the workflow
    Then the mini audit trail should be visible

  Scenario: Review summary shows entered data
    When I fill the claim form with:
      | customerName | Alice Tester                                    |
      | policyNumber | POL-1001                                        |
      | claimType    | Health                                          |
      | amount       | 7500                                            |
      | incidentDate | 2024-06-01                                      |
      | description  | Review summary integration test description     |
    And I click the Next button
    Then the review summary should contain "Alice Tester"
    And the review summary should contain "7,500"

  Scenario: Back button returns to step 1
    When I load the "approved" sample
    And I click the Next button
    And I click the Back button
    Then the customer name input should be present

  Scenario: Submit another claim resets the form
    When I load the "approved" sample and submit the workflow
    And I click the Submit Another Claim button
    Then the customer name input should be present

  Scenario: Rejected sample produces rejection decision
    When I load the "rejected" sample and submit the workflow
    Then the decision title should be "Claim Rejected"

  Scenario: Manual review sample produces manual review decision
    When I load the "manual review" sample and submit the workflow
    Then the decision title should be "Sent to Manual Review"
