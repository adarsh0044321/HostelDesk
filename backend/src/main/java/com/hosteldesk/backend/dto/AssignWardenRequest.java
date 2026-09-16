package com.hosteldesk.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;

public class AssignWardenRequest {
    @NotNull(message = "Warden ID is required")
    @JsonAlias({"warden_id", "userId", "user_id"})
    private Long wardenId;

    public AssignWardenRequest() {}
    public AssignWardenRequest(Long wardenId) { this.wardenId = wardenId; }

    public Long getWardenId() { return wardenId; }
    public void setWardenId(Long wardenId) { this.wardenId = wardenId; }
}
