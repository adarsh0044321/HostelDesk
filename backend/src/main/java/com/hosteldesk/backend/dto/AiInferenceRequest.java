package com.hosteldesk.backend.dto;

public class AiInferenceRequest {
    private String title;
    private String description;
    private String category;
    private String blockName;
    private String roomNumber;

    public AiInferenceRequest() {}

    public AiInferenceRequest(String title, String description, String category, String blockName, String roomNumber) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.blockName = blockName;
        this.roomNumber = roomNumber;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBlockName() { return blockName; }
    public void setBlockName(String blockName) { this.blockName = blockName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
}
