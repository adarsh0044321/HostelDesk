package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class InstitutePublicDto {
    @SerializedName("instituteCode")
    private String instituteCode;

    @SerializedName("instituteName")
    private String instituteName;

    @SerializedName("campusName")
    private String campusName;

    @SerializedName("contactNumber")
    private String contactNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("status")
    private String status;

    public InstitutePublicDto() {}

    public String getInstituteCode() { return instituteCode; }
    public void setInstituteCode(String instituteCode) { this.instituteCode = instituteCode; }

    public String getInstituteName() { return instituteName; }
    public void setInstituteName(String instituteName) { this.instituteName = instituteName; }

    public String getCampusName() { return campusName; }
    public void setCampusName(String campusName) { this.campusName = campusName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}