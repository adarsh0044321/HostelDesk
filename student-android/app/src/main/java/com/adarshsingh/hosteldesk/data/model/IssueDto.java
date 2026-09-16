package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class IssueDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("ticketNumber")
    private String ticketNumber;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("priority")
    private String priority;

    @SerializedName("status")
    private String status;

    @SerializedName("blockName")
    private String blockName;

    @SerializedName("roomNumber")
    private String roomNumber;

    @SerializedName("hostelId")
    private Long hostelId;

    @SerializedName("hostelName")
    private String hostelName;

    @SerializedName("reportedById")
    private Long reportedById;

    @SerializedName("reportedByName")
    private String reportedByName;

    @SerializedName("assignedDepartmentId")
    private Long assignedDepartmentId;

    @SerializedName("assignedDepartmentName")
    private String assignedDepartmentName;

    @SerializedName("assignedStaffId")
    private Long assignedStaffId;

    @SerializedName("assignedStaffName")
    private String assignedStaffName;

    @SerializedName("technicianNotes")
    private String technicianNotes;

    @SerializedName("firstAttachmentUrl")
    private String firstAttachmentUrl;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("resolvedAt")
    private String resolvedAt;

    @SerializedName("verifiedAt")
    private String verifiedAt;

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

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(String resolvedAt) { this.resolvedAt = resolvedAt; }

    @SerializedName("rating")
    private Integer rating;

    @SerializedName("workerReview")
    private String workerReview;

    public String getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(String verifiedAt) { this.verifiedAt = verifiedAt; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getWorkerReview() { return workerReview; }
    public void setWorkerReview(String workerReview) { this.workerReview = workerReview; }
}

