package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AiAnalysisDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("predictedCategory")
    private String predictedCategory;

    @SerializedName("predictedPriority")
    private String predictedPriority;

    @SerializedName("confidence")
    private Double confidence;

    @SerializedName("summary")
    private String summary;

    @SerializedName("urgencyReason")
    private String urgencyReason;

    @SerializedName("clusterTag")
    private String clusterTag;

    @SerializedName("suggestedActions")
    private List<String> suggestedActions;

    @SerializedName("isFallback")
    private Boolean isFallback;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPredictedCategory() { return predictedCategory; }
    public void setPredictedCategory(String predictedCategory) { this.predictedCategory = predictedCategory; }

    public String getPredictedPriority() { return predictedPriority; }
    public void setPredictedPriority(String predictedPriority) { this.predictedPriority = predictedPriority; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getUrgencyReason() { return urgencyReason; }
    public void setUrgencyReason(String urgencyReason) { this.urgencyReason = urgencyReason; }

    public String getClusterTag() { return clusterTag; }
    public void setClusterTag(String clusterTag) { this.clusterTag = clusterTag; }

    public List<String> getSuggestedActions() { return suggestedActions; }
    public void setSuggestedActions(List<String> suggestedActions) { this.suggestedActions = suggestedActions; }

    public Boolean getIsFallback() { return isFallback; }
    public void setIsFallback(Boolean fallback) { isFallback = fallback; }
}
