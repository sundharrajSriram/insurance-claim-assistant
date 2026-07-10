package com.insuranceclaim.bdd.steps;

import com.insuranceclaim.bdd.hooks.ScenarioContext;
import com.insuranceclaim.bdd.pages.ReviewQueuePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewQueueSteps {

    private final ReviewQueuePage reviewPage = new ReviewQueuePage();
    private int cardCountBefore = 0;

    @Then("the reviewer field should contain {string}")
    public void theReviewerFieldShouldContain(String expected) {
        reviewPage.sleep(500);
        assertEquals(expected, reviewPage.getReviewerFieldValue());
    }

    @When("I set the reviewer name to {string}")
    public void iSetTheReviewerNameTo(String name) {
        reviewPage.sleep(500);
        reviewPage.setReviewerName(name);
    }

    @Then("the empty state or review cards should be visible")
    public void theEmptyStateOrReviewCardsShouldBeVisible() {
        reviewPage.sleep(500);
        boolean hasEmpty = reviewPage.isEmptyStateVisible();
        boolean hasCards = reviewPage.getReviewCardCount() > 0;
        assertTrue(hasEmpty || hasCards,
                "Either empty state or review cards should be visible");
    }

    @Then("at least one review card should be present")
    public void atLeastOneReviewCardShouldBePresent() {
        reviewPage.sleep(500);
        assertTrue(reviewPage.getReviewCardCount() >= 1,
                "Expected at least one review card");
    }

    @Then("the review card should contain customer name {string}")
    public void theReviewCardShouldContainCustomerName(String name) {
        reviewPage.sleep(500);
        String topText = reviewPage.getReviewCardCustomerName();
        assertTrue(topText.contains(name),
                "Review card should contain '" + name + "', got: " + topText);
    }

    @Then("the review card should contain claim type {string}")
    public void theReviewCardShouldContainClaimType(String type) {
        String metaText = reviewPage.getReviewCardMeta();
        assertTrue(metaText.contains(type),
                "Review card meta should contain '" + type + "', got: " + metaText);
    }

    @Then("the review card should have at least {int} score bars")
    public void theReviewCardShouldHaveScoreBars(int count) {
        reviewPage.sleep(500);
        assertTrue(reviewPage.getScoreBarCount() >= count,
                "Expected at least " + count + " score bars");
    }

    @Then("the review card should have an {string} button")
    public void theReviewCardShouldHaveAnButton(String buttonText) {
        reviewPage.sleep(500);
        List<String> texts = reviewPage.getActionButtonTexts();
        assertTrue(texts.contains(buttonText),
                "Expected '" + buttonText + "' button, found: " + texts);
    }

    @Then("the review card should have a {string} button")
    public void theReviewCardShouldHaveAButton(String buttonText) {
        List<String> texts = reviewPage.getActionButtonTexts();
        assertTrue(texts.contains(buttonText),
                "Expected '" + buttonText + "' button, found: " + texts);
    }

    @When("I add a comment {string}")
    public void iAddAComment(String comment) {
        reviewPage.sleep(500);
        cardCountBefore = reviewPage.getReviewCardCount();
        reviewPage.addComment(comment);
    }

    @When("I click the Approve button")
    public void iClickTheApproveButton() {
        reviewPage.sleep(500);
        if (cardCountBefore == 0) {
            cardCountBefore = reviewPage.getReviewCardCount();
        }
        reviewPage.clickApprove();
        ScenarioContext.put("cardCountBefore", cardCountBefore);
    }

    @When("I click the Reject button")
    public void iClickTheRejectButton() {
        reviewPage.sleep(500);
        if (cardCountBefore == 0) {
            cardCountBefore = reviewPage.getReviewCardCount();
        }
        reviewPage.clickReject();
        ScenarioContext.put("cardCountBefore", cardCountBefore);
    }

    @Then("the review card count should decrease")
    public void theReviewCardCountShouldDecrease() {
        int before = ScenarioContext.get("cardCountBefore") != null
                ? (int) ScenarioContext.get("cardCountBefore") : cardCountBefore;
        int after = reviewPage.getReviewCardCount();
        assertTrue(after < before,
                "Review card count should decrease. Before: " + before + ", After: " + after);
    }
}