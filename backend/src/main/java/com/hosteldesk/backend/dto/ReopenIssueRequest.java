package com.hosteldesk.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ReopenIssueRequest {
    @NotBlank(message = "Reopen reason/explanation is required")
    private String reason;

    public ReopenIssueRequest() {}

    public ReopenIssueRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
