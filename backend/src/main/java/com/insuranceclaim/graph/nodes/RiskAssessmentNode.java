package com.insuranceclaim.graph.nodes;

import com.insuranceclaim.graph.Node;
import com.insuranceclaim.graph.NodeNames;
import com.insuranceclaim.model.ClaimState;
import com.insuranceclaim.model.RiskLevel;
import org.springframework.stereotype.Component;

/**
 * Combines the fraud score, policy result, claim amount and claim type into an
 * overall risk score (0-100) and a categorical risk level.
 */
@Component
public class RiskAssessmentNode implements Node {

    @Override
    public String name() {
        return NodeNames.RISK;
    }

    @Override
    public void apply(ClaimState s) {
        int score = 0;

        // Fraud contributes the most.
        score += (int) Math.round(s.getFraudScore() * 0.5);

        // Ineligible policy is risky.
        if (!s.isPolicyEligible()) {
            score += 25;
        }

        // Incomplete documents add risk.
        if (s.getMissingDocuments() != null && !s.getMissingDocuments().isEmpty()) {
            score += 15;
        }

        // High-value claims add risk.
        if (s.getCoverageAmount() > 0) {
            double ratio = s.getClaimAmount() / s.getCoverageAmount();
            if (ratio > 0.6) {
                score += 15;
            } else if (ratio > 0.3) {
                score += 8;
            }
        }

        // Certain claim types are inherently higher risk.
        String type = s.getClaimType() == null ? "" : s.getClaimType().toLowerCase();
        if (type.equals("life") || type.equals("property")) {
            score += 5;
        }

        score = Math.min(score, 100);
        s.setRiskScore(score);

        RiskLevel level;
        if (score >= 60) {
            level = RiskLevel.HIGH;
        } else if (score >= 30) {
            level = RiskLevel.MEDIUM;
        } else {
            level = RiskLevel.LOW;
        }
        s.setRiskLevel(level);
        s.audit(name(), "Risk score = " + score + " (" + level + ").");
    }
}
