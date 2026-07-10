package com.insuranceclaim;

import com.insuranceclaim.data.PolicyRepository;
import com.insuranceclaim.data.SampleClaims;
import com.insuranceclaim.graph.ClaimWorkflow;
import com.insuranceclaim.graph.nodes.ClaimValidationNode;
import com.insuranceclaim.graph.nodes.DocumentVerificationNode;
import com.insuranceclaim.graph.nodes.FinalizeNode;
import com.insuranceclaim.graph.nodes.FraudDetectionNode;
import com.insuranceclaim.graph.nodes.HumanReviewNode;
import com.insuranceclaim.graph.nodes.PolicyVerificationNode;
import com.insuranceclaim.graph.nodes.RecommendationNode;
import com.insuranceclaim.graph.nodes.RiskAssessmentNode;
import com.insuranceclaim.model.ClaimRequest;
import com.insuranceclaim.model.ClaimState;
import com.insuranceclaim.model.Decision;
import com.insuranceclaim.model.WorkflowStatus;
import com.insuranceclaim.service.ClaimService;
import com.insuranceclaim.model.ReviewRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClaimWorkflowTest {

    private ClaimService service;

    @BeforeEach
    void setUp() {
        PolicyRepository repo = new PolicyRepository();
        repo.seed();
        ClaimWorkflow workflow = new ClaimWorkflow(
                new ClaimValidationNode(),
                new PolicyVerificationNode(repo),
                new DocumentVerificationNode(),
                new FraudDetectionNode(),
                new RiskAssessmentNode(),
                new RecommendationNode(),
                new HumanReviewNode(),
                new FinalizeNode());
        service = new ClaimService(workflow);
    }

    @Test
    void approvedSampleIsApproved() {
        ClaimState s = service.submit(SampleClaims.approved());
        assertEquals(Decision.APPROVED, s.getFinalDecision());
        assertEquals(WorkflowStatus.COMPLETED, s.getStatus());
        assertTrue(s.getAuditLog().size() >= 6);
    }

    @Test
    void rejectedSampleIsRejected() {
        ClaimState s = service.submit(SampleClaims.rejected());
        assertEquals(Decision.REJECTED, s.getFinalDecision());
        assertEquals(WorkflowStatus.COMPLETED, s.getStatus());
    }

    @Test
    void manualReviewSamplePausesThenRespectsReviewer() {
        ClaimState s = service.submit(SampleClaims.manualReview());
        assertEquals(Decision.MANUAL_REVIEW, s.getRecommendation());
        assertEquals(WorkflowStatus.PENDING_REVIEW, s.getStatus());
        assertEquals(1, service.pendingReview().size());

        ReviewRequest review = new ReviewRequest();
        review.setDecision(Decision.APPROVED);
        review.setReviewerName("Claims Officer");
        review.setComments("Verified documents offline.");
        ClaimState resumed = service.review(s.getClaimId(), review);

        assertEquals(Decision.APPROVED, resumed.getFinalDecision());
        assertEquals(WorkflowStatus.COMPLETED, resumed.getStatus());
        assertTrue(service.pendingReview().isEmpty());
    }

    @Test
    void invalidClaimIsRejected() {
        ClaimRequest bad = new ClaimRequest();
        bad.setCustomerName("Test User");
        bad.setPolicyNumber("POL-1001");
        bad.setClaimType("Health");
        bad.setClaimAmount(1000);
        bad.setIncidentDate("2099-01-01"); // future date
        bad.setDescription("short");
        ClaimState s = service.submit(bad);
        assertEquals(Decision.REJECTED, s.getFinalDecision());
    }
}
