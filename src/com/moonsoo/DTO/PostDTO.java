package com.moonsoo.DTO;

import java.util.Date;

public class PostDTO {
    private int id;
    private int userId;
    private String article;

    public PostDTO(int userId) {
        this.userId = userId;
    }

    public PostDTO(int userId, String article) {
        this.userId = userId;
        this.article = article;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }
}
