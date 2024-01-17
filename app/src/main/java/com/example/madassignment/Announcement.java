package com.example.madassignment;

public class Announcement {
    private String day;
    private String date;
    private String time;
    private String title;
    private String description;
    private String id;  // Assuming you have an ID attribute

    public Announcement(String day, String date, String time, String title, String description, String id) {
        this.day = day;
        this.date = date;
        this.time = time;
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Getter method for ID
    public String getId() {
        return id;
    }
}
