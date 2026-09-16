package com.hosteldesk.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProgressRequest {
    @NotBlank(message = "Progress note cannot be empty")
    private String note;

    public UpdateProgressRequest() {}

    public UpdateProgressRequest(String note) {
        this.note = note;
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
