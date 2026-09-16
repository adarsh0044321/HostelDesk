package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateHostelRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("location")
    private String location;

    @SerializedName("description")
    private String description;

    @SerializedName("campusId")
    private Long campusId;

    @SerializedName("active")
    private Boolean active = true;

    public CreateHostelRequest() {}

    public CreateHostelRequest(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }

    public CreateHostelRequest(String name, String location, String description, Long campusId, Boolean active) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.campusId = campusId;
        this.active = active;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCampusId() { return campusId; }
    public void setCampusId(Long campusId) { this.campusId = campusId; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
