package com.example.baitap10.model;

import java.io.Serializable;
import java.util.Map;

public class VideoModel implements Serializable {
    private String title;
    private String decs;

    public VideoModel() {
    }

    private String avatar;

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

    public VideoModel(Map<String, Boolean> dislikes, Map<String, Boolean> likes, String url, String avatar, String decs, String title) {
        this.dislikes = dislikes;
        this.likes = likes;
        this.url = url;
        this.avatar = avatar;
        this.decs = decs;
        this.title = title;
    }

    private String url;

    // NEW: Thêm likes và dislikes
    private Map<String, Boolean> likes;
    private Map<String, Boolean> dislikes;
}
