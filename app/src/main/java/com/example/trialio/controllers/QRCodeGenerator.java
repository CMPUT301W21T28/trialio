package com.example.trialio.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Barcode;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Date;
import java.util.function.LongFunction;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;



/*
Creating QR Code
Video Title: How To Make QR Code Generator Using Zxing (Zebra Crossing) Library - Android Studio Tutorial
Link to Video: https://www.youtube.com/watch?v=zHStZwXtbj0&ab_channel=Programmity
Video uploader: Programmity
Uploader's channel: https://www.youtube.com/channel/UC0gObgODeCoWwk5wYysAidQ
 */


/**
 * QRCodeGenerator generates QR code for trials when called
 */
public class QRCodeGenerator extends AppCompatActivity {
    private static final String TAG = "qrgenerator";
    private static ExperimentManager experimentManager;
    private static BarcodeManager barcodeManager;
    private static User current_user;
    private static Context context;


    /**
     * Generates a QR code for a given trial result
     *
     * @param experiment the experiment to encode
     * @param strResult  the string result to encode
     * @return the bitmap that represents the QR code
     */
    public static Bitmap generateForTrial(Experiment experiment, String strResult, String information) {
        String infoResult = "";
        Date date = new Date();
        BitMatrix result = null;
        try {
            infoResult = experiment.getTrialManager().getType() + "\n" + strResult + "\n" + experiment.getExperimentID();
            result = new MultiFormatWriter().encode(infoResult, BarcodeFormat.QR_CODE, 300, 300, null);
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

    // TODO: Callback for result of scan, enum to represent different statuses

    /**
     * readQR takes in the input that is encoded in the code then create a new trial with its info
     *
     * @param input
     * @param user
     */
    public static void readQR(Context context, String[] input, Location location, User user) {
        // parse the raw input
        String type = input[0];
        String result = input[1];
        String experimentId = input[2];

        // fetch experiment encoded in QR code
        ExperimentManager experimentManager = new ExperimentManager();
        experimentManager.setOnExperimentFetchListener(experimentId, new ExperimentManager.OnExperimentFetchListener() {
            @Override
            public void onExperimentFetch(Experiment experiment) {
                // input validation: make sure experiment is valid
                if (experiment == null) {
                    Log.w(TAG, "Experiment for QR code is not valid");
                    return;
                }

                // determine if experiment is geolocation enabled
                boolean isLocationReq = experiment.getSettings().getGeoLocationRequired();
                CreateTrialCommand createCommand;

                if (type.equals(ExperimentTypeUtility.getBinomialType())) {
                    // create a binomial trial
                    boolean binomialRes = Boolean.parseBoolean(result);
                    createCommand = new CreateBinomialTrialCommand(
                            context, isLocationReq, binomialRes,
                            trial -> addTrialToExperiment(trial, experiment)
                    );

                } else if (type.equals(ExperimentTypeUtility.getCountType())) {
                    // create a count trial
                    createCommand = new CreateCountTrialCommand(
                            context, isLocationReq,
                            trial -> addTrialToExperiment(trial, experiment)
                    );
                } else if (type.equals(ExperimentTypeUtility.getNonNegativeType())) {
                    // create a non-negative trial
                    int nonNegRes = Integer.parseInt(result);
                    createCommand = new CreateNonNegativeTrialCommand(
                            context, isLocationReq, nonNegRes,
                            trial -> addTrialToExperiment(trial, experiment)
                    );
                } else if (type.equals(ExperimentTypeUtility.getMeasurementType())) {
                    // create a measurement trial
                    double measurementRes = Double.parseDouble(result);
                    createCommand = new CreateMeasurementTrialCommand(
                            context, isLocationReq, measurementRes,
                            trial -> addTrialToExperiment(trial, experiment)
                    );
                } else {
                    // error
                    throw new IllegalArgumentException("Invalid experiment type for scanned trial");
                }

                createCommand.execute();
            }
        });


//        if (input[0].equals("BINOMIAL")) {
//            current_user = user;
//            Date date = new Date();
//
//            experimentManager.setOnExperimentFetchListener(input[2], new ExperimentManager.OnExperimentFetchListener() {
//                @Override
//                public void onExperimentFetch(Experiment new_experiment) {
//                    if (new_experiment != null) {
//                        BinomialTrial new_trial = new BinomialTrial(current_user.getId(), location, date, Boolean.parseBoolean(input[1]));
//                        new_experiment.getTrialManager().addTrial(new_trial);
//                        experimentManager.editExperiment(input[2], new_experiment);
//                    } else {
//                        Log.e(TAG, "Failed to load experiments");
//                    }
//                }
//            });
//
//        } else if (input[0].equals("COUNT")) {
//            current_user = user;
//            Date date = new Date();
//
//
//            experimentManager.setOnExperimentFetchListener(input[2], new ExperimentManager.OnExperimentFetchListener() {
//
//                @Override
//                public void onExperimentFetch(Experiment new_experiment) {
//                    if (new_experiment != null) {
//                        CountTrial new_trial = new CountTrial(current_user.getId(), location, date);
//                        new_experiment.getTrialManager().addTrial(new_trial);
//                        experimentManager.editExperiment(input[2], new_experiment);
//                    } else {
//                        Log.e(TAG, "Failed to load experiments");
//                    }
//                }
//            });
//        } else if (input[0].equals("NONNEGATIVE")) {
//            current_user = user;
//            Date date = new Date();
//            experimentManager.setOnExperimentFetchListener(input[2], new ExperimentManager.OnExperimentFetchListener() {
//                @Override
//                public void onExperimentFetch(Experiment new_experiment) {
//                    if (new_experiment != null) {
//                        NonNegativeTrial new_trial = new NonNegativeTrial(current_user.getId(), location, date, Integer.parseInt(input[1]));
//                        new_experiment.getTrialManager().addTrial(new_trial);
//                        experimentManager.editExperiment(input[2], new_experiment);
//                    } else {
//                        Log.e(TAG, "Failed to load experiments");
//                    }
//                }
//            });
//        } else if (input[0].equals("MEASUREMENT")) {
//            current_user = user;
//            Date date = new Date();
//
//            experimentManager.setOnExperimentFetchListener(input[2], new ExperimentManager.OnExperimentFetchListener() {
//                @Override
//                public void onExperimentFetch(Experiment new_experiment) {
//                    if (new_experiment != null) {
//                        MeasurementTrial new_trial = new MeasurementTrial(current_user.getId(), location, date, Double.parseDouble(input[1]), input[3]);
//                        new_experiment.getTrialManager().addTrial(new_trial);
//                        experimentManager.editExperiment(input[2], new_experiment);
//                    } else {
//                        Log.e(TAG, "Failed to load experiments");
//                    }
//                }
//            });
//        }

    }

    private static void addTrialToExperiment(Trial trial, Experiment experiment) {
        ExperimentManager experimentManager = new ExperimentManager();
        experiment.getTrialManager().addTrial(trial);
        experimentManager.editExperiment(experiment.getExperimentID(), experiment);
    }
}