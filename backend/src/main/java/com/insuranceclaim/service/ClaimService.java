package com.insuranceclaim.service;

import com.insuranceclaim.graph.ClaimWorkflow;
import com.insuranceclaim.model.ClaimRequest;
import com.insuranceclaim.model.ClaimState;
import com.insuranceclaim.model.Decision;
import com.insuranceclaim.model.ReviewRequest;
import com.insuranceclaim.model.WorkflowStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Orchestrates claim submission and human review on top of the {@link ClaimWorkflow},
 * and keeps an in-memory record of every processed claim.
 */
@Service
public class ClaimService {

    private final ClaimWorkflow workflow;
    private final Map<String, ClaimState> claims = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1000);

    public ClaimService(ClaimWorkflow workflow) {
        this.workflow = workflow;
    }

    public ClaimState submit(ClaimRequest request) {
        ClaimState state = new ClaimState();
        String id = (request.getClaimId() != null && !request.getClaimId().isBlank())
                ? request.getClaimId() : nextClaimId();
        state.setClaimId(id);
        state.setCustomerName(request.getCustomerName());
        state.setPolicyNumber(request.getPolicyNumber());
        state.setClaimType(request.getClaimType());
        state.setClaimAmount(request.getClaimAmount());
        state.setIncidentDate(request.getIncidentDate());
        state.setDescription(request.getDescription());
        state.setDocumentsUploaded(request.getDocumentsUploaded() == null
                ? new ArrayList<>() : new ArrayList<>(request.getDocumentsUploaded()));

        workflow.submit(state);
        claims.put(state.getClaimId(), state);
        return state;
    }

    public List<ClaimState> findAll() {
        List<ClaimState> all = new ArrayList<>(claims.values());
        all.sort(Comparator.comparing(ClaimState::getSubmittedAt).reversed());
        return all;
    }

    public ClaimState findById(String claimId) {
        ClaimState state = claims.get(claimId);
        if (state == null) {
            throw new ClaimNotFoundException(claimId);
        }
        return state;
    }

    public List<ClaimState> pendingReview() {
        List<ClaimState> pending = new ArrayList<>();
        for (ClaimState s : claims.values()) {
            if (s.getStatus() == WorkflowStatus.PENDING_REVIEW) {
                pending.add(s);
            }
        }
        pending.sort(Comparator.comparing(ClaimState::getSubmittedAt));
        return pending;
    }

    public ClaimState review(String claimId, ReviewRequest request) {
        ClaimState state = findById(claimId);
        if (state.getStatus() != WorkflowStatus.PENDING_REVIEW) {
            throw new IllegalStateException("Claim " + claimId + " is not awaiting review.");
        }
        if (request.getDecision() != Decision.APPROVED
                && request.getDecision() != Decision.REJECTED
                && request.getDecision() != Decision.REQUEST_DOCUMENTS) {
            throw new IllegalArgumentException("Reviewer decision must be APPROVED, REJECTED, or REQUEST_DOCUMENTS.");
        }
        state.setReviewerDecision(request.getDecision());
        state.setReviewerComments(request.getComments());
        state.setReviewerName(request.getReviewerName());
        if (request.getDecision() == Decision.REQUEST_DOCUMENTS) {
            state.audit("human_review", "Reviewer " + request.getReviewerName()
                    + " requested additional documents: " + request.getComments());
            state.setReviewerDecision(null);
            state.setStatus(WorkflowStatus.PENDING_REVIEW);
        } else {
            workflow.resumeAfterReview(state);
        }
        return state;
    }

    public Map<String, Object> stats() {
        int total = claims.size();
        long approved = count(Decision.APPROVED);
        long rejected = count(Decision.REJECTED);
        long pending = claims.values().stream()
                .filter(s -> s.getStatus() == WorkflowStatus.PENDING_REVIEW)
                .count();
        Map<String, Object> stats = new java.util.LinkedHashMap<>();
        stats.put("total", total);
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("pendingReview", pending);
        return stats;
    }

    private long count(Decision decision) {
        return claims.values().stream()
                .filter(s -> s.getFinalDecision() == decision)
                .count();
    }

    private String nextClaimId() {
        return "CLM-" + sequence.incrementAndGet();
    }
}
