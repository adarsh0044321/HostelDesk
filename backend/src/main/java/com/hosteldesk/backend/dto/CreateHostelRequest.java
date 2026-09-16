package com.hosteldesk.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateHostelRequest {
    @NotBlank(message = "Hostel name is required")
    private String name;

    private String location;
    private String description;
    private Long campusId;
    private Boolean active = true;

    public CreateHostelRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCampusId() { return campusId; }
    public void setCampusId(Long campusId) { this.campusId = campusId; }

    public Boolean getActive() { return active != null ? active : true; }
    public void setActive(Boolean active) { this.active = active; }
}
