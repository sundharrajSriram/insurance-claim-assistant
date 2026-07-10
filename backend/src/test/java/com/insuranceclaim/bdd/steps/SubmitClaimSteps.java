package com.insuranceclaim.bdd.steps;

import com.insuranceclaim.bdd.hooks.ScenarioContext;
import com.insuranceclaim.bdd.pages.SubmitClaimPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SubmitClaimSteps {

    private final SubmitClaimPage submitPage = new SubmitClaimPage();

    @Then("the stepper should contain exactly {int} step items")
    public void theStepperShouldContainStepItems(int count) {
        assertEquals(count, submitPage.getStepperItemCount());
    }

    @Then("the first stepper circle should be active")
    public void theFirstStepperCircleShouldBeActive() {
        assertTrue(submitPage.isFirstStepActive(),
                "Expected first stepper circle to be active");
    }

    @Then("the {string} sample button should be visible")
    public void theSampleButtonShouldBeVisible(String buttonText) {
        submitPage.waitForSampleButtons();
        assertTrue(submitPage.hasSampleButton(buttonText),
                "Expected '" + buttonText + "' sample button to be present");
    }

    @Then("the customer name input should be present")
    public void theCustomerNameInputShouldBePresent() {
        assertTrue(submitPage.hasCustomerNameInput(), "Customer name input not found");
    }

    @Then("the policy number input should be present")
    public void thePolicyNumberInputShouldBePresent() {
        assertTrue(submitPage.hasPolicyNumberInput(), "Policy number input not found");
    }

    @Then("the claim type select should be present")
    public void theClaimTypeSelectShouldBePresent() {
        assertTrue(submitPage.hasClaimTypeSelect(), "Claim type select not found");
    }

    @Then("the claim amount input should be present")
    public void theClaimAmountInputShouldBePresent() {
        assertTrue(submitPage.hasAmountInput(), "Claim amount input not found");
    }

    @Then("the incident date input should be present")
    public void theIncidentDateInputShouldBePresent() {
        assertTrue(submitPage.hasDateInput(), "Incident date input not found");
    }

    @Then("the description textarea should be present")
    public void theDescriptionTextareaShouldBePresent() {
        assertTrue(submitPage.hasDescriptionTextarea(), "Description textarea not found");
    }

    @Then("the Next button should be disabled")
    public void theNextButtonShouldBeDisabled() {
        assertTrue(submitPage.isNextButtonDisabled(),
                "Expected Next button to be disabled when form is empty");
    }

    @Then("at least one policy item should be visible")
    public void atLeastOnePolicyItemShouldBeVisible() {
        assertFalse(submitPage.getPolicyItems().isEmpty(),
                "Expected at least one policy item");
    }

    @When("I click the first policy item")
    public void iClickTheFirstPolicyItem() {
        submitPage.clickFirstPolicy();
    }

    @Then("the customer name input should be populated")
    public void theCustomerNameInputShouldBePopulated() {
        String value = submitPage.getCustomerNameValue();
        assertNotNull(value, "Customer name should not be null");
        assertFalse(value.trim().isEmpty(), "Customer name should be populated after clicking policy");
    }

    @When("I fill the claim form with:")
    public void iFillTheClaimFormWith(Map<String, String> data) {
        submitPage.fillClaimForm(
                data.get("customerName"),
                data.get("policyNumber"),
                data.get("claimType"),
                data.get("amount"),
                data.get("incidentDate"),
                data.get("description")
        );
    }

    @When("I click the Next button")
    public void iClickTheNextButton() {
        submitPage.clickNextButton();
    }

    @Then("the document selection grid should be visible")
    public void theDocumentSelectionGridShouldBeVisible() {
        assertTrue(submitPage.isDocGridVisible(), "Document grid should be visible");
    }

    @When("I load the {string} sample and submit the workflow")
    public void iLoadTheSampleAndSubmitTheWorkflow(String sampleName) {
        submitPage.loadSampleAndSubmit(sampleName);
    }

    @When("I load the {string} sample")
    public void iLoadTheSample(String sampleName) {
        submitPage.loadSample(sampleName);
    }

    @Then("the decision title should be {string}")
    public void theDecisionTitleShouldBe(String expected) {
        assertEquals(expected, submitPage.getDecisionTitle());
    }

    @Then("the claim ID should start with {string}")
    public void theClaimIdShouldStartWith(String prefix) {
        String claimId = submitPage.getDecisionClaimId();
        assertTrue(claimId.startsWith(prefix),
                "Expected claim ID to start with '" + prefix + "', got: " + claimId);
    }

    @Then("the node progression section should be visible")
    public void theNodeProgressionSectionShouldBeVisible() {
        assertTrue(submitPage.hasNodeProgression(), "Node progression section should be visible");
    }

    @Then("the mini audit trail should be visible")
    public void theMiniAuditTrailShouldBeVisible() {
        assertTrue(submitPage.hasMiniAudit(), "Mini audit trail should be visible");
    }

    @Then("the review summary should contain {string}")
    public void theReviewSummaryShouldContain(String text) {
        String summary = submitPage.getReviewSummaryText();
        assertTrue(summary.contains(text),
                "Expected review summary to contain '" + text + "' but got: " + summary);
    }

    @When("I click the Back button")
    public void iClickTheBackButton() {
        submitPage.clickBackButton();
    }

    @When("I click the Submit Another Claim button")
    public void iClickTheSubmitAnotherClaimButton() {
        submitPage.clickSubmitAnotherClaim();
    }

    @When("I submit a {string} sample claim")
    public void iSubmitASampleClaim(String sampleName) {
        submitPage.navigateTo("Submit Claim");
        submitPage.loadSampleAndSubmit(sampleName);
    }

    @When("I submit an {string} sample claim")
    public void iSubmitAnSampleClaim(String sampleName) {
        submitPage.navigateTo("Submit Claim");
        submitPage.loadSampleAndSubmit(sampleName);
    }
}
