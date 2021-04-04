package com.example.trialio.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trialio.models.Experiment;
import com.example.trialio.models.ExperimentSettings;
import com.example.trialio.models.Region;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages experiments and handles the persistence of experiment data.
 */
public class ExperimentManager {
    private static final String TAG = "ExperimentManager";
    private static String COLLECTION_PATH = "experiments-v5";

    private static final String E_EXPERIMENTID_FIELD = "experimentID";
    private static final String E_KEYWORDS_FIELD = "keywords";
    private static final String ES_DESCRIPTION_FIELD = "description";
    private static final String ES_R_REGIONTEXT_FIELD = "regionText";
    private static final String ES_R_KMRADIUS_FIELD = "kmRadius";
    private static final String ES_OWNERID_FIELD = "ownerID";
    private static final String ES_GEOLOCATIONREQUIRED_FIELD = "geoLocationRequired";
    private static final String TM_TYPE_FIELD = "type";
    private static final String TM_IGNOREDUSERIDS = "ignoredUserIDs";
    private static final String TM_MINNUMOFTRIALS = "minNumOfTrials";
    private static final String TM_ISOPEN = "isOpen";

    private final CollectionReference experimentsCollection;

    /*
     * This interface design was adapted from Android callbacks such as OnCLickListener
     * Android Developer Docs, "View.OnClickListener", 2020-09-30, Apache 2.0,
     * https://developer.android.com/reference/android/view/View.OnClickListener
     */

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
        public void onManyExperimentsFetch(List<Experiment> experiments);
    }

    /**
     * Constructor for ExperimentManager
     */
    public ExperimentManager() {
        experimentsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_PATH);
    }

    /**
     * Constructor for ExperimentManager
     */
    public ExperimentManager(String collectionPath) {
        experimentsCollection = FirebaseFirestore.getInstance().collection(collectionPath);
    }

    /**
     * This adds an experiment to the database
     *
     * @param experiment Candidate experiment to add to the database
     */
    public void publishExperiment(Experiment experiment) {
        Log.d(TAG, "Adding experiment " + experiment.toString());
        String ID = experiment.getExperimentID();
        experimentsCollection
                .document(ID)
                .set(compressExperiment(experiment))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = "Experiment " + ID + " was successfully added.";
                        Log.d(TAG, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = "Failed to add experiment " + ID;
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
                        try {
                            Experiment experiment = extractExperiment(doc);
                            listener.onExperimentFetch(experiment);
                            Log.d(TAG, "Experiment " + experimentId + " fetched successfully.");
                        } catch (Exception e) {
                            Log.d(TAG, "Error fetching " + experimentId + ".");
                        }
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
                                Experiment experiment = extractExperiment(doc);
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
                .set(compressExperiment(experiment))
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
     *
     * @param owner    User for which to find all of their owned experiments
     * @param listener the listener with the action to be taken once the experiments are fetched
     */
    public void getOwnedExperiments(User owner, ExperimentManager.OnManyExperimentsFetchListener listener) {
        String field = "ownerID";
        String id = owner.getId();
        experimentsCollection.whereEqualTo(field, id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String message = String.format("Owned experiments for user %s fetched successfully", id);
                            Log.d(TAG, message);

                            QuerySnapshot qs = task.getResult();
                            assert qs != null;
                            ArrayList<Experiment> experimentList = new ArrayList<>();
                            for (DocumentSnapshot doc : qs.getDocuments()) {
                                Experiment experiment = extractExperiment(doc);
                                experimentList.add(experiment);
                            }

                            listener.onManyExperimentsFetch(experimentList);
                        } else {
                            String message = "Failed to fetch owned experiments";
                            Log.d(TAG, message);
                        }
                    }
                });
    }

    /**
     * Searches through all experiments using a list of keywords and returns the resulting
     * experiments via a callback. This method will find experiments that match any of the
     * specified keyword (OR operator). If no keywords are given, a standard retrieval of all
     * experiments is performed.
     *
     * @param keywords String keyword to search for
     * @param listener the callback function to call once experiments have been fetched
     */
    public void searchByKeyword(List<String> keywords, OnManyExperimentsFetchListener listener) {
        /* Firebase Developer Docs, "Get data with Cloud Firestore", 2021-03-18, Apache 2.0
         * https://firebase.google.com/docs/firestore/query-data/queries#array-contains-any
         */
        if (keywords.size() == 0) {
            // Do a standard all experiment get
            setOnAllExperimentsFetchCallback(listener);
        } else {
            experimentsCollection.whereArrayContainsAny("keywords", keywords)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot query = task.getResult();
                                ArrayList<Experiment> result = new ArrayList<>();
                                for (DocumentSnapshot doc : query.getDocuments()) {
                                    Experiment exp = extractExperiment(doc);
                                    result.add(exp);
                                }
                                listener.onManyExperimentsFetch(result);
                            } else {
                                Log.d(TAG, "get failed with " + task.getException());
                            }
                        }
                    });
        }
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
     * Compresses an experiment into a Map which can be stored as a Firebase document. This method
     * does not compress the trials ArrayList of the experiment (see TrialManager addTrial method).
     * @param experiment The experiment to compress.
     * @return Returns the map representing the compressed experiment.
     */
    public Map<String, Object> compressExperiment(Experiment experiment) {

        // create map
        Map<String, Object> data = new HashMap<String, Object>();

        // set Experiment fields
        data.put(E_EXPERIMENTID_FIELD, experiment.getExperimentID());
        data.put(E_KEYWORDS_FIELD, experiment.getKeywords());

        // set ExperimentSettings fields
        data.put(ES_DESCRIPTION_FIELD, experiment.getSettings().getDescription());
        data.put(ES_R_REGIONTEXT_FIELD, experiment.getSettings().getRegion().getRegionText());
        data.put(ES_R_KMRADIUS_FIELD, experiment.getSettings().getRegion().getKmRadius());
        data.put(ES_OWNERID_FIELD, experiment.getSettings().getOwnerID());
        data.put(ES_GEOLOCATIONREQUIRED_FIELD, experiment.getSettings().getGeoLocationRequired());

        // set TrialManager fields
        data.put(TM_TYPE_FIELD, experiment.getTrialManager().getType());
        data.put(TM_IGNOREDUSERIDS, experiment.getTrialManager().getIgnoredUserIDs());
        data.put(TM_MINNUMOFTRIALS, experiment.getTrialManager().getMinNumOfTrials());
        data.put(TM_ISOPEN, experiment.getTrialManager().getIsOpen());

        return data;
    }

    /**
     * Extracts an experiment object from a Firebase document. This method assumes the document
     * hold a valid Experiment.
     *
     * @param document The document to be extracted.
     * @return The extracted experiment.
     */
    private Experiment extractExperiment(DocumentSnapshot document) {

        // get the data
        Map<String, Object> data = document.getData();

        // initialize experiment
        Experiment experiment = new Experiment();

        // set experimentID
        String experimentID = (String) data.get(E_EXPERIMENTID_FIELD);
        assert experimentID != null;
        experiment.setExperimentID(experimentID);

        // set settings
        experiment.setSettings(new ExperimentSettings());

        // set description in settings
        String description = (String) data.get(ES_DESCRIPTION_FIELD);
        assert description != null;
        experiment.getSettings().setDescription(description);

        // set region in settings
        experiment.getSettings().setRegion(new Region());

        // set regionText in region in settings
        String regionText = (String) data.get(ES_R_REGIONTEXT_FIELD);
        assert regionText != null;
        experiment.getSettings().getRegion().setRegionText(regionText);

        // set kmRadius in region in settings
        double kmRadius = (double) data.get(ES_R_KMRADIUS_FIELD);
        experiment.getSettings().getRegion().setKmRadius(kmRadius);

        // set ownerID in settings
        String ownerID = (String) data.get(ES_OWNERID_FIELD);
        assert ownerID != null;
        experiment.getSettings().setOwnerID(ownerID);

        // set geoLocationRequired in settings
        boolean geoLocationRequired = (boolean) data.get(ES_GEOLOCATIONREQUIRED_FIELD);
        experiment.getSettings().setGeoLocationRequired(geoLocationRequired);

        // set type in trialManager
        String type = (String) data.get(TM_TYPE_FIELD);
        assert type != null;
        experiment.getTrialManager().setType(type);

        // set ignoredUserIDs in trialManager
        ArrayList<String> ignoredUserIDs = (ArrayList<String>) data.get(TM_IGNOREDUSERIDS);
        assert ignoredUserIDs != null;
        experiment.getTrialManager().setIgnoredUserIDs(ignoredUserIDs);

        // set minNumOfTrials in trialManager
        int minNumOfTrials = ((Long) data.get(TM_MINNUMOFTRIALS)).intValue();
        experiment.getTrialManager().setMinNumOfTrials(minNumOfTrials);

        // set isOpen
        boolean isOpen = (boolean) data.get(TM_ISOPEN);
        experiment.getTrialManager().setIsOpen(isOpen);

        // set keywords
        ArrayList<String> keywords = (ArrayList<String>) data.get(E_KEYWORDS_FIELD);
        assert keywords != null;
        experiment.setKeywords(keywords);

        // set experimentID in trialManager
        experiment.getTrialManager().setExperimentID(experimentID);

        return experiment;
    }


    /**
     * Sets the collection path of all ExperimentManagers. Used for injection during testing.
     * @param newPath String of the new collection path.
     */
    public static void setCollectionPath(String newPath) {
        COLLECTION_PATH = newPath;
    }
}
