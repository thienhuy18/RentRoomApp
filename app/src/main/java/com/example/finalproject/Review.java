package com.example.finalproject;

public class Review {
    private String userName;
    private String comment;
    private float rating;
    private long timestamp;


    public Review() {}

    public Review(String userName, String comment, float rating, long timestamp) {
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
