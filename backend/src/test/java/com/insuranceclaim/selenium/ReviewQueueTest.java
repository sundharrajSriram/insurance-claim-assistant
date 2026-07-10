package com.insuranceclaim.selenium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium UI tests for the Manual Review Queue page.
 *
 * Prerequisites:
 *   - Backend running on port 8080
 *   - Frontend running on port 5173
 */
public class ReviewQueueTest extends BaseSeleniumTest {

    @BeforeEach
    void openApplication() {
        openApp();
    }

    // ── Helper ──────────────────────────────────────────────────────────────

    /**
     * Navigates to Submit Claim, loads the manual_review sample, advances
     * through the stepper, and submits the workflow. Returns after the
     * decision result has rendered.
     */
    private void submitManualReviewSample() {
        navigateTo("Submit Claim");
        sleep(1000); // wait for sample buttons to load from API

        // Click "Load manual review" sample button
        clickButtonContaining("manual review");
        sleep(500);

        // Advance to step 2 – document selection
        clickButton("Next: Select Documents");
        sleep(500);

        // Submit and run the workflow
        clickButton("Run Multi-Agent Workflow");

        // Wait until the decision hero is visible (workflow completed)
        waitForVisible(By.className("decision-hero"));
    }

    // ── Tests ───────────────────────────────────────────────────────────────

    /**
     * Test 1 – Verify the page heading is "Manual Review Queue".
     */
    @Test
    void pageHeadingIsCorrect() {
        navigateTo("Manual Review");
        sleep(500);

        String heading = getPageHeading();
        assertEquals("Manual Review Queue", heading,
                "Page heading should be 'Manual Review Queue'");
    }

    /**
     * Test 2 – Verify the reviewer name input defaults to "Claims Officer".
     */
    @Test
    void reviewerFieldHasDefaultValue() {
        navigateTo("Manual Review");
        sleep(500);

        WebElement reviewerInput = waitForVisible(By.cssSelector(".reviewer-field input"));
        String defaultValue = reviewerInput.getAttribute("value");
        assertEquals("Claims Officer", defaultValue,
                "Reviewer input should default to 'Claims Officer'");
    }

    /**
     * Test 3 – When the queue is empty the empty-state card must show the
     * "Nothing to review right now." message.
     */
    @Test
    void emptyQueueShowsMessage() {
        navigateTo("Manual Review");
        sleep(500);

        // Only assert empty state if no review cards are present
        List<WebElement> reviewCards = driver.findElements(By.className("review-card"));
        if (reviewCards.isEmpty()) {
            WebElement emptyCard = waitForVisible(By.className("empty"));
            assertTrue(emptyCard.getText().contains("Nothing to review right now."),
                    "Empty-state card should contain 'Nothing to review right now.'");
        }
    }

    /**
     * Test 4 – Changing the reviewer name input value should be reflected
     * immediately in the field.
     */
    @Test
    void changeReviewerName() {
        navigateTo("Manual Review");
        sleep(500);

        WebElement reviewerInput = waitForVisible(By.cssSelector(".reviewer-field input"));
        clearAndType(reviewerInput, "Senior Adjuster");
        sleep(300);

        String updatedValue = reviewerInput.getAttribute("value");
        assertEquals("Senior Adjuster", updatedValue,
                "Reviewer input value should update to 'Senior Adjuster'");
    }

    /**
     * Test 5 – Submit a manual-review claim and then verify a review-card
     * appears in the queue.
     */
    @Test
    void submitManualReviewClaimThenVerifyInQueue() {
        submitManualReviewSample();

        navigateTo("Manual Review");
        sleep(500);

        List<WebElement> reviewCards = driver.findElements(By.className("review-card"));
        assertFalse(reviewCards.isEmpty(),
                "At least one review-card should be present after submitting a manual-review claim");
    }

    /**
     * Test 6 – After submitting the manual-review sample, verify the
     * review-card displays the correct customer name, claim type, and amount.
     */
    @Test
    void reviewCardShowsClaimDetails() {
        submitManualReviewSample();

        navigateTo("Manual Review");
        sleep(500);

        WebElement reviewCard = waitForVisible(By.className("review-card"));

        // Customer name "Rohan Verma" should appear in the review-top section
        WebElement reviewTop = reviewCard.findElement(By.className("review-top"));
        assertTrue(reviewTop.getText().contains("Rohan Verma"),
                "review-top should contain customer name 'Rohan Verma'");

        // Claim type and amount are shown in review-meta
        WebElement reviewMeta = reviewCard.findElement(By.className("review-meta"));
        String metaText = reviewMeta.getText();
        assertTrue(metaText.contains("Property"),
                "review-meta should display the claim type 'Property'");
        // Amount 1,800,000 may be formatted as ₹18,00,000 (en-IN) or ₹1,800,000 (en-US)
        assertTrue(metaText.contains("18") || metaText.contains("1,800"),
                "review-meta should display the claim amount, got: " + metaText);
    }

    /**
     * Test 7 – Verify that the review-card contains score-bar elements for
     * both Fraud and Risk scores.
     */
    @Test
    void reviewCardHasScoreBars() {
        submitManualReviewSample();

        navigateTo("Manual Review");
        sleep(500);

        WebElement reviewCard = waitForVisible(By.className("review-card"));
        WebElement reviewScores = reviewCard.findElement(By.className("review-scores"));
        List<WebElement> scoreBars = reviewScores.findElements(By.className("scorebar"));

        assertTrue(scoreBars.size() >= 2,
                "review-scores section should contain at least 2 scorebar elements");
    }

    /**
     * Test 8 – Verify all three action buttons (Approve, Reject, Request
     * Documents) are present inside the review-card.
     */
    @Test
    void reviewCardHasAllThreeActionButtons() {
        submitManualReviewSample();

        navigateTo("Manual Review");
        sleep(500);

        WebElement reviewCard = waitForVisible(By.className("review-card"));
        WebElement reviewActions = reviewCard.findElement(By.className("review-actions"));

        List<WebElement> buttons = reviewActions.findElements(By.tagName("button"));
        List<String> buttonTexts = buttons.stream()
                .map(WebElement::getText)
                .toList();

        assertTrue(buttonTexts.contains("Approve"),
                "review-actions should contain an 'Approve' button");
        assertTrue(buttonTexts.contains("Reject"),
                "review-actions should contain a 'Reject' button");
        assertTrue(buttonTexts.contains("Request Documents"),
                "review-actions should contain a 'Request Documents' button");
    }

    /**
     * Test 9 – Type a comment in the textarea and click Approve. The
     * review-card count should decrease by one.
     */
    @Test
    void addCommentAndApprove() {
        submitManualReviewSample();

        navigateTo("Manual Review");
        sleep(500);

        List<WebElement> cardsBefore = driver.findElements(By.className("review-card"));
        int countBefore = cardsBefore.size();
        assertTrue(countBefore >= 1, "At least one review-card should be present");

        WebElement reviewCard = cardsBefore.get(0);

        // Type a reviewer comment
        WebElement commentArea = reviewCard.findElement(By.tagName("textarea"));
        scrollIntoView(commentArea);
        clearAndType(commentArea, "Claim verified - approving.");
        sleep(300);

        // Click the Approve button inside the card's review-actions
        WebElement approveBtn = reviewCard.findElement(By.className("btn-approve"));
        scrollIntoView(approveBtn);
        approveBtn.click();

        // Wait for the API call to complete and card count to decrease
        sleep(2000);
        int countAfter = driver.findElements(By.className("review-card")).size();
        assertTrue(countAfter < countBefore,
                "Review card count should decrease after approving. Before: " + countBefore + ", After: " + countAfter);
    }

    /**
     * Test 10 – After approving a claim from the queue, navigate to History
     * and verify the claim is listed with an APPROVED badge.
     */
    @Test
    void approvedClaimShowsInHistory() {
        submitManualReviewSample();

        navigateTo("Manual Review");
        sleep(500);

        WebElement reviewCard = waitForVisible(By.className("review-card"));
        WebElement approveBtn = reviewCard.findElement(By.className("btn-approve"));
        scrollIntoView(approveBtn);
        approveBtn.click();

        // Wait for the API call to complete
        sleep(3000);

        // Navigate to History and look for an APPROVED badge
        navigateTo("History");
        sleep(500);

        waitForVisible(By.className("table-history"));

        // Find all badge elements; at least one should read "APPROVED"
        List<WebElement> badges = getBadges();
        boolean hasApproved = badges.stream()
                .anyMatch(b -> b.getText().equalsIgnoreCase("APPROVED"));
        assertTrue(hasApproved,
                "History table should show an APPROVED badge for the reviewed claim");
    }
}
