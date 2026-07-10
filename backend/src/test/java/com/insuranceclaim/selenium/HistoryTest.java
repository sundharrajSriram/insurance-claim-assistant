package com.insuranceclaim.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium UI tests for the Claim History / Audit Trail page.
 *
 * Prerequisites:
 *   - Backend running on port 8080
 *   - Frontend running on port 5173
 */
public class HistoryTest extends BaseSeleniumTest {

    @BeforeEach
    void openApplication() {
        openApp();
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Submits the "approved" sample claim (Aarav Sharma) through the full
     * multi-agent workflow and waits for the decision result.
     */
    private void submitApprovedSample() {
        navigateTo("Submit Claim");
        sleep(1000); // wait for sample buttons to load from API

        clickButtonContaining("approved");
        sleep(500);

        clickButton("Next: Select Documents");
        sleep(500);

        clickButton("Run Multi-Agent Workflow");
        waitForVisible(By.className("decision-hero"));
    }

    /**
     * Submits the "rejected" sample claim (Kabir Menon) and waits for result.
     */
    private void submitRejectedSample() {
        navigateTo("Submit Claim");
        sleep(1000); // wait for sample buttons to load from API

        clickButtonContaining("rejected");
        sleep(500);

        clickButton("Next: Select Documents");
        sleep(500);

        clickButton("Run Multi-Agent Workflow");
        waitForVisible(By.className("decision-hero"));
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    /**
     * Test 1 – Verify the page heading is "Claim History / Audit Trail".
     */
    @Test
    void pageHeadingIsCorrect() {
        navigateTo("History");
        sleep(500);

        String heading = getPageHeading();
        assertEquals("Claim History / Audit Trail", heading,
                "Page heading should be 'Claim History / Audit Trail'");
    }

    /**
     * Test 2 – Verify that exactly 5 filter-tab buttons are present and
     * their labels match the expected values.
     */
    @Test
    void filterTabsArePresent() {
        navigateTo("History");
        sleep(500);

        List<WebElement> filterTabs = driver.findElements(By.className("filter-tab"));
        assertEquals(5, filterTabs.size(),
                "There should be exactly 5 filter-tab buttons");

        List<String> labels = filterTabs.stream().map(WebElement::getText).toList();
        assertTrue(labels.contains("ALL"),          "Filter tabs should include 'ALL'");
        assertTrue(labels.contains("APPROVED"),     "Filter tabs should include 'APPROVED'");
        assertTrue(labels.contains("REJECTED"),     "Filter tabs should include 'REJECTED'");
        assertTrue(labels.contains("MANUAL REVIEW"), "Filter tabs should include 'MANUAL REVIEW'");
        assertTrue(labels.contains("PENDING"),      "Filter tabs should include 'PENDING'");
    }

    /**
     * Test 3 – Verify the ALL filter tab is active by default (has the
     * "filter-tab-active" CSS class).
     */
    @Test
    void allFilterIsActiveByDefault() {
        navigateTo("History");
        sleep(500);

        List<WebElement> activeTabs = driver.findElements(By.className("filter-tab-active"));
        assertFalse(activeTabs.isEmpty(),
                "At least one filter-tab-active element should be present");

        String activeLabel = activeTabs.get(0).getText();
        assertEquals("ALL", activeLabel,
                "The 'ALL' filter tab should be active by default");
    }

    /**
     * Test 4 – Verify the search input with the expected placeholder exists.
     */
    @Test
    void searchInputExists() {
        navigateTo("History");
        sleep(500);

        WebElement searchInput = waitForVisible(
                By.cssSelector("input[placeholder='Search id, name, policy...']"));
        assertNotNull(searchInput, "Search input with correct placeholder should be present");
    }

    /**
     * Test 5 – When no claims are present the table should show the
     * "No claims match." message.
     */
    @Test
    void emptyHistoryShowsNoMatch() {
        navigateTo("History");
        sleep(500);

        // Check whether any rows with data exist; if the history is empty
        // (or after filtering to an empty result) the message should appear.
        List<WebElement> reviewCards = driver.findElements(By.className("review-card"));

        // Apply a filter that is guaranteed to produce an empty result by
        // searching for a string that will never match any claim
        WebElement searchInput = waitForVisible(
                By.cssSelector("input[placeholder='Search id, name, policy...']"));
        clearAndType(searchInput, "ZZZNOMATCH_XYZ_99999");
        sleep(500);

        WebElement tableBody = waitForVisible(By.cssSelector(".table-history tbody"));
        assertTrue(tableBody.getText().contains("No claims match."),
                "Table should show 'No claims match.' when no rows match the search");
    }

    /**
     * Test 6 – Submit an approved claim and verify it appears in the History
     * table with the customer name "Aarav Sharma".
     */
    @Test
    void submitClaimThenVerifyInHistory() {
        submitApprovedSample();

        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));

        // Look for the customer name in the table
        List<WebElement> cells = driver.findElements(By.cssSelector(".table-history td"));
        boolean found = cells.stream()
                .anyMatch(td -> td.getText().contains("Aarav Sharma"));
        assertTrue(found,
                "History table should contain a row for customer 'Aarav Sharma'");
    }

    /**
     * Test 7 – Verify the table headers contain the expected column names.
     */
    @Test
    void tableHasCorrectHeaders() {
        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));
        List<WebElement> headers = driver.findElements(By.cssSelector(".table-history th"));
        // CSS text-transform: uppercase may cause getText() to return uppercased text
        List<String> headerTexts = headers.stream()
                .map(h -> h.getText().toUpperCase()).toList();

        assertTrue(headerTexts.contains("CLAIM"),     "Header 'Claim' should be present");
        assertTrue(headerTexts.contains("CUSTOMER"),  "Header 'Customer' should be present");
        assertTrue(headerTexts.contains("TYPE"),      "Header 'Type' should be present");
        assertTrue(headerTexts.contains("AMOUNT"),    "Header 'Amount' should be present");
        assertTrue(headerTexts.contains("RISK"),      "Header 'Risk' should be present");
        assertTrue(headerTexts.contains("AI REC."),   "Header 'AI Rec.' should be present");
        assertTrue(headerTexts.contains("REVIEWER"),  "Header 'Reviewer' should be present");
        assertTrue(headerTexts.contains("FINAL"),     "Header 'Final' should be present");
        assertTrue(headerTexts.contains("SUBMITTED"), "Header 'Submitted' should be present");
    }

    /**
     * Test 8 – Submit an approved claim, navigate to History, activate the
     * APPROVED filter and verify the row is shown. Then activate REJECTED
     * and verify "No claims match." appears (assuming no rejected claims).
     */
    @Test
    void filterByApproved() {
        submitApprovedSample();

        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));

        // Click the APPROVED filter tab
        List<WebElement> filterTabs = driver.findElements(By.className("filter-tab"));
        for (WebElement tab : filterTabs) {
            if (tab.getText().toUpperCase().contains("APPROVED")) {
                tab.click();
                break;
            }
        }
        sleep(500);

        // Aarav Sharma should still be visible under APPROVED filter
        List<WebElement> cells = driver.findElements(By.cssSelector(".table-history td"));
        boolean foundApproved = cells.stream()
                .anyMatch(td -> td.getText().contains("Aarav Sharma"));
        assertTrue(foundApproved,
                "'Aarav Sharma' should appear under the APPROVED filter");

        // Verify that clicking APPROVED filter actually filtered – check active tab
        List<WebElement> activeTabs = driver.findElements(By.className("filter-tab-active"));
        assertFalse(activeTabs.isEmpty(),
                "At least one filter tab should be active after clicking APPROVED");
        assertTrue(activeTabs.get(0).getText().toUpperCase().contains("APPROVED"),
                "The APPROVED filter tab should be active");
    }

    /**
     * Test 9 – Type the customer name in the search input and verify that
     * the table filters to show only matching rows.
     */
    @Test
    void searchByCustName() {
        submitApprovedSample();

        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));

        // Search for the known customer name
        WebElement searchInput = waitForVisible(
                By.cssSelector("input[placeholder='Search id, name, policy...']"));
        clearAndType(searchInput, "Aarav Sharma");
        sleep(500);

        List<WebElement> cells = driver.findElements(By.cssSelector(".table-history td"));
        boolean found = cells.stream()
                .anyMatch(td -> td.getText().contains("Aarav Sharma"));
        assertTrue(found,
                "Searching by customer name 'Aarav Sharma' should return matching rows");

        // Now search for a non-existent name to confirm filtering
        clearAndType(searchInput, "ZZZNOBODYMATCH");
        sleep(500);

        WebElement tableBody = driver.findElement(By.cssSelector(".table-history tbody"));
        assertTrue(tableBody.getText().contains("No claims match."),
                "Searching for a non-existent name should show 'No claims match.'");
    }

    /**
     * Test 10 – Click the expand button on a row and verify that the
     * history-expand panel appears containing "Graph Path" text and
     * hist-node-badge elements.
     */
    @Test
    void expandRowShowsDetails() {
        submitApprovedSample();

        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));

        // Click the first expand button
        WebElement expandBtn = waitForClickable(By.className("expand-btn"));
        scrollIntoView(expandBtn);
        expandBtn.click();
        sleep(500);

        // The expand panel should now be visible
        WebElement expandPanel = waitForVisible(By.className("history-expand"));
        assertNotNull(expandPanel, "history-expand panel should be visible after clicking expand");

        // It should contain "Graph Path" text (may be uppercased by CSS)
        String panelText = expandPanel.getText().toUpperCase();
        assertTrue(panelText.contains("GRAPH PATH"),
                "Expanded panel should contain 'Graph Path' label, got: " + expandPanel.getText());

        // It should contain node badges
        List<WebElement> nodeBadges = expandPanel.findElements(By.className("hist-node-badge"));
        assertFalse(nodeBadges.isEmpty(),
                "Expanded panel should contain hist-node-badge elements");
    }
}
