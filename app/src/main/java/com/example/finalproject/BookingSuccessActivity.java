package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BookingSuccessActivity extends AppCompatActivity {
    private TextView tvBookingInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_success);

        tvBookingInfo = findViewById(R.id.tvBookingInfo);

        // Retrieve the data passed from DatePickerActivity
        Intent intent = getIntent();
        String bookedDate = intent.getStringExtra("bookedDate");


        // Display booking info
        tvBookingInfo.setText("Room booked for: " + bookedDate );


    }
}
