package com.hosteldesk.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardDto {
    private String studentName;
    private String institutionalId;
    private String hostelName;
    private String blockName;
    private String roomNumber;

    // Vitals
    private String waterStatus = "✓ Normal";
    private String powerStatus = "✓ Normal";
    private int activeRequestsCount = 0;
    private int pendingVerificationCount = 0;
    private int resolvedCount = 0;

    // Notice
    private String maintenanceNotice = "Block B Routine Maintenance: Supply lines normal. Please report any low pressure promptly.";

    // Active issues
    private List<IssueDto> activeIssues = new ArrayList<>();
    private List<NotificationDto> recentNotifications = new ArrayList<>();

    public StudentDashboardDto() {}

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getInstitutionalId() { return institutionalId; }
    public void setInstitutionalId(String institutionalId) { this.institutionalId = institutionalId; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getWaterStatus() { return waterStatus; }
    public void setWaterStatus(String waterStatus) { this.waterStatus = waterStatus; }

    public String getPowerStatus() { return powerStatus; }
    public void setPowerStatus(String powerStatus) { this.powerStatus = powerStatus; }

    public int getActiveRequestsCount() { return activeRequestsCount; }
    public void setActiveRequestsCount(int activeRequestsCount) { this.activeRequestsCount = activeRequestsCount; }

    public int getPendingVerificationCount() { return pendingVerificationCount; }
    public void setPendingVerificationCount(int pendingVerificationCount) { this.pendingVerificationCount = pendingVerificationCount; }

    public int getResolvedCount() { return resolvedCount; }
    public void setResolvedCount(int resolvedCount) { this.resolvedCount = resolvedCount; }

    public String getMaintenanceNotice() { return maintenanceNotice; }
    public void setMaintenanceNotice(String maintenanceNotice) { this.maintenanceNotice = maintenanceNotice; }

    public List<IssueDto> getActiveIssues() { return activeIssues; }
    public void setActiveIssues(List<IssueDto> activeIssues) { this.activeIssues = activeIssues; }

    public List<NotificationDto> getRecentNotifications() { return recentNotifications; }
    public void setRecentNotifications(List<NotificationDto> recentNotifications) { this.recentNotifications = recentNotifications; }
}
