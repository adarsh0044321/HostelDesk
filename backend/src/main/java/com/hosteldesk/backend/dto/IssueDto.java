package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.Issue;
import com.hosteldesk.backend.entity.IssuePriority;
import com.hosteldesk.backend.entity.IssueStatus;
import java.time.ZonedDateTime;

public class IssueDto {
    private Long id;
    private String ticketNumber;
    private String title;
    private String description;
    private String category;
    private IssuePriority priority;
    private IssueStatus status;
    private String blockName;
    private String roomNumber;
    private Long hostelId;
    private String hostelName;
    private Long reportedById;
    private String reportedByName;
    private Long assignedDepartmentId;
    private String assignedDepartmentName;
    private Long assignedStaffId;
    private String assignedStaffName;
    private String technicianNotes;
    private String firstAttachmentUrl;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime resolvedAt;
    private ZonedDateTime verifiedAt;
    private Integer rating;
    private String workerReview;
    private Integer wardenViewCount = 0;
    private ZonedDateTime wardenViewedAt;

    public IssueDto() {}

    public static IssueDto fromEntity(Issue issue) {
        if (issue == null) return null;
        IssueDto dto = new IssueDto();
        dto.setId(issue.getId());
        dto.setTicketNumber(issue.getTicketNumber());
        dto.setTitle(issue.getTitle());
        String cat = issue.getCategory();
        if (issue.getAssignedDepartment() != null) {
            cat = issue.getAssignedDepartment().getName();
        }
        dto.setCategory(cat != null ? cat : "GENERAL");
        dto.setPriority(issue.getPriority());
        dto.setStatus(issue.getStatus());
        dto.setBlockName(issue.getBlockName());
        dto.setRoomNumber(issue.getRoomNumber());
        dto.setTechnicianNotes(issue.getTechnicianNotes());
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setUpdatedAt(issue.getUpdatedAt());
        dto.setResolvedAt(issue.getResolvedAt());
        dto.setVerifiedAt(issue.getVerifiedAt());
        dto.setRating(issue.getRating());
        dto.setWorkerReview(issue.getWorkerReview());
        dto.setWardenViewCount(issue.getWardenViewCount());
        dto.setWardenViewedAt(issue.getWardenViewedAt());

        if (issue.getHostel() != null) {
            dto.setHostelId(issue.getHostel().getId());
            dto.setHostelName(issue.getHostel().getName());
        }
        if (issue.getReportedBy() != null) {
            dto.setReportedById(issue.getReportedBy().getId());
            dto.setReportedByName(issue.getReportedBy().getFullName());
        }
        if (issue.getAssignedDepartment() != null) {
            dto.setAssignedDepartmentId(issue.getAssignedDepartment().getId());
            dto.setAssignedDepartmentName(issue.getAssignedDepartment().getName());
        }
        if (issue.getAssignedStaff() != null) {
            dto.setAssignedStaffId(issue.getAssignedStaff().getId());
            dto.setAssignedStaffName(issue.getAssignedStaff().getFullName());
        }
        if (issue.getAttachments() != null && !issue.getAttachments().isEmpty()) {
            dto.setFirstAttachmentUrl(issue.getAttachments().get(0).getFileUrl());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public IssuePriority getPriority() { return priority; }
    public void setPriority(IssuePriority priority) { this.priority = priority; }

    public IssueStatus getStatus() { return status; }
    public void setStatus(IssueStatus status) { this.status = status; }

    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Long getHostelId() { return hostelId; }
    public void setHostelId(Long hostelId) { this.hostelId = hostelId; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public Long getReportedById() { return reportedById; }
    public void setReportedById(Long reportedById) { this.reportedById = reportedById; }

    public String getReportedByName() { return reportedByName; }
    public void setReportedByName(String reportedByName) { this.reportedByName = reportedByName; }

    public Long getAssignedDepartmentId() { return assignedDepartmentId; }
    public void setAssignedDepartmentId(Long assignedDepartmentId) { this.assignedDepartmentId = assignedDepartmentId; }

    public String getAssignedDepartmentName() { return assignedDepartmentName; }
    public void setAssignedDepartmentName(String assignedDepartmentName) { this.assignedDepartmentName = assignedDepartmentName; }

    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getAssignedStaffName() { return assignedStaffName; }
    public void setAssignedStaffName(String assignedStaffName) { this.assignedStaffName = assignedStaffName; }

    public String getTechnicianNotes() { return technicianNotes; }
    public void setTechnicianNotes(String technicianNotes) { this.technicianNotes = technicianNotes; }

    public String getFirstAttachmentUrl() { return firstAttachmentUrl; }
    public void setFirstAttachmentUrl(String firstAttachmentUrl) { this.firstAttachmentUrl = firstAttachmentUrl; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public ZonedDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(ZonedDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public ZonedDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(ZonedDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getWorkerReview() { return workerReview; }
    public void setWorkerReview(String workerReview) { this.workerReview = workerReview; }

    public Integer getWardenViewCount() { return wardenViewCount != null ? wardenViewCount : 0; }
    public void setWardenViewCount(Integer wardenViewCount) { this.wardenViewCount = wardenViewCount; }

    public ZonedDateTime getWardenViewedAt() { return wardenViewedAt; }
    public void setWardenViewedAt(ZonedDateTime wardenViewedAt) { this.wardenViewedAt = wardenViewedAt; }
}
