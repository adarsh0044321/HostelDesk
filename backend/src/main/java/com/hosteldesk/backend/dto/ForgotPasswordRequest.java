package com.hosteldesk.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message = "Institute ID is required")
    @JsonAlias({"institute_id", "instituteCode"})
    private String instituteCode;

    @NotBlank(message = "User ID / Email is required")
    @JsonAlias({"userId", "studentId", "wardenId", "staffId", "institutionalId", "email"})
    private String identifier;

    private String role; // STUDENT, WARDEN, STAFF
    private String reason;

    @JsonAlias({"contactPhone", "phone", "contactNumber", "mobileNumber", "mobile"})
    private String contactPhone;

    public ForgotPasswordRequest() {}

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
