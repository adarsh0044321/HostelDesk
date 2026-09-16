package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class UserDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("institutionalId")
    private String institutionalId;

    @SerializedName("role")
    private String role;

    @SerializedName("status")
    private String status;

    @SerializedName("hostelId")
    private Long hostelId;

    @SerializedName("hostelName")
    private String hostelName;

    @SerializedName("departmentId")
    private Long departmentId;

    @SerializedName("departmentName")
    private String departmentName;

    @SerializedName("roomNumber")
    private String roomNumber;

    @SerializedName("batch")
    private String batch;

    @SerializedName("needsPasswordChange")
    private Boolean needsPasswordChange = false;

    @SerializedName("instituteId")
    private Long instituteId;

    @SerializedName("instituteName")
    private String instituteName;

    @SerializedName("instituteCode")
    private String instituteCode;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getInstitutionalId() { return institutionalId; }
    public void setInstitutionalId(String institutionalId) { this.institutionalId = institutionalId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getHostelId() { return hostelId; }
    public void setHostelId(Long hostelId) { this.hostelId = hostelId; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Long getInstituteId() { return instituteId; }
    public void setInstituteId(Long instituteId) { this.instituteId = instituteId; }

    public String getInstituteName() { return instituteName; }
    public void setInstituteName(String instituteName) { this.instituteName = instituteName; }

    public String getInstituteCode() { return instituteCode; }
    public void setInstituteCode(String instituteCode) { this.instituteCode = instituteCode; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public Boolean getNeedsPasswordChange() { return needsPasswordChange; }
    public void setNeedsPasswordChange(Boolean needsPasswordChange) { this.needsPasswordChange = needsPasswordChange; }
}
