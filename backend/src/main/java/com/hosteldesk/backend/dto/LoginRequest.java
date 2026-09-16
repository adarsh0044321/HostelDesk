package com.hosteldesk.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @JsonAlias({"institute_id", "instituteCode", "instituteId"})
    private String instituteCode;

    @JsonAlias({"email", "username", "institutionalId", "studentId", "staffId", "adminId", "wardenId", "identifier"})
    private String emailOrInstitutionalId;

    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @JsonAlias({"securityPasscode", "security_passcode", "secretPin", "secret_pin"})
    private String securityPasscode;

    private String targetApp; // "STUDENT" or "ADMIN" or "WARDEN_PORTAL" or "EXECUTIVE_PORTAL"

    public LoginRequest() {}

    public LoginRequest(String emailOrInstitutionalId, String password, String targetApp) {
        this.emailOrInstitutionalId = emailOrInstitutionalId;
        this.email = emailOrInstitutionalId;
        this.password = password;
        this.targetApp = targetApp;
    }

    public LoginRequest(String instituteCode, String emailOrInstitutionalId, String password, String securityPasscode, String targetApp) {
        this.instituteCode = instituteCode;
        this.emailOrInstitutionalId = emailOrInstitutionalId;
        this.email = emailOrInstitutionalId;
        this.password = password;
        this.securityPasscode = securityPasscode;
        this.targetApp = targetApp;
    }

    public String getEmailOrInstitutionalId() {
        if (emailOrInstitutionalId != null && !emailOrInstitutionalId.trim().isEmpty()) {
            return emailOrInstitutionalId.trim();
        }
        if (email != null && !email.trim().isEmpty()) {
            return email.trim();
        }
        return null;
    }

    public void setEmailOrInstitutionalId(String emailOrInstitutionalId) {
        this.emailOrInstitutionalId = emailOrInstitutionalId;
        if (this.email == null) {
            this.email = emailOrInstitutionalId;
        }
    }

    public String getEmail() {
        return getEmailOrInstitutionalId();
    }

    public void setEmail(String email) {
        this.email = email;
        if (this.emailOrInstitutionalId == null) {
            this.emailOrInstitutionalId = email;
        }
    }

    public String getInstituteCode() { return instituteCode != null ? instituteCode.trim() : null; }
    public void setInstituteCode(String instituteCode) { this.instituteCode = instituteCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSecurityPasscode() { return securityPasscode != null ? securityPasscode.trim() : null; }
    public void setSecurityPasscode(String securityPasscode) { this.securityPasscode = securityPasscode; }

    public String getTargetApp() { return targetApp; }
    public void setTargetApp(String targetApp) { this.targetApp = targetApp; }
}
