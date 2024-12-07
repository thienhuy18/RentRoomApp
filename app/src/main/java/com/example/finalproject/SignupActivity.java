package com.example.finalproject;


import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etName = findViewById(R.id.etName);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        RadioGroup rgRole = findViewById(R.id.rgRole);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            int selectedRoleId = rgRole.getCheckedRadioButtonId();
            RadioButton selectedRole = findViewById(selectedRoleId);
            String role = selectedRole != null ? selectedRole.getText().toString() : "";

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("name", name);
                            userData.put("pass", password);
                            userData.put("email", email);
                            userData.put("role", role);

                            db.collection("users").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                        // Chuyển hướng về màn hình đăng nhập
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu thông tin", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
