package com.example.finalproject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.List;

public class RoomDetailsActivity extends AppCompatActivity {
    private ViewPager2 viewPagerImages;
    private TextView tvImageCounter, tvRoomAddress, tvRoomDescription, tvRoomPrice, tvOwnerName;
    private ImageButton btnPrevious, btnNext;
    private Button btnContactOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        viewPagerImages = findViewById(R.id.viewPagerImages);
        tvImageCounter = findViewById(R.id.tvImageCounter);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        // Additional Room Information
        tvRoomAddress = findViewById(R.id.tvRoomAddress);
        tvRoomDescription = findViewById(R.id.tvRoomDescription);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        btnContactOwner = findViewById(R.id.btnContactOwner);
        tvOwnerName = findViewById(R.id.tvOwnerName);

        // Retrieve the room and imageUrls
        Room room = (Room) getIntent().getSerializableExtra("room");
        List<String> imageUrls = room != null ? room.getImageUrls() : null;
        if (room != null) {
            btnContactOwner.setOnClickListener(v -> {
                String ownerName = room.getOwnerName();
                if (ownerName != null && !ownerName.isEmpty()) {
                    fetchOwnerIdByNameAndNavigate(ownerName);
                } else {
                    Toast.makeText(this, "Owner name is not available.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Populate Room Information
        if (room != null) {
            tvRoomAddress.setText("Address: " + room.getAddress());
            tvRoomDescription.setText(room.getDescription());
            tvRoomPrice.setText("Price: $" + room.getPrice());
            tvOwnerName.setText("Owner: " + room.getOwnerName());
        }

        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Set up the ViewPager2
            ImagePagerAdapter adapter = new ImagePagerAdapter(imageUrls, this);
            viewPagerImages.setAdapter(adapter);

            // Update image counter
            tvImageCounter.setText("1/" + imageUrls.size());

            // ViewPager2 Page Change Listener
            viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tvImageCounter.setText((position + 1) + "/" + imageUrls.size());
                }
            });

            Button btnVerification = findViewById(R.id.btnVerification);
            btnVerification.setOnClickListener(v -> {
                Intent intent = new Intent(RoomDetailsActivity.this, RoomVerificationActivity.class);
                intent.putExtra("room", room);
                startActivity(intent);
            });
            Button btnDeposit = findViewById(R.id.btnDeposit);
            btnDeposit.setOnClickListener(v -> {
                Intent intent = new Intent(RoomDetailsActivity.this, DepositActivity.class);
                intent.putExtra("room", room);
                startActivity(intent);
            });

            // Previous Button
            btnPrevious.setOnClickListener(v -> {
                int currentItem = viewPagerImages.getCurrentItem();
                if (currentItem > 0) {
                    viewPagerImages.setCurrentItem(currentItem - 1);
                }
            });

            // Next Button
            btnNext.setOnClickListener(v -> {
                int currentItem = viewPagerImages.getCurrentItem();
                if (currentItem < imageUrls.size() - 1) {
                    viewPagerImages.setCurrentItem(currentItem + 1);
                }
            });
        } else {
            // No images
            tvImageCounter.setText("0/0");
        }
    }
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null; // Trả về null nếu người dùng chưa đăng nhập
        }
    }

    private void fetchOwnerIdByNameAndNavigate(String ownerName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = getCurrentUserId();

        if (currentUserId == null) {
            Toast.makeText(this, "You need to log in to contact the owner.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("name", ownerName) // Assuming 'name' is the field for the user's name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Lấy ownerId từ document ID
                        String ownerId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        // Chuyển hướng đến ChatActivity với đủ thông tin
                        Intent intent = new Intent(this, ChatActivity.class);
                        intent.putExtra("contactName", ownerName);
                        intent.putExtra("contactUserId", ownerId);
                        intent.putExtra("currentUserId", currentUserId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Owner not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch owner ID.", Toast.LENGTH_SHORT).show());
    }

}