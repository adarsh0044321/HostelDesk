package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class ReopenIssueRequest {
    @SerializedName("reason")
    private String reason;

    public ReopenIssueRequest() {}

    public ReopenIssueRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

