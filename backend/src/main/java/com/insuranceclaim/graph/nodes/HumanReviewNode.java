package com.insuranceclaim.graph.nodes;

import com.insuranceclaim.graph.Node;
import com.insuranceclaim.graph.NodeNames;
import com.insuranceclaim.model.ClaimState;
import com.insuranceclaim.model.WorkflowStatus;
import org.springframework.stereotype.Component;

/**
 * Human-in-the-loop node. On first entry (no reviewer decision yet) it parks the
 * claim in the review queue and the graph pauses. Once a reviewer has recorded a
 * decision, re-running the graph lets the claim continue to finalisation.
 */
@Component
public class HumanReviewNode implements Node {

    @Override
    public String name() {
        return NodeNames.HUMAN_REVIEW;
    }

    @Override
    public void apply(ClaimState s) {
        if (s.getReviewerDecision() == null) {
            s.setStatus(WorkflowStatus.PENDING_REVIEW);
            s.audit(name(), "Claim queued for human review.");
        } else {
            s.audit(name(), "Reviewer " + safe(s.getReviewerName()) + " decided "
                    + s.getReviewerDecision()
                    + (s.getReviewerComments() != null ? ": " + s.getReviewerComments() : "."));
        }
    }

    private String safe(String v) {
        return (v == null || v.isBlank()) ? "(unknown)" : v;
    }
}
