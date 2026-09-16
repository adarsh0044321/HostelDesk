package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("instituteCode")
    private String instituteCode;

    @SerializedName("emailOrInstitutionalId")
    private String emailOrInstitutionalId;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    @SerializedName("targetApp")
    private String targetApp = "STUDENT";

    public LoginRequest() {}

    public LoginRequest(String identifier, String password) {
        this(null, identifier, password);
    }

    public LoginRequest(String instituteCode, String identifier, String password) {
        this.instituteCode = instituteCode;
        this.emailOrInstitutionalId = identifier;
        this.email = identifier;
        this.password = password;
        this.targetApp = "STUDENT";
    }

    public String getInstituteCode() { return instituteCode; }
    public void setInstituteCode(String instituteCode) { this.instituteCode = instituteCode; }

    public String getEmailOrInstitutionalId() { return emailOrInstitutionalId; }
    public void setEmailOrInstitutionalId(String emailOrInstitutionalId) {
        this.emailOrInstitutionalId = emailOrInstitutionalId;
        this.email = emailOrInstitutionalId;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        this.emailOrInstitutionalId = email;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTargetApp() { return targetApp; }
    public void setTargetApp(String targetApp) { this.targetApp = targetApp; }
}
