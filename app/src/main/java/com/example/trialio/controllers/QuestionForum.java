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



//    Bundle experimentInfo = getIntent().getExtras;
//    String experimentID = experimentInfo.getString("experimentID);
    private static final String TAG = "QuestionForum";
    private static final String QUESTION_FORUM_PATH = "experiemnts"; // TODO: can you even make this into one path or do i have to split it up

    private final CollectionReference questionForumCollection;

    // WANT TO DELETE THIS --> private ArrayList<Question> experimentList;  : Just made the collection mod below ** is this okay?
    private Collection<Question> questions;

    /**
     * This interface represents an action to be taken when an Experiment document is fetched from
     * the database.
     */
    public interface OnQuestionFetchListener {

        /**
         * This method will be called when an Experiment is fetched from the database.
         *
         * @param experiment the experiment that was fetched from the database
         */
        public void onQuestionFetch(Question question);
    }

    /**
     * This interface represents an action to be taken when a collection of Experiments is fetched
     * from the database.
     */
    public interface OnManyQuestionsFetchListener {
        /**
         * This method will be called when a collection of Experiments is fetched from the database.
         *
         * @param experiments the experiments that were fetched from the database
         */
        public void onManyExperimentsFetch(ArrayList<Experiment> experiments);
    }


//    public interface OnReplyFetchListener {
//
//        /**
//         * This method will be called when an Experiment is fetched from the database.
//         *
//         * @param experiment the experiment that was fetched from the database
//         */
//        public void onQuestionFetch(Question question);
//    }
//
//    public interface OnManyRepliesFetchListener {
//        /**
//         * This method will be called when a collection of Experiments is fetched from the database.
//         *
//         * @param experiments the experiments that were fetched from the database
//         */
//        public void onManyExperimentsFetch(ArrayList<Experiment> experiments);
//    }


    /**
     * Constructor for QuestionForum
     */
    public QuestionForum() {
        questionForumCollection = FirebaseFirestore.getInstance().collection(QUESTION_FORUM_PATH); // TODO: how can I make this path into one string?? is that even possible?
        questions = new Collection<Question>();  // TODO: is it okay to use a Colletion instead of an ArrayList here ??
    }

    public Collection<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(Collection<Question> questions) {
        this.questions = questions;
    }

    public void createQuestion (String title, String body, User user) {
        Question newQuestion = new Question();
        Log.d(TAG, "Posting question " + title.toString());
        String creatorID = user.getId();
        questionForumCollection
                .document(experimentID)
                .collection(questionForum)
                .set(newQuestion)

    }

    public void createReply (Question question, String body, User user) {
        //...
    }

    public void deletePost (Post post) {
        //...
    }

//    public ArrayList<Question> getAllQuestions() {}



}
