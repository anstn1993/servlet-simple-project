package com.moonsoo.model;

public class Post {
    private int userId;
    private int postId;
    private String nickname;
    private String profile;
    private String article;
    private String fileName;
    private String time;
    private boolean likeStatus;//로그인한 사용자가 해당 게시물을 좋아하는지 여부

    public Post(int userId, int postId, String nickname, String profile, String article, String fileName, String time, boolean likeStatus) {
        this.userId = userId;
        this.postId = postId;
        this.nickname = nickname;
        this.profile = profile;
        this.article = article;
        this.fileName = fileName;
        this.time = time;
        this.likeStatus = likeStatus;
    }

    public Post(int userId, int postId, String nickname, String profile, String fileName, String time) {
        this.userId = userId;
        this.postId = postId;
        this.nickname = nickname;
        this.profile = profile;
        this.fileName = fileName;
        this.time = time;
    }

    public Post(int userId, int postId, String article, String fileName, String time) {
        this.userId = userId;
        this.postId = postId;
        this.article = article;
        this.fileName = fileName;
        this.time = time;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getLikeStatus() {
        return likeStatus;
    }
}
