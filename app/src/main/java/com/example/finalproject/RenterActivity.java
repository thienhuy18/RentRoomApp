package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RenterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter);

        tvWelcome = findViewById(R.id.tvWelcome);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

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
                    Toast.makeText(RenterActivity.this, "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvWelcome.setText("Xin chào, User");
        }

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RenterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        Button btnSavedRooms = findViewById(R.id.btnSavedRooms);
        btnSavedRooms.setOnClickListener(v -> {
            Intent intent = new Intent(RenterActivity.this, SavedRoomsActivity.class);
            startActivity(intent);
        });

        Button btnSearchRoom = findViewById(R.id.btnSearchRoom);
        btnSearchRoom.setOnClickListener(v -> {
            Intent intent = new Intent(RenterActivity.this, SearchRoomActivity.class);
            startActivity(intent);
        });

        ImageButton btnChatList = findViewById(R.id.btnChatList);
        btnChatList.setOnClickListener(v -> {
            Intent intent = new Intent(RenterActivity.this, ChatListActivity.class);
            intent.putExtra("currentUserId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList, this);
        recyclerView.setAdapter(roomAdapter);

        loadRooms();
    }

    private void loadRooms() {
        db.collection("rooms")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        roomList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Room room = document.toObject(Room.class);
                                roomList.add(room);
                            }
                            roomAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
    }
}
