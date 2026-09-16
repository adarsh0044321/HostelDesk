package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.Issue;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IssueDetailDto extends IssueDto {
    private String resolutionNotes;
    private String reopenReason;
    private AiAnalysisDto aiAnalysis;
    private List<AttachmentDto> attachments = new ArrayList<>();
    private List<ActivityDto> activities = new ArrayList<>();

    public IssueDetailDto() {}

    public static IssueDetailDto fromEntity(Issue issue) {
        if (issue == null) return null;
        IssueDto base = IssueDto.fromEntity(issue);
        IssueDetailDto dto = new IssueDetailDto();
        dto.setId(base.getId());
        dto.setTicketNumber(base.getTicketNumber());
        dto.setTitle(base.getTitle());
        dto.setDescription(base.getDescription());
        dto.setCategory(base.getCategory());
        dto.setPriority(base.getPriority());
        dto.setStatus(base.getStatus());
        dto.setBlockName(base.getBlockName());
        dto.setRoomNumber(base.getRoomNumber());
        dto.setHostelId(base.getHostelId());
        dto.setHostelName(base.getHostelName());
        dto.setReportedById(base.getReportedById());
        dto.setReportedByName(base.getReportedByName());
        dto.setAssignedDepartmentId(base.getAssignedDepartmentId());
        dto.setAssignedDepartmentName(base.getAssignedDepartmentName());
        dto.setAssignedStaffId(base.getAssignedStaffId());
        dto.setAssignedStaffName(base.getAssignedStaffName());
        dto.setTechnicianNotes(base.getTechnicianNotes());
        dto.setCreatedAt(base.getCreatedAt());
        dto.setUpdatedAt(base.getUpdatedAt());
        dto.setResolvedAt(base.getResolvedAt());
        dto.setVerifiedAt(base.getVerifiedAt());
        dto.setWardenViewCount(base.getWardenViewCount());
        dto.setWardenViewedAt(base.getWardenViewedAt());

        dto.setResolutionNotes(issue.getResolutionNotes());
        dto.setReopenReason(issue.getReopenReason());
        dto.setRating(issue.getRating());
        dto.setWorkerReview(issue.getWorkerReview());

        if (issue.getAiAnalysis() != null) {
            dto.setAiAnalysis(AiAnalysisDto.fromEntity(issue.getAiAnalysis()));
        }

        if (issue.getAttachments() != null) {
            dto.setAttachments(issue.getAttachments().stream()
                    .map(AttachmentDto::fromEntity)
                    .collect(Collectors.toList()));
            if (!issue.getAttachments().isEmpty()) {
                dto.setFirstAttachmentUrl(issue.getAttachments().get(0).getFileUrl());
            }
        }

        if (issue.getActivities() != null) {
            dto.setActivities(issue.getActivities().stream()
                    .map(ActivityDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getReopenReason() { return reopenReason; }
    public void setReopenReason(String reopenReason) { this.reopenReason = reopenReason; }

    public AiAnalysisDto getAiAnalysis() { return aiAnalysis; }
    public void setAiAnalysis(AiAnalysisDto aiAnalysis) { this.aiAnalysis = aiAnalysis; }

    public List<AttachmentDto> getAttachments() { return attachments; }
    public void setAttachments(List<AttachmentDto> attachments) { this.attachments = attachments; }

    public List<ActivityDto> getActivities() { return activities; }
    public void setActivities(List<ActivityDto> activities) { this.activities = activities; }
}
