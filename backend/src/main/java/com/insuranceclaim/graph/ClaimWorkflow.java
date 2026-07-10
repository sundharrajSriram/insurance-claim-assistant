package com.insuranceclaim.graph;

import com.insuranceclaim.graph.nodes.ClaimValidationNode;
import com.insuranceclaim.graph.nodes.DocumentVerificationNode;
import com.insuranceclaim.graph.nodes.FinalizeNode;
import com.insuranceclaim.graph.nodes.FraudDetectionNode;
import com.insuranceclaim.graph.nodes.HumanReviewNode;
import com.insuranceclaim.graph.nodes.PolicyVerificationNode;
import com.insuranceclaim.graph.nodes.RecommendationNode;
import com.insuranceclaim.graph.nodes.RiskAssessmentNode;
import com.insuranceclaim.model.ClaimState;
import com.insuranceclaim.model.Decision;
import org.springframework.stereotype.Component;

/**
 * Assembles the multi-agent claim workflow as a {@link StateGraph}.
 *
 * <pre>
 *   validation -> policy -> document -> fraud -> risk -> recommendation
 *        recommendation --(APPROVED/REJECTED)--> finalize --> END
 *        recommendation --(MANUAL_REVIEW)------> human_review
 *              human_review --(reviewed)-------> finalize --> END
 *              human_review --(pending)--------> [pause]
 * </pre>
 */
@Component
public class ClaimWorkflow {

    private final StateGraph graph;

    public ClaimWorkflow(ClaimValidationNode validation,
                         PolicyVerificationNode policy,
                         DocumentVerificationNode document,
                         FraudDetectionNode fraud,
                         RiskAssessmentNode risk,
                         RecommendationNode recommendation,
                         HumanReviewNode humanReview,
                         FinalizeNode finalize) {

        this.graph = new StateGraph()
                .addNode(validation)
                .addNode(policy)
                .addNode(document)
                .addNode(fraud)
                .addNode(risk)
                .addNode(recommendation)
                .addNode(humanReview)
                .addNode(finalize)
                .setEntryPoint(NodeNames.VALIDATION)
                .addEdge(NodeNames.VALIDATION, NodeNames.POLICY)
                .addEdge(NodeNames.POLICY, NodeNames.DOCUMENT)
                .addEdge(NodeNames.DOCUMENT, NodeNames.FRAUD)
                .addEdge(NodeNames.FRAUD, NodeNames.RISK)
                .addEdge(NodeNames.RISK, NodeNames.RECOMMENDATION)
                // Conditional router after the recommendation node.
                .addConditionalEdges(NodeNames.RECOMMENDATION, state ->
                        state.getRecommendation() == Decision.MANUAL_REVIEW
                                ? NodeNames.HUMAN_REVIEW
                                : NodeNames.FINALIZE)
                // Human review pauses (null) until a reviewer decision exists.
                .addConditionalEdges(NodeNames.HUMAN_REVIEW, state ->
                        state.getReviewerDecision() != null ? NodeNames.FINALIZE : null)
                .addEdge(NodeNames.FINALIZE, StateGraph.END);
    }

    /** Run the full workflow for a freshly submitted claim. */
    public void submit(ClaimState state) {
        graph.run(state);
    }

    /** Resume a paused claim after a human review decision was recorded. */
    public void resumeAfterReview(ClaimState state) {
        graph.run(state, NodeNames.HUMAN_REVIEW);
    }
}
