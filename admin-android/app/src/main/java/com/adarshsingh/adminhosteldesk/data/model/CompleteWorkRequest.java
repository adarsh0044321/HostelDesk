package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class CompleteWorkRequest {
    @SerializedName("technicianNote")
    private String technicianNote;

    public CompleteWorkRequest() {}

    public CompleteWorkRequest(String technicianNote) {
        this.technicianNote = technicianNote;
    }

    public String getTechnicianNote() { return technicianNote; }
    public void setTechnicianNote(String technicianNote) { this.technicianNote = technicianNote; }
}
