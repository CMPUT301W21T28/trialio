package com.example.trialio.models;

import com.example.trialio.models.User;

/**
 * Represents a post on an experiment's question forum
 */
public abstract class Post {

    private String body;
    private User user;

    /**
     * Gets the body of a post
     *
     * @return the body of a post
     */
    public String getBody() {
        return this.body;
    }

}
