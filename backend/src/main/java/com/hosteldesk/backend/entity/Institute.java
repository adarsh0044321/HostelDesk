package com.hosteldesk.backend.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "institutes")
public class Institute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g. "NCH-001"

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50)
    private String type = "UNIVERSITY";

    @Column(length = 100)
    private String email;

    @Column(name = "contact_number", length = 30)
    private String contactNumber;

    @Column(name = "ambulance_contact", length = 30)
    private String ambulanceContact = "108";

    @Column(name = "security_contact", length = 30)
    private String securityContact = "112";

    @Column(name = "emergency_desk_contact", length = 30)
    private String emergencyDeskContact = "+91 11 2766 7722";

    @Column(name = "security_passcode", length = 100)
    private String securityPasscode = "112233";

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, PENDING, SUSPENDED

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

    public Institute() {}

    public Institute(Long id, String code, String name, String type, String email, String contactNumber, String status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.email = email;
        this.contactNumber = contactNumber;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAmbulanceContact() { return ambulanceContact != null && !ambulanceContact.isEmpty() ? ambulanceContact : "108"; }
    public void setAmbulanceContact(String ambulanceContact) { this.ambulanceContact = ambulanceContact; }

    public String getSecurityContact() { return securityContact != null && !securityContact.isEmpty() ? securityContact : "112"; }
    public void setSecurityContact(String securityContact) { this.securityContact = securityContact; }

    public String getEmergencyDeskContact() { return emergencyDeskContact != null && !emergencyDeskContact.isEmpty() ? emergencyDeskContact : "+91 11 2766 7722"; }
    public void setEmergencyDeskContact(String emergencyDeskContact) { this.emergencyDeskContact = emergencyDeskContact; }

    public String getSecurityPasscode() { return securityPasscode != null && !securityPasscode.isEmpty() ? securityPasscode : "112233"; }
    public void setSecurityPasscode(String securityPasscode) { this.securityPasscode = securityPasscode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
