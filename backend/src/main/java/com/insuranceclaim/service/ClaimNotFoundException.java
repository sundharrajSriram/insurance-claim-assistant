package com.insuranceclaim.service;

public class ClaimNotFoundException extends RuntimeException {
    public ClaimNotFoundException(String claimId) {
        super("Claim not found: " + claimId);
    }
}
