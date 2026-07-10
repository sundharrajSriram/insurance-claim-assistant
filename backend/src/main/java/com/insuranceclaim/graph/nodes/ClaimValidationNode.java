package com.insuranceclaim.graph.nodes;

import com.insuranceclaim.graph.Node;
import com.insuranceclaim.graph.NodeNames;
import com.insuranceclaim.model.ClaimState;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates the raw claim request: mandatory fields, a sane incident date and a
 * positive claim amount for a known claim type.
 */
@Component
public class ClaimValidationNode implements Node {

    private static final List<String> KNOWN_TYPES =
            List.of("Health", "Motor", "Property", "Travel", "Life");

    @Override
    public String name() {
        return NodeNames.VALIDATION;
    }

    @Override
    public void apply(ClaimState s) {
        List<String> problems = new ArrayList<>();

        if (isBlank(s.getCustomerName())) {
            problems.add("customer name is missing");
        }
        if (isBlank(s.getPolicyNumber())) {
            problems.add("policy number is missing");
        }
        if (isBlank(s.getClaimType())) {
            problems.add("claim type is missing");
        } else if (KNOWN_TYPES.stream().noneMatch(t -> t.equalsIgnoreCase(s.getClaimType()))) {
            problems.add("unsupported claim type '" + s.getClaimType() + "'");
        }
        if (s.getClaimAmount() <= 0) {
            problems.add("claim amount must be greater than zero");
        }
        if (isBlank(s.getDescription()) || s.getDescription().trim().length() < 10) {
            problems.add("description is too short");
        }
        validateIncidentDate(s, problems);

        if (problems.isEmpty()) {
            s.setValidationStatus("VALID");
            s.setValidationReason("All mandatory fields present and consistent.");
            s.audit(name(), "Claim passed validation checks.");
        } else {
            s.setValidationStatus("INVALID");
            s.setValidationReason(String.join("; ", problems));
            s.audit(name(), "Validation failed: " + s.getValidationReason());
        }
    }

    private void validateIncidentDate(ClaimState s, List<String> problems) {
        if (isBlank(s.getIncidentDate())) {
            problems.add("incident date is missing");
            return;
        }
        try {
            LocalDate incident = LocalDate.parse(s.getIncidentDate().trim());
            if (incident.isAfter(LocalDate.now())) {
                problems.add("incident date is in the future");
            }
        } catch (DateTimeParseException e) {
            problems.add("incident date is not a valid ISO date (yyyy-MM-dd)");
        }
    }

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}
