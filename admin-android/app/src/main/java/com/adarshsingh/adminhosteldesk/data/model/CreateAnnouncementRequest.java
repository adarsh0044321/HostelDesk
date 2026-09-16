package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateAnnouncementRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("hostelId")
    private Long hostelId;

    @SerializedName("expiresInHours")
    private Integer expiresInHours;

    @SerializedName("pinned")
    private Boolean pinned = false;

    public CreateAnnouncementRequest() {}

    public CreateAnnouncementRequest(String title, String content, Long hostelId, Integer expiresInHours, Boolean pinned) {
        this.title = title;
        this.content = content;
        this.hostelId = hostelId;
        this.expiresInHours = expiresInHours;
        this.pinned = pinned;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getHostelId() { return hostelId; }
    public void setHostelId(Long hostelId) { this.hostelId = hostelId; }

    public Integer getExpiresInHours() { return expiresInHours; }
    public void setExpiresInHours(Integer expiresInHours) { this.expiresInHours = expiresInHours; }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }
}
