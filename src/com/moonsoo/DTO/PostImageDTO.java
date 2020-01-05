package com.moonsoo.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostImageDTO {
    private int id;
    private int postId;

    List<String> fileNames;

    public PostImageDTO() {
        fileNames = new ArrayList<>();
    }

    public PostImageDTO(int id, int postId, List<String> fileNames) {
        this.id = id;
        this.postId = postId;
        this.fileNames = fileNames;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public void add(String fileName){//리스트에 이미지 파일 명 추가
        fileNames.add(fileName);
    }
}
