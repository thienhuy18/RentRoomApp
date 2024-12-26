package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HostActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvWelcome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_host);
        tvWelcome = findViewById(R.id.tvWelcome);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name");
                        tvWelcome.setText("Xin chào, " + userName);
                    } else {
                        tvWelcome.setText("Xin chào, User");
                    }
                } else {
                    tvWelcome.setText("Xin chào, User");
                    Toast.makeText(HostActivity.this, "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvWelcome.setText("Xin chào, User");
        }

        Button btnPostRoom = findViewById(R.id.btnPostRoom);
        btnPostRoom.setOnClickListener(v -> {
            // Khi nhấn nút, chuyển sang màn hình đăng tin cho thuê phòng
            Intent intent = new Intent(HostActivity.this, AddRoomActivity.class);  // Đến màn hình đăng phòng
            startActivity(intent);
        });

        Button btnManageRooms = findViewById(R.id.btnManageRooms);  // Button quản lý các phòng đã đăng
        btnManageRooms.setOnClickListener(v -> {
            // Chuyển đến màn hình quản lý phòng
            Intent intent = new Intent(HostActivity.this, ManageRoomsActivity.class);  // Màn hình quản lý các phòng
            startActivity(intent);
        });

        ImageButton btnChatList = findViewById(R.id.btnChatList);
        btnChatList.setOnClickListener(v -> {
            Intent intent = new Intent(HostActivity.this, ChatListActivity.class);
            intent.putExtra("currentUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);

        });
        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HostActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
