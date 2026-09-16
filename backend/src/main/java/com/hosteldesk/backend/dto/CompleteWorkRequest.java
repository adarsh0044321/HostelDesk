package com.hosteldesk.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CompleteWorkRequest {
    @NotBlank(message = "Technician completion note is required")
    private String technicianNote;

    public CompleteWorkRequest() {}

    public CompleteWorkRequest(String technicianNote) {
        this.technicianNote = technicianNote;
    }

    public String getTechnicianNote() { return technicianNote; }
    public void setTechnicianNote(String technicianNote) { this.technicianNote = technicianNote; }
}
