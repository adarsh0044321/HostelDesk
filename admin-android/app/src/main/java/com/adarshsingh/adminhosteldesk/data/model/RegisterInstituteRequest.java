package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterInstituteRequest {
    @SerializedName("instituteName")
    private String instituteName;

    @SerializedName("instituteCode")
    private String instituteCode;

    @SerializedName("instituteType")
    private String instituteType = "UNIVERSITY";

    @SerializedName("instituteEmail")
    private String instituteEmail;

    @SerializedName("contactNumber")
    private String contactNumber;

    @SerializedName("adminName")
    private String adminName;

    @SerializedName("adminId")
    private String adminId;

    @SerializedName("adminEmail")
    private String adminEmail;

    @SerializedName("password")
    private String password;

    @SerializedName("securityPasscode")
    private String securityPasscode;

    public RegisterInstituteRequest() {}

    public RegisterInstituteRequest(String instituteName, String instituteCode, String contactNumber,
                                    String adminName, String adminEmail, String password) {
        this(instituteName, instituteCode, contactNumber, adminName, adminEmail, password, null);
    }

    public RegisterInstituteRequest(String instituteName, String instituteCode, String contactNumber,
                                    String adminName, String adminEmail, String password, String securityPasscode) {
        this.instituteName = instituteName;
        this.instituteCode = instituteCode;
        this.contactNumber = contactNumber;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.password = password;
        this.securityPasscode = securityPasscode;
        this.adminId = "ADM-" + (instituteCode != null ? instituteCode : "01");
        this.instituteEmail = adminEmail;
    }

    public String getInstituteName() { return instituteName; }
    public void setInstituteName(String instituteName) { this.instituteName = instituteName; }

    public String getInstituteCode() { return instituteCode; }
    public void setInstituteCode(String instituteCode) { this.instituteCode = instituteCode; }

    public String getInstituteType() { return instituteType; }
    public void setInstituteType(String instituteType) { this.instituteType = instituteType; }

    public String getInstituteEmail() { return instituteEmail; }
    public void setInstituteEmail(String instituteEmail) { this.instituteEmail = instituteEmail; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSecurityPasscode() { return securityPasscode; }
    public void setSecurityPasscode(String securityPasscode) { this.securityPasscode = securityPasscode; }
}