package com.insuranceclaim.model;

/**
 * Terminal routing outcomes of the claim workflow, mirroring the case study's
 * conditional router (approve / reject / manual review).
 */
public enum Decision {
    APPROVED,
    REJECTED,
    MANUAL_REVIEW,
    PENDING
}
