package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class AssignWardenRequest {
    @SerializedName("wardenId")
    private Long wardenId;

    public AssignWardenRequest() {}

    public AssignWardenRequest(Long wardenId) {
        this.wardenId = wardenId;
    }

    public Long getWardenId() {
        return wardenId;
    }

    public void setWardenId(Long wardenId) {
        this.wardenId = wardenId;
    }
}
