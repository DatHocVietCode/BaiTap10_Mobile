package com.example.baitap10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    MaterialButton btn_login;
    EditText et_email, et_password;
    ImageButton btn_facebookLogin, btn_googleLogin;
    private GoogleSignInClient googleSignInClient;
    private TextView tv_signup;
    private final ActivityResultLauncher<Intent> googleLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        Toast.makeText(this, "Google Sign-In thất bại", Toast.LENGTH_SHORT).show();
                        Log.e("GoogleSignIn", "Mã lỗi: " + e.getStatusCode());
                    }
                }
                else {
                    Intent data = result.getData();
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            for (String key : extras.keySet()) {
                                Object value = extras.get(key);
                                Log.d("GoogleSignInDebug", "Key: " + key + " | Value: " + value);
                            }
                        } else {
                            Log.d("GoogleSignInDebug", "Intent data không có extras.");
                        }
                    } else {
                        Log.d("GoogleSignInDebug", "Intent data = null.");
                    }

                    Log.d("GoogleSignInDebug", "Google Sign-In thất bại - resultCode: " + result.getResultCode());
                    Toast.makeText(this, "Google Sign-In thất bại. resultCode: " + result.getResultCode(), Toast.LENGTH_LONG).show();
                }

            }
    );
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        AnhXa();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();
                                    String emailUser = user.getEmail();
                                    String name = user.getDisplayName(); // Có thể null nếu user chưa đặt
                                    String photoUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "Chưa có ảnh";

                                    Log.d("UserInfo", "UID: " + uid);
                                    Log.d("UserInfo", "Email: " + emailUser);
                                    Log.d("UserInfo", "Display Name: " + name);
                                    Log.d("UserInfo", "Photo URL: " + photoUrl);
                                }
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, VideoShortsFirebaseActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                Log.d("errorr", "onClick: Failed" + task.getException().getMessage());
                            }
                        });

            }
        });
        btn_googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                googleLauncher.launch(signInIntent);
            }
        });
        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    private void AnhXa() {
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        btn_googleLogin = findViewById(R.id.btn_googleLogin);
        tv_signup = findViewById(R.id.tv_signup);
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String emailUser = user.getEmail();
                            String name = user.getDisplayName();
                            String photoUrl = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "Chưa có ảnh";

                            Log.d("UserInfo", "UID: " + uid);
                            Log.d("UserInfo", "Email: " + emailUser);
                            Log.d("UserInfo", "Display Name: " + name);
                            Log.d("UserInfo", "Photo URL: " + photoUrl);
                        }
                        Toast.makeText(LoginActivity.this, "Google đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, VideoShortsFirebaseActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Xác thực với Firebase thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
