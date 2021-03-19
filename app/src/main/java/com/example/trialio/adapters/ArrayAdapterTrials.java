package com.example.trialio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.trialio.R;
import com.example.trialio.models.BinomialTrial;
import com.example.trialio.models.Experiment;
import com.example.trialio.models.Trial;

import java.util.ArrayList;

public class ArrayAdapterTrials extends ArrayAdapter {
    private Context context;
    private ArrayList<Trial> trialList;
    private BinomialTrial binomialTrial;
    private Experiment experiment;

    public ArrayAdapterTrials(Context context, Experiment experiment) {
        super(context, 0, experiment.getTrialManager().getTrials());

        this.trialList = experiment.getTrialManager().getTrials();
        this.context = context;
        this.experiment = experiment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.content_trials, parent,false);
        }

        Trial trial = trialList.get(position);

        // get the textviews
        TextView textOwner = view.findViewById(R.id.text_trial_owner);
        TextView textDate = view.findViewById(R.id.text_trial_date);
        TextView textResult = view.findViewById(R.id.text_trial_result);

        // set the textviews
        textOwner.setText("Owner:"+trial.getExperimenterID());
        textDate.setText("Date:"+trial.getDate().toString());

        if (experiment.getTrialManager().getType().equals("BINOMIAL")){
            textResult.setText("Result:" + ((BinomialTrial) trial).getIsSuccess());
        }


        return view;
    }
}
