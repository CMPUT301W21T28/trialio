package com.example.trialio.models;

import com.example.trialio.models.User;

public abstract class Post {

    private String body;
    private User user;

    public String getBody() {
        return this.body;
    }

}
