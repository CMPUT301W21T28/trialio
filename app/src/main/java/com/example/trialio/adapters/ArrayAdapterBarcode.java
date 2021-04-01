package com.example.trialio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.R;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.CountTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.MeasurementTrial;
import com.example.trialio.models.NonNegativeTrial;
import com.example.trialio.models.Trial;

import java.util.ArrayList;

public class ArrayAdapterBarcode extends ArrayAdapter{
    private Context context;
    private ArrayList<Trial> trialList;
    private Experiment experiment;

    public ArrayAdapterBarcode(@NonNull Context context, int resource) {
        super(context, resource);
    }

    /**
     * takes in experiment and store relevant information needed for listview
     * @param context
     * @param experiment
     */
    //public ArrayAdapterBarcode(Context context, Experiment experiment) {
    //    super(context, 0, experiment.getTrialManager().getTrials());
    //
    //    this.trialList = experiment.getTrialManager().getTrials();
    //    this.context = context;
    //    this.experiment = experiment;
    //}


    /**
     * this sets the field of items in listview with information retrieved from experiment
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_qr, parent,false);
        }

        Trial trial = trialList.get(position);

        // get the textviews
        TextView textResult = view.findViewById(R.id.text_trial_result);


        if (experiment.getTrialManager().getType().equals("BINOMIAL")){
            textResult.setText("Result:" + ((BinomialTrial) trial).getIsSuccess());
        }else if (experiment.getTrialManager().getType().equals("MEASUREMENT")){
            textResult.setText("Result:" + ((MeasurementTrial) trial).getMeasurement() + " " + ((MeasurementTrial) trial).getUnit());
        }else if (experiment.getTrialManager().getType().equals("COUNT")){
            textResult.setText("Result:" + ((CountTrial) trial).getCount());
        }else if (experiment.getTrialManager().getType().equals("NONNEGATIVE")){
            textResult.setText("Result:" + ((NonNegativeTrial) trial).getNonNegCount());
        }
        return view;
    }

}