package com.example.finalproject;

import java.io.Serializable;
import java.util.Date;

public class RoomVerification implements Serializable {
    private String verificationId;
    private String roomId;
    private String ownerId;
    private String renterId;
    private String status; // PENDING, APPROVED, REJECTED
    private String ownerVerificationDetails;
    private String renterVerificationDetails;
    private Date createdAt;
    private Date updatedAt;

    // Constructors
    public RoomVerification() {}

    public RoomVerification(String roomId, String ownerId, String renterId) {
        this.roomId = roomId;
        this.ownerId = ownerId;
        this.renterId = renterId;
        this.status = "PENDING";
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerVerificationDetails() {
        return ownerVerificationDetails;
    }

    public void setOwnerVerificationDetails(String ownerVerificationDetails) {
        this.ownerVerificationDetails = ownerVerificationDetails;
    }

    public String getRenterVerificationDetails() {
        return renterVerificationDetails;
    }

    public void setRenterVerificationDetails(String renterVerificationDetails) {
        this.renterVerificationDetails = renterVerificationDetails;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
