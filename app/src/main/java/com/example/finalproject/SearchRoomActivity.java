package com.example.finalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchRoomActivity extends AppCompatActivity {
    private RecyclerView rvSearchResults;
    private Button btnSearch;
    private EditText etSearchLocation, etSearchPrice, etSearchArea, etSearchAmenities;
    private FirebaseFirestore db;
    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        db = FirebaseFirestore.getInstance();
        rvSearchResults = findViewById(R.id.rvSearchResults);
        btnSearch = findViewById(R.id.btnSearch);
        etSearchLocation = findViewById(R.id.etSearchLocation);
        etSearchPrice = findViewById(R.id.etSearchPrice);
        etSearchArea = findViewById(R.id.etSearchArea);
        etSearchAmenities = findViewById(R.id.etSearchAmenities);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));

        roomAdapter = new RoomAdapter(new ArrayList<>(), this);
        rvSearchResults.setAdapter(roomAdapter);

        btnSearch.setOnClickListener(v -> searchRooms());
    }

    private void searchRooms() {
        String location = etSearchLocation.getText().toString().trim();
        String price = etSearchPrice.getText().toString().trim();
        String area = etSearchArea.getText().toString().trim();
        String amenities = etSearchAmenities.getText().toString().trim();

        if (location.isEmpty() && price.isEmpty() && area.isEmpty() && amenities.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập ít nhất một tiêu chí tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = db.collection("rooms");

        if (!location.isEmpty()) {
            query = query.whereEqualTo("address", location);
        }
        if (!price.isEmpty()) {
            query = query.whereEqualTo("price", price);
        }
        if (!area.isEmpty()) {
            query = query.whereEqualTo("area", area);
        }
        if (!amenities.isEmpty()) {
            query = query.whereArrayContains("amenities", amenities);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Room> roomList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Room room = documentSnapshot.toObject(Room.class);
                        if (room != null) {
                            roomList.add(room);
                        }
                    }
                    roomAdapter.setRoomList(roomList);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi tìm kiếm phòng", Toast.LENGTH_SHORT).show());
    }
}
