package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class PasswordResetDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("userFullName")
    private String userFullName;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("institutionalId")
    private String institutionalId;

    @SerializedName("userRole")
    private String userRole;

    @SerializedName("contactPhone")
    private String contactPhone;

    @SerializedName("userPhone")
    private String userPhone;

    @SerializedName("status")
    private String status;

    @SerializedName("reason")
    private String reason;

    @SerializedName("assignedHandler")
    private String assignedHandler;

    @SerializedName("assignedDepartment")
    private String assignedDepartment;

    @SerializedName("createdAt")
    private String createdAt;

    public PasswordResetDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getInstitutionalId() { return institutionalId; }
    public void setInstitutionalId(String institutionalId) { this.institutionalId = institutionalId; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getContactPhone() {
        if (contactPhone != null && !contactPhone.trim().isEmpty()) {
            return contactPhone.trim();
        }
        return userPhone != null ? userPhone.trim() : null;
    }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getAssignedHandler() { return assignedHandler; }
    public void setAssignedHandler(String assignedHandler) { this.assignedHandler = assignedHandler; }

    public String getAssignedDepartment() { return assignedDepartment; }
    public void setAssignedDepartment(String assignedDepartment) { this.assignedDepartment = assignedDepartment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
