package com.example.madassignment;

public class Complaint {
    private String date;
    private String time;
    private String title;
    private String description;
    private String category;
    private String ticketNo;

    public Complaint() {}

    public Complaint(String date, String time, String title, String description) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public String getTicketNo() {
        return ticketNo;
    }
}
