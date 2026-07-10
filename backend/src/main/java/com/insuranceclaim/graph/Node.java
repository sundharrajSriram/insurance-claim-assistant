package com.insuranceclaim.graph;

import com.insuranceclaim.model.ClaimState;

/**
 * A single agent implemented as a graph node. Every node reads and mutates the
 * shared {@link ClaimState}, exactly like a LangGraph node function that takes
 * the state and returns partial updates.
 */
public interface Node {

    /** Stable node name used for edges, routing and the audit trail. */
    String name();

    /** Execute the agent's logic against the shared state. */
    void apply(ClaimState state);
}
