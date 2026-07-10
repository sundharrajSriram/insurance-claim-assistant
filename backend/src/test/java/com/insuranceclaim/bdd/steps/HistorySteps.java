package com.insuranceclaim.bdd.steps;

import com.insuranceclaim.bdd.pages.HistoryPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistorySteps {

    private final HistoryPage historyPage = new HistoryPage();

    @Then("there should be exactly {int} filter tabs")
    public void thereShouldBeFilterTabs(int count) {
        assertEquals(count, historyPage.getFilterTabLabels().size());
    }

    @Then("the filter tabs should include {string}")
    public void theFilterTabsShouldInclude(String label) {
        List<String> labels = historyPage.getFilterTabLabels();
        assertTrue(labels.contains(label),
                "Filter tabs should include '" + label + "', found: " + labels);
    }

    @Then("the active filter tab should be {string}")
    public void theActiveFilterTabShouldBe(String expected) {
        assertEquals(expected, historyPage.getActiveFilterLabel());
    }

    @Then("the search input should be present")
    public void theSearchInputShouldBePresent() {
        assertTrue(historyPage.hasSearchInput(), "Search input should be present");
    }

    @When("I search for {string}")
    public void iSearchFor(String query) {
        historyPage.search(query);
    }

    @Then("the table should show {string}")
    public void theTableShouldShow(String text) {
        String bodyText = historyPage.getTableBodyText();
        assertTrue(bodyText.contains(text),
                "Table should show '" + text + "', got: " + bodyText);
    }

    @Then("the history table should contain {string}")
    public void theHistoryTableShouldContain(String text) {
        historyPage.sleep(500);
        assertTrue(historyPage.tableContainsText(text),
                "History table should contain '" + text + "'");
    }

    @Then("the table headers should include {string}")
    public void theTableHeadersShouldInclude(String header) {
        List<String> headers = historyPage.getTableHeaders();
        assertTrue(headers.contains(header),
                "Table headers should include '" + header + "', found: " + headers);
    }

    @When("I click the {string} filter tab")
    public void iClickTheFilterTab(String tabName) {
        historyPage.clickFilterTab(tabName);
    }

    @Then("the history table should have an {string} badge")
    public void theHistoryTableShouldHaveAnBadge(String badgeText) {
        historyPage.sleep(500);
        assertTrue(historyPage.hasBadgeWithText(badgeText),
                "History table should have a '" + badgeText + "' badge");
    }

    @Then("the history table should have a {string} badge")
    public void theHistoryTableShouldHaveABadge(String badgeText) {
        historyPage.sleep(500);
        assertTrue(historyPage.hasBadgeWithText(badgeText),
                "History table should have a '" + badgeText + "' badge");
    }

    @When("I click the expand button on the first row")
    public void iClickTheExpandButton() {
        historyPage.clickExpandButton();
    }

    @Then("the expanded panel should contain {string}")
    public void theExpandedPanelShouldContain(String text) {
        String panelText = historyPage.getExpandPanelText().toUpperCase();
        assertTrue(panelText.contains(text.toUpperCase()),
                "Expanded panel should contain '" + text + "', got: " + historyPage.getExpandPanelText());
    }

    @Then("the expanded panel should have node badges")
    public void theExpandedPanelShouldHaveNodeBadges() {
        assertTrue(historyPage.hasNodeBadges(), "Expanded panel should have node badges");
    }
}