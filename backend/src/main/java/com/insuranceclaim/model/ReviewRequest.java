package com.insuranceclaim.model;

import jakarta.validation.constraints.NotNull;

/** Reviewer decision recorded during human-in-the-loop review. */
public class ReviewRequest {

    /** Must be APPROVED or REJECTED. */
    @NotNull
    private Decision decision;

    private String comments;

    private String reviewerName;

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
}
