package com.insuranceclaim.graph.nodes;

import com.insuranceclaim.graph.Node;
import com.insuranceclaim.graph.NodeNames;
import com.insuranceclaim.model.ClaimState;
import com.insuranceclaim.model.Decision;
import com.insuranceclaim.model.RiskLevel;
import org.springframework.stereotype.Component;

/**
 * Consumes every prior node's output and produces an AI recommendation together
 * with an explanation. The recommendation drives the conditional router that
 * chooses APPROVED / REJECTED / MANUAL_REVIEW.
 */
@Component
public class RecommendationNode implements Node {

    @Override
    public String name() {
        return NodeNames.RECOMMENDATION;
    }

    @Override
    public void apply(ClaimState s) {
        // Hard rejections first.
        if ("INVALID".equals(s.getValidationStatus())) {
            reject(s, "Claim failed validation: " + s.getValidationReason());
            return;
        }
        if (!s.isPolicyEligible()) {
            reject(s, "Policy not eligible: " + s.getCoverageReason());
            return;
        }
        if (s.getFraudScore() >= 70 || s.getRiskLevel() == RiskLevel.HIGH) {
            // High fraud/risk is escalated to a human rather than auto-rejected.
            manualReview(s, "High risk profile (fraud score " + s.getFraudScore()
                    + ", risk " + s.getRiskLevel() + ") requires human review.");
            return;
        }
        if ("INCOMPLETE".equals(s.getDocumentStatus())) {
            manualReview(s, "Incomplete documentation: missing "
                    + String.join(", ", s.getMissingDocuments()) + ".");
            return;
        }
        if (s.getRiskLevel() == RiskLevel.MEDIUM || s.getFraudScore() >= 40) {
            manualReview(s, "Medium risk claim flagged for reviewer confirmation.");
            return;
        }

        approve(s, "Low risk, eligible policy and complete documentation.");
    }

    private void approve(ClaimState s, String reason) {
        s.setRecommendation(Decision.APPROVED);
        s.setRecommendationReason(reason);
        s.audit(name(), "Recommendation: APPROVED. " + reason);
    }

    private void reject(ClaimState s, String reason) {
        s.setRecommendation(Decision.REJECTED);
        s.setRecommendationReason(reason);
        s.audit(name(), "Recommendation: REJECTED. " + reason);
    }

    private void manualReview(ClaimState s, String reason) {
        s.setRecommendation(Decision.MANUAL_REVIEW);
        s.setRecommendationReason(reason);
        s.audit(name(), "Recommendation: MANUAL_REVIEW. " + reason);
    }
}
