package com.example.baitap10;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
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
import com.example.baitap10.model.VideoModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadVideoActivity extends AppCompatActivity {
    Button btn_uploadVideo, btn_chooseVideo;
    CircleImageView videoThumbnailView;
    ImageButton btn_back;
    private Uri mVideoUri;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video); // nếu có layout riêng thì sửa lại

        AnhXa();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Uploading video...");

        btn_chooseVideo.setOnClickListener(v -> CheckPermission());
        btn_uploadVideo.setOnClickListener(v -> uploadVideo());
    }

    private void AnhXa() {
        btn_uploadVideo = findViewById(R.id.btn_uploadVideo);
        btn_chooseVideo = findViewById(R.id.btn_chooseVideo);
        videoThumbnailView = findViewById(R.id.cmv_upload_video);
        btn_back = findViewById(R.id.uploadImage_btn_back);

        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("isUpdated", true);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openVideoGallery();
            return;
        }
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openVideoGallery();
        } else {
            requestPermissions(permissions(), MY_REQUEST_CODE);
        }
    }

    private void openVideoGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Video"));
    }

    private final ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data == null) return;

                    mVideoUri = data.getData();

                    // Lấy path thực
                    String realPath = getRealPathFromURI(mVideoUri);
                    if (realPath != null) {
                        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
                                realPath,
                                MediaStore.Images.Thumbnails.MINI_KIND
                        );
                        if (thumbnail != null) {
                            videoThumbnailView.setImageBitmap(thumbnail);
                        } else {
                            Toast.makeText(this, "Không thể tạo thumbnail", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    private void uploadVideo() {
        if (mVideoUri == null) {
            Toast.makeText(this, "Vui lòng chọn video trước", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgressDialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Tạo tên file duy nhất từ UID và thời gian hiện tại
        String fileName = uid + "_" + System.currentTimeMillis() + "_video.mp4";

        String videoTitle = "Video: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                + " - " + fileName;
        String videoDescription = "Mô tả video" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                + " - " + fileName; ;



        FirebaseStorage.getInstance().getReference("videos/" + fileName)
                .putFile(mVideoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                        String videoUrl = uri.toString();

                        // Lấy thông tin người dùng từ Firebase Realtime Database
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(uid)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DataSnapshot snapshot = task.getResult();
                                        if (snapshot.exists()) {
                                            // Lấy thông tin avatarUrl và các thông tin khác
                                            String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                                            String email = snapshot.child("email").getValue(String.class);
                                            String username = snapshot.child("displayName").getValue(String.class);

                                            // Tạo đối tượng VideoModel
                                            VideoModel video = new VideoModel(
                                                    videoTitle, // title: lấy từ videoTitle
                                                    videoDescription, // description: lấy từ videoDescription
                                                    avatarUrl, // avatar
                                                    videoUrl, // URL video
                                                    new HashMap<>(), // likes (sẽ thêm khi có người thích)
                                                    new HashMap<>(), // dislikes (sẽ thêm khi có người không thích)
                                                    email, // email người dùng
                                                    username, // username người dùng
                                                    uid // owner (UID của người dùng)
                                            );

                                            // Lưu video vào Firebase Realtime Database
                                            FirebaseDatabase.getInstance().getReference("videos")
                                                    .push()
                                                    .setValue(video)
                                                    .addOnCompleteListener(task1 -> {
                                                        mProgressDialog.dismiss();
                                                        if (task1.isSuccessful()) {
                                                            Toast.makeText(this, "Upload video thành công!", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(this, "Lỗi cập nhật video vào Realtime Database", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            mProgressDialog.dismiss();
                                            Toast.makeText(this, "Thông tin người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(this, "Lỗi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(this, "Upload thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




    // Permission
    public static final int MY_REQUEST_CODE = 100;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_VIDEO
    };

    public static String[] storge_permissions = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static String[] permissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? storge_permissions_33
                : storge_permissions;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openVideoGallery();
            }
        }
    }
}

