package com.example.baitap10.model;

import java.io.Serializable;
import java.util.Map;

public class VideoModel implements Serializable {

    private String title;       // Tiêu đề video
    private String decs;        // Mô tả video
    private String avatar;      // Avatar người đăng
    private String url;         // URL của video (videoUrl)
    private Map<String, Boolean> likes;   // Likes của video (map người dùng đã thích)
    private Map<String, Boolean> dislikes; // Dislikes của video (map người dùng đã không thích)
    private String email;       // Email người dùng
    private String username;    // Tên người dùng
    private String owner;       // ID của người sở hữu video

    // Constructor mặc định (để Firebase có thể sử dụng)
    public VideoModel() {
    }

    // Constructor với tất cả các tham số
    public VideoModel(String title, String decs, String avatar, String url,
                      Map<String, Boolean> likes, Map<String, Boolean> dislikes,
                      String email, String username, String owner) {
        this.title = title;
        this.decs = decs;
        this.avatar = avatar;
        this.url = url;
        this.likes = likes;
        this.dislikes = dislikes;
        this.email = email;
        this.username = username;
        this.owner = owner;
    }

    // Getter và Setter cho từng thuộc tính
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDecs() {
        return decs;
    }

    public void setDecs(String decs) {
        this.decs = decs;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Map<String, Boolean> dislikes) {
        this.dislikes = dislikes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
