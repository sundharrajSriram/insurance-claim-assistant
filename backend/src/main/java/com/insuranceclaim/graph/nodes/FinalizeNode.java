package com.insuranceclaim.graph.nodes;

import com.insuranceclaim.graph.Node;
import com.insuranceclaim.graph.NodeNames;
import com.insuranceclaim.model.ClaimState;
import com.insuranceclaim.model.Decision;
import com.insuranceclaim.model.WorkflowStatus;
import org.springframework.stereotype.Component;

/**
 * Terminal node that records the final decision and closes the audit trail. For
 * claims that went through human review, the reviewer's decision takes priority
 * over the AI recommendation.
 */
@Component
public class FinalizeNode implements Node {

    @Override
    public String name() {
        return NodeNames.FINALIZE;
    }

    @Override
    public void apply(ClaimState s) {
        Decision decision = s.getReviewerDecision() != null
                ? s.getReviewerDecision()
                : s.getRecommendation();
        if (decision == null || decision == Decision.MANUAL_REVIEW) {
            decision = Decision.PENDING;
        }
        s.setFinalDecision(decision);
        s.setStatus(WorkflowStatus.COMPLETED);
        s.audit(name(), "Final decision recorded: " + decision + ".");
    }
}
