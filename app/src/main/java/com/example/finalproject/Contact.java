package com.example.finalproject;

public class Contact {
    private String name;
    private String userId;

    public Contact(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}
