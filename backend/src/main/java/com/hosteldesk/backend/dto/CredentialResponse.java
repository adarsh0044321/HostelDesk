package com.hosteldesk.backend.dto;

public class CredentialResponse {
    private Long userId;
    private String fullName;
    private String institutionalId;
    private String email;
    private String role;
    private String temporaryPassword;
    private String message;

    public CredentialResponse() {}

    public CredentialResponse(Long userId, String fullName, String institutionalId, String email, String role, String temporaryPassword, String message) {
        this.userId = userId;
        this.fullName = fullName;
        this.institutionalId = institutionalId;
        this.email = email;
        this.role = role;
        this.temporaryPassword = temporaryPassword;
        this.message = message;
    }

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
