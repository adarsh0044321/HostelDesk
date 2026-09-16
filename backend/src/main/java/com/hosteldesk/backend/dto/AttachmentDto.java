package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.IssueAttachment;
import java.time.ZonedDateTime;

public class AttachmentDto {
    private Long id;
    private Long issueId;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String attachmentType;
    private ZonedDateTime createdAt;

    public AttachmentDto() {}

    public static AttachmentDto fromEntity(IssueAttachment attachment) {
        if (attachment == null) return null;
        AttachmentDto dto = new AttachmentDto();
        dto.setId(attachment.getId());
        dto.setIssueId(attachment.getIssue().getId());
        dto.setFileUrl(attachment.getFileUrl());
        dto.setFileName(attachment.getFileName());
        dto.setFileType(attachment.getFileType());
        dto.setFileSize(attachment.getFileSize());
        dto.setAttachmentType(attachment.getAttachmentType());
        dto.setCreatedAt(attachment.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
