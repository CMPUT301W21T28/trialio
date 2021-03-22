package com.example.trialio.models;

import java.io.Serializable;
import java.util.Collection;

/**
 * Represents a question posed on an experiment's question forum
 */
public class Question implements Serializable {

    private String title;
    private Collection<Reply> replies;

//    public Collection<Reply> getAllReplies () {    }


}
