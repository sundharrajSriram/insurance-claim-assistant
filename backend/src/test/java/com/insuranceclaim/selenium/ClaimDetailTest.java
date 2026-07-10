package com.insuranceclaim.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium UI tests for the Claim Detail drawer/overlay.
 *
 * The drawer is opened by clicking a claim row from either the Dashboard
 * recent-claims list or the History table.
 *
 * Prerequisites:
 *   - Backend running on port 8080
 *   - Frontend running on port 5173
 */
public class ClaimDetailTest extends BaseSeleniumTest {

    @BeforeEach
    void openApplication() {
        openApp();
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    /**
     * Navigates to Submit Claim, loads the "approved" sample (Aarav Sharma),
     * advances through the stepper, submits the workflow, waits for the
     * decision result, and finally navigates back to Dashboard.
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

        navigateTo("Dashboard");
        sleep(500);
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    /**
     * Test 1 – Submit an approved claim, then click its row on the Dashboard.
     * Verify the drawer overlay becomes visible.
     */
    @Test
    void submitClaimAndOpenFromDashboard() {
        submitApprovedSample();

        // The "Recent Claims" list should contain a list-row button
        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        // The drawer overlay should now be visible
        WebElement overlay = waitForVisible(By.className("drawer-overlay"));
        assertNotNull(overlay, "drawer-overlay should be visible after clicking a claim row");
    }

    /**
     * Test 2 – Verify the drawer-id element shows a "CLM-" prefix.
     */
    @Test
    void drawerShowsClaimId() {
        submitApprovedSample();

        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        WebElement drawerId = waitForVisible(By.className("drawer-id"));
        String claimId = drawerId.getText();
        assertTrue(claimId.startsWith("CLM-"),
                "drawer-id should display a claim ID starting with 'CLM-', got: " + claimId);
    }

    /**
     * Test 3 – Verify the h2 inside the drawer-header contains the
     * customer name "Aarav Sharma".
     */
    @Test
    void drawerShowsCustomerName() {
        submitApprovedSample();

        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        waitForVisible(By.className("drawer-header"));
        WebElement nameHeading = driver.findElement(By.cssSelector(".drawer-header h2"));
        String customerName = nameHeading.getText();
        assertEquals("Aarav Sharma", customerName,
                "Drawer header h2 should display the customer name 'Aarav Sharma'");
    }

    /**
     * Test 4 – Verify the detail-decision section contains badge elements
     * (DecisionBadge, StatusBadge, RiskBadge).
     */
    @Test
    void drawerShowsDecisionBadges() {
        submitApprovedSample();

        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        WebElement detailDecision = waitForVisible(By.className("detail-decision"));
        List<WebElement> badges = detailDecision.findElements(By.className("badge"));
        assertFalse(badges.isEmpty(),
                "detail-decision section should contain at least one badge element");
    }

    /**
     * Test 5 – Verify the detail-grid section contains exactly 6 field
     * elements (Policy, Claim type, Amount, Coverage, Incident date,
     * Submitted).
     */
    @Test
    void drawerShowsClaimFields() {
        submitApprovedSample();

        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        waitForVisible(By.className("detail-grid"));
        List<WebElement> fields = driver.findElements(By.cssSelector(".detail-grid .field"));
        assertEquals(6, fields.size(),
                "detail-grid should contain exactly 6 field elements");
    }

    /**
     * Test 6 – Verify the "Agent Node Outputs" section heading exists and
     * that at least 6 node-out elements are present.
     */
    @Test
    void drawerShowsAgentNodeOutputs() {
        submitApprovedSample();

        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        // Verify the section heading
        List<WebElement> headings = driver.findElements(By.tagName("h3"));
        boolean hasNodeOutputsHeading = headings.stream()
                .anyMatch(h -> h.getText().equals("Agent Node Outputs"));
        assertTrue(hasNodeOutputsHeading,
                "Drawer should contain an h3 'Agent Node Outputs'");

        // Verify at least 6 node-out cards are rendered
        List<WebElement> nodeOuts = driver.findElements(By.className("node-out"));
        assertTrue(nodeOuts.size() >= 6,
                "Drawer should contain at least 6 node-out elements, found: " + nodeOuts.size());
    }

    /**
     * Test 7 – Verify the "Workflow Path" section heading exists and
     * graph-node elements are rendered.
     */
    @Test
    void drawerShowsWorkflowPath() {
        submitApprovedSample();

        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        // Section heading
        List<WebElement> headings = driver.findElements(By.tagName("h3"));
        boolean hasWorkflowHeading = headings.stream()
                .anyMatch(h -> h.getText().equals("Workflow Path"));
        assertTrue(hasWorkflowHeading,
                "Drawer should contain an h3 'Workflow Path'");

        // Graph nodes
        List<WebElement> graphNodes = driver.findElements(By.className("graph-node"));
        assertFalse(graphNodes.isEmpty(),
                "Workflow Path section should contain graph-node elements");
    }

    /**
     * Test 8 – Verify the "Audit Trail" section heading exists and timeline
     * li elements are present.
     */
    @Test
    void drawerShowsAuditTrail() {
        submitApprovedSample();

        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        // Section heading
        List<WebElement> headings = driver.findElements(By.tagName("h3"));
        boolean hasAuditHeading = headings.stream()
                .anyMatch(h -> h.getText().equals("Audit Trail"));
        assertTrue(hasAuditHeading,
                "Drawer should contain an h3 'Audit Trail'");

        // Timeline items
        List<WebElement> timelineItems = driver.findElements(By.cssSelector(".timeline li"));
        assertFalse(timelineItems.isEmpty(),
                "Audit Trail section should contain at least one timeline li element");
    }

    /**
     * Test 9 – Click the × icon-btn to close the drawer and verify it
     * disappears.
     */
    @Test
    void closeDrawerByClickingX() {
        submitApprovedSample();

        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        // Drawer is open – click the close button
        WebElement closeBtn = waitForClickable(By.className("icon-btn"));
        closeBtn.click();
        sleep(500);

        // Drawer overlay should be absent
        waitForElementAbsent(By.className("drawer-overlay"));
        assertFalse(isElementPresent(By.className("drawer-overlay")),
                "drawer-overlay should be absent after clicking the close button");
    }

    /**
     * Test 10 – Re-open the drawer, then click the overlay backdrop to
     * dismiss it and verify the drawer is gone.
     */
    @Test
    void closeDrawerByClickingOverlay() {
        submitApprovedSample();

        // Open the drawer
        WebElement listRow = waitForClickable(By.className("list-row"));
        scrollIntoView(listRow);
        listRow.click();
        sleep(500);

        waitForVisible(By.className("drawer-overlay"));

        // Click the overlay itself (the backdrop outside the aside panel).
        // Use JavaScript click on the overlay element directly so we hit the
        // overlay div rather than the drawer content inside it.
        WebElement overlay = driver.findElement(By.className("drawer-overlay"));
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", overlay);
        sleep(500);

        // Drawer overlay should now be absent
        waitForElementAbsent(By.className("drawer-overlay"));
        assertFalse(isElementPresent(By.className("drawer-overlay")),
                "drawer-overlay should be absent after clicking the overlay backdrop");
    }
}
