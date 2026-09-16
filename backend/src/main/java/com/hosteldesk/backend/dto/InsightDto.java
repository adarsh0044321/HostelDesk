package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.InfrastructureInsight;
import java.time.ZonedDateTime;

public class InsightDto {
    private Long id;
    private Long hostelId;
    private String blockName;
    private String category;
    private Integer complaintCount;
    private Integer timeWindowDays;
    private String patternDescription;
    private String probableCause;
    private String recommendedAction;
    private ZonedDateTime createdAt;

    public InsightDto() {}

    public static InsightDto fromEntity(InfrastructureInsight insight) {
        if (insight == null) return null;
        InsightDto dto = new InsightDto();
        dto.setId(insight.getId());
        if (insight.getHostel() != null) {
            dto.setHostelId(insight.getHostel().getId());
        }
        dto.setBlockName(insight.getBlockName());
        dto.setCategory(insight.getCategory());
        dto.setComplaintCount(insight.getComplaintCount());
        dto.setTimeWindowDays(insight.getTimeWindowDays());
        dto.setPatternDescription(insight.getPatternDescription());
        dto.setProbableCause(insight.getProbableCause());
        dto.setRecommendedAction(insight.getRecommendedAction());
        dto.setCreatedAt(insight.getCreatedAt());
        return dto;
    }

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

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
