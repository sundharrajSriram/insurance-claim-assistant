package com.insuranceclaim.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium tests for the Dashboard page of the Insurance Claim Assistant application.
 *
 * Prerequisites:
 *   - Backend running on port 8080 (mvnw spring-boot:run)
 *   - Frontend running on port 5173 (npm run dev)
 */
public class DashboardTest extends BaseSeleniumTest {

    // ── 1. Dashboard shows four stat cards ──────────────────────────────────

    @Test
    void dashboardShowsFourStatCards() {
        openApp();

        // Wait for the stat grid to be present, then count stat-card elements
        waitForVisible(By.className("stat-grid"));
        List<WebElement> statCards = getStatCards();

        assertEquals(4, statCards.size(),
                "Dashboard should display exactly 4 stat cards");
    }

    // ── 2. Stat cards have the expected labels ───────────────────────────────

    @Test
    void statCardsHaveLabels() {
        openApp();

        waitForVisible(By.className("stat-grid"));

        List<WebElement> statLabels = driver.findElements(By.className("stat-label"));

        assertEquals(4, statLabels.size(),
                "Expected 4 stat label elements");

        List<String> expectedLabels = List.of(
                "Total claims",
                "Approved",
                "Rejected",
                "Awaiting review"
        );

        for (String expected : expectedLabels) {
            boolean found = statLabels.stream()
                    .anyMatch(label -> label.getText().contains(expected));
            assertTrue(found,
                    "Stat card with label '" + expected + "' was not found on the Dashboard");
        }
    }

    // ── 3. Stat card values show "0" when no claims have been submitted ──────

    @Test
    void statCardsShowNumericValues() {
        openApp();

        waitForVisible(By.className("stat-grid"));

        List<WebElement> statValues = driver.findElements(By.className("stat-value"));

        assertFalse(statValues.isEmpty(),
                "Expected at least one stat-value element to be present");

        for (WebElement statValue : statValues) {
            String text = statValue.getText().trim();
            assertTrue(text.matches("\\d+"),
                    "Stat value should be a numeric string, but found: '" + text + "'");
        }
    }

    // ── 4. Recent Claims section shows empty-state message ──────────────────

    @Test
    void recentClaimsOrEmptyMessageShown() {
        openApp();

        // Wait for the dashboard card content to load
        waitForVisible(By.className("dash-layout"));
        sleep(500);

        // Either "No claims yet" message or list-row elements should be present
        boolean hasEmptyMessage = isElementPresent(
                By.xpath("//p[contains(@class,'muted') and contains(text(),'No claims yet')]"));
        boolean hasClaimRows = !driver.findElements(By.className("list-row")).isEmpty();

        assertTrue(hasEmptyMessage || hasClaimRows,
                "Dashboard should show either 'No claims yet' message or recent claim rows");
    }

    // ── 5. Agent Workflow section exists with correct h3 heading ────────────

    @Test
    void agentWorkflowSectionExists() {
        openApp();

        waitForVisible(By.className("dash-layout"));

        // Find the h3 element whose text is "Agent Workflow"
        WebElement agentWorkflowHeading = waitForVisible(
                By.xpath("//h3[normalize-space(text())='Agent Workflow']")
        );

        assertTrue(agentWorkflowHeading.isDisplayed(),
                "Expected h3 heading 'Agent Workflow' to be visible on the Dashboard");
    }

    // ── 6. Workflow graph renders node elements (fetched from API) ───────────

    @Test
    void workflowGraphRendersNodes() {
        openApp();

        // The WorkflowGraph component fetches data from the API and renders
        // .graph-node elements — wait up to the configured timeout for them to appear
        waitForVisible(By.className("graph-node"));

        int nodeCount = countElements(By.className("graph-node"));

        assertTrue(nodeCount > 0,
                "Expected at least one graph-node element to be rendered by the workflow graph");
    }

    // ── 7. Dashboard page has the correct subheading (muted description) ─────

    @Test
    void dashboardPageHasCorrectSubheading() {
        openApp();

        waitForVisible(By.className("page-head"));

        // The subheading is a <p class="muted"> directly under the page-head div
        WebElement subheading = waitForVisible(
                By.xpath("//div[contains(@class,'page-head')]//p[contains(@class,'muted')]")
        );

        String subheadingText = subheading.getText().trim();

        assertEquals("Overview of claim volumes and agent decisions.", subheadingText,
                "Expected dashboard subheading text to be "
                        + "'Overview of claim volumes and agent decisions.'");
    }
}
