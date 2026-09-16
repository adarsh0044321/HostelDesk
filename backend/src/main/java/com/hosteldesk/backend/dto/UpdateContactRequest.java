package com.hosteldesk.backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class UpdateContactRequest {
    @JsonAlias({"phone", "phoneNumber", "contactNumber"})
    private String phone;

    @JsonAlias({"email", "officialEmail"})
    private String email;

    public UpdateContactRequest() {}
    public UpdateContactRequest(String phone, String email) {
        this.phone = phone;
        this.email = email;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
