package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class VerifyResolutionRequest {
    @SerializedName("satisfactionNote")
    private String satisfactionNote;

    @SerializedName("rating")
    private Integer rating;

    @SerializedName("workerReview")
    private String workerReview;

    public VerifyResolutionRequest() {}

    public VerifyResolutionRequest(String satisfactionNote) {
        this.satisfactionNote = satisfactionNote;
    }

    public VerifyResolutionRequest(String satisfactionNote, Integer rating, String workerReview) {
        this.satisfactionNote = satisfactionNote;
        this.rating = rating;
        this.workerReview = workerReview;
    }

    public String getSatisfactionNote() { return satisfactionNote; }
    public void setSatisfactionNote(String satisfactionNote) { this.satisfactionNote = satisfactionNote; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getWorkerReview() { return workerReview; }
    public void setWorkerReview(String workerReview) { this.workerReview = workerReview; }
}

