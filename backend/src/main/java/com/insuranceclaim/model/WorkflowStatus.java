package com.insuranceclaim.model;

/**
 * Lifecycle status of a claim as it moves through the graph. A claim routed to
 * MANUAL_REVIEW pauses at PENDING_REVIEW until a human reviewer resumes it.
 */
public enum WorkflowStatus {
    PROCESSING,
    PENDING_REVIEW,
    COMPLETED
}
