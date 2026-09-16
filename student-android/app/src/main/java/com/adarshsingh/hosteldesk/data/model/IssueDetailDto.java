package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class IssueDetailDto extends IssueDto {
    @SerializedName("resolutionNotes")
    private String resolutionNotes;

    @SerializedName("reopenReason")
    private String reopenReason;

    @SerializedName("aiAnalysis")
    private AiAnalysisDto aiAnalysis;

    @SerializedName("attachments")
    private List<AttachmentDto> attachments = new ArrayList<>();

    @SerializedName("activities")
    private List<ActivityDto> activities = new ArrayList<>();

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
