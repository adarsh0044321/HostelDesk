package com.hosteldesk.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class CrewWorkloadDto {
    private Long departmentId;
    private String name;
    private String displayName;
    private String description;
    private long staffCount;
    private List<String> staffNames = new ArrayList<>();
    private long activeTasks;
    private long resolvedTasks;
    private long totalTasks;

    public CrewWorkloadDto() {}

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public long getStaffCount() { return staffCount; }
    public void setStaffCount(long staffCount) { this.staffCount = staffCount; }

    public List<String> getStaffNames() { return staffNames; }
    public void setStaffNames(List<String> staffNames) { this.staffNames = staffNames; }

    public long getActiveTasks() { return activeTasks; }
    public void setActiveTasks(long activeTasks) { this.activeTasks = activeTasks; }

    public long getResolvedTasks() { return resolvedTasks; }
    public void setResolvedTasks(long resolvedTasks) { this.resolvedTasks = resolvedTasks; }

    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }
}
