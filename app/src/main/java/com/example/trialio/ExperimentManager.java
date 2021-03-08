package com.example.trialio;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExperimentManager {
    private final String TAG = "ExperimentManager";

    private static CollectionReference experimentsCollection = FirebaseFirestore.getInstance().collection("experiments");

    private ArrayList<Experiment> experimentList;
    private ArrayAdapter<Experiment> experimentAdapter;

    public ExperimentManager() {
        experimentList = new ArrayList<Experiment>();
        experimentsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                experimentList.clear();
                for (Experiment experiment : (ArrayList<Experiment>) value.toObjects(Experiment.class)) {
                    Log.d(TAG, "experimentList: " + experiment.toString());
                    experimentList.add(experiment);
                }
                if (experimentAdapter != null) {
                    experimentAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void setAdapter(ArrayAdapter adapater) {
        experimentAdapter = adapater;
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
        ArrayList<Experiment> ownedExperiments = new ArrayList<Experiment>();
        for (Experiment experiment : experimentList) {
            if (experiment.getSettings().getOwner() == owner) {
                ownedExperiments.add(experiment);
            }
        }
        return ownedExperiments;
    }

//    public ArrayList<Experiment> searchByKeyword (String keyword) { }

    /**
     * This returns the current experiment list
     * @return
     * Returns the list of experiments
     */
    public ArrayList<Experiment> getExperimentList() {
        Log.d(TAG, "explist size = " + experimentList.size());

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
