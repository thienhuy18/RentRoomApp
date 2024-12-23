package com.example.finalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewActivity extends AppCompatActivity {
    private EditText etComment;
    private RatingBar ratingBar;
    private Button btnSubmit;
    private Room room;
    private String currentUserName; // To store the username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        etComment = findViewById(R.id.etComment);
        ratingBar = findViewById(R.id.ratingBar);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Get room details from Intent
        room = (Room) getIntent().getSerializableExtra("room");

        // Fetch the username of the current user
        fetchCurrentUserName();

        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void fetchCurrentUserName() {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để đánh giá.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users") // Assuming usernames are stored in the "users" collection
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUserName = documentSnapshot.getString("name"); // Assume "name" field stores the username
                    } else {
                        Toast.makeText(this, "Không thể lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Đã xảy ra lỗi khi lấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                });
    }

    private void submitReview() {
        String comment = etComment.getText().toString();
        float rating = ratingBar.getRating();

        if (comment.isEmpty() || rating == 0) {
            Toast.makeText(this, "Vui lòng cung cấp đánh giá và bình luận.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserName == null) {
            Toast.makeText(this, "Không thể gửi đánh giá mà không có tên người dùng.", Toast.LENGTH_SHORT).show();
            return;
        }

        Review review = new Review(currentUserName, comment, rating, System.currentTimeMillis());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Save the review in Firestore
        db.collection("rooms")
                .document(room.getIdRoom())
                .collection("reviews")
                .add(review)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Đánh giá của bạn đã được gửi!", Toast.LENGTH_SHORT).show();
                    finish(); // Go back to the previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Đã xảy ra lỗi khi gửi đánh giá.", Toast.LENGTH_SHORT).show();
                });
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }
}
