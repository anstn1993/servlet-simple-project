package com.moonsoo.model;

public class Follow {
    private int id;
    private int followingUserId;
    private int followedUserId;

    public Follow(int followingUserId, int followedUserId) {
        this.followingUserId = followingUserId;
        this.followedUserId = followedUserId;
    }

    public int getId() {
        return id;
    }

    public int getFollowingUserId() {
        return followingUserId;
    }

    public int getFollowedUserId() {
        return followedUserId;
    }
}
