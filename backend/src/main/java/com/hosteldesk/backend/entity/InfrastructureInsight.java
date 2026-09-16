package com.hosteldesk.backend.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "infrastructure_insights")
public class InfrastructureInsight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hostel_id")
    private Hostel hostel;

    @Column(name = "block_name", length = 50)
    private String blockName;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "complaint_count", nullable = false)
    private Integer complaintCount;

    @Column(name = "time_window_days", nullable = false)
    private Integer timeWindowDays;

    @Column(name = "pattern_description", columnDefinition = "TEXT", nullable = false)
    private String patternDescription;

    @Column(name = "probable_cause", columnDefinition = "TEXT", nullable = false)
    private String probableCause;

    @Column(name = "recommended_action", columnDefinition = "TEXT", nullable = false)
    private String recommendedAction;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    public InfrastructureInsight() {}

    public InfrastructureInsight(Long id, Hostel hostel, String blockName, String category,
                                 Integer complaintCount, Integer timeWindowDays, String patternDescription,
                                 String probableCause, String recommendedAction) {
        this.id = id;
        this.hostel = hostel;
        this.blockName = blockName;
        this.category = category;
        this.complaintCount = complaintCount;
        this.timeWindowDays = timeWindowDays;
        this.patternDescription = patternDescription;
        this.probableCause = probableCause;
        this.recommendedAction = recommendedAction;
    }

    public InfrastructureInsight(Long id, Hostel hostel, String blockName, String category,
                                 Integer complaintCount, Integer timeWindowDays, String patternDescription,
                                 String probableCause, String recommendedAction, ZonedDateTime createdAt) {
        this.id = id;
        this.hostel = hostel;
        this.blockName = blockName;
        this.category = category;
        this.complaintCount = complaintCount;
        this.timeWindowDays = timeWindowDays;
        this.patternDescription = patternDescription;
        this.probableCause = probableCause;
        this.recommendedAction = recommendedAction;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Hostel getHostel() { return hostel; }
    public void setHostel(Hostel hostel) { this.hostel = hostel; }

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
