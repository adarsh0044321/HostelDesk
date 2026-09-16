package com.hosteldesk.backend.entity;

public enum Role {
    STUDENT,
    WARDEN,
    STAFF,
    MAINTENANCE_STAFF, // Legacy compatibility alias for STAFF
    INSTITUTE_ADMIN,
    ADMIN,              // Legacy compatibility alias for INSTITUTE_ADMIN
    SUPER_ADMIN;

    public boolean isInstituteAdmin() {
        return this == INSTITUTE_ADMIN || this == ADMIN || this == SUPER_ADMIN;
    }

    public boolean isWarden() {
        return this == WARDEN;
    }

    public boolean isStaff() {
        return this == STAFF || this == MAINTENANCE_STAFF;
    }

    public boolean isStudent() {
        return this == STUDENT;
    }
}
