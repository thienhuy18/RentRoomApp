package com.example.finalproject;

import java.io.File;
import java.io.Serializable;
import java.util.List;



public class Room implements Serializable {
    private String roomName;
    private String price;
    private String address;
    private String description;
    private String idRoom;
    private String ownerName;
    private String userId;
    private List<String> imageUrls;
    private List<String> amenities;

    public Room() {

    }

    public Room(String userId, String roomName, String price, String address, String description, String idRoom, String ownerName, List<String> imageUrls, List<String> amenities) {
        this.roomName = roomName;
        this.price = price;
        this.address = address;
        this.description = description;
        this.idRoom = idRoom;
        this.ownerName = ownerName;
        this.imageUrls = imageUrls;
        this.amenities = amenities;
        this.userId = userId;
    }





    public String getUserId() {
        return userId;
    }



    public String getImageFileName() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String imageUrl = imageUrls.get(0);
            File file = new File(imageUrl);
            return file.getName();
        }
        return null;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }
}
