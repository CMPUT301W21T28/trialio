package com.example.trialio.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.trialio.models.Experiment;
import com.example.trialio.models.Post;
import com.example.trialio.models.Question;
import com.example.trialio.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class QuestionForum implements Serializable {

    private final CollectionReference questionForumCollection;
    private static final String TAG = "QuestionForum";
    private static final String QUESTION_FORUM_PATH = "questionForum"; // TODO: can you even make this into one path or do i have to split it up
    private static final String EXPERIMENT_PATH = "experiments";

    // Bundle experimentInfo = getIntent().getExtras;
    // TODO: send experiment id to several activities after Q&A button on experiment activity is pressed
    String experimentID = "DXefHaVmzELGzizNq51A"; // HARDCODED FOR NOW


    private Collection<Question> questions;


    /**
     * This interface represents an action to be taken when an Question document is fetched from the database.
     */
    public interface OnQuestionFetchListener {
        /**
         * This method will be called when an Experiment is fetched from the database.
         *
         * @param question the question that was fetched from the database
         */
        public void onQuestionFetch(Question question);
    }

    /**
     * This interface represents an action to be taken when a collection of Questions is fetched
     * from the database.
     */
    public interface OnManyQuestionsFetchListener {
        /**
         * This method will be called when a collection of Experiments is fetched from the database.
         *
         * @param questions all the questions that were fetched from the database (belong to the current experiment)
         */
        public void onManyExperimentsFetch(ArrayList<Question> questions);
    }


    // TODO: how to handle the fetching of replies???

    /**
     * Constructor for QuestionForum
     */
    public QuestionForum() {
        questionForumCollection = FirebaseFirestore.getInstance().collection(EXPERIMENT_PATH).document(experimentID).collection(QUESTION_FORUM_PATH); // TODO: how can I make this path into one string?? is that even possible?
        questions = new ArrayList<Question>();  // TODO: is it okay to use a Colletion instead of an ArrayList here ??
    }

    public Collection<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Collection<Question> questions) {
        this.questions = questions;
    }


    // TODO: why don't we just in a Question object instead ??
    public void createQuestion (Question question) {
        Question newQuestion = new Question();
        Log.d(TAG, "Posting question " + title.toString());


        questionForumCollection
                .document(questionID)

    }

    public void createReply (Question question, String body, User user) {
        //...
    }

    public void deletePost (Post post) {
        //...
    }

//    public ArrayList<Question> getAllQuestions() {}



}
