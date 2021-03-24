package com.example.trialio.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.trialio.R;
import com.example.trialio.controllers.QRCodeGenerator;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

import java.util.Date;

public class QRFragment extends DialogFragment {
    private ImageView imgQR;
    private Experiment experiment;
    private Trial trial;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_show_qr_code, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
        Bundle bundle = getArguments();
        trial = (Trial) bundle.getSerializable("trial");
        experiment = (Experiment) bundle.getSerializable("experiment");
        imgQR = view.findViewById(R.id.imgQRCode);
        Bitmap qrCode = QRCodeGenerator.createBitmap(String.valueOf(trial));
        imgQR.setImageBitmap(qrCode);



        builder.setView(view).setTitle("Show QR:").setNegativeButton("Cancel",null);
        return builder.create();
    }

}

