package com.example.memories;

public class Memory {
    private String title;
    private String description;
    private String location;
    private byte[] image;

    public Memory(String title, String description, String location, byte[] image) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.image = image;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public byte[] getImage() { return image; }
}
