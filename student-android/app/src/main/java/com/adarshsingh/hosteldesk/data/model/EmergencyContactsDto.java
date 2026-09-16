package com.adarshsingh.hosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class EmergencyContactsDto {
    @SerializedName("ambulanceContact")
    private String ambulanceContact;

    @SerializedName("securityContact")
    private String securityContact;

    @SerializedName("emergencyDeskContact")
    private String emergencyDeskContact;

    @SerializedName("dutyWardenName")
    private String dutyWardenName;

    @SerializedName("dutyWardenPhone")
    private String dutyWardenPhone;

    @SerializedName("hostelName")
    private String hostelName;

    public EmergencyContactsDto() {}

    public String getAmbulanceContact() { return ambulanceContact; }
    public void setAmbulanceContact(String ambulanceContact) { this.ambulanceContact = ambulanceContact; }

    public String getSecurityContact() { return securityContact; }
    public void setSecurityContact(String securityContact) { this.securityContact = securityContact; }

    public String getEmergencyDeskContact() { return emergencyDeskContact; }
    public void setEmergencyDeskContact(String emergencyDeskContact) { this.emergencyDeskContact = emergencyDeskContact; }

    public String getDutyWardenName() { return dutyWardenName; }
    public void setDutyWardenName(String dutyWardenName) { this.dutyWardenName = dutyWardenName; }

    public String getDutyWardenPhone() { return dutyWardenPhone; }
    public void setDutyWardenPhone(String dutyWardenPhone) { this.dutyWardenPhone = dutyWardenPhone; }

    public String getHostelName() { return hostelName; }
    public void setHostelName(String hostelName) { this.hostelName = hostelName; }
}
