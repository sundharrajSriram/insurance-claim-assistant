package com.insuranceclaim.graph;

import com.insuranceclaim.model.ClaimState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A minimal, dependency-free port of LangGraph's {@code StateGraph} to Java.
 *
 * <p>It supports the same building blocks referenced by the case study:
 * <ul>
 *   <li>nodes ({@link #addNode})</li>
 *   <li>normal edges ({@link #addEdge})</li>
 *   <li>conditional edges / routers ({@link #addConditionalEdges})</li>
 *   <li>an entry point ({@link #setEntryPoint}) and a terminal {@link #END} marker</li>
 * </ul>
 *
 * <p>The graph runs a shared {@link ClaimState} from the entry point, following
 * edges until it reaches {@link #END} or a node that has no outgoing edge (used
 * to pause the workflow for human-in-the-loop review).
 */
public class StateGraph {

    /** Terminal marker; reaching it stops execution. */
    public static final String END = "__END__";

    private final Map<String, Node> nodes = new HashMap<>();
    private final Map<String, String> edges = new HashMap<>();
    private final Map<String, Function<ClaimState, String>> conditionalEdges = new HashMap<>();
    private String entryPoint;

    public StateGraph addNode(Node node) {
        nodes.put(node.name(), node);
        return this;
    }

    public StateGraph addEdge(String from, String to) {
        edges.put(from, to);
        return this;
    }

    /**
     * Register a conditional router. After {@code from} executes, {@code router}
     * inspects the state and returns the name of the next node (or {@link #END}).
     */
    public StateGraph addConditionalEdges(String from, Function<ClaimState, String> router) {
        conditionalEdges.put(from, router);
        return this;
    }

    public StateGraph setEntryPoint(String node) {
        this.entryPoint = node;
        return this;
    }

    /**
     * Execute the graph from {@code start} until reaching {@link #END} or a node
     * with no outgoing edge. Returns the name of the last node executed, which
     * lets callers detect a human-review pause.
     */
    public String run(ClaimState state, String start) {
        String current = start;
        while (current != null && !END.equals(current)) {
            Node node = nodes.get(current);
            if (node == null) {
                throw new IllegalStateException("Unknown node: " + current);
            }
            state.setCurrentNode(current);
            node.apply(state);
            state.getNodePath().add(current);

            String next;
            if (conditionalEdges.containsKey(current)) {
                next = conditionalEdges.get(current).apply(state);
            } else {
                next = edges.get(current);
            }
            if (next == null) {
                // No outgoing edge: pause here (e.g. awaiting human review).
                return current;
            }
            current = next;
        }
        state.setCurrentNode(END);
        return END;
    }

    /** Run from the configured entry point. */
    public String run(ClaimState state) {
        if (entryPoint == null) {
            throw new IllegalStateException("Entry point not set");
        }
        return run(state, entryPoint);
    }
}
