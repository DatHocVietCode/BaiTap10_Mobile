package com.example.baitap10.adapter;

import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baitap10.R;
import com.example.baitap10.model.VideoModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class VideosFireBaseAdapter extends FirebaseRecyclerAdapter<VideoModel, VideosFireBaseAdapter.MyHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    //public boolean isFav = false;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = currentUser != null ? currentUser.getUid() : null;

    public VideosFireBaseAdapter(@NonNull FirebaseRecyclerOptions<VideoModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyHolder holder, int position, @NonNull VideoModel model) {
        holder.textVideoTitle.setText(model.getTitle());
        holder.textVideoDescription.setText(model.getDecs());
        holder.videoView.setVideoPath(model.getUrl());
        Glide.with(holder.itemView.getContext())
                .load(model.getAvatar()) // Hoặc user.getAvatar() tuỳ ông lưu ở đâu
                .circleCrop()
                .placeholder(R.drawable.baseline_person_pin_24) // Ảnh mặc định nếu chưa có avatar
                .into(holder.imgUploadedAvatar);
        holder.videoView.setOnPreparedListener(mp -> {
            holder.videoProgressBar.setVisibility(View.GONE);
            mp.start();
            float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
            float screenRatio = holder.videoView.getWidth() / (float) holder.videoView.getHeight();
            float scale = videoRatio / screenRatio;
            if (scale >= 1f) {
                holder.videoView.setScaleX(scale);
            } else {
                holder.videoView.setScaleY(1f / scale);
            }
            holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.start();
                }
            });
        });

        DatabaseReference videoRef = FirebaseDatabase.getInstance()
                .getReference("videos") // hoặc path của ông
                .child(getRef(position).getKey());  // giả sử ông có `getId()`, nếu không thì `getKey()` từ snapshot

        // Hiển thị số lượng và icon ban đầu
        if (model.getLikes() != null) {
            holder.tv_numLikes.setText(String.valueOf(model.getLikes().size()));
        } else {
            holder.tv_numLikes.setText("0");
        }

        if (model.getDislikes() != null) {
            holder.tv_numDislikes.setText(String.valueOf(model.getDislikes().size()));
        } else {
            holder.tv_numDislikes.setText("0");
        }

        // Set icon ban đầu cho người dùng hiện tại
        if (currentUserId != null) {
            if (model.getLikes() != null && model.getLikes().containsKey(currentUserId)) {
                LikeVideo(holder);
            } else if (model.getDislikes() != null && model.getDislikes().containsKey(currentUserId)) {
                DislikeVideo(holder);
            } else {
                UnlikeVideo(holder);
            }
        }

        // Xử lý Like
        holder.imgLikes.setOnClickListener(view -> {
            if (currentUserId == null) return;

            boolean isLiked = model.getLikes() != null && model.getLikes().containsKey(currentUserId);

            if (isLiked) {
                videoRef.child("likes").child(currentUserId).removeValue().addOnSuccessListener(aVoid -> {
                    UnlikeVideo(holder);
                    int likeCount = model.getLikes().size() - 1;
                    holder.tv_numLikes.setText(String.valueOf(Math.max(0, likeCount)));
                    model.getLikes().remove(currentUserId);
                });
            } else {
                videoRef.child("likes").child(currentUserId).setValue(true).addOnSuccessListener(aVoid -> {
                    LikeVideo(holder);
                    int likeCount = (model.getLikes() != null ? model.getLikes().size() : 0) + 1;
                    holder.tv_numLikes.setText(String.valueOf(likeCount));
                    if (model.getLikes() == null) model.setLikes(new HashMap<>());
                    model.getLikes().put(currentUserId, true);
                });

                if (model.getDislikes() != null && model.getDislikes().containsKey(currentUserId)) {
                    videoRef.child("dislikes").child(currentUserId).removeValue().addOnSuccessListener(aVoid -> {
                        //UnDislikeVideo(holder);
                        int dislikeCount = model.getDislikes().size() - 1;
                        holder.tv_numDislikes.setText(String.valueOf(Math.max(0, dislikeCount)));
                        model.getDislikes().remove(currentUserId);
                    });
                }
            }
        });

        // Xử lý Dislike
        holder.imgDislikes.setOnClickListener(view -> {
            if (currentUserId == null) return;

            boolean isDisliked = model.getDislikes() != null && model.getDislikes().containsKey(currentUserId);

            if (isDisliked) {
                videoRef.child("dislikes").child(currentUserId).removeValue().addOnSuccessListener(aVoid -> {
                    UnDislikeVideo(holder);
                    int dislikeCount = model.getDislikes().size() - 1;
                    holder.tv_numDislikes.setText(String.valueOf(Math.max(0, dislikeCount)));
                    model.getDislikes().remove(currentUserId);
                });
            } else {
                videoRef.child("dislikes").child(currentUserId).setValue(true).addOnSuccessListener(aVoid -> {
                    DislikeVideo(holder);
                    int dislikeCount = (model.getDislikes() != null ? model.getDislikes().size() : 0) + 1;
                    holder.tv_numDislikes.setText(String.valueOf(dislikeCount));
                    if (model.getDislikes() == null) model.setDislikes(new HashMap<>());
                    model.getDislikes().put(currentUserId, true);
                });

                if (model.getLikes() != null && model.getLikes().containsKey(currentUserId)) {
                    videoRef.child("likes").child(currentUserId).removeValue().addOnSuccessListener(aVoid -> {
                        //UnlikeVideo(holder);
                        int likeCount = model.getLikes().size() - 1;
                        holder.tv_numLikes.setText(String.valueOf(Math.max(0, likeCount)));
                        model.getLikes().remove(currentUserId);
                    });
                }
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            // Lấy ảnh real từ tài khoản google oAuth2
            /*if (currentUser != null) {
                Uri photoUrl = currentUser.getPhotoUrl(); // <-- avatar URL ở đây

                if (photoUrl != null) {
                    Glide.with(holder.itemView.getContext())
                            .load(photoUrl.toString())
                            .circleCrop()
                            .placeholder(R.drawable.baseline_person_pin_24)
                            .into(holder.imgPerson);
                } else {
                    // Nếu user không có avatar, hiện placeholder
                    holder.imgPerson.setImageResource(R.drawable.baseline_person_pin_24);
                }
            }*/

            Log.d("FirebaseUser", "UID: " + currentUser.getUid());
            Log.d("FirebaseUser", "Email: " + currentUser.getEmail());
            Log.d("FirebaseUser", "Display Name: " + currentUser.getDisplayName());
            Log.d("FirebaseUser", "Photo URL: " + currentUser.getPhotoUrl());

            // Lấy / tạo ảnh fake từ bảng users trong real time database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
            userRef.child("avatarUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String avatarUrl = snapshot.getValue(String.class);
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(holder.itemView.getContext())
                                .load(avatarUrl)
                                .placeholder(R.drawable.baseline_person_outline_24) // ảnh mặc định nếu chưa có
                                .into(holder.imgPerson);
                    } else {
                        holder.imgPerson.setImageResource(R.drawable.baseline_person_outline_24);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý nếu có lỗi
                }
            });
        }
    }
    private void LikeVideo(MyHolder holder)
    {
        holder.imgLikes.setImageResource(R.drawable.baseline_thumb_up_alt_24);
        holder.imgDislikes.setImageResource(R.drawable.baseline_thumb_down_off_alt_24);
    }
    private void UnlikeVideo(MyHolder holder)
    {
        holder.imgLikes.setImageResource(R.drawable.baseline_thumb_up_off_alt_24);
        holder.imgDislikes.setImageResource(R.drawable.baseline_thumb_down_off_alt_24);
    }
    private void DislikeVideo(MyHolder holder)
    {
        holder.imgLikes.setImageResource(R.drawable.baseline_thumb_up_off_alt_24);
        holder.imgDislikes.setImageResource(R.drawable.baseline_thumb_down_alt_24);
    }
    private void UnDislikeVideo(MyHolder holder)
    {
        holder.imgLikes.setImageResource(R.drawable.baseline_thumb_up_off_alt_24);
        holder.imgDislikes.setImageResource(R.drawable.baseline_thumb_down_off_alt_24);
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row, parent, false);
        return new MyHolder(view);
    }

    public static  class MyHolder extends RecyclerView.ViewHolder {
        private VideoView videoView;
        private ImageView imgLikes, imgDislikes, imgShare, imgMore, imgPerson, imgUploadedAvatar;
        private TextView textVideoTitle, textVideoDescription, tv_numLikes, tv_numDislikes;
        private ProgressBar videoProgressBar;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);

            imgPerson = itemView.findViewById(R.id.imgPerson);
            imgUploadedAvatar = itemView.findViewById(R.id.img_avatarUploadedUser);
            imgLikes = itemView.findViewById(R.id.imgLike);
            imgDislikes = itemView.findViewById(R.id.imgDislike);
            imgShare = itemView.findViewById(R.id.imgShare);
            imgMore = itemView.findViewById(R.id.imgMore);
            tv_numLikes = itemView.findViewById(R.id.tv_numLikes);
            tv_numDislikes = itemView.findViewById(R.id.tv_numDislikes);

            videoProgressBar = itemView.findViewById(R.id.videoProgressBar);
            textVideoDescription = itemView.findViewById(R.id.textVideoDescription);
            textVideoTitle = itemView.findViewById(R.id.textVideoTitle);
        }
    }
}
