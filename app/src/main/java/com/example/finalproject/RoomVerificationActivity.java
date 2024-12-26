package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RoomVerificationActivity extends AppCompatActivity {
    private Room room;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView tvVerificationStatus;
    private EditText etOwnerVerification;
    private EditText etRenterVerification;
    private Button btnSubmitVerification;
    private Button btnApproveVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        room = (Room) getIntent().getSerializableExtra("room");

        tvVerificationStatus = findViewById(R.id.tvVerificationStatus);
        etOwnerVerification = findViewById(R.id.etOwnerVerification);
        etRenterVerification = findViewById(R.id.etRenterVerification);
        btnSubmitVerification = findViewById(R.id.btnSubmitVerification);
        btnApproveVerification = findViewById(R.id.btnApproveVerification);

        initializeVerificationProcess();
    }

    private void initializeVerificationProcess() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        if (currentUserId.equals(room.getUserId())) {
            setupOwnerView();
        } else {
            setupRenterView();
        }
    }

    private void setupOwnerView() {
        etRenterVerification.setVisibility(View.GONE);
        btnSubmitVerification.setText("Xác Nhận Thông Tin");

        btnSubmitVerification.setOnClickListener(v -> {
            String ownerDetails = etOwnerVerification.getText().toString().trim();
            if (!ownerDetails.isEmpty()) {
                submitOwnerVerification(ownerDetails);
            } else {
                Toast.makeText(this, "Vui lòng nhập thông tin xác nhận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRenterView() {
        etOwnerVerification.setVisibility(View.GONE);
        btnSubmitVerification.setText("Gửi Thông Tin Xác Nhận");

        btnSubmitVerification.setOnClickListener(v -> {
            String renterDetails = etRenterVerification.getText().toString().trim();
            if (!renterDetails.isEmpty()) {
                submitRenterVerification(renterDetails);
            } else {
                Toast.makeText(this, "Vui lòng nhập thông tin xác nhận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitOwnerVerification(String ownerDetails) {
        Map<String, Object> verificationData = new HashMap<>();
        verificationData.put("roomId", room.getIdRoom());
        verificationData.put("ownerId", room.getUserId());
        verificationData.put("ownerVerificationDetails", ownerDetails);
        verificationData.put("status", "PENDING");
        verificationData.put("createdAt", new Date());

        db.collection("verifications")
                .add(verificationData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Thông tin xác nhận đã được gửi", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void submitRenterVerification(String renterDetails) {
        Map<String, Object> verificationData = new HashMap<>();
        verificationData.put("roomId", room.getIdRoom());
        verificationData.put("renterId", mAuth.getCurrentUser().getUid());
        verificationData.put("renterVerificationDetails", renterDetails);
        verificationData.put("status", "PENDING");
        verificationData.put("createdAt", new Date());

        db.collection("verifications")
                .add(verificationData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Thông tin xác nhận đã được gửi", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
