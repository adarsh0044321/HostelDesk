package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class VerifyResolutionRequest {
    @SerializedName("satisfactionNote")
    private String satisfactionNote;

    public VerifyResolutionRequest() {}

    public VerifyResolutionRequest(String satisfactionNote) {
        this.satisfactionNote = satisfactionNote;
    }

    public String getSatisfactionNote() { return satisfactionNote; }
    public void setSatisfactionNote(String satisfactionNote) { this.satisfactionNote = satisfactionNote; }
}

