package com.insuranceclaim.data;

/** A mock insurance policy record used by the Policy Verification node. */
public class Policy {
    private final String policyNumber;
    private final String holderName;
    private final String type;
    private final boolean active;
    private final boolean premiumPaid;
    private final double coverageLimit;

    public Policy(String policyNumber, String holderName, String type,
                  boolean active, boolean premiumPaid, double coverageLimit) {
        this.policyNumber = policyNumber;
        this.holderName = holderName;
        this.type = type;
        this.active = active;
        this.premiumPaid = premiumPaid;
        this.coverageLimit = coverageLimit;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isPremiumPaid() {
        return premiumPaid;
    }

    public double getCoverageLimit() {
        return coverageLimit;
    }
}
