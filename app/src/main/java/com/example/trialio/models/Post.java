package com.example.trialio.models;

import com.example.trialio.models.User;

public abstract class Post {

    private String postID;
    private String body;
    private User user;


    public Post(String body, User user) {
        this.body = body;
        this.user = user;
    }

    public Post(String postID, String body, User user) {
        this.postID = postID;
        this.body = body;
        this.user = user;
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

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
