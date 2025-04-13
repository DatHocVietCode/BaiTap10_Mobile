package com.example.baitap10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button btn_baitap1, btn_baitap2, btn_baitap3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View mainView = findViewById(R.id.main); // Lấy view có id="main" trong activity_main.xml

        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       /* // Tạo runnable để chuyển sang WebviewActivity
        Runnable goToWebView = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
                startActivity(intent);
            }
        };

        // Gọi runnable sau 2 giây
        mainView.postDelayed(goToWebView, 2000);*/
        AnhXa();
        btn_baitap1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
                //intent.putExtra("url", "https://www.thegioididong.com/");
                startActivity(intent);
            }
        });
        btn_baitap2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                //intent.putExtra("url", "https://www.thegioididong.com/");
                startActivity(intent);

            }
        });
    }
    private void AnhXa()
    {
        btn_baitap1 = (Button) findViewById(R.id.btn_baitap1);
        btn_baitap2 = (Button) findViewById(R.id.btn_baitap2);

    }
}
