package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ManageRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ManageRoomAdapter manageRoomAdapter;
    private List<Room> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        recyclerView = findViewById(R.id.recyclerViewManageRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        roomList = new ArrayList<>();
        manageRoomAdapter = new ManageRoomAdapter(roomList, this, new ManageRoomAdapter.OnRoomActionListener() {
            @Override
            public void onEdit(Room room) {
                // Chỉnh sửa phòng
                Intent editIntent = new Intent(ManageRoomsActivity.this, EditRoomActivity.class);
                editIntent.putExtra("ROOM_ID", room.getIdRoom());
                startActivityForResult(editIntent, 1001);
            }

            @Override
            public void onDelete(Room room) {
                // Xóa phòng
                deleteRoom(room);
            }
        });

        recyclerView.setAdapter(manageRoomAdapter);

        loadRooms();
        Button btnAddRoom = findViewById(R.id.btnAddRoom);
        btnAddRoom.setOnClickListener(v -> {
            Intent intent = new Intent(ManageRoomsActivity.this, AddRoomActivity.class);
            startActivityForResult(intent, 1001); // Gọi AddRoomActivity và yêu cầu trả lại kết quả
        });

    }

    private void loadRooms() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("rooms")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            Room room = document.toObject(Room.class);
                            roomList.add(room);
                        }
                        manageRoomAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ManageRoomsActivity.this, "Chưa có phòng nào đăng", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(ManageRoomsActivity.this, "Lỗi tải phòng", Toast.LENGTH_SHORT).show());
    }

    private void deleteRoom(Room room) {
        db.collection("rooms")
                .document(room.getIdRoom())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    roomList.remove(room);
                    manageRoomAdapter.notifyDataSetChanged();
                    Toast.makeText(ManageRoomsActivity.this, "Phòng đã được xóa", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(ManageRoomsActivity.this, "Lỗi xóa phòng", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            roomList.clear();
            loadRooms();
        }
    }
}
