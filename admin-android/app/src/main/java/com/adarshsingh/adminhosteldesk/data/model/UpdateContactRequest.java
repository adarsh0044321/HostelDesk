package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class UpdateContactRequest {
    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    public UpdateContactRequest() {}

    public UpdateContactRequest(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
