package com.hosteldesk.backend.dto;

import java.math.BigDecimal;

public class AiInferenceResponse {
    private String summary;
    private String category;
    private String priority;
    private String recommendedDepartment;
    private String safetyHazardNote;
    private BigDecimal confidence;
    private Boolean isFallback = false;

    public AiInferenceResponse() {}

    public AiInferenceResponse(String summary, String category, String priority,
                               String recommendedDepartment, String safetyHazardNote,
                               BigDecimal confidence, Boolean isFallback) {
        this.summary = summary;
        this.category = category;
        this.priority = priority;
        this.recommendedDepartment = recommendedDepartment;
        this.safetyHazardNote = safetyHazardNote;
        this.confidence = confidence;
        this.isFallback = isFallback;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getRecommendedDepartment() { return recommendedDepartment; }
    public void setRecommendedDepartment(String recommendedDepartment) { this.recommendedDepartment = recommendedDepartment; }

    public String getSafetyHazardNote() { return safetyHazardNote; }
    public void setSafetyHazardNote(String safetyHazardNote) { this.safetyHazardNote = safetyHazardNote; }

    public BigDecimal getConfidence() { return confidence; }
    public void setConfidence(BigDecimal confidence) { this.confidence = confidence; }

    public Boolean getIsFallback() { return isFallback; }
    public void setIsFallback(Boolean isFallback) { this.isFallback = isFallback; }
}
