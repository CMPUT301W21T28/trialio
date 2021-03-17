package com.example.trialio.models;

import java.io.Serializable;
import java.util.Collection;

public class Question extends Post implements Serializable {

    private String title;
    private Collection<Reply> replies;

    public Question(String body, User user, String title, Collection<Reply> replies) {
        super(body, user);
        this.title = title;
        this.replies = replies;
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

    public void setReplies(Collection<Reply> replies) {
        this.replies = replies;
    }

}
