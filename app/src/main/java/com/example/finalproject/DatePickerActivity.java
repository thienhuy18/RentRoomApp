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
    private String userId;
    private RecyclerView rvBookedDates;
    private BookedDatesAdapter bookedDatesAdapter;
    private List<String> bookedDatesList;
    private String roomAddress = "Room Address Placeholder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        db = FirebaseFirestore.getInstance();
        roomId = getIntent().getStringExtra("roomId");

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        rvBookedDates = findViewById(R.id.rvBookedDates);
        bookedDatesList = new ArrayList<>();
        bookedDatesAdapter = new BookedDatesAdapter(bookedDatesList);

        rvBookedDates.setLayoutManager(new LinearLayoutManager(this));
        rvBookedDates.setAdapter(bookedDatesAdapter);

        fetchBookedDates();

        checkExistingBooking();

        Button btnPickDate = findViewById(R.id.btnPickDate);
        btnPickDate.setOnClickListener(v -> openDatePicker());
    }

    private void fetchBookedDates() {

        db.collection("rooms")
                .document(roomId)
                .collection("bookings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookedDatesList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            bookedDatesList.add(document.getId());
                        }
                        bookedDatesAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(DatePickerActivity.this, "Failed to fetch booked dates.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkExistingBooking() {
        db.collection("rooms")
                .document(roomId)
                .collection("bookings")
                .whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().size() > 0) {
                        Intent successIntent = new Intent(DatePickerActivity.this, BookingSuccessActivity.class);
                        successIntent.putExtra("bookedDate", task.getResult().getDocuments().get(0).getId());
                        successIntent.putExtra("roomAddress", roomAddress);
                        startActivity(successIntent);
                        finish();
                    }
                });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = formatDate(selectedYear, selectedMonth, selectedDay);

            if (bookedDatesList.contains(selectedDate)) {
                Toast.makeText(DatePickerActivity.this, "This date is already booked!", Toast.LENGTH_SHORT).show();
            } else {
                bookRoom(selectedDate);
            }
        }, year, month, day);

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
        bookingData.put("userID", userId);

        // Add booking to Firestore
        db.collection("rooms")
                .document(roomId)
                .collection("bookings")
                .document(selectedDate)
                .set(bookingData)
                .addOnSuccessListener(aVoid -> {
                    Intent successIntent = new Intent(DatePickerActivity.this, BookingSuccessActivity.class);
                    successIntent.putExtra("bookedDate", selectedDate);
                    successIntent.putExtra("roomAddress", roomAddress);
                    startActivity(successIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DatePickerActivity.this, "Failed to book the room.", Toast.LENGTH_SHORT).show();
                });
    }
}
