package com.hosteldesk.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "password_reset_requests")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PasswordResetRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_id", nullable = false)
    private Institute institute;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_role", nullable = false, length = 30)
    private String userRole;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "contact_phone", length = 30)
    private String contactPhone;

    @Column(name = "assigned_handler", length = 100)
    private String assignedHandler = "Institute IT Helpdesk";

    @Column(name = "assigned_department", length = 50)
    private String assignedDepartment = "IT_SUPPORT";

    @JsonIgnore
    @Column(name = "temporary_password_hash")
    private String temporaryPasswordHash;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private ZonedDateTime reviewedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    public PasswordResetRequest() {}

    public PasswordResetRequest(Institute institute, User user, String userRole, String reason) {
        this.institute = institute;
        this.user = user;
        this.userRole = userRole;
        this.reason = reason;
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Institute getInstitute() { return institute; }
    public void setInstitute(Institute institute) { this.institute = institute; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getTemporaryPasswordHash() { return temporaryPasswordHash; }
    public void setTemporaryPasswordHash(String temporaryPasswordHash) { this.temporaryPasswordHash = temporaryPasswordHash; }

    public User getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(User reviewedBy) { this.reviewedBy = reviewedBy; }

    public ZonedDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(ZonedDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public Long getUserId() { return user != null ? user.getId() : null; }
    public String getUserFullName() { return user != null ? user.getFullName() : null; }
    public String getUserEmail() { return user != null ? user.getEmail() : null; }
    public String getInstitutionalId() { return user != null ? user.getInstitutionalId() : null; }
    public String getInstituteCode() { return institute != null ? institute.getCode() : null; }
    public String getInstituteName() { return institute != null ? institute.getName() : null; }
    public Long getReviewedById() { return reviewedBy != null ? reviewedBy.getId() : null; }
    public String getReviewedByName() { return reviewedBy != null ? reviewedBy.getFullName() : null; }

    public String getContactPhone() {
        if (contactPhone != null && !contactPhone.trim().isEmpty()) {
            return contactPhone.trim();
        }
        return user != null ? user.getPhone() : null;
    }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getUserPhone() {
        return getContactPhone();
    }

    public String getAssignedHandler() { return assignedHandler; }
    public void setAssignedHandler(String assignedHandler) { this.assignedHandler = assignedHandler; }

    public String getAssignedDepartment() { return assignedDepartment; }
    public void setAssignedDepartment(String assignedDepartment) { this.assignedDepartment = assignedDepartment; }
}
