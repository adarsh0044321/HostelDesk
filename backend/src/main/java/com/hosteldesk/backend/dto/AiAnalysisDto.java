package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.IssueAiAnalysis;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class AiAnalysisDto {
    private Long id;
    private Long issueId;
    private String detectedCategory;
    private String detectedPriority;
    private String recommendedDepartment;
    private String summary;
    private String safetyHazardNote;
    private BigDecimal confidence;
    private Boolean isFallback;
    private ZonedDateTime analyzedAt;

    public AiAnalysisDto() {}

    public static AiAnalysisDto fromEntity(IssueAiAnalysis analysis) {
        if (analysis == null) return null;
        AiAnalysisDto dto = new AiAnalysisDto();
        dto.setId(analysis.getId());
        dto.setIssueId(analysis.getIssue().getId());
        dto.setDetectedCategory(analysis.getDetectedCategory());
        dto.setDetectedPriority(analysis.getDetectedPriority());
        dto.setRecommendedDepartment(analysis.getRecommendedDepartment());
        dto.setSummary(analysis.getSummary());
        dto.setSafetyHazardNote(analysis.getSafetyHazardNote());
        dto.setConfidence(analysis.getConfidence());
        dto.setIsFallback(analysis.getIsFallback());
        dto.setAnalyzedAt(analysis.getAnalyzedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIssueId() { return issueId; }
    public void setIssueId(Long issueId) { this.issueId = issueId; }

    public String getDetectedCategory() { return detectedCategory; }
    public void setDetectedCategory(String detectedCategory) { this.detectedCategory = detectedCategory; }

    public String getDetectedPriority() { return detectedPriority; }
    public void setDetectedPriority(String detectedPriority) { this.detectedPriority = detectedPriority; }

    public String getRecommendedDepartment() { return recommendedDepartment; }
    public void setRecommendedDepartment(String recommendedDepartment) { this.recommendedDepartment = recommendedDepartment; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getSafetyHazardNote() { return safetyHazardNote; }
    public void setSafetyHazardNote(String safetyHazardNote) { this.safetyHazardNote = safetyHazardNote; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public Boolean getIsFallback() { return isFallback; }
    public void setIsFallback(Boolean isFallback) { this.isFallback = isFallback; }

    public ZonedDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(ZonedDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}
