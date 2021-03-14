package com.example.trialio.controllers;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Experiment;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ExperimentManager {
    private static final String TAG = "ExperimentManager";
    private static final String COLLECTION_PATH = "experiments";

    private static final CollectionReference experimentsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_PATH);

    // WANT TO DELETE THIS
    private ArrayList<Experiment> experimentList;

    // WANT TO DELETE THIS
    private ArrayAdapter<Experiment> experimentAdapter;

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
        experimentList = new ArrayList<Experiment>();

        // set up a listener which calls onEvent whenever the collection is updated
        experimentsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                experimentList.clear();
                for (DocumentSnapshot ds : value.getDocuments()) {
                    // copy the entire experiment from firebase
                    Experiment experiment = ds.toObject(Experiment.class);

                    // clear the trials since they do not have subclass specific attributes
                    experiment.getTrialManager().setTrials(new ArrayList<Trial>());

                    Map data = ds.getData();
                    Map tm = (Map) data.get("trialManager");

                    // cast trials to the appropriate type and add them to the trial manager
                    if (ExperimentTypeUtility.isBinomial(tm.get("type").toString())) {
                        for (Map trial : (ArrayList<Map>) tm.get("trials")) {
                            Map location = (Map) trial.get("location");

                            experiment.getTrialManager().addTrial(new BinomialTrial((String) trial.get("experimenterId"), new Location((double) location.get("latitude"), (double) location.get("longitude")), ((com.google.firebase.Timestamp) trial.get("date")).toDate(), (boolean) trial.get("isSuccess")));
                        }
                    } else if (ExperimentTypeUtility.isCount(tm.get("type").toString())) {
                        // TODO
                    } else if (ExperimentTypeUtility.isNonNegative(tm.get("type").toString())) {
                        for (Map trial : (ArrayList<Map>) tm.get("trials")) {
                            Map location = (Map) trial.get("location");

                            experiment.getTrialManager().addTrial(new NonNegativeTrial((String) trial.get("experimenterId"), new Location((double) location.get("latitude"), (double) location.get("longitude")), ((com.google.firebase.Timestamp) trial.get("date")).toDate(), ((java.lang.Long) trial.get("nonNegCount")).intValue()));
                        }
                    } else if (ExperimentTypeUtility.isMeasurement(tm.get("type").toString())) {
                        // TODO
                    } else {
                        assert (false);
                    }
                    experimentList.add(experiment);
                }
                // if an adapter was set, tell it to update
                if (experimentAdapter != null) {
                    experimentAdapter.notifyDataSetChanged();
                }
                for (Experiment e : experimentList) {
                    Log.d(TAG, "experiment in experimentList: " + e.toString());
                }
            }
        });
    }

    /**
     * DO NOT USE THIS
     * Use setOnExperimentListFetchListener and set the adapter manually instead.
     * -------------------------------------------------------------------------
     * This sets an adapter for the experiment manager
     *
     * @param adapter Candidate adapter to set
     */
    public void setAdapter(ArrayAdapter adapter) {
        experimentAdapter = adapter;
    }

    /**
     * This adds an experiment to the database
     *
     * @param experiment Candidate experiment to add to the database
     */
    public void publishExperiment(Experiment experiment) {
        Log.d(TAG, "Adding experiment" + experiment.toString());
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
    public void setOnExperimentFetchCallback(String experimentId, OnExperimentFetchListener listener) {
        // https://firebase.google.com/docs/firestore/query-data/get-data
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
        // https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
        Log.d(TAG, "Fetching all experiment");
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
                        Log.d(TAG, "Data was edited!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not edited!" + e.toString());
                    }
                });
    }

    /**
     * This deletes an experiment associated with a given experiment ID
     *
     * @param experimentId Experiment ID of the candidate experiment to delete
     */
    public void unpublishExperiment(String experimentId) {
        Log.d(TAG, "Deleting " + experimentId);
        experimentsCollection
                .document(experimentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data was deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not deleted!" + e.toString());
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
    public static String getNewExperimentID() {
        return experimentsCollection.document().getId();
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

                experiment.getTrialManager().addTrial(new BinomialTrial((String) trial.get("experimenterId"), new Location((double) location.get("latitude"), (double) location.get("longitude")), ((com.google.firebase.Timestamp) trial.get("date")).toDate(), (boolean) trial.get("isSuccess")));
            }
        } else if (ExperimentTypeUtility.isCount(tm.get("type").toString())) {
            // TODO
        } else if (ExperimentTypeUtility.isNonNegative(tm.get("type").toString())) {
            for (Map trial : (ArrayList<Map>) tm.get("trials")) {
                Map location = (Map) trial.get("location");

                experiment.getTrialManager().addTrial(new NonNegativeTrial((String) trial.get("experimenterId"), new Location((double) location.get("latitude"), (double) location.get("longitude")), ((com.google.firebase.Timestamp) trial.get("date")).toDate(), ((java.lang.Long) trial.get("nonNegCount")).intValue()));
            }
        } else if (ExperimentTypeUtility.isMeasurement(tm.get("type").toString())) {
            // TODO
        } else {
            assert (false);
        }
        return experiment;
    }

}
