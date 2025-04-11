package com.example.baitap10;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.baitap10.adapter.VideosFireBaseAdapter;
import com.example.baitap10.model.VideoModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VideoShortsFirebaseActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private VideosFireBaseAdapter videosFireBaseAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.video_short);
        viewPager2 = findViewById(R.id.vpager);
        getVideos();

    }

    private void getVideos() {
        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference("videos");
        FirebaseRecyclerOptions<VideoModel> options = new FirebaseRecyclerOptions.Builder<VideoModel>()
                .setQuery(mDataBase, VideoModel.class).build();

        videosFireBaseAdapter = new VideosFireBaseAdapter(options);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(videosFireBaseAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        videosFireBaseAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videosFireBaseAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onStop() {
        super.onStop();
        videosFireBaseAdapter.stopListening();
    }
}
