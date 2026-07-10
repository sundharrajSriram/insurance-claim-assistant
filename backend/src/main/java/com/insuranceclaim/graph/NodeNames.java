package com.insuranceclaim.graph;

/** Central registry of node names so edges and routers stay consistent. */
public final class NodeNames {
    public static final String VALIDATION = "claim_validation";
    public static final String POLICY = "policy_verification";
    public static final String DOCUMENT = "document_verification";
    public static final String FRAUD = "fraud_detection";
    public static final String RISK = "risk_assessment";
    public static final String RECOMMENDATION = "recommendation";
    public static final String HUMAN_REVIEW = "human_review";
    public static final String FINALIZE = "finalize";

    private NodeNames() {
    }
}
