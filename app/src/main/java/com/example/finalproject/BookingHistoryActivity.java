package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String roomId;
    private RecyclerView rvBookingHistory;
    private BookingHistoryAdapter bookingHistoryAdapter;
    private List<String> bookingDatesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        db = FirebaseFirestore.getInstance();
        roomId = getIntent().getStringExtra("roomId");

        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        bookingDatesList = new ArrayList<>();
        bookingHistoryAdapter = new BookingHistoryAdapter(bookingDatesList);

        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(bookingHistoryAdapter);

        fetchBookingHistory();
    }

    private void fetchBookingHistory() {
        db.collection("rooms")
                .document(roomId)  // Use the roomId that you're currently working with
                .collection("bookings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookingDatesList.clear();  // Clear the list before adding new data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookingDate = document.getId(); // The document ID (bookingDate)
                            String userId = document.getString("userID"); // Fetch the userId

                            if (userId != null && !userId.isEmpty()) {
                                // Fetch the user's name from the users collection using the userId
                                db.collection("users")
                                        .document(userId)  // Access the user's document
                                        .get()
                                        .addOnCompleteListener(userTask -> {
                                            if (userTask.isSuccessful()) {
                                                DocumentSnapshot userDocument = userTask.getResult();
                                                if (userDocument.exists()) {
                                                    String userName = userDocument.getString("name");  // Assuming 'name' field exists

                                                    if (userName != null && !userName.isEmpty()) {
                                                        // If userName is found, display the booking date with the userName
                                                        bookingDatesList.add("Booking Date: " + bookingDate + " | User: " + userName);
                                                    } else {
                                                        Log.d("BookingHistory", "Missing userName for userId: " + userId);
                                                    }
                                                }
                                            } else {
                                                Log.d("BookingHistory", "Error fetching user data for userId: " + userId);
                                            }
                                            // Notify the adapter after all user data is fetched
                                            bookingHistoryAdapter.notifyDataSetChanged();
                                        });
                            } else {
                                Log.d("BookingHistory", "Missing userId for booking: " + bookingDate);
                            }
                        }
                    } else {
                        Toast.makeText(BookingHistoryActivity.this, "Failed to fetch booking history.", Toast.LENGTH_SHORT).show();
                    }
                });
    }





}
