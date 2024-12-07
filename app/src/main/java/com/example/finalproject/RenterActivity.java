package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renter);

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RenterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        });
        // Initialize the search button
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

        // Initialize Firestore and RecyclerView
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewRooms);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns
        roomList = new ArrayList<>();
        roomAdapter = new RoomAdapter(roomList, this);
        recyclerView.setAdapter(roomAdapter);

        // Load room data from Firestore
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
        // Reload the room data from Firestore to refresh UI
        loadRooms();
    }


}
