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
    private Context context;
    private ZXingScannerView scannerView;
    private TextView txtResult;

    /**
     * onCreate first ask for user permission, then proceeds to scan using camera
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        scannerView = (ZXingScannerView)findViewById(R.id.scanner);
        txtResult = (TextView)findViewById(R.id.txtResultQR);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    /**
     * fetch results from the qr code
     * @param rawResult
     */
    @Override
    public void handleResult(Result rawResult){
        processResult(rawResult.getText());
        Intent intent = new Intent(context, ExperimentActivity.class);
        onDestroy();
        startActivity(intent);
    }

    private void processResult(String text){

    }
}