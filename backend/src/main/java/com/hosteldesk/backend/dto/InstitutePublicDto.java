package com.hosteldesk.backend.dto;

public class InstitutePublicDto {
    private String instituteCode;
    private String instituteName;
    private String campusName;
    private String contactNumber;
    private String email;
    private String status;

    public InstitutePublicDto() {}

    public InstitutePublicDto(String instituteCode, String instituteName, String campusName,
                              String contactNumber, String email, String status) {
        this.instituteCode = instituteCode;
        this.instituteName = instituteName;
        this.campusName = campusName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.status = status;
    }

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