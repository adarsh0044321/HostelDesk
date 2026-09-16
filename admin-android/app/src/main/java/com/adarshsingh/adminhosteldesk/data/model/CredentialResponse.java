package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class CredentialResponse {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("institutionalId")
    private String institutionalId;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("temporaryPassword")
    private String temporaryPassword;

    @SerializedName("message")
    private String message;

    public CredentialResponse() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getInstitutionalId() { return institutionalId; }
    public void setInstitutionalId(String institutionalId) { this.institutionalId = institutionalId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTemporaryPassword() { return temporaryPassword; }
    public void setTemporaryPassword(String temporaryPassword) { this.temporaryPassword = temporaryPassword; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
