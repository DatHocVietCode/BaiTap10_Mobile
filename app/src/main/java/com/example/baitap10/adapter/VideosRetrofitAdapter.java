package com.example.baitap10.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baitap10.R;
import com.example.baitap10.model.VideoModelRetrofit;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

public class VideosRetrofitAdapter extends RecyclerView.Adapter<VideosRetrofitAdapter.MyViewHolder> {
    private Context context;
    private List<VideoModelRetrofit> videoList;
    private boolean isFav;

    public VideosRetrofitAdapter(Context context, List<VideoModelRetrofit> videoList, boolean isFav) {
        this.context = context;
        this.videoList = videoList;
        this.isFav = isFav;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<VideoModelRetrofit> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoModelRetrofit> videoList) {
        this.videoList = videoList;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    @NonNull
    @Override
    public VideosRetrofitAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_row_retrofit, parent, false);
        return new VideosRetrofitAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosRetrofitAdapter.MyViewHolder holder, int position) {
        VideoModelRetrofit video = videoList.get(position);
        holder.textVideoTitle.setText(video.getTitle());
        holder.textVideoDescription.setText(video.getDescription());
        // Trích xuất videoId
        String videoUrl = video.getUrl();
        String videoId = extractYoutubeId(videoUrl);

        holder.videoView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                holder.videoProgressBar.setVisibility(View.GONE); // Ẩn progress bar khi đã sẵn sàng
                youTubePlayer.loadVideo(videoId, 0); // Auto-play từ đầu
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState state) {
                if (state == PlayerConstants.PlayerState.ENDED) {
                    youTubePlayer.loadVideo(videoId, 0); // Lặp lại video khi kết thúc
                }
            }
        });
        holder.imgFavorite.setTag(isFav); // mặc định là chưa yêu thích
        holder.imgFavorite.setOnClickListener(view -> {
            if (!isFav) {
                holder.imgFavorite.setImageResource(R.drawable.baseline_thumb_up_alt_24);
                isFav = true;
            } else {
                holder.imgFavorite.setImageResource(R.drawable.baseline_thumb_up_off_alt_24);
                isFav = false;
            }

            view.setTag(isFav); // cập nhật trạng thái mới
        });

    }
    private String extractYoutubeId(String url) {
        Uri uri = Uri.parse(url);
        return uri.getQueryParameter("v"); // Trích tham số ?v=
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private YouTubePlayerView videoView;
        private ProgressBar videoProgressBar;
        private TextView textVideoTitle;
        private TextView textVideoDescription;
        private ImageView imgPerson, imgFavorite, imgShare, imgMore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView_youtube);
            videoProgressBar = itemView.findViewById(R.id.videoProgressBar_youtube);

            textVideoTitle = itemView.findViewById(R.id.textVideoTitle_youtube);
            textVideoDescription = itemView.findViewById(R.id.textVideoDescription_youtube);
            imgPerson = itemView.findViewById(R.id.img_avatarUploadedUser_youtube);

            imgFavorite = itemView.findViewById(R.id.imgLike_youtube);
            imgShare = itemView.findViewById(R.id.imgShare_youtube);
            imgMore = itemView.findViewById(R.id.imgMore_youtube);

        }
    }
}
