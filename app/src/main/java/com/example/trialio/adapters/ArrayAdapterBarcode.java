package com.example.trialio.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.R;
import com.example.trialio.controllers.BarcodeManager;
import com.example.trialio.controllers.ChangeUsernameCommand;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Barcode;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;

import java.util.ArrayList;

public class ArrayAdapterBarcode extends ArrayAdapter {

    private Context context;
    private ArrayList<String> barcodeList;
    private Experiment experiment;

    public ArrayAdapterBarcode (Context context, ArrayList<String> barcodeList, Experiment experiment) {
        super(context, 0, barcodeList);
        this.barcodeList = barcodeList;
        this.context = context;
        this.experiment = experiment;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_barcode, parent, false);
        }


        BarcodeManager barcodeManager = new BarcodeManager();   // TODO: double check if empty constructor is appropriate for use in this example
        String barcodeID = barcodeList.get(position);

        TextView barcodeResult = view.findViewById(R.id.barcodeResult);
        ImageView barcodeImageView = view.findViewById(R.id.barcodeImageView);


        // set text view
        barcodeResult.setText(barcodeID);

        // generate image
        //Bitmap barcodeImage = barcodeManager.generateBarcode(barcodeID);

        // set image
        //barcodeImageView.setImageBitmap(barcodeImage);

        return view;
    }
}
