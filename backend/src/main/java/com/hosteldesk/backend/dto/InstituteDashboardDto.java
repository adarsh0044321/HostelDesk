package com.hosteldesk.backend.dto;

import java.util.List;

public class InstituteDashboardDto {
    private String instituteCode;
    private String instituteName;
    private long totalStudents;
    private long totalWardens;
    private long totalStaff;
    private long totalHostels;
    private long openIssues;
    private long urgentIssues;
    private long resolvedIssues;
    private long pendingVerifications;
    private long pendingPasswordResets;
    private List<IssueDto> recentIssues;

    public InstituteDashboardDto() {}

    public String getInstituteCode() { return instituteCode; }
    public void setInstituteCode(String instituteCode) { this.instituteCode = instituteCode; }

    public String getInstituteName() { return instituteName; }
    public void setInstituteName(String instituteName) { this.instituteName = instituteName; }

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalWardens() { return totalWardens; }
    public void setTotalWardens(long totalWardens) { this.totalWardens = totalWardens; }

    public long getTotalStaff() { return totalStaff; }
    public void setTotalStaff(long totalStaff) { this.totalStaff = totalStaff; }

    public long getTotalHostels() { return totalHostels; }
    public void setTotalHostels(long totalHostels) { this.totalHostels = totalHostels; }

    public long getOpenIssues() { return openIssues; }
    public void setOpenIssues(long openIssues) { this.openIssues = openIssues; }

    public long getUrgentIssues() { return urgentIssues; }
    public void setUrgentIssues(long urgentIssues) { this.urgentIssues = urgentIssues; }

    public long getResolvedIssues() { return resolvedIssues; }
    public void setResolvedIssues(long resolvedIssues) { this.resolvedIssues = resolvedIssues; }

    public long getPendingVerifications() { return pendingVerifications; }
    public void setPendingVerifications(long pendingVerifications) { this.pendingVerifications = pendingVerifications; }

    public long getPendingPasswordResets() { return pendingPasswordResets; }
    public void setPendingPasswordResets(long pendingPasswordResets) { this.pendingPasswordResets = pendingPasswordResets; }

    public List<IssueDto> getRecentIssues() { return recentIssues; }
    public void setRecentIssues(List<IssueDto> recentIssues) { this.recentIssues = recentIssues; }
}
