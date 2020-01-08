package com.moonsoo.model;

public class Follow {
    private int id;
    private int followingUserId;
    private int followerUserId;

    public Follow(int followingUserId, int followerUserId) {
        this.followingUserId = followingUserId;
        this.followerUserId = followerUserId;
    }

    public int getId() {
        return id;
    }

    public int getFollowingUserId() {
        return followingUserId;
    }

    public int getFollowerUserId() {
        return followerUserId;
    }
}
