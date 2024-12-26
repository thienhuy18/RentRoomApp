package com.example.finalproject;

import java.io.Serializable;
import java.util.Date;

public class Deposit implements Serializable {
    private String depositId;
    private String roomId;
    private String ownerId;
    private String renterId;
    private double depositAmount;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private String paymentMethod;
    private String transactionDetails;

    public Deposit() {}

    public Deposit(String roomId, String ownerId, String renterId, double depositAmount) {
        this.roomId = roomId;
        this.ownerId = ownerId;
        this.renterId = renterId;
        this.depositAmount = depositAmount;
        this.status = "PENDING";
        this.createdAt = new Date();
    }

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
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

    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(String transactionDetails) {
        this.transactionDetails = transactionDetails;
    }
}