package com.example.baitap10;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.baitap10.Retrofit.APIService;
import com.example.baitap10.adapter.VideosRetrofitAdapter;
import com.example.baitap10.model.MessageVideoModelRetrofit;
import com.example.baitap10.model.VideoModelRetrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoShortAPI extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private VideosRetrofitAdapter videosAdapter;
    private List<VideoModelRetrofit> videoList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_short);
        viewPager2 = findViewById(R.id.vpager);
        getVideos();


    }

    private void getVideos() {
        APIService.serviceApi.getVideos().enqueue(new Callback<MessageVideoModelRetrofit>()
        {
            @Override
            public void onFailure(Call<MessageVideoModelRetrofit> call, Throwable t) {
                Log.d("RetrofitFailure", "onFailure: " + t.getMessage());
            }

            @Override
            public void onResponse(Call<MessageVideoModelRetrofit> call, Response<MessageVideoModelRetrofit> response) {
                videoList = response.body().getResult();
                videosAdapter = new VideosRetrofitAdapter(VideoShortAPI.this, videoList, false);
                viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
                viewPager2.setAdapter(videosAdapter);
            }
        });
    }
}
