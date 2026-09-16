package com.adarshsingh.adminhosteldesk.data.model;

import com.google.gson.annotations.SerializedName;

public class HostelDto {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("location")
    private String location;

    @SerializedName("description")
    private String description;

    @SerializedName("active")
    private Boolean active;

    @SerializedName("campusName")
    private String campusName;

    @SerializedName("campusId")
    private Long campusId;

    @SerializedName("studentCount")
    private long studentCount;

    @SerializedName("wardenId")
    private Long wardenId;

    @SerializedName("wardenName")
    private String wardenName;

    @SerializedName("wardenPhone")
    private String wardenPhone;

    @SerializedName("wardenAssigned")
    private boolean wardenAssigned;

    @SerializedName("openIssuesCount")
    private long openIssuesCount;

    @SerializedName("resolvedIssuesCount")
    private long resolvedIssuesCount;

    @SerializedName("totalIssuesCount")
    private long totalIssuesCount;

    public HostelDto() {}

    public HostelDto(Long id, String name, String location, String description, Boolean active) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getCampusName() { return campusName; }
    public void setCampusName(String campusName) { this.campusName = campusName; }

    public Long getCampusId() { return campusId; }
    public void setCampusId(Long campusId) { this.campusId = campusId; }

    public long getStudentCount() { return studentCount; }
    public void setStudentCount(long studentCount) { this.studentCount = studentCount; }

    public Long getWardenId() { return wardenId; }
    public void setWardenId(Long wardenId) { this.wardenId = wardenId; }

    public String getWardenName() { return wardenName; }
    public void setWardenName(String wardenName) { this.wardenName = wardenName; }

    public String getWardenPhone() { return wardenPhone; }
    public void setWardenPhone(String wardenPhone) { this.wardenPhone = wardenPhone; }

    public boolean isWardenAssigned() { return wardenAssigned; }
    public void setWardenAssigned(boolean wardenAssigned) { this.wardenAssigned = wardenAssigned; }

    public long getOpenIssuesCount() { return openIssuesCount; }
    public void setOpenIssuesCount(long openIssuesCount) { this.openIssuesCount = openIssuesCount; }

    public long getResolvedIssuesCount() { return resolvedIssuesCount; }
    public void setResolvedIssuesCount(long resolvedIssuesCount) { this.resolvedIssuesCount = resolvedIssuesCount; }

    public long getTotalIssuesCount() { return totalIssuesCount; }
    public void setTotalIssuesCount(long totalIssuesCount) { this.totalIssuesCount = totalIssuesCount; }

    @Override
    public String toString() {
        return name != null ? name : ("Hostel #" + id);
    }
}
