@dashboard
Feature: Dashboard
  As a claims manager
  I want to view a dashboard with claim statistics and workflow overview
  So that I can monitor claim processing at a glance

  Background:
    Given the application is open

  Scenario: Dashboard shows four stat cards
    Then the dashboard should display exactly 4 stat cards

  Scenario: Stat cards have expected labels
    Then the stat cards should include label "Total claims"
    And the stat cards should include label "Approved"
    And the stat cards should include label "Rejected"
    And the stat cards should include label "Awaiting review"

  Scenario: Stat card values are numeric
    Then all stat card values should be numeric

  Scenario: Recent claims section shows content
    Then the dashboard should show either empty message or claim rows

  Scenario: Agent Workflow section exists
    Then the "Agent Workflow" section heading should be visible

  Scenario: Workflow graph renders nodes
    Then at least one graph node should be rendered

  Scenario: Dashboard has correct subheading
    Then the dashboard subheading should be "Overview of claim volumes and agent decisions."
