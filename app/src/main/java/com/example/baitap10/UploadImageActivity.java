package com.example.baitap10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadImageActivity extends AppCompatActivity {
    Button btn_uploadImg, btn_chooseImg;
    CircleImageView imageViewChoose;
    ImageButton btn_back;
    private Uri mUri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        AnhXa();
        mProgressDialog = new ProgressDialog(UploadImageActivity.this);
        mProgressDialog.setMessage("Please wait upload....");
        //bắt sự kiện nút chọn ảnh
        btn_chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }
        });
        btn_uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImage();
            }
        });
    }
    private void AnhXa()
    {
        btn_uploadImg = findViewById(R.id.btn_uploadImg);
        btn_chooseImg = findViewById(R.id.btn_chooseImg);
        imageViewChoose = findViewById(R.id.cmv_upload_avatar);
        btn_back = findViewById(R.id.uploadImage_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("isUpdated", true);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        loadUserAvatar();
    }
    private void loadUserAvatar()
    {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatar = snapshot.child("avatarUrl").getValue(String.class);
                // Load ảnh bằng Glide
                Glide.with(UploadImageActivity.this)
                        .load(avatar)
                        .placeholder(R.drawable.user) // ảnh mặc định nếu null hoặc lỗi
                        .error(R.drawable.user) // ảnh nếu có lỗi khi load
                        .into(imageViewChoose);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadImageActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private ProgressDialog mProgressDialog;
    public static final int MY_REQUEST_CODE=100;
    public static final String TAG = MainActivity.class.getName();
    public static String[] storge_permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };
    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }
    private void CheckPermission() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            openGallery();
            return;
        }
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else{
            //ActivityCompat.requestPermissions(UploadImageActivity.this,permissions(),MY_REQUEST_CODE);
            //hoặc
            requestPermissions(permissions(),MY_REQUEST_CODE);
        }
    }
    //Hàm xử lý lấy ảnh
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }
    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e("logd",  "onActivityResult");
                    if(result.getResultCode()== Activity.RESULT_OK){
                        //request code
                        Intent data = result.getData();
                        if(data==null){
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            imageViewChoose.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    public void UploadImage() {
        if (mUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.show();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String fileName = uid + "_avatar.jpg";

        FirebaseStorage.getInstance().getReference("avatars/" + fileName)
                .putFile(mUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        String avatarUrl = uri.toString();

                        // Gán avatarUrl vào đúng UID trong Users
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .child("avatarUrl")
                                .setValue(avatarUrl)
                                .addOnCompleteListener(task -> {
                                    mProgressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UploadImageActivity.this, "Cập nhật avatar thành công!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(UploadImageActivity.this, "Lỗi cập nhật avatarUrl", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(this, "Lỗi upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "UploadImage: " + e.getMessage());
                });
    }

}
