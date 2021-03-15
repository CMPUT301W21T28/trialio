package com.example.trialio.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Location;
import com.example.trialio.models.Trial;
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

import java.util.ArrayList;
import java.util.Map;

public class ExperimentManager {
    private static final String TAG = "ExperimentManager";
    private static final String COLLECTION_PATH = "experiments";

    private final CollectionReference experimentsCollection;

    // WANT TO DELETE THIS
    private ArrayList<Experiment> experimentList;

    /**
     * This interface represents an action to be taken when an Experiment document is fetched from
     * the database.
     */
    public interface OnExperimentFetchListener {

        /**
         * This method will be called when an Experiment is fetched from the database.
         *
         * @param experiment the experiment that was fetched from the database
         */
        public void onExperimentFetch(Experiment experiment);
    }

    /**
     * This interface represents an action to be taken when a collection of Experiments is fetched
     * from the database.
     */
    public interface OnManyExperimentsFetchListener {
        /**
         * This methid will be called when a collection of Experiments is fetched from the database.
         *
         * @param experiments the experiments that were fetched from the database
         */
        public void onManyExperimentsFetch(ArrayList<Experiment> experiments);
    }

    /**
     * Constructor for ExperimentManager
     */
    public ExperimentManager() {
        experimentsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_PATH);
        experimentList = new ArrayList<Experiment>(); // ONLY HERE TO AVOID BREAKING CODE
    }

    /**
     * Constructor for ExperimentManager
     */
    public ExperimentManager(String collectionPath) {
        experimentsCollection = FirebaseFirestore.getInstance().collection(collectionPath);
        experimentList = new ArrayList<Experiment>(); // ONLY HERE TO AVOID BREAKING CODE
    }

    /**
     * This adds an experiment to the database
     *
     * @param experiment Candidate experiment to add to the database
     */
    public void publishExperiment(Experiment experiment) {
        Log.d(TAG, "Adding experiment " + experiment.toString());
        String id = experiment.getExperimentID();
        experimentsCollection
                .document(id)
                .set(experiment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = "Experiment " + id + " was successfully added.";
                        Log.d(TAG, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = "Failed to add experiment " + id;
                        Log.d(TAG, message);
                    }
                });
    }

    /**
     * Sets a function to be called when an experiment is fetched
     *
     * @param experimentId the id of the experiment to fetch
     * @param listener     the function to be called when the experiment is fetched
     */
    public void setOnExperimentFetchListener(String experimentId, OnExperimentFetchListener listener) {
        /* Firebase Developer Docs, "Get a document", 2021-03-09, Apache 2.0
         * https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
         */
        Log.d(TAG, "Fetching experiment " + experimentId);
        DocumentReference docRef = experimentsCollection.document(experimentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Experiment experiment = extractExperimentDocument(doc);
                        listener.onExperimentFetch(experiment);
                        Log.d(TAG, "Experiment " + experimentId + " fetched successfully.");
                    } else {
                        Log.d(TAG, "No experiment found with id " + experimentId);
                    }
                } else {
                    Log.d(TAG, "Experiment fetch failed with " + task.getException());
                }
            }
        });
    }


    /**
     * Sets a function to be called when all experiment are fetched
     *
     * @param listener the function to be called when the experiments are fetched
     */
    public void setOnAllExperimentsFetchCallback(OnManyExperimentsFetchListener listener) {
        /* Firebase Developer Docs, "Get all documents in a collection", 2021-03-09, Apache 2.0
         * https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
         */
        Log.d(TAG, "Fetching all experiments from collection");
        experimentsCollection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String message = "All experiments fetched successfully";
                            Log.d(TAG, message);

                            QuerySnapshot qs = task.getResult();
                            ArrayList<Experiment> experimentList = new ArrayList<>();
                            for (DocumentSnapshot doc : qs.getDocuments()) {
                                Experiment experiment = extractExperimentDocument(doc);
                                experimentList.add(experiment);
                            }
                            listener.onManyExperimentsFetch(experimentList);
                        } else {
                            String message = "Failed to fetch all experiments";
                            Log.d(TAG, message);
                        }
                    }
                });
    }

    /**
     * This sets the experiment with a given experiment ID as a given edited experiment
     *
     * @param experimentId Experiment ID of candidate experiment to edit
     * @param experiment   Candidate edited experiment to set
     */
    public void editExperiment(String experimentId, Experiment experiment) {
        Log.d(TAG, "Editing " + experimentId + "with" + experiment.toString());
        experimentsCollection
                .document(experimentId)
                .set(experiment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = String.format("Experiment %s was edited successfully", experimentId);
                        Log.d(TAG, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = String.format("Failed to edit experiment %s", experimentId);
                        Log.d(TAG, message);
                    }
                });
    }

    /**
     * This deletes an experiment associated with a given experiment ID
     *
     * @param experimentId Experiment ID of the candidate experiment to delete
     */
    public void unpublishExperiment(String experimentId) {
        Log.d(TAG, "Deleting experiment" + experimentId);
        experimentsCollection
                .document(experimentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = String.format("Experiment %s was deleted successfully", experimentId);
                        Log.d(TAG, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = String.format("Failed to delete experiment %s", experimentId);
                        Log.d(TAG, message);
                    }
                });
    }

    /**
     * This finds the list of all experiments owned by a given user
     * NOTICE: Temporarily private while we rewrite it.
     *
     * @param owner User for which to find all of their owned experiments
     * @return Returns the list of experiments owned by owner
     */
    private ArrayList<Experiment> getOwnedExperiments(User owner) {
        // TODO: test
        ArrayList<Experiment> ownedExperiments = new ArrayList<Experiment>();
        for (Experiment experiment : experimentList) {
            if (experiment.getSettings().getOwner() == owner) {
                ownedExperiments.add(experiment);
            }
        }
        return ownedExperiments;
    }

    /**
     * This finds the list of experiments associated with a given keyword.
     * NOTICE: Temporarily private while we rewrite it.
     *
     * @param keyword String keyword to search for
     * @return Returns the list of experiments associated with keyword
     */
    private ArrayList<Experiment> searchByKeyword(String keyword) {
        // TODO: test
        ArrayList<Experiment> searchResults = new ArrayList<Experiment>();
        for (Experiment experiment : experimentList) {
            if (experiment.getKeywords().contains(keyword)) {
                searchResults.add(experiment);
            }
        }
        return searchResults;
    }

    /**
     * DO NOT USE THIS
     * This returns the current experiment list
     *
     * @return Returns the list of experiments
     */
    public ArrayList<Experiment> getExperimentList() {
        return experimentList;
    }

    /**
     * This generates a new unique experiment ID
     *
     * @return Returns a string which is a new experiment ID
     */
    public String getNewExperimentID() {
        return this.experimentsCollection.document().getId();
    }

    /**
     * Extracts an experiment object from a Firestore document. This method assumes the document
     * hold a valid Experiment.
     *
     * @param document the document to be extracted
     * @return the extracted experiment
     */
    private Experiment extractExperimentDocument(DocumentSnapshot document) {
        Experiment experiment = document.toObject(Experiment.class);

        // clear the trials since they do not have subclass specific attributes
        experiment.getTrialManager().setTrials(new ArrayList<Trial>());

        Map data = document.getData();
        Map tm = (Map) data.get("trialManager");

        // cast trials to the appropriate type and add them to the trial manager
        if (ExperimentTypeUtility.isBinomial(tm.get("type").toString())) {
            for (Map trial : (ArrayList<Map>) tm.get("trials")) {
                Map location = (Map) trial.get("location");
                experiment.getTrialManager().addTrial(new BinomialTrial((String) trial.get("experimenterID"), new Location((double) location.get("latitude"), (double) location.get("longitude")), ((com.google.firebase.Timestamp) trial.get("date")).toDate(), (boolean) trial.get("isSuccess")));
            }
        } else if (ExperimentTypeUtility.isCount(tm.get("type").toString())) {
            for (Map trial : (ArrayList<Map>) tm.get("trials")) {
                Map location = (Map) trial.get("location");
                experiment.getTrialManager().addTrial(new CountTrial((String) trial.get("experimenterID"), new Location((double) location.get("latitude"), (double) location.get("longitude")), ((com.google.firebase.Timestamp) trial.get("date")).toDate()));
            }
        } else if (ExperimentTypeUtility.isNonNegative(tm.get("type").toString())) {
            for (Map trial : (ArrayList<Map>) tm.get("trials")) {
                Map location = (Map) trial.get("location");
                experiment.getTrialManager().addTrial(new NonNegativeTrial((String) trial.get("experimenterID"), new Location((double) location.get("latitude"), (double) location.get("longitude")), ((com.google.firebase.Timestamp) trial.get("date")).toDate(), ((java.lang.Long) trial.get("nonNegCount")).intValue()));
            }
        } else if (ExperimentTypeUtility.isMeasurement(tm.get("type").toString())) {
            for (Map trial : (ArrayList<Map>) tm.get("trials")) {
                Map location = (Map) trial.get("location");
                experiment.getTrialManager().addTrial(new MeasurementTrial((String) trial.get("experimenterID"), new Location((double) location.get("latitude"), (double) location.get("longitude")), ((com.google.firebase.Timestamp) trial.get("date")).toDate(), ((java.lang.Double) trial.get("measurement")).doubleValue(), trial.get("unit").toString()));
            }
        } else {
            assert (false);
        }
        return experiment;
    }
}
