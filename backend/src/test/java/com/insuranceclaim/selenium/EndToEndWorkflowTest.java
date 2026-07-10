package com.insuranceclaim.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end workflow Selenium tests covering the full claim lifecycle across
 * all pages of the Insurance Claim Assistant application.
 *
 * Scenarios covered:
 *   1. Full approval workflow
 *   2. Full rejection workflow
 *   3. Full manual-review → approve workflow
 *   4. Full manual-review → reject workflow
 *   5. Multiple claims updating dashboard stats
 *   6. Dashboard progression badges rendered after submission
 *
 * Prerequisites:
 *   - Backend running on port 8080
 *   - Frontend running on port 5173
 */
public class EndToEndWorkflowTest extends BaseSeleniumTest {

    @BeforeEach
    void openApplication() {
        openApp();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Navigates to Submit Claim, clicks the sample button whose label
     * contains {@code sampleName}, advances through the two-step wizard,
     * triggers the multi-agent workflow, and waits until the decision result
     * is rendered on screen.
     *
     * @param sampleName partial text that appears in the sample button label
     *                   (e.g. "approved", "rejected", "manual review")
     */
    private void submitSample(String sampleName) {
        navigateTo("Submit Claim");
        sleep(1000); // wait for sample buttons to load from API

        clickButtonContaining(sampleName);
        sleep(500);

        clickButton("Next: Select Documents");
        sleep(500);

        clickButton("Run Multi-Agent Workflow");

        // Wait for the decision result panel to be rendered
        waitForVisible(By.className("decision-hero"));
    }

    /**
     * Navigates to the Dashboard, waits for the stat-grid to load, then
     * finds the stat-card whose label matches {@code label} (case-insensitive)
     * and returns its numeric value text.
     *
     * @param label the stat-card label (e.g. "Total claims", "Approved")
     * @return the displayed stat value as a {@link String}
     */
    private String getStatValue(String label) {
        navigateTo("Dashboard");
        sleep(500);

        // Allow the API data to propagate to React state
        sleep(500);

        List<WebElement> statCards = getStatCards();
        for (WebElement card : statCards) {
            String cardLabel = card.findElement(By.className("stat-label")).getText();
            if (cardLabel.equalsIgnoreCase(label)) {
                return card.findElement(By.className("stat-value")).getText();
            }
        }
        throw new RuntimeException("Stat card not found for label: " + label);
    }

    // ── Tests ────────────────────────────────────────────────────────────────

    /**
     * Test 1 – Full approval workflow.
     *
     * Submit → verify APPROVED decision → Dashboard shows approved count ≥ 1
     * → History shows APPROVED badge.
     */
    @Test
    void fullApprovalWorkflow() {
        // Step 1: Submit the approved sample
        submitSample("approved");

        // Step 2: Verify the decision title says "Claim Approved"
        WebElement decisionTitle = waitForVisible(By.className("decision-hero-title"));
        assertEquals("Claim Approved", decisionTitle.getText(),
                "Decision hero title should be 'Claim Approved' for the approved sample");

        // Step 3: Check Dashboard stat – Approved count should be at least 1
        String approvedCount = getStatValue("Approved");
        int approved = Integer.parseInt(approvedCount.trim());
        assertTrue(approved >= 1,
                "Dashboard 'Approved' stat should be at least 1 after submitting approved claim");

        // Step 4: History should contain an APPROVED badge
        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));
        List<WebElement> badges = getBadges();
        boolean hasApproved = badges.stream()
                .anyMatch(b -> b.getText().equalsIgnoreCase("APPROVED"));
        assertTrue(hasApproved,
                "History table should contain an APPROVED badge");
    }

    /**
     * Test 2 – Full rejection workflow.
     *
     * Submit → verify REJECTED decision → Dashboard shows rejected count ≥ 1
     * → History shows REJECTED badge.
     */
    @Test
    void fullRejectionWorkflow() {
        // Step 1: Submit the rejected sample
        submitSample("rejected");

        // Step 2: Verify the decision title says "Claim Rejected"
        WebElement decisionTitle = waitForVisible(By.className("decision-hero-title"));
        assertEquals("Claim Rejected", decisionTitle.getText(),
                "Decision hero title should be 'Claim Rejected' for the rejected sample");

        // Step 3: Check Dashboard stat – Rejected count should be at least 1
        String rejectedCount = getStatValue("Rejected");
        int rejected = Integer.parseInt(rejectedCount.trim());
        assertTrue(rejected >= 1,
                "Dashboard 'Rejected' stat should be at least 1 after submitting rejected claim");

        // Step 4: History should contain a REJECTED badge
        navigateTo("History");
        sleep(500);

        // Filter to REJECTED to isolate
        List<WebElement> filterTabs = driver.findElements(By.className("filter-tab"));
        for (WebElement tab : filterTabs) {
            if (tab.getText().equals("REJECTED")) {
                tab.click();
                break;
            }
        }
        sleep(500);

        List<WebElement> badges = getBadges();
        boolean hasRejected = badges.stream()
                .anyMatch(b -> b.getText().equalsIgnoreCase("REJECTED"));
        assertTrue(hasRejected,
                "History table should contain a REJECTED badge after filtering by REJECTED");
    }

    /**
     * Test 3 – Full manual-review → approve workflow.
     *
     * Submit → verify "Sent to Manual Review" → navigate to Review Queue
     * → verify claim present → add comment → Approve → queue empty
     * → History shows APPROVED badge.
     */
    @Test
    void fullManualReviewAndApproveWorkflow() {
        // Step 1: Submit the manual-review sample
        submitSample("manual review");

        // Step 2: Verify the decision title
        WebElement decisionTitle = waitForVisible(By.className("decision-hero-title"));
        assertEquals("Sent to Manual Review", decisionTitle.getText(),
                "Decision hero title should be 'Sent to Manual Review' for the manual-review sample");

        // Step 3: Navigate to the Review Queue and confirm the claim is present
        navigateTo("Manual Review");
        sleep(500);

        WebElement reviewCard = waitForVisible(By.className("review-card"));
        assertNotNull(reviewCard, "A review-card should be present in the queue");

        // Step 4: Add a comment and approve
        WebElement commentArea = reviewCard.findElement(By.tagName("textarea"));
        scrollIntoView(commentArea);
        clearAndType(commentArea, "Verified offline");
        sleep(300);

        int countBefore = driver.findElements(By.className("review-card")).size();
        WebElement approveBtn = reviewCard.findElement(By.className("btn-approve"));
        scrollIntoView(approveBtn);
        approveBtn.click();

        // Step 5: Card count should decrease after approving
        sleep(3000);
        int countAfter = driver.findElements(By.className("review-card")).size();
        assertTrue(countAfter < countBefore,
                "Review card count should decrease after approval. Before: " + countBefore + ", After: " + countAfter);

        // Step 6: History should show APPROVED badge
        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));
        List<WebElement> badges = getBadges();
        boolean hasApproved = badges.stream()
                .anyMatch(b -> b.getText().equalsIgnoreCase("APPROVED"));
        assertTrue(hasApproved,
                "History table should contain an APPROVED badge after reviewer approval");
    }

    /**
     * Test 4 – Full manual-review → reject workflow.
     *
     * Submit → navigate to Review Queue → click Reject → queue empty
     * → History shows REJECTED badge.
     */
    @Test
    void fullManualReviewAndRejectWorkflow() {
        // Step 1: Submit the manual-review sample
        submitSample("manual review");

        // Step 2: Navigate to the Review Queue
        navigateTo("Manual Review");
        sleep(500);

        WebElement reviewCard = waitForVisible(By.className("review-card"));
        assertNotNull(reviewCard, "A review-card should be present in the queue");

        // Step 3: Click Reject
        int countBefore = driver.findElements(By.className("review-card")).size();
        WebElement rejectBtn = reviewCard.findElement(By.className("btn-reject"));
        scrollIntoView(rejectBtn);
        rejectBtn.click();

        // Step 4: Card count should decrease after rejecting
        sleep(3000);
        int countAfter = driver.findElements(By.className("review-card")).size();
        assertTrue(countAfter < countBefore,
                "Review card count should decrease after rejection. Before: " + countBefore + ", After: " + countAfter);

        // Step 5: History should show REJECTED badge
        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));
        List<WebElement> badges = getBadges();
        boolean hasRejected = badges.stream()
                .anyMatch(b -> b.getText().equalsIgnoreCase("REJECTED"));
        assertTrue(hasRejected,
                "History table should contain a REJECTED badge after reviewer rejection");
    }

    /**
     * Test 5 – Submitting three different sample claims (approved, rejected,
     * manual_review) should update the Dashboard stats so that:
     * total ≥ 3, approved ≥ 1, rejected ≥ 1, pendingReview ≥ 1.
     */
    @Test
    void multipleClaimsUpdateDashboardStats() {
        // Submit all three samples
        submitSample("approved");
        submitSample("rejected");
        submitSample("manual review");

        // Navigate to Dashboard and let the API data load
        navigateTo("Dashboard");
        sleep(1000);

        List<WebElement> statCards = getStatCards();
        assertEquals(4, statCards.size(),
                "Dashboard should have 4 stat-cards");

        // Parse each stat value
        int total         = parseStatCard(statCards, "Total claims");
        int approved      = parseStatCard(statCards, "Approved");
        int rejected      = parseStatCard(statCards, "Rejected");
        int pendingReview = parseStatCard(statCards, "Awaiting review");

        assertTrue(total >= 3,
                "Dashboard 'Total claims' should be at least 3, got: " + total);
        assertTrue(approved >= 1,
                "Dashboard 'Approved' should be at least 1, got: " + approved);
        assertTrue(rejected >= 1,
                "Dashboard 'Rejected' should be at least 1, got: " + rejected);
        assertTrue(pendingReview >= 1,
                "Dashboard 'Awaiting review' should be at least 1, got: " + pendingReview);
    }

    /**
     * Test 6 – After submitting an approved sample, navigate to the Dashboard
     * and verify the "Latest Claim: Agent Progression" card contains
     * prog-badge elements representing each agent node.
     */
    @Test
    void dashboardProgressionBadgesShowAfterSubmit() {
        // Submit an approved sample so there is a latest claim
        submitSample("approved");

        // Navigate to Dashboard
        navigateTo("Dashboard");
        sleep(500);

        // The dash-progression card should be present
        WebElement dashProgression = waitForVisible(By.className("dash-progression"));
        assertNotNull(dashProgression,
                "dash-progression card should be present on the Dashboard");

        // It should contain node-progression div with prog-badge elements
        List<WebElement> progBadges = dashProgression.findElements(By.className("prog-badge"));
        assertFalse(progBadges.isEmpty(),
                "dash-progression card should contain prog-badge elements");

        // Expect at least 6 badges (Validation, Policy, Documents, Fraud, Risk, Decision)
        assertTrue(progBadges.size() >= 6,
                "dash-progression should have at least 6 prog-badge elements, found: "
                        + progBadges.size());
    }

    // ── Private utility ──────────────────────────────────────────────────────

    /**
     * Extracts the integer value from a stat-card whose label matches
     * {@code label} (case-insensitive) inside the given list of stat-card
     * elements.
     */
    private int parseStatCard(List<WebElement> statCards, String label) {
        for (WebElement card : statCards) {
            String cardLabel = card.findElement(By.className("stat-label")).getText();
            if (cardLabel.equalsIgnoreCase(label)) {
                String valueText = card.findElement(By.className("stat-value")).getText().trim();
                return Integer.parseInt(valueText);
            }
        }
        throw new RuntimeException("Stat card not found for label: " + label);
    }
}
