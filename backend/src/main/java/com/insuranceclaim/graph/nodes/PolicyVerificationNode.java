package com.insuranceclaim.graph.nodes;

import com.insuranceclaim.data.Policy;
import com.insuranceclaim.data.PolicyRepository;
import com.insuranceclaim.graph.Node;
import com.insuranceclaim.graph.NodeNames;
import com.insuranceclaim.model.ClaimState;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Verifies policy eligibility: the policy must exist, be active, have its premium
 * paid, and cover the claimed amount.
 */
@Component
public class PolicyVerificationNode implements Node {

    private final PolicyRepository policyRepository;

    public PolicyVerificationNode(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public String name() {
        return NodeNames.POLICY;
    }

    @Override
    public void apply(ClaimState s) {
        Optional<Policy> maybe = policyRepository.findByNumber(s.getPolicyNumber());
        if (maybe.isEmpty()) {
            s.setPolicyStatus("NOT_FOUND");
            s.setPolicyEligible(false);
            s.setCoverageAmount(0);
            s.setCoverageReason("No policy found for number " + s.getPolicyNumber() + ".");
            s.audit(name(), s.getCoverageReason());
            return;
        }

        Policy policy = maybe.get();
        s.setCoverageAmount(policy.getCoverageLimit());

        if (!policy.isActive()) {
            s.setPolicyStatus("LAPSED");
            s.setPolicyEligible(false);
            s.setCoverageReason("Policy is not active.");
        } else if (!policy.isPremiumPaid()) {
            s.setPolicyStatus("PREMIUM_DUE");
            s.setPolicyEligible(false);
            s.setCoverageReason("Premium payment is outstanding.");
        } else if (s.getClaimAmount() > policy.getCoverageLimit()) {
            s.setPolicyStatus("ACTIVE");
            s.setPolicyEligible(false);
            s.setCoverageReason(String.format(
                    "Claim amount %.2f exceeds coverage limit %.2f.",
                    s.getClaimAmount(), policy.getCoverageLimit()));
        } else {
            s.setPolicyStatus("ACTIVE");
            s.setPolicyEligible(true);
            s.setCoverageReason("Policy active, premium paid and within coverage limit.");
        }
        s.audit(name(), "Policy status " + s.getPolicyStatus()
                + " (eligible=" + s.isPolicyEligible() + ").");
    }
}
