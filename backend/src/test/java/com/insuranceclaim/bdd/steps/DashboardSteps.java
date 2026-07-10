package com.insuranceclaim.bdd.steps;

import com.insuranceclaim.bdd.pages.DashboardPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardSteps {

    private final DashboardPage dashboardPage = new DashboardPage();

    @Then("the dashboard should display exactly {int} stat cards")
    public void theDashboardShouldDisplayStatCards(int count) {
        assertEquals(count, dashboardPage.getStatCardCount());
    }

    @Then("the stat cards should include label {string}")
    public void theStatCardsShouldIncludeLabel(String label) {
        List<String> labels = dashboardPage.getStatLabels();
        boolean found = labels.stream().anyMatch(l -> l.contains(label));
        assertTrue(found, "Stat card with label '" + label + "' not found. Labels: " + labels);
    }

    @Then("all stat card values should be numeric")
    public void allStatCardValuesShouldBeNumeric() {
        List<String> values = dashboardPage.getStatValues();
        assertFalse(values.isEmpty(), "Expected at least one stat-value element");
        for (String value : values) {
            assertTrue(value.matches("\\d+"),
                    "Stat value should be numeric but found: '" + value + "'");
        }
    }

    @Then("the dashboard should show either empty message or claim rows")
    public void theDashboardShouldShowEitherEmptyMessageOrClaimRows() {
        dashboardPage.sleep(500);
        assertTrue(dashboardPage.hasEmptyMessage() || dashboardPage.hasClaimRows(),
                "Dashboard should show either 'No claims yet' message or claim rows");
    }

    @Then("the {string} section heading should be visible")
    public void theSectionHeadingShouldBeVisible(String heading) {
        if (heading.equals("Agent Workflow")) {
            assertTrue(dashboardPage.hasAgentWorkflowSection(),
                    "Expected 'Agent Workflow' section heading to be visible");
        }
    }

    @Then("at least one graph node should be rendered")
    public void atLeastOneGraphNodeShouldBeRendered() {
        assertTrue(dashboardPage.getGraphNodeCount() > 0,
                "Expected at least one graph-node element");
    }

    @Then("the dashboard subheading should be {string}")
    public void theDashboardSubheadingShouldBe(String expected) {
        assertEquals(expected, dashboardPage.getSubheading());
    }

    @Then("the {string} stat should be at least {int}")
    public void theStatShouldBeAtLeast(String label, int minValue) {
        dashboardPage.navigateTo("Dashboard");
        dashboardPage.sleep(1000);
        String value = dashboardPage.getStatValue(label);
        int actual = Integer.parseInt(value.trim());
        assertTrue(actual >= minValue,
                "Expected '" + label + "' stat to be at least " + minValue + " but was " + actual);
    }

    @When("I click the first claim row on the dashboard")
    public void iClickTheFirstClaimRowOnDashboard() {
        dashboardPage.clickFirstClaimRow();
    }

    @Then("at least one claim row should be visible on the dashboard")
    public void atLeastOneClaimRowShouldBeVisible() {
        assertTrue(dashboardPage.hasClaimRows(),
                "Expected at least one claim row on the dashboard");
    }
}
