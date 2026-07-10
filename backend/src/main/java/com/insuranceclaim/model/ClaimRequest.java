package com.insuranceclaim.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

/** Incoming payload for submitting a new claim from the UI. */
public class ClaimRequest {

    private String claimId;

    @NotBlank
    private String customerName;

    @NotBlank
    private String policyNumber;

    @NotBlank
    private String claimType;

    @Positive
    private double claimAmount;

    @NotBlank
    private String incidentDate;

    @NotBlank
    private String description;

    private List<String> documentsUploaded = new ArrayList<>();

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
}
