package com.example.trialio.models;

import com.example.trialio.models.User;

public abstract class Post {

    private String body;
    private User user;


    public Post(String body, User user) {
        this.body = body;
        this.user = user;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
