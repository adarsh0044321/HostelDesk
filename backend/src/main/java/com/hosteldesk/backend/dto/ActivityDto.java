package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.IssueActivity;
import java.time.ZonedDateTime;

public class ActivityDto {
    private Long id;
    private Long issueId;
    private Long performedById;
    private String performedByName;
    private Long actorId;
    private String actorName;
    private String actorRole;
    private String action;
    private String message;
    private String notes;
    private ZonedDateTime createdAt;

    public ActivityDto() {}

    public static ActivityDto fromEntity(IssueActivity activity) {
        if (activity == null) return null;
        ActivityDto dto = new ActivityDto();
        dto.setId(activity.getId());
        dto.setIssueId(activity.getIssue() != null ? activity.getIssue().getId() : null);
        if (activity.getPerformedBy() != null) {
            dto.setPerformedById(activity.getPerformedBy().getId());
            dto.setPerformedByName(activity.getPerformedBy().getFullName());
            dto.setActorId(activity.getPerformedBy().getId());
            dto.setActorName(activity.getPerformedBy().getFullName());
            dto.setActorRole(activity.getPerformedBy().getRole() != null ? activity.getPerformedBy().getRole().name() : "STAFF");
        } else {
            dto.setPerformedByName("Automated System");
            dto.setActorName("Automated System");
            dto.setActorRole("SYSTEM");
        }
        dto.setAction(activity.getAction());
        dto.setMessage(activity.getMessage());
        dto.setNotes(activity.getMessage());
        dto.setCreatedAt(activity.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }

    public Long getPerformedById() { return performedById; }
    public void setPerformedById(Long performedById) { this.performedById = performedById; }

    public String getPerformedByName() { return performedByName; }
    public void setPerformedByName(String performedByName) { this.performedByName = performedByName; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }

    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
