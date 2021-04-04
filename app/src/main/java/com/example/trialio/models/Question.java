package com.example.trialio.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a Question that an experimenter can ask about an experiment.
 */
public class Question extends Post implements Serializable {
    private String title;
    private ArrayList<Reply> replies;


    public Question () {

    }

    public Question(String postID, String body, String userId, String title) {
        super(postID, body, userId);
        this.title = title;
    }

    public Collection<Reply> getAllReplies () {
        return this.replies;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Collection<Reply> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Reply> replies) {
        this.replies = replies;
    }

}
