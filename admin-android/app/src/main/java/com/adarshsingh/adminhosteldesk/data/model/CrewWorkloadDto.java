package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class CrewWorkloadDto {
    @SerializedName("departmentId")
    private Long departmentId;

    @SerializedName("name")
    private String name;

    @SerializedName("displayName")
    private String displayName;

    @SerializedName("description")
    private String description;

    @SerializedName("staffCount")
    private long staffCount;

    @SerializedName("staffNames")
    private List<String> staffNames = new ArrayList<>();

    @SerializedName("activeTasks")
    private long activeTasks;

    @SerializedName("resolvedTasks")
    private long resolvedTasks;

    @SerializedName("totalTasks")
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
