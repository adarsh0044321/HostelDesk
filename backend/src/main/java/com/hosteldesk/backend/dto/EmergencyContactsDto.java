package com.hosteldesk.backend.dto;

public class EmergencyContactsDto {
    private String ambulanceContact = "108";
    private String securityContact = "112";
    private String emergencyDeskContact = "+91 11 2766 7722";
    private String wardenName;
    private String wardenPhone;
    private String hostelName;

    public EmergencyContactsDto() {}

    public EmergencyContactsDto(String ambulanceContact, String securityContact, String emergencyDeskContact,
                                String wardenName, String wardenPhone, String hostelName) {
        this.ambulanceContact = ambulanceContact;
        this.securityContact = securityContact;
        this.emergencyDeskContact = emergencyDeskContact;
        this.wardenName = wardenName;
        this.wardenPhone = wardenPhone;
        this.hostelName = hostelName;
    }

    public String getAmbulanceContact() { return ambulanceContact; }
    public void setAmbulanceContact(String ambulanceContact) { this.ambulanceContact = ambulanceContact; }

    public String getSecurityContact() { return securityContact; }
    public void setSecurityContact(String securityContact) { this.securityContact = securityContact; }

    public String getEmergencyDeskContact() { return emergencyDeskContact; }
    public void setEmergencyDeskContact(String emergencyDeskContact) { this.emergencyDeskContact = emergencyDeskContact; }

    public String getWardenName() { return wardenName; }
    public void setWardenName(String wardenName) { this.wardenName = wardenName; }

    public String getWardenPhone() { return wardenPhone; }
    public void setWardenPhone(String wardenPhone) { this.wardenPhone = wardenPhone; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }
}
