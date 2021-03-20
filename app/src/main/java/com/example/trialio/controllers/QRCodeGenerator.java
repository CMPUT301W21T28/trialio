package com.example.trialio.controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;

import com.example.trialio.R;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

/**
 * QRCodeGenerator generates QR code for trials when called
 */
public class QRCodeGenerator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
    }

    /**
     * generateForTrial will take in trial and experiment to produce a QR code for trial with the type the experiment is
     * @param trial
     * @param experiment
     * @return
     *  Image: QRCode
     */
    public Image generateForTrial(Trial trial, Experiment experiment){
       return null;
    };
}