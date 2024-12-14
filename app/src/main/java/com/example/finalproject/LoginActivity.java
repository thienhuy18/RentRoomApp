package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnSignup = findViewById(R.id.btnSignup);

        // Xử lý nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    db.collection("users").document(user.getUid())
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    String role = documentSnapshot.getString("role");
                                                    navigateToRoleActivity(role);
                                                } else {
                                                    Toast.makeText(this, "Không tìm thấy vai trò người dùng", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi truy xuất dữ liệu", Toast.LENGTH_SHORT).show());
                                }
                            } else {
                                Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Đăng ký
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    // Hiển thị hộp thoại chọn vai trò
    private void showRoleSelectionDialog(OnRoleSelectedListener listener) {
        String[] roles = {"Chủ Nhà", "Người Thuê"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn vai trò")
                .setItems(roles, (dialog, which) -> {
                    String selectedRole = roles[which];
                    listener.onRoleSelected(selectedRole);
                })
                .setCancelable(false)
                .show();
    }

    // Điều hướng đến màn hình tương ứng
    private void navigateToRoleActivity(String role) {
        if ("Chủ Nhà".equals(role)) {
            // Chuyển đến màn hình cho thuê phòng (HostActivity)
            Intent intent = new Intent(this, HostActivity.class);
            startActivity(intent);
            finish();  // Dừng Activity đăng nhập hiện tại
        } else if ("Người Thuê".equals(role)) {
            // Chuyển đến màn hình tìm kiếm phòng (RenterActivity)
            Intent intent = new Intent(this, RenterActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Vai trò không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }


    interface OnRoleSelectedListener {
        void onRoleSelected(String role);
    }
}
