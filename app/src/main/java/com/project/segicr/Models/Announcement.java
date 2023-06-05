package com.project.segicr.Models;

public class Announcement {
    private String id, text;

    public Announcement(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public Announcement() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
