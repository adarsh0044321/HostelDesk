package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.Announcement;
import java.time.ZonedDateTime;

public class AnnouncementDto {
    private Long id;
    private Long instituteId;
    private Long hostelId;
    private String hostelName;
    private Long authorId;
    private String authorName;
    private String authorRole;
    private String title;
    private String content;
    private Boolean pinned;
    private ZonedDateTime expiresAt;
    private ZonedDateTime createdAt;
    private boolean active;
    private String targetScope; // "INSTITUTE_WIDE" or "HOSTEL_SPECIFIC"

    public AnnouncementDto() {}

    public static AnnouncementDto fromEntity(Announcement entity) {
        if (entity == null) return null;
        AnnouncementDto dto = new AnnouncementDto();
        dto.setId(entity.getId());
        if (entity.getInstitute() != null) {
            dto.setInstituteId(entity.getInstitute().getId());
        }
        if (entity.getHostel() != null) {
            dto.setHostelId(entity.getHostel().getId());
            dto.setHostelName(entity.getHostel().getName());
            dto.setTargetScope("HOSTEL_SPECIFIC");
        } else {
            dto.setTargetScope("INSTITUTE_WIDE");
        }
        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
        }
        dto.setAuthorName(entity.getAuthorName());
        dto.setAuthorRole(entity.getAuthorRole());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setPinned(entity.getPinned() != null ? entity.getPinned() : false);
        dto.setExpiresAt(entity.getExpiresAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setActive(entity.getExpiresAt() == null || entity.getExpiresAt().isAfter(ZonedDateTime.now()));
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInstituteId() { return instituteId; }
    public void setInstituteId(Long instituteId) { this.instituteId = instituteId; }

    public Long getHostelId() { return hostelId; }
    public void setHostelId(Long hostelId) { this.hostelId = hostelId; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorRole() { return authorRole; }
    public void setAuthorRole(String authorRole) { this.authorRole = authorRole; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getPinned() { return pinned; }
    public void setPinned(Boolean pinned) { this.pinned = pinned; }

    public ZonedDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(ZonedDateTime expiresAt) { this.expiresAt = expiresAt; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getTargetScope() { return targetScope; }
    public void setTargetScope(String targetScope) { this.targetScope = targetScope; }
}
