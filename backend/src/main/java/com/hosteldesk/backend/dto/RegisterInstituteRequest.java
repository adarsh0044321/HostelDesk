package com.hosteldesk.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterInstituteRequest {

    @NotBlank(message = "Institute name is required")
    private String instituteName;

    @JsonAlias({"instituteId", "institute_id"})
    private String instituteCode;

    private String instituteType = "UNIVERSITY";
    private String instituteEmail;
    private String contactNumber;

    @NotBlank(message = "Administrator name is required")
    private String adminName;

    @JsonAlias({"adminId", "admin_id"})
    private String adminId;

    @NotBlank(message = "Administrator email is required")
    private String adminEmail;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @JsonAlias({"securityPasscode", "security_passcode", "secretPin", "secret_pin"})
    private String securityPasscode;

    public RegisterInstituteRequest() {}

    public String getInstituteName() { return instituteName; }
    public void setInstituteName(String instituteName) { this.instituteName = instituteName; }

    public String getInstituteCode() { return instituteCode; }
    public void setInstituteCode(String instituteCode) { this.instituteCode = instituteCode; }

    public String getInstituteType() { return instituteType; }
    public void setInstituteType(String instituteType) { this.instituteType = instituteType; }

    public String getInstituteEmail() { return instituteEmail; }
    public void setInstituteEmail(String instituteEmail) { this.instituteEmail = instituteEmail; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getAdminId() { return adminId != null && !adminId.trim().isEmpty() ? adminId : "ADM-001"; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSecurityPasscode() { return securityPasscode; }
    public void setSecurityPasscode(String securityPasscode) { this.securityPasscode = securityPasscode; }
}
