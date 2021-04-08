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
import com.example.trialio.controllers.ExperimentManager;
import com.example.trialio.controllers.TrialManager;
import com.example.trialio.controllers.UserManager;
import com.example.trialio.models.Barcode;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Question;
import com.example.trialio.models.Trial;
import com.example.trialio.models.User;
import com.example.trialio.utils.ExperimentTypeUtility;

import java.util.ArrayList;

public class ArrayAdapterBarcode extends ArrayAdapter {

    private Context context;
    private ArrayList<Barcode> barcodeList;
    private Experiment experiment;
    private User user;


    public ArrayAdapterBarcode (Context context, ArrayList<Barcode> barcodeList, Experiment experiment, User user) {
        super(context, 0, barcodeList);
        this.barcodeList = barcodeList;
        this.context = context;
        this.experiment = experiment;
        this.user = user;
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.content_barcode, parent, false);
        }

        ExperimentManager experimentManager = new ExperimentManager();


        BarcodeManager barcodeManager = new BarcodeManager(user.getUsername()); // TODO: double check if empty constructor is appropriate for use in this example
        Barcode barcode = barcodeList.get(position);

        String barcodeID = barcode.getBarcodeID();
//        String barcodeExperimentID = barcode.getExperiment().getExperimentID();
//        String barcodeTrialType = experiment.getTrialManager().getType();
//        String barcodeTrialResult = barcode.getTrialResult();
//

        TextView barcodeResult = view.findViewById(R.id.barcodeResult);
        ImageView barcodeImageView = view.findViewById(R.id.barcodeImageView);

        // set text view
        barcodeResult.setText(barcodeID);

        // generate image
        Bitmap barcodeImage = barcodeManager.generateBarcode(barcodeID);

        // set image
        barcodeImageView.setImageBitmap(barcodeImage);

        return view;
    }
}
