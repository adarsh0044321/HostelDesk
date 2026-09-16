package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class UpdateProgressRequest {
    @SerializedName("note")
    private String note;

    public UpdateProgressRequest() {}

    public UpdateProgressRequest(String note) {
        this.note = note;
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
