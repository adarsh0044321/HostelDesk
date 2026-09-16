package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class UpdateProfileRequest {
    @SerializedName("fullName")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("roomNumber")
    private String roomNumber;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String fullName, String phone, String roomNumber) {
        this.fullName = fullName;
        this.phone = phone;
        this.roomNumber = roomNumber;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
}