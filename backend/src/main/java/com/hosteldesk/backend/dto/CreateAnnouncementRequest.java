package com.hosteldesk.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAnnouncementRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private Long hostelId; // null = all hostels in institute

    private Integer durationHours; // null or 0 = permanent / no expiry

    private Boolean pinned = false;

    public CreateAnnouncementRequest() {}

    public CreateAnnouncementRequest(String title, String content, Long hostelId, Integer durationHours, Boolean pinned) {
        this.title = title;
        this.content = content;
        this.hostelId = hostelId;
        this.durationHours = durationHours;
        this.pinned = pinned;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getHostelId() { return hostelId; }
    public void setHostelId(Long hostelId) { this.hostelId = hostelId; }

    public Integer getDurationHours() { return durationHours; }
    public void setDurationHours(Integer durationHours) { this.durationHours = durationHours; }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }
}
