package com.example.trialio.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a reply to a question on an experiment's question forum
 */
public class Reply extends Post implements Serializable {

    private List<Reply> replies;

    public Reply () {

    }

    public Reply(String body, String userId, List<Reply> replies) {
        super(body, userId);
        this.replies = replies;
    }

    public Reply(String postID, String body, String userId) {
        super(postID, body, userId);
        this.replies = new ArrayList<>();
    }

    public Reply(String postID, String body, String userId, List<Reply> replies) {
        super(postID, body, userId);
        this.replies = replies;
    }


    public Collection<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }


}
