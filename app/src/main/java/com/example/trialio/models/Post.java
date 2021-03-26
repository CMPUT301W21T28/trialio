package com.example.trialio.models;

import com.example.trialio.models.User;

import java.io.Serializable;

public abstract class Post implements Serializable {

    private String postID;
    private String body;
    private String userId;


    public Post(String body, String userId) {
        this.body = body;
        this.userId = userId;
    }

    public Post(String postID, String body, String userId) {
        this.postID = postID;
        this.body = body;
        this.userId = userId;
    }

    protected Post() {
    }

    public void setPostID(String id) { this.postID = id ; }

    public String getPostID() { return this.postID; }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return this.body;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

}
