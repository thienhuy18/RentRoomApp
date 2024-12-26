package com.example.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddRoomActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button btnAddRoom, btnSelectImages;
    private EditText etRoomName, etPrice, etAddress, etDescription, etAmenities;
    private List<Uri> imageUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        etRoomName = findViewById(R.id.etRoomName);
        etPrice = findViewById(R.id.etPrice);
        etAddress = findViewById(R.id.etAddress);
        etDescription = findViewById(R.id.etDescription);
        etAmenities = findViewById(R.id.etAmenities);
        btnAddRoom = findViewById(R.id.btnAddRoom);
        btnSelectImages = findViewById(R.id.btnSelectImages);

        btnSelectImages.setOnClickListener(v -> selectImages());


        btnAddRoom.setOnClickListener(v -> addRoom());
    }

    private void selectImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    String savedImagePath = saveImageToInternalStorage(imageUri);
                    imageUris.add(Uri.parse(savedImagePath)); // Store saved path
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                String savedImagePath = saveImageToInternalStorage(imageUri);
                imageUris.add(Uri.parse(savedImagePath));
            }
            Toast.makeText(this, "Chọn ảnh thành công", Toast.LENGTH_SHORT).show();
        }
    }


    private void addRoom() {
        String roomName = etRoomName.getText().toString();
        String price = etPrice.getText().toString();
        String address = etAddress.getText().toString();
        String description = etDescription.getText().toString();
        String amenitiesText = etAmenities.getText().toString();

        List<String> amenities = new ArrayList<>();
        for (String amenity : amenitiesText.split(",")) {
            amenities.add(amenity.trim());
        }

        if (!roomName.isEmpty() && !price.isEmpty() && !address.isEmpty() && !imageUris.isEmpty()) {
            List<String> imagePaths = new ArrayList<>();
            for (Uri uri : imageUris) {
                imagePaths.add(uri.toString());
            }

            String idRoom = db.collection("rooms").document().getId();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();

                db.collection("users").document(userId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String ownerName;

                            if (documentSnapshot.exists() && documentSnapshot.getString("name") != null) {
                                ownerName = documentSnapshot.getString("name");
                            } else {
                                ownerName = "Unknown Owner";
                            }

                            Room room = new Room(userId, roomName, price, address, description, idRoom, ownerName, imagePaths, amenities);

                            db.collection("rooms").document(idRoom).set(room)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Thêm phòng thành công", Toast.LENGTH_SHORT).show();
                                        Intent resultIntent = new Intent();
                                        setResult(RESULT_OK, resultIntent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Lỗi thêm phòng", Toast.LENGTH_SHORT).show();
                                    });

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi thêm phòng", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Người dùng không xác định", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin và chọn ít nhất 1 ảnh", Toast.LENGTH_SHORT).show();
        }
    }


    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File directory = new File(getFilesDir(), "images");
            if (!directory.exists()) {
                directory.mkdir();
            }
            String imageFileName = "image_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(directory, imageFileName);
            OutputStream outputStream = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
