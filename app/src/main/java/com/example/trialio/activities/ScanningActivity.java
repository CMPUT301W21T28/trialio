package com.example.trialio.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trialio.R;
import com.example.trialio.controllers.BarcodeManager;
import com.example.trialio.controllers.QRCodeGenerator;
import com.example.trialio.fragments.QRFragment;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Location;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * this activity opens when user clicks the camera button in a experiment activity
 */
public class ScanningActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scannerView;
    private User currentUser;
    private Experiment experiment;
    private String result;
    private Boolean isBarcode;
    private BarcodeManager barcodeManager;
    private String parentActivity;
    private Context context = this;

    private final String TAG = "ScanningActivity";


    /**
     * onCreate first ask for user permission, then proceeds to scan using camera
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
        Bundle bundle = getIntent().getExtras();
        Intent intent = getIntent();
        parentActivity = intent.getStringExtra("Parent");
        currentUser = (User) bundle.getSerializable("user_scan");
        experiment = (Experiment) bundle.getSerializable("experiment");
        result = (String) bundle.getSerializable("result");
        isBarcode = bundle.getBoolean("isBarcode");

        scannerView = (ZXingScannerView) findViewById(R.id.scanner);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(ScanningActivity.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ScanningActivity.this, "Need to accept permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }


    /**
     * fetch results from the qr code
     * stop the camera and return to experiment activtiy
     *
     * @param rawResult
     */
    @Override
    public void handleResult(Result rawResult) {
        processResult(rawResult.getText());
        scannerView.stopCamera();
        finish();

    }

    /**
     * sends the input from qr code to readQR in QRCodeGenerator to create new trials
     *
     * @param text
     */
    private void processResult(String text) {
        Log.d(TAG, "Process scanned result: isBarcode = " + isBarcode.toString());
        String[] items = text.split("\n");
        Log.d(TAG, "Raw Input: " + Arrays.toString(items));
        /*
         * reads in from which activity is scanning activity called from
         * if ExperimentActivity is calling Scanning activity, it means user wants to scan a QR or a barcode
         * if its not experiment activity it means user is trying to register a barcode
         */
        boolean isQR = String.valueOf(items.length).equals("3"); // otherwise it's barcode
        boolean isAddTrial = parentActivity.equals("ExperimentActivity")
                || parentActivity.equals("MainActivity"); // otherwise it's register (barcode)

        if (isAddTrial) {
            // user wants to scan code to record trial
            if (isQR) {
                // user is scanning a QR code to add trial
                Log.d(TAG, "QR Code identified");
                QRCodeGenerator.readQR(getApplicationContext(), items, new QRCodeGenerator.OnReadResultListener() {
                    @Override
                    public void onReadResult(QRCodeGenerator.Result result) {
                        switch (result) {
                            case SUCCESS:
                                displaySuccessToast();
                                break;

                            case EXPERIMENT_CLOSED:
                                displayExperimentClosedToast();
                                break;

                            case LOCATION_DENIED:
                                displayNoLocationToast();
                                break;

                            case INVALID_EXPERIMENT:
                                displayInvalidExperimentToast();
                                break;
                        }
                    }
                });

            } else {
                // user is scanning a barcode to add trial
                Log.d(TAG, "Barcode identified");
                barcodeManager = new BarcodeManager(currentUser.getUsername());
                barcodeManager.readBarcode(getApplicationContext(), text, new BarcodeManager.OnReadResultListener() {
                    @Override
                    public void onReadResult(BarcodeManager.Result result) {
                        switch (result) {
                            case SUCCESS:
                                displaySuccessToast();
                                break;

                            case EXPERIMENT_CLOSED:
                                displayExperimentClosedToast();
                                break;

                            case LOCATION_DENIED:
                                displayNoLocationToast();
                                break;

                            case INVALID_EXPERIMENT:
                                displayInvalidExperimentToast();
                                break;

                            case UNREGISTERED_BARCODE:
                                displayUnRegBarcodeToast();
                                break;
                        }
                    }
                });
            }
        } else {
            // if intent is not coming from Experiment Activity i.e. QRActivity
            // this is used for registering new barcode
            if (isQR) {
                // user tried to register a QR Code
                displayRegisterQRCodeAsBarcodeToast();
            } else {
                // register the barcode
                barcodeManager = new BarcodeManager(currentUser.getUsername());
                barcodeManager.registerBarcode(text, experiment, result);
            }
        }


    }

    /**
     * Displays error message to the user cannot register a QR Code as a custom barcode
     */
    private void displayRegisterQRCodeAsBarcodeToast() {
        String message = "Please do not register a QR code as a barcode";
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Displays error message to the user that trial could not be added because location services
     * are not enabled
     */
    private void displayNoLocationToast() {
        String message = "Unable to add trial: Please enable location permissions";
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Displays error message to the user that trial could not be added because location services
     * are not enabled
     */
    private void displayExperimentClosedToast() {
        String message = "Unable to add trial: This experiment is closed to new trials";
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Displays message to user when trial was added successfully.
     */
    private void displaySuccessToast() {
        String message = "Trial added successfully";
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Displays message when invalid experiment was provided and no trial could be added
     */
    private void displayInvalidExperimentToast() {
        String message = "Unable to add trial: This experiment does not exist";
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Displays a message when the user scans an unregistered barcode
     */
    private void displayUnRegBarcodeToast() {
        String message = "Unable to add trial: This barcode has not been registered";
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }
}

