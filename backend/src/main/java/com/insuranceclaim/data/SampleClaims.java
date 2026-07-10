package com.insuranceclaim.data;

import com.insuranceclaim.model.ClaimRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Ready-made sample claims that exercise each decision path. */
public final class SampleClaims {

    private SampleClaims() {
    }

    public static Map<String, ClaimRequest> all() {
        Map<String, ClaimRequest> samples = new LinkedHashMap<>();
        samples.put("approved", approved());
        samples.put("rejected", rejected());
        samples.put("manual_review", manualReview());
        return samples;
    }

    /** Clean, low-risk, fully documented claim -> APPROVED. */
    public static ClaimRequest approved() {
        ClaimRequest r = new ClaimRequest();
        r.setCustomerName("Aarav Sharma");
        r.setPolicyNumber("POL-1001");
        r.setClaimType("Health");
        r.setClaimAmount(45000);
        r.setIncidentDate("2025-05-12");
        r.setDescription("Hospitalised for two nights due to dengue; standard treatment and tests.");
        r.setDocumentsUploaded(List.of("Hospital Report", "Medical Bills", "ID Proof"));
        return r;
    }

    /** Lapsed policy -> REJECTED by the policy verification node. */
    public static ClaimRequest rejected() {
        ClaimRequest r = new ClaimRequest();
        r.setCustomerName("Kabir Menon");
        r.setPolicyNumber("POL-1005");
        r.setClaimType("Health");
        r.setClaimAmount(60000);
        r.setIncidentDate("2025-06-01");
        r.setDescription("Outpatient surgery and follow up consultations after minor accident.");
        r.setDocumentsUploaded(List.of("Hospital Report", "Medical Bills", "ID Proof"));
        return r;
    }

    /** High-value property claim with missing documents -> MANUAL_REVIEW. */
    public static ClaimRequest manualReview() {
        ClaimRequest r = new ClaimRequest();
        r.setCustomerName("Rohan Verma");
        r.setPolicyNumber("POL-1003");
        r.setClaimType("Property");
        r.setClaimAmount(1800000);
        r.setIncidentDate("2025-06-20");
        r.setDescription("Fire damage to residential property; urgent settlement requested.");
        r.setDocumentsUploaded(List.of("Damage Photos", "ID Proof"));
        return r;
    }
}
