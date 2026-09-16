package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class AssignIssueRequest {
    @SerializedName("departmentId")
    private Long departmentId;

    @SerializedName("staffId")
    private Long staffId;

    @SerializedName("priority")
    private String priority;

    @SerializedName("notes")
    private String notes;

    public AssignIssueRequest() {}

    public AssignIssueRequest(Long departmentId, Long staffId, String priority, String notes) {
        this.departmentId = departmentId;
        this.staffId = staffId;
        this.priority = priority;
        this.notes = notes;
    }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
