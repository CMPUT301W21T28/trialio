package com.example.trialio.models;

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a reply to a question on an experiment's question forum
 */
public class Reply implements Serializable {

    private Collection<Reply> replies;

    public Reply(Collection<Reply> replies) {
        this.replies = replies;
    }

//    public Collection<Reply> getAllReplies() { }

}
