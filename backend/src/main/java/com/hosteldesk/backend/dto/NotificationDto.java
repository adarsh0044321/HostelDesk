package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.Notification;
import java.time.ZonedDateTime;

public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private String type;
    private Long relatedIssueId;
    private String relatedTicketNumber;
    private Boolean isRead;
    private ZonedDateTime createdAt;

    public NotificationDto() {}

    public static NotificationDto fromEntity(Notification notification) {
        if (notification == null) return null;
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());
        if (notification.getRelatedIssue() != null) {
            dto.setRelatedIssueId(notification.getRelatedIssue().getId());
            dto.setRelatedTicketNumber(notification.getRelatedIssue().getTicketNumber());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getRelatedIssueId() { return relatedIssueId; }
    public void setRelatedIssueId(Long relatedIssueId) { this.relatedIssueId = relatedIssueId; }

    public String getRelatedTicketNumber() { return relatedTicketNumber; }
    public void setRelatedTicketNumber(String relatedTicketNumber) { this.relatedTicketNumber = relatedTicketNumber; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
