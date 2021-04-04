package com.example.trialio.controllers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * TrialManager manages Trials for an experiment and is responsible for the persistence of Trial
 * data. This class is used to perform create, read, update and delete functionality on experiment
 * trials that are uploaded for an experiment. This class communicates with the Firebase database.
 */
public class TrialManager implements Serializable {
    private final String TAG = "TrialManager";
    private static final String EXPERIMENT_COLLECTION_PATH = "experiments-v6";
    private static final String TRIALS_COLLECTION_PATH = "trials";

    private static final String EXPERIMENTERID_FIELD = "experimenterID";
    private static final String L_LATITUDE_FIELD = "latitude";
    private static final String L_LONGITUDE_FIELD = "longitude";
    private static final String DATE_FIELD = "date";
    private static final String B_ISSUCCESS_FIELD = "isSuccess";
    private static final String C_COUNT_FIELD = "count";
    private static final String M_MEASUREMENT_FIELD = "measurement";
    private static final String M_UNIT_FIELD = "unit";
    private static final String N_NONNEGCOUNT_FIELD = "nonNegCount";

    private String type;
    private ArrayList<String> ignoredUserIDs;
    private int minNumOfTrials;
    private boolean isOpen;
    private String experimentID;

    public TrialManager() {
        this.ignoredUserIDs = new ArrayList<String>();
    }

    public TrialManager(String experimentID, String type, boolean isOpen, int minNumOfTrials) {
        this.type = type;
        this.ignoredUserIDs = new ArrayList<String>();
        this.minNumOfTrials = minNumOfTrials;
        this.isOpen = isOpen;
        this.experimentID = experimentID;
    }

    /**
     * This gets the type of trials controlled by this trial manager.
     * @return Returns the type fo this trial manager.
     */
    public String getType() {
        return type;
    }

    /**
     * This sets the type of trials controlled by this trial manger.
     * @param type The candidate type to set as the trial manager type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This adds a trial to the trial collection.
     * @param trial The candidate trial to add to the trial collection.
     */
    public void addTrial(Trial trial) {

        CollectionReference trialCollection = FirebaseFirestore.getInstance().collection(EXPERIMENT_COLLECTION_PATH).document(experimentID).collection(TRIALS_COLLECTION_PATH);

        // get a new ID for the document
        String newTrialID = trialCollection.document().getId();

        // set the compressed trial in Firebase
        trialCollection
                .document(newTrialID)
                .set(compressTrial(trial))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Trial " + newTrialID + " was successfully added.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to add trial " + newTrialID + ".");
                    }
                });
    }

    /**
     * This gets the list of user ids which are ignored by the trial manager.
     * @return Returns the list of ignored user ids.
     */
    public ArrayList<String> getIgnoredUserIDs() {
        return ignoredUserIDs;
    }

    /**
     * This sets the list of user ids ignored by the trial manager.
     * @param ignoredUserIDs The candidate list of user ids to be ignored by the trial manager.
     */
    public void setIgnoredUserIDs(ArrayList<String> ignoredUserIDs) {
        this.ignoredUserIDs = ignoredUserIDs;
    }

    /**
     * This gets the minimum number of trials of the trial manager.
     * @return Returns the minimum number of trials of the trial manager.
     */
    public int getMinNumOfTrials() {
        return minNumOfTrials;
    }

    /**
     * This sets the minimum number of trials of the trial manager.
     * @param minNumOfTrials The candidate integer to set as the minimum number of trails of the trial manager.
     */
    public void setMinNumOfTrials(int minNumOfTrials) {
        this.minNumOfTrials = minNumOfTrials;
    }

    /**
     * This gets a boolean that signifies if the trial manager is open. ie. can have trials added.
     * @return Returns the boolean isOpen of the trial manager.
     */
    public boolean getIsOpen() {
        return isOpen;
    }

    /**
     * This sets the boolean isOpen of the trial manager.
     * @param open The canidate boolean value to set as isOpen of the trail manager.
     */
    public void setIsOpen(boolean open) {
        this.isOpen = open;
    }

    /**
     * This adds a userID to the list of ignored userIDs.
     * @param userID The candidate userID to add to the list of ignored userIds.
     */
    public void addIgnoredUser(String userID) {
        ignoredUserIDs.add(userID);
    }

    /**
     * This removes a userID from the list of ignored userIDs
     * @param userID The candidate userID to remove from the list of ignored userIDs.
     */
    public void removeIgnoredUsers(String userID) {
        ignoredUserIDs.remove(userID);
    }

    /**
     * Sets a listener to the trial collection. This function sets up a listener so that
     * listener.onAllTrialsUpdate() is called when all visible trials in the collection have been
     * fetched.
     *
     * @param listener the listener with the callback function to be called when the Trial
     *                 collection is updated
     */
    public void setAllVisibleTrialsFetchListener(TrialManager.OnAllVisibleTrialsFetchListener listener) {
        CollectionReference trialCollection = FirebaseFirestore.getInstance().collection(EXPERIMENT_COLLECTION_PATH).document(experimentID).collection(TRIALS_COLLECTION_PATH);

        trialCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                // initialize a list of trials
                ArrayList<Trial> trialList = new ArrayList<Trial>();

                // extract all trial documents in  the collection
                for (DocumentSnapshot doc : value.getDocuments()) {
                    try {
                        Trial trial = extractTrial(doc);
                        if (!ignoredUserIDs.contains(trial.getExperimenterId())) {
                            trialList.add(trial);
                        }
                        Log.d(TAG, "Trial " + doc.getId() + " fetched successfully.");
                    } catch (Exception e) {
                        Log.d(TAG, "Error fetching " + doc.getId() + ".");
                    }
                }

                // pass the trialList to the listener
                listener.onAllVisibleTrialsFetch(trialList);
            }
        });
    }

    /**
     * This gets the ID of the experiment associated with the trialManager.
     * @return Returns the String that is the ID of the experiment.
     */
    public String getExperimentID() {
        return experimentID;
    }

    /**
     * This sets the ID of the experiment associated with the trialManager.
     * @param experimentID The candidate string to set as the experiment ID of the trialManager.
     */
    public void setExperimentID(String experimentID) {
        this.experimentID = experimentID;
    }

    /**
     * This interface represents an action to be taken when all visible trials of a collection have
     * been fetched.
     */
    public interface OnAllVisibleTrialsFetchListener {

        /**
         * This method is called when all visible trials in the collection have been fetched and
         * extracted. @param trialList The list of trials extracted from the collection.
         */
        public void onAllVisibleTrialsFetch(ArrayList<Trial> trialList);
    }

    /**
     * Compresses a trial into a Map which can be stored in a Firebase document.
     * @param trial Trial to compress into a Map.
     * @return Returns the map which will be stored in a Firebase document.
     */
    public Map<String, Object> compressTrial(Trial trial) {

        // create map
        Map<String, Object> data = new HashMap<String, Object>();

        // set Trial fields
        data.put(EXPERIMENTERID_FIELD, trial.getExperimenterId());
        data.put(DATE_FIELD, trial.getDate());
        data.put(L_LONGITUDE_FIELD, trial.getLocation().getLongitude());
        data.put(L_LATITUDE_FIELD, trial.getLocation().getLatitude());

        if (ExperimentTypeUtility.isMeasurement(type)) {

            // set MeasurementTrial fields
            data.put(M_MEASUREMENT_FIELD, ((MeasurementTrial) trial).getMeasurement());
            data.put(M_UNIT_FIELD, ((MeasurementTrial) trial).getUnit());

        } else if (ExperimentTypeUtility.isCount(type)) {

            // set CountTrial fields
            data.put(C_COUNT_FIELD, ((CountTrial) trial).getCount());

        } else if (ExperimentTypeUtility.isNonNegative(type)) {

            // set NonNegativeTrial fields
            data.put(N_NONNEGCOUNT_FIELD, ((NonNegativeTrial) trial).getNonNegCount());

        } else if (ExperimentTypeUtility.isBinomial(type)) {

            // set BinomialTrial fields
            data.put(B_ISSUCCESS_FIELD, ((BinomialTrial) trial).getIsSuccess());

        } else {
            Log.d(TAG, "EXPERIMENT_TYPE_ERROR: invalid type in compressTrial.");
            assert false;
        }

        return data;
    }

    /**
     * Extracts a trial object from a Firebase document. This method assumes the document holds a
     * valid trial.
     * @param document The document to extract a trial object from.
     * @return The trial object extracted from the document.
     */
    public Trial extractTrial(DocumentSnapshot document) {

        // get the data
        Map<String, Object> data = document.getData();

        // initialize trial
        Trial trial = null;

        // set Trial subclass specific attributes
        if (ExperimentTypeUtility.isBinomial(type)){
            trial = new BinomialTrial();

            // set isSuccess
            boolean isSuccess = (boolean) data.get(B_ISSUCCESS_FIELD);
            ((BinomialTrial) trial).setIsSuccess(isSuccess);

        } else if (ExperimentTypeUtility.isNonNegative(type)) {
            trial = new NonNegativeTrial();

            // set nonNegCount
            int nonNegCount = ((Long) data.get(N_NONNEGCOUNT_FIELD)).intValue();
            ((NonNegativeTrial) trial).setNonNegCount(nonNegCount);

        } else if (ExperimentTypeUtility.isCount(type)) {
            trial = new CountTrial();

            // set count
            int count = ((Long) data.get(C_COUNT_FIELD)).intValue();
            ((CountTrial) trial).setCount(count);

        } else if (ExperimentTypeUtility.isMeasurement(type)) {
            trial = new MeasurementTrial();

            // set measurement
            double measurement = (double) data.get(M_MEASUREMENT_FIELD);
            ((MeasurementTrial) trial).setMeasurement(measurement);

            // set unit
            String unit = (String) data.get(M_UNIT_FIELD);
            assert unit != null;
            ((MeasurementTrial) trial).setUnit(unit);

        } else {
            Log.d(TAG, "EXPERIMENT_TYPE_ERROR: invalid type in extractTrial.");
            assert false;
        }

        // fail if we don't have a trial at this point
        assert trial != null;

        // set experimenterID
        String experimenterID = (String) data.get(EXPERIMENTERID_FIELD);
        assert experimenterID != null;
        trial.setExperimenterId(experimenterID);

        // set location
        trial.setLocation(new Location());

        // set latitude in location
        double latitude = (double) data.get(L_LATITUDE_FIELD);
        trial.getLocation().setLatitude(latitude);

        // set longitude in location
        double longitude = (double) data.get(L_LONGITUDE_FIELD);
        trial.getLocation().setLongitude(longitude);

        // set date
        Date date = ((Timestamp) data.get(DATE_FIELD)).toDate();
        assert date != null;
        trial.setDate(date);

        // return the trial we extracted
        return trial;
    }
}
