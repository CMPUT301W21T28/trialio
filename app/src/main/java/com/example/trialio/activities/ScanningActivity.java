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

    private final String TAG = "scanningactivity";


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
        Log.d(TAG, "in ProcessResult");
        Log.d(TAG, String.valueOf(isBarcode));

        // it reads in from which activity is scanning activity called from
        // if ExperimentActivity is calling Scanning activity, it means user wants to scan a QR or a barcode
        // if its not experiment activity it means user is trying to register a barcode
        if (parentActivity.equals("ExperimentActivity")) {
            String processed = text;
            String[] items = processed.split("\n");
            Log.d(TAG, String.valueOf(items.length));
            Log.d(TAG, processed);
            // if its a QRCode the length is 3
            if (String.valueOf(items.length).equals("3")) {
                if (experiment.getSettings().getGeoLocationRequired()) {
                    Task<android.location.Location> locTask = Location.requestLocation(context);
                    if (locTask == null) {
                        return;
                    }
                    locTask.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(android.location.Location loc) {
                            Location location = new Location();
                            location.setLatitude(loc.getLatitude());
                            location.setLongitude(loc.getLongitude());
                            QRCodeGenerator.readQR(items, location, currentUser);
                        }
                    });
                }
                // if its a Barcode
            } else {
                if (experiment.getSettings().getGeoLocationRequired()) {
                    Task<android.location.Location> locTask = Location.requestLocation(context);
                    if (locTask == null) {
                        return;
                    }
                    locTask.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(android.location.Location loc) {
                            Location location = new Location();
                            location.setLatitude(loc.getLatitude());
                            location.setLongitude(loc.getLongitude());
                             barcodeManager.readBarcode(items, location, currentUser);
                        }
                    });
                }
            }
            // if intent is not coming from Experiment Activity i.e. QRActivity
            // this is used for registering new barcode
        } else {
            barcodeManager = new BarcodeManager(experiment.getExperimentID());
            barcodeManager.registerBarcode(text, currentUser, experiment, result);
        }


    }
}