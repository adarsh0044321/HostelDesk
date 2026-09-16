package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class AttachmentDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("fileType")
    private String fileType;

    @SerializedName("attachmentType")
    private String attachmentType;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }
}
