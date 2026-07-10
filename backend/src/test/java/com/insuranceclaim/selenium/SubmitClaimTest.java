package com.insuranceclaim.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium UI tests for the Submit Claim page of the Insurance Claim Assistant.
 *
 * Prerequisites:
 *   - Backend running on port 8080
 *   - Frontend running on port 5173
 */
public class SubmitClaimTest extends BaseSeleniumTest {

    // ── Shared helpers ──────────────────────────────────────────────────────────

    /**
     * Fills all required claim form fields on Step 0 (Claim Details).
     */
    private void fillClaimDetailsForm(String customerName, String policyNumber,
                                      String claimType, String claimAmount,
                                      String incidentDate, String description) {
        WebElement nameInput = waitForVisible(By.cssSelector("input[placeholder='Full name']"));
        clearAndType(nameInput, customerName);

        WebElement policyInput = driver.findElement(By.cssSelector("input[placeholder='e.g. POL-1001']"));
        clearAndType(policyInput, policyNumber);

        WebElement claimTypeEl = driver.findElement(By.tagName("select"));
        Select claimTypeSelect = new Select(claimTypeEl);
        claimTypeSelect.selectByVisibleText(claimType);

        WebElement amountInput = driver.findElement(By.cssSelector("input[type='number']"));
        clearAndType(amountInput, claimAmount);

        WebElement dateInput = driver.findElement(By.cssSelector("input[type='date']"));
        clearAndType(dateInput, incidentDate);

        WebElement descInput = driver.findElement(By.tagName("textarea"));
        clearAndType(descInput, description);
    }

    /**
     * Navigates from Step 0 → Step 1 using the "Next: Select Documents" button.
     * Assumes required form fields are already filled.
     */
    private void proceedToStep2() {
        WebElement nextBtn = waitForClickable(
                By.xpath("//button[contains(text(),'Next')]"));
        scrollIntoView(nextBtn);
        nextBtn.click();
        waitForVisible(By.className("doc-grid"));
    }

    /**
     * Loads a sample preset, waits for state update, then proceeds to Step 1 and submits.
     * Waits for the decision-hero element to confirm Step 2 is shown.
     */
    private void loadSampleAndSubmitFull(String sampleButtonPartialText) {
        sleep(1000); // wait for sample buttons to load from API
        clickButtonContaining(sampleButtonPartialText);
        sleep(500); // allow React state to update after loading sample
        proceedToStep2();
        clickButtonContaining("Run Multi-Agent Workflow");
        waitForVisible(By.className("decision-hero"));
    }

    // ── Setup ───────────────────────────────────────────────────────────────────

    @BeforeEach
    void navigateToSubmitClaim() {
        openApp();
        navigateTo("Submit Claim");
    }

    // ── Tests ───────────────────────────────────────────────────────────────────

    /**
     * Test 1: The page heading should read "Submit a Claim".
     */
    @Test
    void pageHeadingIsCorrect() {
        String heading = getPageHeading();
        assertEquals("Submit a Claim", heading,
                "Expected page heading to be 'Submit a Claim' but was: " + heading);
    }

    /**
     * Test 2: The stepper should contain exactly 3 step items.
     */
    @Test
    void stepperShowsThreeSteps() {
        int stepCount = countElements(By.className("stepper-item"));
        assertEquals(3, stepCount,
                "Expected 3 stepper-item elements but found: " + stepCount);
    }

    /**
     * Test 3: The first stepper circle should carry the active class on page load.
     */
    @Test
    void firstStepIsActiveByDefault() {
        WebElement firstCircle = waitForVisible(By.className("stepper-circle"));
        String classes = firstCircle.getAttribute("class");
        assertTrue(classes.contains("stepper-active"),
                "Expected first stepper-circle to have class 'stepper-active' but classes were: " + classes);
    }

    /**
     * Test 4: All three sample-load buttons must be visible.
     */
    @Test
    void sampleButtonsArePresent() {
        // Sample buttons are loaded asynchronously from the API - wait for them explicitly
        waitForVisible(By.xpath("//button[contains(text(),'Load approved')]"));

        assertTrue(isElementPresent(
                By.xpath("//button[contains(text(),'Load approved')]")),
                "Expected 'Load approved' button to be present");
        assertTrue(isElementPresent(
                By.xpath("//button[contains(text(),'Load rejected')]")),
                "Expected 'Load rejected' button to be present");
        assertTrue(isElementPresent(
                By.xpath("//button[contains(text(),'Load manual')]")),
                "Expected 'Load manual review' button to be present");
    }

    /**
     * Test 5: All required form fields exist in the Claim Details step.
     */
    @Test
    void claimDetailsFormFieldsExist() {
        // Customer name input (placeholder="Full name")
        assertTrue(isElementPresent(By.cssSelector("input[placeholder='Full name']")),
                "Customer name input not found");

        // Policy number input (placeholder="e.g. POL-1001")
        assertTrue(isElementPresent(By.cssSelector("input[placeholder='e.g. POL-1001']")),
                "Policy number input not found");

        // Claim type select
        assertTrue(isElementPresent(By.tagName("select")),
                "Claim type select element not found");

        // Claim amount input (type="number")
        assertTrue(isElementPresent(By.cssSelector("input[type='number']")),
                "Claim amount input not found");

        // Incident date input
        assertTrue(isElementPresent(By.cssSelector("input[type='date']")),
                "Incident date input not found");

        // Description textarea
        assertTrue(isElementPresent(By.tagName("textarea")),
                "Incident description textarea not found");
    }

    /**
     * Test 6: The "Next: Select Documents" button is disabled when the form is empty.
     */
    @Test
    void nextButtonDisabledWhenFormEmpty() {
        sleep(500); // wait for form to render
        WebElement nextBtn = waitForVisible(
                By.xpath("//button[contains(text(),'Next')]"));
        assertFalse(nextBtn.isEnabled(),
                "Expected 'Next: Select Documents' button to be disabled when form is empty");
    }

    /**
     * Test 7: The right sidebar should display at least one policy-item.
     */
    @Test
    void policySidebarShowsPolicies() {
        List<WebElement> policyItems = driver.findElements(By.className("policy-item"));
        assertFalse(policyItems.isEmpty(),
                "Expected at least one policy-item in the sidebar but found none");
    }

    /**
     * Test 8: Clicking the first policy item should populate the customerName input.
     */
    @Test
    void clickPolicyFillsForm() {
        sleep(1000); // wait for policies to load from API
        WebElement firstPolicy = waitForClickable(By.className("policy-item"));
        scrollIntoView(firstPolicy);
        firstPolicy.click();
        sleep(500); // wait for React state to propagate

        WebElement nameInput = waitForVisible(By.cssSelector("input[placeholder='Full name']"));
        String value = nameInput.getAttribute("value");
        assertNotNull(value, "Customer name input value should not be null after clicking a policy");
        assertFalse(value.trim().isEmpty(),
                "Expected customerName input to be filled after clicking policy item, but it was empty");
    }

    /**
     * Test 9: Manually filling all required fields enables the Next button and clicking
     * it advances to Step 1 where the document grid is visible.
     */
    @Test
    void fillFormAndProceedToStep2() {
        sleep(500); // wait for form to render
        fillClaimDetailsForm(
                "Jane Doe",
                "POL-1001",
                "Health",
                "5000",
                "2024-03-15",
                "Patient required emergency surgery following an accident."
        );

        WebElement nextBtn = waitForClickable(
                By.xpath("//button[contains(text(),'Next')]"));
        scrollIntoView(nextBtn);
        nextBtn.click();

        waitForVisible(By.className("doc-grid"));
        assertTrue(isElementPresent(By.className("doc-grid")),
                "Expected doc-grid to be visible after proceeding to step 2");
    }

    /**
     * Test 10: The document grid should contain 13 doc-card options on Step 1.
     */
    @Test
    void documentGridShowsDocumentOptions() {
        clickButtonContaining("Load approved");
        sleep(500);
        proceedToStep2();

        int docCardCount = countElements(By.className("doc-card"));
        assertEquals(13, docCardCount,
                "Expected 13 doc-card elements in the document grid but found: " + docCardCount);
    }

    /**
     * Test 11: Clicking a doc-card should toggle the "doc-card-on" class onto it.
     */
    @Test
    void toggleDocumentSelection() {
        clickButtonContaining("Load approved");
        sleep(500);
        proceedToStep2();

        WebElement firstDocCard = waitForClickable(By.className("doc-card"));
        scrollIntoView(firstDocCard);

        // Capture the card reference for the off→on toggle
        // (click once to enable if not already selected)
        String classesBefore = firstDocCard.getAttribute("class");
        if (classesBefore.contains("doc-card-on")) {
            // Already on – click to toggle off, then back on
            firstDocCard.click();
            sleep(200);
            firstDocCard.click();
            sleep(200);
        } else {
            firstDocCard.click();
            sleep(200);
        }

        String classesAfter = firstDocCard.getAttribute("class");
        assertTrue(classesAfter.contains("doc-card-on"),
                "Expected doc-card to have class 'doc-card-on' after clicking, but classes were: " + classesAfter);
    }

    /**
     * Test 12: The review summary on Step 1 should display the customer name and amount
     * that were entered on Step 0.
     */
    @Test
    void reviewSummaryShowsEnteredData() {
        String customerName = "Alice Tester";
        String claimAmount  = "7500";

        sleep(500); // wait for form to render
        fillClaimDetailsForm(
                customerName,
                "POL-1001",
                "Health",
                claimAmount,
                "2024-06-01",
                "Review summary integration test description."
        );
        proceedToStep2();

        WebElement summary = waitForVisible(By.className("submit-summary"));
        String summaryText = summary.getText();

        assertTrue(summaryText.contains(customerName),
                "Expected review summary to contain customer name '" + customerName + "' but got: " + summaryText);
        // Amount is formatted as currency (e.g. ₹7,500) so check for formatted version
        assertTrue(summaryText.contains("7,500") || summaryText.contains("7500"),
                "Expected review summary to contain claim amount '7500' (raw or formatted) but got: " + summaryText);
    }

    /**
     * Test 13: Clicking "Back" on Step 1 should return the user to Step 0 and
     * show the Claim Details form fields again.
     */
    @Test
    void backButtonReturnsToStep1() {
        // Wait for samples to load
        sleep(1000);
        clickButtonContaining("Load approved");
        sleep(500);
        proceedToStep2();

        clickButton("Back");
        sleep(500);

        // Verify form fields are visible again (Step 0 is active)
        WebElement nameInput = waitForVisible(By.cssSelector("input[placeholder='Full name']"));
        assertNotNull(nameInput,
                "Expected to be back on Step 0 with Customer name input visible after clicking Back");
    }

    /**
     * Test 14: Loading the "approved" sample and submitting should produce an APPROVED decision.
     */
    @Test
    void loadApprovedSampleAndSubmit() {
        loadSampleAndSubmitFull("Load approved");

        boolean approvedVisible =
                isElementPresent(By.xpath(
                        "//*[contains(@class,'decision-hero-title') and contains(text(),'Approved')]"))
                || isElementPresent(By.xpath(
                        "//*[contains(@class,'badge') and contains(text(),'APPROVED')]"))
                || isElementPresent(By.xpath(
                        "//*[contains(text(),'APPROVED')]"));

        assertTrue(approvedVisible,
                "Expected an APPROVED decision indicator after submitting the approved sample");
    }

    /**
     * Test 15: Loading the "rejected" sample and submitting should produce a REJECTED decision.
     */
    @Test
    void loadRejectedSampleAndSubmit() {
        loadSampleAndSubmitFull("Load rejected");

        boolean rejectedVisible =
                isElementPresent(By.xpath(
                        "//*[contains(@class,'decision-hero-title') and contains(text(),'Rejected')]"))
                || isElementPresent(By.xpath(
                        "//*[contains(@class,'badge') and contains(text(),'REJECTED')]"))
                || isElementPresent(By.xpath(
                        "//*[contains(text(),'REJECTED')]"));

        assertTrue(rejectedVisible,
                "Expected a REJECTED decision indicator after submitting the rejected sample");
    }

    /**
     * Test 16: Loading the "manual review" sample and submitting should produce a MANUAL REVIEW decision.
     */
    @Test
    void loadManualReviewSampleAndSubmit() {
        loadSampleAndSubmitFull("Load manual review");

        boolean manualVisible =
                isElementPresent(By.xpath(
                        "//*[contains(@class,'decision-hero-title') and contains(text(),'Manual')]"))
                || isElementPresent(By.xpath(
                        "//*[contains(@class,'badge') and contains(text(),'MANUAL')]"))
                || isElementPresent(By.xpath(
                        "//*[contains(text(),'MANUAL REVIEW')]"));

        assertTrue(manualVisible,
                "Expected a MANUAL REVIEW decision indicator after submitting the manual review sample");
    }

    /**
     * Test 17: Clicking "Submit Another Claim" after a decision should reset back to Step 0.
     */
    @Test
    void submitAnotherClaimResetsForm() {
        loadSampleAndSubmitFull("Load approved");

        clickButton("Submit Another Claim");
        sleep(300);

        // Verify we are back on Step 0 – form fields should be visible
        assertTrue(isElementPresent(By.cssSelector("input[placeholder='Full name']")),
                "Expected to return to Step 0 form after clicking 'Submit Another Claim'");

        // Verify the first stepper circle is active again
        WebElement firstCircle = waitForVisible(By.className("stepper-circle"));
        assertTrue(firstCircle.getAttribute("class").contains("stepper-active"),
                "Expected first stepper-circle to be active again after resetting the form");
    }

    /**
     * Test 18: After submission the audit trail list (mini-audit-v2) should have entries.
     */
    @Test
    void decisionResultShowsAuditTrail() {
        loadSampleAndSubmitFull("Load approved");

        // Scroll down to ensure the audit trail is in view
        List<WebElement> auditEntries = driver.findElements(By.className("mini-audit-v2"));
        if (auditEntries.isEmpty()) {
            // Try child list items within any audit container
            auditEntries = driver.findElements(By.cssSelector(".mini-audit-v2 li, .audit-trail li, [class*='audit'] li"));
        }

        assertFalse(auditEntries.isEmpty(),
                "Expected at least one mini-audit-v2 entry in the audit trail after submission");
    }

    /**
     * Test 19: After submission the node-progression panel should list node-step elements.
     */
    @Test
    void decisionResultShowsNodeProgression() {
        loadSampleAndSubmitFull("Load approved");

        List<WebElement> nodeSteps = driver.findElements(By.className("node-step"));
        assertFalse(nodeSteps.isEmpty(),
                "Expected at least one node-step element in the node-progression after submission");
    }
}
