package com.insuranceclaim.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium tests for sidebar navigation and page routing in the
 * Insurance Claim Assistant application.
 *
 * Prerequisites:
 *   - Backend running on port 8080 (mvnw spring-boot:run)
 *   - Frontend running on port 5173 (npm run dev)
 */
public class NavigationTest extends BaseSeleniumTest {

    // ── 1. App loads and shows Dashboard by default ─────────────────────────

    @Test
    void appLoadsAndShowsDashboardByDefault() {
        openApp();

        String heading = getPageHeading();

        assertEquals("Dashboard", heading,
                "Expected the default page heading to be 'Dashboard'");
    }

    // ── 2. Sidebar brand mark and brand name are visible ────────────────────

    @Test
    void sidebarBrandIsVisible() {
        openApp();

        WebElement brandMark = waitForVisible(By.className("brand-mark"));
        WebElement brandName = waitForVisible(By.className("brand-name"));

        assertEquals("CF", brandMark.getText(),
                "Brand mark should display the text 'CF'");
        assertEquals("ClaimFlow", brandName.getText(),
                "Brand name should display 'ClaimFlow'");
    }

    // ── 3. All four nav items are present with correct labels ────────────────

    @Test
    void allNavItemsArePresent() {
        openApp();

        List<WebElement> navItems = driver.findElements(By.className("nav-item"));

        assertEquals(4, navItems.size(),
                "There should be exactly 4 sidebar navigation items");

        List<String> expectedLabels = List.of("Dashboard", "Submit Claim", "Manual Review", "History");
        for (String label : expectedLabels) {
            boolean found = navItems.stream()
                    .anyMatch(item -> item.getText().contains(label));
            assertTrue(found, "Nav item with label '" + label + "' was not found");
        }
    }

    // ── 4. Navigate to Submit Claim ──────────────────────────────────────────

    @Test
    void navigateToSubmitClaim() {
        openApp();

        navigateTo("Submit Claim");

        waitForText(By.tagName("h1"), "Submit a Claim");
        assertEquals("Submit a Claim", getPageHeading(),
                "Expected h1 heading 'Submit a Claim' after clicking Submit Claim nav");
    }

    // ── 5. Navigate to Manual Review ────────────────────────────────────────

    @Test
    void navigateToManualReview() {
        openApp();

        navigateTo("Manual Review");

        waitForText(By.tagName("h1"), "Manual Review Queue");
        assertEquals("Manual Review Queue", getPageHeading(),
                "Expected h1 heading 'Manual Review Queue' after clicking Manual Review nav");
    }

    // ── 6. Navigate to History ───────────────────────────────────────────────

    @Test
    void navigateToHistory() {
        openApp();

        navigateTo("History");

        waitForText(By.tagName("h1"), "Claim History / Audit Trail");
        assertEquals("Claim History / Audit Trail", getPageHeading(),
                "Expected h1 heading 'Claim History / Audit Trail' after clicking History nav");
    }

    // ── 7. Navigate back to Dashboard from History ───────────────────────────

    @Test
    void navigateBackToDashboard() {
        openApp();

        // First navigate away to History
        navigateTo("History");
        waitForText(By.tagName("h1"), "Claim History / Audit Trail");

        // Then navigate back to Dashboard
        navigateTo("Dashboard");

        waitForText(By.tagName("h1"), "Dashboard");
        assertEquals("Dashboard", getPageHeading(),
                "Expected h1 heading 'Dashboard' after navigating back from History");
    }

    // ── 8. Active nav item is highlighted with nav-item-active class ─────────

    @Test
    void activeNavItemIsHighlighted() {
        openApp();

        navigateTo("Submit Claim");

        // Find the nav button whose text contains "Submit Claim"
        List<WebElement> navItems = driver.findElements(By.className("nav-item"));
        WebElement submitClaimNav = navItems.stream()
                .filter(item -> item.getText().contains("Submit Claim"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Submit Claim nav item not found"));

        String cssClasses = submitClaimNav.getAttribute("class");
        assertTrue(cssClasses.contains("nav-item-active"),
                "Expected the 'Submit Claim' nav button to have class 'nav-item-active', "
                        + "but its class was: " + cssClasses);
    }
}
