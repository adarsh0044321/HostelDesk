package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {
    @SerializedName("instituteCode")
    private String instituteCode;

    @SerializedName("identifier")
    private String identifier;

    @SerializedName("role")
    private String role;

    @SerializedName("reason")
    private String reason;

    @SerializedName("contactPhone")
    private String contactPhone;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String instituteCode, String identifier, String role, String reason) {
        this.instituteCode = instituteCode;
        this.identifier = identifier;
        this.role = role;
        this.reason = reason;
    }

    public ForgotPasswordRequest(String instituteCode, String identifier, String role, String reason, String contactPhone) {
        this.instituteCode = instituteCode;
        this.identifier = identifier;
        this.role = role;
        this.reason = reason;
        this.contactPhone = contactPhone;
    }

    public String getInstituteCode() { return instituteCode; }
    public void setInstituteCode(String instituteCode) { this.instituteCode = instituteCode; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
}