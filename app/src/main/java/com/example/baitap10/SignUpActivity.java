package com.example.baitap10;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText etEmail, etPassword, etRePassword;
    MaterialButton btnSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Anhxa();
    }

    private void Anhxa() {
        etEmail = findViewById(R.id.signup_et_email);
        etPassword = findViewById(R.id.signup_et_password);
        etRePassword = findViewById(R.id.signup_et_repassword);
        btnSignUp = findViewById(R.id.btn_signup);

        btnSignUp.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String repassword = etRePassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(repassword)) {
                Toast.makeText(SignUpActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();

                                // Gán tên mặc định là phần trước @ trong email
                                String defaultName = email.split("@")[0];

                                // Tạo dữ liệu user trong bảng "Users"
                                DatabaseReference userRef = FirebaseDatabase.getInstance()
                                        .getReference("Users").child(uid);

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", email);
                                userData.put("displayName", defaultName);
                                userData.put("avatarUrl", ""); // Chưa có ảnh thì để trống

                                userRef.setValue(userData)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                                finish(); // Quay lại màn login
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "Lỗi khi lưu user: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

}
