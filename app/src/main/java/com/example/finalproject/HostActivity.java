package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
}
