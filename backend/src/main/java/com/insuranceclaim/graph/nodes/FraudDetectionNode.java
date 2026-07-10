package com.insuranceclaim.graph.nodes;

import com.insuranceclaim.graph.Node;
import com.insuranceclaim.graph.NodeNames;
import com.insuranceclaim.model.ClaimState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Heuristic fraud scoring. Produces a 0-100 fraud score and a list of human
 * readable indicators, contributing to explainability.
 */
@Component
public class FraudDetectionNode implements Node {

    private static final List<String> SUSPICIOUS_KEYWORDS = List.of(
            "urgent", "cash only", "lost receipt", "no proof", "backdated",
            "friend's", "duplicate", "as soon as possible", "immediately need");

    @Override
    public String name() {
        return NodeNames.FRAUD;
    }

    @Override
    public void apply(ClaimState s) {
        int score = 0;
        List<String> indicators = new ArrayList<>();

        // Large claim relative to coverage.
        if (s.getCoverageAmount() > 0 && s.getClaimAmount() > 0.8 * s.getCoverageAmount()) {
            score += 30;
            indicators.add("Claim amount is close to the full coverage limit.");
        }

        // Suspicious language in the description.
        String desc = s.getDescription() == null ? "" : s.getDescription().toLowerCase(Locale.ROOT);
        for (String kw : SUSPICIOUS_KEYWORDS) {
            if (desc.contains(kw)) {
                score += 15;
                indicators.add("Suspicious phrase detected: '" + kw + "'.");
            }
        }

        // Very round, high amounts are a mild indicator.
        if (s.getClaimAmount() >= 100000 && s.getClaimAmount() % 100000 == 0) {
            score += 10;
            indicators.add("Claim amount is a suspiciously round figure.");
        }

        // Missing supporting documents raise fraud suspicion.
        if (s.getMissingDocuments() != null && !s.getMissingDocuments().isEmpty()) {
            score += 15;
            indicators.add("Supporting documents are missing.");
        }

        // Thin description.
        if (desc.length() < 25) {
            score += 10;
            indicators.add("Incident description lacks detail.");
        }

        score = Math.min(score, 100);
        s.setFraudScore(score);
        if (indicators.isEmpty()) {
            indicators.add("No fraud indicators detected.");
        }
        s.setFraudIndicators(indicators);
        s.audit(name(), "Fraud score = " + score + " with "
                + (indicators.size()) + " indicator(s).");
    }
}
