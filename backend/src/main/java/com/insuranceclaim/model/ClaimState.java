package com.insuranceclaim.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared graph state passed between every node in the workflow. This is the Java
 * equivalent of the LangGraph {@code TypedDict} state described in the case study:
 * it carries the raw claim input, each agent/node's output, the final decision,
 * and the audit trail.
 */
public class ClaimState {

    // --- Identification ---
    private String claimId;
    private String customerName;
    private String policyNumber;

    // --- Claim details (submitted from the UI) ---
    private String claimType;
    private double claimAmount;
    private String incidentDate;
    private String description;

    // --- Document Verification Node output ---
    private List<String> documentsUploaded = new ArrayList<>();
    private List<String> missingDocuments = new ArrayList<>();
    private String documentStatus;

    // --- Claim Validation Node output ---
    private String validationStatus;
    private String validationReason;

    // --- Policy Verification Node output ---
    private String policyStatus;
    private double coverageAmount;
    private boolean policyEligible;
    private String coverageReason;

    // --- Fraud Detection Node output ---
    private int fraudScore;
    private List<String> fraudIndicators = new ArrayList<>();

    // --- Risk Assessment Node output ---
    private int riskScore;
    private RiskLevel riskLevel;

    // --- Recommendation Node output ---
    private Decision recommendation;
    private String recommendationReason;

    // --- Human Review Node output ---
    private Decision reviewerDecision;
    private String reviewerComments;
    private String reviewerName;

    // --- Final decision + traceability ---
    private Decision finalDecision = Decision.PENDING;
    private List<AuditEntry> auditLog = new ArrayList<>();

    // --- Orchestration metadata (used for the graph visualization + HITL) ---
    private WorkflowStatus status = WorkflowStatus.PROCESSING;
    private List<String> nodePath = new ArrayList<>();
    private String currentNode;
    private Instant submittedAt = Instant.now();
    private Instant updatedAt = Instant.now();

    /** Append a node-level trace to the audit log. */
    public void audit(String node, String message) {
        this.auditLog.add(new AuditEntry(node, message));
        this.updatedAt = Instant.now();
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public double getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(double claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getIncidentDate() {
        return incidentDate;
    }

    public void setIncidentDate(String incidentDate) {
        this.incidentDate = incidentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getDocumentsUploaded() {
        return documentsUploaded;
    }

    public void setDocumentsUploaded(List<String> documentsUploaded) {
        this.documentsUploaded = documentsUploaded;
    }

    public List<String> getMissingDocuments() {
        return missingDocuments;
    }

    public void setMissingDocuments(List<String> missingDocuments) {
        this.missingDocuments = missingDocuments;
    }

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public String getValidationReason() {
        return validationReason;
    }

    public void setValidationReason(String validationReason) {
        this.validationReason = validationReason;
    }

    public String getPolicyStatus() {
        return policyStatus;
    }

    public void setPolicyStatus(String policyStatus) {
        this.policyStatus = policyStatus;
    }

    public double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public boolean isPolicyEligible() {
        return policyEligible;
    }

    public void setPolicyEligible(boolean policyEligible) {
        this.policyEligible = policyEligible;
    }

    public String getCoverageReason() {
        return coverageReason;
    }

    public void setCoverageReason(String coverageReason) {
        this.coverageReason = coverageReason;
    }

    public int getFraudScore() {
        return fraudScore;
    }

    public void setFraudScore(int fraudScore) {
        this.fraudScore = fraudScore;
    }

    public List<String> getFraudIndicators() {
        return fraudIndicators;
    }

    public void setFraudIndicators(List<String> fraudIndicators) {
        this.fraudIndicators = fraudIndicators;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Decision getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Decision recommendation) {
        this.recommendation = recommendation;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public Decision getReviewerDecision() {
        return reviewerDecision;
    }

    public void setReviewerDecision(Decision reviewerDecision) {
        this.reviewerDecision = reviewerDecision;
    }

    public String getReviewerComments() {
        return reviewerComments;
    }

    public void setReviewerComments(String reviewerComments) {
        this.reviewerComments = reviewerComments;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Decision getFinalDecision() {
        return finalDecision;
    }

    public void setFinalDecision(Decision finalDecision) {
        this.finalDecision = finalDecision;
    }

    public List<AuditEntry> getAuditLog() {
        return auditLog;
    }

    public void setAuditLog(List<AuditEntry> auditLog) {
        this.auditLog = auditLog;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public List<String> getNodePath() {
        return nodePath;
    }

    public void setNodePath(List<String> nodePath) {
        this.nodePath = nodePath;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
