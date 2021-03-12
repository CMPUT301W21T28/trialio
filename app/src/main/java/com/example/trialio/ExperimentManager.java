package com.example.trialio;

import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class ExperimentManager {
    private final String TAG = "ExperimentManager";

    private static CollectionReference experimentsCollection = FirebaseFirestore.getInstance().collection("experiments");

    private ArrayList<Experiment> experimentList;
    private ArrayAdapter<Experiment> experimentAdapter;

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

                            experiment.getTrialManager().addTrial(new BinomialTrial((String)trial.get("experimenterId"), new com.example.trialio.Location((double)location.get("latitude"), (double)location.get("longitude")),((com.google.firebase.Timestamp)trial.get("date")).toDate(), (boolean)trial.get("isSuccess")));
                            Log.d(TAG, "hi");
                        }
                    } else if (ExperimentTypeUtility.isCount(tm.get("type").toString())) {
                        // TODO
                    } else if (ExperimentTypeUtility.isNonNegative(tm.get("type").toString())) {
                        for (Map trial : (ArrayList<Map>) tm.get("trials")) {
                            Map location = (Map) trial.get("location");

                            experiment.getTrialManager().addTrial(new NonNegativeTrial((String)trial.get("experimenterId"), new com.example.trialio.Location((double)location.get("latitude"), (double)location.get("longitude")),((com.google.firebase.Timestamp)trial.get("date")).toDate(), ((java.lang.Long)trial.get("nonNegCount")).intValue()));
                            Log.d(TAG, "hi");
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
                    Log.d(TAG, "ELISTBOS " + e.toString());
                }
            }
        });
    }

    /**
     * This sets an adapter for the experiment manager
     * @param adapter
     * Candidate adapter to set
     */
    public void setAdapter(ArrayAdapter adapter) {
        experimentAdapter = adapter;
    }

    /**
     * This adds an experiment to the database
     * @param experiment
     * Candidate experiment to add to the database
     */
    public void publishExperiment (Experiment experiment) {
        Log.d(TAG, "Adding " + experiment.toString());
        experimentsCollection
                .document(experiment.getExperimentID())
                .set(experiment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data was added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data was not added!" + e.toString());
                    }
                });
    }

    /**
     * This sets the experiment with a given experiment ID as a given edited experiment
     * @param experimentId
     * Experiment ID of candidate experiment to edit
     * @param experiment
     * Candidate edited experiment to set
     */
    public void editExperiment (String experimentId, Experiment experiment) {
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
     * @param experimentId
     * Experiment ID of the candidate experiment to delete
     */
    public void unpublishExperiment (String experimentId) {
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
     * @param owner
     * User for which to find all of their owned experiments
     * @return
     * Returns the list of experiments owned by owner
     */
    public ArrayList<Experiment> getOwnedExperiments (User owner) {
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
     * This finds the list of experiments associated with a given keyword
     * @param keyword
     * String keyword to search for
     * @return
     * Returns the list of experiments associated with keyword
     */
    public ArrayList<Experiment> searchByKeyword (String keyword) {
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
     * This returns the current experiment list
     * @return
     * Returns the list of experiments
     */
    public ArrayList<Experiment> getExperimentList() {
        return experimentList;
    }

    /**
     * This generates a new unique experiment ID
     * @return
     * Returns a string which is a new experiment ID
     */
    public static String getNewExperimentID() {
        return experimentsCollection.document().getId();
    }

}
