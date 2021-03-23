package com.example.trialio.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a reply to a question on an experiment's question forum
 */
public class Reply extends Post implements Serializable {

    private Collection<Reply> replies;

    public Reply(String body, User user, Collection<Reply> replies) {
        super(body, user);
        this.replies = replies;
    }

    public Reply(String postID, String body, User user) {
        super(postID, body, user);
        this.replies = new ArrayList<>();
    }

    public Reply(String postID, String body, User user, Collection<Reply> replies) {
        super(postID, body, user);
        this.replies = replies;
    }


    public Collection<Reply> getReplies() {
        return replies;
    }

    public void setReplies(Collection<Reply> replies) {
        this.replies = replies;
    }


}
