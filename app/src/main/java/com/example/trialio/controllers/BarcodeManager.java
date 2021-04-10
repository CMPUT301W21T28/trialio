package com.example.trialio.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.trialio.models.Barcode;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Question;
import com.example.trialio.models.Reply;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class BarcodeManager implements Serializable {

    private final CollectionReference barcodeCollection;   // does this have to be final ???

    private static final String TAG = "BarcodeForumManager";

    private static final String USERS_PATH = "usersUNIT";
    private static final String BARCODES_PATH = "barcodes";
    private static final String USERS_PATH = "users";

    public enum Result {
        SUCCESS,
        EXPERIMENT_CLOSED,
        LOCATION_DENIED,
        INVALID_EXPERIMENT,
        UNREGISTERED_BARCODE
    }


    /**
     * Returns the status of the read to the invoker
     */
    public interface OnReadResultListener {
        void onReadResult(Result result);
    }

    /**
     * This interface represents an action to be taken when an Question document is fetched from the database.
     */
    public interface OnBarcodeFetchListener {
        /**
         * This method will be called when a Question is fetched from the database.
         *
         * @param barcode the barcode that was fetched from the database
         */
        void onBarcodeFetch(Barcode barcode);
    }

    /**
     * This interface represents an action to be taken when a collection of Questions is fetched
     * from the database.
     */
    public interface OnManyBarcodesFetchListener {
        /**
         * This method will be called when a collection of Questions is fetched from the database.
         *
         * @param barcodes all the barcodes that were fetched from the database (belong to the current experiment)
         */
        void onManyBarcodesFetch(List<Barcode> barcodes);
    }

    /**
     * Constructor for QuestionForumManager
     */
    public BarcodeManager(String userID) {
        barcodeCollection = FirebaseFirestore.getInstance().collection(USERS_PATH).document(userID).collection(BARCODES_PATH);
    }

    public void createBarcode(Barcode newBarcode) {

        Log.d(TAG, "Posting barcode string " + newBarcode.getBarcodeID());
        barcodeCollection
                .document(newBarcode.getBarcodeID())
                .set(newBarcode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = String.format("Experiment %s was added successfully", newBarcode.getBarcodeID());
                        Log.d(TAG, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = String.format("Failed to add experiment %s", newBarcode.getBarcodeID());
                        Log.d(TAG, message);
                    }
                });
    }


    public void deleteBarcode(String barcodeID) {
        Log.d(TAG, "Deleting barcode " + barcodeID);
        barcodeCollection
                .document(barcodeID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = String.format("Experiment %s was deleted successfully", barcodeID);
                        Log.d(TAG, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = String.format("Failed to delete experiment %s", barcodeID);
                        Log.d(TAG, message);
                    }
                });
    }

    /**
     * Deletes all barcodes, effectively deleting the barcode collection
     */
    public void deleteAllBarcodes() {
        Log.d(TAG, "Deleting all barcodes");
        barcodeCollection
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        // delete all of the trials in the trial collection
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            doc.getReference().delete();
                        }
                    }
                });
    }

    /**
     * Copies all barcodes to a new BarcodeManager.
     *
     * @param newManager the BarcodeManager to copy over to
     * @param clearAfter indicates if current BarcodeManager should be cleared after the transfer
     */
    public void transferTo(BarcodeManager newManager, boolean clearAfter) {
        setOnAllBarcodesFetchCallback(new OnManyBarcodesFetchListener() {
            @Override
            public void onManyBarcodesFetch(List<Barcode> barcodes) {
                for (Barcode b : barcodes) {
                    newManager.createBarcode(b);
                }
                if (clearAfter) {
                    deleteAllBarcodes();
                }
            }
        });
    }


    /**
     * Sets a function to be called when a barcode is fetched
     *
     * @param barcodeID the id of the barcode to fetch
     * @param listener  the function to be called when the experiment is fetched
     */
    public void setOnBarcodeFetchListener(String barcodeID, BarcodeManager.OnBarcodeFetchListener listener) {
        /* Firebase Developer Docs, "Get a document", 2021-03-09, Apache 2.0
         * https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
         */
        Log.d(TAG, "Fetching barcode");
        DocumentReference docRef = barcodeCollection.document(barcodeID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        Barcode barcode = extractBarcodeDocument(doc);
                        Log.d(TAG, "Barcode fetched successfully.");
                        listener.onBarcodeFetch(barcode);
                    } else {
                        Log.d(TAG, "No barcode(s) found");
                        listener.onBarcodeFetch(null);
                    }
                } else {
                    Log.d(TAG, "Barcode fetch failed with " + task.getException());
                }
            }
        });
    }


    /**
     * Sets a function to be called when all barcodes are fetched
     *
     * @param listener the function to be called when the barcodes are fetched
     */
    public void setOnAllBarcodesFetchCallback(BarcodeManager.OnManyBarcodesFetchListener listener) {
        /* Firebase Developer Docs, "Get all documents in a collection", 2021-03-09, Apache 2.0
         * https://firebase.google.com/docs/firestore/query-data/get-data#get_all_documents_in_a_collection
         */
        Log.d(TAG, "Fetching all barcodes from collection");
        barcodeCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String message = "All barcodes fetched successfully";
                            Log.d(TAG, message);

                            QuerySnapshot qs = task.getResult();
                            ArrayList<Barcode> barcodeList = new ArrayList<>();

                            for (DocumentSnapshot doc : qs.getDocuments()) {
                                // retrieves all documents (barcodes) within barcodeForum collection
                                Barcode barcode = extractBarcodeDocument(doc);
                                barcodeList.add(barcode);
                            }
                            listener.onManyBarcodesFetch(barcodeList);
                        } else {
                            String message = "Failed to fetch all barcodes";
                            Log.d(TAG, message);
                        }
                    }
                });
    }

    private Barcode extractBarcodeDocument(DocumentSnapshot document) {
        Barcode barcode = document.toObject(Barcode.class);
        return barcode;

    }

//------------------------------------------------------------------------------------------------------------------------------------------------------------------------


//    TODO: do you need me ?
//    public static void readBarcode() {}
    /*
     * TODO:
     *  1) how to get experiment field + where
     *
     * */

    /**
     * to generateBarcode for qrfragment
     *
     * @param barcodeID
     * @return
     */
    public static Bitmap generateBarcode(String barcodeID) {
        BitMatrix result = null;
        try {
            result = new MultiFormatWriter().encode(barcodeID, BarcodeFormat.CODE_128, 1000, 200, null);
        } catch (WriterException writerException) {
            writerException.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];

        for (int x = 0; x < height; x++) {
            int offset = x * width;
            for (int k = 0; k < width; k++) {
                pixels[offset + k] = result.get(k, x) ? BLACK : WHITE;
            }
        }

        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        myBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return myBitmap;
    }

    /**
     * for storing barcode to barcode collection
     *
     * @param barcodeID
     * @param experiment
     * @param result
     */
    public void registerBarcode(String barcodeID, Experiment experiment, String result) {
        Barcode barcode = new Barcode(experiment, result, barcodeID);
        this.createBarcode(barcode);
    }

    /**
     * readBarcode reads registered barcode and add a new trial to its responsible experiment
     *
     * @param context  the context in which the barcode was scanned
     * @param input    the raw input of the scanned barcode
     * @param listener the listener callback to pass the result of the read
     */
    //input contains barcode info, use the info to fetch the document from firebase
    public void readBarcode(Context context, String input, OnReadResultListener listener) {
        setOnBarcodeFetchListener(input, new OnBarcodeFetchListener() {
            @Override
            public void onBarcodeFetch(Barcode barcode) {
                if (barcode == null) {
                    // scanned barcode is not registered by the user
                    listener.onReadResult(Result.UNREGISTERED_BARCODE);
                    return;
                }
                Experiment experiment = barcode.getExperiment();
                String type = barcode.getExperiment().getTrialManager().getType();

                // fetch the experiment again from ExperimentManager to make sure it still exists
                ExperimentManager experimentManager = new ExperimentManager();
                experimentManager.setOnExperimentFetchListener(experiment.getExperimentID(), new ExperimentManager.OnExperimentFetchListener() {
                    @Override
                    public void onExperimentFetch(Experiment experiment) {
                        // input validation: make sure experiment is valid
                        if (experiment == null) {
                            Log.w(TAG, "Experiment for QR code is not valid");
                            listener.onReadResult(Result.INVALID_EXPERIMENT);
                            return;
                        }

                        // determine if experiment is geolocation enabled
                        boolean isLocationReq = experiment.getSettings().getGeoLocationRequired();
                        CreateTrialCommand createCommand;

                        if (type.equals(ExperimentTypeUtility.getBinomialType())) {
                            // create a binomial trial
                            boolean binomialRes = Boolean.parseBoolean(barcode.getTrialResult());
                            createCommand = new CreateBinomialTrialCommand(
                                    context, isLocationReq, binomialRes,
                                    trial -> addTrialToExperiment(trial, experiment, listener)
                            );

                        } else if (type.equals(ExperimentTypeUtility.getCountType())) {
                            // create a count trial
                            createCommand = new CreateCountTrialCommand(
                                    context, isLocationReq,
                                    trial -> addTrialToExperiment(trial, experiment, listener)
                            );
                        } else if (type.equals(ExperimentTypeUtility.getNonNegativeType())) {
                            // create a non-negative trial
                            int nonNegRes = Integer.parseInt(barcode.getTrialResult());
                            createCommand = new CreateNonNegativeTrialCommand(
                                    context, isLocationReq, nonNegRes,
                                    trial -> addTrialToExperiment(trial, experiment, listener)
                            );
                        } else if (type.equals(ExperimentTypeUtility.getMeasurementType())) {
                            // create a measurement trial
                            double measurementRes = Double.parseDouble(barcode.getTrialResult());
                            createCommand = new CreateMeasurementTrialCommand(
                                    context, isLocationReq, measurementRes, experiment.getUnit(),
                                    trial -> addTrialToExperiment(trial, experiment, listener)
                            );
                        } else {
                            // error
                            throw new IllegalArgumentException("Invalid experiment type for scanned trial");
                        }

                        createCommand.execute();

                    }
                });
            }
        });

    }

    /**
     * Adds a created trial to the experiment. Used as a common callback between all the create trial
     * commands.
     *
     * @param trial      the trial to add
     * @param experiment the experiment to add to
     * @param listener   the listener callback to pass the result of the operation
     */
    private void addTrialToExperiment(Trial trial, Experiment experiment, OnReadResultListener listener) {
        // if no trial was created, location error occurred
        if (trial == null /*&& experiment.getSettings().getGeoLocationRequired()*/) {
            listener.onReadResult(Result.LOCATION_DENIED);
            return;
        }

        // add the created trial
        ExperimentManager experimentManager = new ExperimentManager();
        boolean addSuccess = experiment.getTrialManager().addTrial(trial);
        experimentManager.editExperiment(experiment.getExperimentID(), experiment);

        if (addSuccess) {
            listener.onReadResult(Result.SUCCESS);
        } else {
            listener.onReadResult(Result.EXPERIMENT_CLOSED);
        }
    }
}