package com.hosteldesk.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "issue_ai_analysis")
public class IssueAiAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false, unique = true)
    private Issue issue;

    @Column(name = "detected_category", nullable = false, length = 50)
    private String detectedCategory;

    @Column(name = "detected_priority", nullable = false, length = 20)
    private String detectedPriority;

    @Column(name = "recommended_department", nullable = false, length = 50)
    private String recommendedDepartment;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(name = "safety_hazard_note", columnDefinition = "TEXT")
    private String safetyHazardNote;

    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal confidence;

    @Column(name = "is_fallback")
    private Boolean isFallback = false;

    @Column(name = "analyzed_at", nullable = false, updatable = false)
    private ZonedDateTime analyzedAt = ZonedDateTime.now();

    public IssueAiAnalysis() {}

    public IssueAiAnalysis(Long id, Issue issue, String detectedCategory, String detectedPriority,
                           String recommendedDepartment, String summary, String safetyHazardNote,
                           BigDecimal confidence, Boolean isFallback) {
        this.id = id;
        this.issue = issue;
        this.detectedCategory = detectedCategory;
        this.detectedPriority = detectedPriority;
        this.recommendedDepartment = recommendedDepartment;
        this.summary = summary;
        this.safetyHazardNote = safetyHazardNote;
        this.confidence = confidence;
        this.isFallback = isFallback;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Issue getIssue() { return issue; }
    public void setIssue(Issue issue) { this.issue = issue; }

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
