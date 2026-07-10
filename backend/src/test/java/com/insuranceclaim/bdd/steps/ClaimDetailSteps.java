package com.insuranceclaim.bdd.steps;

import com.insuranceclaim.bdd.pages.ClaimDetailPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class ClaimDetailSteps {

    private final ClaimDetailPage detailPage = new ClaimDetailPage();

    @Then("the claim detail drawer should be visible")
    public void theClaimDetailDrawerShouldBeVisible() {
        assertTrue(detailPage.isDrawerVisible(), "Drawer should be visible");
    }

    @Then("the claim detail drawer should not be visible")
    public void theClaimDetailDrawerShouldNotBeVisible() {
        assertFalse(detailPage.isDrawerVisible(), "Drawer should not be visible");
    }

    @Then("the drawer claim ID should start with {string}")
    public void theDrawerClaimIdShouldStartWith(String prefix) {
        String claimId = detailPage.getDrawerClaimId();
        assertTrue(claimId.startsWith(prefix),
                "Expected drawer claim ID to start with '" + prefix + "', got: " + claimId);
    }

    @Then("the drawer customer name should be {string}")
    public void theDrawerCustomerNameShouldBe(String expected) {
        assertEquals(expected, detailPage.getDrawerCustomerName());
    }

    @Then("the drawer should have decision badges")
    public void theDrawerShouldHaveDecisionBadges() {
        assertTrue(detailPage.hasDecisionBadges(), "Drawer should have decision badges");
    }

    @Then("the drawer should have exactly {int} detail fields")
    public void theDrawerShouldHaveDetailFields(int count) {
        assertEquals(count, detailPage.getFieldCount());
    }

    @Then("the drawer should have an {string} section")
    public void theDrawerShouldHaveASection(String sectionName) {
        switch (sectionName) {
            case "Agent Node Outputs" -> assertTrue(detailPage.hasAgentNodeOutputsSection());
            case "Workflow Path" -> assertTrue(detailPage.hasWorkflowPathSection());
            case "Audit Trail" -> assertTrue(detailPage.hasAuditTrailSection());
            default -> throw new RuntimeException("Unknown section: " + sectionName);
        }
    }

    @Then("the drawer should have a {string} section")
    public void theDrawerShouldHaveASection2(String sectionName) {
        theDrawerShouldHaveASection(sectionName);
    }

    @Then("the drawer should have at least {int} node output cards")
    public void theDrawerShouldHaveNodeOutputCards(int count) {
        assertTrue(detailPage.getNodeOutCount() >= count,
                "Expected at least " + count + " node-out elements, found: " + detailPage.getNodeOutCount());
    }

    @Then("the drawer should have graph nodes")
    public void theDrawerShouldHaveGraphNodes() {
        assertTrue(detailPage.hasGraphNodes(), "Drawer should have graph nodes");
    }

    @Then("the drawer should have timeline items")
    public void theDrawerShouldHaveTimelineItems() {
        assertTrue(detailPage.hasTimelineItems(), "Drawer should have timeline items");
    }

    @When("I close the drawer using the X button")
    public void iCloseTheDrawerUsingTheXButton() {
        detailPage.closeByXButton();
    }

    @When("I close the drawer by clicking the overlay")
    public void iCloseTheDrawerByClickingTheOverlay() {
        detailPage.closeByOverlayClick();
    }
}