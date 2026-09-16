package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.IssuePriority;
import jakarta.validation.constraints.NotNull;

public class AssignIssueRequest {
    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private Long staffId;
    private IssuePriority priority;
    private String notes;

    public AssignIssueRequest() {}

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public IssuePriority getPriority() { return priority; }
    public void setPriority(IssuePriority priority) { this.priority = priority; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
