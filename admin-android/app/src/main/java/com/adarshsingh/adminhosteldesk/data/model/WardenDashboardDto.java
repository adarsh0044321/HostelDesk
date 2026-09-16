package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class WardenDashboardDto {
    @SerializedName("totalOpenCount")
    private long totalOpenCount;

    @SerializedName("urgentP1Count")
    private long urgentP1Count;

    @SerializedName("inWorkCount")
    private long inWorkCount;

    @SerializedName("pendingVerificationCount")
    private long pendingVerificationCount;

    @SerializedName("totalResolvedCount")
    private long totalResolvedCount;

    // Warden Audit & Work Review Metrics
    @SerializedName("totalHostelStaffCount")
    private long totalHostelStaffCount;

    @SerializedName("assignedStaffTasksCount")
    private long assignedStaffTasksCount;

    @SerializedName("unassignedStaffTasksCount")
    private long unassignedStaffTasksCount;

    @SerializedName("viewedIssuesCount")
    private long viewedIssuesCount;

    @SerializedName("unviewedIssuesCount")
    private long unviewedIssuesCount;

    @SerializedName("totalWardenViews")
    private long totalWardenViews;

    @SerializedName("healthPercentage")
    private int healthPercentage;

    @SerializedName("healthStatus")
    private String healthStatus;

    @SerializedName("healthSummary")
    private String healthSummary;

    @SerializedName("attentionRequired")
    private List<IssueDto> attentionRequired = new ArrayList<>();

    @SerializedName("departmentWorkloads")
    private List<java.util.Map<String, Object>> departmentWorkloads = new ArrayList<>();

    @SerializedName("recurringInsights")
    private List<InsightDto> recurringInsights = new ArrayList<>();

    public long getTotalOpenCount() { return totalOpenCount; }
    public void setTotalOpenCount(long totalOpenCount) { this.totalOpenCount = totalOpenCount; }

    public long getUrgentP1Count() { return urgentP1Count; }
    public void setUrgentP1Count(long urgentP1Count) { this.urgentP1Count = urgentP1Count; }

    public long getInWorkCount() { return inWorkCount; }
    public void setInWorkCount(long inWorkCount) { this.inWorkCount = inWorkCount; }

    public long getPendingVerificationCount() { return pendingVerificationCount; }
    public void setPendingVerificationCount(long pendingVerificationCount) { this.pendingVerificationCount = pendingVerificationCount; }

    public long getTotalResolvedCount() { return totalResolvedCount; }
    public void setTotalResolvedCount(long totalResolvedCount) { this.totalResolvedCount = totalResolvedCount; }

    public long getTotalHostelStaffCount() { return totalHostelStaffCount; }
    public void setTotalHostelStaffCount(long totalHostelStaffCount) { this.totalHostelStaffCount = totalHostelStaffCount; }

    public long getAssignedStaffTasksCount() { return assignedStaffTasksCount; }
    public void setAssignedStaffTasksCount(long assignedStaffTasksCount) { this.assignedStaffTasksCount = assignedStaffTasksCount; }

    public long getUnassignedStaffTasksCount() { return unassignedStaffTasksCount; }
    public void setUnassignedStaffTasksCount(long unassignedStaffTasksCount) { this.unassignedStaffTasksCount = unassignedStaffTasksCount; }

    public long getViewedIssuesCount() { return viewedIssuesCount; }
    public void setViewedIssuesCount(long viewedIssuesCount) { this.viewedIssuesCount = viewedIssuesCount; }

    public long getUnviewedIssuesCount() { return unviewedIssuesCount; }
    public void setUnviewedIssuesCount(long unviewedIssuesCount) { this.unviewedIssuesCount = unviewedIssuesCount; }

    public long getTotalWardenViews() { return totalWardenViews; }
    public void setTotalWardenViews(long totalWardenViews) { this.totalWardenViews = totalWardenViews; }

    public int getHealthPercentage() { return healthPercentage; }
    public void setHealthPercentage(int healthPercentage) { this.healthPercentage = healthPercentage; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public String getHealthSummary() { return healthSummary; }
    public void setHealthSummary(String healthSummary) { this.healthSummary = healthSummary; }

    public List<IssueDto> getAttentionRequired() { return attentionRequired; }
    public void setAttentionRequired(List<IssueDto> attentionRequired) { this.attentionRequired = attentionRequired; }

    public List<java.util.Map<String, Object>> getDepartmentWorkloads() { return departmentWorkloads; }
    public void setDepartmentWorkloads(List<java.util.Map<String, Object>> departmentWorkloads) { this.departmentWorkloads = departmentWorkloads; }

    public List<InsightDto> getRecurringInsights() { return recurringInsights; }
    public void setRecurringInsights(List<InsightDto> recurringInsights) { this.recurringInsights = recurringInsights; }
}
