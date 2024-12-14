package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HostActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_host);  // Layout cho màn hình chủ nhà

        Button btnPostRoom = findViewById(R.id.btnPostRoom); // Button cho phép đăng tin cho thuê phòng
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
        Button btnChat = findViewById(R.id.btnChatList);
        Button btnChatList = findViewById(R.id.btnChatList);
        btnChatList.setOnClickListener(v -> {
            Intent intent = new Intent(HostActivity.this, ChatListActivity.class);
            intent.putExtra("currentUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);

        });
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HostActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        });

    }
}
