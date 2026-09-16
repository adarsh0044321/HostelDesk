package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateUserWithTempPasswordRequest {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("institutionalId")
    private String institutionalId;

    @SerializedName("hostelId")
    private Long hostelId;

    @SerializedName("campusId")
    private Long campusId;

    @SerializedName("departmentId")
    private Long departmentId;

    @SerializedName("roomNumber")
    private String roomNumber;

    @SerializedName("blockName")
    private String blockName;

    @SerializedName("batch")
    private String batch;

    public CreateUserWithTempPasswordRequest() {}

    public CreateUserWithTempPasswordRequest(String fullName, String email, String phone, String institutionalId, Long hostelId, String roomNumber, String blockName) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.institutionalId = institutionalId;
        this.hostelId = hostelId;
        this.roomNumber = roomNumber;
        this.blockName = blockName;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getInstitutionalId() { return institutionalId; }
    public void setInstitutionalId(String institutionalId) { this.institutionalId = institutionalId; }

    public Long getHostelId() { return hostelId; }
    public void setHostelId(Long hostelId) { this.hostelId = hostelId; }

    public Long getCampusId() { return campusId; }
    public void setCampusId(Long campusId) { this.campusId = campusId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
}
