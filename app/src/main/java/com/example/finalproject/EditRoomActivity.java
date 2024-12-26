package com.example.finalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditRoomActivity extends AppCompatActivity {
    private EditText etRoomName, etPrice, etAddress, etDescription, etAmenities;
    private Button btnUpdateRoom, btnSelectImages;
    private List<Uri> imageUris = new ArrayList<>();
    private FirebaseFirestore db;

    private String roomId;
    private Room currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        db = FirebaseFirestore.getInstance();

        etRoomName = findViewById(R.id.etRoomName);
        etPrice = findViewById(R.id.etPrice);
        etAddress = findViewById(R.id.etAddress);
        etDescription = findViewById(R.id.etDescription);
        etAmenities = findViewById(R.id.etAmenities);
        btnUpdateRoom = findViewById(R.id.btnUpdateRoom);
        btnSelectImages = findViewById(R.id.btnSelectImages);

        roomId = getIntent().getStringExtra("ROOM_ID");

        if (roomId != null) {
            loadRoomDetails(roomId);
        }

        btnSelectImages.setOnClickListener(v -> selectImages());
        btnUpdateRoom.setOnClickListener(v -> updateRoomDetails());
    }

    private void loadRoomDetails(String roomId) {
        db.collection("rooms").document(roomId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentRoom = documentSnapshot.toObject(Room.class);
                    if (currentRoom != null) {
                        populateFields(currentRoom);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải thông tin phòng", Toast.LENGTH_SHORT).show();
                });
    }

    private void populateFields(Room room) {
        etRoomName.setText(room.getRoomName());
        etPrice.setText(room.getPrice());
        etAddress.setText(room.getAddress());
        etDescription.setText(room.getDescription());
        etAmenities.setText(String.join(", ", room.getAmenities()));

    }

    private void selectImages() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            imageUris.clear();
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    imageUris.add(data.getData());
                }
            }
            Toast.makeText(this, imageUris.size() + " ảnh đã chọn", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRoomDetails() {
        String roomName = etRoomName.getText().toString();
        String price = etPrice.getText().toString();
        String address = etAddress.getText().toString();
        String description = etDescription.getText().toString();
        String amenitiesText = etAmenities.getText().toString();

        List<String> amenities = new ArrayList<>();
        if (!amenitiesText.isEmpty()) {
            amenities = Arrays.asList(amenitiesText.split(","));
        }

        List<String> imageUrls = new ArrayList<>();
        for (Uri uri : imageUris) {
            imageUrls.add(uri.toString());
        }

        if (currentRoom != null) {
            currentRoom.setRoomName(roomName);
            currentRoom.setPrice(price);
            currentRoom.setAddress(address);
            currentRoom.setDescription(description);
            currentRoom.setAmenities(amenities);

            if (!imageUrls.isEmpty()) {
                currentRoom.setImageUrls(imageUrls);
            }

            db.collection("rooms").document(roomId).set(currentRoom)
                    .addOnSuccessListener(aVoid -> {
                        setResult(RESULT_OK);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi cập nhật thông tin phòng", Toast.LENGTH_SHORT).show();
                    });
        }
    }


}
