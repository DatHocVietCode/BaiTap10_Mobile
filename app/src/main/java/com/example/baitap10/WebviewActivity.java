package com.example.baitap10;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

public class WebviewActivity extends AppCompatActivity {
    private FirebaseAnalytics firebaseAnalytics;
    private WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        // Khởi tạo Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Gửi một sự kiện
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "app_start");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

        // Cấu hình WebView
        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient());
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


        // Tải trang web responsive
        webView.loadUrl("http://iotstar.vn/Account/Login"); // Thay bằng URL của bạn
    }
}
