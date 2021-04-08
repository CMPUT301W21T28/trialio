package com.example.trialio.controllers;

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

    private CollectionReference barcodeCollection;   // does this have to be final ???
    private static User current_user;

    private static final String TAG = "BarcodeForumManager";

    private static final String EXPERIMENT_PATH = "users-v5";
    private static final String BARCODES_PATH = "barcodes";


    /**
     * Constructor for QuestionForumManager
     */
    public BarcodeManager(String associatedExperimentID) {
        barcodeCollection = FirebaseFirestore.getInstance().collection(EXPERIMENT_PATH).document(associatedExperimentID).collection(BARCODES_PATH);
    }

    public BarcodeManager() {    }


//    /**
//     * Generates a new unique barcode ID
//     * @return unique ID for a new barcode which is about to be posted
//     */
//
//    public String getNewPostID() { return this.barcodeForumCollection.document().getId(); }
//
//    public String getNewReplyID(String barcodeID) {
//        return this.barcodeForumCollection.document(barcodeID).collection("Replies").document().getId();
//    }


    /**
     * This interface represents an action to be taken when an Question document is fetched from the database.
     */
    public interface OnBarcodeFetchListener {
        /**
         * This method will be called when a Question is fetched from the database.
         *
         * @param barcode the barcode that was fetched from the database
         */
        void onBarcodeFetch(String barcode);
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
        void onManyBarcodesFetch(List<String> barcodes);
    }


    public void createBarcode(String barcodeString) {
        Map<String, Object> newBarcode = new HashMap<>();
        newBarcode.put("Barcode Info", barcodeString);

        Log.d(TAG, "Posting barcode string " + barcodeString);
        barcodeCollection
                .add(newBarcode)   //TODO: might be annoying to use the document name with the functions bellow ****
                //TODO ERROR HERE
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding barcode", e);
                    }
                });
    }


    public void deleteBarcode(String barcodeString) {
        Log.d(TAG, "Deleting barcode " + barcodeString);
        barcodeCollection
                .document(barcodeString)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String message = String.format("Experiment %s was deleted successfully", barcodeString);
                        Log.d(TAG, message);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = String.format("Failed to delete experiment %s", barcodeString);
                        Log.d(TAG, message);
                    }
                });
    }


    /**
     * Sets a function to be called when a barcode is fetched
     *
     * @param barcodeString the id of the barcode to fetch
     * @param listener      the function to be called when the experiment is fetched
     */
    public void setOnBarcodeFetchListener(String barcodeString, BarcodeManager.OnBarcodeFetchListener listener) {
        /* Firebase Developer Docs, "Get a document", 2021-03-09, Apache 2.0
         * https://firebase.google.com/docs/firestore/query-data/get-data#get_a_document
         */
        Log.d(TAG, "Fetching barcode");
        DocumentReference docRef = barcodeCollection.document(barcodeString);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String barcode = extractBarcodeDocument(doc);

                        listener.onBarcodeFetch(barcode);
                        Log.d(TAG, "Barcode fetched successfully.");
                    } else {
                        Log.d(TAG, "No barcode(s) found");
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
                            ArrayList<String> barcodeList = new ArrayList<>();

                            for (DocumentSnapshot doc : qs.getDocuments()) {
                                // retrieves all documents (barcodes) within barcodeForum collection
                                String barcodeString = doc.getString("Barcode Info");
                                barcodeList.add(barcodeString);
                            }
                            listener.onManyBarcodesFetch(barcodeList);
                        } else {
                            String message = "Failed to fetch all barcodes";
                            Log.d(TAG, message);
                        }
                    }
                });
    }

    private String extractBarcodeDocument(DocumentSnapshot document) {
        // TODO custom mapping here
        String barcodeInfo = document.getString("Barcode Info");
        return barcodeInfo;
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
     * @param input
     * @param user
     * @param experiment
     * @param result
     */
    public void registerBarcode(String input, User user, Experiment experiment, String result) {
        if (experiment.getTrialManager().getType().equals("BINOMIAL")) {
            String infoResult = input + "\n" + experiment.getTrialManager().getType() + "\n" + result + "\n" + experiment.getExperimentID();
            this.createBarcode(infoResult);
        } else if (experiment.getTrialManager().getType().equals("COUNT")) {
            String infoResult = input + "\n" + experiment.getTrialManager().getType() + "\n" + result + "\n" + experiment.getExperimentID();
            this.createBarcode(infoResult);

        } else if (experiment.getTrialManager().getType().equals("NONNEGATIVE")) {
            String infoResult = input + "\n" + experiment.getTrialManager().getType() + "\n" + result + "\n" + experiment.getExperimentID();
            this.createBarcode(infoResult);

        } else if (experiment.getTrialManager().getType().equals("MEASUREMENT")) {
            String infoResult = input + "\n" + experiment.getTrialManager().getType() + "\n" + result + "\n" + experiment.getExperimentID();
            this.createBarcode(infoResult);

        }
    }

    public static void readBarcode(String[] input, Location location, User user){
        if (input[1].equals("BINOMIAL")){
            current_user = user;
            Date date = new Date();
            ExperimentManager experimentManager = new ExperimentManager();
            experimentManager.setOnExperimentFetchListener(input[3], new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment new_experiment) {
                    BinomialTrial new_trial = new BinomialTrial(current_user.getUsername(), location, date, Boolean.parseBoolean(input[2]));
                    new_experiment.getTrialManager().addTrial(new_trial);
                    experimentManager.editExperiment(input[3],new_experiment);
                }
            });

        } else if (input[1].equals("COUNT")){
            current_user = user;
            Date date = new Date();
            ExperimentManager experimentManager = new ExperimentManager();
            experimentManager.setOnExperimentFetchListener(input[3], new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment new_experiment) {
                    CountTrial new_trial = new CountTrial(current_user.getUsername(), location, date);
                    new_experiment.getTrialManager().addTrial(new_trial);
                    experimentManager.editExperiment(input[3],new_experiment);
                }
            });
        } else if (input[1].equals("NONNEGATIVE")){
            current_user = user;
            Date date = new Date();
            ExperimentManager experimentManager = new ExperimentManager();
            experimentManager.setOnExperimentFetchListener(input[3], new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment new_experiment) {
                    NonNegativeTrial new_trial = new NonNegativeTrial(current_user.getUsername(), location, date, Integer.parseInt(input[2]));
                    new_experiment.getTrialManager().addTrial(new_trial);
                    experimentManager.editExperiment(input[3],new_experiment);
                }
            });
        } else if (input[1].equals("MEASUREMENT")){
            current_user = user;
            Date date = new Date();
            ExperimentManager experimentManager = new ExperimentManager();
            experimentManager.setOnExperimentFetchListener(input[3], new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment new_experiment) {

                    MeasurementTrial new_trial = new MeasurementTrial(current_user.getUsername(), location, date, Double.parseDouble(input[2]), input[3]);
                    new_experiment.getTrialManager().addTrial(new_trial);
                    experimentManager.editExperiment(input[3],new_experiment);
                }
            });
        }

    }



}