package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DepositActivity extends AppCompatActivity {
    private Room room;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextView tvRoomName, tvDepositAmount;
    private EditText etTransactionDetails;
    private Spinner spinnerPaymentMethod;
    private Button btnSubmitDeposit;

    private String selectedPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Retrieve the room from intent
        room = (Room) getIntent().getSerializableExtra("room");

        // Initialize views
        tvRoomName = findViewById(R.id.tvRoomName);
        tvDepositAmount = findViewById(R.id.tvDepositAmount);
        etTransactionDetails = findViewById(R.id.etTransactionDetails);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        btnSubmitDeposit = findViewById(R.id.btnSubmitDeposit);

        // Set room name and calculate deposit amount (e.g., 1 month's rent)
        if (room != null) {
            tvRoomName.setText(room.getRoomName());

            // Parse price and calculate deposit (assuming price is a string with $ sign)
            try {
                double monthlyRent = Double.parseDouble(room.getPrice().replace("$", "").trim());
                tvDepositAmount.setText(String.format("$%.2f", monthlyRent));
            } catch (NumberFormatException e) {
                tvDepositAmount.setText("Unable to calculate deposit");
            }
        }

        // Setup payment method spinner
        setupPaymentMethodSpinner();

        // Submit deposit button
        btnSubmitDeposit.setOnClickListener(v -> submitDeposit());
    }

    private void setupPaymentMethodSpinner() {
        String[] paymentMethods = {"Bank Transfer", "Credit Card", "Digital Wallet"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paymentMethods
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(adapter);

        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPaymentMethod = paymentMethods[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPaymentMethod = null;
            }
        });
    }

    private void submitDeposit() {
        // Validate input
        String transactionDetails = etTransactionDetails.getText().toString().trim();

        if (selectedPaymentMethod == null) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        if (transactionDetails.isEmpty()) {
            Toast.makeText(this, "Please enter transaction details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create deposit object
        try {
            double depositAmount = Double.parseDouble(room.getPrice().replace("$", "").trim());

            Map<String, Object> depositData = new HashMap<>();
            depositData.put("roomId", room.getIdRoom());
            depositData.put("ownerId", room.getUserId());
            depositData.put("renterId", mAuth.getCurrentUser().getUid());
            depositData.put("depositAmount", depositAmount);
            depositData.put("status", "PENDING");
            depositData.put("createdAt", new Date());
            depositData.put("paymentMethod", selectedPaymentMethod);
            depositData.put("transactionDetails", transactionDetails);

            // Save deposit to Firestore
            db.collection("deposits")
                    .add(depositData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Deposit submitted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error submitting deposit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid deposit amount", Toast.LENGTH_SHORT).show();
        }
    }
}