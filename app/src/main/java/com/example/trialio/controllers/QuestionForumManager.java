package com.example.trialio.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trialio.models.Question;
import com.example.trialio.models.Reply;
import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionForumManager implements Serializable {

    private final CollectionReference questionForumCollection;
    private static final String TAG = "QuestionForumManager";
    private static final String QUESTION_FORUM_PATH = "questionForum"; // TODO: can you even make this into one path or do i have to split it up
    private static final String EXPERIMENT_PATH = "experiments";



    /**
     * Generates a new unique question ID
     * @return unique ID for a new question which is about to be posted
     */

    public String getNewPostID() { return this.questionForumCollection.document().getId(); }


    /**
     * This interface represents an action to be taken when an Question document is fetched from the database.
     */
    public interface OnQuestionFetchListener {
        /**
         * This method will be called when a Question is fetched from the database.
         * @param question the question that was fetched from the database
         */
        void onQuestionFetch(Question question);
    }

    /**
     * This interface represents an action to be taken when a collection of Questions is fetched
     * from the database.
     */
    public interface OnManyQuestionsFetchListener {
        /**
         * This method will be called when a collection of Questions is fetched from the database.
         *
         * @param questions all the questions that were fetched from the database (belong to the current experiment)
         */
        void onManyQuestionsFetch(List<Question> questions);
    }


    // TODO: how to handle the fetching of replies???

    /**
     * Constructor for QuestionForumManager
     */
    public QuestionForumManager(String associatedExperimentID) {
        questionForumCollection = FirebaseFirestore.getInstance().collection(EXPERIMENT_PATH).document(associatedExperimentID).collection(QUESTION_FORUM_PATH); // TODO: how can I make this path into one string?? is that even possible?
    }



    public void createQuestion (Question newQuestion) {
        Log.d(TAG, "Posting question " + newQuestion.getTitle());
        questionForumCollection
                .add(newQuestion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Question written successfully with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding question", e);
                    }
                });
    }


    public void deleteQuestion (String questionID) {
        Log.d(TAG, "Posting question " + questionID);
        questionForumCollection
                .document(questionID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = String.format("Experiment %s was deleted successfully", questionID);
                        Log.d(TAG, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = String.format("Failed to delete experiment %s", questionID);
                        Log.d(TAG, message);
                    }
                });
    }


    // TODO Replies
    public void createReply (Question question, String body, User user) {
        //...
    }

    public void deleteReply (Reply reply) {
        //...
    }



    /**
     * Sets a function to be called when an experiment is fetched
     * @param postID   the id of the question to fetch
     * @param listener     the function to be called when the experiment is fetched
     */
    public void setOnQuestionFetchListener(String postID, QuestionForumManager.OnQuestionFetchListener listener) {
        /* Firebase Developer Docs, "Get a document", 2021-03-09, Apache 2.0
         * https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
         */
        Log.d(TAG, "Fetching question");
        DocumentReference docRef = questionForumCollection.document(postID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Question question = extractQuestionDocument(doc);
                        listener.onQuestionFetch(question);
                        Log.d(TAG, "Question fetched successfully.");
                    } else {
                        Log.d(TAG, "No question(s) found");
                    }
                } else {
                    Log.d(TAG, "Question fetch failed with " + task.getException());
                }
            }
        });
    }


    /**
     * Sets a function to be called when all questions are fetched
     *
     * @param listener the function to be called when the questions are fetched
     */
    public void setOnAllQuestionsFetchCallback(OnManyQuestionsFetchListener listener) {
        /* Firebase Developer Docs, "Get all documents in a collection", 2021-03-09, Apache 2.0
         * https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
         */
        Log.d(TAG, "Fetching all questions from collection");
        questionForumCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String message = "All questions fetched successfully";
                            Log.d(TAG, message);

                            QuerySnapshot qs = task.getResult();
                            ArrayList<Question> questionList = new ArrayList<>();
                            for (DocumentSnapshot doc : qs.getDocuments()) {
                               // retrieves all documents (questions) within questionForum collection
                                Question question = extractQuestionDocument(doc);
                                questionList.add(question);
                            }
                            listener.onManyQuestionsFetch(questionList);
                        } else {
                            String message = "Failed to fetch all questions";
                            Log.d(TAG, message);
                        }
                    }
                });
    }

    /**
     * Extracts a Question object from a Firestore sub-collection (questionForum). This method assumes the document
     * hold a valid question.
     *
     * @param document the document to be extracted
     * @return the extracted question
     */

    // TODO: this seems to good to be true -> test the limitations of this function heavily
    private Question extractQuestionDocument(DocumentSnapshot document) {
        Question question = document.toObject(Question.class);


        return question;
    }

}
