package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.activities.QRBinomialActivity;
import com.example.trialio.controllers.QRCodeGenerator;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Location;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.Reference;
import java.util.Date;

public class QRFragment extends DialogFragment {
    private ImageView imgQR;
    private Experiment experiment;
    private String result;
    private String unit;
    private Trial trial;
    private Integer position;
    private Boolean location;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_show_qr_code, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle = getArguments();
        experiment = (Experiment) bundle.getSerializable("experiment");
        result = bundle.getString("result");

        location = experiment.getSettings().getGeoLocationRequired();
        Location loc = new Location();
        Date date = new Date();

        if (location){
            loc.getCurrentLocation(getContext(),getActivity());
        }

        imgQR = view.findViewById(R.id.imgQRCode);
        Bitmap qrcode = QRCodeGenerator.generateForTrial(experiment, result, loc);
        imgQR.setImageBitmap(qrcode);


        builder.setView(view).setTitle("Show QR:").setNegativeButton("Cancel",null);
        return builder.create();
    }

}

