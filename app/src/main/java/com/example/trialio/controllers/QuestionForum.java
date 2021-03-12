package com.example.trialio.controllers;

import com.example.trialio.models.Post;
import com.example.trialio.models.Question;
import com.example.trialio.models.User;

import java.io.Serializable;
import java.util.Collection;

public class QuestionForum implements Serializable {

    private Collection<Question> questions;

    public void createQuestion (String title, String body, User user) {
        // ... finish me ...
    }

    public void createReply (Question question, String body, User user) {
        //...
    }

    public void deletePost (Post post) {
        //...
    }

//    public ArrayList<Question> getAllQuestions() {}



}
