package com.insuranceclaim.controller;

import com.insuranceclaim.data.Policy;
import com.insuranceclaim.data.PolicyRepository;
import com.insuranceclaim.data.SampleClaims;
import com.insuranceclaim.graph.NodeNames;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reference data for the UI: policies, claim types, sample claims and the graph. */
@RestController
@RequestMapping("/api")
public class MetadataController {

    private final PolicyRepository policyRepository;

    public MetadataController(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @GetMapping("/policies")
    public List<Policy> policies() {
        return policyRepository.findAll();
    }

    @GetMapping("/claim-types")
    public List<String> claimTypes() {
        return List.of("Health", "Motor", "Property", "Travel", "Life");
    }

    @GetMapping("/samples")
    public Map<String, ?> samples() {
        return SampleClaims.all();
    }

    /** Static description of the workflow graph, used for the UI visualization. */
    @GetMapping("/workflow/graph")
    public Map<String, Object> graph() {
        List<Map<String, String>> nodes = List.of(
                node(NodeNames.VALIDATION, "Claim Validation"),
                node(NodeNames.POLICY, "Policy Verification"),
                node(NodeNames.DOCUMENT, "Document Verification"),
                node(NodeNames.FRAUD, "Fraud Detection"),
                node(NodeNames.RISK, "Risk Assessment"),
                node(NodeNames.RECOMMENDATION, "Recommendation"),
                node(NodeNames.HUMAN_REVIEW, "Human Review (HITL)"),
                node(NodeNames.FINALIZE, "Final Decision")
        );
        List<Map<String, String>> edges = List.of(
                edge(NodeNames.VALIDATION, NodeNames.POLICY, ""),
                edge(NodeNames.POLICY, NodeNames.DOCUMENT, ""),
                edge(NodeNames.DOCUMENT, NodeNames.FRAUD, ""),
                edge(NodeNames.FRAUD, NodeNames.RISK, ""),
                edge(NodeNames.RISK, NodeNames.RECOMMENDATION, ""),
                edge(NodeNames.RECOMMENDATION, NodeNames.FINALIZE, "approve / reject"),
                edge(NodeNames.RECOMMENDATION, NodeNames.HUMAN_REVIEW, "manual review"),
                edge(NodeNames.HUMAN_REVIEW, NodeNames.FINALIZE, "reviewed")
        );
        Map<String, Object> g = new LinkedHashMap<>();
        g.put("nodes", nodes);
        g.put("edges", edges);
        return g;
    }

    private Map<String, String> node(String id, String label) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("label", label);
        return m;
    }

    private Map<String, String> edge(String from, String to, String label) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("from", from);
        m.put("to", to);
        m.put("label", label);
        return m;
    }
}
