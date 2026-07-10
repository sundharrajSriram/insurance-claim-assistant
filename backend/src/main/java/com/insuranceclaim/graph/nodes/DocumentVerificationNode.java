package com.insuranceclaim.graph.nodes;

import com.insuranceclaim.graph.Node;
import com.insuranceclaim.graph.NodeNames;
import com.insuranceclaim.model.ClaimState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Checks that the documents required for the given claim type were uploaded and
 * records any that are missing.
 */
@Component
public class DocumentVerificationNode implements Node {

    private static final Map<String, List<String>> REQUIRED = Map.of(
            "health", List.of("Hospital Report", "Medical Bills", "ID Proof"),
            "motor", List.of("Repair Bill", "Police FIR", "Damage Photos", "ID Proof"),
            "property", List.of("Damage Photos", "Repair Estimate", "Ownership Proof", "ID Proof"),
            "travel", List.of("Booking Proof", "Receipts", "ID Proof"),
            "life", List.of("Death Certificate", "Nominee ID", "Policy Document")
    );

    @Override
    public String name() {
        return NodeNames.DOCUMENT;
    }

    @Override
    public void apply(ClaimState s) {
        String type = s.getClaimType() == null ? "" : s.getClaimType().toLowerCase(Locale.ROOT);
        List<String> required = REQUIRED.getOrDefault(type, List.of("ID Proof"));

        List<String> uploadedLower = new ArrayList<>();
        for (String d : s.getDocumentsUploaded()) {
            uploadedLower.add(d.trim().toLowerCase(Locale.ROOT));
        }

        List<String> missing = new ArrayList<>();
        for (String req : required) {
            if (!uploadedLower.contains(req.toLowerCase(Locale.ROOT))) {
                missing.add(req);
            }
        }

        s.setMissingDocuments(missing);
        if (missing.isEmpty()) {
            s.setDocumentStatus("COMPLETE");
            s.audit(name(), "All required documents provided.");
        } else {
            s.setDocumentStatus("INCOMPLETE");
            s.audit(name(), "Missing documents: " + String.join(", ", missing) + ".");
        }
    }
}
