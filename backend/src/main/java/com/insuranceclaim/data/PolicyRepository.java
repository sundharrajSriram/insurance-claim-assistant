package com.insuranceclaim.data;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory catalogue of sample policies. Mirrors an external policy master that
 * the Policy Verification node would normally call. Includes active, lapsed and
 * unpaid-premium policies so all decision paths can be demonstrated.
 */
@Repository
public class PolicyRepository {

    private final Map<String, Policy> policies = new LinkedHashMap<>();

    @PostConstruct
    public void seed() {
        add(new Policy("POL-1001", "Aarav Sharma", "Health", true, true, 500000));
        add(new Policy("POL-1002", "Diya Patel", "Motor", true, true, 300000));
        add(new Policy("POL-1003", "Rohan Verma", "Property", true, true, 2000000));
        add(new Policy("POL-1004", "Isha Nair", "Travel", true, true, 150000));
        add(new Policy("POL-1005", "Kabir Menon", "Health", false, true, 400000));   // lapsed
        add(new Policy("POL-1006", "Ananya Rao", "Motor", true, false, 250000));      // premium unpaid
        add(new Policy("POL-1007", "Vivaan Gupta", "Life", true, true, 5000000));
    }

    private void add(Policy p) {
        policies.put(p.getPolicyNumber().toUpperCase(), p);
    }

    public Optional<Policy> findByNumber(String policyNumber) {
        if (policyNumber == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(policies.get(policyNumber.trim().toUpperCase()));
    }

    public java.util.List<Policy> findAll() {
        return new java.util.ArrayList<>(policies.values());
    }
}
