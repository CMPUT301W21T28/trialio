package com.example.trialio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trialio.R;
import com.example.trialio.controllers.QRCodeGenerator;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
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
public class ScanningActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView scannerView;
    private User currentUser;

    /**
     * onCreate first ask for user permission, then proceeds to scan using camera
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
        Bundle bundle = getIntent().getExtras();
        currentUser = (User) bundle.getSerializable("user_scan");
        scannerView = (ZXingScannerView)findViewById(R.id.scanner);

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
     * @param rawResult
     */
    @Override
    public void handleResult(Result rawResult){
        processResult(rawResult.getText());
        scannerView.stopCamera();
        finish();

    }

    /**
     * sends the input from qr code to readQR in QRCodeGenerator to create new trials
     * @param text
     */
    private void processResult(String text){
        String processed = text;
        String [] items = processed.split("\n");
        QRCodeGenerator.readQR(items, currentUser);
    }
}