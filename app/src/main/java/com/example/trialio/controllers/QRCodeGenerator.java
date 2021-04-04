package com.example.trialio.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Date;

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
    private static User current_user;
    private static Boolean locationRequired;
    private static Context context;

    public static Bitmap generateForTrial(Experiment experiment, String strResult, Location location){
        String infoResult = "";
        Date date = new Date();
        if (experiment.getTrialManager().getType().equals("BINOMIAL")){
            infoResult = experiment.getTrialManager().getType() + "\n" +  strResult + "\n" +  experiment.getExperimentID() + "\n" + location.getLatitude()+ "\n" + location.getLongitude();
        } else if (experiment.getTrialManager().getType().equals("COUNT")){
            infoResult = experiment.getTrialManager().getType() + "\n" +  strResult + "\n" +  experiment.getExperimentID() + "\n" + location.getLatitude()+ "\n" + location.getLongitude();
        } else if (experiment.getTrialManager().getType().equals("NONNEGATIVE")){
            infoResult = experiment.getTrialManager().getType() + "\n" +  strResult + "\n" +  experiment.getExperimentID() + "\n" + location.getLatitude()+ "\n" + location.getLongitude();
        } else if (experiment.getTrialManager().getType().equals("MEASUREMENT")){
            infoResult = experiment.getTrialManager().getType() + "\n" +  strResult + "\n" +  experiment.getExperimentID() + "\n" + location.getLatitude()+ "\n" + location.getLongitude();
        }
        BitMatrix result = null;
        try{
            result = new MultiFormatWriter().encode(infoResult, BarcodeFormat.QR_CODE, 300, 300, null);
        } catch (WriterException writerException) {
            writerException.printStackTrace();
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];

        for (int x = 0; x < height; x ++){
            int offset = x * width;
            for (int k = 0; k < width; k++) {
                pixels[offset + k] = result.get(k,x) ? BLACK : WHITE;
            }
        }

        Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        myBitmap.setPixels(pixels,0,width,0,0,width,height);
        return myBitmap;
    }


    /**
     * readQR takes in the input that is encoded in the code then create a new trial with its info
     * @param input 
     * @param user
     */
    public static void readQR(String[] input, User user){
        if (input[0].equals("BINOMIAL")){
            current_user = user;
            Location location = new Location();
            Date date = new Date();

            location.setLatitude(Double.valueOf(input[3]));
            location.setLongitude(Double.valueOf(input[3]));

            ExperimentManager experimentManager = new ExperimentManager();
            experimentManager.setOnExperimentFetchListener(input[2], new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment new_experiment) {
                    BinomialTrial new_trial = new BinomialTrial(current_user.getUsername(), location, date, Boolean.parseBoolean(input[1]));
                    new_experiment.getTrialManager().addTrial(new_trial);
                    experimentManager.editExperiment(input[2],new_experiment);
                }
            });

        } else if (input[0].equals("COUNT")){
            current_user = user;
            Date date = new Date();
            Location location = new Location();

            location.setLatitude(Double.valueOf(input[3]));
            location.setLongitude(Double.valueOf(input[3]));

            ExperimentManager experimentManager = new ExperimentManager();
            experimentManager.setOnExperimentFetchListener(input[2], new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment new_experiment) {
                    CountTrial new_trial = new CountTrial(current_user.getUsername(), location, date);
                    new_experiment.getTrialManager().addTrial(new_trial);
                    experimentManager.editExperiment(input[2],new_experiment);
                }
            });
        } else if (input[0].equals("NONNEGATIVE")){
            current_user = user;
            Date date = new Date();
            Location location = new Location();

            location.setLatitude(Double.valueOf(input[3]));
            location.setLongitude(Double.valueOf(input[3]));

            ExperimentManager experimentManager = new ExperimentManager();
            experimentManager.setOnExperimentFetchListener(input[2], new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment new_experiment) {
                    NonNegativeTrial new_trial = new NonNegativeTrial(current_user.getUsername(), location, date, Integer.parseInt(input[1]));
                    new_experiment.getTrialManager().addTrial(new_trial);
                    experimentManager.editExperiment(input[2],new_experiment);
                }
            });
        } else if (input[0].equals("MEASUREMENT")){
            current_user = user;
            Date date = new Date();
            Location location = new Location();

            location.setLatitude(Double.valueOf(input[3]));
            location.setLongitude(Double.valueOf(input[3]));

            ExperimentManager experimentManager = new ExperimentManager();
            experimentManager.setOnExperimentFetchListener(input[2], new ExperimentManager.OnExperimentFetchListener() {
                @Override
                public void onExperimentFetch(Experiment new_experiment) {

                    MeasurementTrial new_trial = new MeasurementTrial(current_user.getUsername(), location, date, Double.parseDouble(input[1]), input[3]);
                    new_experiment.getTrialManager().addTrial(new_trial);
                    experimentManager.editExperiment(input[2],new_experiment);
                }
            });
        }

    }





}