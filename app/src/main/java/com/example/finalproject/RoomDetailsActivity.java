package com.example.finalproject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoomDetailsActivity extends AppCompatActivity {
    private ViewPager2 viewPagerImages;
    private TextView tvImageCounter, tvRoomAddress, tvRoomDescription, tvRoomPrice, tvOwnerName;
    private ImageButton btnPrevious, btnNext;
    private Button btnContactOwner;

    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private String roomId;

    private List<Review> reviewList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        viewPagerImages = findViewById(R.id.viewPagerImages);
        tvImageCounter = findViewById(R.id.tvImageCounter);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        tvRoomAddress = findViewById(R.id.tvRoomAddress);
        tvRoomDescription = findViewById(R.id.tvRoomDescription);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);
        btnContactOwner = findViewById(R.id.btnContactOwner);
        tvOwnerName = findViewById(R.id.tvOwnerName);

        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReviews.setAdapter(reviewAdapter);







        Room room = (Room) getIntent().getSerializableExtra("room");

        List<String> imageUrls = room != null ? room.getImageUrls() : null;

        if (room != null) {
            roomId = room.getIdRoom();
            fetchReviews(roomId);
        }

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



        if (room != null) {
            tvRoomAddress.setText("Address: " + room.getAddress());
            Log.d("RoomDetailsActivity", "Room Address: " + room.getAddress());

            tvRoomDescription.setText(room.getDescription());
            tvRoomPrice.setText("Price: $" + room.getPrice()+"/Tháng");
            tvOwnerName.setText("Owner: " + room.getOwnerName());
        }

        if (imageUrls != null && !imageUrls.isEmpty()) {
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
            Button btnReviewAndComment = findViewById(R.id.btnReviewAndComment);
            btnReviewAndComment.setOnClickListener(v -> {
                Intent intent = new Intent(RoomDetailsActivity.this, ReviewActivity.class);
                intent.putExtra("room", room);  // Gửi thông tin phòng tới Activity Review
                startActivity(intent);
            });

            btnPrevious.setOnClickListener(v -> {
                int currentItem = viewPagerImages.getCurrentItem();
                if (currentItem > 0) {
                    viewPagerImages.setCurrentItem(currentItem - 1);
                }
            });

            btnNext.setOnClickListener(v -> {
                int currentItem = viewPagerImages.getCurrentItem();
                if (currentItem < imageUrls.size() - 1) {
                    viewPagerImages.setCurrentItem(currentItem + 1);
                }
            });
        } else {
            tvImageCounter.setText("0/0");
        }
    }
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
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
                        String ownerId = queryDocumentSnapshots.getDocuments().get(0).getId();

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





    @Override
    protected void onResume() {
        super.onResume();
        fetchReviews(roomId);
    }





    private void fetchReviews(String roomId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("rooms")
                .document(roomId)
                .collection("reviews")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Order reviews by most recent
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "No reviews found for this room.", Toast.LENGTH_SHORT).show();
                    } else {
                        reviewList.clear();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Review review = document.toObject(Review.class);
                            if (review != null) {
                                reviewList.add(review);
                            }
                        }
                        reviewAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load reviews: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



}