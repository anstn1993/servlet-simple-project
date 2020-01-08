package com.moonsoo.model;

public class Like {
    private int id;
    private int postId;
    private int userId;

    public Like(int postId, int userId) {
        this.postId = postId;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public int getPostId() {
        return postId;
    }

    public int getUserId() {
        return userId;
    }
}
