package com.example.baitap10;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {
    ImageButton btn_back;
    Button btn_editInformation, btn_uploadVideo;
    TextView tv_numVideo, tv_name, tv_email, tv_password, tv_id;
    CircleImageView user_avatar;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    boolean isUpdated = result.getData().getBooleanExtra("isUpdated", false);
                    if (isUpdated) {
                        // Reload lại dữ liệu
                        loadUserData();
                    }
                }
            }
    );
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AnhXa();
    }


    private void AnhXa() {
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> finish());

        tv_numVideo = findViewById(R.id.tv_numVideo);
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_password = findViewById(R.id.tv_password);
        tv_id = findViewById(R.id.tv_id);


        btn_editInformation = findViewById(R.id.btn_editInformation);
        btn_editInformation.setOnClickListener(view -> openEditDialog());
        btn_uploadVideo = findViewById(R.id.btn_openUploadVideo);
        btn_uploadVideo.setOnClickListener(view -> {
            Intent intent = new Intent(MyProfileActivity.this, UploadVideoActivity.class);
            launcher.launch(intent); // Thay vì startActivity
        });

        user_avatar = findViewById(R.id.user_avatar);
        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProfileActivity.this, UploadImageActivity.class);
                launcher.launch(intent); // Thay vì startActivity
            }
        });

        loadUserData();
        countUserVideos();
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            tv_id.setText(uid);

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("displayName").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);
                    String avatar = snapshot.child("avatarUrl").getValue(String.class);


                    tv_name.setText(name);
                    tv_email.setText(email);
                    tv_password.setText(password);
                    // Load ảnh bằng Glide
                    Glide.with(MyProfileActivity.this)
                            .load(avatar)
                            .placeholder(R.drawable.user) // ảnh mặc định nếu null hoặc lỗi
                            .error(R.drawable.user) // ảnh nếu có lỗi khi load
                            .into(user_avatar);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MyProfileActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void countUserVideos() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference("videos");
            videoRef.orderByChild("owner").equalTo(user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long count = snapshot.getChildrenCount();
                            tv_numVideo.setText("Đã đăng " + count + " video");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("VideoCount", "Lỗi truy vấn: " + error.getMessage());
                        }
                    });
        }
    }

    private void openEditDialog() {
        Dialog dialog = new Dialog(MyProfileActivity.this);
        dialog.setContentView(R.layout.dialog_update_information);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etName = dialog.findViewById(R.id.etName);
        EditText etEmail = dialog.findViewById(R.id.etEmail);
        EditText etPassword = dialog.findViewById(R.id.etPassword);
        Button btnSaveChanges = dialog.findViewById(R.id.btnSaveChanges);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Gán dữ liệu hiện tại vào các EditText
        etName.setText(tv_name.getText().toString());
        etEmail.setText(tv_email.getText().toString());
        etPassword.setText(tv_password.getText().toString());

        btnSaveChanges.setOnClickListener(view -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                // Cập nhật name
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();
                user.updateProfile(profileUpdates);

                // Cập nhật email
                if (!email.isEmpty() && !email.equals(user.getEmail())) {
                    user.updateEmail(email);
                }

                // Cập nhật password
                if (!password.isEmpty()) {
                    user.updatePassword(password);
                }

                // Lưu lại vào node "Users"
                Map<String, Object> updates = new HashMap<>();
                updates.put("displayName", name);
                updates.put("email", email);
                updates.put("password", password);
                userRef.updateChildren(updates);

                loadUserData(); // Cập nhật lại UI
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }
}
