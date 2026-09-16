package com.hosteldesk.backend.dto;

import com.hosteldesk.backend.entity.Hostel;

public class HostelDto {
    private Long id;
    private String name;
    private String location;
    private String description;
    private Boolean active;
    private String campusName;
    private Long campusId;
    private long studentCount;
    private Long wardenId;
    private String wardenName;
    private String wardenPhone;
    private boolean wardenAssigned;
    private long openIssuesCount;
    private long resolvedIssuesCount;
    private long totalIssuesCount;

    public HostelDto() {}

    public HostelDto(Long id, String name, String location, String description, Boolean active) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.active = active;
    }

    public static HostelDto fromEntity(Hostel hostel) {
        if (hostel == null) return null;
        HostelDto dto = new HostelDto();
        dto.setId(hostel.getId());
        dto.setName(hostel.getName());
        dto.setLocation(hostel.getLocation());
        dto.setDescription(hostel.getDescription());
        dto.setActive(hostel.getActive());
        if (hostel.getCampus() != null) {
            dto.setCampusId(hostel.getCampus().getId());
            dto.setCampusName(hostel.getCampus().getName());
        }
        return dto;
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

    public long getStudentCount() { return studentCount; }
    public void setStudentCount(long studentCount) { this.studentCount = studentCount; }

    public String getWardenName() { return wardenName; }
    public void setWardenName(String wardenName) { this.wardenName = wardenName; }

    public String getWardenPhone() { return wardenPhone; }
    public void setWardenPhone(String wardenPhone) { this.wardenPhone = wardenPhone; }

    public long getOpenIssuesCount() { return openIssuesCount; }
    public void setOpenIssuesCount(long openIssuesCount) { this.openIssuesCount = openIssuesCount; }

    public long getResolvedIssuesCount() { return resolvedIssuesCount; }
    public void setResolvedIssuesCount(long resolvedIssuesCount) { this.resolvedIssuesCount = resolvedIssuesCount; }

    public Long getCampusId() { return campusId; }
    public void setCampusId(Long campusId) { this.campusId = campusId; }

    public Long getWardenId() { return wardenId; }
    public void setWardenId(Long wardenId) { this.wardenId = wardenId; }

    public boolean isWardenAssigned() { return wardenAssigned; }
    public void setWardenAssigned(boolean wardenAssigned) { this.wardenAssigned = wardenAssigned; }

    public long getTotalIssuesCount() { return totalIssuesCount; }
    public void setTotalIssuesCount(long totalIssuesCount) { this.totalIssuesCount = totalIssuesCount; }
}
