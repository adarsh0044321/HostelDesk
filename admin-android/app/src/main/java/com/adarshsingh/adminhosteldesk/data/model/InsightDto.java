package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class InsightDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("hostelId")
    private Long hostelId;

    @SerializedName("blockName")
    private String blockName;

    @SerializedName("category")
    private String category;

    @SerializedName("complaintCount")
    private Integer complaintCount;

    @SerializedName("timeWindowDays")
    private Integer timeWindowDays;

    @SerializedName("patternDescription")
    private String patternDescription;

    @SerializedName("probableCause")
    private String probableCause;

    @SerializedName("recommendedAction")
    private String recommendedAction;

    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHostelId() { return hostelId; }
    public void setHostelId(Long hostelId) { this.hostelId = hostelId; }

    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getComplaintCount() { return complaintCount; }
    public void setComplaintCount(Integer complaintCount) { this.complaintCount = complaintCount; }

    public Integer getTimeWindowDays() { return timeWindowDays; }
    public void setTimeWindowDays(Integer timeWindowDays) { this.timeWindowDays = timeWindowDays; }

    public String getPatternDescription() { return patternDescription; }
    public void setPatternDescription(String patternDescription) { this.patternDescription = patternDescription; }

    public String getProbableCause() { return probableCause; }
    public void setProbableCause(String probableCause) { this.probableCause = probableCause; }

    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
