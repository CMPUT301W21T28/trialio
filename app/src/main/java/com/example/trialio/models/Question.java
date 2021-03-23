package com.example.trialio.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Question extends Post implements Serializable {
    private String title;
    private List<Reply> replies;


    public Question () {

    }

//    public Question(String postID, String body, User user, String title) {
//        super(postID, body, user);
//        this.title = title;
//    }

    public Question(String postID, String body, User user, String title) {
        super(postID, body, user);
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

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

}
