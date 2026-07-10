package com.insuranceclaim.model;

import java.time.Instant;

/**
 * A single node-level trace record used to build the explainable audit trail
 * required by the case study (node name, message, timestamp).
 */
public class AuditEntry {
    private String node;
    private String message;
    private Instant timestamp;

    public AuditEntry() {
    }

    public AuditEntry(String node, String message) {
        this.node = node;
        this.message = message;
        this.timestamp = Instant.now();
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
