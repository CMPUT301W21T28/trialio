package com.example.trialio.models;

import com.example.trialio.models.User;

import java.io.Serializable;

/**
 * Represents a Post that a user can make for an experiment. The Post class can be instantiated
 * through either the Question or the Reply subclass.
 */
public abstract class Post implements Serializable {

    /**
     * The id of the post
     */
    private String postId;

    /**
     * The body text of the post
     */
    private String body;

    /**
     * The id of the user that created the post
     */
    private String userId;


    /**
     * Creates a Post
     *
     * @param body   the body text of the post
     * @param userId the id of the User that created the post
     */
    public Post(String body, String userId) {
        this.body = body;
        this.userId = userId;
    }

    /**
     * Creates a Post
     *
     * @param postID the id of the post
     * @param body   the body text of the post
     * @param userId the id of the User that created the post
     */
    public Post(String postID, String body, String userId) {
        this.postId = postID;
        this.body = body;
        this.userId = userId;
    }

    /**
     * Creates a Post
     */
    protected Post() {
    }

    /**
     * Gets the id of the post
     *
     * @return the post id
     */
    public String getPostId() {
        return this.postId;
    }

    /**
     * Sets the id of the post
     */
    public void setPostId(String id) {
        this.postId = id;
    }

    /**
     * Gets the body text of the post
     *
     * @return the body text of the post
     */
    public String getBody() {
        return this.body;
    }

    /**
     * Sets the id of the post
     *
     * @param body the id to be set to the post
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the id of the user that created the post
     *
     * @return the id of the user that created the post
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the id of the User that created the post the post
     *
     * @param userId the id of the User that created the post the post
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
