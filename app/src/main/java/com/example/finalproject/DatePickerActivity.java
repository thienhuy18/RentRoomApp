package com.example.finalproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DatePickerActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String roomId;
    private String userId;  // Store user ID
    private RecyclerView rvBookedDates;
    private BookedDatesAdapter bookedDatesAdapter;
    private List<String> bookedDatesList;
    private String roomAddress = "Room Address Placeholder";  // Placeholder, fetch from your database if needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        db = FirebaseFirestore.getInstance();
        roomId = getIntent().getStringExtra("roomId");

        // Get userId from Firebase Authentication (or pass it via Intent)
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Assuming user is logged in

        // Initialize RecyclerView and adapter
        rvBookedDates = findViewById(R.id.rvBookedDates);
        bookedDatesList = new ArrayList<>();
        bookedDatesAdapter = new BookedDatesAdapter(bookedDatesList);

        rvBookedDates.setLayoutManager(new LinearLayoutManager(this));
        rvBookedDates.setAdapter(bookedDatesAdapter);

        // Fetch the booked dates from Firestore
        fetchBookedDates();

        // Check if the user already has a booking for the room
        checkExistingBooking();

        // Set listener for the date pick button
        Button btnPickDate = findViewById(R.id.btnPickDate);
        btnPickDate.setOnClickListener(v -> openDatePicker());
    }

    private void fetchBookedDates() {
        // Fetch booked dates from Firestore
        db.collection("rooms")
                .document(roomId)
                .collection("bookings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookedDatesList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bookedDatesList.add(document.getId());  // Add the document ID as the booked date
                        }
                        bookedDatesAdapter.notifyDataSetChanged();  // Notify the adapter to update the UI
                    } else {
                        Toast.makeText(DatePickerActivity.this, "Failed to fetch booked dates.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkExistingBooking() {
        // Check if the user has already booked the room
        db.collection("rooms")
                .document(roomId)
                .collection("bookings")
                .whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() > 0) {
                        // Redirect to the booking success page if already booked
                        Intent successIntent = new Intent(DatePickerActivity.this, BookingSuccessActivity.class);
                        successIntent.putExtra("bookedDate", task.getResult().getDocuments().get(0).getId());
                        successIntent.putExtra("roomAddress", roomAddress);  // Pass the address
                        startActivity(successIntent);
                        finish();  // Close the current activity
                    }
                });
    }

    private void openDatePicker() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Open DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);

            // Check if the room is already booked on the selected date
            if (bookedDatesList.contains(selectedDate)) {
                Toast.makeText(DatePickerActivity.this, "This date is already booked!", Toast.LENGTH_SHORT).show();
            } else {
                // If the date is free, proceed to book the room
                bookRoom(selectedDate);
            }
        }, year, month, day); // Default date

        datePickerDialog.show();
    }

    private String formatDate(int year, int month, int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return dateFormat.format(calendar.getTime());
    }

    private void bookRoom(String selectedDate) {
        HashMap<String, Object> bookingData = new HashMap<>();
        bookingData.put("userID", userId);  // Add userID to the booking data

        // Add booking to Firestore
        db.collection("rooms")
                .document(roomId)
                .collection("bookings")
                .document(selectedDate)
                .set(bookingData) // Book the room by saving a document with the selected date and userID
                .addOnSuccessListener(aVoid -> {
                    // Redirect to the success page after booking
                    Intent successIntent = new Intent(DatePickerActivity.this, BookingSuccessActivity.class);
                    successIntent.putExtra("bookedDate", selectedDate);
                    successIntent.putExtra("roomAddress", roomAddress);  // Pass the address
                    startActivity(successIntent);
                    finish();  // Close the current activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DatePickerActivity.this, "Failed to book the room.", Toast.LENGTH_SHORT).show();
                });
    }
}
