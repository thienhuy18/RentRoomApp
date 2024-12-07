package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class SavedRoomsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedRoomsAdapter savedRoomsAdapter;
    private List<Room> savedRoomList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_rooms);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewSavedRooms);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        savedRoomList = new ArrayList<>();
        savedRoomsAdapter = new SavedRoomsAdapter(savedRoomList, this);
        recyclerView.setAdapter(savedRoomsAdapter);

        loadSavedRooms();
    }

    private void loadSavedRooms() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("savedRooms")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        savedRoomList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Room room = document.toObject(Room.class);
                            savedRoomList.add(room);
                        }
                        savedRoomsAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Đã xảy ra lỗi khi tải danh sách phòng.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

